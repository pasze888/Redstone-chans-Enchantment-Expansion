package com.chinaex123.redstone_enchants.event.unbreaking;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModDataComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
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
                if (!stack.has(DataComponents.UNBREAKABLE)) {
                    stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                    stack.set(ModDataComponents.INDESTRUCTIBLE_APPLIED.get(), Unit.INSTANCE);
                }
            } else {
                // 如果没有附魔，移除不可摧毁组件（修复：只移除本附魔写入的，
                // 指令/其它 mod 设置的 UNBREAKABLE 不再被剥——判定标记是否存在）
                if (stack.has(ModDataComponents.INDESTRUCTIBLE_APPLIED.get())) {
                    stack.remove(DataComponents.UNBREAKABLE);
                    stack.remove(ModDataComponents.INDESTRUCTIBLE_APPLIED.get());
                }
            }
        }
    }

    private UnbreakingEquipmentEvents() {
    }
}
