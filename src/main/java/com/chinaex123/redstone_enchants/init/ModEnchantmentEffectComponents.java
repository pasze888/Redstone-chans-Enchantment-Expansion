package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
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
    /** 处决（execution）：目标当前生命占比低于 25% 时直接击杀 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> EXECUTION =
            unit("execution");
    /** 生命吸取（life_steal）：按本次实际造成伤害恢复的比例 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> LIFE_STEAL_RATIO =
            value("life_steal_ratio");
    /** 伏击（ambush）：潜行首击的加伤比例（每级） */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> AMBUSH_BONUS =
            value("ambush_bonus");
    /** 背刺（backstab）：从目标背后攻击的加伤比例（每级） */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> BACKSTAB_BEHIND_BONUS =
            value("backstab_behind_bonus");
    /** 背刺（backstab）：从目标正面攻击的减伤比例（每级） */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> BACKSTAB_FRONT_PENALTY =
            value("backstab_front_penalty");
    /** 均衡器（equalizer）：伤害随目标血量百分比变化的加成系数（每级） */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> EQUALIZER_BONUS =
            value("equalizer_bonus");
    /** 屠夫（butcher）：击杀动物时额外掉落的比例（每级） */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> BUTCHER_EXTRA_DROP =
            value("butcher_extra_drop");

    /** 攻击附加掉落（模板）：在受害实体位置生成物品堆，供 swords 类附魔通过 post_attack 声明 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> POST_ATTACK_SUMMON =
            targeted("post_attack_summon");

    /** 裸数值组件（模板）：承载单个 LevelBasedValue，配合 withSpecialEffect 使用 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LevelBasedValue>> RAW_VALUE =
            special("raw_value", LevelBasedValue.CODEC);

    /** 赌徒（gambler）：触发概率与加/减伤乘数的复合参数 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<com.chinaex123.redstone_enchants.enchantment.component.GamblerData>> GAMBLER_DATA =
            special("gambler_data", com.chinaex123.redstone_enchants.enchantment.component.GamblerData.CODEC);

    private static DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> value(String name) {
        return TYPES.register(name, () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf())
                .build());
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> unit(String name) {
        return TYPES.register(name, () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
    }

    /**
     * 定向实体效果组件：{@code List<TargetedConditionalEffect<EnchantmentEntityEffect>>}。
     * <p>供战斗类附魔通过原版 {@code minecraft:post_attack} 声明"对攻击者/受害实体施加效果"；
     * {@code enchanted/affected} 目标在 provider 里由 {@code Enchantment.Builder.withEffect(..., EnchantmentTarget, ...)} 指定。
     */
    private static DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> targeted(String name) {
        return TYPES.register(name, () -> DataComponentType.<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>builder()
                .persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
                .build());
    }

    /**
     * 裸值组件（配合 {@code Enchantment.Builder.withSpecialEffect(component, value)} 使用），
     * 承载单个 {@code LevelBasedValue} 或其它自定义 record（参考神化 {@code MINERS_FERVOR} / {@code BERSERKING}）。
     */
    private static <E> DeferredHolder<DataComponentType<?>, DataComponentType<E>> special(String name, com.mojang.serialization.Codec<E> codec) {
        return TYPES.register(name, () -> DataComponentType.<E>builder().persistent(codec).build());
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    private ModEnchantmentEffectComponents() {
    }
}
