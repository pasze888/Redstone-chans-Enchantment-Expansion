package com.chinaex123.redstone_enchants.data;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.data.provider.ModEnchantmentProvider;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
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

        // splash_delayed_explosion 依赖 ars_nouveau 的状态效果（ars_nouveau:blasting）；
        // rapid 依赖 apothic_attributes 的属性（apothic_attributes:draw_speed）——均带 mod_loaded 条件
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                output, event.getLookupProvider(), ModEnchantmentProvider.DATA_BUILDER,
                conditions -> {
                    conditions.accept(ModEnchantments.SPLASH_DELAYED_EXPLOSION,
                            new ModLoadedCondition("ars_nouveau"));
                    conditions.accept(ModEnchantments.RAPID,
                            new ModLoadedCondition("apothic_attributes"));
                    conditions.accept(ModEnchantments.TOUCH_BLEEDING,
                            new ModLoadedCondition("apothic_attributes"));
                    conditions.accept(ModEnchantments.UNDERCURRENT,
                            new ModLoadedCondition("apothic_attributes"));
                    conditions.accept(ModEnchantments.UNDERCURRENT,
                            new ModLoadedCondition("twilightforest"));
                },
                Set.of(RedstoneEnchants.MOD_ID)));
    }

    private ModDataGenerator() {
    }
}
