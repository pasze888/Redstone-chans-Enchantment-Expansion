package com.chinaex123.redstone_enchants.event.all_fishing;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 钓鱼竿（all_fishing）附魔在鱼钩 tick 事件上的统一分发器（导电鱼线：雷雨天勾住生物召唤闪电）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class FishingHookTickEvents {
    private static final Set<UUID> STRUCK_ENTITIES = new HashSet<>();

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
            UUID entityId = livingEntity.getUUID();

            // 防止重复召唤（旧版同款：双侧执行，客户端本地生成的闪电为无效副本，保留原样）
            if (!STRUCK_ENTITIES.contains(entityId)) {
                STRUCK_ENTITIES.add(entityId);

                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(livingEntity.level());
                if (lightning != null) {
                    lightning.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                    livingEntity.level().addFreshEntity(lightning);
                }
            }
        } else {
            // 没有勾住时清除所有记录
            STRUCK_ENTITIES.clear();
        }
    }

    private FishingHookTickEvents() {
    }
}
