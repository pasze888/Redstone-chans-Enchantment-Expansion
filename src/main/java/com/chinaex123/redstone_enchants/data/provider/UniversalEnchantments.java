package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import net.minecraft.world.item.enchantment.effects.SetValue;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 全物品通用（c:enchantables）附魔的 datagen 声明。 */
final class UniversalEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        register(context, ModEnchantments.ADVANCED_UNBREAKING, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 1, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.ANY),
                0xFFAA00)
                .exclusiveWith(enchantments.getOrThrow(UNBREAKING_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.ITEM_DAMAGE,
                        new RemoveBinomial(new LevelBasedValue.Fraction(LevelBasedValue.constant(4), LevelBasedValue.constant(5)))));

        register(context, ModEnchantments.SACRIFICE, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.ANY),
                0xFFAA00)
                .exclusiveWith(enchantments.getOrThrow(UNBREAKING_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.SACRIFICE_REPAIR.get(),
                        new SetValue(LevelBasedValue.perLevel(1.0F, 0.5F))));

        register(context, ModEnchantments.INDESTRUCTIBLE, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 1, 1,
                        Enchantment.dynamicCost(0, 0), Enchantment.dynamicCost(0, 0), 50, EquipmentSlotGroup.ANY),
                0xFF00BB)
                .exclusiveWith(enchantments.getOrThrow(INDESTRUCTIBLE_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.INDESTRUCTIBLE.get()));

        register(context, ModEnchantments.STURDY, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ANY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.STURDY.get()));

        register(context, ModEnchantments.PRESERVATION, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ANY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.PRESERVATION.get()));
    }

    private UniversalEnchantments() {
    }
}
