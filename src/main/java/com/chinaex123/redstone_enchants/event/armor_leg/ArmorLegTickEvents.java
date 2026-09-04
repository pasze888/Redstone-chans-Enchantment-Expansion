package com.chinaex123.redstone_enchants.event.armor_leg;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModAttachments;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * 护腿（armors_leg）附魔在实体 tick 事件上的统一分发器（隐身斗篷：潜行时隐身）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorLegTickEvents {
    private static final int BASE_INVISIBILITY_DURATION = 80; // 基础时间

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        invisibilityCloak(event);
    }

    // ---- 隐身斗篷 ----

    private static void invisibilityCloak(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(player.level() instanceof ServerLevel)) {
            // 效果施加以服务端为准（旧版双侧执行，行为不变）
            return;
        }

        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.isEmpty()) {
            return;
        }

        boolean hasEnchant = EnchantmentHelper.has(leggings, ModEnchantmentEffectComponents.INVISIBILITY_CLOAK.get());

        if (hasEnchant && player.isShiftKeyDown()) {
            if (!player.hasEffect(MobEffects.INVISIBILITY)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.INVISIBILITY,
                        BASE_INVISIBILITY_DURATION,
                        0,
                        false,
                        false
                ));
                player.setData(ModAttachments.CLOAK_INVISIBILITY.get(), Boolean.TRUE);
            }
        } else {
            // 修复：只移除本附魔施加的隐身，其它来源（药水等）的隐身不再被误抹
            if (player.hasEffect(MobEffects.INVISIBILITY)
                    && player.hasData(ModAttachments.CLOAK_INVISIBILITY.get())) {
                player.removeEffect(MobEffects.INVISIBILITY);
            }
            player.removeData(ModAttachments.CLOAK_INVISIBILITY.get());
        }
    }

    private ArmorLegTickEvents() {
    }
}
