package com.chinaex123.redstone_enchants.event.trident;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

/**
 * 三叉戟（trident）附魔在命中事件上的统一分发器（海风：风压爆炸）。
 * <p>行为参数由附魔 JSON 组件声明；爆炸半径/击退强度为固定物理常量（不随等级缩放），留在代码里。
 * 旧实现是单个订阅者类同时挂两个事件，本分发器按相同主题合并两个钩子。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class TridentImpactEvents {
    private static final float EXPLOSION_RADIUS = 2.0F; // 爆炸半径
    private static final float KNOCKBACK_HORIZONTAL = 1.2F; // 水平击退强度
    private static final float KNOCKBACK_VERTICAL = 0.3F; // 垂直击退强度

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        // 旧版语义：直击实体为箭（三叉戟），拥有者主手必须仍是三叉戟且带附魔
        Entity directEntity = event.getSource().getDirectEntity();
        if (!(directEntity instanceof AbstractArrow arrow)) {
            return;
        }
        Entity owner = arrow.getOwner();
        if (!(owner instanceof LivingEntity attacker)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty() || !weapon.is(Items.TRIDENT)) {
            return;
        }
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.SEA_BREEZE.get())) {
            return;
        }
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity target = event.getEntity();
        Vec3 explosionPos = target.position().add(0, target.getBbHeight() / 2, 0);

        float damage = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.SEA_BREEZE_DAMAGE.get());

        AABB explosionBox = new AABB(
                explosionPos.x - EXPLOSION_RADIUS,
                explosionPos.y - EXPLOSION_RADIUS,
                explosionPos.z - EXPLOSION_RADIUS,
                explosionPos.x + EXPLOSION_RADIUS,
                explosionPos.y + EXPLOSION_RADIUS,
                explosionPos.z + EXPLOSION_RADIUS
        );

        List<Entity> affectedEntities = attacker.level().getEntities(target, explosionBox,
                entity -> entity instanceof LivingEntity && entity != attacker);

        for (Entity entity : affectedEntities) {
            double distance = entity.position().distanceTo(explosionPos);
            float distanceMultiplier = (float) Math.max(0, 1.0 - (distance / EXPLOSION_RADIUS));
            float finalDamage = damage * distanceMultiplier;

            if (finalDamage > 0) {
                entity.hurt(event.getSource(), finalDamage);

                Vec3 knockbackDir = entity.position().subtract(explosionPos).normalize();
                double knockbackStrength = KNOCKBACK_HORIZONTAL * distanceMultiplier;
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        knockbackDir.x * knockbackStrength,
                        KNOCKBACK_VERTICAL * distanceMultiplier,
                        knockbackDir.z * knockbackStrength
                ));
                entity.hurtMarked = true;
            }
        }

        spawnExplosionEffects(serverLevel, attacker, explosionPos);
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof AbstractArrow arrow)) {
            return;
        }
        Entity owner = arrow.getOwner();
        if (!(owner instanceof LivingEntity attacker)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty() || !weapon.is(Items.TRIDENT)) {
            return;
        }
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.SEA_BREEZE.get())) {
            return;
        }
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        HitResult hitResult = event.getRayTraceResult();
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        Vec3 hitPos = blockHit.getLocation();

        float damage = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.SEA_BREEZE_DAMAGE.get());

        AABB explosionBox = new AABB(
                hitPos.x - EXPLOSION_RADIUS,
                hitPos.y - EXPLOSION_RADIUS,
                hitPos.z - EXPLOSION_RADIUS,
                hitPos.x + EXPLOSION_RADIUS,
                hitPos.y + EXPLOSION_RADIUS,
                hitPos.z + EXPLOSION_RADIUS
        );

        List<LivingEntity> affectedEntities = attacker.level().getEntitiesOfClass(LivingEntity.class, explosionBox,
                entity -> entity != attacker);

        for (LivingEntity entity : affectedEntities) {
            double distance = entity.position().distanceTo(hitPos);
            float distanceMultiplier = (float) Math.max(0, 1.0 - (distance / EXPLOSION_RADIUS));
            float finalDamage = damage * distanceMultiplier;

            if (finalDamage > 0) {
                entity.hurt(attacker.level().damageSources().magic(), finalDamage);

                Vec3 knockbackDir = entity.position().subtract(hitPos).normalize();
                double knockbackStrength = KNOCKBACK_HORIZONTAL * distanceMultiplier;
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        knockbackDir.x * knockbackStrength,
                        KNOCKBACK_VERTICAL * distanceMultiplier,
                        knockbackDir.z * knockbackStrength
                ));
                entity.hurtMarked = true;
            }
        }

        spawnExplosionEffects(serverLevel, attacker, hitPos);
    }

    // 爆炸特效（旧版两处共用的粒子 + 音效）
    private static void spawnExplosionEffects(ServerLevel serverLevel, LivingEntity attacker, Vec3 pos) {
        serverLevel.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);

        for (int i = 0; i < 20; i++) {
            double offsetX = (attacker.getRandom().nextDouble() - 0.5) * EXPLOSION_RADIUS * 2;
            double offsetY = (attacker.getRandom().nextDouble() - 0.5) * EXPLOSION_RADIUS;
            double offsetZ = (attacker.getRandom().nextDouble() - 0.5) * EXPLOSION_RADIUS * 2;
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    pos.z + offsetZ,
                    1, 0, 0.1, 0, 0);
        }

        attacker.level().playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.WIND_CHARGE_BURST, SoundSource.PLAYERS, 1.5F, 1.0F);
    }

    private TridentImpactEvents() {
    }
}
