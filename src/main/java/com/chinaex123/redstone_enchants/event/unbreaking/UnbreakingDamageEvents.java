package com.chinaex123.redstone_enchants.event.unbreaking;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModAttachments;
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
        // 固定顺序：坚固装备耐久恢复 → 牺牲
        sturdyRestore(event);
        sacrificeRepair(event);
    }

    // ---- 坚固 ----

    private static void sturdyImmunity(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();

        // 检查伤害来源是否为爆炸/闪电/火焰
        DamageSource source = event.getSource();
        boolean protectedDamage = source.is(DamageTypeTags.IS_EXPLOSION)
                || source.is(DamageTypes.LIGHTNING_BOLT)
                || source.is(DamageTypeTags.IS_FIRE);
        if (!protectedDamage) {
            return;
        }

        // 检查是否有槽位带坚固附魔
        boolean hasSturdy = false;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty() && EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.STURDY.get())) {
                hasSturdy = true;
                break;
            }
        }
        if (!hasSturdy) {
            return;
        }

        // 行为变更（经用户确认）：不再整次免疫本体伤害，本体照常受伤；
        // 这里记录受伤前各槽耐久快照，Post 时把带坚固的装备耐久恢复原样
        int[] snapshot = new int[EquipmentSlot.values().length];
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            snapshot[slot.ordinal()] = stack.isEmpty() ? -1 : stack.getDamageValue();
        }
        entity.setData(ModAttachments.STURDY_DAMAGE_SNAPSHOT.get(), snapshot);
    }

    private static void sturdyRestore(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasData(ModAttachments.STURDY_DAMAGE_SNAPSHOT.get())) {
            return;
        }
        int[] snapshot = entity.getData(ModAttachments.STURDY_DAMAGE_SNAPSHOT.get());
        entity.removeData(ModAttachments.STURDY_DAMAGE_SNAPSHOT.get());
        if (!(entity.level() instanceof ServerLevel)) {
            // 耐久以服务端为准
            return;
        }

        // 恢复带坚固附魔装备的耐久到受伤前（装备被扣毁的物品已不存在，跳过）
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            int before = snapshot[slot.ordinal()];
            if (before < 0) {
                continue;
            }
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty() || !EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.STURDY.get())) {
                continue;
            }
            stack.setDamageValue(before);
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
