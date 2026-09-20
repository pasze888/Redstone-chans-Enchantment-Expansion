package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import java.util.Optional;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.TagParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.ExplodeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/**
 * 附魔 datagen 主入口：保留 DATA_BUILDER、全部物品/实体/方块标签常量与共用 helper；
 * 各分类附魔的具体声明见同包的 *Enchantments 类。
 * <p>runData 生成 {@code data/redstone_enchants/enchantment/*.json}；数值与迁移前的手写 JSON 一致。
 */
public final class ModEnchantmentProvider {

    public static final RegistrySetBuilder DATA_BUILDER =
            new RegistrySetBuilder().add(Registries.ENCHANTMENT, ModEnchantmentProvider::bootstrap);

    static final TagKey<Item> TOOLS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("tools"));
    static final TagKey<Item> SWORDS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("swords"));
    static final TagKey<Item> SWORDS_AND_AXES = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("swords_and_axes"));
    static final TagKey<Item> SWORDS_AND_BOW = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("swords_and_bow"));
    static final TagKey<Item> TRIDENT_AND_BOW = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("trident_and_bow"));
    static final TagKey<Item> ELYTRA = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("elytra"));
    static final TagKey<Item> HORSE_ANIMAL_ARMOR = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("horse_animal_armor"));
    static final TagKey<Item> ALL_TOOLS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_tools"));
    static final TagKey<Item> C_ENCHANTABLES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "enchantables"));
    static final TagKey<Item> WEAPON_ITEMS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("weapon"));
    static final TagKey<Item> LAST_HOPE_WEAPONS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("last_hope_weapons"));
    static final TagKey<Item> CHEST_ARMOR_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("chest_armor"));
    static final TagKey<Item> PICKAXES_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("pickaxes"));
    static final TagKey<Item> MACE_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/mace"));
    static final TagKey<Item> SHIELD_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/shield"));
    static final TagKey<Item> ALL_FLINT_AND_STEEL = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_flint_and_steel"));
    static final TagKey<Item> ARMORS = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors"));
    static final TagKey<Item> ALL_BOW = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_bow"));
    static final TagKey<Item> ALL_FISHING = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_fishing"));
    static final TagKey<Item> ALL_SHEAR = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("all_shear"));
    static final TagKey<Item> ARMORS_HEAD = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors_head"));
    static final TagKey<Item> ARMORS_FOOT = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors_foot"));
    static final TagKey<Item> ARMORS_CHEST = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors_chest"));
    static final TagKey<Item> ARMORS_LEG = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("armors_leg"));
    static final TagKey<Item> WOLF_ARMOR = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("wolf_armor"));
    static final TagKey<Item> HORSE_ARMOR = TagKey.create(Registries.ITEM, RedstoneEnchants.asResource("horse_armor"));
    static final TagKey<Enchantment> MACE_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/mace"));
    static final TagKey<Enchantment> NO_SEA_BREEZE_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/no_sea_breeze"));
    static final TagKey<Item> ENCHANTABLES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "enchantables"));
    static final TagKey<Enchantment> STONE_TRANSMUTATION_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/stone_transmutation"));
    static final TagKey<Enchantment> UNBREAKING_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/unbreaking"));
    static final TagKey<Enchantment> INDESTRUCTIBLE_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/indestructible"));
    static final TagKey<Enchantment> AURA_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/aura"));
    static final TagKey<Enchantment> SPLASH_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/splash"));
    static final TagKey<Enchantment> BOOTS_GALLOP_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/boots_gallop"));
    static final TagKey<Enchantment> DAMAGE_BOW_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/damage_bow"));
    static final TagKey<Enchantment> BOW_SPREAD_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/bow_spread"));
    static final TagKey<Enchantment> NO_SHOTGUN_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/no_shotgun"));
    static final TagKey<Enchantment> DAMAGE_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/damage"));
    static final TagKey<Enchantment> ARMORS_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/armors"));
    static final TagKey<Enchantment> ARMORS_HEAD_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/armors_head"));
    static final TagKey<Enchantment> HEAD_LUCKY_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/head_lucky"));
    static final TagKey<Enchantment> WALKER_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/walker"));
    static final TagKey<Enchantment> BEDROCK_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/bedrock"));
    static final TagKey<Enchantment> SWORDS_AND_BOW_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/swords_and_bow"));
    static final TagKey<Enchantment> DAMAGE_BANE_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/damage_bane"));
    static final TagKey<Enchantment> SHIELD_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/shield"));
    static final TagKey<Enchantment> BANE_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/bane"));
    static final TagKey<Enchantment> TOUCH_EXCLUSIVE =
            TagKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource("exclusive_set/touch"));
    static final TagKey<EntityType<?>> ENTITY_BOSS =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_boss"));
    static final TagKey<EntityType<?>> ENTITY_END =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_end"));
    static final TagKey<EntityType<?>> ENTITY_ILLAGER =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_illager"));
    static final TagKey<EntityType<?>> ENTITY_NETHER =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_nether"));
    static final TagKey<EntityType<?>> ENTITY_WATER =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("entity_water"));
    static final TagKey<EntityType<?>> BLACK_ENTITY =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("black_entity"));
    static final TagKey<EntityType<?>> PROJECTILES =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("projectiles"));
    static final TagKey<EntityType<?>> EXORCISM_ENTITIES =
            TagKey.create(Registries.ENTITY_TYPE, RedstoneEnchants.asResource("exorcism"));
    static final TagKey<Block> BEDROCK_BREAKER_BLOCKS =
            TagKey.create(Registries.BLOCK, RedstoneEnchants.asResource("bedrock_breaker"));
    static final TagKey<Block> GLASS_BLOCKS =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks"));

    /**
     * 跨注册表裸 id 的 Holder：holder owner 恒返回 canSerializeIn=true，
     * 使 datagen 序列化走 ResourceKey 分支直接输出 id 字符串，
     * 不要求该注册表条目在 datagen JVM 里真实存在（如 ars_nouveau:blasting、apothic_attributes:draw_speed）。
     */
    /** 解析手写 SNBT 为 NbtPredicate（parseTag 抛受检异常，集中处理）。 */
    static NbtPredicate nbtPredicate(String snbt) {
        try {
            return new NbtPredicate(TagParser.parseTag(snbt));
        } catch (CommandSyntaxException e) {
            throw new IllegalStateException("bad snbt: " + snbt, e);
        }
    }

    static <T> Holder<T> foreignHolder(ResourceKey<T> key) {
        return Holder.Reference.createStandAlone(
                new HolderOwner<T>() {
                    @Override
                    public boolean canSerializeIn(HolderOwner<T> owner) {
                        return true;
                    }
                }, key);
    }

    /** {@link #foreignHolder} 的 HolderSet 版（ApplyMobEffect.toApply 等单元素集合）。 */
    static <T> HolderSet<T> foreignId(ResourceKey<T> key) {
        return HolderSet.direct(foreignHolder(key));
    }

    /** 风爆箭（blast_arrows）：爆裂效果，wind_burst 音效（裸 id 引用）。 */
    static ExplodeEffect windBurstExplosion(HolderGetter<DamageType> damageTypes) {
        return new ExplodeEffect(false, Optional.of(damageTypes.getOrThrow(DamageTypes.EXPLOSION)),
                Optional.of(LevelBasedValue.perLevel(0.5F, 0.25F)),
                Optional.of(BuiltInRegistries.BLOCK.getOrCreateTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)),
                new Vec3(0, 0.5, 0), LevelBasedValue.perLevel(1.5F, 0.75F), false,
                Level.ExplosionInteraction.NONE, ParticleTypes.GUST_EMITTER_SMALL,
                ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.WIND_CHARGE_BURST);
    }

    /** 轰炸箭（bomb_arrows）：灰烬爆炸，warden 音效固定范围 16（对象形状）。 */
    static ExplodeEffect ashExplosion(HolderGetter<DamageType> damageTypes, float radiusBase, float radiusPerLevel,
                                       Optional<LevelBasedValue> knockbackMultiplier) {
        return new ExplodeEffect(false, Optional.of(damageTypes.getOrThrow(DamageTypes.ARROW)), knockbackMultiplier,
                Optional.empty(), new Vec3(0, 0.5, 0), LevelBasedValue.perLevel(radiusBase, radiusPerLevel), false,
                Level.ExplosionInteraction.NONE, ParticleTypes.ASH, ParticleTypes.EXPLOSION_EMITTER,
                Holder.direct(SoundEvent.createFixedRangeEvent(
                        ResourceLocation.withDefaultNamespace("entity.warden.sonic_boom"), 16.0F)));
    }

    private static void bootstrap(BootstrapContext<Enchantment> context) {
        RangedEnchantments.bootstrap(context);
        MeleeEnchantments.bootstrap(context);
        MaceEnchantments.bootstrap(context);
        ShieldEnchantments.bootstrap(context);
        HelmetEnchantments.bootstrap(context);
        ChestplateEnchantments.bootstrap(context);
        LeggingsEnchantments.bootstrap(context);
        BootsEnchantments.bootstrap(context);
        ArmorEnchantments.bootstrap(context);
        AnimalArmorEnchantments.bootstrap(context);
        ToolEnchantments.bootstrap(context);
        UniversalEnchantments.bootstrap(context);
        CurseEnchantments.bootstrap(context);
    }

    static Enchantment.Builder colored(Enchantment.EnchantmentDefinition definition, int color) {
        return Enchantment.enchantment(definition)
                .withCustomName(name -> name.withStyle(style -> style.withColor(color)));
    }

    static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private ModEnchantmentProvider() {
    }
}
