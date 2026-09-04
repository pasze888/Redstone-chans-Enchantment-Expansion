package com.chinaex123.redstone_enchants.event.armor_wolf;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * 狼铠（wolf_armor）附魔在死亡事件上的统一分发器（尸体回收：狼击杀后回复生命）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorWolfDeathEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        carrionEater(event);
    }

    // ---- 尸体回收 ----

    private static void carrionEater(LivingDeathEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof Wolf wolf)) {
            return;
        }

        // 检查狼是否有主人
        if (!wolf.isTame()) {
            return;
        }

        // 检查狼铠是否有尸体回收附魔
        ItemStack armor = wolf.getItemBySlot(EquipmentSlot.BODY);
        if (armor.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(armor, ModEnchantmentEffectComponents.CARRION_EATER_HEAL.get())) {
            return;
        }
        if (!(wolf.level() instanceof ServerLevel serverLevel)) {
            // 治疗以服务端为准
            return;
        }

        // 计算治疗量：每级 25% 最大生命值
        float healRatio = EnchantmentUtil.itemValue(serverLevel, armor,
                ModEnchantmentEffectComponents.CARRION_EATER_HEAL.get());
        float maxHealth = wolf.getMaxHealth();
        float healAmount = maxHealth * healRatio;

        // 应用治疗
        wolf.heal(healAmount);
    }

    private ArmorWolfDeathEvents() {
    }
}
