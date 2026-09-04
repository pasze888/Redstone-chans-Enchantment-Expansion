package com.chinaex123.redstone_enchants.event.all_shear;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;

/**
 * 剪刀（all_shear）附魔在实体交互事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：绵延不绝 → （后续）经验修剪/收获回响/牧羊人。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ShearEntityInteractEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        // 固定顺序：绵延不绝 → 经验修剪 → 收获回响 → 牧羊人（旧版为独立订阅者，顺序未定义）
        endlessWool(event);
    }

    // ---- 绵延不绝 ----

    private static void endlessWool(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        net.minecraft.world.level.Level level = player.level();

        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();

        // 检查是否是剪刀剪羊毛
        if (!(target instanceof Sheep sheep)) {
            return;
        }
        if (!stack.is(Items.SHEARS)) {
            return;
        }

        // 只在服务端处理逻辑
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 检查羊是否有羊毛（未被剪过）
        if (sheep.isSheared()) {
            return;
        }

        if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.ENDLESS_WOOL_REGROW_CHANCE.get())) {
            return;
        }

        // 每级 10% 概率让羊重新长出羊毛
        RandomSource random = level.getRandom();
        double probability = EnchantmentUtil.itemValue(serverLevel, stack, ModEnchantmentEffectComponents.ENDLESS_WOOL_REGROW_CHANCE.get());

        if (probability >= 1.0 || random.nextDouble() < probability) {
            // 延迟执行，确保剪切完成后再生长的
            Objects.requireNonNull(level.getServer()).execute(() -> {
                sheep.setSheared(false);

                // 发送数据包让客户端显示粒子
                serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        sheep.getX(), sheep.getY() + 1, sheep.getZ(),
                        10, 0.5, 0.5, 0.5, 0.1
                );
            });
        }
    }

    private ShearEntityInteractEvents() {
    }
}
