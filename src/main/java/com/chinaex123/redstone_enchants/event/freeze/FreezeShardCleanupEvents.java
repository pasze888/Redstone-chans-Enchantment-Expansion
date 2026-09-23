package com.chinaex123.redstone_enchants.event.freeze;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.EternalFrostAnimationEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * 永恒冰霜动画的残留清扫。
 * <p>动画由 {@code TickTask} 三段延时推进（见 {@link EternalFrostAnimationEffect}），而
 * {@code TickTask} 是纯内存队列：服务器在动画的约 50 tick 内重启，或区块在此期间卸载，
 * 推进链就断了，13 片霜冰会以存档里的目标变换永久留在原地。
 * <p>判据用 {@link EntityJoinLevelEvent#loadedFromDisk()}——它为 true 只有两条路径：区块实体
 * 从存档反序列化（{@code PersistentEntitySectionManager.processPendingLoads}，
 * {@code PersistentEntitySectionManager.java:247}）与旧区块实体格式（同文件 {@code :114}）；
 * 自己 {@code addFreshEntity} 出来的走 {@code addNewEntity}（同文件 {@code :70-71}），标志是
 * false。于是"带霜冰 tag 且来自磁盘"必然是断链残留，取消加入即可——该事件在实体进入
 * {@code PersistentEntitySectionManager} **之前**触发且可取消，比事后 {@code discard()} 干净，
 * 也避开了它 javadoc 里"不要在此做世界交互（会与区块加载抢锁）"的警告。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class FreezeShardCleanupEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // 客户端该标志恒为 false（见 EntityJoinLevelEvent#loadedFromDisk 的约定），故不需要再判侧
        if (!event.loadedFromDisk()) {
            return;
        }

        if (event.getEntity().getTags().contains(EternalFrostAnimationEffect.FREEZING_TAG)) {
            event.setCanceled(true);
        }
    }

    private FreezeShardCleanupEvents() {
    }
}
