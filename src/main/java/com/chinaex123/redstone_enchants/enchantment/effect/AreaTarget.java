package com.chinaex123.redstone_enchants.enchantment.effect;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * 范围效果的作用对象：以触发实体为中心的范围效果（光环药水、光环点火）共用这套语义。
 * <ul>
 *   <li>{@code all}：范围内所有生物（含触发者自己）</li>
 *   <li>{@code others}：除触发者自己</li>
 *   <li>{@code others_non_player}：除触发者自己，且不含玩家（负面效果用，避免联机时无差别波及）</li>
 *   <li>{@code self}：仅触发者自己</li>
 * </ul>
 */
public enum AreaTarget implements StringRepresentable {
    ALL("all"), OTHERS("others"), OTHERS_NON_PLAYER("others_non_player"), SELF("self");

    public static final Codec<AreaTarget> CODEC = StringRepresentable.fromEnum(AreaTarget::values);

    private final String name;

    AreaTarget(String name) {
        this.name = name;
    }

    /** 这个候选实体是否该受影响（{@code source} 是触发实体，即穿戴者） */
    public boolean includes(Entity source, Entity candidate) {
        return switch (this) {
            case ALL -> true;
            case SELF -> candidate == source;
            case OTHERS -> candidate != source;
            case OTHERS_NON_PLAYER -> candidate != source && !(candidate instanceof Player);
        };
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
