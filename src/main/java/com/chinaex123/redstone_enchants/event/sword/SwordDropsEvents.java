package com.chinaex123.redstone_enchants.event.sword;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 剑类战斗附魔在掉落事件上的统一分发器（与 {@link SwordLivingDamageEvents} 同模式）。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：屠夫 → （后续）斩首。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class SwordDropsEvents {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (attacker == null) {
            return;
        }
        butcherExtraDrops(event, attacker);
    }

    // ---- 屠夫 ----

    private static void butcherExtraDrops(LivingDropsEvent event, LivingEntity attacker) {
        LivingEntity entity = event.getEntity();
        // 只处理动物（包括猪、牛、羊等）
        if (!(entity instanceof Animal)) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        float extraRatio = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.BUTCHER_EXTRA_DROP.get());
        if (extraRatio <= 0) {
            return;
        }

        // 为每个掉落物额外追加一份：数量 = max(1, ceil(原数量 × 比例))（比例 = 0.5×级）
        List<ItemEntity> additionalDrops = new ArrayList<>();
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            int extraCount = Math.max(1, (int) Math.ceil(stack.getCount() * extraRatio));
            ItemStack extraStack = stack.copy();
            extraStack.setCount(extraCount);
            additionalDrops.add(new ItemEntity(serverLevel, entity.getX(), entity.getY(), entity.getZ(), extraStack));
        }
        event.getDrops().addAll(additionalDrops);
    }

    private SwordDropsEvents() {
    }
}
