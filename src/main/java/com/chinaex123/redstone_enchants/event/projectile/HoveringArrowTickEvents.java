package com.chinaex123.redstone_enchants.event.projectile;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * 精准射击（accuracy_shot）悬浮箭的到期清理。
 * <p>到期时刻（绝对存档时钟）由 {@code HoveringArrowEffect} 写在箭的持久附件里，这里每 tick 与所在维度的
 * {@link net.minecraft.world.level.Level#getGameTime()} 比对；没有登记过的箭（附件默认值 0）不处理。
 * <p>本类替代原先的 {@code MinecraftServer#tell(new TickTask(now + 1200, ...))}：那条路径在健康服务器上
 * 会在同一 tick 内被执行（见 {@code HoveringArrowEffect} 的说明），导致箭一射出来就被清掉。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class HoveringArrowTickEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow) || !(arrow.level() instanceof ServerLevel level)) {
            return;
        }

        long deadline = arrow.getData(ModAttachments.HOVERING_ARROW_DEADLINE.get());
        if (deadline != 0L && level.getGameTime() >= deadline) {
            arrow.discard();
        }
    }

    private HoveringArrowTickEvents() {
    }
}
