package com.chinaex123.redstone_enchants.event.sword;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.component.GamblerData;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 剑类战斗附魔在伤害事件上的统一分发器。
 * <p>行为参数由附魔 JSON 声明（见 {@link ModEnchantmentEffectComponents}），
 * 这里按固定顺序驱动各效果。旧实现是每个附魔一个独立订阅者，
 * 执行顺序取决于注册顺序且互相覆盖（均以原始伤害为基数的附魔只有一个生效），
 * 分发器固定执行顺序：赌徒 → 伏击 → （后续）背刺 → 均衡器 → 处决，生命吸取在 Post 阶段，
 * 沿用各旧公式与攻击者解析（各段自行按旧版语义判定）。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class SwordLivingDamageEvents {
    private static final EquipmentSlot[] HAND_SLOTS = { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND };

    /** 伏击每玩家状态（旧 handler 同款 Map）：记录已非潜行攻击过/已吃过潜行首击加成 */
    private static final Map<UUID, Boolean> AMBUSH_HAS_ATTACKED = new HashMap<>();

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        // 固定顺序：赌徒 → 伏击 → 背刺 → 均衡器 → 处决（各段按旧版语义自行解析攻击者）
        gamblerRoll(event);
        ambushStrike(event);
        executionKill(event);
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (attacker == null) {
            return;
        }
        lifeSteal(event, attacker);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // 如果玩家停止潜行，重置伏击标记（旧 handler 同款，双侧 tick 均执行）
        Player player = event.getEntity();
        if (!player.isCrouching()) {
            AMBUSH_HAS_ATTACKED.remove(player.getUUID());
        }
    }

    // ---- 赌徒 ----

    private static void gamblerRoll(LivingDamageEvent.Pre event) {
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (attacker == null) {
            return;
        }
        ItemStack tool = findHandStackWith(attacker, ModEnchantmentEffectComponents.GAMBLER_DATA.get());
        if (tool == null) {
            return;
        }
        GamblerData data = EnchantmentUtil.specialValue(tool, ModEnchantmentEffectComponents.GAMBLER_DATA.get());
        if (data == null) {
            return;
        }
        float originalDamage = event.getOriginalDamage();
        if (attacker.getRandom().nextFloat() < data.odds()) {
            event.setNewDamage(originalDamage * data.bonusMultiplier());
        } else {
            event.setNewDamage(originalDamage * data.penaltyMultiplier());
        }
    }

    // ---- 伏击 ----

    private static void ambushStrike(LivingDamageEvent.Pre event) {
        // 旧版用 getDirectEntity + Player 判定攻击者（弓箭等投射物不触发）
        if (!(event.getSource().getDirectEntity() instanceof Player attacker)) {
            return;
        }
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.AMBUSH_BONUS.get())) {
            return;
        }
        UUID playerId = attacker.getUUID();

        // 检查是否在潜行
        if (!attacker.isCrouching()) {
            // 不在潜行，标记为已攻击过
            AMBUSH_HAS_ATTACKED.put(playerId, true);
            return;
        }

        // 检查是否是潜行后的首次攻击
        if (AMBUSH_HAS_ATTACKED.getOrDefault(playerId, false)) {
            return;
        }

        // 潜行时的首次攻击，增加伤害（每级+20%，公式原样）
        float bonus = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.AMBUSH_BONUS.get());
        event.setNewDamage(event.getOriginalDamage() * (1 + bonus));

        // 标记为已攻击
        AMBUSH_HAS_ATTACKED.put(playerId, true);
    }

    // ---- 处决 ----

    private static void executionKill(LivingDamageEvent.Pre event) {
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (attacker == null) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.EXECUTION.get())) {
            return;
        }
        LivingEntity target = event.getEntity();
        // 旧版公式原样：目标当前生命占比 < 25% 时，把伤害设为目标当前生命值（必死）
        float healthPercent = target.getHealth() / target.getMaxHealth();
        if (healthPercent < 0.25F) {
            event.setNewDamage(target.getHealth());
        }
    }

    // ---- 生命吸取 ----

    /**
     * 生命吸取：按本次实际造成的伤害（Post 阶段最终扣血量）× 组件比例治疗攻击者。
     * <p>⚠️ 修复旧实现 bug：旧 handler 引用了不存在的附魔 ID {@code leeching}
     * （真 ID 是 {@code life_steal}），{@code getHolder} 永远为 null，该附魔此前从未生效；
     * 数值基准同时从 original 伤害改为实际伤害（Post 的 {@code getNewDamage}）。
     */
    private static void lifeSteal(LivingDamageEvent.Post event, LivingEntity attacker) {
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        float ratio = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.LIFE_STEAL_RATIO.get());
        if (ratio <= 0) {
            return;
        }
        attacker.heal(event.getNewDamage() * ratio);
    }

    /** 在主手/副手查找携带指定单值组件的物品（旧版赌徒逐槽检查到第一个即止）。 */
    private static ItemStack findHandStackWith(LivingEntity attacker, DataComponentType<?> type) {
        for (EquipmentSlot slot : HAND_SLOTS) {
            ItemStack stack = attacker.getItemBySlot(slot);
            if (!stack.isEmpty() && EnchantmentUtil.specialValue(stack, type) != null) {
                return stack;
            }
        }
        return null;
    }

    private SwordLivingDamageEvents() {
    }
}
