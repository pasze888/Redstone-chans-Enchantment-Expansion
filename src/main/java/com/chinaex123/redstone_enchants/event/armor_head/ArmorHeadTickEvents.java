package com.chinaex123.redstone_enchants.event.armor_head;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

/**
 * 头盔（armors_head）附魔在实体 tick 事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：矿工 → 以寡敌众 → （后续）反伪装/绝境逆袭。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorHeadTickEvents {
    private static final ResourceLocation AAO_DAMAGE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "against_all_odds_damage");
    private static final ResourceLocation AAO_ARMOR_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "against_all_odds_armor");
    private static final ResourceLocation DC_DAMAGE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "desperate_counter_damage");
    private static final double AAO_DETECTION_RANGE = 8.0;
    private static final double AC_DETECTION_RANGE = 16.0; // 反伪装检测范围 16 格
    private static final int AC_BASE_DURATION = 40; // 反伪装发光基础时长（tick）

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        // 固定顺序：矿工 → 以寡敌众 → 反伪装 → 绝境逆袭（旧版为独立订阅者，顺序未定义）
        adaptive(event);
        againstAllOdds(event);
        antiCamouflage(event);
        desperateCounter(event);
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

    // ---- 以寡敌众 ----

    private static void againstAllOdds(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            // 属性修饰符以服务端为准，客户端由属性同步获得
            return;
        }

        // 检查头盔是否有以寡敌众附魔
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(helmet, ModEnchantmentEffectComponents.AGAINST_ALL_ODDS_BONUS_PER_ENEMY.get())) {
            return;
        }
        float bonusPerEnemy = EnchantmentUtil.itemValue(serverLevel, helmet,
                ModEnchantmentEffectComponents.AGAINST_ALL_ODDS_BONUS_PER_ENEMY.get());
        if (bonusPerEnemy <= 0) {
            return;
        }

        // 获取范围内的敌对生物数量（修复：已死亡实体不再计入，旧版无过滤）
        List<Monster> nearbyEnemies = player.level().getEntitiesOfClass(
                Monster.class,
                player.getBoundingBox().inflate(AAO_DETECTION_RANGE),
                net.minecraft.world.entity.LivingEntity::isAlive
        );
        int enemyCount = nearbyEnemies.size();

        // 获取属性
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);

        if (attackDamageAttribute != null && armorAttribute != null) {
            // 移除旧的修饰符
            attackDamageAttribute.removeModifier(AAO_DAMAGE_MODIFIER_ID);
            armorAttribute.removeModifier(AAO_ARMOR_MODIFIER_ID);

            if (enemyCount > 0) {
                // 每多一个敌人，伤害/护甲 +2%（每级）
                double bonus = enemyCount * bonusPerEnemy;

                AttributeModifier damageModifier = new AttributeModifier(
                        AAO_DAMAGE_MODIFIER_ID,
                        bonus,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                );
                attackDamageAttribute.addPermanentModifier(damageModifier);

                AttributeModifier armorModifier = new AttributeModifier(
                        AAO_ARMOR_MODIFIER_ID,
                        bonus,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                );
                armorAttribute.addPermanentModifier(armorModifier);
            }
        }
    }

    // ---- 反伪装 ----

    private static void antiCamouflage(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            // 效果施加以服务端为准，客户端由效果同步获得
            return;
        }

        // 检查头盔是否有反伪装附魔
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(helmet, ModEnchantmentEffectComponents.ANTI_CAMOUFLAGE_DURATION_BONUS.get())) {
            return;
        }

        // 只在潜行时生效
        if (!player.isShiftKeyDown()) {
            return;
        }

        // 获取范围内的所有敌对生物
        List<Monster> nearbyMonsters = player.level().getEntitiesOfClass(
                Monster.class, player.getBoundingBox().inflate(AC_DETECTION_RANGE)
        );

        // 给每个敌对生物施加发光效果（40 + 10×级 tick）
        float durationBonus = EnchantmentUtil.itemValue(serverLevel, helmet,
                ModEnchantmentEffectComponents.ANTI_CAMOUFLAGE_DURATION_BONUS.get());
        int duration = AC_BASE_DURATION + (int) durationBonus;
        for (Monster monster : nearbyMonsters) {
            monster.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, 0, false, false));
        }
    }

    // ---- 绝境逆袭 ----

    private static void desperateCounter(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            // 属性修饰符以服务端为准，客户端由属性同步获得
            return;
        }

        // 检查头盔是否有绝境逆袭附魔
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(helmet, ModEnchantmentEffectComponents.DESPERATE_COUNTER_DAMAGE.get())) {
            return;
        }

        // 检查是否有失明或黑暗效果
        boolean hasBlindness = player.hasEffect(MobEffects.BLINDNESS);
        boolean hasDarkness = player.hasEffect(MobEffects.DARKNESS);

        // 获取攻击伤害属性
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamageAttribute == null) {
            return;
        }

        // 移除旧的修饰符
        attackDamageAttribute.removeModifier(DC_DAMAGE_MODIFIER_ID);

        // 如果有失明或黑暗，添加伤害加成（0.25×级）
        if (hasBlindness || hasDarkness) {
            float damagePerLevel = EnchantmentUtil.itemValue(serverLevel, helmet,
                    ModEnchantmentEffectComponents.DESPERATE_COUNTER_DAMAGE.get());
            AttributeModifier modifier = new AttributeModifier(
                    DC_DAMAGE_MODIFIER_ID,
                    damagePerLevel,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
            attackDamageAttribute.addPermanentModifier(modifier);
        }
    }

    private ArmorHeadTickEvents() {
    }
}
