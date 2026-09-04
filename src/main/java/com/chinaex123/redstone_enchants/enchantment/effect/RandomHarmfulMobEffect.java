package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * 灾厄（calamity）：攻击时按概率给受害实体施加一个随机的负面药水效果。
 * <p>概率为 per-level 的 {@link LevelBasedValue}（旧版每级 5%）；持续时间为 4~10 秒（uniform）；
 * 强度固定 1 级，与旧 handler 行为一致。排除不祥之兆与试炼之兆（旧版黑名单）。
 * 由 {@code minecraft:post_attack} 声明驱动。
 */
public record RandomHarmfulMobEffect(LevelBasedValue chance) implements EnchantmentEntityEffect {

    public static final MapCodec<RandomHarmfulMobEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("chance").forGetter(RandomHarmfulMobEffect::chance)
    ).apply(instance, RandomHarmfulMobEffect::new));

    private static final int MIN_DURATION_SECONDS = 4;
    private static final int MAX_DURATION_SECONDS = 10;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof LivingEntity victim)) {
            return;
        }
        if (entity.getRandom().nextFloat() >= chance.calculate(enchantmentLevel)) {
            return;
        }
        List<Holder<MobEffect>> harmfulEffects = new ArrayList<>();
        level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).holders()
                .forEach(effect -> {
                    if (!effect.value().isBeneficial()
                            && !effect.is(MobEffects.BAD_OMEN)
                            && !effect.is(MobEffects.TRIAL_OMEN)) {
                        harmfulEffects.add(effect);
                    }
                });
        if (harmfulEffects.isEmpty()) {
            return;
        }
        RandomSource random = entity.getRandom();
        Holder<MobEffect> effect = harmfulEffects.get(random.nextInt(harmfulEffects.size()));
        int seconds = MIN_DURATION_SECONDS + random.nextInt(MAX_DURATION_SECONDS - MIN_DURATION_SECONDS + 1);
        victim.addEffect(new MobEffectInstance(effect, seconds * 20, 0));
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
