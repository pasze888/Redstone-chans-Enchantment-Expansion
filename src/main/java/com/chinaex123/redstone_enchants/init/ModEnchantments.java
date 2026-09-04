package com.chinaex123.redstone_enchants.init;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;

/**
 * 模组附魔的 ResourceKey 常量表。附魔本体是数据包条目，由 {@code data/ModEnchantmentProvider} 生成。
 */
public final class ModEnchantments {
    public static final ResourceKey<Enchantment> ADVANCED_UNBREAKING = key("advanced_unbreaking");
    public static final ResourceKey<Enchantment> ANGLER = key("angler");
    public static final ResourceKey<Enchantment> AMBUSH = key("ambush");
    public static final ResourceKey<Enchantment> AUTO_SMELT = key("auto_smelt");
    public static final ResourceKey<Enchantment> BACKSTAB = key("backstab");
    public static final ResourceKey<Enchantment> BOONS = key("boons");
    public static final ResourceKey<Enchantment> BOLTBRINGER = key("boltbringer");
    public static final ResourceKey<Enchantment> BUTCHER = key("butcher");
    public static final ResourceKey<Enchantment> CALAMITY = key("calamity");
    public static final ResourceKey<Enchantment> CHAIN_HASTE = key("chain_haste");
    public static final ResourceKey<Enchantment> CONDUCTIVE_LINE = key("conductive_line");
    public static final ResourceKey<Enchantment> CURSE_OF_RUST = key("curse_of_rust");
    public static final ResourceKey<Enchantment> CURSE_OF_WATER_SOURCE = key("curse_of_water_source");
    public static final ResourceKey<Enchantment> DAYNIGHT_CYCLE = key("daynight_cycle");
    public static final ResourceKey<Enchantment> DECAPITATION = key("decapitation");
    public static final ResourceKey<Enchantment> ECHOES_BATTLE = key("echoes_battle");
    public static final ResourceKey<Enchantment> ENDLESS_WOOL = key("endless_wool");
    public static final ResourceKey<Enchantment> EQUALIZER = key("equalizer");
    public static final ResourceKey<Enchantment> EXCAVATOR = key("excavator");
    public static final ResourceKey<Enchantment> EXECUTION = key("execution");
    public static final ResourceKey<Enchantment> EXPERIENCE_SHEAR = key("experience_shear");
    public static final ResourceKey<Enchantment> GAMBLER = key("gambler");
    public static final ResourceKey<Enchantment> GEOLOGY = key("geology");
    public static final ResourceKey<Enchantment> GOLDFINGER = key("goldfinger");
    public static final ResourceKey<Enchantment> HARVEST_ECHO = key("harvest_echo");
    public static final ResourceKey<Enchantment> HASTE = key("haste");
    public static final ResourceKey<Enchantment> INDESTRUCTIBLE = key("indestructible");
    public static final ResourceKey<Enchantment> LIFE_STEAL = key("life_steal");
    public static final ResourceKey<Enchantment> MAGNET = key("magnet");
    public static final ResourceKey<Enchantment> MASTER_GATHERER = key("master_gatherer");
    public static final ResourceKey<Enchantment> MOIST = key("moist");
    public static final ResourceKey<Enchantment> NULLIFY = key("nullify");
    public static final ResourceKey<Enchantment> PRESERVATION = key("preservation");
    public static final ResourceKey<Enchantment> POTENTIAL_CONVERSION = key("potential_conversion");
    public static final ResourceKey<Enchantment> REVIVE_WARD = key("revive_ward");
    public static final ResourceKey<Enchantment> SACRIFICE = key("sacrifice");
    public static final ResourceKey<Enchantment> SEA_BREEZE = key("sea_breeze");
    public static final ResourceKey<Enchantment> SEARING = key("searing");
    public static final ResourceKey<Enchantment> SHEPHERD = key("shepherd");
    public static final ResourceKey<Enchantment> SNIPE = key("snipe");
    public static final ResourceKey<Enchantment> STURDY = key("sturdy");
    public static final ResourceKey<Enchantment> TIDE_SENSE = key("tide_sense");
    public static final ResourceKey<Enchantment> TIMBER = key("timber");
    public static final ResourceKey<Enchantment> VOLT = key("volt");

    public static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, RedstoneEnchants.asResource(path));
    }

    public static void register(IEventBus eventBus) {
        ModEnchantmentEffectComponents.register(eventBus);
        ModEnchantmentEntityEffects.register(eventBus);
        ModEnchantmentLevelBasedValues.register(eventBus);
    }

    private ModEnchantments() {
    }
}
