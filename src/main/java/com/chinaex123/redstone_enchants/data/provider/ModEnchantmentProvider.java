package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.component.GamblerData;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomBeneficialMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomHarmfulMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RemoveRandomBeneficialEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MovementPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import org.joml.Vector3f;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.DamageEntity;
import net.minecraft.world.item.enchantment.effects.DamageImmunity;
import net.minecraft.world.item.enchantment.effects.DamageItem;
import net.minecraft.world.item.enchantment.effects.ExplodeEffect;
import net.minecraft.world.item.enchantment.effects.Ignite;
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect;
import net.minecraft.world.item.enchantment.effects.MultiplyValue;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import net.minecraft.world.item.enchantment.effects.ReplaceBlock;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.item.enchantment.effects.RunFunction;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.item.enchantment.effects.SpawnParticlesEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.EnchantmentActiveCheck;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.TimeCheck;
import net.minecraft.world.level.storage.loot.predicates.ValueCheckCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.phys.Vec3;

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
    private static final TagKey<Enchantment> DAMAGE_BOW_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/damage_bow"));
    private static final TagKey<Enchantment> BOW_SPREAD_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/bow_spread"));
    private static final TagKey<Enchantment> NO_SHOTGUN_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/no_shotgun"));
    private static final TagKey<Enchantment> DAMAGE_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/damage"));
    private static final TagKey<Enchantment> ARMORS_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/armors"));
    private static final TagKey<Enchantment> ARMORS_HEAD_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/armors_head"));
    private static final TagKey<Enchantment> HEAD_LUCKY_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/head_lucky"));
    private static final TagKey<Enchantment> WALKER_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/walker"));
    private static final TagKey<Enchantment> BANE_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/bane"));
    private static final TagKey<Enchantment> TOUCH_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/touch"));
    private static final TagKey<EntityType<?>> ENTITY_BOSS =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_boss"));
    private static final TagKey<EntityType<?>> ENTITY_END =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_end"));
    private static final TagKey<EntityType<?>> ENTITY_ILLAGER =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_illager"));
    private static final TagKey<EntityType<?>> ENTITY_NETHER =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_nether"));
    private static final TagKey<EntityType<?>> ENTITY_WATER =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_water"));
    private static final TagKey<EntityType<?>> BLACK_ENTITY =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("black_entity"));
    private static final TagKey<Block> GLASS_BLOCKS =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks"));

    /**
     * 跨注册表裸 id 的 Holder：holder owner 恒返回 canSerializeIn=true，
     * 使 datagen 序列化走 ResourceKey 分支直接输出 id 字符串，
     * 不要求该注册表条目在 datagen JVM 里真实存在（如 ars_nouveau:blasting、apothic_attributes:draw_speed）。
     */
    private static <T> Holder<T> foreignHolder(ResourceKey<T> key) {
        return Holder.Reference.createStandAlone(
                new HolderOwner<T>() {
                    @Override
                    public boolean canSerializeIn(HolderOwner<T> owner) {
                        return true;
                    }
                }, key);
    }

    /** {@link #foreignHolder} 的 HolderSet 版（ApplyMobEffect.toApply 等单元素集合）。 */
    private static <T> HolderSet<T> foreignId(ResourceKey<T> key) {
        return HolderSet.direct(foreignHolder(key));
    }

    /** 风爆箭（blast_arrows）：爆裂效果，wind_burst 音效（裸 id 引用）。 */
    private static ExplodeEffect windBurstExplosion(HolderGetter<DamageType> damageTypes) {
        return new ExplodeEffect(false, Optional.of(damageTypes.getOrThrow(DamageTypes.EXPLOSION)),
                Optional.of(LevelBasedValue.perLevel(0.5F, 0.25F)),
                Optional.of(BuiltInRegistries.BLOCK.getOrCreateTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)),
                new Vec3(0, 0.5, 0), LevelBasedValue.perLevel(1.5F, 0.75F), false,
                Level.ExplosionInteraction.NONE, ParticleTypes.GUST_EMITTER_SMALL,
                ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.WIND_CHARGE_BURST);
    }

    /** 轰炸箭（bomb_arrows）：灰烬爆炸，warden 音效固定范围 16（对象形状）。 */
    private static ExplodeEffect ashExplosion(HolderGetter<DamageType> damageTypes, float radiusBase, float radiusPerLevel,
                                              Optional<LevelBasedValue> knockbackMultiplier) {
        return new ExplodeEffect(false, Optional.of(damageTypes.getOrThrow(DamageTypes.ARROW)), knockbackMultiplier,
                Optional.empty(), new Vec3(0, 0.5, 0), LevelBasedValue.perLevel(radiusBase, radiusPerLevel), false,
                Level.ExplosionInteraction.NONE, ParticleTypes.ASH, ParticleTypes.EXPLOSION_EMITTER,
                Holder.direct(SoundEvent.createFixedRangeEvent(
                        ResourceLocation.withDefaultNamespace("entity.warden.sonic_boom"), 16.0F)));
    }

    private static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<DamageType> damageTypes = context.lookup(Registries.DAMAGE_TYPE);

        register(context, ModEnchantments.ACCURACY_SHOT, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/accuracy_shot/on_shoot"))));

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

        register(context, ModEnchantments.BANE_WATER, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE), items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F, 2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(ENTITY_WATER)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.constant(1.5F), LevelBasedValue.perLevel(1.5F, 0.5F),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(ENTITY_WATER)),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))))
                .exclusiveWith(enchantments.getOrThrow(BANE_EXCLUSIVE)));

        register(context, ModEnchantments.BERSERK, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 2, 5,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.CHEST),
                0xFFAA00)
                .withEffect(ModEnchantmentEffectComponents.BERSERK_DAMAGE_BONUS.get(),
                        new SetValue(LevelBasedValue.perLevel(0.03F))));

        register(context, ModEnchantments.BLAST_ARROWS, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        windBurstExplosion(damageTypes),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK, windBurstExplosion(damageTypes),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_BOW_EXCLUSIVE)));

        register(context, ModEnchantments.BOMB_ARROWS, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.DAMAGE, new AddValue(LevelBasedValue.constant(2.0F)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new AllOf.EntityEffects(List.of(ashExplosion(damageTypes, 1.0F, 0.5F, Optional.empty()))),
                        AllOfCondition.allOf(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS))))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        ashExplosion(damageTypes, 0.5F, 0.5F, Optional.of(LevelBasedValue.constant(0.0F))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_BOW_EXCLUSIVE)));

        register(context, ModEnchantments.BULLET_TIME, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.SLOW_FALLING),
                                LevelBasedValue.constant(1.3F), LevelBasedValue.constant(1.3F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F))));

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

        register(context, ModEnchantments.ICE_ARROWS, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/ice_arrows/ice_arrows_hit_block")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/ice_arrows/ice_arrows_hit_mob")),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.constant(0.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.DAMAGING_ENTITY,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

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

        register(context, ModEnchantments.DEVOURING, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/devouring")),
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

        register(context, ModEnchantments.ECHOES_BATTLE, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ECHOES_BATTLE.get()));

        register(context, ModEnchantments.SCATTER, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.PROJECTILE_SPREAD,
                        new AddValue(LevelBasedValue.perLevel(10.0F, 10.0F)))
                .exclusiveWith(enchantments.getOrThrow(BOW_SPREAD_EXCLUSIVE)));

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

        register(context, ModEnchantments.SHOTGUN, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 2, 6,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.PROJECTILE_COUNT,
                        new AddValue(LevelBasedValue.perLevel(0.0F, 1.0F)))
                .withEffect(EnchantmentEffectComponents.PROJECTILE_SPREAD,
                        new AddValue(LevelBasedValue.perLevel(0.0F, 1.0F)))
                .withSpecialEffect(EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME,
                        new AddValue(LevelBasedValue.perLevel(0.5F, 0.25F)))
                .withEffect(EnchantmentEffectComponents.ITEM_DAMAGE,
                        new MultiplyValue(new LevelBasedValue.Fraction(
                                LevelBasedValue.constant(1.0F), LevelBasedValue.perLevel(1.0F, 1.0F))),
                        LootItemRandomChanceCondition.randomChance(0.9F))
                .exclusiveWith(enchantments.getOrThrow(NO_SHOTGUN_EXCLUSIVE)));

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

        register(context, ModEnchantments.CURSE_OF_ACROPHOBIA, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HEAD),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(
                                HolderSet.direct(MobEffects.CONFUSION, MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.constant(11.0F), LevelBasedValue.constant(11.0F),
                                LevelBasedValue.constant(2.0F), LevelBasedValue.constant(2.0F)),
                        LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                .setY(MinMaxBounds.Doubles.atLeast(180.0)))));

        register(context, ModEnchantments.CURSE_OF_BASIPHOBIA, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HEAD),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(
                                HolderSet.direct(MobEffects.DIG_SLOWDOWN, MobEffects.DARKNESS),
                                LevelBasedValue.constant(11.0F), LevelBasedValue.constant(11.0F),
                                LevelBasedValue.constant(2.0F), LevelBasedValue.constant(2.0F)),
                        LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                .setY(MinMaxBounds.Doubles.atMost(-36.0)))));

        register(context, ModEnchantments.CURSE_OF_BLINDNESS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HEAD),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.BLINDNESS),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F))));

        register(context, ModEnchantments.CURSE_OF_GRAVITY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.FEET),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.curse_of_gravity_1"),
                        Attributes.GRAVITY,
                        LevelBasedValue.perLevel(0.02F, 0.06F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.CURSE_OF_HUNGER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 4, 4,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HEAD),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.HUNGER),
                                LevelBasedValue.constant(0.2F), LevelBasedValue.constant(0.2F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F))));

        register(context, ModEnchantments.CURSE_OF_UNLUCKY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 5, 4,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HEAD),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.UNLUCK),
                                LevelBasedValue.constant(0.2F), LevelBasedValue.constant(0.2F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F))));

        register(context, ModEnchantments.CURSE_OF_BLAST, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.ARMOR),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new ExplodeEffect(true,
                                        Optional.of(damageTypes.getOrThrow(DamageTypes.EXPLOSION)),
                                        Optional.of(LevelBasedValue.constant(1.0F)), Optional.empty(),
                                        new Vec3(0, 1, 0), LevelBasedValue.constant(3.0F), true,
                                        Level.ExplosionInteraction.TNT,
                                        ParticleTypes.EXPLOSION_EMITTER, ParticleTypes.EXPLOSION_EMITTER,
                                        SoundEvents.GENERIC_EXPLODE),
                                new DamageEntity(LevelBasedValue.constant(4.0F), LevelBasedValue.constant(4.0F),
                                        damageTypes.getOrThrow(DamageTypes.EXPLOSION)),
                                new Ignite(LevelBasedValue.constant(5.0F)))),
                        LootItemRandomChanceCondition.randomChance(0.3F)));

        register(context, ModEnchantments.CURSE_OF_STILLNESS, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.ARMOR),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new DamageEntity(LevelBasedValue.constant(0.0F), LevelBasedValue.constant(0.5F),
                                damageTypes.getOrThrow(DamageTypes.DRY_OUT)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().moving(new MovementPredicate(
                                        MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY,
                                        MinMaxBounds.Doubles.exactly(0.0), MinMaxBounds.Doubles.exactly(0.0),
                                        MinMaxBounds.Doubles.exactly(0.0), MinMaxBounds.Doubles.exactly(0.0))))));

        register(context, ModEnchantments.CURSE_OF_VULNERABILITY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.curse_of_vulnerability_damage"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(-0.2F, 0.0F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.curse_of_vulnerability_speed"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.1F, 0.0F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

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

        register(context, ModEnchantments.RAIN_DRIPSTONE, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/rain/rain_dripstone")))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/rain/rain_dripstone")))),
                        AllOfCondition.allOf(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS))))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_BOW_EXCLUSIVE)));

        register(context, ModEnchantments.RAIN_FORGE, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/rain/rain_forge")))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RunFunction(RedstoneEnchants.asResource("enchantment/rain/rain_forge")))),
                        AllOfCondition.allOf(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS))))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_BOW_EXCLUSIVE)));

        register(context, ModEnchantments.RAPID, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("rapid"),
                        foreignHolder(ResourceKey.create(Registries.ATTRIBUTE,
                                ResourceLocation.fromNamespaceAndPath("apothic_attributes", "draw_speed"))),
                        LevelBasedValue.perLevel(0.3F, 0.15F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

        register(context, ModEnchantments.RICOCHET, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.DAMAGING_ENTITY,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/ricochet")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.PROJECTILE_PIERCING,
                        new AddValue(LevelBasedValue.perLevel(1.0F, 1.0F))));

        register(context, ModEnchantments.RESILIENCE_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 2, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.MAINHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.resilience_shield_1"),
                        Attributes.KNOCKBACK_RESISTANCE,
                        LevelBasedValue.perLevel(0.2F, 0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

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

        register(context, ModEnchantments.SNOWBALL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 6, 3,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.ARMOR),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/snowball")),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.3F, 0.3F)))));

        register(context, ModEnchantments.SPIRIT, colored(
                Enchantment.definition(items.getOrThrow(HORSE_ARMOR), items.getOrThrow(HORSE_ARMOR), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.SPIRIT_SPEED_BONUS.get(),
                        new SetValue(LevelBasedValue.perLevel(0.25F))));

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

        register(context, ModEnchantments.TRACKER, colored(
                Enchantment.definition(items.getOrThrow(WOLF_ARMOR), items.getOrThrow(WOLF_ARMOR), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.BODY),
                0x55FFFF)
                .withEffect(ModEnchantmentEffectComponents.TRACKER_GLOW_DURATION_BONUS.get(),
                        new AddValue(LevelBasedValue.perLevel(20.0F))));

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
                        new RunFunction(RedstoneEnchants.asResource("enchantment/unleash_potential")))
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_EXCLUSIVE)));

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

        register(context, ModEnchantments.WATER_BOTTLE_PROJECTION, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 6, 2,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.MAINHAND),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource(
                                "enchantment/bottle_projection/water_bottle_projection/water_1")),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)),
                                () -> new ValueCheckCondition(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(1.0F, 1.0F)),
                                        IntRange.exact(1))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource(
                                "enchantment/bottle_projection/water_bottle_projection/water_2")),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)),
                                () -> new ValueCheckCondition(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(1.0F, 1.0F)),
                                        IntRange.exact(2)))));

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

        register(context, ModEnchantments.CHAINS, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/chains/chains_initial")),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)),
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.2F, 0.2F))),
                                InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects()
                                                .and(MobEffects.MOVEMENT_SLOWDOWN).and(MobEffects.WEAKNESS)))))));

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

        // fatal_arrow 三条的公共条件：攻击者是箭 + 命中实体不是黑名单实体（exclusive_set/damage_bow）
        LootItemCondition.Builder fatalCondition = AllOfCondition.allOf(
                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                        EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)),
                InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(BLACK_ENTITY))));

        register(context, ModEnchantments.FATAL_ARROW, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 1, 1,
                        Enchantment.dynamicCost(80, 50), Enchantment.dynamicCost(100, 50), 200,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/fatal_arrow")), fatalCondition)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.DAMAGING_ENTITY,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")), fatalCondition)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new AllOf.EntityEffects(List.of(
                                new ApplyMobEffect(HolderSet.direct(MobEffects.BAD_OMEN),
                                        LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F),
                                        LevelBasedValue.constant(2.0F), LevelBasedValue.constant(2.0F)),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.WEAKNESS),
                                        LevelBasedValue.constant(120.0F), LevelBasedValue.constant(120.0F),
                                        LevelBasedValue.constant(2.0F), LevelBasedValue.constant(2.0F)),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.UNLUCK),
                                        LevelBasedValue.constant(600.0F), LevelBasedValue.constant(600.0F),
                                        LevelBasedValue.constant(9.0F), LevelBasedValue.constant(9.0F)),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.BLINDNESS),
                                        LevelBasedValue.constant(60.0F), LevelBasedValue.constant(60.0F),
                                        LevelBasedValue.constant(1.0F), LevelBasedValue.constant(1.0F)))),
                        fatalCondition)
                .exclusiveWith(enchantments.getOrThrow(DAMAGE_BOW_EXCLUSIVE)));

        register(context, ModEnchantments.FIRE_ARROWS, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 2, 1,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/fire_arrows")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/fire_arrows")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.DAMAGING_ENTITY,
                        new RunFunction(RedstoneEnchants.asResource("libs/kill_arrow")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.FOCUS, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.PROJECTILE_SPREAD,
                        new AddValue(LevelBasedValue.perLevel(-5.0F, -5.0F)))
                .exclusiveWith(enchantments.getOrThrow(BOW_SPREAD_EXCLUSIVE)));

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

        register(context, ModEnchantments.EXOSKELETON, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.exoskeleton_1"),
                        Attributes.ARMOR_TOUGHNESS,
                        LevelBasedValue.perLevel(4.0F, 2.0F),
                        AttributeModifier.Operation.ADD_VALUE)));

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

        register(context, ModEnchantments.CHAIN_REACTION, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/chain_reaction/chain_reaction_spectral")),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.4F, 0.2F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityType.SPECTRAL_ARROW))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/chain_reaction/chain_reaction")),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.4F, 0.2F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityType.ARROW)))));

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

        register(context, ModEnchantments.GLASS_BREAKER, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new ReplaceBlock(Vec3i.ZERO,
                                Optional.of(BlockPredicate.matchesTag(Vec3i.ZERO, GLASS_BLOCKS)),
                                BlockStateProvider.simple(Blocks.AIR), Optional.empty()),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new SpawnParticlesEffect(ParticleTypes.ELECTRIC_SPARK,
                                SpawnParticlesEffect.inBoundingBox(), SpawnParticlesEffect.inBoundingBox(),
                                SpawnParticlesEffect.fixedVelocity(ConstantFloat.of(0.0F)),
                                SpawnParticlesEffect.fixedVelocity(ConstantFloat.of(0.0F)),
                                ConstantFloat.of(0.3F))));

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

        register(context, ModEnchantments.LUCKY_BOOST, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HEAD),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantencore.lucky_boost_1"),
                        Attributes.LUCK,
                        LevelBasedValue.perLevel(2.0F, 2.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .exclusiveWith(enchantments.getOrThrow(HEAD_LUCKY_EXCLUSIVE)));

        register(context, ModEnchantments.LUCKY_LIGHT, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HEAD),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.LUCK),
                                LevelBasedValue.constant(0.2F), LevelBasedValue.constant(0.2F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F)))
                .exclusiveWith(enchantments.getOrThrow(HEAD_LUCKY_EXCLUSIVE)));

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

        register(context, ModEnchantments.MAXIMIZATION, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 1, 4,
                        Enchantment.dynamicCost(18, 8), Enchantment.dynamicCost(48, 18), 16, EquipmentSlotGroup.HEAD),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.maximization_1"),
                        Attributes.SCALE,
                        new LevelBasedValue.Lookup(List.of(0.15F, 0.25F, 0.35F, 0.5F),
                                LevelBasedValue.perLevel(0.25F, 0.25F)),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.maximization_2"),
                        Attributes.STEP_HEIGHT,
                        LevelBasedValue.perLevel(0.25F, 0.25F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.maximization_3"),
                        Attributes.ATTACK_DAMAGE,
                        LevelBasedValue.perLevel(1.0F, 0.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.maximization_4"),
                        Attributes.MAX_HEALTH,
                        LevelBasedValue.perLevel(2.5F, 2.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.maximization_5"),
                        Attributes.ENTITY_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(0.15F, 0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.maximization_6"),
                        Attributes.BLOCK_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(0.15F, 0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.maximization_7"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(-0.015F, -0.01F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.maximization_8"),
                        Attributes.JUMP_STRENGTH,
                        LevelBasedValue.perLevel(-0.04F, -0.02F),
                        AttributeModifier.Operation.ADD_VALUE))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_HEAD_EXCLUSIVE)));

        register(context, ModEnchantments.MINIFY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 1, 4,
                        Enchantment.dynamicCost(18, 8), Enchantment.dynamicCost(48, 18), 16, EquipmentSlotGroup.HEAD),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.minify_1"),
                        Attributes.SCALE,
                        new LevelBasedValue.Lookup(List.of(-0.15F, -0.25F, -0.35F, -0.5F),
                                LevelBasedValue.perLevel(-0.15F, -0.15F)),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.minify_2"),
                        Attributes.STEP_HEIGHT,
                        LevelBasedValue.perLevel(-0.25F, -0.25F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.minify_3"),
                        Attributes.ATTACK_DAMAGE,
                        LevelBasedValue.perLevel(-1.0F, -0.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.minify_4"),
                        Attributes.MAX_HEALTH,
                        LevelBasedValue.perLevel(-2.5F, -2.5F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.minify_5"),
                        Attributes.ENTITY_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(-0.15F, -0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.minify_6"),
                        Attributes.BLOCK_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(-0.15F, -0.1F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.minify_7"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.015F, 0.01F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.minify_8"),
                        Attributes.JUMP_STRENGTH,
                        LevelBasedValue.perLevel(0.04F, 0.02F),
                        AttributeModifier.Operation.ADD_VALUE))
                .exclusiveWith(enchantments.getOrThrow(ARMORS_HEAD_EXCLUSIVE)));

        register(context, ModEnchantments.MOIST, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.HOES), 3, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.MOIST.get()));

        register(context, ModEnchantments.TIMBER, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.AXES), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF));

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

        register(context, ModEnchantments.XP_REAPER_MOBS, colored(
                Enchantment.definition(items.getOrThrow(SWORDS_AND_AXES), items.getOrThrow(SWORDS), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF)
                .withEffect(EnchantmentEffectComponents.MOB_EXPERIENCE,
                        new MultiplyValue(LevelBasedValue.perLevel(1.5F, 1.0F))));
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
