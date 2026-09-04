package com.chinaex123.redstone_enchants.event.unbreaking;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * unbreaking 家族（穿戴侧）在伤害事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：Pre 坚固（后续迁入）→ …，Post 牺牲。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class UnbreakingDamageEvents {

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        sacrificeRepair(event);
    }

    // ---- 牺牲 ----

    private static void sacrificeRepair(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        // 检查所有装备槽位
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            float repairBase = EnchantmentUtil.itemValue(serverLevel, stack, ModEnchantmentEffectComponents.SACRIFICE_REPAIR.get());
            if (repairBase <= 0) {
                continue;
            }
            // 修复物品（减少耐久值）；修复量公式原样 = floor(1.0 + (level - 1) × 0.5)
            int repairAmount = (int) Math.floor(repairBase);
            int newDamage = Math.max(0, stack.getDamageValue() - repairAmount);
            stack.setDamageValue(newDamage);
        }
    }

    private UnbreakingDamageEvents() {
    }
}
