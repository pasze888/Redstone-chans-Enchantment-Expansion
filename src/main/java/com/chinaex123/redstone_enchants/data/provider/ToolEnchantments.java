package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.DamageItem;
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect;
import net.minecraft.world.item.enchantment.effects.MultiplyValue;
import net.minecraft.world.item.enchantment.effects.ReplaceBlock;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.item.enchantment.effects.SpawnParticlesEffect;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 工具/钓鱼竿/剪刀/打火石附魔的 datagen 声明。 */
final class ToolEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        register(context, ModEnchantments.AUTO_SMELT, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.AUTO_SMELT.get()));

        register(context, ModEnchantments.BEDROCK_BREAKER, colored(
                Enchantment.definition(items.getOrThrow(PICKAXES_ITEMS), items.getOrThrow(PICKAXES_ITEMS), 1, 1,
                        Enchantment.dynamicCost(80, 50), Enchantment.dynamicCost(100, 50), 200, EquipmentSlotGroup.MAINHAND),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new PlaySoundEffect(SoundEvents.GENERIC_EXPLODE,
                                ConstantFloat.of(1.0F), ConstantFloat.of(2.0F)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new AllOf.EntityEffects(List.of(
                                new SpawnParticlesEffect(ParticleTypes.EXPLOSION_EMITTER,
                                        new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                        new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                        new SpawnParticlesEffect.VelocitySource(0.1F, ConstantFloat.of(0.1F)),
                                        new SpawnParticlesEffect.VelocitySource(0.1F, ConstantFloat.of(0.1F)),
                                        ConstantFloat.of(0.0F)),
                                new PlaySoundEffect(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BREEZE_DEATH),
                                        ConstantFloat.of(0.1F), ConstantFloat.of(0.75F)),
                                new ReplaceBlock(Vec3i.ZERO, Optional.empty(),
                                        BlockStateProvider.simple(Blocks.AIR),
                                        Optional.of(GameEvent.BLOCK_DESTROY)),
                                new DamageItem(LevelBasedValue.constant(1000.0F)))),
                        LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(
                                net.minecraft.advancements.critereon.BlockPredicate.Builder.block()
                                        .of(BEDROCK_BREAKER_BLOCKS))))
                .exclusiveWith(enchantments.getOrThrow(BEDROCK_EXCLUSIVE)));

        register(context, ModEnchantments.ROCK_ILLUSION, colored(
                Enchantment.definition(items.getOrThrow(PICKAXES_ITEMS), items.getOrThrow(PICKAXES_ITEMS), 1, 1,
                        Enchantment.dynamicCost(80, 50), Enchantment.dynamicCost(100, 50), 200, EquipmentSlotGroup.MAINHAND),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new DamageItem(LevelBasedValue.constant(1000.0F)),
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BEDROCK))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new ReplaceBlock(Vec3i.ZERO,
                                Optional.of(BlockPredicate.matchesBlocks(Blocks.BEDROCK)),
                                BlockStateProvider.simple(Blocks.REINFORCED_DEEPSLATE),
                                Optional.of(GameEvent.BLOCK_DESTROY)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new SpawnParticlesEffect(ParticleTypes.POOF,
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                new SpawnParticlesEffect.VelocitySource(0.0F, ConstantFloat.ZERO),
                                ConstantFloat.of(0.5F)),
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BEDROCK))
                .exclusiveWith(enchantments.getOrThrow(BEDROCK_EXCLUSIVE)));

        register(context, ModEnchantments.SEARING, colored(
                Enchantment.definition(items.getOrThrow(ALL_FLINT_AND_STEEL), items.getOrThrow(ALL_FLINT_AND_STEEL), 5, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF)
                .withEffect(ModEnchantmentEffectComponents.SEARING_FIRE_TICKS.get(), new SetValue(LevelBasedValue.perLevel(40.0F)))
                .withEffect(ModEnchantmentEffectComponents.SEARING_DAMAGE.get(), new SetValue(LevelBasedValue.perLevel(1.0F))));

        register(context, ModEnchantments.XP_SPRING_BLOCK, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), items.getOrThrow(TOOLS), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF)
                .withEffect(EnchantmentEffectComponents.BLOCK_EXPERIENCE,
                        new MultiplyValue(LevelBasedValue.perLevel(1.5F, 1.0F))));

        register(context, ModEnchantments.ANGLER, colored(
                Enchantment.definition(items.getOrThrow(ALL_FISHING), items.getOrThrow(ALL_FISHING), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ANGLER_DOUBLE_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.1F))));

        register(context, ModEnchantments.CONDUCTIVE_LINE, colored(
                Enchantment.definition(items.getOrThrow(ALL_FISHING), items.getOrThrow(ALL_FISHING), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.CONDUCTIVE_LINE.get()));

        register(context, ModEnchantments.TIDE_SENSE, colored(
                Enchantment.definition(items.getOrThrow(ALL_FISHING), items.getOrThrow(ALL_FISHING), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.FISHING_TIME_REDUCTION, new AddValue(LevelBasedValue.perLevel(10.0F)))
                .withEffect(ModEnchantmentEffectComponents.TIDE_SENSE_FISH_CHANCE.get(),
                        new SetValue(LevelBasedValue.perLevel(0.8F, 0.1F))));

        register(context, ModEnchantments.ENDLESS_WOOL, colored(
                Enchantment.definition(items.getOrThrow(ALL_SHEAR), items.getOrThrow(ALL_SHEAR), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ENDLESS_WOOL_REGROW_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.10F))));

        register(context, ModEnchantments.EXPERIENCE_SHEAR, colored(
                Enchantment.definition(items.getOrThrow(ALL_SHEAR), items.getOrThrow(ALL_SHEAR), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.EXPERIENCE_SHEAR_EXP_PER_LEVEL.get(),
                        new SetValue(LevelBasedValue.perLevel(3.0F))));

        register(context, ModEnchantments.EXTEND, colored(
                Enchantment.definition(items.getOrThrow(ALL_TOOLS), items.getOrThrow(ALL_TOOLS), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.HAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.extend_1"),
                        Attributes.BLOCK_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(1.0F, 1.0F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.extend_2"),
                        Attributes.ENTITY_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(1.0F, 1.0F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.HARVEST_ECHO, colored(
                Enchantment.definition(items.getOrThrow(ALL_SHEAR), items.getOrThrow(ALL_SHEAR), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.HARVEST_ECHO.get()));

        register(context, ModEnchantments.SHEPHERD, colored(
                Enchantment.definition(items.getOrThrow(ALL_SHEAR), items.getOrThrow(ALL_SHEAR), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.SHEPHERD_EXTRA_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.2F))));

        register(context, ModEnchantments.CHAIN_HASTE, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.CHAIN_HASTE_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.01F))));

        register(context, ModEnchantments.EXCAVATOR, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.AREA_BREAK_RADIUS.get(), new AddValue(LevelBasedValue.perLevel(1.0F))));

        register(context, ModEnchantments.GEOLOGY, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.PICKAXES), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .exclusiveWith(enchantments.getOrThrow(STONE_TRANSMUTATION_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.STONE_TO_ORE_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.05F))));

        register(context, ModEnchantments.GOLDFINGER, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.PICKAXES), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .exclusiveWith(enchantments.getOrThrow(STONE_TRANSMUTATION_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.STONE_TO_GOLD_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.025F))));

        register(context, ModEnchantments.HASTE, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("haste"),
                        Attributes.BLOCK_BREAK_SPEED,
                        LevelBasedValue.perLevel(0.1F, 0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

        register(context, ModEnchantments.LIGHTWEIGHT, colored(
                Enchantment.definition(items.getOrThrow(ALL_TOOLS), items.getOrThrow(ALL_TOOLS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("lightweight"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.05F, 0.05F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

        register(context, ModEnchantments.MAGNET, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.PICKAXES), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.MAGNET_RANGE.get(), new AddValue(LevelBasedValue.perLevel(4.0F))));

        register(context, ModEnchantments.MASTER_GATHERER, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ORE_DOUBLE_DROP_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.2F))));

        register(context, ModEnchantments.MOIST, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.HOES), 3, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.MOIST.get()));

        register(context, ModEnchantments.TIMBER, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.AXES), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF));
    }

    private ToolEnchantments() {
    }
}
