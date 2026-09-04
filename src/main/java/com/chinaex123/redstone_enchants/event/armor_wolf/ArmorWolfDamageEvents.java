package com.chinaex123.redstone_enchants.event.armor_wolf;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

/**
 * 狼铠（wolf_armor）附魔在伤害事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：狼群领袖 → 追踪者。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorWolfDamageEvents {
    private static final double PACK_LEADER_RANGE = 16.0; // 同伴狼检测范围
    private static final int TRACKER_BASE_DURATION = 80; // 追踪者发光基础时长（tick）

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        // 固定顺序：狼群领袖 → 追踪者（旧版为独立订阅者，顺序未定义）
        packLeader(event);
        tracker(event);
    }

    // ---- 狼群领袖 ----

    private static void packLeader(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getDirectEntity() instanceof Wolf wolf)) {
            return;
        }

        // 检查狼是否有主人
        if (!wolf.isTame()) {
            return;
        }

        Player owner = (Player) wolf.getOwner();
        if (owner == null) {
            return;
        }

        // 检查狼铠是否有狼群领袖附魔
        ItemStack armor = wolf.getItemBySlot(EquipmentSlot.BODY);
        if (armor.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(armor, ModEnchantmentEffectComponents.PACK_LEADER_DAMAGE_BONUS.get())) {
            return;
        }
        if (!(wolf.level() instanceof ServerLevel serverLevel)) {
            // 伤害计算以服务端为准
            return;
        }

        // 计算附近的狼数量（16 格范围内）
        List<Wolf> nearbyWolves = wolf.level().getEntitiesOfClass(Wolf.class, wolf.getBoundingBox().inflate(PACK_LEADER_RANGE));

        // 排除自己，计算其他狼的数量
        int wolfCount = nearbyWolves.size() - 1;
        if (wolfCount <= 0) {
            return;
        }

        // 计算伤害加成：每级每只狼 × 组件值
        float bonusPerWolf = EnchantmentUtil.itemValue(serverLevel, armor,
                ModEnchantmentEffectComponents.PACK_LEADER_DAMAGE_BONUS.get());
        double damageBonus = wolfCount * bonusPerWolf;

        // 应用伤害加成
        event.setNewDamage((float) (event.getNewDamage() * (1 + damageBonus)));
    }

    // ---- 追踪者 ----

    private static void tracker(LivingDamageEvent.Pre event) {
        Entity directEntity = event.getSource().getDirectEntity();

        if (!(directEntity instanceof Wolf wolf)) {
            return;
        }

        LivingEntity target = event.getEntity();

        // 检查狼铠是否有追踪者附魔（狼铠在 BODY 槽位）
        ItemStack armor = wolf.getItemBySlot(EquipmentSlot.BODY);
        if (armor.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(armor, ModEnchantmentEffectComponents.TRACKER_GLOW_DURATION_BONUS.get())) {
            return;
        }
        if (!(wolf.level() instanceof ServerLevel serverLevel)) {
            // 效果施加以服务端为准
            return;
        }

        // 给目标施加发光效果（80 + 20×级 tick）
        float durationBonus = EnchantmentUtil.itemValue(serverLevel, armor,
                ModEnchantmentEffectComponents.TRACKER_GLOW_DURATION_BONUS.get());
        int duration = TRACKER_BASE_DURATION + (int) durationBonus;
        target.addEffect(new MobEffectInstance(
                MobEffects.GLOWING, duration, 0, false, false
        ));
    }

    private ArmorWolfDamageEvents() {
    }
}
