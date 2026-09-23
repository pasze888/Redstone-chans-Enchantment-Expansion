package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
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
 * 随机药水效果：按概率给目标施加一个随机药水效果（恩赐 boons / 灾厄 calamity 共用）。
 * <p>概率为 per-level 的 {@link LevelBasedValue}（旧版每级 5%）；时长 4~10 秒（uniform）；
 * 强度固定 1 级，与旧 handler 行为一致。由 {@code minecraft:post_attack} 声明驱动，
 * 施加对象（攻击者 / 受害者）由声明里的 {@code EnchantmentTarget} 决定。
 * <p>{@code pool} 决定候选池，也就是原先两个类唯一的实质差异：{@code beneficial} 取
 * {@link MobEffect#isBeneficial()}；{@code harmful} 取"非 beneficial"并排除不祥之兆 / 试炼之兆
 * （旧版黑名单，因此含中性效果，与旧 handler 一致）。
 */
public record RandomMobEffectEffect(LevelBasedValue chance, Pool pool) implements EnchantmentEntityEffect {

    public enum Pool implements StringRepresentable {
        BENEFICIAL("beneficial"), HARMFUL("harmful");

        public static final Codec<Pool> CODEC = StringRepresentable.fromEnum(Pool::values);

        private final String name;

        Pool(String name) {
            this.name = name;
        }

        /** 该效果的分类是否落在这个候选池里 */
        boolean accepts(Holder<MobEffect> effect) {
            if (this == BENEFICIAL) {
                return effect.value().isBeneficial();
            }
            return !effect.value().isBeneficial() && !isOmen(effect);
        }

        /** 不祥之兆 / 试炼之兆不在负面池里（旧版黑名单） */
        private static boolean isOmen(Holder<MobEffect> effect) {
            // Holder#is(Holder) 已弃用；这两个是注册表单例，直接比实例
            return effect.value() == MobEffects.BAD_OMEN.value() || effect.value() == MobEffects.TRIAL_OMEN.value();
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static final MapCodec<RandomMobEffectEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("chance").forGetter(RandomMobEffectEffect::chance),
            Pool.CODEC.fieldOf("pool").forGetter(RandomMobEffectEffect::pool)
    ).apply(instance, RandomMobEffectEffect::new));

    private static final int MIN_DURATION_SECONDS = 4;
    private static final int MAX_DURATION_SECONDS = 10;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof LivingEntity target)) {
            return;
        }
        if (entity.getRandom().nextFloat() >= chance.calculate(enchantmentLevel)) {
            return;
        }
        List<Holder<MobEffect>> candidates = new ArrayList<>();
        level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).holders()
                .forEach(effect -> {
                    if (this.pool.accepts(effect)) {
                        candidates.add(effect);
                    }
                });
        if (candidates.isEmpty()) {
            return;
        }
        RandomSource random = entity.getRandom();
        Holder<MobEffect> effect = candidates.get(random.nextInt(candidates.size()));
        int seconds = MIN_DURATION_SECONDS + random.nextInt(MAX_DURATION_SECONDS - MIN_DURATION_SECONDS + 1);
        target.addEffect(new MobEffectInstance(effect, seconds * 20, 0));
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
