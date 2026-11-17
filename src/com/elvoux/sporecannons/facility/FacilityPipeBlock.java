/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.elvoux.sporecannons.facility;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FacilityPipeBlock
extends RotatedPillarBlock {
    public static final MapCodec<FacilityPipeBlock> CODEC = FacilityPipeBlock.simpleCodec(FacilityPipeBlock::new);
    private static final VoxelShape X_SHAPE = Shapes.or((VoxelShape)FacilityPipeBlock.box((double)0.0, (double)5.0, (double)5.0, (double)16.0, (double)11.0, (double)11.0), (VoxelShape[])new VoxelShape[]{FacilityPipeBlock.box((double)0.0, (double)3.0, (double)3.0, (double)3.0, (double)13.0, (double)13.0), FacilityPipeBlock.box((double)13.0, (double)3.0, (double)3.0, (double)16.0, (double)13.0, (double)13.0)});
    private static final VoxelShape Y_SHAPE = Shapes.or((VoxelShape)FacilityPipeBlock.box((double)5.0, (double)0.0, (double)5.0, (double)11.0, (double)16.0, (double)11.0), (VoxelShape[])new VoxelShape[]{FacilityPipeBlock.box((double)3.0, (double)0.0, (double)3.0, (double)13.0, (double)3.0, (double)13.0), FacilityPipeBlock.box((double)3.0, (double)13.0, (double)3.0, (double)13.0, (double)16.0, (double)13.0)});
    private static final VoxelShape Z_SHAPE = Shapes.or((VoxelShape)FacilityPipeBlock.box((double)5.0, (double)5.0, (double)0.0, (double)11.0, (double)11.0, (double)16.0), (VoxelShape[])new VoxelShape[]{FacilityPipeBlock.box((double)3.0, (double)3.0, (double)0.0, (double)13.0, (double)13.0, (double)3.0), FacilityPipeBlock.box((double)3.0, (double)3.0, (double)13.0, (double)13.0, (double)13.0, (double)16.0)});

    public FacilityPipeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch ((Direction.Axis)state.getValue((Property)AXIS)) {
            case Direction.Axis.X -> X_SHAPE;
            case Direction.Axis.Z -> Z_SHAPE;
            default -> Y_SHAPE;
        };
    }
}

