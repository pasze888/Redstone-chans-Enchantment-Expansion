package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * 火焰箭（fire_arrows）：命中点周围 3x3x3 的空气点燃为火，并撒火焰粒子。
 * <p>等价于原命令 {@code fill ~-1 ~-1 ~-1 ~1 ~1 ~1 fire keep}
 * （keep 过滤 = 仅替换空气）+ {@code particle flame}。
 */
public record IgniteAreaEffect() implements EnchantmentEntityEffect {

    public static final IgniteAreaEffect INSTANCE = new IgniteAreaEffect();
    public static final MapCodec<IgniteAreaEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        net.minecraft.core.BlockPos center = net.minecraft.core.BlockPos.containing(origin);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    net.minecraft.core.BlockPos pos = center.offset(dx, dy, dz);
                    if (level.getBlockState(pos).isAir()) {
                        level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                    }
                }
            }
        }
        level.sendParticles(ParticleTypes.FLAME, origin.x(), origin.y(), origin.z(), 50, 1.0, 1.0, 1.0, 0.5);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
