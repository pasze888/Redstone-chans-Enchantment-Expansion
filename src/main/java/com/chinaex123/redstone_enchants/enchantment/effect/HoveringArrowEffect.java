package com.chinaex123.redstone_enchants.enchantment.effect;

import com.chinaex123.redstone_enchants.init.ModAttachments;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 精准射击（accuracy_shot）：射出的箭矢不受重力直飞（NoGravity），发射后 {@value #LIFETIME_TICKS} tick
 * （60 秒）由 {@link com.chinaex123.redstone_enchants.event.projectile.HoveringArrowTickEvents} 兜底清除。
 * <p><strong>不要用 {@code MinecraftServer#tell(TickTask)} 当延时</strong>：{@code TickTask} 的 tick 只在
 * 服务器落后于计划时才起作用（{@code MinecraftServer#shouldRun} 里是 {@code tick + 3 < tickCount ||
 * haveTime()}），而健康服务器在每 tick 末的 {@code waitUntilNextTick()} 里就 {@code runAllTasks()} 把队列
 * 抽干——"延后 1200 tick"实际等于"当 tick 立刻执行"，箭一离弦就被 {@code discard()}。所以这里把到期时刻
 * 写成持久化实体附件，由每 tick 的实体事件按存档全局时钟 {@link net.minecraft.world.level.Level#getGameTime()} 比对。
 * <p>附件必须持久化：箭是会存档的实体（{@code Entity#shouldBeSaved()} 默认 true），区块卸载/重载后
 * {@code Entity#tickCount} 从 0 重来，只有绝对的 {@code gameTime} 时刻能让"发射后 60 秒"守恒。
 * <p>落地箭不需要本效果操心：原版 {@code AbstractArrow#tickDespawn()} 在 {@code life >= 1200}（落地后
 * 60 秒）时自行 {@code discard()}，这 60 秒内还能捡回；本效果真正要覆盖的是"因为 NoGravity 而永不落地"
 * 的箭。清理用 {@link Entity#discard()} 而不是 {@code kill()}：后者会额外发出 {@code GameEvent.ENTITY_DIE}。
 * <p>已知边界：箭飞进未加载区块会停在半空不再 tick，等该区块再次加载时若 60 秒已过，它会被立刻清掉
 * （而不是把剩下的行程飞完）。
 */
public record HoveringArrowEffect() implements EnchantmentEntityEffect {

    public static final HoveringArrowEffect INSTANCE = new HoveringArrowEffect();
    public static final MapCodec<HoveringArrowEffect> CODEC = MapCodec.unit(INSTANCE);

    /** 悬浮箭的总寿命上限（tick）：1200 = 60 秒，与原版 {@code AbstractArrow#tickDespawn()} 的窗口一致 */
    private static final int LIFETIME_TICKS = 1200;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        entity.setNoGravity(true);
        entity.setData(ModAttachments.HOVERING_ARROW_DEADLINE.get(), level.getGameTime() + LIFETIME_TICKS);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
