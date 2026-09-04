package com.chinaex123.redstone_enchants.event.projectile;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * 传送（teleport）附魔：投射物命中实体后，射手与目标互换位置（双向 tp），
 * 两端各播末影人传送音效 + 反向传送门粒子。
 * <p>原实现是三条 post_attack 声明 + data storage 跨函数传坐标 + {@code $} 宏拼接
 * tp 命令（hostile_int / player_int / hostile_tp 等 5 个 mcfunction）；坐标交换
 * 强依赖存储时序，改在伤害事件里直接交换，语义一致且更直观。
 * <p>触发条件与原声明一致：伤害直击实体是投射物（#redstone_enchants:projectiles：
 * 原版箭系 + 三叉戟）、受害者为生物；附魔检查从伤害来源武器（三叉戟投射物自带）
 * 或射手主手（弓）读取。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class TeleportSwapEvents {

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }
        Entity directEntity = event.getSource().getDirectEntity();
        if (!(directEntity instanceof AbstractArrow projectile) || !(projectile.getOwner() instanceof LivingEntity shooter)) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (victim == shooter) {
            return;
        }

        // 三叉戟投射物自带物品堆（含附魔）；箭矢则查射手主手的弓
        ItemStack weapon = projectile instanceof ThrownTrident trident
                ? trident.getPickupItemStackOrigin()
                : shooter.getMainHandItem();
        if (weapon.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(
                EnchantmentUtil.holder(level.registryAccess(), ModEnchantments.TELEPORT), weapon) <= 0) {
            return;
        }

        swapPositions(level, shooter, victim);
    }

    private static void swapPositions(ServerLevel level, LivingEntity shooter, LivingEntity victim) {
        double shooterX = shooter.getX(), shooterY = shooter.getY(), shooterZ = shooter.getZ();

        shooter.teleportTo(victim.getX(), victim.getY(), victim.getZ());
        playTeleportEffects(level, shooter);

        victim.teleportTo(shooterX, shooterY, shooterZ);
        playTeleportEffects(level, victim);
    }

    private static void playTeleportEffects(ServerLevel level, LivingEntity target) {
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 0.5F, 0.5F);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                target.getX(), target.getY() + 0.5, target.getZ(), 100, 0.3, 0.7, 0.3, 0.0);
    }

    private TeleportSwapEvents() {
    }
}
