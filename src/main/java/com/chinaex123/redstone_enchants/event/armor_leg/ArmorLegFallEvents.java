package com.chinaex123.redstone_enchants.event.armor_leg;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

/**
 * 护腿（armors_leg）附魔在坠落事件上的统一分发器（战术护膝：潜行落地免摔伤）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorLegFallEvents {

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        tacticalKnee(event);
    }

    // ---- 战术护膝 ----

    private static void tacticalKnee(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 检查护腿是否有战术护膝附魔
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(leggings, ModEnchantmentEffectComponents.TACTICAL_KNEE.get())) {
            return;
        }

        // 检查玩家是否潜行
        if (player.isShiftKeyDown()) {
            // 取消掉落伤害
            event.setCanceled(true);
        }
    }

    private ArmorLegFallEvents() {
    }
}
