package com.chinaex123.redstone_enchants.event.all_armor;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 全身盔甲（all_armor）附魔在伤害事件上的统一分发器（重生护盾：致命伤时保留 0.5 血）。
 * <p>行为参数由附魔 JSON 组件声明。旧实现是单个订阅者类。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorDamageEvents {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        reviveWard(event);
    }

    // ---- 重生护盾 ----

    private static void reviveWard(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();

        // 行为变更（经用户确认）：判死时点从 Pre(original) 改为 Post 实际扣血后——
        // 考虑伤害链中其它修改后真正致死的伤害才触发守护
        if (!entity.isDeadOrDying()) {
            return;
        }

        // 检查所有盔甲槽位是否有重生护盾附魔
        List<EquipmentSlot> slotsWithEnchant = new ArrayList<>();
        EquipmentSlot maxLevelSlot = null;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armor = entity.getItemBySlot(slot);
            if (armor.isEmpty()) {
                continue;
            }
            if (!EnchantmentHelper.has(armor, ModEnchantmentEffectComponents.REVIVE_WARD.get())) {
                continue;
            }
            slotsWithEnchant.add(slot);
            if (maxLevelSlot == null) {
                // max_level 1，首个带附魔的槽位即最高级槽位（旧版取最高等级槽位）
                maxLevelSlot = slot;
            }
        }

        // 如果没有重生护盾附魔，不触发
        if (slotsWithEnchant.isEmpty()) {
            return;
        }

        // 阻止死亡：Post 时 die() 尚未调用，把血量拉回 0.5（旧版为压伤到保留 0.5 血）
        entity.setHealth(0.5F);

        // 播放不死图腾音效
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

        // 生成粒子效果
        if (entity.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 30; ++i) {
                double d0 = serverLevel.random.nextGaussian() * 0.02D;
                double d1 = serverLevel.random.nextGaussian() * 0.02D;
                double d2 = serverLevel.random.nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
                        1, d0, d1, d2, 1.0D);
            }
        }

        // 抗火
        entity.addEffect(new MobEffectInstance(
                MobEffects.FIRE_RESISTANCE, 20 * 40, 0, false, true
        ));
        // 生命恢复
        entity.addEffect(new MobEffectInstance(
                MobEffects.REGENERATION, 20 * 40, 1, false, true
        ));
        // 抗性提升
        entity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, 20 * 40, 2, false, true
        ));

        // 移除最高级槽位上的本附魔（max_level 1 → 直接移除，旧版是减 1 级/到 0 移除）
        ItemStack armor = entity.getItemBySlot(maxLevelSlot);
        if (!armor.isEmpty()) {
            Holder<Enchantment> holder = EnchantmentUtil.holder(
                    entity.level().registryAccess(), ModEnchantments.REVIVE_WARD);
            ItemEnchantments currentEnchants = armor.get(DataComponents.ENCHANTMENTS);
            if (currentEnchants != null) {
                ItemEnchantments.Mutable mutableEnchants = new ItemEnchantments.Mutable(currentEnchants);
                mutableEnchants.removeIf(enchant -> enchant.equals(holder));
                armor.set(DataComponents.ENCHANTMENTS, mutableEnchants.toImmutable());
            }
        }
    }

    private ArmorDamageEvents() {
    }
}
