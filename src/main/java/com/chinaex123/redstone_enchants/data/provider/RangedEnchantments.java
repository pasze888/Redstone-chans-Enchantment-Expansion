package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.ChainArrowsEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.EternalFrostAnimationEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.IgniteAreaEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ParticleBurstEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RainBlocksEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RicochetEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.FreezeWaterEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.HoveringArrowEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.IceArrowSlownessEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.KillSelfEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.SplashCloudEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.WeatherThunderEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import org.joml.Vector3f;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.item.enchantment.effects.ReplaceBlock;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.item.enchantment.effects.SummonEntityEffect;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.item.enchantment.effects.SpawnParticlesEffect;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 弓/弩/三叉戟附魔的 datagen 声明。 */
final class RangedEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
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

        register(context, ModEnchantments.SNIPE, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.SNIPE_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.15F))));

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

        register(context, ModEnchantments.VOLT, colored(
                Enchantment.definition(items.getOrThrow(ALL_BOW), items.getOrThrow(ALL_BOW), 3, 4,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HAND),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.VOLT_BONUS.get(), new AddValue(LevelBasedValue.perLevel(0.25F))));

        register(context, ModEnchantments.TELEPORT, colored(
                Enchantment.definition(items.getOrThrow(TRIDENT_AND_BOW), items.getOrThrow(TRIDENT_AND_BOW), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.ANY),
                0xFF55FF));

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

        register(context, ModEnchantments.ETERNAL_FROST, colored(
                Enchantment.definition(items.getOrThrow(TRIDENT_AND_BOW), items.getOrThrow(TRIDENT_AND_BOW), 2, 3,
                        Enchantment.dynamicCost(16, 8), Enchantment.dynamicCost(32, 16), 12, EquipmentSlotGroup.HAND),
                0xFFAA00)
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
                        EternalFrostAnimationEffect.INSTANCE)
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
                        EternalFrostAnimationEffect.INSTANCE,
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(PROJECTILES)))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        new KillSelfEffect(),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().of(PROJECTILES)))
                .exclusiveWith(enchantments.getOrThrow(SPLASH_EXCLUSIVE)));

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
    }

    private RangedEnchantments() {
    }
}
