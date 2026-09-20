package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.SnowballBurstEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.TimeCheck;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 全身通用护甲与鞘翅附魔的 datagen 声明。 */
final class ArmorEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        register(context, ModEnchantments.SAFE_LANDING, colored(
                Enchantment.definition(items.getOrThrow(ELYTRA), items.getOrThrow(ELYTRA), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.CHEST),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantencore.safe_landing_1"),
                        Attributes.SAFE_FALL_DISTANCE,
                        LevelBasedValue.perLevel(2.0F, 2.5F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.DANGEROUS_EDGE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.dangerous_edge_1"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(-2.5F, -1.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.dangerous_edge_2"),
                        Attributes.ATTACK_DAMAGE,
                        LevelBasedValue.perLevel(2.5F, 1.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_EXCLUSIVE)));

        register(context, ModEnchantments.DAYNIGHT_CYCLE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.DAYNIGHT_CYCLE.get()));

        register(context, ModEnchantments.PROTECTION_DAY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0F, 2.0F)),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))),
                                TimeCheck.time(IntRange.range(0, 12000)).setPeriod(24000)))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_EXCLUSIVE)));

        register(context, ModEnchantments.PROTECTION_END, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0F, 2.0F)),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))),
                                LocationCheck.checkLocation(LocationPredicate.Builder.inDimension(Level.END))))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_EXCLUSIVE)));

        register(context, ModEnchantments.PROTECTION_NETHER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0F, 2.0F)),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))),
                                LocationCheck.checkLocation(LocationPredicate.Builder.inDimension(Level.NETHER))))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_EXCLUSIVE)));

        register(context, ModEnchantments.PROTECTION_NIGHT, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0F, 2.0F)),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))),
                                TimeCheck.time(IntRange.range(12000, 24000)).setPeriod(24000)))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_EXCLUSIVE)));

        register(context, ModEnchantments.REVIVE_WARD, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.ARMOR),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.REVIVE_WARD.get()));

        register(context, ModEnchantments.SNOWBALL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 6, 3,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.ARMOR),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new SnowballBurstEffect(),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.3F, 0.3F)))));

        register(context, ModEnchantments.FIRE_PROTECTION, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        ResourceLocation.withDefaultNamespace("enchantment.fire_protection_1"),
                        Attributes.BURNING_TIME,
                        LevelBasedValue.perLevel(-0.25F, -0.25F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0F, 2.0F)),
                        AllOfCondition.allOf(DamageSourceCondition.hasDamageSource(
                                DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate.is(DamageTypeTags.IS_FIRE))
                                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))))));

        register(context, ModEnchantments.FORTITUDE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.fortitude_1"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(0.14F, 0.12F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModEnchantments.FURY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS,
                        new AddValue(LevelBasedValue.perLevel(-0.045F, -0.035F)))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        ResourceLocation.withDefaultNamespace("enchantment.fury_1"),
                        Attributes.ATTACK_DAMAGE,
                        LevelBasedValue.perLevel(0.2F, 0.2F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        ResourceLocation.withDefaultNamespace("enchantment.fury_2"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(-0.1F, -0.2F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_EXCLUSIVE)));

        register(context, ModEnchantments.ENDER_HEART, colored(
                Enchantment.definition(items.getOrThrow(ELYTRA), items.getOrThrow(ELYTRA), 3, 2,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.CHEST),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.REGENERATION),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().periodicTick(40)))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.ender_heart_1"),
                        Attributes.MAX_HEALTH,
                        LevelBasedValue.perLevel(0.0F, 2.0F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.EXOSKELETON, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.exoskeleton_1"),
                        Attributes.ARMOR_TOUGHNESS,
                        LevelBasedValue.perLevel(4.0F, 2.0F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.MOONWALK, colored(
                Enchantment.definition(items.getOrThrow(ELYTRA), items.getOrThrow(ELYTRA), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.CHEST),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.moonwalk_1"),
                        Attributes.GRAVITY,
                        LevelBasedValue.perLevel(-0.03F, -0.015F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.WEAK_ARMOR, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("weak_armor_1"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(-1.0F, -1.0F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("weak_armor_2"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.05F, 0.05F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.DAMAGE_RESISTANCE),
                                LevelBasedValue.constant(0.2F), LevelBasedValue.constant(0.2F),
                                LevelBasedValue.constant(1.0F), LevelBasedValue.constant(1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags()
                                        .setOnGround(true).setCrouching(true))))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_EXCLUSIVE)));
    }

    private ArmorEnchantments() {
    }
}
