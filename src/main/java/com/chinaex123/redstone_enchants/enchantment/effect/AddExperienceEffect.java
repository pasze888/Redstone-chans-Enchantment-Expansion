package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 经验利刃（xp_blade）：攻击命中时从玩家身上扣除经验点数（原版等价
 * {@code xp add @s -15 points}）。固定 15 点/次，不随附魔等级变化。
 * <p>调用方（datagen）已用"玩家等级 ≥10"条件约束，不会把玩家扣成负数
 * （giveExperiencePoints 自身也不会让等级低于 0）。
 */
public record AddExperienceEffect(int points) implements EnchantmentEntityEffect {

    public static final MapCodec<AddExperienceEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.intRange(Integer.MIN_VALUE + 1, Integer.MAX_VALUE)
                    .fieldOf("points").forGetter(AddExperienceEffect::points)
    ).apply(instance, AddExperienceEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (entity instanceof Player player) {
            player.giveExperiencePoints(this.points);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
