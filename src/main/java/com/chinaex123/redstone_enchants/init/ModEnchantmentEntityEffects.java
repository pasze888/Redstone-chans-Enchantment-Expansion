package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.enchantment.effect.AddExperienceEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AddTagEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.AirTossEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ChainArrowsEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ChainBindEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ClearMainHandEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.DevouringEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.DropHeldItemEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.FreezeWaterEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.GiveItemEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.HoveringArrowEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.IceArrowSlownessEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.IgniteAreaEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.KillSelfEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ParticleBurstEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RainBlocksEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomBeneficialMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RandomHarmfulMobEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RemoveRandomBeneficialEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.RicochetEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.SnowballBurstEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.SplashCloudEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.SummonItemEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.ThrowWaterBottleEffect;
import com.chinaex123.redstone_enchants.enchantment.effect.WeatherThunderEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义附魔实体效果（enchantment_entity_effect_type）注册表。
 * <p>该注册表存的是各 effect 的 {@link MapCodec}（序列化器），不是 effect 实例；
 * 运行期由原版 {@code post_attack} 等组件求值时按 codec 解码并按 level 施加。
 * <p>参考神化 {@code Ench.EnchantEffects} 的 {@code R.custom("rebounding", ...)} 形态，
 * 落点为 1.21.1 的 {@code Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE}。
 */
public final class ModEnchantmentEntityEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> TYPES =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, RedstoneEnchants.MOD_ID);

    /** 攻击时在受害实体位置生成物品堆（模板示例） */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> SUMMON_ITEM =
            TYPES.register("summon_item", () -> SummonItemEffect.CODEC);

    /** 恩赐（boons）：攻击时按概率给攻击者施加随机正面药水 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> RANDOM_BENEFICIAL_MOB_EFFECT =
            TYPES.register("random_beneficial_mob_effect", () -> RandomBeneficialMobEffect.CODEC);

    /** 灾厄（calamity）：攻击时按概率给受害者施加随机负面药水 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> RANDOM_HARMFUL_MOB_EFFECT =
            TYPES.register("random_harmful_mob_effect", () -> RandomHarmfulMobEffect.CODEC);

    /** 消解（nullify）：攻击时按概率移除受害者身上的一个正面药水 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> REMOVE_RANDOM_BENEFICIAL =
            TYPES.register("remove_random_beneficial", () -> RemoveRandomBeneficialEffect.CODEC);

    /** 蔓延系列（splash_*）：命中时生成带药水效果的区域效果云 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> SPLASH_CLOUD =
            TYPES.register("splash_cloud", () -> SplashCloudEffect.CODEC);

    /** 空中抛掷（air_toss）：把攻击者向上抛起 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> AIR_TOSS =
            TYPES.register("air_toss", () -> AirTossEffect.CODEC);

    /** 水瓶投射（water_bottle_projection）：掷出喷溅型水瓶 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> THROW_WATER_BOTTLE =
            TYPES.register("throw_water_bottle", () -> ThrowWaterBottleEffect.CODEC);

    /** 雷鸣（thundering）：把天气切为雷暴 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> WEATHER_THUNDER =
            TYPES.register("weather_thunder", () -> WeatherThunderEffect.CODEC);

    /** 经验利刃（xp_blade）：扣除玩家经验点数 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> ADD_EXPERIENCE =
            TYPES.register("add_experience", () -> AddExperienceEffect.CODEC);

    /** 吞噬（devouring）：攻击者获得饱和与条件生命恢复 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> DEVOURING =
            TYPES.register("devouring", () -> DevouringEffect.CODEC);

    /** 击杀命令实体（替代 libs/kill_arrow）：命中结算后清除箭矢本体 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> KILL_SELF =
            TYPES.register("kill_self", () -> KillSelfEffect.CODEC);

    /** 锁链（chains）：受害者及周围 3 个生物重度减速+虚弱 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> CHAIN_BIND =
            TYPES.register("chain_bind", () -> ChainBindEffect.CODEC);

    /** 冰霜箭：把 3x3x3 的水冻结成冰并撒雪花粒子 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> FREEZE_WATER =
            TYPES.register("freeze_water", () -> FreezeWaterEffect.CODEC);

    /** 冰霜箭：重度冰缓（4 项属性归零），5 秒后自动移除 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> ICE_ARROW_SLOWNESS =
            TYPES.register("ice_arrow_slowness", () -> IceArrowSlownessEffect.CODEC);

    /** 笨拙诅咒：把主手物品掉出去 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> DROP_HELD_ITEM =
            TYPES.register("drop_held_item", () -> DropHeldItemEffect.CODEC);

    /** 给实体打 tag（第一印象） */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> ADD_TAG =
            TYPES.register("add_tag", () -> AddTagEffect.CODEC);

    /** 精准射击：箭矢悬浮，5 秒无玩家靠近则清除 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> HOVERING_ARROW =
            TYPES.register("hovering_arrow", () -> HoveringArrowEffect.CODEC);

    /** 跳弹：箭矢飞向最近目标 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> RICOCHET =
            TYPES.register("ricochet", () -> RicochetEffect.CODEC);

    /** 雪球：受击时弹出雪球射向最近生物 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> SNOWBALL_BURST =
            TYPES.register("snowball_burst", () -> SnowballBurstEffect.CODEC);

    /** 雨：生成环形坠落方块（石锥/铁砧） */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> RAIN_BLOCKS =
            TYPES.register("rain_blocks", () -> RainBlocksEffect.CODEC);

    /** 连锁反应：从受害者射出 6 支环形箭 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> CHAIN_ARROWS =
            TYPES.register("chain_arrows", () -> ChainArrowsEffect.CODEC);

    /** 火焰箭：点燃 3x3x3 空气并撒火焰粒子 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> IGNITE_AREA =
            TYPES.register("ignite_area", () -> IgniteAreaEffect.CODEC);

    /** 最后的希望：清空主手 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> CLEAR_MAIN_HAND =
            TYPES.register("clear_main_hand", () -> ClearMainHandEffect.CODEC);

    /** 通用粒子爆发 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> PARTICLE_BURST =
            TYPES.register("particle_burst", () -> ParticleBurstEffect.CODEC);

    /** 检索（retrieval）：给受害者一支同种箭 */
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<? extends EnchantmentEntityEffect>> GIVE_ITEM =
            TYPES.register("give_item", () -> GiveItemEffect.CODEC);

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    private ModEnchantmentEntityEffects() {
    }
}
