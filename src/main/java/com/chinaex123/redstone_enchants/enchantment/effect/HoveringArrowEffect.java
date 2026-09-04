package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 精准射击（accuracy_shot）：射出的箭矢不受重力悬浮在空中（NoGravity）；
 * 每 5 秒检查一次，若箭矢 5 格内没有玩家则清除。
 * <p>等价于 on_shoot（NoGravity + tag + 记分板存发射时间）+ schedule_handler
 * 轮询 + kill 的时间窗判定；原实现里"发射后 100 tick 内才可杀"的时间窗恒为真
 * （首次轮询即 100 tick 后），故简化为纯粹的延迟检查。轮询用服务器
 * {@link TickTask} 递归重排实现，等价于 mcfunction 的 schedule append。
 */
public record HoveringArrowEffect() implements EnchantmentEntityEffect {

    public static final HoveringArrowEffect INSTANCE = new HoveringArrowEffect();
    public static final MapCodec<HoveringArrowEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final int CHECK_INTERVAL_TICKS = 100;
    private static final double PLAYER_RANGE = 5.0;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        entity.setNoGravity(true);
        scheduleCheck(level, entity);
    }

    private void scheduleCheck(ServerLevel level, Entity arrow) {
        level.getServer().tell(new TickTask(level.getServer().getTickCount() + CHECK_INTERVAL_TICKS, () -> {
            if (arrow.isRemoved() || arrow.level() != level) {
                return;
            }
            boolean playerNearby = level.players().stream()
                    .anyMatch(p -> p.distanceToSqr(arrow) <= PLAYER_RANGE * PLAYER_RANGE);
            if (playerNearby) {
                scheduleCheck(level, arrow);
            } else {
                arrow.kill();
            }
        }));
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
