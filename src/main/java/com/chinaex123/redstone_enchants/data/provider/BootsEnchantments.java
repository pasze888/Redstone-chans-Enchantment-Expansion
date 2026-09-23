package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.AreaIgniteEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AreaMobEffectEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MovementPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import org.joml.Vector3f;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.DamageImmunity;
import net.minecraft.world.item.enchantment.effects.DamageItem;
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.item.enchantment.effects.SpawnParticlesEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.EnchantmentActiveCheck;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 靴子附魔（含光环/行者/疾驰）的 datagen 声明。 */
final class BootsEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        register(context, ModEnchantments.PEGASUS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.PEGASUS.get()));

        register(context, ModEnchantments.WALKER_GRASS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 2,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
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
                                        BlockPredicate.matchesBlocks(new Vec3i(0, 1, 0), Blocks.AIR),
                                        BlockPredicate.matchesBlocks(Blocks.DIRT),
                                        BlockPredicate.unobstructed())),
                                BlockStateProvider.simple(Blocks.GRASS_BLOCK),
                                Optional.of(GameEvent.BLOCK_PLACE)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new SpawnParticlesEffect(new DustParticleOptions(new Vector3f(0.016F, 0.761F, 0.114F), 1.0F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, 0.0F, 1.0F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, 0.0F, 1.0F),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                ConstantFloat.of(1.0F)))
                .exclusiveWith(enchantments.getOrThrow(WALKER_EXCLUSIVE)));

        register(context, ModEnchantments.WALKER_MAGMA, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 2,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
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
                                        BlockPredicate.matchesBlocks(new Vec3i(0, 1, 0), Blocks.AIR),
                                        BlockPredicate.matchesFluids(Fluids.LAVA),
                                        BlockPredicate.unobstructed())),
                                new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                        .add(Blocks.BASALT.defaultBlockState(), 9).build()),
                                Optional.empty()),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true))))
                .exclusiveWith(enchantments.getOrThrow(WALKER_EXCLUSIVE)));

        register(context, ModEnchantments.WALKER_SNOWMELT, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 2,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE_IMMUNITY,
                        new DamageImmunity(),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.BURN_FROM_STEPPING))
                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new ReplaceDisk(
                                new LevelBasedValue.Clamped(LevelBasedValue.perLevel(3.0F, 1.0F), 0.0F, 16.0F),
                                LevelBasedValue.constant(1.0F), Vec3i.ZERO,
                                Optional.of(BlockPredicate.allOf(
                                        BlockPredicate.matchesBlocks(new Vec3i(0, 1, 0), Blocks.AIR),
                                        BlockPredicate.matchesBlocks(Blocks.SNOW),
                                        BlockPredicate.unobstructed())),
                                BlockStateProvider.simple(Blocks.AIR),
                                Optional.of(GameEvent.BLOCK_PLACE)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new SpawnParticlesEffect(new DustParticleOptions(new Vector3f(1.0F, 0.914F, 0.149F), 1.0F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, 0.0F, 1.0F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, 0.0F, 1.0F),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                ConstantFloat.of(1.0F)))
                .exclusiveWith(enchantments.getOrThrow(WALKER_EXCLUSIVE)));

        register(context, ModEnchantments.WAVE_WALKER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.DEPTH_STRIDER)))
                .withEffect(ModEnchantmentEffectComponents.WAVE_WALKER.get()));

        register(context, ModEnchantments.AURA_BURNING, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.FEET),
                0x55FFFF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaIgniteEffect(2.0F, 80))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(2.0F, MobEffects.FIRE_RESISTANCE, 60, 0, AreaMobEffectEffect.Target.SELF))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_GLOWING, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.GLOWING, 60, 0, AreaMobEffectEffect.Target.OTHERS))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_HASTE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.DIG_SPEED, 60, 0, AreaMobEffectEffect.Target.ALL))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_INFESTED, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.INFESTED, 60, 0, AreaMobEffectEffect.Target.OTHERS_NON_PLAYER))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_JUMP_BOOST, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.JUMP, 60, 0, AreaMobEffectEffect.Target.ALL))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_POISON, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.POISON, 60, 0, AreaMobEffectEffect.Target.OTHERS_NON_PLAYER))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_REGENERATION, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.REGENERATION, 60, 0, AreaMobEffectEffect.Target.ALL))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_RESISTANCE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.DAMAGE_RESISTANCE, 60, 0, AreaMobEffectEffect.Target.ALL))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_SLOWNESS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.MOVEMENT_SLOWDOWN, 60, 0, AreaMobEffectEffect.Target.OTHERS_NON_PLAYER))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_SPEED, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.MOVEMENT_SPEED, 60, 0, AreaMobEffectEffect.Target.ALL))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_STRENGTH, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.DAMAGE_BOOST, 60, 0, AreaMobEffectEffect.Target.ALL))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_WEAKNESS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.WEAKNESS, 60, 0, AreaMobEffectEffect.Target.OTHERS_NON_PLAYER))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_WITHER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.WITHER, 60, 0, AreaMobEffectEffect.Target.OTHERS_NON_PLAYER))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.CROP_DANCE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 1, 1,
                        Enchantment.dynamicCost(18, 8), Enchantment.dynamicCost(48, 18), 16, EquipmentSlotGroup.FEET),
                0xFF00BB)
                .withEffect(ModEnchantmentEffectComponents.CROP_DANCE.get())
                .withEffect(ModEnchantmentEffectComponents.CROP_DANCE_GROWTH_CHANCE.get(),
                        new SetValue(LevelBasedValue.perLevel(0.2F, 0.1F))));

        register(context, ModEnchantments.FLAME_WALKER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.DEPTH_STRIDER)))
                .withEffect(ModEnchantmentEffectComponents.FLAME_WALKER.get()));

        register(context, ModEnchantments.JUMP_AMPLIFIER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        ResourceLocation.withDefaultNamespace("enchantment.leaping_jump_1"),
                        Attributes.JUMP_STRENGTH,
                        LevelBasedValue.perLevel(0.15F, 0.25F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        ResourceLocation.withDefaultNamespace("enchantment.leaping_jump_2"),
                        Attributes.SAFE_FALL_DISTANCE,
                        LevelBasedValue.perLevel(0.15F, 0.25F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

        register(context, ModEnchantments.GALLOP, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.gallop_1"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.2F, 0.2F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .exclusiveWith(enchantments.getOrThrow(BOOTS_GALLOP_EXCLUSIVE)));

        register(context, ModEnchantments.GALLOP_END, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.soul_speed"),
                                Attributes.MOVEMENT_SPEED,
                                LevelBasedValue.perLevel(0.0405F, 0.0105F),
                                AttributeModifier.Operation.ADD_VALUE),
                        AllOfCondition.allOf(
                                InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity()))),
                                AnyOfCondition.anyOf(
                                        AllOfCondition.allOf(
                                                EnchantmentActiveCheck.enchantmentActiveCheck(),
                                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))),
                                                AnyOfCondition.anyOf(
                                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity().movementAffectedBy(
                                                                        LocationPredicate.Builder.location().inDimension(Level.END))),
                                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(false))))),
                                        AllOfCondition.allOf(
                                                EnchantmentActiveCheck.enchantmentInactiveCheck(),
                                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity().movementAffectedBy(
                                                                        LocationPredicate.Builder.location().inDimension(Level.END))
                                                                .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false)))))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.withDefaultNamespace("enchantment.soul_speed"),
                                Attributes.MOVEMENT_EFFICIENCY,
                                LevelBasedValue.constant(1.0F),
                                AttributeModifier.Operation.ADD_VALUE),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().movementAffectedBy(
                                        LocationPredicate.Builder.location().inDimension(Level.END))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new DamageItem(LevelBasedValue.constant(1.0F)),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.constant(0.04F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().movementAffectedBy(
                                                        LocationPredicate.Builder.location().inDimension(Level.END))
                                                .flags(EntityFlagsPredicate.Builder.flags().setOnGround(true)))))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new SpawnParticlesEffect(ParticleTypes.SOUL,
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, 0.1F, 1.0F),
                                new SpawnParticlesEffect.VelocitySource(-0.2F, ConstantFloat.ZERO),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.of(0.1F)),
                                ConstantFloat.of(1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity()
                                        .moving(new MovementPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY,
                                                MinMaxBounds.Doubles.atLeast(9.999999747378752E-06), MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY))
                                        .movementAffectedBy(LocationPredicate.Builder.location().inDimension(Level.END))
                                        .flags(EntityFlagsPredicate.Builder.flags().setOnGround(true).setIsFlying(false))
                                        .periodicTick(5)))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new PlaySoundEffect(SoundEvents.SOUL_ESCAPE,
                                ConstantFloat.of(0.6F), UniformFloat.of(0.6F, 1.0F)),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(0.35F),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity()
                                                .moving(new MovementPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY,
                                                MinMaxBounds.Doubles.atLeast(9.999999747378752E-06), MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY))
                                                .movementAffectedBy(LocationPredicate.Builder.location().inDimension(Level.END))
                                                .flags(EntityFlagsPredicate.Builder.flags().setOnGround(true).setIsFlying(false))
                                                .periodicTick(5))))
                .exclusiveWith(enchantments.getOrThrow(BOOTS_GALLOP_EXCLUSIVE)));

        register(context, ModEnchantments.GALLOP_SAND, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new EnchantmentAttributeEffect(
                                RedstoneEnchants.asResource("enchantment.gallop_sand_1"),
                                Attributes.MOVEMENT_SPEED,
                                LevelBasedValue.perLevel(0.0405F, 0.0105F),
                                AttributeModifier.Operation.ADD_VALUE),
                        AllOfCondition.allOf(
                                InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity()))),
                                AnyOfCondition.anyOf(
                                        AllOfCondition.allOf(
                                                EnchantmentActiveCheck.enchantmentActiveCheck(),
                                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))),
                                                AnyOfCondition.anyOf(
                                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity().movementAffectedBy(
                                                                        LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SAND)))),
                                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(false))))),
                                        AllOfCondition.allOf(
                                                EnchantmentActiveCheck.enchantmentInactiveCheck(),
                                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity().movementAffectedBy(
                                                                        LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SAND)))
                                                                .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false)))))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new EnchantmentAttributeEffect(
                                RedstoneEnchants.asResource("enchantment.gallop_sand_2"),
                                Attributes.MOVEMENT_EFFICIENCY,
                                LevelBasedValue.constant(1.0F),
                                AttributeModifier.Operation.ADD_VALUE),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().movementAffectedBy(
                                        LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SAND)))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new DamageItem(LevelBasedValue.constant(1.0F)),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.constant(0.04F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().movementAffectedBy(
                                                        LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SAND)))
                                                .flags(EntityFlagsPredicate.Builder.flags().setOnGround(true)))))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new SpawnParticlesEffect(new DustParticleOptions(new Vector3f(0.0F, 0.0F, 0.0F), 0.01F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, 0.1F, 1.0F),
                                new SpawnParticlesEffect.VelocitySource(-0.2F, ConstantFloat.ZERO),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.of(0.1F)),
                                ConstantFloat.of(1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity()
                                        .moving(new MovementPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY,
                                                MinMaxBounds.Doubles.atLeast(1.0E-05), MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY))
                                        .movementAffectedBy(LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SAND)))
                                        .flags(EntityFlagsPredicate.Builder.flags().setOnGround(true).setIsFlying(false))
                                        .periodicTick(5)))
                .exclusiveWith(enchantments.getOrThrow(BOOTS_GALLOP_EXCLUSIVE)));
    }

    private BootsEnchantments() {
    }
}
