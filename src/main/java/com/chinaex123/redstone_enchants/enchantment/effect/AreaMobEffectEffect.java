package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 光环（aura_*）：以触发实体为中心，对范围内生物施加药水效果。
 * <p>由 {@code minecraft:location_changed} 组件驱动，穿戴者移动换格时反复触发，
 * 因此时长取 3 秒（60 tick）：移动中持续重刷，停下后约 3 秒渐退。
 * <p>作用对象由 {@link AreaTarget} 声明，见该枚举的语义说明。
 * <p>效果以 ambient（无屏幕抖动）+ 隐藏粒子施加，来源实体记为施加者
 * （可被 /effect 时间轴与统计正确归属）。
 */
public record AreaMobEffectEffect(float radius, Holder<MobEffect> effect, int durationTicks, int amplifier,
                                 AreaTarget target) implements EnchantmentLocationBasedEffect {

    public static final MapCodec<AreaMobEffectEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, 128.0F).fieldOf("radius").forGetter(AreaMobEffectEffect::radius),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(AreaMobEffectEffect::effect),
            Codec.intRange(1, 72000).optionalFieldOf("duration_ticks", 60).forGetter(AreaMobEffectEffect::durationTicks),
            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(AreaMobEffectEffect::amplifier),
            AreaTarget.CODEC.optionalFieldOf("target", AreaTarget.ALL).forGetter(AreaMobEffectEffect::target)
    ).apply(instance, AreaMobEffectEffect::new));

    @Override
    public void onChangedBlock(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 pos,
                               boolean applyTransientEffects) {
        if (this.target == AreaTarget.SELF) {
            if (entity instanceof LivingEntity self) {
                this.addEffectTo(self, entity);
            }
            return;
        }
        AABB box = new AABB(pos, pos).inflate(this.radius);
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (this.target.includes(entity, candidate)) {
                this.addEffectTo(candidate, entity);
            }
        }
    }

    private void addEffectTo(LivingEntity target, Entity source) {
        target.addEffect(new MobEffectInstance(this.effect, this.durationTicks, this.amplifier, true, false),
                source instanceof LivingEntity living ? living : null);
    }

    @Override
    public MapCodec<? extends EnchantmentLocationBasedEffect> codec() {
        return CODEC;
    }
}
