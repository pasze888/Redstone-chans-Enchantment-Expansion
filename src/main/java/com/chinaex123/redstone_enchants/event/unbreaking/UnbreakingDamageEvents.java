package com.chinaex123.redstone_enchants.event.unbreaking;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * unbreaking 家族（穿戴侧）在伤害事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：Pre 坚固免疫，Post 牺牲。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class UnbreakingDamageEvents {

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        sturdyImmunity(event);
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        sacrificeRepair(event);
    }

    // ---- 坚固 ----

    private static void sturdyImmunity(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();

        // 检查所有装备槽
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.STURDY.get())) {
                continue;
            }

            // 检查伤害来源
            DamageSource source = event.getSource();

            // 免疫爆炸
            if (source.is(DamageTypeTags.IS_EXPLOSION)) {
                event.setNewDamage(0);
                return;
            }

            // 免疫闪电
            if (source.is(DamageTypes.LIGHTNING_BOLT)) {
                event.setNewDamage(0);
                return;
            }

            // 免疫岩浆/火焰
            if (source.is(DamageTypeTags.IS_FIRE)) {
                event.setNewDamage(0);
                return;
            }
        }
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
