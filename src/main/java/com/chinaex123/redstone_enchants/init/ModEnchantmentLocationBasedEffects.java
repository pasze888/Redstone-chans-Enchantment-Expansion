package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.AreaIgniteEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AreaMobEffectEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义附魔位置效果（enchantment_location_based_effect_type）注册表。
 * <p>该注册表存的是各 effect 的 {@link MapCodec}（序列化器），不是 effect 实例；
 * 运行期由原版 {@code location_changed} 组件求值时按 codec 解码并触发
 * {@link EnchantmentLocationBasedEffect#onChangedBlock}。
 * <p>与 {@link ModEnchantmentEntityEffects} 平行：location_changed 组件反序列化走的是
 * location-based 注册表，直接实现 {@code onChangedBlock} 而非 {@code apply}。
 */
public final class ModEnchantmentLocationBasedEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentLocationBasedEffect>> TYPES =
            DeferredRegister.create(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, RedstoneEnchants.MOD_ID);

    /** 光环：对范围内生物施加药水效果（target: all / others / self） */
    public static final DeferredHolder<MapCodec<? extends EnchantmentLocationBasedEffect>, MapCodec<? extends EnchantmentLocationBasedEffect>> AREA_MOB_EFFECT =
            TYPES.register("area_mob_effect", () -> AreaMobEffectEffect.CODEC);

    /** 光环：点燃范围内生物（不含触发者） */
    public static final DeferredHolder<MapCodec<? extends EnchantmentLocationBasedEffect>, MapCodec<? extends EnchantmentLocationBasedEffect>> AREA_IGNITE =
            TYPES.register("area_ignite", () -> AreaIgniteEffect.CODEC);

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    private ModEnchantmentLocationBasedEffects() {
    }
}
