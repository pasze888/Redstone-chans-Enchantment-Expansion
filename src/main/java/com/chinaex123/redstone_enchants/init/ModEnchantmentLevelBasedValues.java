package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.value.ExponentialLevelBasedValue;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义附魔数值函数（enchantment_level_based_value_type）注册表。
 * <p>该注册表存的是各函数的 {@link MapCodec}（序列化器）。原版内置
 * linear / constant / clamped / levels_squared / fraction / lookup，这里补扩展形态。
 * <p>参考神化 {@code Ench} 的 {@code R.custom("exponential", Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, ...)}。
 */
public final class ModEnchantmentLevelBasedValues {
    public static final DeferredRegister<MapCodec<? extends LevelBasedValue>> TYPES =
            DeferredRegister.create(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, RedstoneEnchants.MOD_ID);

    /** 指数型数值：{@code base ^ (exponent)}，用于非线性增长附魔 */
    public static final DeferredHolder<MapCodec<? extends LevelBasedValue>, MapCodec<? extends LevelBasedValue>> EXPONENTIAL =
            TYPES.register("exponential", () -> ExponentialLevelBasedValue.CODEC);

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    private ModEnchantmentLevelBasedValues() {
    }
}
