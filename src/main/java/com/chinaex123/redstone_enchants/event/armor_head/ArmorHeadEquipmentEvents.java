package com.chinaex123.redstone_enchants.event.armor_head;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

/**
 * 头盔（armors_head）附魔在装备变更事件上的统一分发器。
 * <p>当前只承载以寡敌众的摘除清理：卸下头盔时移除其属性修饰符。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorHeadEquipmentEvents {
    private static final ResourceLocation AAO_DAMAGE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "against_all_odds_damage");
    private static final ResourceLocation AAO_ARMOR_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "against_all_odds_armor");

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 只处理头部槽位的变化（以寡敌众摘除清理）
        if (event.getSlot() != EquipmentSlot.HEAD) {
            return;
        }

        // 移除属性加成
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);

        if (attackDamageAttribute != null) {
            attackDamageAttribute.removeModifier(AAO_DAMAGE_MODIFIER_ID);
        }
        if (armorAttribute != null) {
            armorAttribute.removeModifier(AAO_ARMOR_MODIFIER_ID);
        }
    }

    private ArmorHeadEquipmentEvents() {
    }
}
