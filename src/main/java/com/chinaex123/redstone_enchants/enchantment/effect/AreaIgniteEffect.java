package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 光环（aura_*）：以触发实体为中心，点燃范围内生物（不含触发者自身，
 * 自身保护由光环附魔另行声明，如燃烧光环同时给穿戴者火焰抗性）。
 * <p>由 {@code minecraft:location_changed} 组件驱动，移动换格时反复触发；
 * {@code igniteForTicks} 每次重设剩余燃烧时间。
 */
public record AreaIgniteEffect(float radius, int fireTicks) implements EnchantmentLocationBasedEffect {

    public static final MapCodec<AreaIgniteEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, 128.0F).fieldOf("radius").forGetter(AreaIgniteEffect::radius),
            Codec.intRange(1, 72000).optionalFieldOf("fire_ticks", 80).forGetter(AreaIgniteEffect::fireTicks)
    ).apply(instance, AreaIgniteEffect::new));

    @Override
    public void onChangedBlock(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 pos,
                               boolean applyTransientEffects) {
        AABB box = new AABB(pos, pos).inflate(this.radius);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (target == entity) {
                continue;
            }
            target.igniteForTicks(this.fireTicks);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentLocationBasedEffect> codec() {
        return CODEC;
    }
}
