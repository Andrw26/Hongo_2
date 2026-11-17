/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.context.BlockPlaceContext
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FacilityMountedBlock
extends FacilityFacingBlock {
    public static final MapCodec<FacilityMountedBlock> CODEC = FacilityMountedBlock.simpleCodec(FacilityMountedBlock::new);
    private static final VoxelShape NORTH = FacilityMountedBlock.box((double)0.0, (double)1.0, (double)12.0, (double)16.0, (double)15.0, (double)16.0);
    private static final VoxelShape SOUTH = FacilityMountedBlock.box((double)0.0, (double)1.0, (double)0.0, (double)16.0, (double)15.0, (double)4.0);
    private static final VoxelShape WEST = FacilityMountedBlock.box((double)12.0, (double)1.0, (double)0.0, (double)16.0, (double)15.0, (double)16.0);
    private static final VoxelShape EAST = FacilityMountedBlock.box((double)0.0, (double)1.0, (double)0.0, (double)4.0, (double)15.0, (double)16.0);

    public FacilityMountedBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends FacilityFacingBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        Direction facing = clicked.getAxis().isHorizontal() ? clicked : context.getHorizontalDirection().getOpposite();
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)facing);
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch ((Direction)state.getValue((Property)FACING)) {
            case Direction.SOUTH -> SOUTH;
            case Direction.WEST -> WEST;
            case Direction.EAST -> EAST;
            default -> NORTH;
        };
    }
}

