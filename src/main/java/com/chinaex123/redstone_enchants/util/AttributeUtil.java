package com.chinaex123.redstone_enchants.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 属性修饰符写入工具：只在"目标状态与现状不同"时才动属性。
 * <p>属性写脏会触发数值重算并向客户端刷同步包，所以挂在 tick 上的分发器不得每 tick
 * 无条件 remove + add，而要按目标值收敛（见 docs/design/enchantment-authoring.md §3）。
 */
public final class AttributeUtil {
    /**
     * 把 {@code modifierId} 对应的永久修饰符收敛到 {@code amount}。
     * <p>{@code amount} 为 {@code null} 表示"不该有该修饰符"：存在则移除；已存在且数值与运算
     * 都相同则什么都不做。属性本身不存在（如该生物没有此属性）时直接跳过。
     */
    public static void applyPermanent(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation modifierId,
                                      Double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        AttributeModifier current = instance.getModifier(modifierId);
        if (amount == null) {
            if (current != null) {
                instance.removeModifier(modifierId);
            }
            return;
        }
        if (current != null && current.amount() == amount && current.operation() == operation) {
            return;
        }
        instance.addOrReplacePermanentModifier(new AttributeModifier(modifierId, amount, operation));
    }

    private AttributeUtil() {
    }
}
