package com.chinaex123.redstone_enchants.event.all_shear;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Objects;

/**
 * 剪刀（all_shear）附魔在实体交互事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：绵延不绝 → 经验修剪 → （后续）收获回响/牧羊人。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ShearEntityInteractEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        // 固定顺序：绵延不绝 → 经验修剪 → 收获回响 → 牧羊人（旧版为独立订阅者，顺序未定义）
        endlessWool(event);
        experienceShear(event);
        harvestEcho(event);
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

    // ---- 经验修剪 ----

    private static void experienceShear(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();

        if (!(target instanceof Sheep sheep)) {
            return;
        }
        if (!stack.is(Items.SHEARS)) {
            return;
        }
        if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.EXPERIENCE_SHEAR_EXP_PER_LEVEL.get())) {
            return;
        }

        if (!sheep.isShearable(player, stack, level, sheep.blockPosition())) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        List<ItemStack> drops = sheep.onSheared(player, stack, level, sheep.blockPosition());

        RandomSource random = level.getRandom();

        // 根据附魔等级生成经验球，每级增加经验数量（每份 1 到 3×级 的随机经验）
        float expPerLevel = EnchantmentUtil.itemValue(serverLevel, stack, ModEnchantmentEffectComponents.EXPERIENCE_SHEAR_EXP_PER_LEVEL.get());
        for (ItemStack drop : drops) {
            // 不掉落羊毛，改为掉落经验球
            int expAmount = random.nextInt((int) expPerLevel) + 1;
            ExperienceOrb.award(serverLevel, sheep.position(), expAmount);
        }

        sheep.gameEvent(GameEvent.SHEAR, player);
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(event.getHand()));

        // 旧 handler 内嵌的收获回响检查（同一次剪毛附带生命恢复）
        if (EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.HARVEST_ECHO.get())) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, true));
        }
    }

    // ---- 收获回响 ----

    private static void harvestEcho(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.SHEARS)) {
            return;
        }
        if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.HARVEST_ECHO.get())) {
            return;
        }

        // 给予 1 级生命恢复效果 3 秒
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, true));
    }

    private ShearEntityInteractEvents() {
    }
}
