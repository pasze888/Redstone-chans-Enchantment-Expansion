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
    public static final ResourceKey<Enchantment> AUTO_SMELT = key("auto_smelt");
    public static final ResourceKey<Enchantment> BOONS = key("boons");
    public static final ResourceKey<Enchantment> CALAMITY = key("calamity");
    public static final ResourceKey<Enchantment> CHAIN_HASTE = key("chain_haste");
    public static final ResourceKey<Enchantment> EXCAVATOR = key("excavator");
    public static final ResourceKey<Enchantment> GEOLOGY = key("geology");
    public static final ResourceKey<Enchantment> GOLDFINGER = key("goldfinger");
    public static final ResourceKey<Enchantment> HASTE = key("haste");
    public static final ResourceKey<Enchantment> MAGNET = key("magnet");
    public static final ResourceKey<Enchantment> MASTER_GATHERER = key("master_gatherer");
    public static final ResourceKey<Enchantment> MOIST = key("moist");
    public static final ResourceKey<Enchantment> NULLIFY = key("nullify");
    public static final ResourceKey<Enchantment> TIMBER = key("timber");

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
