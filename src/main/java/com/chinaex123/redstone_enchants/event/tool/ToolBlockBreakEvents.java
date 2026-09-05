package com.chinaex123.redstone_enchants.event.tool;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.config.ModConfigData;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/**
 * 挖掘钩子上附魔效果的统一分发器。
 * <p>各效果的行为参数由附魔 JSON 声明（见 {@link ModEnchantmentEffectComponents}），
 * 这里只负责在方块破坏事件上按固定顺序驱动各效果：
 * 连锁急迫 → 自动熔炼 → 概率加成掉落（地质学 / 点石成金 / 精通采集）→ 连锁砍树 → 区域挖掘。
 * 旧实现是每个附魔一个独立订阅者，执行顺序取决于注册顺序、且互相之间会因事件取消而不确定。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ToolBlockBreakEvents {
    private static final TagKey<Block> CONVENTIONAL_ORES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores"));
    private static final TagKey<Block> CONVENTIONAL_STONES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "stones"));
    private static final TagKey<Item> CONVENTIONAL_ORE_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores"));
    /** 地质学旧版硬编码的石头集合，保持行为一致 */
    private static final Set<Block> GEOLOGY_STONES = Set.of(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE);
    private static final ResourceLocation CHAIN_HASTE_MODIFIER_ID = RedstoneEnchants.asResource("chain_haste_bonus");
    private static final int CHAIN_HASTE_BONUS_CAP_PERCENT = 80;
    private static final long CHAIN_HASTE_WINDOW_MS = 2000;

    private static final Map<UUID, MiningStreak> MINING_STREAKS = new HashMap<>();
    /** #c:ores 方块清单，注册表冻结后惰性构建并缓存 */
    private static List<ItemStack> oreBlockDrops;

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) {
            return;
        }
        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) {
            return;
        }
        ServerLevel level = (ServerLevel) player.level();

        updateChainHaste(player, level, tool, event.getPos(), event.getState());

        if (!player.isCreative()
                && EnchantmentHelper.has(tool, ModEnchantmentEffectComponents.AUTO_SMELT.get())) {
            autoSmeltBreak(event, player, level, tool);
            return;
        }

        geologyBonusDrop(event, player, level, tool);
        goldfingerBonusDrop(event, player, level, tool);
        masterGathererDoubleDrop(event, player, level, tool);
        timberChainBreak(event, player, level, tool);
        excavatorAreaBreak(event, player, level, tool);
    }

    // ---- 自动熔炼 ----

    private static void autoSmeltBreak(BlockEvent.BreakEvent event, Player player, ServerLevel level, ItemStack tool) {
        event.setCanceled(true);
        BlockPos pos = event.getPos();
        List<ItemStack> drops = Block.getDrops(event.getState(), level, pos, level.getBlockEntity(pos), player, tool);
        level.destroyBlock(pos, false, player);
        for (ItemStack drop : drops) {
            spawnItem(level, pos, smelt(drop, level));
        }
        tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    private static ItemStack smelt(ItemStack input, ServerLevel level) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        for (RecipeHolder<SmeltingRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
            SmeltingRecipe recipe = holder.value();
            if (recipe.matches(recipeInput, level)) {
                return recipe.getResultItem(level.registryAccess()).copy();
            }
        }
        return input.copy();
    }

    // ---- 地质学：挖石头概率掉矿石 ----

    private static void geologyBonusDrop(BlockEvent.BreakEvent event, Player player, ServerLevel level, ItemStack tool) {
        if (!GEOLOGY_STONES.contains(event.getState().getBlock())) {
            return;
        }
        float chance = EnchantmentUtil.itemValue(level, tool, ModEnchantmentEffectComponents.STONE_TO_ORE_CHANCE.get());
        if (chance <= 0 || level.getRandom().nextFloat() >= chance) {
            return;
        }
        List<ItemStack> pool = oreBlockDrops(level);
        if (pool.isEmpty()) {
            return;
        }
        ItemStack oreDrop = pool.get(level.getRandom().nextInt(pool.size())).copy();
        applyFortuneCount(oreDrop, level, tool);
        spawnItem(level, event.getPos(), oreDrop);
    }

    // ---- 点石成金：挖石头概率掉金粒 ----

    private static void goldfingerBonusDrop(BlockEvent.BreakEvent event, Player player, ServerLevel level, ItemStack tool) {
        if (!event.getState().is(CONVENTIONAL_STONES)) {
            return;
        }
        float chance = EnchantmentUtil.itemValue(level, tool, ModEnchantmentEffectComponents.STONE_TO_GOLD_CHANCE.get());
        if (chance <= 0 || level.getRandom().nextFloat() >= chance) {
            return;
        }
        ItemStack goldNuggets = new ItemStack(Items.GOLD_NUGGET, 1 + level.getRandom().nextInt(3));
        applyFortuneCount(goldNuggets, level, tool);
        spawnItem(level, event.getPos(), goldNuggets);
    }

    // ---- 精通采集：挖矿石概率双倍掉落 ----

    private static void masterGathererDoubleDrop(BlockEvent.BreakEvent event, Player player, ServerLevel level, ItemStack tool) {
        if (!Item.byBlock(event.getState().getBlock()).builtInRegistryHolder().is(CONVENTIONAL_ORE_ITEMS)) {
            return;
        }
        float chance = Math.min(EnchantmentUtil.itemValue(level, tool,
                ModEnchantmentEffectComponents.ORE_DOUBLE_DROP_CHANCE.get()), 1.0F);
        if (chance <= 0 || level.getRandom().nextFloat() >= chance) {
            return;
        }
        BlockPos pos = event.getPos();
        for (ItemStack drop : Block.getDrops(event.getState(), level, pos, null, player, tool)) {
            if (!drop.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy());
                itemEntity.setPickUpDelay(0);
                level.addFreshEntity(itemEntity);
            }
        }
    }

    // ---- 连锁砍树 ----

    private static void timberChainBreak(BlockEvent.BreakEvent event, Player player, ServerLevel level, ItemStack tool) {
        BlockState startState = event.getState();
        if (!startState.is(BlockTags.LOGS)) {
            return;
        }
        Holder<Enchantment> timber = EnchantmentUtil.holder(level.registryAccess(), ModEnchantments.TIMBER);
        if (EnchantmentUtil.levelOn(timber, tool) <= 0) {
            return;
        }
        int limit = ModConfigData.TIMBER_CHAIN_LIMIT.get();
        BlockPos pos = event.getPos();
        for (BlockPos logPos : findConnectedLogs(level, pos, startState, limit)) {
            BlockState state = level.getBlockState(logPos);
            List<ItemStack> drops = Block.getDrops(state, level, logPos, level.getBlockEntity(logPos), player, tool);
            level.destroyBlock(logPos, false, player);
            for (ItemStack drop : drops) {
                spawnItem(level, logPos, drop);
            }
            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }

    private static List<BlockPos> findConnectedLogs(ServerLevel level, BlockPos start, BlockState startState, int maxBlocks) {
        List<BlockPos> result = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty() && result.size() < maxBlocks) {
            BlockPos current = queue.poll();
            if (!current.equals(start)) {
                result.add(current);
            }
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);
                if (visited.add(neighbor)) {
                    BlockState state = level.getBlockState(neighbor);
                    if (state.is(startState.getBlock())) {
                        queue.offer(neighbor);
                    }
                }
            }
        }
        return result;
    }

    // ---- 区域挖掘 ----

    private static void excavatorAreaBreak(BlockEvent.BreakEvent event, Player player, ServerLevel level, ItemStack tool) {
        if (player.isCreative()) {
            return;
        }
        float radius = EnchantmentUtil.itemValue(level, tool, ModEnchantmentEffectComponents.AREA_BREAK_RADIUS.get());
        if (radius <= 0) {
            return;
        }
        BlockPos centerPos = event.getPos();
        Direction facing = getFacingFromPlayer(player);
        for (BlockPos pos : getAreaPositions(centerPos, facing, Math.round(radius))) {
            if (pos.equals(centerPos)) {
                continue;
            }
            BlockState targetState = level.getBlockState(pos);
            if (targetState.getDestroySpeed(level, pos) < 0 || !tool.isCorrectToolForDrops(targetState)) {
                continue;
            }
            List<ItemStack> drops = Block.getDrops(targetState, level, pos, level.getBlockEntity(pos), player, tool);
            level.destroyBlock(pos, false, player);
            for (ItemStack drop : drops) {
                spawnItem(level, pos, drop);
            }
            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }

    private static Direction getFacingFromPlayer(Player player) {
        float pitch = player.getXRot();
        if (pitch > 45) {
            return Direction.DOWN;
        }
        if (pitch < -45) {
            return Direction.UP;
        }
        float yaw = player.getYRot() % 360;
        if (yaw > 180) {
            yaw -= 360;
        }
        if (yaw < -180) {
            yaw += 360;
        }
        if (yaw > -45 && yaw <= 45) {
            return Direction.SOUTH;
        }
        if (yaw > 45 && yaw <= 135) {
            return Direction.WEST;
        }
        if (yaw > 135 || yaw <= -135) {
            return Direction.NORTH;
        }
        return Direction.EAST;
    }

    private static List<BlockPos> getAreaPositions(BlockPos center, Direction facing, int radius) {
        List<BlockPos> positions = new ArrayList<>();
        Direction.Axis axis1;
        Direction.Axis axis2;
        if (facing.getAxis() == Direction.Axis.Y) {
            axis1 = Direction.Axis.X;
            axis2 = Direction.Axis.Z;
        } else {
            axis1 = Direction.UP.getAxis();
            axis2 = facing.getClockWise().getAxis();
        }
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                BlockPos offset = center;
                offset = offsetOffset(offset, axis1, i);
                offset = offsetOffset(offset, axis2, j);
                positions.add(offset);
            }
        }
        return positions;
    }

    private static BlockPos offsetOffset(BlockPos pos, Direction.Axis axis, int amount) {
        return switch (axis) {
            case X -> pos.offset(amount, 0, 0);
            case Y -> pos.offset(0, amount, 0);
            case Z -> pos.offset(0, 0, amount);
        };
    }

    // ---- 连锁急迫 ----

    private static void updateChainHaste(Player player, ServerLevel level, ItemStack tool, BlockPos pos, BlockState state) {
        float bonusPerBlock = EnchantmentUtil.itemValue(level, tool, ModEnchantmentEffectComponents.CHAIN_HASTE_BONUS.get());
        if (bonusPerBlock <= 0) {
            return;
        }
        MiningStreak streak = MINING_STREAKS.get(player.getUUID());
        if (streak != null && streak.lastBlock.is(state.getBlock())
                && System.currentTimeMillis() - streak.lastTime < CHAIN_HASTE_WINDOW_MS) {
            streak.streak++;
            streak.lastPos = pos;
            streak.lastBlock = state;
            streak.lastTime = System.currentTimeMillis();
            double bonus = Math.min(streak.streak * bonusPerBlock, CHAIN_HASTE_BONUS_CAP_PERCENT / 100.0);
            applyMiningSpeed(player, bonus);
        } else {
            removeMiningSpeed(player);
            MINING_STREAKS.put(player.getUUID(), new MiningStreak(pos, state));
        }
    }

    /** 由 {@link ToolPlayerTickEvents} 每 tick 调用，衰减过期的连锁急迫。 */
    static void decayChainHaste(Player player) {
        MiningStreak streak = MINING_STREAKS.get(player.getUUID());
        if (streak != null && System.currentTimeMillis() - streak.lastTime >= CHAIN_HASTE_WINDOW_MS) {
            removeMiningSpeed(player);
            MINING_STREAKS.remove(player.getUUID());
        }
    }

    private static void applyMiningSpeed(Player player, double bonus) {
        AttributeInstance attribute = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (attribute == null) {
            return;
        }
        attribute.removeModifier(CHAIN_HASTE_MODIFIER_ID);
        attribute.addTransientModifier(new AttributeModifier(
                CHAIN_HASTE_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private static void removeMiningSpeed(Player player) {
        AttributeInstance attribute = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (attribute != null) {
            attribute.removeModifier(CHAIN_HASTE_MODIFIER_ID);
        }
    }

    // ---- 公共 ----

    private static int fortuneLevel(ServerLevel level, ItemStack tool) {
        Holder<Enchantment> fortune = EnchantmentUtil.holder(level.registryAccess(), Enchantments.FORTUNE);
        return EnchantmentUtil.levelOn(fortune, tool);
    }

    /** 时运加成：与原实现一致，bonus = rand(fortune + 2) - 1，最小 1 */
    private static void applyFortuneCount(ItemStack stack, ServerLevel level, ItemStack tool) {
        int fortune = fortuneLevel(level, tool);
        if (fortune > 0) {
            int bonus = level.getRandom().nextInt(fortune + 2) - 1;
            if (bonus < 1) {
                bonus = 1;
            }
            stack.setCount(stack.getCount() * bonus);
        }
    }

    private static void spawnItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        level.addFreshEntity(new ItemEntity(level,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
    }

    private static List<ItemStack> oreBlockDrops(ServerLevel level) {
        if (oreBlockDrops == null) {
            List<ItemStack> stacks = new ArrayList<>();
            level.registryAccess().lookupOrThrow(Registries.BLOCK).getOrThrow(CONVENTIONAL_ORES)
                    .forEach(holder -> stacks.add(new ItemStack(holder.value().asItem())));
            oreBlockDrops = stacks;
        }
        return oreBlockDrops;
    }

    private ToolBlockBreakEvents() {
    }

    private static final class MiningStreak {
        BlockPos lastPos;
        BlockState lastBlock;
        int streak;
        long lastTime;

        MiningStreak(BlockPos pos, BlockState state) {
            this.lastPos = pos;
            this.lastBlock = state;
            this.streak = 1;
            this.lastTime = System.currentTimeMillis();
        }
    }
}
