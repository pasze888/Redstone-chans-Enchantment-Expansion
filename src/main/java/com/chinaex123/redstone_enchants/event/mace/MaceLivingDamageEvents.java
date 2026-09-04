package com.chinaex123.redstone_enchants.event.mace;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * 锤类（mace）附魔在伤害事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：势能转化 → （后续）闪电使者。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class MaceLivingDamageEvents {

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        // 固定顺序：势能转化 → 闪电使者（各段按旧版语义自行解析攻击者）
        potentialConversion(event);
    }

    // ---- 势能转化 ----

    private static void potentialConversion(LivingDamageEvent.Pre event) {
        // 旧版用 getDirectEntity + Player 判定攻击者
        if (!(event.getSource().getDirectEntity() instanceof Player attacker)) {
            return;
        }
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.POTENTIAL_CONVERSION_FALL_BONUS.get())) {
            return;
        }
        LivingEntity target = event.getEntity();

        // 检查是否是下落攻击（fallDistance > 0）
        float fallDistance = attacker.fallDistance;
        if (fallDistance <= 0) {
            return;
        }

        // 每格高度增加 0.8% 伤害 × 附魔等级
        float bonusPerBlock = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.POTENTIAL_CONVERSION_FALL_BONUS.get());
        float totalBonus = fallDistance * bonusPerBlock;

        // 如果目标有护甲，额外增加伤害
        float targetArmor = target.getArmorValue();
        if (targetArmor > 0) {
            float armorFactor = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.POTENTIAL_CONVERSION_ARMOR_FACTOR.get());
            float armorBonus = targetArmor * armorFactor;
            totalBonus *= (1 + armorBonus);
        }

        // 增加伤害（基数 original，公式原样）
        event.setNewDamage(event.getOriginalDamage() * (1 + totalBonus));
    }

    private MaceLivingDamageEvents() {
    }
}
