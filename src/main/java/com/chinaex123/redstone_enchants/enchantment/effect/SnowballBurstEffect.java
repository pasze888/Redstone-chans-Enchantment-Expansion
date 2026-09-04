package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * 雪球（snowball）：穿戴者受到伤害时，从眼前 1 格处弹出一颗雪球，
 * 飞向 16 格内最近的非玩家生物（速度 = 方向 × 0.5/tick）。
 * <p>原函数的 marker + scoreboard 运算链等价替换为向量运算；并修复原选择器
 * {@code tag=!this} 未排除受害者自身、导致雪球朝自己反飞的 bug（范围内无目标
 * 时不再生成雪球）。
 */
public record SnowballBurstEffect() implements EnchantmentEntityEffect {

    public static final SnowballBurstEffect INSTANCE = new SnowballBurstEffect();
    public static final MapCodec<SnowballBurstEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final double RANGE = 16.0;
    private static final double MOTION_SCALE = 0.5;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof LivingEntity victim)) {
            return;
        }
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class,
                victim.getBoundingBox().inflate(RANGE),
                e -> e != victim && !(e instanceof Player));
        LivingEntity target = candidates.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(victim)))
                .orElse(null);
        if (target == null) {
            return;
        }
        Vec3 spawn = victim.position().add(victim.getViewVector(1.0F));
        Snowball snowball = new Snowball(level, spawn.x, spawn.y, spawn.z);
        snowball.setDeltaMovement(target.position().subtract(spawn).scale(MOTION_SCALE));
        level.addFreshEntity(snowball);
        level.playSound(null, spawn.x, spawn.y, spawn.z,
                SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
