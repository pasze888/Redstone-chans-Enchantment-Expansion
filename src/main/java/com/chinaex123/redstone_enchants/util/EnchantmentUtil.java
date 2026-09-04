package com.chinaex123.redstone_enchants.util;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.ArrayList;
import java.util.List;

/**
 * 附魔效果组件读取工具（confluence EnchantmentUtils 模式）。
 */
public final class EnchantmentUtil {
    /**
     * 汇总手持物上所有附魔在指定数值组件上的取值（requirements 为 ENCHANTED_ITEM 上下文）。
     * 未携带对应组件的附魔贡献 0。
     */
    public static float itemValue(ServerLevel level, ItemStack stack,
                                  DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType) {
        MutableFloat value = new MutableFloat(0);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) ->
                enchantment.value().modifyItemFilteredCount(componentType, level, enchantmentLevel, stack, value));
        return value.floatValue();
    }

    /**
     * 读取物品附魔上的单值（复合）组件（{@code withSpecialEffect} 声明的形态），
     * 取第一个携带该组件的附魔的值；物品上没有任何附魔携带时返回 {@code null}。
     */
    public static <E> E specialValue(ItemStack stack, DataComponentType<E> componentType) {
        List<E> found = new ArrayList<>(1);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) -> {
            E value = enchantment.value().effects().get(componentType);
            if (value != null) {
                found.add(value);
            }
        });
        return found.isEmpty() ? null : found.get(0);
    }

    /** 物品上该附魔的等级 */
    public static int levelOn(Holder<Enchantment> enchantment, ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
    }

    public static Holder<Enchantment> holder(RegistryAccess registryAccess, ResourceKey<Enchantment> key) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    private EnchantmentUtil() {
    }
}
