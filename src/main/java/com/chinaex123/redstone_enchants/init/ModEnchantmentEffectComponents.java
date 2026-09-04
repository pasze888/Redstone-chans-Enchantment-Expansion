package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

/**
 * 自定义附魔效果组件（enchantment_effect_component_type）。
 * <p>附魔行为不再写在事件处理器里，而是由附魔 JSON 的 {@code effects} 声明这些组件，
 * 运行期由 {@link com.chinaex123.redstone_enchants.util.EnchantmentUtil} 在对应钩子点读取求值。
 */
public final class ModEnchantmentEffectComponents {
    public static final DeferredRegister.DataComponents TYPES =
            DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, RedstoneEnchants.MOD_ID);

    /** 区域挖掘（excavator）：垂直于挖掘面的半径（1 级 = 3x3） */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> AREA_BREAK_RADIUS =
            value("area_break_radius");
    /** 地质学（geology）：挖石头时掉落随机矿石的概率 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> STONE_TO_ORE_CHANCE =
            value("stone_to_ore_chance");
    /** 点石成金（goldfinger）：挖石头时掉落金粒的概率 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> STONE_TO_GOLD_CHANCE =
            value("stone_to_gold_chance");
    /** 精通采集（master_gatherer）：挖矿石时双倍掉落的概率 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> ORE_DOUBLE_DROP_CHANCE =
            value("ore_double_drop_chance");
    /** 磁力（magnet）：吸引掉落物的半径（格） */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MAGNET_RANGE =
            value("magnet_range");
    /** 连锁急迫（chain_haste）：每连挖一个方块增加的挖掘速度比例 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> CHAIN_HASTE_BONUS =
            value("chain_haste_bonus");
    /** 自动熔炼（auto_smelt）：破坏方块时掉落物自动烧炼 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> AUTO_SMELT =
            unit("auto_smelt");
    /** 湿润（moist）：锄地后耕地保持最大湿度 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> MOIST =
            unit("moist");

    private static DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> value(String name) {
        return TYPES.register(name, () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf())
                .build());
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> unit(String name) {
        return TYPES.register(name, () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    private ModEnchantmentEffectComponents() {
    }
}
