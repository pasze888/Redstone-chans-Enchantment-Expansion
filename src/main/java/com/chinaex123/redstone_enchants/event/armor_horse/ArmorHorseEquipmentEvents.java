package com.chinaex123.redstone_enchants.event.armor_horse;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

/**
 * 马铠（horse_armor）附魔在装备变更事件上的统一分发器。
 * <p>当前只承载精神的摘除清理：卸下马铠时移除其速度修饰符。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorHorseEquipmentEvents {
    private static final ResourceLocation SPIRIT_SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "spirit_speed");

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof AbstractHorse horse)) {
            return;
        }

        // 只处理 BODY 槽位的变化（精神摘除清理）
        if (event.getSlot() != EquipmentSlot.BODY) {
            return;
        }

        // 移除速度加成
        AttributeInstance speedAttribute = horse.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(SPIRIT_SPEED_MODIFIER_ID);
        }
    }

    private ArmorHorseEquipmentEvents() {
    }
}
