package com.chinaex123.redstone_enchants.event.tool;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * 方块放置钩子上附魔效果的统一分发器（湿润）。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ToolBlockPlaceEvents {
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getLevel().isClientSide()) {
            return;
        }
        ItemStack tool = player.getMainHandItem();
        if (!EnchantmentHelper.has(tool, ModEnchantmentEffectComponents.MOIST.get())) {
            return;
        }
        BlockState state = event.getState();
        if (state.getBlock() == Blocks.FARMLAND) {
            event.getLevel().setBlock(event.getPos(), state.setValue(FarmBlock.MOISTURE, 7), 2);
        }
    }

    private ToolBlockPlaceEvents() {
    }
}
