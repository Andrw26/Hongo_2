/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.EntityTypeTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.elvoux.sporecannons.field;

import com.elvoux.sporecannons.SporeCannons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class WhiteoutTargeting {
    public static final TagKey<EntityType<?>> CONTAINMENT_AFFECTED = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)SporeCannons.id("containment_affected"));
    public static final TagKey<EntityType<?>> CONTAINMENT_IMMUNE = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)SporeCannons.id("containment_immune"));
    public static final TagKey<Block> CONTAINMENT_CLEANABLE = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)SporeCannons.id("containment_cleanable"));
    public static final TagKey<Block> CONTAINMENT_BLOCK_IMMUNE = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)SporeCannons.id("containment_immune"));

    private WhiteoutTargeting() {
    }

    public static boolean isAffected(Entity entity) {
        if (entity.getType().is(CONTAINMENT_IMMUNE)) {
            return false;
        }
        if (entity.getType().is(CONTAINMENT_AFFECTED) || entity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
            return true;
        }
        ResourceLocation id = entity.getType().builtInRegistryHolder().key().location();
        return "spore".equals(id.getNamespace());
    }

    public static boolean isExplicitlyCleanable(BlockState state) {
        return state.is(CONTAINMENT_CLEANABLE) && !state.is(CONTAINMENT_BLOCK_IMMUNE);
    }

    public static boolean isBlockImmune(BlockState state) {
        return state.is(CONTAINMENT_BLOCK_IMMUNE);
    }
}

