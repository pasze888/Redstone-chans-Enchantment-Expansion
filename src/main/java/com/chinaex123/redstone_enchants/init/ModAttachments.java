package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体数据附件（默认不持久化、不同步的临时运行期标记）。
 * <p>仅用于服务端事件链内的状态传递；世界重载后自动清空，无需存档。
 * <p>例外是需要跨区块卸载/重载守恒的绝对时刻（如 {@link #HOVERING_ARROW_DEADLINE}）：它显式加了
 * {@code serialize(...)}，会随实体 NBT 一起存档。
 */
public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RedstoneEnchants.MOD_ID);

    /** 隐身斗篷（invisibility_cloak）：当前隐身效果由本附魔施加（精确移除用） */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> CLOAK_INVISIBILITY =
            ATTACHMENT_TYPES.register("cloak_invisibility",
                    () -> AttachmentType.builder(() -> Boolean.FALSE).build());

    /** 坚固（sturdy）：受伤前各装备槽耐久快照（按 EquipmentSlot.values() 顺序，-1=空槽） */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<int[]>> STURDY_DAMAGE_SNAPSHOT =
            ATTACHMENT_TYPES.register("sturdy_damage_snapshot",
                    () -> AttachmentType.builder(() -> new int[0]).build());

    /** 庄稼舞（crop_dance）：玩家上一 tick 是否潜行（识别"刚开始潜行"的边沿） */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> CROP_DANCE_SNEAKING =
            ATTACHMENT_TYPES.register("crop_dance_sneaking",
                    () -> AttachmentType.builder(() -> Boolean.FALSE).build());

    /** 导电鱼线（conductive_line）：该鱼钩已对它当前勾住的生物劈过闪电（松钩/鱼钩消失即复位） */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> CONDUCTIVE_LINE_STRUCK =
            ATTACHMENT_TYPES.register("conductive_line_struck",
                    () -> AttachmentType.builder(() -> Boolean.FALSE).build());

    /** 伏击（ambush）：该玩家当前是否已用掉潜行首击加成（脱离潜行或实体重建即复位） */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> AMBUSH_HAS_ATTACKED =
            ATTACHMENT_TYPES.register("ambush_has_attacked",
                    () -> AttachmentType.builder(() -> Boolean.FALSE).build());

    /**
     * 保全（preservation）：玩家背包内各物品上一次见到的耐久（identityHashCode(stack) → damage）。
     * <p>只用于识别"刚达到最大耐久"那一次；每 tick 用本 tick 见到的物品裁剪一遍，容量随背包大小有界。
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Map<Integer, Integer>>> PRESERVATION_LAST_DAMAGE =
            ATTACHMENT_TYPES.register("preservation_last_damage",
                    () -> AttachmentType.<Map<Integer, Integer>>builder(() -> new HashMap<Integer, Integer>()).build());

    /**
     * 精准射击（accuracy_shot）：该悬浮箭的到期时刻（{@code Level#getGameTime()}，0 = 未登记）。
     * <p>这是全套方案里唯一需要持久化的附件：区块卸载会把箭写进存档、重载后 {@code Entity#tickCount}
     * 从 0 重来，只有绝对的存档时钟时刻能让"发射后 60 秒"跨卸载/重载守恒（同原版把
     * {@code AbstractArrow#life} 写进 NBT 的思路）。
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>> HOVERING_ARROW_DEADLINE =
            ATTACHMENT_TYPES.register("hovering_arrow_deadline",
                    () -> AttachmentType.<Long>builder(() -> 0L).serialize(Codec.LONG).build());

    private ModAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}