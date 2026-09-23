package com.chinaex123.redstone_enchants.event.tool;

import com.chinaex123.redstone_enchants.RedstoneEnchants;
import com.chinaex123.redstone_enchants.init.ModEnchantmentEffectComponents;
import com.chinaex123.redstone_enchants.init.ModEnchantments;
import com.chinaex123.redstone_enchants.util.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
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
 * 这里负责方块破坏前后的挖掘效果：
 * 连锁急迫在破坏前结算；{@link BlockDropsEvent} 上先跑精通采集复制掉落，再由自动熔炼熔炼（启用时跳过地质学 / 点石成金），
 * 未启用自动熔炼时则追加地质学 / 点石成金的加成掉落；连锁砍树与区域挖掘仍在破坏前发起。
 * 旧实现是每个附魔一个独立订阅者，执行顺序取决于注册顺序、且互相之间会因事件取消而不确定。
 */
@EventBusSubscriber(modid = RedstoneEnchants.MOD_ID)
public final class ToolBlockBreakEvents {
    /** 连锁砍树单次最多连带破坏的原木数（原服务端配置默认值，配置系统已移除） */
    private static final int TIMBER_CHAIN_LIMIT = 512;
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

        boolean autoSmelt = !player.isCreative()
                && EnchantmentHelper.has(tool, ModEnchantmentEffectComponents.AUTO_SMELT.get());
        // 掉落相关效果（精通采集 / 自动熔炼 / 地质学 / 点石成金）全在 BlockDropsEvent 中处理；
        // 这里保持旧行为，自动熔炼启用时不发起连锁砍树与区域挖掘。
        if (!autoSmelt) {
            timberChainBreak(event, player, level, tool);
            excavatorAreaBreak(event, player, level, tool);
        }
    }

    /**
     * 方块掉落的统一后处理：精通采集复制掉落项 → 自动熔炼替换掉落项（启用时跳过地质学 / 点石成金）
     * → 地质学 / 点石成金追加掉落项。
     * <p>这些加成掉落都放在这里而不是破坏前的 {@link BlockEvent.BreakEvent}，是为了让它们作用于原版（含
     * 时运、精准采集与其它模组）已经算好的掉落列表，而不是自行重算一遍。
     * <p>精通采集排在自动熔炼之前：两者同时存在时先把掉落复制一份，复制出的原矿随后一起进熔炼，
     * 也就是"先结算双倍掉落，再熔炼双倍的数量"。
     */
    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        if (player.isCreative()) {
            return;
        }
        ItemStack tool = event.getTool();
        if (tool.isEmpty()) {
            return;
        }
        List<ItemEntity> drops = event.getDrops();
        if (drops.isEmpty()) {
            return;
        }
        masterGathererDuplicateDrops(event, drops);
        // 保持旧行为：自动熔炼启用时不追加地质学 / 点石成金的加成掉落（精通采集已在上一步结算）。
        if (EnchantmentHelper.has(tool, ModEnchantmentEffectComponents.AUTO_SMELT.get())) {
            smeltDrops(event, drops);
            return;
        }
        geologyBonusDrop(event, tool);
        goldfingerBonusDrop(event, tool);
    }

    private static void smeltDrops(BlockDropsEvent event, List<ItemEntity> drops) {
        List<ItemEntity> originalDrops = new ArrayList<>(drops);
        drops.clear();
        for (ItemEntity drop : originalDrops) {
            appendSmeltedDrop(drops, drop, smelt(drop.getItem(), event.getLevel()));
        }
    }

    // ---- 自动熔炼 ----

    private static void appendSmeltedDrop(List<ItemEntity> drops, ItemEntity drop, ItemStack smelted) {
        ItemStack input = drop.getItem();
        if (smelted.isEmpty()
                || (ItemStack.isSameItemSameComponents(input, smelted) && input.getCount() == smelted.getCount())) {
            drops.add(drop);
            return;
        }

        int maxStackSize = Math.max(1, smelted.getMaxStackSize());
        int remaining = smelted.getCount();
        int firstCount = Math.min(maxStackSize, remaining);
        drop.setItem(smelted.copyWithCount(firstCount));
        drops.add(drop);
        remaining -= firstCount;

        while (remaining > 0) {
            int extraCount = Math.min(maxStackSize, remaining);
            ItemEntity extra = drop.copy();
            extra.setItem(smelted.copyWithCount(extraCount));
            if (drop.hasPickUpDelay()) {
                extra.setDefaultPickUpDelay();
            }
            drops.add(extra);
            remaining -= extraCount;
        }
    }

    private static ItemStack smelt(ItemStack input, ServerLevel level) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level)
                .map(holder -> {
                    ItemStack result = holder.value().getResultItem(level.registryAccess()).copy();
                    if (result.isEmpty()) {
                        return input.copy();
                    }
                    result.setCount(input.getCount() * result.getCount());
                    return result;
                })
                .orElseGet(input::copy);
    }

    // ---- 地质学：挖石头概率掉矿石 ----

    private static void geologyBonusDrop(BlockDropsEvent event, ItemStack tool) {
        if (!GEOLOGY_STONES.contains(event.getState().getBlock())) {
            return;
        }
        ServerLevel level = event.getLevel();
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
        addBonusDrop(event, oreDrop);
    }

    // ---- 点石成金：挖石头概率掉金粒 ----

    private static void goldfingerBonusDrop(BlockDropsEvent event, ItemStack tool) {
        if (!event.getState().is(CONVENTIONAL_STONES)) {
            return;
        }
        ServerLevel level = event.getLevel();
        float chance = EnchantmentUtil.itemValue(level, tool, ModEnchantmentEffectComponents.STONE_TO_GOLD_CHANCE.get());
        if (chance <= 0 || level.getRandom().nextFloat() >= chance) {
            return;
        }
        ItemStack goldNuggets = new ItemStack(Items.GOLD_NUGGET, 1 + level.getRandom().nextInt(3));
        applyFortuneCount(goldNuggets, level, tool);
        addBonusDrop(event, goldNuggets);
    }

    // ---- 精通采集：挖矿石概率双倍掉落 ----

    private static void masterGathererDuplicateDrops(BlockDropsEvent event, List<ItemEntity> drops) {
        if (!Item.byBlock(event.getState().getBlock()).builtInRegistryHolder().is(CONVENTIONAL_ORE_ITEMS)) {
            return;
        }
        ServerLevel level = event.getLevel();
        ItemStack tool = event.getTool();
        float chance = Math.min(EnchantmentUtil.itemValue(level, tool,
                ModEnchantmentEffectComponents.ORE_DOUBLE_DROP_CHANCE.get()), 1.0F);
        if (chance <= 0 || level.getRandom().nextFloat() >= chance) {
            return;
        }
        // 复制已经算好的掉落项，而不是重新计算一遍：时运、精准采集与其它模组的改动都随之翻倍。
        // 副本由 ItemEntity#copy() 生成，位置与初速度与原掉落一致，堆叠数不超上限。
        // 本步排在自动熔炼之前，所以同时附两者时复制出的原矿会一起进熔炼。
        int originalCount = drops.size();
        for (int i = 0; i < originalCount; i++) {
            drops.add(drops.get(i).copy());
        }
    }

    // ---- 连锁砍树 ----

    private static void timberChainBreak(BlockEvent.BreakEvent event, Player player, ServerLevel level, ItemStack tool) {
        BlockState startState = event.getState();
        if (!startState.is(BlockTags.LOGS)) {
            return;
        }
        if (EnchantmentUtil.levelOf(level.registryAccess(), tool, ModEnchantments.TIMBER) <= 0) {
            return;
        }
        int limit = TIMBER_CHAIN_LIMIT;
        BlockPos pos = event.getPos();
        for (BlockPos logPos : findConnectedLogs(level, pos, startState, limit)) {
            BlockState state = level.getBlockState(logPos);
            List<ItemStack> drops = blockDrops(state, level, logPos, level.getBlockEntity(logPos), player, tool);
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
            List<ItemStack> drops = blockDrops(targetState, level, pos, level.getBlockEntity(pos), player, tool);
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

    /**
     * 方块掉落计算：等价于已弃用的 {@code Block.getDrops(state, level, pos, blockEntity, entity, tool)}
     * （它的实现就是下面的写法），改走 {@code BlockState#getDrops(LootParams.Builder)}。
     */
    private static List<ItemStack> blockDrops(BlockState state, ServerLevel level, BlockPos pos,
                                             BlockEntity blockEntity, Entity entity, ItemStack tool) {
        return state.getDrops(new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity));
    }

    private static int fortuneLevel(ServerLevel level, ItemStack tool) {
        return EnchantmentUtil.levelOf(level.registryAccess(), tool, Enchantments.FORTUNE);
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

    /** 直接在世界里生成掉落物，位置取方块中心（连锁砍树 / 区域挖掘使用，它们不走方块掉落事件） */
    private static void spawnItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        level.addFreshEntity(new ItemEntity(level,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
    }

    /** 把追加掉落放进 {@link BlockDropsEvent} 的掉落列表，位置取方块中心 */
    private static void addBonusDrop(BlockDropsEvent event, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        BlockPos pos = event.getPos();
        event.getDrops().add(new ItemEntity(event.getLevel(),
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
