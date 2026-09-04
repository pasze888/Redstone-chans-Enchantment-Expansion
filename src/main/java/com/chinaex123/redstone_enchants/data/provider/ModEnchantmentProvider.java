package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomBeneficialMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomHarmfulMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RemoveRandomBeneficialEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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

/**
 * 已迁移附魔的注册表 bootstrap（runData 生成 {@code data/redstone_enchants/enchantment/*.json}）。
 * <p>数值与迁移前的手写 JSON 一致；效果从事件硬编码改为组件声明。后续批次继续往这里迁入。
 */
public final class ModEnchantmentProvider {
    public static final RegistrySetBuilder DATA_BUILDER =
            new RegistrySetBuilder().add(Registries.ENCHANTMENT, ModEnchantmentProvider::bootstrap);

    private static final TagKey<Item> TOOLS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("tools"));
    private static final TagKey<Item> SWORDS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("swords"));
    private static final TagKey<Item> SWORDS_AND_AXES = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("swords_and_axes"));
    private static final TagKey<Enchantment> STONE_TRANSMUTATION_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/stone_transmutation"));

    private static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        register(context, ModEnchantments.AUTO_SMELT, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.AUTO_SMELT.get()));

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

        register(context, ModEnchantments.NULLIFY, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RemoveRandomBeneficialEffect(LevelBasedValue.perLevel(0.05F))));

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

    private static Enchantment.Builder colored(Enchantment.EnchantmentDefinition definition, int color) {
        return Enchantment.enchantment(definition)
                .withCustomName(name -> name.withStyle(style -> style.withColor(color)));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private ModEnchantmentProvider() {
    }
}
