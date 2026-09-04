package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * 冰霜箭（ice_arrows）的冻结区域：以命中点为中心把 3x3x3 范围内的水冻结成冰，
 * 并撒雪花粒子。等价于原命令 {@code fill ~-1 ~-1 ~-1 ~1 ~1 ~1 ice replace water}
 * （replace 过滤按方块 id 匹配，含流动水）+ {@code particle snowflake}。
 * <p>既用于箭命中方块（hit_block，实体为箭），也用于命中生物（post_attack）。
 */
public record FreezeWaterEffect() implements EnchantmentEntityEffect {

    public static final FreezeWaterEffect INSTANCE = new FreezeWaterEffect();
    public static final MapCodec<FreezeWaterEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        BlockPos center = BlockPos.containing(origin);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (level.getBlockState(pos).is(Blocks.WATER)) {
                        level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                    }
                }
            }
        }
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                origin.x(), origin.y(), origin.z(), 50, 1.0, 1.0, 1.0, 0.5);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
