package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 雷鸣（thundering）：三叉戟雷击命中后把天气切为雷暴。
 * <p>等价于原命令 {@code weather thunder}：对主世界全局生效（WeatherCommand 固定
 * 作用于 overworld），时长为命令默认值 6000 tick（5 分钟）。
 * <p>调用方（datagen）已用"下雨 + 三叉戟 + 可见天空"条件约束，仅雷击时触发。
 */
public record WeatherThunderEffect() implements EnchantmentEntityEffect {

    public static final WeatherThunderEffect INSTANCE = new WeatherThunderEffect();
    public static final MapCodec<WeatherThunderEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final int THUNDER_DURATION_TICKS = 6000;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        level.getServer().overworld().setWeatherParameters(0, THUNDER_DURATION_TICKS, true, true);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
