package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 最后的希望（last_hope）代价：命中后清空武器主手。
 * <p>等价于原命令 {@code item replace entity @s weapon.mainhand with air}
 * （对生物同样生效——生物的 mainhand 即其武器槽）。
 * <p>命中敌方（非黑名单实体）的条件保留在 datagen 声明中。
 */
public record ClearMainHandEffect() implements EnchantmentEntityEffect {

    public static final ClearMainHandEffect INSTANCE = new ClearMainHandEffect();
    public static final MapCodec<ClearMainHandEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (entity instanceof LivingEntity living) {
            living.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
