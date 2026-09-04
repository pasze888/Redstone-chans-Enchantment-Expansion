package com.chinaex123.redstone_enchants.event.armor_horse;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * 马铠（horse_armor）附魔在实体 tick 事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：牧场 → （后续）精神。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ArmorHorseTickEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        // 固定顺序：牧场 → 精神（旧版为独立订阅者，顺序未定义）
        pasture(event);
    }

    // ---- 牧场 ----

    private static void pasture(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractHorse horse)) {
            return;
        }

        // 检查马是否有鞍（被骑乘）
        if (!horse.isSaddled()) {
            return;
        }

        // 检查马铠是否有牧场附魔
        ItemStack armor = horse.getItemBySlot(EquipmentSlot.BODY);
        if (armor.isEmpty()) {
            return;
        }
        if (!EnchantmentHelper.has(armor, ModEnchantmentEffectComponents.PASTURE_HEAL.get())) {
            return;
        }
        if (!(horse.level() instanceof ServerLevel serverLevel)) {
            // 治疗以服务端为准
            return;
        }

        // 检查马是否在草方块上
        BlockPos pos = horse.blockPosition();
        BlockPos belowPos = pos.below();

        if (horse.level().getBlockState(belowPos).is(Blocks.GRASS_BLOCK)) {
            // 每 20 tick（1 秒）恢复一次生命值
            if (horse.tickCount % 20 == 0) {
                // 每级恢复 0.5 点生命值
                float healPerLevel = EnchantmentUtil.itemValue(serverLevel, armor,
                        ModEnchantmentEffectComponents.PASTURE_HEAL.get());
                horse.heal(healPerLevel);
            }
        }
    }

    private ArmorHorseTickEvents() {
    }
}
