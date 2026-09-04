package com.chinaex123.redstone_enchants.event.unbreaking;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModDataComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * unbreaking 家族在掉落物保护事件上的统一分发器（坚固：掉落物防火/防爆/防雷）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是每个附魔一个独立订阅者。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class UnbreakingItemEntityEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        ItemStack stack = itemEntity.getItem();
        boolean hasSturdy = EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.STURDY.get());

        if (hasSturdy) {
            // 设置防火
            if (!stack.has(DataComponents.FIRE_RESISTANT)) {
                stack.set(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);
                stack.set(ModDataComponents.STURDY_APPLIED.get(), Unit.INSTANCE);
            }
        } else {
            // 移除防火（修复：只移除本附魔写入的；物品默认组件（下界合金）/其它来源的
            // FIRE_RESISTANT 不再被剥——判定标记是否存在）
            if (stack.has(ModDataComponents.STURDY_APPLIED.get())) {
                stack.remove(DataComponents.FIRE_RESISTANT);
                stack.remove(ModDataComponents.STURDY_APPLIED.get());
            }
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        // 从受影响的实体列表中移除带坚固附魔的物品
        event.getAffectedEntities().removeIf(entity -> entity instanceof ItemEntity itemEntity
                && EnchantmentHelper.has(itemEntity.getItem(), ModEnchantmentEffectComponents.STURDY.get()));
    }

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        // 如果被击中的实体是带坚固附魔的物品，则取消事件
        if (event.getEntity() instanceof ItemEntity itemEntity
                && EnchantmentHelper.has(itemEntity.getItem(), ModEnchantmentEffectComponents.STURDY.get())) {
            event.setCanceled(true);
        }
    }

    private UnbreakingItemEntityEvents() {
    }
}
