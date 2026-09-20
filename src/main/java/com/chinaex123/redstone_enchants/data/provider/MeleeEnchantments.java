package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.component.GamblerData;
import com.chinaex123.redstone_enchants.enchantment.effect.AddExperienceEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AddTagEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AirTossEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ChainBindEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ClearMainHandEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ParticleBurstEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.DevouringEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomBeneficialMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomHarmfulMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RemoveRandomBeneficialEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ThrowWaterBottleEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.UnleashPotentialEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect;
import net.minecraft.world.item.enchantment.effects.MultiplyValue;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.item.enchantment.effects.SpawnParticlesEffect;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.ValueCheckCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 剑/斧等近战武器附魔的 datagen 声明。 */
final class MeleeEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        register(context, ModEnchantments.AMBUSH, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.AMBUSH_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.2F))));

        register(context, ModEnchantments.AIR_TOSS, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_BOW), items.getOrThrow(SWORDS_AND_BOW), 3, 2,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AirTossEffect(LevelBasedValue.perLevel(0.0F, 1.0F)),
                        AllOfCondition.allOf(
                                AnyOfCondition.anyOf(
                                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)),
                                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                                .tag(TagPredicate.is(TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("arrows")))))),
                                () -> new ValueCheckCondition(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(1.0F, 1.0F)), IntRange.exact(1))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AirTossEffect(LevelBasedValue.perLevel(1.0F, 1.0F)),
                        AllOfCondition.allOf(
                                AnyOfCondition.anyOf(
                                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)),
                                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                                .tag(TagPredicate.is(TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("arrows")))))),
                                () -> new ValueCheckCondition(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(1.0F, 1.0F)), IntRange.exact(2))))
                .exclusiveWith(enchantments.getOrThrow(SWORDS_AND_BOW_EXCLUSIVE)));

        register(context, ModEnchantments.BACKSTAB, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.BACKSTAB_BEHIND_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.3F)))
                .withEffect(ModEnchantmentEffectComponents.BACKSTAB_FRONT_PENALTY.get(), new AddValue(LevelBasedValue.perLevel(0.15F))));

        register(context, ModEnchantments.BANE_BADY, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new MultiplyValue(LevelBasedValue.perLevel(2.0F, 2.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsBaby(true))))
                .exclusiveWith(enchantments.getOrThrow(BANE_EXCLUSIVE)));

        register(context, ModEnchantments.BANE_BOSS, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F, 2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(ENTITY_BOSS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.constant(1.5F), LevelBasedValue.perLevel(1.5F, 0.5F),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(ENTITY_BOSS)),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))))
                .exclusiveWith(enchantments.getOrThrow(BANE_EXCLUSIVE)));

        register(context, ModEnchantments.BANE_END, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F, 2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(ENTITY_END)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.constant(1.5F), LevelBasedValue.perLevel(1.5F, 0.5F),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(ENTITY_END)),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))))
                .exclusiveWith(enchantments.getOrThrow(BANE_EXCLUSIVE)));

        register(context, ModEnchantments.BANE_ILLAGER, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F, 2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(ENTITY_ILLAGER)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.constant(1.5F), LevelBasedValue.perLevel(1.5F, 0.5F),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(ENTITY_ILLAGER)),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))))
                .exclusiveWith(enchantments.getOrThrow(BANE_EXCLUSIVE)));

        register(context, ModEnchantments.BANE_NETHER, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F, 2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(ENTITY_NETHER)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.constant(1.5F), LevelBasedValue.perLevel(1.5F, 0.5F),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(ENTITY_NETHER)),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))))
                .exclusiveWith(enchantments.getOrThrow(BANE_EXCLUSIVE)));

        register(context, ModEnchantments.BANE_PHANTOM, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F, 2.5F)),
                        AllOfCondition.allOf(
                                AnyOfCondition.anyOf(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(EntityType.PHANTOM)
                                                .flags(EntityFlagsPredicate.Builder.flags().setOnGround(false)))))
                .exclusiveWith(enchantments.getOrThrow(BANE_EXCLUSIVE)));

        register(context, ModEnchantments.BUTCHER, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 4, 5,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF)
                .withEffect(ModEnchantmentEffectComponents.BUTCHER_EXTRA_DROP.get(), new AddValue(LevelBasedValue.perLevel(0.5F))));

        register(context, ModEnchantments.DECAPITATION, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.DECAPITATION_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.2F))));

        register(context, ModEnchantments.DEVOURING, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new DevouringEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity())));

        register(context, ModEnchantments.DYNAMO, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0x55FFFF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(1.0F, 1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setSprinting(true))))
                .withEffect(EnchantmentEffectComponents.KNOCKBACK,
                        new AddValue(LevelBasedValue.perLevel(0.5F, 0.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setSprinting(true)))));

        register(context, ModEnchantments.SHADOW_ASSAULT, colored(
                Enchantment.definition(items.getOrThrow(WEAPON_ITEMS), items.getOrThrow(WEAPON_ITEMS), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new MultiplyValue(LevelBasedValue.constant(2.0F)),
                        LootItemRandomChanceCondition.randomChance(0.33F))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new MultiplyValue(LevelBasedValue.constant(2.0F)),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(0.33F),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(PROJECTILES))))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

        register(context, ModEnchantments.SHADOW_PIERCE, colored(
                Enchantment.definition(items.getOrThrow(SWORDS), items.getOrThrow(SWORDS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new MultiplyValue(LevelBasedValue.perLevel(1.5F, 1.5F)),
                        AnyOfCondition.anyOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects()
                                                .and(MobEffects.INVISIBILITY))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects()
                                                .and(MobEffects.DARKNESS))))));

        register(context, ModEnchantments.COMMITTED, colored(
                Enchantment.definition(items.getOrThrow(WEAPON_ITEMS), items.getOrThrow(WEAPON_ITEMS), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.HAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new MultiplyValue(LevelBasedValue.perLevel(1.5F, 0.25F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().nbt(nbtPredicate("{Tags:[\"harmed_by_committed\"]}"))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new ParticleBurstEffect(ParticleTypes.ANGRY_VILLAGER, 1, 0.0F, 0.0F, 0.0F, 0.0F, 2.2F),
                                new AddTagEffect("harmed_by_committed"))),
                        AllOfCondition.allOf(
                                InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().nbt(nbtPredicate("{Tags:[\"harmed_by_committed\"]}")))),
                                InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(EntityType.PLAYER)))))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

        register(context, ModEnchantments.RESILIENCE_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.resilience_shield_1"),
                        Attributes.KNOCKBACK_RESISTANCE,
                        LevelBasedValue.perLevel(0.2F, 0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModEnchantments.TOUCH_BLEEDING, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(
                                foreignId(ResourceKey.create(Registries.MOB_EFFECT,
                                        ResourceLocation.fromNamespaceAndPath("apothic_attributes", "bleeding"))),
                                LevelBasedValue.constant(4.0F), LevelBasedValue.constant(6.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment()
                                        .mainhand(ItemPredicate.Builder.item().of(SWORDS_AND_AXES)))))
                .exclusiveWith(enchantments.getOrThrow(TOUCH_EXCLUSIVE)));

        register(context, ModEnchantments.TOUCH_POISON, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(HolderSet.direct(MobEffects.POISON),
                                LevelBasedValue.constant(4.0F), LevelBasedValue.constant(6.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment()
                                        .mainhand(ItemPredicate.Builder.item().of(SWORDS_AND_AXES)))))
                .exclusiveWith(enchantments.getOrThrow(TOUCH_EXCLUSIVE)));

        register(context, ModEnchantments.TOUCH_WITHER, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(HolderSet.direct(MobEffects.WITHER),
                                LevelBasedValue.constant(4.0F), LevelBasedValue.constant(6.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment()
                                        .mainhand(ItemPredicate.Builder.item().of(SWORDS_AND_AXES)))))
                .exclusiveWith(enchantments.getOrThrow(TOUCH_EXCLUSIVE)));

        register(context, ModEnchantments.UNDERCURRENT, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new ApplyMobEffect(
                                        foreignId(ResourceKey.create(Registries.MOB_EFFECT,
                                                ResourceLocation.fromNamespaceAndPath("twilightforest", "frosted"))),
                                        LevelBasedValue.constant(5.0F), LevelBasedValue.constant(10.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.WITHER),
                                        LevelBasedValue.constant(5.0F), LevelBasedValue.constant(10.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.WEAKNESS),
                                        LevelBasedValue.constant(5.0F), LevelBasedValue.constant(10.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
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
                                        ConstantFloat.of(1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment()
                                        .mainhand(ItemPredicate.Builder.item().of(SWORDS_AND_AXES)))))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.undercurrent_1"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("apothic_attributes", "cold_damage"))),
                        LevelBasedValue.perLevel(2.0F, 1.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

        register(context, ModEnchantments.UNDER_PRESSURE, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(
                                HolderSet.direct(MobEffects.DAMAGE_BOOST, MobEffects.DIG_SPEED, MobEffects.MOVEMENT_SPEED),
                                LevelBasedValue.constant(5.0F), LevelBasedValue.constant(5.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment()
                                        .mainhand(ItemPredicate.Builder.item().of(ItemTags.WEAPON_ENCHANTABLE)))))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

        register(context, ModEnchantments.UNLEASH_POTENTIAL, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new UnleashPotentialEffect())
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

        register(context, ModEnchantments.WATER_BOTTLE_PROJECTION, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 6, 2,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.MAINHAND),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ThrowWaterBottleEffect(),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)),
                                () -> new ValueCheckCondition(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(1.0F, 1.0F)),
                                        IntRange.exact(1))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ThrowWaterBottleEffect(),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)),
                                () -> new ValueCheckCondition(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(1.0F, 1.0F)),
                                        IntRange.exact(2)))));

        register(context, ModEnchantments.CHAINS, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ChainBindEffect(),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)),
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.2F, 0.2F))),
                                InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects()
                                                .and(MobEffects.MOVEMENT_SLOWDOWN).and(MobEffects.WEAKNESS)))))));

        register(context, ModEnchantments.EXORCISM, colored(
                Enchantment.definition(items.getOrThrow(WEAPON_ITEMS), items.getOrThrow(WEAPON_ITEMS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F, 2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EXORCISM_ENTITIES)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(
                                HolderSet.direct(MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.constant(1.5F), LevelBasedValue.perLevel(3.0F, 0.5F),
                                LevelBasedValue.constant(1.0F), LevelBasedValue.constant(1.0F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(EXORCISM_ENTITIES)),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_BANE_EXCLUSIVE)));

        register(context, ModEnchantments.FIRST_IMPRESSION, colored(
                Enchantment.definition(items.getOrThrow(WEAPON_ITEMS), items.getOrThrow(WEAPON_ITEMS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AddTagEffect("redstone_enchants.first_impression"))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(5.0F, 2.5F)),
                        InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().nbt(nbtPredicate("{Tags:['redstone_enchants.first_impression']}"))))));

        register(context, ModEnchantments.SWIFT_SHADOWCUTTER, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.swift_shadowcutter_1"),
                        Attributes.ATTACK_SPEED,
                        LevelBasedValue.perLevel(0.05F, 0.15F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

        register(context, ModEnchantments.BOONS, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new RandomBeneficialMobEffect(LevelBasedValue.perLevel(0.05F))));

        register(context, ModEnchantments.CALAMITY, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RandomHarmfulMobEffect(LevelBasedValue.perLevel(0.05F))));

        register(context, ModEnchantments.EQUALIZER, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.EQUALIZER_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.2F))));

        register(context, ModEnchantments.NULLIFY, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RemoveRandomBeneficialEffect(LevelBasedValue.perLevel(0.05F))));

        register(context, ModEnchantments.EXECUTION, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.EXECUTION.get()));

        register(context, ModEnchantments.LAST_HOPE, colored(
                Enchantment.definition(items.getOrThrow(LAST_HOPE_WEAPONS), items.getOrThrow(LAST_HOPE_WEAPONS), 1, 1,
                        Enchantment.dynamicCost(120, 100), Enchantment.dynamicCost(150, 120), 200, EquipmentSlotGroup.MAINHAND),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new ClearMainHandEffect(),
                        InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(BLACK_ENTITY))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ParticleBurstEffect(ParticleTypes.SONIC_BOOM, 50, 1.0F, 1.0F, 1.0F, 0.5F, 0.0F))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.constant(2.14748365E9F)),
                        InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(BLACK_ENTITY))))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

        register(context, ModEnchantments.LIFE_STEAL, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.LIFE_STEAL_RATIO.get(),
                        new SetValue(LevelBasedValue.perLevel(0.1F))));

        register(context, ModEnchantments.GAMBLER, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_BOW), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withSpecialEffect(ModEnchantmentEffectComponents.GAMBLER_DATA.get(),
                        new GamblerData(0.5F, 1.4F, 0.8F)));

        register(context, ModEnchantments.WEIGHTED, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.weighted_damage"),
                        Attributes.ATTACK_DAMAGE,
                        LevelBasedValue.perLevel(0.1F, 0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.weighted_speed"),
                        Attributes.ATTACK_SPEED,
                        LevelBasedValue.perLevel(-0.05F, -0.05F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

        register(context, ModEnchantments.XP_BLADE, colored(
                Enchantment.definition(items.getOrThrow(WEAPON_ITEMS), items.getOrThrow(WEAPON_ITEMS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new AddExperienceEffect(-15),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER,
                                EntityPredicate.Builder.entity().subPredicate(
                                        PlayerPredicate.Builder.player().setLevel(MinMaxBounds.Ints.atLeast(10)).build())))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(1.0F, 1.25F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER,
                                EntityPredicate.Builder.entity().subPredicate(
                                        PlayerPredicate.Builder.player().setLevel(MinMaxBounds.Ints.atLeast(10)).build())))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

        register(context, ModEnchantments.XP_REAPER_MOBS, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF)
                .withEffect(EnchantmentEffectComponents.MOB_EXPERIENCE,
                        new MultiplyValue(LevelBasedValue.perLevel(1.5F, 1.0F))));
    }

    private MeleeEnchantments() {
    }
}
