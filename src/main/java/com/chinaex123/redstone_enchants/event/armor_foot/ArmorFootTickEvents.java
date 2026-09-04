package com.chinaex123.redstone_enchants.event.armor_foot;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 靴子（armors_foot）附魔在实体 tick 事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：庄稼舞 → 踏焰者 → （后续）飞马座/踏浪者。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorFootTickEvents {
    private static final Random RANDOM = new Random(); // 旧版同款 java.util.Random 静态实例

    private static final int CROP_DANCE_GROWTH_RANGE = 3; // 生效范围基础值
    private static final double CROP_DANCE_GROWTH_CHANCE_CAP = 0.99; // 概率封顶

    // 记录每个玩家上一次的潜行状态（旧版同款：Map 从不清理已退出玩家）
    private static final Map<Player, Boolean> LAST_SNEAKING = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        // 固定顺序：庄稼舞 → 踏焰者 → 飞马座 → 踏浪者（旧版为独立订阅者，顺序未定义）
        cropDance(event);
        flameWalker(event);
        pegasus(event);
    }

    // ---- 庄稼舞 ----

    private static void cropDance(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(boots, ModEnchantmentEffectComponents.CROP_DANCE.get())) {
            return;
        }

        boolean isSneaking = player.isShiftKeyDown();
        boolean lastSneaking = LAST_SNEAKING.getOrDefault(player, false);

        // 检查是否刚刚开始潜行（从非潜行状态变为潜行状态）
        if (isSneaking && !lastSneaking) {
            // 执行一次催熟（概率 0.1 + 0.1×级，封顶 0.99）
            float growthChance = EnchantmentUtil.itemValue(serverLevel, boots,
                    ModEnchantmentEffectComponents.CROP_DANCE_GROWTH_CHANCE.get());
            int enchantLevel = EnchantmentUtil.levelOn(
                    EnchantmentUtil.holder(serverLevel.registryAccess(), ModEnchantments.CROP_DANCE), boots);
            executeCropGrowth(player, serverLevel, enchantLevel, growthChance);
        }

        // 更新潜行状态记录
        LAST_SNEAKING.put(player, isSneaking);
    }

    private static void executeCropGrowth(Player player, ServerLevel serverLevel, int enchantLevel, double growthChance) {
        BlockPos playerPos = player.blockPosition();
        int actualRange = CROP_DANCE_GROWTH_RANGE + enchantLevel;
        AABB area = new AABB(playerPos).inflate(actualRange);

        // 计算实际生长概率
        double cappedChance = Math.min(CROP_DANCE_GROWTH_CHANCE_CAP, growthChance);

        BlockPos.betweenClosedStream(area).forEach(pos -> {
            BlockState state = serverLevel.getBlockState(pos);

            if (state.getBlock() instanceof BonemealableBlock bonemealable) {
                // 使用百分比判断
                if (RANDOM.nextDouble() < cappedChance) {
                    if (bonemealable.isValidBonemealTarget(serverLevel, pos, state)) {
                        bonemealable.performBonemeal(serverLevel, serverLevel.random, pos, state);

                        double particleX = pos.getX() + 0.5;
                        double particleY = pos.getY() + 0.5;
                        double particleZ = pos.getZ() + 0.5;
                        serverLevel.sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                particleX, particleY, particleZ,
                                5,
                                0.3, 0.3, 0.3,
                                0.02
                        );
                    }
                }
            }
        });
    }

    // ---- 踏焰者 ----

    private static void flameWalker(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(boots, ModEnchantmentEffectComponents.FLAME_WALKER.get())) {
            return;
        }

        // 如果玩家潜行，允许正常下潜
        if (player.isCrouching()) {
            return;
        }

        // 检查玩家是否不在岩浆中
        if (player.isInLava()) {
            return;
        }

        // 检测脚底下方 0.4 格的位置是否有岩浆
        BlockPos entityPos = player.blockPosition();
        BlockPos lavaCheckPos = new BlockPos(
                entityPos.getX(),
                (int) Math.floor(player.getBoundingBox().minY - 0.4),
                entityPos.getZ()
        );

        boolean hasLavaBelow = player.level().getFluidState(lavaCheckPos).is(Fluids.LAVA);

        if (hasLavaBelow) {
            // 获取岩浆表面高度
            double lavaHeight = lavaCheckPos.getY() + 1.0;

            // 如果玩家在岩浆面上方，调整到岩浆表面高度
            if (player.getY() > lavaHeight) {
                player.setPos(player.getX(), lavaHeight, player.getZ());
            }

            // 设置垂直速度为 0
            player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
            // 重置掉落距离
            player.fallDistance = 0;
            // 标记为在地面上
            player.setOnGround(true);
        }
    }

    // ---- 飞马座 ----

    private static final int PEGASUS_EFFECT_DURATION = 80; // 缓降持续时间（tick）

    private static void pegasus(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) {
            return;
        }

        boolean hasEnchant = EnchantmentHelper.has(boots, ModEnchantmentEffectComponents.PEGASUS.get());

        if (hasEnchant && player.getVehicle() != null) {
            // 给玩家添加缓降
            if (!player.hasEffect(MobEffects.SLOW_FALLING)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.SLOW_FALLING,
                        PEGASUS_EFFECT_DURATION,
                        0,
                        false,
                        false
                ));
            }

            // 给骑乘的生物添加缓降
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof LivingEntity livingVehicle) {
                if (!livingVehicle.hasEffect(MobEffects.SLOW_FALLING)) {
                    livingVehicle.addEffect(new MobEffectInstance(
                            MobEffects.SLOW_FALLING,
                            PEGASUS_EFFECT_DURATION,
                            0,
                            false,
                            false
                    ));
                }
            }
        } else {
            // 移除玩家的缓降
            if (player.hasEffect(MobEffects.SLOW_FALLING)) {
                player.removeEffect(MobEffects.SLOW_FALLING);
            }

            // 移除骑乘生物的缓降
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof LivingEntity livingVehicle) {
                if (livingVehicle.hasEffect(MobEffects.SLOW_FALLING)) {
                    livingVehicle.removeEffect(MobEffects.SLOW_FALLING);
                }
            }
        }
    }

    private ArmorFootTickEvents() {
    }
}
