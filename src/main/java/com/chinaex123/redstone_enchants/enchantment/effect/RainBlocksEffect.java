package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/**
 * 雨（石锥雨/铁砧雨）：命中点上方 5 格生成 25 个坠落方块（中心 1 + 半径 1 环 8 个
 * 45° 间隔 + 半径 2 环 16 个 22.5° 间隔），用三角函数替代原函数硬编码的 25 行坐标。
 * <p>等价于原 {@code summon falling_block} NBT：Time=1、DropItem=0、CancelDrop=1、
 * HurtEntities=1、FallHurtMax=40、FallDistance=5、FallHurtAmount 按方块（石锥 2.3/铁砧 1.6）。
 * 通过 {@code Entity.load(NBT)} 构造，与 summon 的 NBT 路径一致。
 */
public record RainBlocksEffect(Block block, float fallHurtAmount) implements EnchantmentEntityEffect {

    public static final MapCodec<RainBlocksEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(RainBlocksEffect::block),
            com.mojang.serialization.Codec.floatRange(0.0F, 1024.0F).fieldOf("fall_hurt_amount").forGetter(RainBlocksEffect::fallHurtAmount)
    ).apply(instance, RainBlocksEffect::new));

    private static final double SPAWN_HEIGHT = 5.0;
    private static final int RING1_COUNT = 8;
    private static final int RING2_COUNT = 16;

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        spawnFallingBlock(level, origin.x(), origin.y() + SPAWN_HEIGHT, origin.z());
        for (int k = 0; k < RING1_COUNT; k++) {
            double angle = k * Math.PI / (RING1_COUNT / 2.0);
            spawnFallingBlock(level, origin.x() + Math.cos(angle), origin.y() + SPAWN_HEIGHT, origin.z() + Math.sin(angle));
        }
        for (int k = 0; k < RING2_COUNT; k++) {
            double angle = k * Math.PI / (RING2_COUNT / 2.0);
            spawnFallingBlock(level, origin.x() + 2 * Math.cos(angle), origin.y() + SPAWN_HEIGHT, origin.z() + 2 * Math.sin(angle));
        }
    }

    private void spawnFallingBlock(ServerLevel level, double x, double y, double z) {
        FallingBlockEntity falling = new FallingBlockEntity(EntityType.FALLING_BLOCK, level);
        CompoundTag tag = new CompoundTag();
        tag.put("BlockState", NbtUtils.writeBlockState(this.block.defaultBlockState()));
        tag.putInt("Time", 1);
        tag.putBoolean("DropItem", false);
        tag.putBoolean("CancelDrop", true);
        tag.putBoolean("HurtEntities", true);
        tag.putInt("FallHurtMax", 40);
        tag.putFloat("FallDistance", 5.0F);
        tag.putFloat("FallHurtAmount", this.fallHurtAmount);
        falling.load(tag);
        falling.setPos(x, y, z);
        level.addFreshEntity(falling);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
