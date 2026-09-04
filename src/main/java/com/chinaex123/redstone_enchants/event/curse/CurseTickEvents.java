package com.chinaex123.redstone_enchants.event.curse;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：腐蚀诅咒 → 水源诅咒。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class CurseTickEvents {
    private static final Map<UUID, Long> LAST_DAMAGE_TIME = new HashMap<>();
    private static final Map<UUID, Long> LAST_EFFECT_TIME = new HashMap<>();
    private static final long PERIOD_TICKS = 20; // 每秒执行一次（20 tick）
    private static final int POISON_DURATION_TICKS = 40; // 中毒效果时长

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // 固定顺序：腐蚀诅咒 → 水源诅咒（各段按旧版语义自行判定环境）
        curseOfRust(event);
        curseOfWaterSource(event);
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

    // ---- 水源诅咒 ----

    private static void curseOfWaterSource(PlayerTickEvent.Post event) {
        LivingEntity player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            // 仅服务端执行（旧版双侧跑，客户端效果写无效，结果行为不变）
            return;
        }

        // 检查是否在水中或雨中
        boolean inWater = player.isInWater();
        boolean inRain = player.level().isRaining() && player.level().canSeeSky(player.blockPosition());

        if (!inWater && !inRain) {
            // 离开水源时移除中毒
            // （旧 handler 同款副作用：会移除其它来源施加的中毒效果，原样保留）
            if (player.hasEffect(MobEffects.POISON)) {
                player.removeEffect(MobEffects.POISON);
            }
            return;
        }

        UUID entityId = player.getUUID();
        long currentTime = serverLevel.getGameTime();
        Long lastTime = LAST_EFFECT_TIME.get(entityId);

        // 每秒执行一次（20 tick）
        if (lastTime != null && currentTime - lastTime < PERIOD_TICKS) {
            return;
        }

        LAST_EFFECT_TIME.put(entityId, currentTime);

        // 检查所有装备槽（旧版同款：含主手/副手/BODY）
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.CURSE_OF_WATER_SOURCE.get())) {
                continue;
            }

            // 施加中毒效果
            player.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, 0));
            break;
        }
    }

    private CurseTickEvents() {
    }
}
