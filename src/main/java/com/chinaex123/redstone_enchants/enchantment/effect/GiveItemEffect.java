package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 检索（retrieval）：穿戴胸甲者被箭/光灵箭命中时，自身获得一支同种箭。
 * <p>等价于原命令 {@code give @s <item>}（give 只对玩家生效，非玩家受害者
 * 命令失败；本实现对玩家给入背包，背包满则原地掉落，非玩家不处理）。
 * <p>触发概率与投射物种类条件保留在 datagen 声明中。
 */
public record GiveItemEffect(Item item) implements EnchantmentEntityEffect {

    public static final MapCodec<GiveItemEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(GiveItemEffect::item)
    ).apply(instance, GiveItemEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof Player player)) {
            return;
        }
        ItemStack stack = new ItemStack(this.item);
        if (!player.getInventory().add(stack) && !stack.isEmpty()) {
            level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack));
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
