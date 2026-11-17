/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlock
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CDURefuelingStationBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public final class CDURefuelingStationBlock
extends KineticBlock
implements IBE<CDURefuelingStationBlockEntity> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty PIPE_CONNECTED = BooleanProperty.create((String)"pipe_connected");

    public CDURefuelingStationBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue((Property)PIPE_CONNECTED, (Comparable)Boolean.valueOf(false)));
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    public Class<CDURefuelingStationBlockEntity> getBlockEntityClass() {
        return CDURefuelingStationBlockEntity.class;
    }

    public BlockEntityType<? extends CDURefuelingStationBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SporeCannonsBlockEntities.CDU_REFUELING_STATION.get();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        CDURefuelingStationBlockEntity station = (CDURefuelingStationBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        return station != null && station.addWaterBucket(player, stack) ? ItemInteractionResult.sidedSuccess((boolean)level.isClientSide) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        CDURefuelingStationBlockEntity station = (CDURefuelingStationBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        if (station == null) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            serverPlayer.openMenu((MenuProvider)station, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess((boolean)level.isClientSide);
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        CDURefuelingStationBlockEntity station;
        if (!state.is(newState.getBlock()) && (station = (CDURefuelingStationBlockEntity)this.getBlockEntity((BlockGetter)level, pos)) != null) {
            station.dropContents();
        }
        IBE.onRemove((BlockState)state, (Level)level, (BlockPos)pos, (BlockState)newState);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)facing)).setValue((Property)PIPE_CONNECTED, (Comparable)Boolean.valueOf(CDURefuelingStationBlock.isFluidPipe(context.getLevel().getBlockState(context.getClickedPos().relative(facing)))));
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);
        Direction front = (Direction)state.getValue((Property)FACING);
        if (fromPos.equals((Object)pos.relative(front))) {
            boolean connected = CDURefuelingStationBlock.isFluidPipe(level.getBlockState(fromPos));
            if ((Boolean)state.getValue((Property)PIPE_CONNECTED) != connected) {
                level.setBlock(pos, (BlockState)state.setValue((Property)PIPE_CONNECTED, (Comparable)Boolean.valueOf(connected)), 3);
            }
        }
    }

    private static boolean isFluidPipe(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey((Object)state.getBlock());
        return id.getNamespace().equals("create") && id.getPath().contains("fluid_pipe");
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{FACING, PIPE_CONNECTED});
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue((Property)FACING, (Comparable)rotation.rotate((Direction)state.getValue((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation((Direction)state.getValue((Property)FACING)));
    }
}

