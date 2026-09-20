package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.GiveItemEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.DamageEntity;
import net.minecraft.world.item.enchantment.effects.DamageItem;
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.item.enchantment.effects.SpawnParticlesEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 胸甲附魔的 datagen 声明。 */
final class ChestplateEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<DamageType> damageTypes = context.lookup(Registries.DAMAGE_TYPE);

        register(context, ModEnchantments.BERSERK, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.CHEST),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.BERSERK_DAMAGE_BONUS.get(),
                        new SetValue(LevelBasedValue.perLevel(0.03F))));

        register(context, ModEnchantments.BULLETPROOF, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.CHEST),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.BULLETPROOF_IMMUNITY_CHANCE.get(),
                        new SetValue(LevelBasedValue.perLevel(0.5F, 0.25F))));

        register(context, ModEnchantments.SPELL_MAGIC_RESIST, colored(
                Enchantment.definition(items.getOrThrow(CHEST_ARMOR_ITEMS), items.getOrThrow(CHEST_ARMOR_ITEMS), 1, 5,
                        Enchantment.dynamicCost(18, 8), Enchantment.dynamicCost(48, 18), 16, EquipmentSlotGroup.CHEST),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_1"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ice_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_2"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "blood_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_3"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "holy_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_4"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "nature_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_5"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "eldritch_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_6"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "lightning_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_7"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "evocation_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_8"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ender_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.magic_resist_9"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fire_magic_resist"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.SPELL_POWER, colored(
                Enchantment.definition(items.getOrThrow(CHEST_ARMOR_ITEMS), items.getOrThrow(CHEST_ARMOR_ITEMS), 1, 5,
                        Enchantment.dynamicCost(18, 8), Enchantment.dynamicCost(48, 18), 16, EquipmentSlotGroup.CHEST),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_1"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ice_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_2"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "blood_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_3"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "holy_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_4"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "nature_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_5"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "eldritch_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_6"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "lightning_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_7"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "evocation_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_8"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ender_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.spell_power_9"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fire_spell_power"))),
                        LevelBasedValue.perLevel(0.1F, 0.1F), AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.DUAL_CRITICAL_BOOST, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 1, 5,
                        Enchantment.dynamicCost(18, 8), Enchantment.dynamicCost(48, 18), 16, EquipmentSlotGroup.CHEST),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.dual_critical_boost_1"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("apothic_attributes", "crit_chance"))),
                        LevelBasedValue.perLevel(0.233F, 0.126F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.dual_critical_boost_2"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("apothic_attributes", "crit_damage"))),
                        LevelBasedValue.perLevel(0.582F, 0.286F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.RESILIENT_BASTION, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.CHEST),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.DAMAGE_RESISTANCE),
                                LevelBasedValue.constant(0.2F), LevelBasedValue.constant(0.2F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F))));

        register(context, ModEnchantments.RETRIEVAL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.CHEST),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new GiveItemEffect(Items.SPECTRAL_ARROW),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.2F, 0.2F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityType.SPECTRAL_ARROW))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new GiveItemEffect(Items.ARROW),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.2F, 0.2F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityType.ARROW)))));

        register(context, ModEnchantments.VITALITY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 2, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.CHEST),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.vitality_1"),
                        Attributes.MAX_HEALTH,
                        LevelBasedValue.perLevel(0.25F, 0.25F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModEnchantments.FROST_THORN, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.CHEST),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.ATTACKER,
                        new AllOf.EntityEffects(List.of(
                                new DamageEntity(LevelBasedValue.constant(1.0F), LevelBasedValue.constant(5.0F),
                                        damageTypes.getOrThrow(DamageTypes.THORNS)),
                                new PlaySoundEffect(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PLAYER_HURT_FREEZE),
                                        ConstantFloat.of(5.0F), UniformFloat.of(0.6F, 0.8F)),
                                new SpawnParticlesEffect(ParticleTypes.SNOWFLAKE,
                                        new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                        new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.5F, 1.0F),
                                        new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                        new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                        ConstantFloat.of(1.0F)),
                                new SpawnParticlesEffect(ParticleTypes.SNOWFLAKE,
                                        new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                        new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.5F, 1.0F),
                                        new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                        new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                        ConstantFloat.of(1.0F)),
                                new SpawnParticlesEffect(ParticleTypes.SNOWFLAKE,
                                        new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                        new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.5F, 1.0F),
                                        new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                        new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                        ConstantFloat.of(1.0F)))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new DamageItem(LevelBasedValue.perLevel(0.15F, 0.0F)))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.ATTACKER,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.perLevel(1.0F, 1.0F), LevelBasedValue.perLevel(5.0F, 5.0F),
                                LevelBasedValue.perLevel(0.0F, 0.25F), LevelBasedValue.perLevel(1.0F, 0.5F)))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new SpawnParticlesEffect(ParticleTypes.SNOWFLAKE,
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 2.0F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 1.0F, 2.0F),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.of(0.0F)),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.of(0.5F)),
                                ConstantFloat.of(0.25F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().periodicTick(15)),
                                LootItemRandomChanceCondition.randomChance(0.75F))));
    }

    private ChestplateEnchantments() {
    }
}
