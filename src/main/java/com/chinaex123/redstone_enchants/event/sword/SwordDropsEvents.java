package com.chinaex123.redstone_enchants.event.sword;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 剑类战斗附魔在掉落事件上的统一分发器（与 {@link SwordLivingDamageEvents} 同模式）。
 * <p>行为参数由附魔 JSON 组件声明，这里按固定顺序驱动各效果。
 * 旧实现是每个附魔一个独立订阅者；分发器固定执行顺序：屠夫 → 斩首。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class SwordDropsEvents {

    /** 斩首掷骰用随机源（旧 handler 同款 java.util.Random） */
    private static final Random DECAPITATION_RANDOM = new Random();

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (attacker == null) {
            return;
        }
        butcherExtraDrops(event, attacker);
        decapitationHeadDrop(event, attacker);
    }

    // ---- 屠夫 ----

    private static void butcherExtraDrops(LivingDropsEvent event, LivingEntity attacker) {
        LivingEntity entity = event.getEntity();
        // 只处理动物（包括猪、牛、羊等）
        if (!(entity instanceof Animal)) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        float extraRatio = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.BUTCHER_EXTRA_DROP.get());
        if (extraRatio <= 0) {
            return;
        }

        // 为每个掉落物额外追加一份：数量 = max(1, ceil(原数量 × 比例))（比例 = 0.5×级）
        List<ItemEntity> additionalDrops = new ArrayList<>();
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            int extraCount = Math.max(1, (int) Math.ceil(stack.getCount() * extraRatio));
            ItemStack extraStack = stack.copy();
            extraStack.setCount(extraCount);
            additionalDrops.add(new ItemEntity(serverLevel, entity.getX(), entity.getY(), entity.getZ(), extraStack));
        }
        event.getDrops().addAll(additionalDrops);
    }

    // ---- 斩首 ----

    private static void decapitationHeadDrop(LivingDropsEvent event, LivingEntity attacker) {
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (!EnchantmentHelper.has(weapon, ModEnchantmentEffectComponents.DECAPITATION_CHANCE.get())) {
            return;
        }
        LivingEntity entity = event.getEntity();
        String entityType = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();

        // 查找对应的头颅物品（支持所有模组）
        ItemStack skullStack = findSkullForEntity(entityType);
        if (skullStack.isEmpty()) {
            return;
        }

        // 掉落概率由组件声明（每级 20%，5 级 100%）
        float dropChance = EnchantmentUtil.itemValue(serverLevel, weapon, ModEnchantmentEffectComponents.DECAPITATION_CHANCE.get());
        if (DECAPITATION_RANDOM.nextFloat() < dropChance) {
            event.getDrops().add(new ItemEntity(
                    entity.level(),
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    skullStack
            ));
        }
    }

    /**
     * 头颅查找（照抄旧 handler）：{@code <type>_head/_skull/head_/skull_} 逐个尝试，
     * 先查 minecraft 命名空间，再全物品注册表按路径遍历，支持所有模组。
     */
    private static ItemStack findSkullForEntity(String entityType) {
        // 尝试多种命名格式
        String[] possibleNames = {
                entityType + "_head",
                entityType + "_skull",
                "head_" + entityType,
                "skull_" + entityType
        };

        for (String name : possibleNames) {
            // 检查 Minecraft 原版
            ResourceLocation skullId = ResourceLocation.fromNamespaceAndPath("minecraft", name);
            if (BuiltInRegistries.ITEM.containsKey(skullId)) {
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(skullId));
                if (!stack.isEmpty()) return stack;
            }

            // 检查所有模组的物品
            for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
                if (itemId.getPath().equals(name)) {
                    ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(itemId));
                    if (!stack.isEmpty()) return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private SwordDropsEvents() {
    }
}
