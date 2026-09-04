package com.chinaex123.redstone_enchants.event.flint_and_steel;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * 打火石（flint_and_steel）附魔在交互事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是每个附魔一个独立订阅者。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class FlintAndSteelInteractEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack tool = player.getMainHandItem();

        if (tool.isEmpty() || !tool.is(Items.FLINT_AND_STEEL)) {
            return;
        }
        if (!EnchantmentHelper.has(tool, ModEnchantmentEffectComponents.SEARING_FIRE_TICKS.get())) {
            return;
        }

        int fireTicks = (int) EnchantmentUtil.itemValue(serverLevel, tool, ModEnchantmentEffectComponents.SEARING_FIRE_TICKS.get());
        float damage = EnchantmentUtil.itemValue(serverLevel, tool, ModEnchantmentEffectComponents.SEARING_DAMAGE.get());

        target.setRemainingFireTicks(fireTicks);

        target.hurt(player.level().damageSources().onFire(), damage);

        // 播放使用动画
        player.swing(InteractionHand.MAIN_HAND);

        // 消耗 1 点耐久
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    private FlintAndSteelInteractEvents() {
    }
}
