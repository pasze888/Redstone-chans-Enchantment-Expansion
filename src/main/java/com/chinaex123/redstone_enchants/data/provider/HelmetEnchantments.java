package com.chinaex123.redstone_enchants.data.provider;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.TrailParticleEffect;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.SetValue;

import static com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider.*;

/** 头盔附魔（含足迹粒子）的 datagen 声明。 */
final class HelmetEnchantments {

    static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

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

        register(context, ModEnchantments.DESPERATE_COUNTER, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 3, 5,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HEAD),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.DESPERATE_COUNTER_DAMAGE.get(),
                        new SetValue(LevelBasedValue.perLevel(0.25F))));

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

        register(context, ModEnchantments.ANTI_CAMOUFLAGE, colored(
                Enchantment.definition(items.getOrThrow(ARMORS_HEAD), items.getOrThrow(ARMORS_HEAD), 3, 1,
                        Enchantment.dynamicCost(12, 6), Enchantment.dynamicCost(24, 12), 8, EquipmentSlotGroup.HEAD),
                0xFF55FF)
                .withEffect(ModEnchantmentEffectComponents.ANTI_CAMOUFLAGE_DURATION_BONUS.get(),
                        new AddValue(LevelBasedValue.perLevel(10.0F))));

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
    }

    private HelmetEnchantments() {
    }
}
