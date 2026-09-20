package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.DamageImmunity;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 狼铠/马铠等动物铠附魔的 datagen 声明。 */
final class AnimalArmorEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, ModEnchantments.PACK_LEADER, colored(
                Enchantment.definition(items.getOrThrow(WOLF_ARMOR), items.getOrThrow(WOLF_ARMOR), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.PACK_LEADER_DAMAGE_BONUS.get(),
                        new SetValue(LevelBasedValue.perLevel(0.5F))));

        register(context, ModEnchantments.PASTURE, colored(
                Enchantment.definition(items.getOrThrow(HORSE_ARMOR), items.getOrThrow(HORSE_ARMOR), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.PASTURE_HEAL.get(),
                        new SetValue(LevelBasedValue.perLevel(0.5F))));

        register(context, ModEnchantments.SPIRIT, colored(
                Enchantment.definition(items.getOrThrow(HORSE_ARMOR), items.getOrThrow(HORSE_ARMOR), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.SPIRIT_SPEED_BONUS.get(),
                        new SetValue(LevelBasedValue.perLevel(0.25F))));

        register(context, ModEnchantments.TRACKER, colored(
                Enchantment.definition(items.getOrThrow(WOLF_ARMOR), items.getOrThrow(WOLF_ARMOR), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.BODY),
                0x55FFFF)
                .withEffect(ModEnchantmentEffectComponents.TRACKER_GLOW_DURATION_BONUS.get(),
                        new AddValue(LevelBasedValue.perLevel(20.0F))));

        register(context, ModEnchantments.FROST_HOOVES, colored(
                Enchantment.definition(items.getOrThrow(HORSE_ARMOR), items.getOrThrow(HORSE_ARMOR), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE_IMMUNITY,
                        new DamageImmunity(),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.BURN_FROM_STEPPING))
                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new ReplaceDisk(
                                new LevelBasedValue.Clamped(LevelBasedValue.perLevel(3.0F, 1.0F), 0.0F, 16.0F),
                                LevelBasedValue.constant(1.0F), new Vec3i(0, -1, 0),
                                Optional.of(BlockPredicate.allOf(
                                        BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
                                        BlockPredicate.matchesBlocks(Blocks.WATER),
                                        BlockPredicate.matchesFluids(Fluids.WATER),
                                        BlockPredicate.unobstructed())),
                                BlockStateProvider.simple(Blocks.FROSTED_ICE),
                                Optional.of(GameEvent.BLOCK_PLACE)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true)))));

        register(context, ModEnchantments.CARRION_EATER, colored(
                Enchantment.definition(items.getOrThrow(WOLF_ARMOR), items.getOrThrow(WOLF_ARMOR), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.CARRION_EATER_HEAL.get(),
                        new SetValue(LevelBasedValue.perLevel(0.25F))));

        register(context, ModEnchantments.ENHANCED_ARMOR, colored(
                Enchantment.definition(items.getOrThrow(HORSE_ANIMAL_ARMOR), items.getOrThrow(HORSE_ANIMAL_ARMOR), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enhanced_armor_bonus"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(2.0F, 2.0F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enhanced_armor_penalty"),
                        Attributes.JUMP_STRENGTH,
                        LevelBasedValue.perLevel(-0.05F, -0.05F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModEnchantments.MESSENGER, colored(
                Enchantment.definition(items.getOrThrow(HORSE_ANIMAL_ARMOR), items.getOrThrow(HORSE_ANIMAL_ARMOR), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.messenger_1"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.25F, 0.125F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.messenger_2"),
                        Attributes.MOVEMENT_EFFICIENCY,
                        LevelBasedValue.perLevel(1.0F, 0.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.messenger_3"),
                        Attributes.WATER_MOVEMENT_EFFICIENCY,
                        LevelBasedValue.perLevel(0.6F, 0.1F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.messenger_4"),
                        Attributes.JUMP_STRENGTH,
                        LevelBasedValue.perLevel(0.2F, 0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModEnchantments.MY_LITTLE_PONY, colored(
                Enchantment.definition(items.getOrThrow(HORSE_ARMOR), items.getOrThrow(HORSE_ARMOR), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.my_little_pony_1"),
                        Attributes.SCALE,
                        LevelBasedValue.perLevel(-0.8F, -0.2F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.my_little_pony_2"),
                        Attributes.JUMP_STRENGTH,
                        LevelBasedValue.perLevel(2.0F, 1.0F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.my_little_pony_3"),
                        Attributes.SAFE_FALL_DISTANCE,
                        LevelBasedValue.perLevel(10.0F, 10.0F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.my_little_pony_4"),
                        Attributes.FALL_DAMAGE_MULTIPLIER,
                        LevelBasedValue.perLevel(0.01F, 0.01F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModEnchantments.WOLF_SPIRIT_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(WOLF_ARMOR), items.getOrThrow(WOLF_ARMOR), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.BODY),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.wolf_spirit_shield_1"),
                        Attributes.ATTACK_DAMAGE,
                        LevelBasedValue.perLevel(5.0F, 5.0F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.wolf_spirit_shield_2"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(5.0F, 5.0F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.wolf_spirit_shield_3"),
                        Attributes.MAX_HEALTH,
                        LevelBasedValue.perLevel(10.0F, 10.0F),
                        AttributeModifier.Operation.ADD_VALUE)));
    }

    private AnimalArmorEnchantments() {
    }
}
