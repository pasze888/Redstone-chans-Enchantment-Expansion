package com.chinaex123.redstone_enchants.event.all_armor;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.AttributeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
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
    private static final double BONUS_PER_LEVEL = 0.05; // 每件带该附魔的盔甲提供 5% 加成
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
        if (!(player.level() instanceof ServerLevel)) {
            // 属性修饰符以服务端为准，客户端由属性同步获得
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

        // 判断是白天还是夜晚
        long dayTime = player.level().getDayTime() % 24000;
        boolean isDay = dayTime >= 0 && dayTime < 12000;

        // 白天加攻、夜晚加移速；没有附魔时两个修饰符都不该存在。
        // 值没变就不写（写脏会触发重算与同步，见 AttributeUtil）。
        double bonus = totalLevel * BONUS_PER_LEVEL;
        Double damage = totalLevel > 0 && isDay ? bonus : null;
        Double speed = totalLevel > 0 && !isDay ? bonus : null;

        AttributeUtil.applyPermanent(player, Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_ID, damage,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        AttributeUtil.applyPermanent(player, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID, speed,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private ArmorEntityTickEvents() {
    }
}
