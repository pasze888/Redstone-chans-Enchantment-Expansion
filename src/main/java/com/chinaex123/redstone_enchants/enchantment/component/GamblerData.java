package com.chinaex123.redstone_enchants.enchantment.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 赌徒（gambler）的行为参数（单值复合组件，由 {@code withSpecialEffect} 声明）。
 *
 * @param odds              触发加伤的概率（旧版 0.5）
 * @param bonusMultiplier   加伤乘数（旧版 1.4）
 * @param penaltyMultiplier 减伤乘数（旧版 0.8）
 */
public record GamblerData(float odds, float bonusMultiplier, float penaltyMultiplier) {

    public static final Codec<GamblerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("odds").forGetter(GamblerData::odds),
            Codec.FLOAT.fieldOf("bonus_multiplier").forGetter(GamblerData::bonusMultiplier),
            Codec.FLOAT.fieldOf("penalty_multiplier").forGetter(GamblerData::penaltyMultiplier)
    ).apply(instance, GamblerData::new));
}
