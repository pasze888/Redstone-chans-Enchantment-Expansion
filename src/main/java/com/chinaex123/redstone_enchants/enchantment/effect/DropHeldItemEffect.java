package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 笨拙诅咒（curse_of_clumsiness）：受诅咒者把主手物品掉出去——
 * 生成掉落物（拾取延迟 3 秒）、主手清空、播放拾取音效。
 * <p>等价于 drop_punch/drop_attack 的核心行为（summon item + 主手 air）。
 * 原两函数仅在掉落位置上有差别（脚下 vs 视线前方 2 格且需为空气），此处统一为脚下。
 * <p>命中方块/攻击时的 40% 概率条件保留在 datagen 声明中。
 */
public record DropHeldItemEffect() implements EnchantmentEntityEffect {

    public static final DropHeldItemEffect INSTANCE = new DropHeldItemEffect();
    public static final MapCodec<DropHeldItemEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final int PICKUP_DELAY_TICKS = 60;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof Player player)) {
            return;
        }
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) {
            return;
        }
        ItemEntity dropped = new ItemEntity(level, origin.x(), origin.y(), origin.z(), held.copy());
        dropped.setPickUpDelay(PICKUP_DELAY_TICKS);
        level.addFreshEntity(dropped);
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        level.playSound(null, origin.x(), origin.y(), origin.z(),
                SoundEvents.ITEM_PICKUP, SoundSource.MASTER, 1.0F, 1.0F);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
