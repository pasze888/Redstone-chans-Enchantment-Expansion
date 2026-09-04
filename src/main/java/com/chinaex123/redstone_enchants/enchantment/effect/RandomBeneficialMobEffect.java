package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
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
 * 恩赐（boons）：攻击时按概率给攻击者施加一个随机的正面药水效果。
 * <p>概率为 per-level 的 {@link LevelBasedValue}（旧版每级 5%）；持续时间为 4~10 秒（uniform）；
 * 强度固定 1 级，与旧 handler 行为一致。由 {@code minecraft:post_attack} 声明驱动。
 */
public record RandomBeneficialMobEffect(LevelBasedValue chance) implements EnchantmentEntityEffect {

    public static final MapCodec<RandomBeneficialMobEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("chance").forGetter(RandomBeneficialMobEffect::chance)
    ).apply(instance, RandomBeneficialMobEffect::new));

    private static final int MIN_DURATION_SECONDS = 4;
    private static final int MAX_DURATION_SECONDS = 10;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof LivingEntity attacker)) {
            return;
        }
        if (entity.getRandom().nextFloat() >= chance.calculate(enchantmentLevel)) {
            return;
        }
        List<Holder<MobEffect>> positiveEffects = new ArrayList<>();
        level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).holders()
                .forEach(effect -> {
                    if (effect.value().isBeneficial()) {
                        positiveEffects.add(effect);
                    }
                });
        if (positiveEffects.isEmpty()) {
            return;
        }
        RandomSource random = entity.getRandom();
        Holder<MobEffect> effect = positiveEffects.get(random.nextInt(positiveEffects.size()));
        int seconds = MIN_DURATION_SECONDS + random.nextInt(MAX_DURATION_SECONDS - MIN_DURATION_SECONDS + 1);
        attacker.addEffect(new MobEffectInstance(effect, seconds * 20, 0));
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
