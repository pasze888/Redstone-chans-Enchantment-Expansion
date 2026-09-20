package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MovementPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.ExplodeEffect;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.phys.Vec3;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 重锤附魔的 datagen 声明。 */
final class MaceEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<DamageType> damageTypes = context.lookup(Registries.DAMAGE_TYPE);

        register(context, ModEnchantments.POTENTIAL_CONVERSION, colored(
                Enchantment.definition(items.getOrThrow(MACE_ITEMS), items.getOrThrow(MACE_ITEMS), 3, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.POTENTIAL_CONVERSION_FALL_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.008F)))
                .withEffect(ModEnchantmentEffectComponents.POTENTIAL_CONVERSION_ARMOR_FACTOR.get(),
                        new SetValue(LevelBasedValue.constant(0.015F))));

        register(context, ModEnchantments.BOLTBRINGER, colored(
                Enchantment.definition(items.getOrThrow(MACE_ITEMS), items.getOrThrow(MACE_ITEMS), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .exclusiveWith(enchantments.getOrThrow(MACE_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.BOLTBRINGER.get()));

        register(context, ModEnchantments.DIVE_BOMB, colored(
                Enchantment.definition(items.getOrThrow(MACE_ITEMS), items.getOrThrow(MACE_ITEMS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ExplodeEffect(false, Optional.of(damageTypes.getOrThrow(DamageTypes.EXPLOSION)),
                                Optional.empty(), Optional.empty(), Vec3.ZERO,
                                LevelBasedValue.perLevel(2.0F, 1.0F), true, Level.ExplosionInteraction.MOB,
                                ParticleTypes.EXPLOSION_EMITTER, ParticleTypes.EXPLOSION,
                                SoundEvents.GENERIC_EXPLODE),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))
                                        .moving(new MovementPredicate(
                                                MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY,
                                                MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY,
                                                MinMaxBounds.Doubles.atLeast(4.0)))))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.jump_strength_1"),
                        Attributes.JUMP_STRENGTH,
                        LevelBasedValue.perLevel(0.2F, 0.2F),
                        AttributeModifier.Operation.ADD_VALUE))
                .exclusiveWith(enchantments.getOrThrow(MACE_EXCLUSIVE)));

        register(context, ModEnchantments.SLUGGER, colored(
                Enchantment.definition(items.getOrThrow(MACE_ITEMS), items.getOrThrow(MACE_ITEMS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F),
                                LevelBasedValue.constant(5.0F), LevelBasedValue.constant(5.0F)))),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.2F, 0.1F)))));

        register(context, ModEnchantments.LIGHTNESS_HEAVY_DUALISM, colored(
                Enchantment.definition(items.getOrThrow(MACE_ITEMS), items.getOrThrow(MACE_ITEMS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.lightness-heavy_dualism_1"),
                        Attributes.GRAVITY,
                        LevelBasedValue.perLevel(-0.12F, -0.02F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.lightness-heavy_dualism_2"),
                        Attributes.ENTITY_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(0.8F, 0.05F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.WIND_PROPULSION, colored(
                Enchantment.definition(items.getOrThrow(MACE_ITEMS), items.getOrThrow(MACE_ITEMS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new ExplodeEffect(false, Optional.empty(),
                                Optional.of(new LevelBasedValue.Lookup(List.of(1.25F, 1.65F, 2.25F),
                                        LevelBasedValue.perLevel(1.25F, 0.35F))),
                                Optional.of(BuiltInRegistries.BLOCK.getOrCreateTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)),
                                new Vec3(0, 0.5, 0), LevelBasedValue.constant(3.5F), false,
                                Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL,
                                ParticleTypes.GUST_EMITTER_LARGE,
                                BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(ResourceKey.create(
                                        Registries.SOUND_EVENT,
                                        ResourceLocation.withDefaultNamespace("entity.wind_charge.wind_burst")))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true)))));
    }

    private MaceEnchantments() {
    }
}
