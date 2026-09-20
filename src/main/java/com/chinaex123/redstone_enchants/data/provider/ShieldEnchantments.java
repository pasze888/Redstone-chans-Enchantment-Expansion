package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 盾牌附魔的 datagen 声明。 */
final class ShieldEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        register(context, ModEnchantments.ABSORBENT_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 2,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.ABSORPTION),
                                LevelBasedValue.constant(11.0F), LevelBasedValue.constant(11.0F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().periodicTick(200)))
                .exclusiveWith(enchantments.getOrThrow(SHIELD_EXCLUSIVE)));

        register(context, ModEnchantments.STABLE_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 2, 2,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.OFFHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.stalwart_1"),
                        Attributes.KNOCKBACK_RESISTANCE,
                        LevelBasedValue.constant(1.0F),
                        AttributeModifier.Operation.ADD_VALUE))
                .exclusiveWith(enchantments.getOrThrow(SHIELD_EXCLUSIVE)));

        register(context, ModEnchantments.STRENGTH_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.DAMAGE_BOOST),
                                LevelBasedValue.perLevel(2.0F, 1.0F), LevelBasedValue.perLevel(4.0F, 3.0F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.1F, 0.2F))))
                .exclusiveWith(enchantments.getOrThrow(SHIELD_EXCLUSIVE)));

        register(context, ModEnchantments.ECHOES_BATTLE, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ECHOES_BATTLE.get()));

        register(context, ModEnchantments.SHIELD_ARMOR, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.hardened_1"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(2.5F, 1.5F),
                        AttributeModifier.Operation.ADD_VALUE)));
    }

    private ShieldEnchantments() {
    }
}
