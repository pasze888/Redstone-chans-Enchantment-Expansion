package com.chinaex123.redstone_enchants.enchantment.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;

/**
 * 指数型附魔数值：计算 {@code base ^ exponent}（exponent 缺省时为等级），
 * 用于需要非线性增长的附魔（参考神化 {@code ExponentialLevelBasedValue}）。
 * <p>落点为 1.21.1 {@code Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE}，
 * 由 {@code init/ModEnchantmentLevelBasedValues} 注册其 codec。
 */
public record ExponentialLevelBasedValue(float base, LevelBasedValue exponent) implements LevelBasedValue {

    public static final MapCodec<ExponentialLevelBasedValue> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("base").forGetter(ExponentialLevelBasedValue::base),
            LevelBasedValue.CODEC.optionalFieldOf("exponent", LevelBasedValue.perLevel(1)).forGetter(ExponentialLevelBasedValue::exponent)
    ).apply(inst, ExponentialLevelBasedValue::new));

    public ExponentialLevelBasedValue(float base) {
        this(base, LevelBasedValue.perLevel(1));
    }

    @Override
    public float calculate(int level) {
        return (float) Math.pow(base, exponent.calculate(level));
    }

    @Override
    public MapCodec<? extends LevelBasedValue> codec() {
        return CODEC;
    }
}
