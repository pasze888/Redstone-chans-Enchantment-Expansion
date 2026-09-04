package com.chinaex123.redstone_enchants;

import com.chinaex123.redstone_enchants.config.ModConfigData;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RedstoneEnchants.MOD_ID)
public class RedstoneEnchants {
    public static final String MOD_ID = "redstone_enchants";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RedstoneEnchants(IEventBus modEventBus, ModContainer modContainer) {
        ModEnchantments.register(modEventBus);
        ModConfigData.register(modContainer);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
