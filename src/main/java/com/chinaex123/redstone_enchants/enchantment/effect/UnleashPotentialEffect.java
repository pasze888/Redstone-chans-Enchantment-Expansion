package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 激发潜能（unleash_potential）：饱食度越低越强——饥饿值 ≤6 时，
 * 同时获得急迫/迅捷/力量，强度 = 6 - 饥饿值（饥饿 0 时 6 级），时长 1 秒隐藏粒子。
 * <p>等价于原函数的 21 条 {@code execute if data entity @s {foodLevel:N}}：
 * 每移动换格重刷，停下后 1 秒渐退。饱食度 ≥7 无效果。
 */
public record UnleashPotentialEffect() implements EnchantmentLocationBasedEffect {

    public static final UnleashPotentialEffect INSTANCE = new UnleashPotentialEffect();
    public static final MapCodec<UnleashPotentialEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final int HUNGER_THRESHOLD = 6;

    @Override
    public void onChangedBlock(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 pos,
                               boolean applyTransientEffects) {
        if (!(entity instanceof Player player)) {
            return;
        }
        int food = player.getFoodData().getFoodLevel();
        if (food > HUNGER_THRESHOLD) {
            return;
        }
        int amplifier = HUNGER_THRESHOLD - food;
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, amplifier, true, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, amplifier, true, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, amplifier, true, false));
    }

    @Override
    public MapCodec<? extends EnchantmentLocationBasedEffect> codec() {
        return CODEC;
    }
}
