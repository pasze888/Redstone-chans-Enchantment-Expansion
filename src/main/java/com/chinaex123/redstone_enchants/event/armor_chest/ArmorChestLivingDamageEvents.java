package com.chinaex123.redstone_enchants.event.armor_chest;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * 胸甲（armors_chest）附魔在受击伤害事件上的统一分发器（狂战士：按已损失生命比例加攻）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorChestLivingDamageEvents {
    private static final float HEALTH_LOST_STEP = 0.1F; // 每 10% 生命损失为一个步长

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        berserk(event);
    }

    // ---- 狂战士 ----

    private static void berserk(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity attacker)) {
            return;
        }
        if (!(attacker instanceof Player player)) {
            return;
        }

        // 检查胸甲是否有狂战士附魔
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(chestplate, ModEnchantmentEffectComponents.BERSERK_DAMAGE_BONUS.get())) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            // 伤害计算以服务端为准
            return;
        }

        // 计算生命值损失百分比
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        float healthLost = maxHealth - currentHealth;
        float healthLostPercent = healthLost / maxHealth;

        // 每损失 10% 生命，伤害 +3%（每级）
        int lostTenPercent = (int) (healthLostPercent / HEALTH_LOST_STEP);
        if (lostTenPercent <= 0) {
            return;
        }

        float damagePerLevel = EnchantmentUtil.itemValue(serverLevel, chestplate,
                ModEnchantmentEffectComponents.BERSERK_DAMAGE_BONUS.get());
        double damageBonus = lostTenPercent * damagePerLevel;

        // 应用伤害加成
        event.setNewDamage((float) (event.getNewDamage() * (1 + damageBonus)));
    }

    private ArmorChestLivingDamageEvents() {
    }
}
