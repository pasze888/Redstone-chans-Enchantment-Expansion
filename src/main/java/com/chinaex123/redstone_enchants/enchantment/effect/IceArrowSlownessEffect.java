package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 冰霜箭（ice_arrows）的重度冰缓：给受害者附加 4 个属性修正（移速/攻速/
 * 飞行速度/跳跃强度，add_value -20，属性实际归零），并安排 5 秒后移除。
 * <p>原实现是 mcfunction 里 {@code attribute ... modifier add} + 一个从未被
 * 调度的 scheduled 函数（modifier 永不移除，属上游 bug）；本实现用
 * transient modifier（不随实体 NBT 保存，死亡/卸载自动清理）+ 服务器
 * TickTask 定时移除，恢复原意图"冻结 5 秒"。
 * <p>修正 id 与原命令一致（redstone_enchants:ice_arrows）。
 */
public record IceArrowSlownessEffect() implements EnchantmentEntityEffect {

    public static final IceArrowSlownessEffect INSTANCE = new IceArrowSlownessEffect();
    public static final MapCodec<IceArrowSlownessEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("redstone_enchants", "ice_arrows");
    private static final int FREEZE_TICKS = 100;

    private static final List<net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>> ATTRIBUTES = List.of(
            Attributes.MOVEMENT_SPEED, Attributes.ATTACK_SPEED, Attributes.FLYING_SPEED, Attributes.JUMP_STRENGTH);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (!(entity instanceof LivingEntity victim)) {
            return;
        }
        AttributeModifier modifier = new AttributeModifier(MODIFIER_ID, -20.0, AttributeModifier.Operation.ADD_VALUE);
        for (net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute : ATTRIBUTES) {
            AttributeInstance instance = victim.getAttribute(attribute);
            if (instance != null) {
                instance.addTransientModifier(modifier);
            }
        }

        level.getServer().tell(new TickTask(level.getServer().getTickCount() + FREEZE_TICKS, () -> {
            if (victim.isRemoved() || victim.level() != level) {
                return;
            }
            for (net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute : ATTRIBUTES) {
                AttributeInstance instance = victim.getAttribute(attribute);
                if (instance != null && instance.getModifier(MODIFIER_ID) != null) {
                    instance.removeModifier(MODIFIER_ID);
                }
            }
        }));
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
