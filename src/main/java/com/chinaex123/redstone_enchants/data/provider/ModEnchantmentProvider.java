package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.component.GamblerData;
import com.chinaex123.redstone_enchants.enchantment.effect.AddExperienceEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AddTagEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AirTossEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AreaIgniteEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AreaMobEffectEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ChainArrowsEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.GiveItemEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ChainBindEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ClearMainHandEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.IgniteAreaEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ParticleBurstEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RainBlocksEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RicochetEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.SnowballBurstEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.DevouringEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.DropHeldItemEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.FreezeWaterEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.HoveringArrowEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.IceArrowSlownessEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.KillSelfEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomBeneficialMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomHarmfulMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RemoveRandomBeneficialEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.SplashCloudEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ThrowWaterBottleEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.TrailParticleEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.UnleashPotentialEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.WeatherThunderEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.nbt.TagParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.core.particles.DustColorTransitionOptions;
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
import net.minecraft.world.item.Items;
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
import net.minecraft.world.item.enchantment.effects.SummonEntityEffect;
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
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.TimeCheck;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;
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
    private static final TagKey<Item> TRIDENT_AND_BOW = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("trident_and_bow"));
    private static final TagKey<Item> ELYTRA = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("elytra"));
    private static final TagKey<Item> HORSE_ANIMAL_ARMOR = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("horse_animal_armor"));
    private static final TagKey<Item> ALL_TOOLS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_tools"));
    private static final TagKey<Item> C_ENCHANTABLES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "enchantables"));
    private static final TagKey<Item> WEAPON_ITEMS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("weapon"));
    private static final TagKey<Item> LAST_HOPE_WEAPONS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("last_hope_weapons"));
    private static final TagKey<Item> CHEST_ARMOR_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("chest_armor"));
    private static final TagKey<Item> PICKAXES_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("pickaxes"));
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
    private static final TagKey<Enchantment> BEDROCK_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/bedrock"));
    private static final TagKey<Enchantment> SWORDS_AND_BOW_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/swords_and_bow"));
    private static final TagKey<Enchantment> DAMAGE_BANE_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/damage_bane"));
    private static final TagKey<Enchantment> SHIELD_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/shield"));
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
    private static final TagKey<EntityType<?>> PROJECTILES =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("projectiles"));
    private static final TagKey<EntityType<?>> EXORCISM_ENTITIES =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("exorcism"));
    private static final TagKey<Block> BEDROCK_BREAKER_BLOCKS =
            TagKey.create(Registries.BLOCK, RedstoneEnchants.asResource("bedrock_breaker"));
    private static final TagKey<Block> GLASS_BLOCKS =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks"));

    /**
     * 跨注册表裸 id 的 Holder：holder owner 恒返回 canSerializeIn=true，
     * 使 datagen 序列化走 ResourceKey 分支直接输出 id 字符串，
     * 不要求该注册表条目在 datagen JVM 里真实存在（如 ars_nouveau:blasting、apothic_attributes:draw_speed）。
     */
    /** 解析手写 SNBT 为 NbtPredicate（parseTag 抛受检异常，集中处理）。 */
    private static NbtPredicate nbtPredicate(String snbt) {
        try {
            return new NbtPredicate(TagParser.parseTag(snbt));
        } catch (CommandSyntaxException e) {
            throw new IllegalStateException("bad snbt: " + snbt, e);
        }
    }

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
                        new HoveringArrowEffect()));

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

        register(context, ModEnchantments.ABSORBENT_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 2,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.ABSORPTION),
                                LevelBasedValue.constant(11.0F), LevelBasedValue.constant(11.0F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().periodicTick(200)))
                .exclusiveWith(enchantments.getOrThrow(SHIELD_EXCLUSIVE)));

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
                        new KillSelfEffect())
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
                        new KillSelfEffect())
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

        register(context, ModEnchantments.SAFE_FALL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.LEGS),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.safe_fall_1"),
                        Attributes.SAFE_FALL_DISTANCE,
                        LevelBasedValue.perLevel(2.0F, 1.0F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.SAFE_LANDING, colored(
                Enchantment.definition(items.getOrThrow(ELYTRA), items.getOrThrow(ELYTRA), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.CHEST),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantencore.safe_landing_1"),
                        Attributes.SAFE_FALL_DISTANCE,
                        LevelBasedValue.perLevel(2.0F, 2.5F),
                        AttributeModifier.Operation.ADD_VALUE)));

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
                        new FreezeWaterEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new KillSelfEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new FreezeWaterEffect(), new IceArrowSlownessEffect(),
                                new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                        LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                        LevelBasedValue.constant(0.0F), LevelBasedValue.constant(0.0F)))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.DAMAGING_ENTITY,
                        new KillSelfEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

        register(context, ModEnchantments.INDESTRUCTIBLE, colored(
                Enchantment.definition(items.getOrThrow(ENCHANTABLES), items.getOrThrow(ENCHANTABLES), 1, 1,
                        Enchantment.dynamicCost(0, 0), Enchantment.dynamicCost(0, 0), 50, EquipmentSlotGroup.ANY),
                0xFF00BB)
                .exclusiveWith(enchantments.getOrThrow(INDESTRUCTIBLE_EXCLUSIVE))
                .withEffect(ModEnchantmentEffectComponents.INDESTRUCTIBLE.get()));

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

        register(context, ModEnchantments.STABLE_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 2, 2,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.OFFHAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.stalwart_1"),
                        Attributes.KNOCKBACK_RESISTANCE,
                        LevelBasedValue.constant(1.0F),
                        AttributeModifier.Operation.ADD_VALUE))
                .exclusiveWith(enchantments.getOrThrow(SHIELD_EXCLUSIVE)));

        register(context, ModEnchantments.STRENGTH_SHIELD, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.DAMAGE_BOOST),
                                LevelBasedValue.perLevel(2.0F, 1.0F), LevelBasedValue.perLevel(4.0F, 3.0F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.1F, 0.2F))))
                .exclusiveWith(enchantments.getOrThrow(SHIELD_EXCLUSIVE)));

        register(context, ModEnchantments.STRIDING, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.LEGS),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.striding_1"),
                        Attributes.STEP_HEIGHT,
                        LevelBasedValue.perLevel(0.5F, 0.25F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.striding_2"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.0F, 0.05F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.122F, 0.122F, 0.137F), new Vector3f(0.122F, 0.122F, 0.137F), 1.0F), MobEffects.BLINDNESS, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.122F, 0.122F, 0.137F), new Vector3f(0.122F, 0.122F, 0.137F), 1.0F), MobEffects.BLINDNESS, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(ParticleTypes.SMALL_FLAME, foreignHolder(ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath("ars_nouveau", "blasting"))), 50, 0, false, 2.0F, 40),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new KillSelfEffect())
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(ParticleTypes.SMALL_FLAME, foreignHolder(ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath("ars_nouveau", "blasting"))), 50, 0, false, 2.0F, 40),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(1.000F, 0.933F, 0.020F), new Vector3f(1.000F, 0.933F, 0.020F), 1.0F), MobEffects.GLOWING, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(1.000F, 0.933F, 0.020F), new Vector3f(1.000F, 0.933F, 0.020F), 1.0F), MobEffects.GLOWING, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.345F, 0.463F, 0.325F), new Vector3f(0.345F, 0.463F, 0.325F), 1.0F), MobEffects.HUNGER, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.345F, 0.463F, 0.325F), new Vector3f(0.345F, 0.463F, 0.325F), 1.0F), MobEffects.HUNGER, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.549F, 0.608F, 0.549F), new Vector3f(0.549F, 0.608F, 0.549F), 1.0F), MobEffects.INFESTED, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.549F, 0.608F, 0.549F), new Vector3f(0.549F, 0.608F, 0.549F), 1.0F), MobEffects.INFESTED, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.6F, 1.0F, 0.639F), new Vector3f(0.6F, 1.0F, 0.639F), 1.0F), MobEffects.OOZING, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.6F, 1.0F, 0.639F), new Vector3f(0.6F, 1.0F, 0.639F), 1.0F), MobEffects.OOZING, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.529F, 0.639F, 0.388F), new Vector3f(0.529F, 0.639F, 0.388F), 1.0F), MobEffects.POISON, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.529F, 0.639F, 0.388F), new Vector3f(0.529F, 0.639F, 0.388F), 1.0F), MobEffects.POISON, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(1.0F, 0.239F, 0.239F), new Vector3f(1.0F, 0.239F, 0.239F), 1.0F), MobEffects.REGENERATION, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(1.0F, 0.239F, 0.239F), new Vector3f(1.0F, 0.239F, 0.239F), 1.0F), MobEffects.REGENERATION, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.953F, 0.812F, 0.725F), new Vector3f(0.953F, 0.812F, 0.725F), 1.0F), MobEffects.SLOW_FALLING, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.953F, 0.812F, 0.725F), new Vector3f(0.953F, 0.812F, 0.725F), 1.0F), MobEffects.SLOW_FALLING, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.545F, 0.686F, 0.878F), new Vector3f(0.545F, 0.686F, 0.878F), 1.0F), MobEffects.MOVEMENT_SLOWDOWN, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.545F, 0.686F, 0.878F), new Vector3f(0.545F, 0.686F, 0.878F), 1.0F), MobEffects.MOVEMENT_SLOWDOWN, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.200F, 0.922F, 1.000F), new Vector3f(0.200F, 0.922F, 1.000F), 1.0F), MobEffects.MOVEMENT_SPEED, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.200F, 0.922F, 1.000F), new Vector3f(0.200F, 0.922F, 1.000F), 1.0F), MobEffects.MOVEMENT_SPEED, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.471F, 0.412F, 0.353F), new Vector3f(0.471F, 0.412F, 0.353F), 1.0F), MobEffects.WEAVING, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.471F, 0.412F, 0.353F), new Vector3f(0.471F, 0.412F, 0.353F), 1.0F), MobEffects.WEAVING, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.741F, 0.788F, 1.0F), new Vector3f(0.741F, 0.788F, 1.0F), 1.0F), MobEffects.WIND_CHARGED, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.741F, 0.788F, 1.0F), new Vector3f(0.741F, 0.788F, 1.0F), 1.0F), MobEffects.WIND_CHARGED, 100, 0, true, 2.0F, 100),
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
                        new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.486F, 0.490F, 0.475F), new Vector3f(0.486F, 0.490F, 0.475F), 1.0F), MobEffects.WITHER, 100, 0, true, 2.0F, 100),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new SplashCloudEffect(new DustColorTransitionOptions(new Vector3f(0.486F, 0.490F, 0.475F), new Vector3f(0.486F, 0.490F, 0.475F), 1.0F), MobEffects.WITHER, 100, 0, true, 2.0F, 100),
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

        register(context, ModEnchantments.CURSE_OF_BREAKING, colored(
                Enchantment.definition(items.getOrThrow(C_ENCHANTABLES), items.getOrThrow(C_ENCHANTABLES), 4, 4,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.ANY),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.ITEM_DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(1.0F, 1.0F)),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.15F, 0.15F)))));

        register(context, ModEnchantments.CURSE_OF_CLUMSINESS, colored(
                Enchantment.definition(items.getOrThrow(C_ENCHANTABLES), items.getOrThrow(C_ENCHANTABLES), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.ANY),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new DropHeldItemEffect(),
                        LootItemRandomChanceCondition.randomChance(0.4F))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new DropHeldItemEffect(),
                        LootItemRandomChanceCondition.randomChance(0.4F)));

        register(context, ModEnchantments.CURSE_OF_DEATH, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), items.getOrThrow(TOOLS), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HAND),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new KillSelfEffect(),
                        LootItemRandomChanceCondition.randomChance(0.05F)));

        register(context, ModEnchantments.CURSE_OF_DOUBLE_EDGE, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), items.getOrThrow(TOOLS), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HAND),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER,
                        new DamageEntity(LevelBasedValue.constant(2.0F), LevelBasedValue.constant(4.0F),
                                damageTypes.getOrThrow(DamageTypes.MAGIC)),
                        LootItemRandomChanceCondition.randomChance(0.4F)));

        register(context, ModEnchantments.CURSE_OF_GRAVITY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.FEET),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.curse_of_gravity_1"),
                        Attributes.GRAVITY,
                        LevelBasedValue.perLevel(0.02F, 0.06F),
                        AttributeModifier.Operation.ADD_VALUE)));

        register(context, ModEnchantments.CURSE_OF_HIDING, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), items.getOrThrow(TOOLS), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HAND),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.INVISIBILITY),
                                LevelBasedValue.perLevel(2.0F, 3.0F), LevelBasedValue.perLevel(5.0F, 5.0F),
                                LevelBasedValue.perLevel(1.0F, 1.0F), LevelBasedValue.perLevel(1.0F, 1.0F)),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType())));

        register(context, ModEnchantments.CURSE_OF_HUNGER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 4, 4,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HEAD),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.HUNGER),
                                LevelBasedValue.constant(0.2F), LevelBasedValue.constant(0.2F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F))));

        register(context, ModEnchantments.CURSE_OF_REACH, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), items.getOrThrow(TOOLS), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.HAND),
                0xFF5555)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.curse_of_reach_1"),
                        Attributes.BLOCK_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(-1.0F, -1.0F),
                        AttributeModifier.Operation.ADD_VALUE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.curse_of_reach_2"),
                        Attributes.ENTITY_INTERACTION_RANGE,
                        LevelBasedValue.perLevel(-1.0F, -1.0F),
                        AttributeModifier.Operation.ADD_VALUE)));

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
                                new RainBlocksEffect(Blocks.POINTED_DRIPSTONE, 2.3F))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new KillSelfEffect())
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RainBlocksEffect(Blocks.POINTED_DRIPSTONE, 2.3F))),
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
                                new RainBlocksEffect(Blocks.ANVIL, 1.6F))),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new KillSelfEffect())
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new RainBlocksEffect(Blocks.ANVIL, 1.6F))),
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
                        new RicochetEffect(),
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

        register(context, ModEnchantments.RESILIENCE_SENTINEL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 2, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.LEGS),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(HolderSet.direct(MobEffects.DAMAGE_RESISTANCE),
                                LevelBasedValue.constant(6.0F), LevelBasedValue.constant(12.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.constant(2.0F)))),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.1F, 0.2F))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.VICTIM, EnchantmentTarget.ATTACKER,
                        new AllOf.EntityEffects(List.of(new ApplyMobEffect(HolderSet.direct(MobEffects.WEAKNESS),
                                LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.constant(0.0F)))),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.1F, 0.05F)))));

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
                        new SnowballBurstEffect(),
                        LootItemRandomChanceCondition.randomChance(
                                new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.3F, 0.3F)))));

        register(context, ModEnchantments.SPIRIT, colored(
                Enchantment.definition(items.getOrThrow(HORSE_ARMOR), items.getOrThrow(HORSE_ARMOR), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.BODY),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.SPIRIT_SPEED_BONUS.get(),
                        new SetValue(LevelBasedValue.perLevel(0.25F))));

        register(context, ModEnchantments.THROWING_ENHANCEMENT, colored(
                Enchantment.definition(
                        items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/trident"))), items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/trident"))), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(3.0F, 1.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityType.TRIDENT))));

        register(context, ModEnchantments.THUNDERING, colored(
                Enchantment.definition(
                        items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/trident"))), items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/trident"))), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new AllOf.EntityEffects(List.of(
                                new SummonEntityEffect(HolderSet.direct(EntityType.LIGHTNING_BOLT.builtInRegistryHolder()), false),
                                new PlaySoundEffect(SoundEvents.TRIDENT_THUNDER,
                                        ConstantFloat.of(5.0F), ConstantFloat.of(1.0F)),
                                new WeatherThunderEffect())),
                        AllOfCondition.allOf(
                                WeatherCheck.weather().setRaining(true),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(EntityType.TRIDENT)),
                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setCanSeeSky(true)),
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.LIGHTNING_ROD))));
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

        register(context, ModEnchantments.UNDERWATER_BLASTING, colored(
                Enchantment.definition(
                        items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/trident"))), items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/trident"))), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.ANY),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new SummonEntityEffect(HolderSet.direct(EntityType.TNT.builtInRegistryHolder()), false),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(0.5F),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().located(
                                                LocationPredicate.Builder.location().setBlock(
                                                        net.minecraft.advancements.critereon.BlockPredicate.Builder.block()
                                                                .of(Blocks.WATER))))))
                .exclusiveWith(enchantments.getOrThrow(NO_SEA_BREEZE_EXCLUSIVE)));

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

        register(context, ModEnchantments.TRAIL_CHERRY_LEAVES, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.CHERRY_LEAVES)));

        register(context, ModEnchantments.TRAIL_DRAGON_BREATH, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.DRAGON_BREATH)));

        register(context, ModEnchantments.TRAIL_FIREWORK, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.FIREWORK)));

        register(context, ModEnchantments.TRAIL_GLOW, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.GLOW)));

        register(context, ModEnchantments.TRAIL_SCULK_SOUL, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.SCULK_SOUL)));

        register(context, ModEnchantments.TRAIL_SNOWFLAKE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.SNOWFLAKE)));

        register(context, ModEnchantments.TRAIL_TRIAL_OMEN, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.TRIAL_OMEN)));

        register(context, ModEnchantments.TRAIL_WAX_OFF, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.WAX_OFF)));

        register(context, ModEnchantments.TRAIL_WAX_ON, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 6, 1,
                        Enchantment.dynamicCost(8, 4), Enchantment.dynamicCost(16, 8), 4, EquipmentSlotGroup.HEAD),
                0xFFFF55)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new TrailParticleEffect(ParticleTypes.WAX_ON)));

        register(context, ModEnchantments.VITALITY, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_CHEST), items.getOrThrow(ARMORS_CHEST), 2, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.CHEST),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.vitality_1"),
                        Attributes.MAX_HEALTH,
                        LevelBasedValue.perLevel(0.25F, 0.25F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

        register(context, ModEnchantments.VOLT, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.VOLT_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.25F))));

        register(context, ModEnchantments.XP_SPRING_BLOCK, colored(
                Enchantment.definition(items.getOrThrow(TOOLS), items.getOrThrow(TOOLS), 4, 3,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF)
                .withEffect(EnchantmentEffectComponents.BLOCK_EXPERIENCE,
                        new MultiplyValue(LevelBasedValue.perLevel(1.5F, 1.0F))));

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
                        new AreaMobEffectEffect(4.0F, MobEffects.INFESTED, 60, 0, AreaMobEffectEffect.Target.OTHERS))
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
                        new AreaMobEffectEffect(4.0F, MobEffects.POISON, 60, 0, AreaMobEffectEffect.Target.OTHERS))
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
                        new AreaMobEffectEffect(4.0F, MobEffects.MOVEMENT_SLOWDOWN, 60, 0, AreaMobEffectEffect.Target.OTHERS))
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
                        new AreaMobEffectEffect(4.0F, MobEffects.WEAKNESS, 60, 0, AreaMobEffectEffect.Target.OTHERS))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

        register(context, ModEnchantments.AURA_WITHER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_FOOT), items.getOrThrow(ARMORS_FOOT), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.FEET),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new AreaMobEffectEffect(4.0F, MobEffects.WITHER, 60, 0, AreaMobEffectEffect.Target.OTHERS))
                .exclusiveWith(enchantments.getOrThrow(AURA_EXCLUSIVE)));

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

        register(context, ModEnchantments.TELEPORT, colored(
                Enchantment.definition(items.getOrThrow(TRIDENT_AND_BOW), items.getOrThrow(TRIDENT_AND_BOW), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ANY),
                0xFF55FF));

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

        register(context, ModEnchantments.FAST_SWIM, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.LEGS),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.DOLPHINS_GRACE),
                                LevelBasedValue.constant(4.0F), LevelBasedValue.constant(8.0F),
                                LevelBasedValue.constant(0.0F), LevelBasedValue.constant(0.0F)),
                        AllOfCondition.allOf(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setSwimming(true))
                                        .periodicTick(80)))));

        register(context, ModEnchantments.FATAL_ARROW, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 1, 1,
                        Enchantment.dynamicCost(80, 50), Enchantment.dynamicCost(100, 50), 200,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF00BB)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new AllOf.EntityEffects(List.of(
                                new ParticleBurstEffect(ParticleTypes.SONIC_BOOM, 50, 1.0F, 1.0F, 1.0F, 0.5F, 0.0F),
                                new KillSelfEffect())), fatalCondition)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.DAMAGING_ENTITY,
                        new KillSelfEffect(), fatalCondition)
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
                        new IgniteAreaEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new KillSelfEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new IgniteAreaEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.DAMAGING_ENTITY,
                        new KillSelfEffect(),
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

        register(context, ModEnchantments.FORTRESS_STANCE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_LEG), items.getOrThrow(ARMORS_LEG), 2, 4,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.LEGS),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.TICK,
                        new ApplyMobEffect(
                                HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DAMAGE_RESISTANCE),
                                LevelBasedValue.constant(0.8F), LevelBasedValue.constant(0.8F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(0.0F, 1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags()
                                        .setOnGround(true).setCrouching(true)))));

        register(context, ModEnchantments.FORTITUDE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS), items.getOrThrow(ARMORS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ARMOR),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.fortitude_1"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(0.14F, 0.12F),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

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

        register(context, ModEnchantments.SHIELD_ARMOR, colored(
                Enchantment.definition(items.getOrThrow(SHIELD_ITEMS), items.getOrThrow(SHIELD_ITEMS), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.hardened_1"),
                        Attributes.ARMOR,
                        LevelBasedValue.perLevel(2.5F, 1.5F),
                        AttributeModifier.Operation.ADD_VALUE)));

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

        register(context, ModEnchantments.ETERNAL_FROST, colored(
                Enchantment.definition(items.getOrThrow(TRIDENT_AND_BOW), items.getOrThrow(TRIDENT_AND_BOW), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.HAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/eternal_frost")))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                LevelBasedValue.perLevel(1.0F, 1.0F), LevelBasedValue.perLevel(3.0F, 1.0F),
                                LevelBasedValue.perLevel(0.0F, 1.0F), LevelBasedValue.perLevel(1.0F, 1.0F)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new ReplaceDisk(
                                LevelBasedValue.perLevel(3.0F, 2.0F),
                                LevelBasedValue.constant(1.0F), Vec3i.ZERO,
                                Optional.of(BlockPredicate.allOf(
                                        BlockPredicate.matchesBlocks(new Vec3i(0, 1, 0), Blocks.AIR),
                                        BlockPredicate.matchesBlocks(Blocks.WATER))),
                                BlockStateProvider.simple(Blocks.FROSTED_ICE),
                                Optional.empty()),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(PROJECTILES)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new RunFunction(RedstoneEnchants.asResource("enchantment/eternal_frost")),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(PROJECTILES)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new KillSelfEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(PROJECTILES)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

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
                        new SetValue(LevelBasedValue.constant(0.1F))));

        register(context, ModEnchantments.CHAIN_REACTION, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8,
                        EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ChainArrowsEffect(true),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(
                                        new EnchantmentLevelProvider(LevelBasedValue.perLevel(0.4F, 0.2F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityType.SPECTRAL_ARROW))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        new ChainArrowsEffect(false),
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

        register(context, ModEnchantments.LIGHTWEIGHT, colored(
                Enchantment.definition(items.getOrThrow(ALL_TOOLS), items.getOrThrow(ALL_TOOLS), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("lightweight"),
                        Attributes.MOVEMENT_SPEED,
                        LevelBasedValue.perLevel(0.05F, 0.05F),
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

        register(context, ModEnchantments.MOONWALK, colored(
                Enchantment.definition(items.getOrThrow(ELYTRA), items.getOrThrow(ELYTRA), 3, 3,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.CHEST),
                0xFF55FF)
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        RedstoneEnchants.asResource("enchantment.moonwalk_1"),
                        Attributes.GRAVITY,
                        LevelBasedValue.perLevel(-0.03F, -0.015F),
                        AttributeModifier.Operation.ADD_VALUE)));

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

        register(context, ModEnchantments.MOIST, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.HOES), 3, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.MOIST.get()));

        register(context, ModEnchantments.TIMBER, colored(
                Enchantment.definition(items.getOrThrow(ItemTags.AXES), 4, 1,
                        Enchantment.dynamicCost(10, 6), Enchantment.dynamicCost(20, 10), 6, EquipmentSlotGroup.MAINHAND),
                0x55FFFF));

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