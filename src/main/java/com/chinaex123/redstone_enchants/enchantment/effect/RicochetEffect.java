package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * 跳弹（ricochet）：箭矢命中后飞向 2~12 格内最近的非玩家生物。
 * <p>等价于原函数的 scoreboard 运算链（marker 记瞄准点 + store result entity
 * Motion double 0.00025 × data get 1000）：新速度 = (瞄准点 - 箭矢位置) × 0.25/tick，
 * 瞄准点为目标眼部上方 1 格。范围内无目标时箭矢保持原弹道（原命令同样不改 Motion）。
 * <p>原实现用记分板会引入整数截断误差，本实现直接向量运算（略更平滑）。
 */
public record RicochetEffect() implements EnchantmentEntityEffect {

    public static final RicochetEffect INSTANCE = new RicochetEffect();
    public static final MapCodec<RicochetEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final double MIN_RANGE = 2.0;
    private static final double MAX_RANGE = 12.0;
    private static final double MOTION_SCALE = 0.25;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(MAX_RANGE),
                e -> !(e instanceof Player) && e.distanceToSqr(entity) >= MIN_RANGE * MIN_RANGE);
        if (candidates.isEmpty()) {
            return;
        }
        LivingEntity target = candidates.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(entity)))
                .orElse(null);
        if (target == null) {
            return;
        }
        Vec3 aim = target.getEyePosition().add(0.0, 1.0, 0.0);
        entity.setDeltaMovement(aim.subtract(entity.position()).scale(MOTION_SCALE));
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
