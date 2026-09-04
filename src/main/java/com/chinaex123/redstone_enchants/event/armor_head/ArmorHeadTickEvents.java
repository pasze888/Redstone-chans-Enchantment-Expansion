package com.chinaex123.redstone_enchants.event.armor_head;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
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
 * 头盔（armors_head）附魔在实体 tick 事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：矿工 → （后续）以寡敌众/反伪装/绝境逆袭。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorHeadTickEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        // 固定顺序：矿工 → 以寡敌众 → 反伪装 → 绝境逆袭（旧版为独立订阅者，顺序未定义）
        adaptive(event);
    }

    // ---- 矿工 ----

    private static void adaptive(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 检查头盔是否有矿工附魔
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(helmet, ModEnchantmentEffectComponents.ADAPTIVE.get())) {
            return;
        }

        // 检查是否在 Y0 以下
        if (player.getY() < 0) {
            // 给予夜视效果
            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION, 20 * 12, 0, false, false
            ));
        } else {
            // 不在 Y0 以下时，移除夜视效果
            if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
    }

    private ArmorHeadTickEvents() {
    }
}
