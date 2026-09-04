package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * 消解（nullify）：攻击时按概率移除受害实体身上的一个正面药水效果。
 * <p>概率为 per-level 的 {@link LevelBasedValue}（旧版每级 5%），与旧 handler 行为一致。
 * 由 {@code minecraft:post_attack} 声明驱动。
 */
public record RemoveRandomBeneficialEffect(LevelBasedValue chance) implements EnchantmentEntityEffect {

    public static final MapCodec<RemoveRandomBeneficialEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("chance").forGetter(RemoveRandomBeneficialEffect::chance)
    ).apply(instance, RemoveRandomBeneficialEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof LivingEntity victim)) {
            return;
        }
        if (entity.getRandom().nextFloat() >= chance.calculate(enchantmentLevel)) {
            return;
        }
        List<MobEffectInstance> positiveEffects = new ArrayList<>();
        victim.getActiveEffects().forEach(instance -> {
            if (instance.getEffect().value().isBeneficial()) {
                positiveEffects.add(instance);
            }
        });
        if (positiveEffects.isEmpty()) {
            return;
        }
        MobEffectInstance effectToRemove = positiveEffects.get(entity.getRandom().nextInt(positiveEffects.size()));
        victim.removeEffect(effectToRemove.getEffect());
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
