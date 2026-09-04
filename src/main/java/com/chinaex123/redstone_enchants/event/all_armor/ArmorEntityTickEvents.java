package com.chinaex123.redstone_enchants.event.all_armor;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * 全身盔甲（all_armor）附魔在实体 tick 事件上的统一分发器（昼夜流转：白天加攻、夜晚加移速）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorEntityTickEvents {
    private static final ResourceLocation DAMAGE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "daynight_cycle_damage");
    private static final ResourceLocation SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "daynight_cycle_speed");
    private static final double BONUS_PER_LEVEL = 0.05; // 每级提供 5% 加成
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 计算所有盔甲槽位的附魔等级总和
        int totalLevel = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armor = player.getItemBySlot(slot);
            if (armor.isEmpty()) {
                continue;
            }
            if (EnchantmentHelper.has(armor, ModEnchantmentEffectComponents.DAYNIGHT_CYCLE.get())) {
                totalLevel += 1;
            }
        }

        if (totalLevel <= 0) {
            // 没有附魔时移除所有修饰符
            removeModifiers(player);
            return;
        }

        // 判断是白天还是夜晚
        long dayTime = player.level().getDayTime() % 24000;
        boolean isDay = dayTime >= 0 && dayTime < 12000;

        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

        if (attackDamageAttribute != null && speedAttribute != null) {
            // 移除旧的修饰符
            removeModifiers(player);

            // 每级提供 5% 加成
            double bonus = totalLevel * BONUS_PER_LEVEL;

            if (isDay) {
                // 白天：增加伤害
                AttributeModifier damageModifier = new AttributeModifier(
                        DAMAGE_MODIFIER_ID,
                        bonus,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                );
                attackDamageAttribute.addPermanentModifier(damageModifier);
            } else {
                // 夜晚：增加移动速度
                AttributeModifier speedModifier = new AttributeModifier(
                        SPEED_MODIFIER_ID,
                        bonus,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                );
                speedAttribute.addPermanentModifier(speedModifier);
            }
        }
    }

    private static void removeModifiers(Player player) {
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

        if (attackDamageAttribute != null) {
            attackDamageAttribute.removeModifier(DAMAGE_MODIFIER_ID);
        }
        if (speedAttribute != null) {
            speedAttribute.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    private ArmorEntityTickEvents() {
    }
}
