package com.chinaex123.redstone_enchants.event.all_shear;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * 剪刀（all_shear）附魔在方块交互事件上的统一分发器（牧羊人：采蜜时额外掉落蜜脾）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类同时挂两个事件，本分发器按相同主题拆分两个钩子。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ShearBlockInteractEvents {
    private static final int MIN_HONEY_LEVEL = 5; // 满蜜等级
    private static final int BASE_HONEYCOMB_COUNT = 3; // 基础蜜脾掉落数

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack stack = player.getItemInHand(event.getHand());
        if (!stack.is(Items.SHEARS)) {
            return;
        }

        BlockState state = level.getBlockState(event.getPos());
        if (!(state.getBlock() instanceof BeehiveBlock)) {
            return;
        }
        if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.SHEPHERD_EXTRA_CHANCE.get())) {
            return;
        }

        // 检查是否有蜂蜜
        int honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);
        if (honeyLevel < MIN_HONEY_LEVEL) {
            return;
        }

        // 取消原事件
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        // 生成基础蜜脾掉落（3 个）
        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB, BASE_HONEYCOMB_COUNT);
        ItemEntity itemEntity = new ItemEntity(level, event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5, honeycomb.copy());
        level.addFreshEntity(itemEntity);

        // 概率额外掉落（每级 20%；额外数量 = 附魔等级）
        RandomSource random = level.getRandom();
        double probability = EnchantmentUtil.itemValue(serverLevel, stack, ModEnchantmentEffectComponents.SHEPHERD_EXTRA_CHANCE.get());

        if (probability >= 1.0 || random.nextDouble() < probability) {
            int enchantLevel = EnchantmentUtil.levelOn(
                    EnchantmentUtil.holder(level.registryAccess(), ModEnchantments.SHEPHERD), stack);
            ItemStack extraDrop = honeycomb.copyWithCount(enchantLevel);
            ItemEntity extraEntity = new ItemEntity(level, event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5, extraDrop);
            level.addFreshEntity(extraEntity);
        }

        // 降低蜂蜜等级
        level.setBlockAndUpdate(event.getPos(), state.setValue(BeehiveBlock.HONEY_LEVEL, 0));

        // 消耗耐久
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(event.getHand()));
    }

    private ShearBlockInteractEvents() {
    }
}
