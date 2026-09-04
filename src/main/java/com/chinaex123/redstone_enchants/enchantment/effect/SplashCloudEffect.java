package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * 蔓延系列（splash_*）：命中时在目标位置生成带药水效果的区域效果云（AEC），
 * 并撒一圈幽匿魂粒子。
 * <p>等价于原 {@code summon area_effect_cloud} NBT 写法：只覆盖 Particle/
 * Radius/Duration/potion_contents，其余字段（WaitTime=20、ReapplicationDelay=20、
 * RadiusPerTick=0）沿用实体默认值，与 summon 默认 NBT 一致。
 * <p>potion_contents 只写 custom_effects（无基础药水、无自定义颜色）；
 * 效果实例 ambient/showIcon 按声明（常规蔓延：ambient=1b、showIcon=0b；
 * 延迟爆破：ambient=0b、showParticles=0b）。
 */
public record SplashCloudEffect(ParticleOptions cloudParticle, Holder<MobEffect> effect, int effectDuration,
                                int effectAmplifier, boolean showEffectParticles, float radius, int cloudDuration)
        implements EnchantmentEntityEffect {

    public static final MapCodec<SplashCloudEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("cloud_particle").forGetter(SplashCloudEffect::cloudParticle),
            SplashCloudEffect.effectCodec().fieldOf("effect").forGetter(SplashCloudEffect::effect),
            net.minecraft.util.ExtraCodecs.POSITIVE_INT.optionalFieldOf("effect_duration", 100)
                    .forGetter(SplashCloudEffect::effectDuration),
            net.minecraft.util.ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("effect_amplifier", 0)
                    .forGetter(SplashCloudEffect::effectAmplifier),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("show_effect_particles", true)
                    .forGetter(SplashCloudEffect::showEffectParticles),
            com.mojang.serialization.Codec.floatRange(0.0F, 128.0F).optionalFieldOf("radius", 2.0F)
                    .forGetter(SplashCloudEffect::radius),
            net.minecraft.util.ExtraCodecs.POSITIVE_INT.optionalFieldOf("cloud_duration", 100)
                    .forGetter(SplashCloudEffect::cloudDuration)
    ).apply(instance, SplashCloudEffect::new));

    private static com.mojang.serialization.Codec<Holder<MobEffect>> effectCodec() {
        return net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.holderByNameCodec();
    }

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, origin.x(), origin.y(), origin.z());
        cloud.setParticle(this.cloudParticle);
        cloud.setRadius(this.radius);
        cloud.setDuration(this.cloudDuration);
        cloud.setPotionContents(new PotionContents(Optional.empty(), Optional.empty(), List.of(
                new MobEffectInstance(this.effect, this.effectDuration, this.effectAmplifier,
                        true, this.showEffectParticles, false))));
        level.addFreshEntity(cloud);

        level.sendParticles(ParticleTypes.SCULK_SOUL, origin.x(), origin.y() + 0.7, origin.z(), 25, 1.0, 0.0, 1.0, 0.01);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
