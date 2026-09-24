package com.chinaex123.redstone_enchants.event.sword;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.component.GamblerData;
import com.chinaex123.redstone_enchants.init.ModAttachments;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 剑类战斗附魔在伤害事件上的统一分发器。
 * <p>行为参数由附魔 JSON 声明（见 {@link ModEnchantmentEffectComponents}），
 * 这里按固定顺序驱动各效果。旧实现是每个附魔一个独立订阅者，
 * 执行顺序取决于注册顺序且互相覆盖（均以原始伤害为基数的附魔只有一个生效），
 * 分发器固定执行顺序：Pre {@code 赌徒 → 伏击 → 背刺 → 均衡器}，Post {@code 处决 → 生命吸取}，
 * 沿用各旧公式与攻击者解析（各段自行按旧版语义判定）。
 * <p>伤害基数统一为 {@code getNewDamage()} 连乘：以 {@code getOriginalDamage()} 覆盖会抹掉
 * Pre 之前已算入 newDamage 的暴击（Apothic Attributes 在 {@code LivingIncomingDamageEvent} 结算）
 * 与护甲/抗性/保护减伤。
 * <p>处决（2026-09-24 改）：从 Pre 的 {@code setNewDamage(当前生命)} 改为 Post 判"这一刀结算后血量 < 25%"
 * 并 {@code setHealth(0)}。Post 在 {@code die()} 之前触发，原版随后照常走图腾判定与死亡处理，
 * 因此图腾/死亡消息/击杀归属/经验/掉落全部保持原版；代价是语义由"两刀"变为"一击补刀"。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class SwordLivingDamageEvents {
    private static final EquipmentSlot[] HAND_SLOTS = { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND };

    // LOW：武器族之一，排在锤/弓之后；各段以 getNewDamage() 连乘
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        // 固定顺序：赌徒 → 伏击 → 背刺 → 均衡器（各段按旧版语义自行解析攻击者）
        gamblerRoll(event);
        ambushStrike(event);
        backstab(event);
        equalizer(event);
    }

    // HIGHEST：处决须先于 ArmorDamageEvents 的重生护盾（LOWEST）执行；生命吸取只治疗攻击者，顺序无影响
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (attacker == null) {
            return;
        }
        // 固定顺序：处决 → 生命吸取（处决只清血，不改写本次伤害）
        executionKill(event, attacker);
        lifeSteal(event, attacker);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // 如果玩家停止潜行，复位伏击标记（双侧 tick 均执行）
        Player player = event.getEntity();
        if (!player.isCrouching()) {
            player.setData(ModAttachments.AMBUSH_HAS_ATTACKED.get(), Boolean.FALSE);
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
        float baseDamage = event.getNewDamage();
        if (attacker.getRandom().nextFloat() < data.odds()) {
            event.setNewDamage(baseDamage * data.bonusMultiplier());
        } else {
            event.setNewDamage(baseDamage * data.penaltyMultiplier());
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
        // 检查是否在潜行
        if (!attacker.isCrouching()) {
            // 不在潜行，标记为已攻击过
            attacker.setData(ModAttachments.AMBUSH_HAS_ATTACKED.get(), Boolean.TRUE);
            return;
        }

        // 检查是否是潜行后的首次攻击
        if (attacker.getData(ModAttachments.AMBUSH_HAS_ATTACKED.get())) {
            return;
        }

        // 潜行时的首次攻击，增加伤害（每级+20%，以 getNewDamage() 连乘）
        float bonus = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.AMBUSH_BONUS.get());
        event.setNewDamage(event.getNewDamage() * (1 + bonus));

        // 标记为已攻击
        attacker.setData(ModAttachments.AMBUSH_HAS_ATTACKED.get(), Boolean.TRUE);
    }

    // ---- 背刺 ----

    private static void backstab(LivingDamageEvent.Pre event) {
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (attacker == null) {
            return;
        }
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.BACKSTAB_BEHIND_BONUS.get())) {
            return;
        }
        LivingEntity target = event.getEntity();

        // 计算攻击者相对于目标的方向向量
        double dx = target.getX() - attacker.getX();
        double dz = target.getZ() - attacker.getZ();

        // 计算目标朝向的单位向量
        float targetYawRad = target.getYRot() * (float) Math.PI / 180.0f;
        float targetLookX = -Mth.sin(targetYawRad);
        float targetLookZ = Mth.cos(targetYawRad);

        // 计算点积：>0 表示攻击者在目标背后，<0 表示在正面
        double dotProduct = dx * targetLookX + dz * targetLookZ;

        // 旧版公式：背后每级+30%，正面（含正侧方）每级-15%，以 getNewDamage() 连乘
        if (dotProduct > 0) {
            float behind = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.BACKSTAB_BEHIND_BONUS.get());
            event.setNewDamage(event.getNewDamage() * (1 + behind));
        } else {
            float front = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.BACKSTAB_FRONT_PENALTY.get());
            event.setNewDamage(event.getNewDamage() * (1 - front));
        }
    }

    // ---- 均衡器 ----

    private static void equalizer(LivingDamageEvent.Pre event) {
        // 旧版用 getDirectEntity + Player 判定攻击者
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
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.EQUALIZER_BONUS.get())) {
            return;
        }
        LivingEntity target = event.getEntity();

        // 计算目标血量百分比
        float maxHealth = target.getMaxHealth();
        float currentHealth = target.getHealth();
        float healthPercentage = currentHealth / maxHealth;

        // 旧版公式：伤害 × (1 + hp% × 0.2×级)，基数改为 getNewDamage() 连乘
        float bonusMultiplier = healthPercentage * EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.EQUALIZER_BONUS.get());
        event.setNewDamage(event.getNewDamage() * (1 + bonusMultiplier));
    }

    // ---- 处决 ----

    /**
     * 处决：这一刀结算后目标血量占比低于 25% 时清空血量。
     * <p>判在 {@link LivingDamageEvent.Post}——此时血量已扣、{@code die()} 尚未调用，所以门槛是"扣血后的真实血量"，
     * 剩余吸收也救不了（吸收已在 Post 之前结算完）。{@code setHealth(0)} 之后原版会自己走
     * {@code checkTotemDeathProtection} → {@code die()}，图腾/死亡消息/击杀归属/经验/掉落均为原版行为。
     * <p>与挂在同一事件上的重生护盾（{@code ArmorDamageEvents}，标 LOWEST）配合：处决先把血量归零，
     * 护盾最后执行、看到濒死状态后拉回 0.5 血并消耗附魔，即**重生护盾救得下被处决的目标**。
     */
    private static void executionKill(LivingDamageEvent.Post event, LivingEntity attacker) {
        LivingEntity target = event.getEntity();
        // 这一刀已经打死则交给原版；扣血为 0（如完全被盾牌格挡且无敌帧已过）不算命中
        if (target.isDeadOrDying() || event.getNewDamage() <= 0.0F) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.EXECUTION.get())) {
            return;
        }
        if (target.getHealth() / target.getMaxHealth() < 0.25F) {
            target.setHealth(0.0F);
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
