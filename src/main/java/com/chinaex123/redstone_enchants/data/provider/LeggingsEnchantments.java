package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
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
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 护腿附魔的 datagen 声明。 */
final class LeggingsEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, ModEnchantments.SAFE_FALL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.LEGS),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.safe_fall_1"),
                        Attributes.SAFE_FALL_DISTANCE,
                        LevelBasedValue.perLevel(2.0F, 1.0F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.STRIDING, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.LEGS),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.striding_1"),
                        Attributes.STEP_HEIGHT,
                        LevelBasedValue.perLevel(0.5F, 0.25F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.striding_2"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.0F, 0.05F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModEnchantments.RESILIENCE_SENTINEL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 2, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.LEGS),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(HolderSet.direct(MobEffects.DAMAGE_RESISTANCE),
                                LevelBasedValue.constant(6.0F), LevelBasedValue.constant(12.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.constant(2.0F)))),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.1F, 0.2F))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.ATTACKER,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(HolderSet.direct(MobEffects.WEAKNESS),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.constant(0.0F)))),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.1F, 0.05F)))));

        register(context, ModEnchantments.FAST_SWIM, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.LEGS),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.DOLPHINS_GRACE),
                                LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.constant(0.0F)),
                        AllOfCondition.allOf(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setSwimming(true))
                                        .periodicTick(80)))));

        register(context, ModEnchantments.FORTRESS_STANCE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 2, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.LEGS),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(
                                HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DAMAGE_RESISTANCE),
                                LevelBasedValue.constant(0.8F), LevelBasedValue.constant(0.8F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags()
                                        .setOnGround(true).setCrouching(true)))));

        register(context, ModEnchantments.INVISIBILITY_CLOAK, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.LEGS),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.INVISIBILITY_CLOAK.get()));

        register(context, ModEnchantments.TACTICAL_KNEE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.LEGS),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.TACTICAL_KNEE.get()));
    }

    private LeggingsEnchantments() {
    }
}
