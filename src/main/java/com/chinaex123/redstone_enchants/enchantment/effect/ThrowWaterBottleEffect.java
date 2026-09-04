package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 水瓶投射（water_bottle_projection）：攻击命中时在实体位置掷出一瓶喷溅型水瓶。
 * <p>等价于原命令 {@code summon potion ~ ~ ~ {Item:{id:"minecraft:splash_potion",
 * components:{"minecraft:potion_contents":{potion:"minecraft:water"}}}}}
 * ——初始无速度，落地（或撞实体）后炸开扑灭火焰/沾湿方块。
 */
public record ThrowWaterBottleEffect() implements EnchantmentEntityEffect {

    public static final ThrowWaterBottleEffect INSTANCE = new ThrowWaterBottleEffect();
    public static final MapCodec<ThrowWaterBottleEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        ThrownPotion potion = new ThrownPotion(level, origin.x(), origin.y(), origin.z());
        ItemStack stack = new ItemStack(Items.SPLASH_POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
        potion.setItem(stack);
        level.addFreshEntity(potion);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
