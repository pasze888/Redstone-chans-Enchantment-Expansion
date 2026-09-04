package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * 锁链（chains）：攻击命中给受害者施加重度减速+虚弱（6 秒，隐藏粒子），
 * 铁链粒子+锁链放置音效；并对受害者 8 格内最近 3 个非玩家生物施加 3 秒同效果。
 * <p>等价于 chains_initial（本体 6s）+ chains_final（波及 3s）。
 * 触发概率与"受害者未同时带有减速+虚弱"条件保留在 datagen 声明中。
 */
public record ChainBindEffect() implements EnchantmentEntityEffect {

    public static final ChainBindEffect INSTANCE = new ChainBindEffect();
    public static final MapCodec<ChainBindEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final int MAIN_DURATION_TICKS = 120;
    private static final int SPREAD_DURATION_TICKS = 60;
    private static final int AMPLIFIER = 9;
    private static final double SPREAD_RANGE = 8.0;
    private static final int SPREAD_TARGETS = 3;
    private static final ItemParticleOption CHAIN_PARTICLE =
            new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.CHAIN));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof LivingEntity victim)) {
            return;
        }
        bind(level, victim, MAIN_DURATION_TICKS);

        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class,
                        victim.getBoundingBox().inflate(SPREAD_RANGE),
                        e -> e != victim && !(e instanceof Player))
                .stream()
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(victim)))
                .limit(SPREAD_TARGETS)
                .toList();
        for (LivingEntity target : nearby) {
            bind(level, target, SPREAD_DURATION_TICKS);
        }
    }

    private void bind(ServerLevel level, LivingEntity target, int durationTicks) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks, AMPLIFIER, true, false));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, durationTicks, AMPLIFIER, true, false));
        level.sendParticles(CHAIN_PARTICLE, target.getX(), target.getY() + 1.0, target.getZ(),
                8, 0.2, 0.5, 0.2, 0.0);
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.CHAIN_PLACE, SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
