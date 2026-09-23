package com.chinaex123.redstone_enchants.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;

/**
 * 范围索敌：本模组的索敌类效果（锁链 / 跳弹 / 雪球）一律"只找非玩家生物"，
 * 这里把选取规则收成一处——按距离升序取最近的前 N 个，可指定最小距离。
 */
public final class TargetingUtil {
    /** {@code source} 周围 {@code [minRange, maxRange]} 内最近的至多 {@code limit} 个非玩家生物（不含 source 自身） */
    public static List<LivingEntity> nearestNonPlayers(ServerLevel level, Entity source,
                                                      double minRange, double maxRange, int limit) {
        double minSqr = minRange * minRange;
        return level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(maxRange),
                        e -> e != source && !(e instanceof Player) && e.distanceToSqr(source) >= minSqr)
                .stream()
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(source)))
                .limit(limit)
                .toList();
    }

    /** 同 {@link #nearestNonPlayers} 但只取最近的一个；范围内没有合格目标时返回 {@code null} */
    public static LivingEntity nearestNonPlayer(ServerLevel level, Entity source, double minRange, double maxRange) {
        List<LivingEntity> found = nearestNonPlayers(level, source, minRange, maxRange, 1);
        return found.isEmpty() ? null : found.get(0);
    }

    private TargetingUtil() {
    }
}
