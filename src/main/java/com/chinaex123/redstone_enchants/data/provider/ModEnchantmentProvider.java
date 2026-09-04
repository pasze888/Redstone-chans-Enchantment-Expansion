package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.component.GamblerData;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomBeneficialMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomHarmfulMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RemoveRandomBeneficialEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import net.minecraft.world.item.enchantment.effects.RunFunction;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

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
    private static final TagKey<Item> SWORDS_AND_BOW = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("swords_and_bow"));
    private static final TagKey<Item> MACE_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/mace"));
    private static final TagKey<Item> SHIELD_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/shield"));
    private static final TagKey<Item> ALL_FLINT_AND_STEEL = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_flint_and_steel"));
    private static final TagKey<Item> ARMORS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors"));
    private static final TagKey<Item> ALL_BOW = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_bow"));
    private static final TagKey<Item> ALL_FISHING = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_fishing"));
    private static final TagKey<Item> ALL_SHEAR = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_shear"));
    private static final TagKey<Item> ARMORS_HEAD = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors_head"));
    private static final TagKey<Item> ARMORS_FOOT = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors_foot"));
    private static final TagKey<Item> ARMORS_CHEST = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors_chest"));
    private static final TagKey<Item> ARMORS_LEG = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors_leg"));
    private static final TagKey<Item> WOLF_ARMOR = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("wolf_armor"));
    private static final TagKey<Item> HORSE_ARMOR = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("horse_armor"));
    private static final TagKey<Enchantment> MACE_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/mace"));
    private static final TagKey<Enchantment> NO_SEA_BREEZE_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/no_sea_breeze"));
    private static final TagKey<Item> ENCHANTABLES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "enchantables"));
    private static final TagKey<Enchantment> STONE_TRANSMUTATION_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/stone_transmutation"));
    private static final TagKey<Enchantment> UNBREAKING_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/unbreaking"));
    private static final TagKey<Enchantment> INDESTRUCTIBLE_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/indestructible"));
    private static final TagKey<Enchantment> AURA_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/aura"));
    private static final TagKey<Enchantment> SPLASH_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/splash"));
    private static final TagKey<Enchantment> BOOTS_GALLOP_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/boots_gallop"));

    /**
     * 跨注册表裸 id 的 HolderSet：holder owner 恒返回 canSerializeIn=true，
     * 使 datagen 序列化走 ResourceKey 分支直接输出 id 字符串，
     * 不要求该注册表条目在 datagen JVM 里真实存在（如 ars_nouveau:blasting）。
     */
    private static <T> HolderSet<T> foreignId(ResourceKey<T> key) {
        return HolderSet.direct(Holder.Reference.createStandAlone(
                new HolderOwner<T>() {
                    @Override
                    public boolean canSerializeIn(HolderOwner<T> owner) {
                        return true;
                    }
                }, key));
    }

    private static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        register(context, ModEnchantments.AUTO_SMELT, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.AUTO_SMELT.get()));

        register(context, ModEnchantments.ADVANCED_UNBREAKING, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 1, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.ANY),
                0xFFAA00)
                .exclusiveWith(enchantments.getOrThrow(UNBREAKING_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.ITEM_DAMAGE,
                        new RemoveBinomial(new LevelBasedValue.Fraction(LevelBasedValue.constant(4), LevelBasedValue.constant(5)))));

        register(context, ModEnchantments.ADAPTIVE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HEAD),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.ADAPTIVE.get()));

        register(context, ModEnchantments.AGAINST_ALL_ODDS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.HEAD),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.AGAINST_ALL_ODDS_BONUS_PER_ENEMY.get(),
                        new SetValue(LevelBasedValue.perLevel(0.02F))));

        register(context, ModEnchantments.AMBUSH, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.AMBUSH_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.2F))));

        register(context, ModEnchantments.BACKSTAB, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.BACKSTAB_BEHIND_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.3F)))
                .withEffect(ModEnchantmentEffectComponents.BACKSTAB_FRONT_PENALTY.get(), new AddValue(LevelBasedValue.perLevel(0.15F))));

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

        register(context, ModEnchantments.DESPERATE_COUNTER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HEAD),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.DESPERATE_COUNTER_DAMAGE.get(),
                        new SetValue(LevelBasedValue.perLevel(0.25F))));

        register(context, ModEnchantments.SACRIFICE, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.ANY),
                0xFFAA00)
                .exclusiveWith(enchantments.getOrThrow(UNBREAKING_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.SACRIFICE_REPAIR.get(),
                        new SetValue(LevelBasedValue.perLevel(1.0F, 0.5F))));

        register(context, ModEnchantments.INDESTRUCTIBLE, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 1, 1,
                        Enchantment.dynamicCost(0, 0), Enchantment.dynamicCost(0, 0), 50, EquipmentSlotGroup.ANY),
                0xFF00BB)
                .exclusiveWith(enchantments.getOrThrow(INDESTRUCTIBLE_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.INDESTRUCTIBLE.get()));

        register(context, ModEnchantments.STURDY, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ANY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.STURDY.get()));

        register(context, ModEnchantments.PEGASUS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.PEGASUS.get()));

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

        register(context, ModEnchantments.PRESERVATION, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ANY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.PRESERVATION.get()));

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

        register(context, ModEnchantments.ECHOES_BATTLE, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ECHOES_BATTLE.get()));

        register(context, ModEnchantments.SEA_BREEZE, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE), items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .exclusiveWith(enchantments.getOrThrow(NO_SEA_BREEZE_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.SEA_BREEZE.get())
                .withEffect(ModEnchantmentEffectComponents.SEA_BREEZE_DAMAGE.get(),
                        new SetValue(LevelBasedValue.perLevel(4.0F, 2.0F))));

        register(context, ModEnchantments.SEARING, colored(
                Enchantment.definition(items.getOrThrow(ALL_FLINT_AND_STEEL), items.getOrThrow(ALL_FLINT_AND_STEEL), 5, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF)
                .withEffect(ModEnchantmentEffectComponents.SEARING_FIRE_TICKS.get(), new SetValue(LevelBasedValue.perLevel(40.0F)))
                .withEffect(ModEnchantmentEffectComponents.SEARING_DAMAGE.get(), new SetValue(LevelBasedValue.perLevel(1.0F))));

        register(context, ModEnchantments.SPLASH_BLINDNESS, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/blindness")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/blindness")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.BLINDNESS),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_DELAYED_EXPLOSION, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 2, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/delayed_explosion")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/delayed_explosion")),
                                new ApplyMobEffect(foreignId(ResourceKey.create(Registries.MOB_EFFECT,
                                                ResourceLocation.fromNamespaceAndPath("ars_nouveau", "blasting"))),
                                        LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_GLOWING, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/glowing")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/glowing")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.GLOWING),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_HUNGER, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/hunger")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/hunger")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.HUNGER),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_INFESTED, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/infested")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/infested")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.INFESTED),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_OOZING, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/oozing")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/oozing")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.OOZING),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_POISON, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/poison")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/poison")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.POISON),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_REGENERATION, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/regeneration")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/regeneration")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.REGENERATION),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_SLOW_FALLING, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/slow_falling")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/slow_falling")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.SLOW_FALLING),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_SLOWNESS, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/slowness")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/slowness")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_SPEED, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/speed")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/speed")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SPEED),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(BOOTS_GALLOP_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_WEAVING, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/weaving")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/weaving")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.WEAVING),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_WIND_CHARGED, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/wind_charged")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/wind_charged")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.WIND_CHARGED),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.SPLASH_WITHER, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/wither")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/splash_arrow/wither")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.WITHER),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.CURSE_OF_RUST, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.ANY),
                0xFF5555)
                .withEffect(ModEnchantmentEffectComponents.CURSE_OF_RUST_DURABILITY.get(),
                        new SetValue(LevelBasedValue.perLevel(1.0F))));

        register(context, ModEnchantments.CURSE_OF_WATER_SOURCE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.ARMOR),
                0xFF5555)
                .withEffect(ModEnchantmentEffectComponents.CURSE_OF_WATER_SOURCE.get()));

        register(context, ModEnchantments.DAYNIGHT_CYCLE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.DAYNIGHT_CYCLE.get()));

        register(context, ModEnchantments.REVIVE_WARD, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.ARMOR),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.REVIVE_WARD.get()));

        register(context, ModEnchantments.SNIPE, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.SNIPE_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.15F))));

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

        register(context, ModEnchantments.TRAIL_CHERRY_LEAVES, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/cherry_leaves"))));

        register(context, ModEnchantments.TRAIL_DRAGON_BREATH, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/dragon_breath"))));

        register(context, ModEnchantments.TRAIL_FIREWORK, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/firework"))));

        register(context, ModEnchantments.TRAIL_GLOW, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/glow"))));

        register(context, ModEnchantments.TRAIL_SCULK_SOUL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/sculk_soul"))));

        register(context, ModEnchantments.TRAIL_SNOWFLAKE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/snowflake"))));

        register(context, ModEnchantments.TRAIL_TRIAL_OMEN, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/trial_omen"))));

        register(context, ModEnchantments.TRAIL_WAX_OFF, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/wax_off"))));

        register(context, ModEnchantments.TRAIL_WAX_ON, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/trail/wax_on"))));

        register(context, ModEnchantments.VOLT, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.VOLT_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.25F))));

        register(context, ModEnchantments.WAVE_WALKER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.DEPTH_STRIDER)))
                .withEffect(ModEnchantmentEffectComponents.WAVE_WALKER.get()));

        register(context, ModEnchantments.ANGLER, colored(
                Enchantment.definition(items.getOrThrow(ALL_FISHING), items.getOrThrow(ALL_FISHING), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ANGLER_DOUBLE_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.1F))));

        register(context, ModEnchantments.ANTI_CAMOUFLAGE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HEAD),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ANTI_CAMOUFLAGE_DURATION_BONUS.get(),
                        new AddValue(LevelBasedValue.perLevel(10.0F))));

        register(context, ModEnchantments.AURA_BURNING, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.FEET),
                0x55FFFF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/burning")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_GLOWING, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/glowing")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_HASTE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/haste")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_INFESTED, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/infested")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_JUMP_BOOST, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/jump_boost")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_POISON, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/poison")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_REGENERATION, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/regeneration")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_RESISTANCE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/resistance")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_SLOWNESS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/slowness")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_SPEED, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/speed")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_STRENGTH, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/strength")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_WEAKNESS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/weakness")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_WITHER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/aura/wither")))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.CONDUCTIVE_LINE, colored(
                Enchantment.definition(items.getOrThrow(ALL_FISHING), items.getOrThrow(ALL_FISHING), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.CONDUCTIVE_LINE.get()));

        register(context, ModEnchantments.CROP_DANCE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 1, 1,
                        Enchantment.dynamicCost(18, 8), Enchantment.dynamicCost(48, 18), 16, EquipmentSlotGroup.FEET),
                0xFF00BB)
                .withEffect(ModEnchantmentEffectComponents.CROP_DANCE.get())
                .withEffect(ModEnchantmentEffectComponents.CROP_DANCE_GROWTH_CHANCE.get(),
                        new SetValue(LevelBasedValue.perLevel(0.2F, 0.1F))));

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

        register(context, ModEnchantments.FLAME_WALKER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.DEPTH_STRIDER)))
                .withEffect(ModEnchantmentEffectComponents.FLAME_WALKER.get()));

        register(context, ModEnchantments.HARVEST_ECHO, colored(
                Enchantment.definition(items.getOrThrow(ALL_SHEAR), items.getOrThrow(ALL_SHEAR), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.HARVEST_ECHO.get()));

        register(context, ModEnchantments.INVISIBILITY_CLOAK, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.LEGS),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.INVISIBILITY_CLOAK.get()));

        register(context, ModEnchantments.SHEPHERD, colored(
                Enchantment.definition(items.getOrThrow(ALL_SHEAR), items.getOrThrow(ALL_SHEAR), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.SHEPHERD_EXTRA_CHANCE.get(), new AddValue(LevelBasedValue.perLevel(0.2F))));

        register(context, ModEnchantments.TACTICAL_KNEE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.LEGS),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.TACTICAL_KNEE.get()));

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

        register(context, ModEnchantments.CARRION_EATER, colored(
                Enchantment.definition(items.getOrThrow(WOLF_ARMOR), items.getOrThrow(WOLF_ARMOR), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.CARRION_EATER_HEAL.get(),
                        new SetValue(LevelBasedValue.perLevel(0.25F))));

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

        register(context, ModEnchantments.LIFE_STEAL, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.LIFE_STEAL_RATIO.get(),
                        new SetValue(LevelBasedValue.constant(0.1F))));

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

        register(context, ModEnchantments.GAMBLER, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_BOW), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withSpecialEffect(ModEnchantmentEffectComponents.GAMBLER_DATA.get(),
                        new GamblerData(0.5F, 1.4F, 0.8F)));

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
