package com.chinaex123.redstone_enchants.event.curse;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 诅咒附魔在玩家 tick 事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：腐蚀诅咒 → （后续）水源诅咒。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class CurseTickEvents {
    private static final Map<UUID, Long> LAST_DAMAGE_TIME = new HashMap<>();
    private static final long PERIOD_TICKS = 20; // 每秒执行一次（20 tick）

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // 固定顺序：腐蚀诅咒 → 水源诅咒（各段按旧版语义自行判定环境）
        curseOfRust(event);
    }

    // ---- 腐蚀诅咒 ----

    private static void curseOfRust(PlayerTickEvent.Post event) {
        LivingEntity player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            // 仅服务端执行（旧版双侧跑，客户端耐久写无效，结果行为不变）
            return;
        }

        // 检查是否在水中或雨中
        boolean inWater = player.isInWater();
        boolean inRain = player.level().isRaining() && player.level().canSeeSky(player.blockPosition());

        if (!inWater && !inRain) {
            return;
        }

        UUID entityId = player.getUUID();
        long currentTime = serverLevel.getGameTime();
        Long lastTime = LAST_DAMAGE_TIME.get(entityId);

        // 每秒执行一次（20 tick）
        if (lastTime != null && currentTime - lastTime < PERIOD_TICKS) {
            return;
        }

        LAST_DAMAGE_TIME.put(entityId, currentTime);

        // 检查所有装备槽（旧版同款：含主手/副手/BODY）
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.CURSE_OF_RUST_DURABILITY.get())) {
                continue;
            }

            // 每级每秒消耗 1 点耐久
            int amount = (int) EnchantmentUtil.itemValue(serverLevel, stack, ModEnchantmentEffectComponents.CURSE_OF_RUST_DURABILITY.get());
            stack.setDamageValue(stack.getDamageValue() + amount);
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                player.onEquippedItemBroken(stack.getItem(), slot);
                player.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    private CurseTickEvents() {
    }
}
