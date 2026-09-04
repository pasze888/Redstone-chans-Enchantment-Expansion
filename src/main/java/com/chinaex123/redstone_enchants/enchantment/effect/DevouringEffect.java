package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 吞噬（devouring）：攻击命中时攻击者获得 1 秒饱和恢复；若攻击者饱食度
 * 恰好为满（20），再获得 1 秒生命恢复。
 * <p>等价于原函数：{@code effect give @s saturation 1 0 true} +
 * {@code execute if data entity @s {foodLevel:20} run effect give @s regeneration 1 0 true}
 * （NBT foodLevel:20 为整值相等判定）。
 */
public record DevouringEffect() implements EnchantmentEntityEffect {

    public static final DevouringEffect INSTANCE = new DevouringEffect();
    public static final MapCodec<DevouringEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20, 0, true, false));
            // NBT foodLevel:20 只在玩家实体上存在（原版 FoodData 仅玩家有），
            // 因此饱和食满额外回复也仅对饱食度为 20 的玩家生效
            if (entity instanceof Player player && player.getFoodData().getFoodLevel() == 20) {
                living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 0, true, false));
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
