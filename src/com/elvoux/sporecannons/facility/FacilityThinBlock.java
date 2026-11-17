/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.elvoux.sporecannons.facility;

import com.elvoux.sporecannons.facility.FacilityFacingBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FacilityThinBlock
extends FacilityFacingBlock {
    public static final MapCodec<FacilityThinBlock> CODEC = FacilityThinBlock.simpleCodec(FacilityThinBlock::new);
    private static final VoxelShape NORTH_SOUTH = FacilityThinBlock.box((double)0.0, (double)0.0, (double)7.0, (double)16.0, (double)16.0, (double)9.0);
    private static final VoxelShape EAST_WEST = FacilityThinBlock.box((double)7.0, (double)0.0, (double)0.0, (double)9.0, (double)16.0, (double)16.0);

    public FacilityThinBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends FacilityFacingBlock> codec() {
        return CODEC;
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ((Direction)state.getValue((Property)FACING)).getAxis() == Direction.Axis.Z ? NORTH_SOUTH : EAST_WEST;
    }
}

