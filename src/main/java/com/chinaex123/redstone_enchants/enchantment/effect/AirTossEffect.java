package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 空中抛掷（air_toss）：攻击命中时把命令实体（攻击者）按等级向上抛起。
 * <p>等价于原命令 {@code data merge entity @e[limit=1,sort=nearest] {Motion:[0,Y,0]}}
 * ——命令原点在攻击者位置，最近实体即攻击者自身（距离 0），且整组 Motion 被替换
 * （水平速度清零）。等级 1 抛 1 格、等级 2 抛 2 格（perLevel(0,1) / perLevel(1,1)）。
 */
public record AirTossEffect(LevelBasedValue motionY) implements EnchantmentEntityEffect {

    public static final MapCodec<AirTossEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("motion_y").forGetter(AirTossEffect::motionY)
    ).apply(instance, AirTossEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        entity.setDeltaMovement(0.0, this.motionY.calculate(enchantmentLevel), 0.0);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
