package com.chinaex123.redstone_enchants.event.unbreaking;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModAttachments;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * unbreaking 家族在玩家侧钩子上的统一分发器（保全：破损特效/阻止挖掘/tooltip 提示）。
 * <p>保全的主功能（耐久归零不消失）在 {@code mixin/PreservationMixin}，同样读 PRESERVATION 标记组件。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class UnbreakingPlayerEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel)) {
            // 音效与粒子以服务端为准，客户端不做（旧实现双侧跑，效果会放两遍）
            return;
        }

        // 上一次见到的耐久随玩家实体生命周期走（附件），不再用静态 Map
        Map<Integer, Integer> lastDamageMap = player.getData(ModAttachments.PRESERVATION_LAST_DAMAGE.get());
        Set<Integer> seen = new HashSet<>();

        // 检查所有物品栏位（旧 handler 同款：仅背包 items，不含盔甲栏）
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) {
                continue;
            }
            if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.PRESERVATION.get())) {
                continue;
            }

            int currentDamage = stack.getDamageValue();
            int maxDamage = stack.getMaxDamage();
            int stackKey = System.identityHashCode(stack);
            seen.add(stackKey);

            // 检测是否刚达到最大耐久
            if (currentDamage >= maxDamage) {
                Integer lastDamage = lastDamageMap.get(stackKey);
                if (lastDamage == null || lastDamage < maxDamage) {
                    // 刚达到最大耐久，播放音效和粒子
                    player.playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F);

                    ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, stack);
                    for (int i = 0; i < 5; i++) {
                        double dx = (player.getRandom().nextDouble() - 0.5) * 0.5;
                        double dy = player.getRandom().nextDouble() * 0.5;
                        double dz = (player.getRandom().nextDouble() - 0.5) * 0.5;
                        player.level().addParticle(particle,
                                player.getX(), player.getY() + 1.5, player.getZ(),
                                dx, dy, dz);
                    }
                }
            }

            lastDamageMap.put(stackKey, currentDamage);
        }

        // 背包里已经没有的物品不再跟踪：条目数随背包大小有界
        lastDamageMap.keySet().retainAll(seen);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.PRESERVATION.get())) {
            return;
        }

        // 如果耐久为最大值（0 耐久状态），阻止挖掘
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // getEntity() 可能为 null（如 JEI 搜索时）
        if (event.getEntity() == null) {
            return;
        }
        if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.PRESERVATION.get())) {
            return;
        }

        // 如果耐久为最大值，显示已损坏提示
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            event.getToolTip().add(Component.translatable("tooltip.redstone_enchants.preservation_broken"));
        }
    }

    private UnbreakingPlayerEvents() {
    }
}
