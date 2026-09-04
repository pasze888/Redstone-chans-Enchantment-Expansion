package com.chinaex123.redstone_enchants.event.sword;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.component.GamblerData;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * 剑类战斗附魔在伤害事件上的统一分发器。
 * <p>行为参数由附魔 JSON 声明（见 {@link ModEnchantmentEffectComponents}），
 * 这里按固定顺序驱动各效果。旧实现是每个附魔一个独立订阅者，
 * 执行顺序取决于注册顺序且互相覆盖（均以原始伤害为基数的附魔只有一个生效），
 * 分发器固定执行顺序：赌徒 → （后续）伏击 → 背刺 → 均衡器 → 处决，沿用各旧公式。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class SwordLivingDamageEvents {
    private static final EquipmentSlot[] HAND_SLOTS = { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND };

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (attacker == null) {
            return;
        }
        gamblerRoll(event, attacker);
        executionKill(event, attacker);
    }

    // ---- 赌徒 ----

    private static void gamblerRoll(LivingDamageEvent.Pre event, LivingEntity attacker) {
        ItemStack tool = findHandStackWith(attacker, ModEnchantmentEffectComponents.GAMBLER_DATA.get());
        if (tool == null) {
            return;
        }
        GamblerData data = EnchantmentUtil.specialValue(tool, ModEnchantmentEffectComponents.GAMBLER_DATA.get());
        if (data == null) {
            return;
        }
        float originalDamage = event.getOriginalDamage();
        if (attacker.getRandom().nextFloat() < data.odds()) {
            event.setNewDamage(originalDamage * data.bonusMultiplier());
        } else {
            event.setNewDamage(originalDamage * data.penaltyMultiplier());
        }
    }

    // ---- 处决 ----

    private static void executionKill(LivingDamageEvent.Pre event, LivingEntity attacker) {
        ItemStack weapon = attacker.getMainHandItem();
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.EXECUTION.get())) {
            return;
        }
        LivingEntity target = event.getEntity();
        // 旧版公式原样：目标当前生命占比 < 25% 时，把伤害设为目标当前生命值（必死）
        float healthPercent = target.getHealth() / target.getMaxHealth();
        if (healthPercent < 0.25F) {
            event.setNewDamage(target.getHealth());
        }
    }

    /** 在主手/副手查找携带指定单值组件的物品（旧版赌徒逐槽检查到第一个即止）。 */
    private static ItemStack findHandStackWith(LivingEntity attacker, DataComponentType<?> type) {
        for (EquipmentSlot slot : HAND_SLOTS) {
            ItemStack stack = attacker.getItemBySlot(slot);
            if (!stack.isEmpty() && EnchantmentUtil.specialValue(stack, type) != null) {
                return stack;
            }
        }
        return null;
    }

    private SwordLivingDamageEvents() {
    }
}
