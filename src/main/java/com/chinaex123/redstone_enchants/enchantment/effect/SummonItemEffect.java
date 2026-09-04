package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 自定义附魔实体效果模板（参考 confluence {@code SummonItemEffect}）。
 * <p>在 {@code minecraft:post_attack} 等钩子触发时，在受害实体位置生成指定物品堆。
 * 由 {@code init/ModEnchantmentEntityEffects} 以 {@code ENCHANTMENT_ENTITY_EFFECT_TYPE}
 * 注册其 codec，供附魔 JSON 的 effects 声明使用。
 * <p>后续 swords 等战斗类附魔需要"攻击附加行为"时，照此形态新增 effect 类即可。
 */
public record SummonItemEffect(Holder<Item> item, LevelBasedValue count) implements EnchantmentEntityEffect {
    public static final MapCodec<SummonItemEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(SummonItemEffect::item),
            LevelBasedValue.CODEC.fieldOf("count").orElseGet(() -> LevelBasedValue.constant(1)).forGetter(SummonItemEffect::count)
    ).apply(instance, SummonItemEffect::new));

    public SummonItemEffect(Holder<Item> item) {
        this(item, LevelBasedValue.constant(1));
    }

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        float calculated = this.count.calculate(enchantmentLevel);
        int count = (int) calculated;
        if (entity.getRandom().nextFloat() < calculated - count) count += 1;
        if (count < 1) return;
        ItemEntity itemEntity = new ItemEntity(level, origin.x, entity.getEyeY(), origin.z, new ItemStack(this.item, count));
        itemEntity.setPickUpDelay(0);
        level.addFreshEntity(itemEntity);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
