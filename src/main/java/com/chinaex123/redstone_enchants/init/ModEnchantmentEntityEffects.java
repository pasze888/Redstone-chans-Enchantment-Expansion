package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomBeneficialMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.SummonItemEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义附魔实体效果（enchantment_entity_effect_type）注册表。
 * <p>该注册表存的是各 effect 的 {@link MapCodec}（序列化器），不是 effect 实例；
 * 运行期由原版 {@code post_attack} 等组件求值时按 codec 解码并按 level 施加。
 * <p>参考神化 {@code Ench.EnchantEffects} 的 {@code R.custom("rebounding", ...)} 形态，
 * 落点为 1.21.1 的 {@code Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE}。
 */
public final class ModEnchantmentEntityEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> TYPES =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, RedstoneEnchants.MOD_ID);

    /** 攻击时在受害实体位置生成物品堆（模板示例） */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> SUMMON_ITEM =
            TYPES.register("summon_item", () -> SummonItemEffect.CODEC);

    /** 恩赐（boons）：攻击时按概率给攻击者施加随机正面药水 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> RANDOM_BENEFICIAL_MOB_EFFECT =
            TYPES.register("random_beneficial_mob_effect", () -> RandomBeneficialMobEffect.CODEC);

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    private ModEnchantmentEntityEffects() {
    }
}
