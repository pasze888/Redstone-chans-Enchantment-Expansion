package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 实体数据附件（不持久化、不同步的临时运行期标记）。
 * <p>仅用于服务端事件链内的状态传递；世界重载后自动清空，无需存档。
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

    private ModAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
