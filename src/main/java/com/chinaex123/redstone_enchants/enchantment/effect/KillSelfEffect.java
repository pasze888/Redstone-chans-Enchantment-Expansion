package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 击杀命令实体（替代 libs/kill_arrow.mcfunction 的 {@code kill @s}）：
 * 多用于命中结算后清除箭矢本体，避免残留在场景中。
 * <p>使用 {@link Entity#kill()}（RemovalReason.KILLED + 实体死亡 gameEvent），
 * 与 {@code /kill} 命令语义一致；对箭矢不会产生掉落物。
 */
public record KillSelfEffect() implements EnchantmentEntityEffect {

    public static final KillSelfEffect INSTANCE = new KillSelfEffect();
    public static final MapCodec<KillSelfEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        entity.kill();
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
