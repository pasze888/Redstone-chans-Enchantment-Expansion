package com.chinaex123.redstone_enchants.util;

import net.minecraft.world.entity.Entity;

/**
 * tick 节流工具：把"每 N tick 做一次"统一成同一套周期常量与判定写法。
 * <p>挂在 tick 事件上的分发器只该做轻量状态收敛，重活（实体查询、组件求值等）必须明确周期，
 * 见 docs/design/enchantment-authoring.md §3。
 */
public final class TickUtil {
    /** 1 秒（20 tick） */
    public static final int ONE_SECOND = 20;

    /** 该实体本 tick 是否轮到这个周期（按实体 tickCount 对齐，不额外保存时间戳） */
    public static boolean isDue(Entity entity, int periodTicks) {
        return entity.tickCount % periodTicks == 0;
    }

    private TickUtil() {
    }
}
