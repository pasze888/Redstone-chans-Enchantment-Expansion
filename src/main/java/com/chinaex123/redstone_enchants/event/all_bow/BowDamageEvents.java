package com.chinaex123.redstone_enchants.event.all_bow;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * 弓类（all_bow）附魔在伤害事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者（两者都以 original 为基数直接 setNewDamage，相互覆盖、不叠加）；
 * 分发器固定执行顺序：狙击 → （后续）伏特。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class BowDamageEvents {
    private static final double DISTANCE_STEP = 10.0; // 每 10 格一个加成档位

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        // 固定顺序：狙击 → 伏特（各段以 original 为基数直接 setNewDamage，与旧版一致地相互覆盖、不叠加）
        snipe(event);
    }

    // ---- 狙击 ----

    private static void snipe(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof Player player)) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LivingEntity target = event.getEntity();

        ItemStack bow = player.getMainHandItem();
        if (bow.isEmpty()) {
            bow = player.getOffhandItem();
        }
        if (bow.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(bow, ModEnchantmentEffectComponents.SNIPE_BONUS.get())) {
            return;
        }

        // 计算距离
        double distance = player.distanceTo(target);

        // 每 10 格增加 15% 伤害 × 附魔等级
        int distanceBonus = (int) (distance / DISTANCE_STEP);
        float bonusPer10Blocks = EnchantmentUtil.itemValue(serverLevel, bow, ModEnchantmentEffectComponents.SNIPE_BONUS.get());
        float bonus = distanceBonus * bonusPer10Blocks;

        event.setNewDamage(event.getOriginalDamage() * (1 + bonus));
    }

    private BowDamageEvents() {
    }
}
