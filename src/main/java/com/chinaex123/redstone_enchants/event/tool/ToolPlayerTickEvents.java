package com.chinaex123.redstone_enchants.event.tool;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 手持工具类附魔的每 tick 效果分发器：磁力吸附与连锁急迫衰减。
 * <p>只在服务端逻辑执行（旧实现在客户端 tick 也跑一遍，会产生无效的速度扰动）。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ToolPlayerTickEvents {
    private static final double MAGNET_RANGE_CAP = 16.0;
    private static final double MAGNET_PULL_ACCELERATION_PER_LEVEL = 0.08;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        ToolBlockBreakEvents.decayChainHaste(player);
        applyMagnet(player, level);
    }

    private static void applyMagnet(ServerPlayer player, ServerLevel level) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        float mainRange = EnchantmentUtil.itemValue(level, mainHand, ModEnchantmentEffectComponents.MAGNET_RANGE.get());
        float offRange = EnchantmentUtil.itemValue(level, offHand, ModEnchantmentEffectComponents.MAGNET_RANGE.get());

        ItemStack source = mainHand;
        float range = mainRange;
        if (offRange > mainRange) {
            source = offHand;
            range = offRange;
        }
        if (range <= 0) {
            return;
        }
        range = (float) Math.min(range, MAGNET_RANGE_CAP);

        Holder<Enchantment> magnet = EnchantmentUtil.holder(level.registryAccess(), ModEnchantments.MAGNET);
        int enchantLevel = EnchantmentUtil.levelOn(magnet, source);
        if (enchantLevel <= 0) {
            return;
        }

        double pullSpeed = MAGNET_PULL_ACCELERATION_PER_LEVEL * enchantLevel;
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(range))) {
            if (item.isRemoved() || item.hasPickUpDelay()) {
                continue;
            }
            double dx = player.getX() - item.getX();
            double dy = player.getY() + 1.0 - item.getY();
            double dz = player.getZ() - item.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance < 0.5 || distance > range) {
                continue;
            }
            // 距离越近吸力越弱，70% 新方向 + 30% 旧运动，避免抽搐
            double speed = pullSpeed * (distance / range);
            var motion = item.getDeltaMovement();
            item.setDeltaMovement(
                    motion.x * 0.3 + dx / distance * speed * 0.7,
                    motion.y * 0.3 + dy / distance * speed * 0.7 + 0.02,
                    motion.z * 0.3 + dz / distance * speed * 0.7);
            item.hurtMarked = true;
        }
    }

    private ToolPlayerTickEvents() {
    }
}
