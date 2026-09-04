package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 连锁反应（chain_reaction）：命中时从受害者身上方 0.8 格向 6 个水平方向
 * （60° 间隔）各射出一支不可拾取的箭（或光灵箭，取决于触发箭种类）。
 * <p>等价于原函数的 6 条 {@code summon arrow ... {Motion:[...],pickup:2b}}。
 */
public record ChainArrowsEffect(boolean spectral) implements EnchantmentEntityEffect {

    public static final MapCodec<ChainArrowsEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("spectral", false).forGetter(ChainArrowsEffect::spectral)
    ).apply(instance, ChainArrowsEffect::new));

    private static final double[][] DIRECTIONS = {
            {1.0, 0.0, 0.0}, {-1.0, 0.0, 0.0},
            {0.5, 0.0, 0.866}, {-0.5, 0.0, 0.866},
            {0.5, 0.0, -0.866}, {-0.5, 0.0, -0.866}
    };

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        for (double[] dir : DIRECTIONS) {
            AbstractArrow arrow = this.spectral
                    ? new SpectralArrow(level, origin.x(), origin.y() + 0.8, origin.z(),
                            new ItemStack(Items.SPECTRAL_ARROW), null)
                    : new Arrow(level, origin.x(), origin.y() + 0.8, origin.z(),
                            new ItemStack(Items.ARROW), null);
            arrow.setDeltaMovement(new Vec3(dir[0], dir[1], dir[2]));
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            level.addFreshEntity(arrow);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
