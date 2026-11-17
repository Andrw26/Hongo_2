/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntity
 *  rbasamoyai.createbigcannons.munitions.big_cannon.ProjectileBlock
 *  rbasamoyai.createbigcannons.munitions.big_cannon.he_shell.HEShellBlock
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsEntityTypes;
import com.elvoux.sporecannons.registry.SporeCannonsSounds;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.ProjectileBlock;
import rbasamoyai.createbigcannons.munitions.big_cannon.he_shell.HEShellBlock;

public final class FreezeShellBlock
extends HEShellBlock {
    private static final MapCodec<ProjectileBlock> CODEC = FreezeShellBlock.simpleCodec(FreezeShellBlock::new);

    public FreezeShellBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return SporeCannonsSounds.freezeShellSoundType();
    }

    public BlockEntityType<? extends FuzedBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SporeCannonsBlockEntities.FREEZE_SHELL.get();
    }

    public EntityType<? extends FreezeShellProjectile> getAssociatedEntityType() {
        return (EntityType)SporeCannonsEntityTypes.FREEZE_SHELLS.get((Object)FreezeShellTier.fromBlock((Block)this)).get();
    }

    @Nullable
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> type) {
        if (type != SporeCannonsBlockEntities.FREEZE_SHELL.get()) {
            return null;
        }
        return (tickerLevel, tickerPos, tickerState, blockEntity) -> ((FuzedBlockEntity)blockEntity).tick();
    }
}

