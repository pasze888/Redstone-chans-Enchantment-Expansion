package com.chinaex123.redstone_enchants.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 服务端配置（存档级，{@code <world>/serverconfig/redstone_enchants-server.toml}）。
 * <p>供服务器所有者直接控制附魔行为数值；不配置时保持默认行为。
 */
public final class ModConfigData {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue TIMBER_CHAIN_LIMIT = BUILDER
            .comment("连锁砍树（timber）单次最多连带破坏的原木数")
            .defineInRange("timberChainLimit", 512, 1, 4096);

    private static final ModConfigSpec SPEC = BUILDER.build();

    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    private ModConfigData() {
    }
}
