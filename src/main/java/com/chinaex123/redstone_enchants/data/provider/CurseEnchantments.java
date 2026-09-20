package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.DropHeldItemEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.KillSelfEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MovementPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.item.enchantment.effects.DamageEntity;
import net.minecraft.world.item.enchantment.effects.ExplodeEffect;
import net.minecraft.world.item.enchantment.effects.Ignite;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.phys.Vec3;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 全部诅咒系附魔（跨槽位，按名称家族聚合）的 datagen 声明。 */
final class CurseEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<DamageType> damageTypes = context.lookup(Registries.DAMAGE_TYPE);

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
    }

    private CurseEnchantments() {
    }
}
