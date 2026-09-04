package com.chinaex123.redstone_enchants.event.all_fishing;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 钓鱼竿（all_fishing）附魔在收竿事件上的统一分发器。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：渔夫 → （后续）潮汐感知。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class FishingRodFishedEvents {
    private static final Random RANDOM = new Random(); // 旧版同款 java.util.Random 静态实例
    private static final TagKey<Item> TIDE_FISH_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("tide:fish"));

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        // 固定顺序：渔夫 → 潮汐感知（旧版为两个独立订阅者，顺序未定义）
        angler(event);
    }

    // ---- 渔夫 ----

    private static void angler(ItemFishedEvent event) {
        Player player = event.getEntity();
        ItemStack rod = player.getMainHandItem();

        if (rod.isEmpty() || !rod.is(Items.FISHING_ROD)) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            // ItemFished 仅在服务端触发，实体生成也需服务端
            return;
        }
        if (!EnchantmentHelper.has(rod, ModEnchantmentEffectComponents.ANGLER_DOUBLE_CHANCE.get())) {
            return;
        }

        // 每级 +10% 概率翻倍
        float chance = EnchantmentUtil.itemValue(serverLevel, rod, ModEnchantmentEffectComponents.ANGLER_DOUBLE_CHANCE.get());

        // 延迟一 tick 后复制掉落物（旧版同款：掉落快照在 lambda 执行时才取）
        Objects.requireNonNull(player.level().getServer()).execute(() -> {
            List<ItemStack> originalDrops = new ArrayList<>(event.getDrops());

            // 概率翻倍
            if (RANDOM.nextFloat() < chance) {
                for (ItemStack drop : originalDrops) {
                    // 只复制鱼类物品
                    if (drop.is(ItemTags.FISHES) || drop.is(TIDE_FISH_TAG)) {
                        ItemEntity itemEntity = new ItemEntity(
                                player.level(),
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                drop.copy()
                        );
                        player.level().addFreshEntity(itemEntity);
                    }
                }
            }
        });
    }

    private FishingRodFishedEvents() {
    }
}
