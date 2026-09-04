package com.chinaex123.redstone_enchants.event.unbreaking;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

/**
 * unbreaking 家族在装备变更事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是每个附魔一个独立订阅者。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class UnbreakingEquipmentEvents {
    private static final EquipmentSlot[] ALL_SLOTS = {
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND,
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.BODY
    };

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();

        for (EquipmentSlot slot : ALL_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            if (EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.INDESTRUCTIBLE.get())) {
                // 添加不可摧毁组件
                stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
            } else {
                // 如果没有附魔，移除不可摧毁组件
                // （旧 handler 同款副作用：会剥离其它来源设置的 UNBREAKABLE，原样保留）
                stack.remove(DataComponents.UNBREAKABLE);
            }
        }
    }

    private UnbreakingEquipmentEvents() {
    }
}
