package com.chinaex123.redstone_enchants.event.armor_chest;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Random;

/**
 * 胸甲（armors_chest）附魔在受击事件上的统一分发器（防弹：概率免疫潜影子弹）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorChestIncomingDamageEvents {
    private static final Random RANDOM = new Random(); // 旧版同款 java.util.Random 静态实例

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        bulletproof(event);
    }

    // ---- 防弹 ----

    private static void bulletproof(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!(target instanceof Player player)) {
            return;
        }

        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(chestplate, ModEnchantmentEffectComponents.BULLETPROOF_IMMUNITY_CHANCE.get())) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            // 事件取消以服务端为准
            return;
        }

        // 检查伤害来源是否是潜影子弹
        if (!(event.getSource().getDirectEntity() instanceof ShulkerBullet)) {
            return;
        }

        // 免疫概率：0.25 + 0.25×(级-1)
        float immunityChance = EnchantmentUtil.itemValue(serverLevel, chestplate,
                ModEnchantmentEffectComponents.BULLETPROOF_IMMUNITY_CHANCE.get());

        if (RANDOM.nextFloat() < immunityChance) {
            event.setCanceled(true);
        }
    }

    private ArmorChestIncomingDamageEvents() {
    }
}
