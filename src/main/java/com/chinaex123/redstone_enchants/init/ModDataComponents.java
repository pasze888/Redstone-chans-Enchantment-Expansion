package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品数据组件（data_component_type）。
 * <p>标记类组件：记录"该物品上的某个运行时组件由本 mod 附魔写入"，
 * 供卸附魔后的精确清理使用，避免误删其它来源（物品默认组件/指令/其它 mod）的组件。
 */
public final class ModDataComponents {
    public static final DeferredRegister.DataComponents TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RedstoneEnchants.MOD_ID);

    /** 坚固（sturdy）：FIRE_RESISTANT 由本附魔写入的标记 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> STURDY_APPLIED =
            unit("sturdy_applied");
    /** 坚不可摧（indestructible）：UNBREAKABLE 由本附魔写入的标记 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> INDESTRUCTIBLE_APPLIED =
            unit("indestructible_applied");

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> unit(String name) {
        return TYPES.register(name, () -> DataComponentType.<Unit>builder()
                .persistent(Unit.CODEC)
                .networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
                .build());
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    private ModDataComponents() {
    }
}
