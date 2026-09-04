package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 给实体打 tag（第一印象 first_impression 使用）：命中时给受害者打上
 * 指定 tag，datagen 侧的伤害加成条件用 NBT 谓词检查该 tag 是否存在。
 * <p>等价于原 mark.mcfunction（{@code tag @s add <tag>}）。
 * 原 reset_mark.mcfunction 无任何引用（死代码），不迁移。
 */
public record AddTagEffect(String tag) implements EnchantmentEntityEffect {

    public static final MapCodec<AddTagEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.STRING.fieldOf("tag").forGetter(AddTagEffect::tag)
    ).apply(instance, AddTagEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        entity.addTag(this.tag);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
