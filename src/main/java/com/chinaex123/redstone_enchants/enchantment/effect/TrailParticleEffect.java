package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 移动轨迹（trail_*）：穿戴者移动换格时，在头部上方撒 3 个粒子。
 * <p>等价于原命令 {@code particle <type> ~ ~1 ~ .3 .3 .3 0 3}：
 * 以实体位置上方 1 格为中心、0.3 扩散、速度 0、数量 3。
 * 不用原版 {@code spawn_particles} 的原因：其 sendParticles 固定 count=0
 * （单粒子+速度向量语义），无法表达"多粒子随机扩散"。
 */
public record TrailParticleEffect(ParticleOptions particle) implements EnchantmentLocationBasedEffect {

    public static final MapCodec<TrailParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(TrailParticleEffect::particle)
    ).apply(instance, TrailParticleEffect::new));

    @Override
    public void onChangedBlock(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 pos,
                               boolean applyTransientEffects) {
        level.sendParticles(this.particle, pos.x(), pos.y() + 1.0, pos.z(), 3, 0.3, 0.3, 0.3, 0.0);
    }

    @Override
    public MapCodec<? extends EnchantmentLocationBasedEffect> codec() {
        return CODEC;
    }
}
