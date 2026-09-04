package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 通用粒子爆发：在实体位置发射固定数量/扩散/速度的粒子。
 * <p>等价于原 {@code particle <type> ~ ~ ~ <dx> <dy> <dz> <speed> <count>}
 * 命令（count>0 时 dx/dy/dz 为扩散范围）。替代 libs/particle/sonic_boom
 * 等单行粒子函数。
 */
public record ParticleBurstEffect(ParticleOptions particle, int count, float dx, float dy, float dz, float speed,
                                  float yOffset)
        implements EnchantmentEntityEffect {

    public static final MapCodec<ParticleBurstEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticleBurstEffect::particle),
            com.mojang.serialization.Codec.intRange(1, 10000).fieldOf("count").forGetter(ParticleBurstEffect::count),
            com.mojang.serialization.Codec.FLOAT.optionalFieldOf("dx", 1.0F).forGetter(ParticleBurstEffect::dx),
            com.mojang.serialization.Codec.FLOAT.optionalFieldOf("dy", 1.0F).forGetter(ParticleBurstEffect::dy),
            com.mojang.serialization.Codec.FLOAT.optionalFieldOf("dz", 1.0F).forGetter(ParticleBurstEffect::dz),
            com.mojang.serialization.Codec.FLOAT.optionalFieldOf("speed", 0.0F).forGetter(ParticleBurstEffect::speed),
            com.mojang.serialization.Codec.FLOAT.optionalFieldOf("y_offset", 0.0F).forGetter(ParticleBurstEffect::yOffset)
    ).apply(instance, ParticleBurstEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        level.sendParticles(this.particle, origin.x(), origin.y() + this.yOffset, origin.z(),
                this.count, this.dx, this.dy, this.dz, this.speed);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
