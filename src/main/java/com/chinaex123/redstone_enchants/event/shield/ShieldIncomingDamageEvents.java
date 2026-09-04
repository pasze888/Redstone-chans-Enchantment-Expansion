package com.chinaex123.redstone_enchants.event.shield;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * 盾牌（shield）附魔在受击事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是每个附魔一个独立订阅者。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ShieldIncomingDamageEvents {
    private static final ResourceLocation ECHOES_BATTLE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RedstoneEnchants.MOD_ID, "echoes_battle_bonus");

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        // 检测玩家格挡（伤害被盾牌减免时触发）
        if (target instanceof Player player && player.isBlocking()) {
            ItemStack shield = player.getUseItem();
            if (!shield.isEmpty() && EnchantmentHelper.has(shield, ModEnchantmentEffectComponents.ECHOES_BATTLE.get())) {
                applyAttackDamageBonus(player, shield);
                return;
            }
        }

        // 检测玩家攻击，移除属性加成
        if (event.getSource().getDirectEntity() instanceof Player attacker) {
            removeAttackDamageBonus(attacker);
        }
    }

    private static void applyAttackDamageBonus(Player player, ItemStack shield) {
        AttributeInstance attribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }

        // 移除旧 modifier
        attribute.removeModifier(ECHOES_BATTLE_MODIFIER_ID);

        // 添加新 modifier（每级 +20%）
        int level = EnchantmentUtil.levelOn(
                EnchantmentUtil.holder(player.level().registryAccess(), ModEnchantments.ECHOES_BATTLE), shield);
        double bonus = 0.2 * level;
        AttributeModifier modifier = new AttributeModifier(
                ECHOES_BATTLE_MODIFIER_ID,
                bonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        attribute.addTransientModifier(modifier);
    }

    private static void removeAttackDamageBonus(Player player) {
        AttributeInstance attribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }

        attribute.removeModifier(ECHOES_BATTLE_MODIFIER_ID);
    }

    private ShieldIncomingDamageEvents() {
    }
}
