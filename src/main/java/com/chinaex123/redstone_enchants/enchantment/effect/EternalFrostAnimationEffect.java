package com.chinaex123.redstone_enchants.enchantment.effect;

import com.chinaex123.redstone_enchants.util.DelayedTasks;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 永恒冰霜（eternal_frost）：在命中点播一段 13 片霜冰「平铺长出 → 回缩下沉 → 消失」的动画。
 * <p>替代原 {@code run_function} + {@code libs/animation/freeze_pic/*} 那 5 个 mcfunction：原实现
 * {@code summon} 13 个 block_display（scale.y=0 的扁平霜冰）→ 用 {@code data merge} 改
 * transformation + interpolation_duration 做两段插值 → {@code kill}。Java 侧走 AT 放开的
 * {@code Display#setTransformation} 等 setter，语义与 {@code data merge} 一一对应；三段延时用
 * {@link DelayedTasks}（真正的 N tick 后执行），等价 mcfunction 的 {@code schedule function}。
 * <p>13 组手调四元数/缩放与各自的插值时长是美术数据（不是伤害/概率/半径这类平衡数值），
 * 故按原样留在代码里，见 docs/reference/enchantment-runtime-effects.md。
 * <p>与原实现的行为差异（详见 docs/reference/enchantment-migrations.md）：
 * <ol>
 *   <li>原 {@code first_step} 里 {@code execute at @e[...] run playsound ... @s} 中的 {@code at}
 *       只换位置不换执行实体，而 {@code schedule function} 的命令源是 gameLoopSender（无实体），
 *       该音效从来没响过；本次让它在命中点真的播——13 片共用同一坐标，逐片叠播只是变响，
 *       故整段只播一次。</li>
 *   <li>{@code start} 的三声同理只发给 {@code @s}——POST_ATTACK 路径下 {@code @s} 是受击者、
 *       HIT_BLOCK 路径下是弹射物，实际几乎不响；本次改为命中点附近所有玩家可闻。</li>
 *   <li>清理只针对本次生成的 13 片霜冰，不再像原 {@code finished} 那样按 tag 全局 kill
 *       （原实现里同时触发的两次动画会互相提前清掉）。</li>
 *   <li>原 {@code schedule function} 会把待执行函数写进存档，重启后仍继续；{@link DelayedTasks}
 *       是内存队列，服务器在动画的 50 tick 内重启（或区块在此期间卸载）会留下未清理的霜冰。
 *       这批残留由 {@code event/freeze/FreezeShardCleanupEvents} 在实体从存档加入世界时清掉，
 *       手工兜底仍是 {@code /kill @e[tag=redstone_enchants.block_display.freezing]}。</li>
 * </ol>
 */
public record EternalFrostAnimationEffect() implements EnchantmentEntityEffect {

    public static final EternalFrostAnimationEffect INSTANCE = new EternalFrostAnimationEffect();
    public static final MapCodec<EternalFrostAnimationEffect> CODEC = MapCodec.unit(INSTANCE);

    /** 原 {@code summon} 与 {@code data merge} 里 {@code Properties:{age:"1"}} 的霜冰 */
    private static final int FROSTED_ICE_AGE = 1;
    /** 生成点相对命中点的抬升（原 {@code execute positioned ~ ~0.25 ~}） */
    private static final double SPAWN_Y_OFFSET = 0.25;
    /** 平铺期与长成期的 translation.y（原 start/first_step 的 {@code 0.0f,-0.25f,0.0f}） */
    private static final float START_TRANSLATION_Y = -0.25F;
    /** 回缩期的 translation.y（原 second_step 的 {@code 0.0f,-0.5f,0.0f}） */
    private static final float END_TRANSLATION_Y = -0.5F;
    private static final float SOUND_VOLUME = 2.0F;
    private static final float SOUND_PITCH = 0.01F;
    /** 原 {@code schedule ... first_step 0.1s} */
    private static final int GROW_DELAY_TICKS = 2;
    /** 原 {@code schedule ... second_step 1.5s} */
    private static final int SHRINK_DELAY_TICKS = 30;
    /** 原 {@code schedule ... finished 18t} */
    private static final int KILL_DELAY_TICKS = 18;
    /** 与原 mcfunction 共用的 tag：手工清理用，{@code FreezeShardCleanupEvents} 也按它识别残留 */
    public static final String FREEZING_TAG = "redstone_enchants.block_display.freezing";
    /** 原 {@code particle minecraft:dust{color:[0.65f,0.8f,1f],scale:1} ... 50} */
    private static final DustParticleOptions FROST_PARTICLE = new DustParticleOptions(new Vector3f(0.65F, 0.8F, 1.0F), 1.0F);
    private static final int PARTICLE_COUNT = 50;
    private static final double PARTICLE_SPREAD = 0.85;
    private static final double PARTICLE_SPEED = 0.85;

    /**
     * 13 片霜冰的动画参数，顺序与原 mcfunction 的 {@code freezing_0..12} 一一对应。
     * <p>{@code grownScaleY} 是 first_step 里长到的高度（{@code freezing_11} 为 0，即始终不平铺），
     * 两段插值时长取自各 {@code data merge} 的 {@code interpolation_duration}。
     */
    private static final List<Keyframe> KEYFRAMES = List.of(
            kf(0.20761608F, -0.1768769F, -0.22127223F, 0.93629533F, -0.0119385645F, 0.4739189F, -0.008892587F, 0.88044274F, 0.48877987F, 0.4082154F, 2.1972475F, 6, 8),
            kf(0.27827242F, 0.21906535F, -0.027267985F, 0.9347895F, 0.008432141F, 0.07245588F, 0.020641554F, -0.9971224F, 0.30155635F, 0.39867896F, 1.9095109F, 3, 4),
            kf(0.27827242F, 0.21906535F, -0.027267985F, 0.9347895F, 0.008432141F, 0.07245588F, 0.020641554F, -0.9971224F, 0.30155635F, 0.39867896F, 1.9095109F, 7, 6),
            kf(0.3035407F, -0.23713717F, 0.038992405F, 0.92201346F, -0.008808866F, 0.09265068F, -0.02308758F, 0.995392F, 0.42169607F, 0.36769652F, 1.7484765F, 8, 4),
            kf(0.20761608F, -0.1768769F, -0.22127223F, 0.93629533F, -0.0119385645F, 0.4739189F, -0.008892587F, 0.88044274F, 0.48877987F, 0.4082154F, 2.1972475F, 9, 12),
            kf(0.0644984F, 0.33012953F, -0.2966116F, 0.89379865F, -0.012078595F, 0.47392493F, -0.008939332F, 0.8804371F, 0.4887792F, 0.40821457F, 2.1972423F, 10, 3),
            kf(-0.041160293F, 0.01865346F, -0.09304882F, 0.9946356F, -0.03366559F, 0.055936594F, -0.020904878F, 0.99764764F, 0.5694376F, 0.48386317F, 1.5423458F, 12, 6),
            kf(0.17247608F, -0.6083231F, 0.44446576F, -0.6345433F, -0.073000684F, -0.0020624977F, -0.08319552F, 0.9938537F, 0.51876557F, 0.564991F, 1.7447492F, 7, 3),
            kf(0.23646699F, -0.67403525F, 0.18719941F, 0.6743265F, 0.020685628F, 0.26502955F, 0.012503981F, -0.96393734F, 0.35222408F, 0.47451732F, 1.9584135F, 6, 8),
            kf(-0.08416754F, 0.61440694F, -0.2348772F, 0.74850035F, -0.009980634F, -0.19441357F, -0.054356012F, 0.9793617F, 0.3508664F, 0.2285353F, 1.1267037F, 5, 10),
            kf(-0.13719803F, 0.25688514F, -0.1243474F, 0.9485381F, -0.020792069F, 0.17890018F, 0.023349473F, 0.9833704F, 0.5010639F, 0.46192458F, 1.0566843F, 4, 5),
            kf(-0.1793692F, 0.82914215F, -0.21142682F, 0.4854368F, -0.024937697F, -0.1498754F, -0.0409054F, 0.9875436F, 0.28164324F, 0.22289199F, 0.0F, 3, 8),
            kf(-0.24112663F, 0.89863044F, -0.15484048F, 0.33218348F, -0.035565913F, -0.070613824F, -0.034442298F, 0.9962744F, 0.50525606F, 0.41262737F, 1.611846F, 4, 12));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        // 生成点在命中点上方 0.25 格（原 execute positioned ~ ~0.25 ~）；13 片共用这一坐标
        Vec3 center = origin.add(0.0, SPAWN_Y_OFFSET, 0.0);

        level.playSound(null, center.x(), center.y(), center.z(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.MASTER, SOUND_VOLUME, SOUND_PITCH);
        level.playSound(null, center.x(), center.y(), center.z(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.MASTER, SOUND_VOLUME, SOUND_PITCH);
        level.playSound(null, center.x(), center.y(), center.z(), SoundEvents.AMETHYST_BLOCK_FALL, SoundSource.MASTER, SOUND_VOLUME, SOUND_PITCH);

        BlockState frostedIce = Blocks.FROSTED_ICE.defaultBlockState().setValue(FrostedIceBlock.AGE, FROSTED_ICE_AGE);
        List<Display.BlockDisplay> shards = new ArrayList<>(KEYFRAMES.size());
        for (Keyframe keyframe : KEYFRAMES) {
            Display.BlockDisplay shard = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
            shard.setBlockState(frostedIce);
            shard.setTransformation(keyframe.transformation(START_TRANSLATION_Y, 0.0F));
            shard.setPos(center.x(), center.y(), center.z());
            shard.addTag(FREEZING_TAG);
            level.addFreshEntity(shard);
            shards.add(shard);
        }

        schedule(level, GROW_DELAY_TICKS, () -> grow(level, shards, center));
    }

    /** 原 first_step：各片按自己的时长从 scale.y=0 长到目标高度并撒粒子；爆响整段只播一次 */
    private void grow(ServerLevel level, List<Display.BlockDisplay> shards, Vec3 center) {
        for (int i = 0; i < shards.size(); i++) {
            Display.BlockDisplay shard = shards.get(i);
            if (shard.isRemoved()) {
                continue;
            }
            Keyframe keyframe = KEYFRAMES.get(i);
            shard.setTransformation(keyframe.transformation(START_TRANSLATION_Y, keyframe.grownScaleY()));
            shard.setTransformationInterpolationDuration(keyframe.growDuration());
            shard.setTransformationInterpolationDelay(0);

            // 原粒子写在 ~ ~0.25 ~（相对每片霜冰的位置），13 片共用同一坐标，叠成一次密集爆发
            level.sendParticles(FROST_PARTICLE, shard.getX(), shard.getY() + SPAWN_Y_OFFSET, shard.getZ(),
                    PARTICLE_COUNT, PARTICLE_SPREAD, 0.0, PARTICLE_SPREAD, PARTICLE_SPEED);
        }

        // 原实现这条音效在 execute at @e[...] 里（每片一次）且因 @s 无实体而失效；
        // 13 片坐标相同、同 tick 叠播只是变响，故整段只播一次
        level.playSound(null, center.x(), center.y(), center.z(),
                SoundEvents.AMETHYST_BLOCK_FALL, SoundSource.MASTER, SOUND_VOLUME, SOUND_PITCH);

        schedule(level, SHRINK_DELAY_TICKS, () -> shrink(level, shards));
    }

    /** 原 second_step：各片按自己的时长缩回 scale.y=0 并下沉半格 */
    private void shrink(ServerLevel level, List<Display.BlockDisplay> shards) {
        for (int i = 0; i < shards.size(); i++) {
            Display.BlockDisplay shard = shards.get(i);
            if (shard.isRemoved()) {
                continue;
            }
            Keyframe keyframe = KEYFRAMES.get(i);
            shard.setTransformation(keyframe.transformation(END_TRANSLATION_Y, 0.0F));
            shard.setTransformationInterpolationDuration(keyframe.shrinkDuration());
            shard.setTransformationInterpolationDelay(0);
        }

        schedule(level, KILL_DELAY_TICKS, () -> remove(shards));
    }

    /** 原 finished：只清理本次生成的霜冰（不再是按 tag 的全局 kill） */
    private static void remove(List<Display.BlockDisplay> shards) {
        for (Display.BlockDisplay shard : shards) {
            if (!shard.isRemoved()) {
                shard.kill();
            }
        }
    }

    private static void schedule(ServerLevel level, int delayTicks, Runnable action) {
        DelayedTasks.schedule(level, delayTicks, action);
    }

    /** 表驱动的构造入口，让 {@link #KEYFRAMES} 保持一行一片、与 mcfunction 行序对应 */
    private static Keyframe kf(float leftX, float leftY, float leftZ, float leftW,
                               float rightX, float rightY, float rightZ, float rightW,
                               float scaleX, float scaleZ, float grownScaleY,
                               int growDuration, int shrinkDuration) {
        return new Keyframe(new Quaternionf(leftX, leftY, leftZ, leftW), new Quaternionf(rightX, rightY, rightZ, rightW),
                scaleX, scaleZ, grownScaleY, growDuration, shrinkDuration);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    /**
     * 单片霜冰的静态参数。{@code translation} 只有 y 会变（两段各一个值），故不单独存。
     */
    private record Keyframe(Quaternionf leftRotation, Quaternionf rightRotation,
                            float scaleX, float scaleZ, float grownScaleY,
                            int growDuration, int shrinkDuration) {

        Transformation transformation(float translationY, float scaleY) {
            return new Transformation(new Vector3f(0.0F, translationY, 0.0F), new Quaternionf(this.leftRotation),
                    new Vector3f(this.scaleX, scaleY, this.scaleZ), new Quaternionf(this.rightRotation));
        }
    }
}
