package com.chinaex123.redstone_enchants.data;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

/**
 * 数据生成入口（runData）。
 * <p>已迁移到声明式的附魔 JSON 由 {@link ModEnchantmentProvider} 生成到 src/generated/resources，
 * 其余仍为手写 JSON（后续批次逐步迁入）。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                output, event.getLookupProvider(), ModEnchantmentProvider.DATA_BUILDER, Set.of(RedstoneEnchants.MOD_ID)));
    }

    private ModDataGenerator() {
    }
}
