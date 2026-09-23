package com.chinaex123.redstone_enchants.event.all_fishing;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModAttachments;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * 钓鱼竿（all_fishing）附魔在鱼钩 tick 事件上的统一分发器（导电鱼线：雷雨天勾住生物召唤闪电）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class FishingHookTickEvents {

    @SubscribeEvent
    public static void onEntityTickPre(EntityTickEvent.Pre event) {
        conductiveLine(event);
    }

    // ---- 导电鱼线 ----

    private static void conductiveLine(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof FishingHook hook)) {
            return;
        }

        Player owner = hook.getPlayerOwner();
        if (owner == null) {
            return;
        }

        ItemStack rod = owner.getMainHandItem();
        if (rod.isEmpty() || !rod.is(Items.FISHING_ROD)) {
            return;
        }

        if (!owner.level().isThundering()) {
            return;
        }

        if (!EnchantmentHelper.has(rod, ModEnchantmentEffectComponents.CONDUCTIVE_LINE.get())) {
            return;
        }

        // 检查是否勾住生物
        Entity hookedEntity = hook.getHookedIn();
        if (hookedEntity instanceof LivingEntity livingEntity) {
            // 去重状态挂在鱼钩上：勾住期间只劈一次，松钩或鱼钩消失后随之复位
            // （旧的静态 UUID 集合在"勾住状态下鱼钩消失"时会永久残留，导致该生物再也劈不到）
            if (!hook.getData(ModAttachments.CONDUCTIVE_LINE_STRUCK.get())) {
                hook.setData(ModAttachments.CONDUCTIVE_LINE_STRUCK.get(), Boolean.TRUE);

                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(livingEntity.level());
                if (lightning != null) {
                    lightning.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                    livingEntity.level().addFreshEntity(lightning);
                }
            }
        } else {
            // 没有勾住时复位，允许下次勾住时再劈
            hook.setData(ModAttachments.CONDUCTIVE_LINE_STRUCK.get(), Boolean.FALSE);
        }
    }

    private FishingHookTickEvents() {
    }
}
