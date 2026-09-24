package com.chinaex123.redstone_enchants.util;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import java.util.Comparator;
import java.util.PriorityQueue;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * 服务端延迟任务：真正"N tick 之后执行"的排队器，替代 {@code MinecraftServer#tell(new TickTask(...))}。
 * <p><strong>{@code TickTask} 不能当定时器</strong>：它的 tick 只在服务器落后于计划时才是闸门
 * （{@code MinecraftServer#shouldRun} = {@code runnable.getTick() + 3 < this.tickCount ||
 * this.haveTime()}，{@code MinecraftServer.java:852-854}），而健康服务器在每 tick 末的
 * {@code waitUntilNextTick()} → {@code runAllTasks()}（同文件 {@code :718}、{@code :833-836}）里会把队列
 * 一次抽干——排到未来刻的任务当 tick 就执行。本仓库曾有三处延时踩了这个坑（精准射击清理、冰霜箭解除、
 * 永恒冰霜动画），表现为"延时任务当 tick 就生效"，看起来像是那些效果本身坏了。
 * <p>本类按 {@link ServerLevel#getGameTime()}（存档全局时钟）到期：入队时记 {@code gameTime + delayTicks}，
 * 由 {@link ServerTickEvent.Post} 每 tick 摘取到期的任务，同一 tick 内按入队先后执行。
 * <p>队列是纯内存的：服务器重启、关卡卸载后未执行的任务直接丢弃（不同于会把待执行函数写进存档的
 * mcfunction {@code schedule function}），调用方要自己兜残留——见 {@code event/freeze/FreezeShardCleanupEvents}。
 * <p>只应从服务端线程调用（现有调用点都是服务端附魔效果）。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class DelayedTasks {

    /** {@code sequence} 让同一 tick 到期的任务保持入队顺序（{@link PriorityQueue} 本身不保证稳定） */
    private record Task(ServerLevel level, long dueTick, long sequence, Runnable action) {
    }

    private static final Comparator<Task> ORDER =
            Comparator.comparingLong(Task::dueTick).thenComparingLong(Task::sequence);
    private static final PriorityQueue<Task> QUEUE = new PriorityQueue<>(ORDER);

    private static long nextSequence;

    /** 在该维度 {@code delayTicks} tick 之后执行（{@code <= 0} 即下一个 tick 的执行点） */
    public static void schedule(ServerLevel level, int delayTicks, Runnable action) {
        QUEUE.add(new Task(level, level.getGameTime() + delayTicks, nextSequence++, action));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        while (!QUEUE.isEmpty()) {
            Task task = QUEUE.peek();
            if (task.level().getGameTime() < task.dueTick()) {
                return;
            }

            QUEUE.poll();
            try {
                task.action().run();
            } catch (Exception exception) {
                // 与 BlockableEventLoop#doRunTask 一致：单个任务出错只记日志，不能把服务器 tick 带崩
                RedstoneEnchants.LOGGER.error("Delayed task failed", exception);
            }
        }
    }

    /** 关卡卸载：它不再推进 gameTime，留在队里会永久占住队首，故整批丢弃 */
    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            QUEUE.removeIf(task -> task.level() == level);
        }
    }

    /** 服务器停止：整队清空，避免任务跨世界残留 */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        QUEUE.clear();
    }

    private DelayedTasks() {
    }
}
