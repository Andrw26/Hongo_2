/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  java.lang.MatchException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package com.elvoux.sporecannons.facility;

import com.elvoux.sporecannons.facility.FacilityKind;
import com.elvoux.sporecannons.facility.FacilityStorageBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class FacilityInteractiveBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final MapCodec<FacilityInteractiveBlock> STORAGE_CODEC = FacilityInteractiveBlock.simpleCodec(properties -> new FacilityInteractiveBlock((BlockBehaviour.Properties)properties, FacilityKind.STORAGE));
    private static final MapCodec<FacilityInteractiveBlock> TECHNICAL_CODEC = FacilityInteractiveBlock.simpleCodec(properties -> new FacilityInteractiveBlock((BlockBehaviour.Properties)properties, FacilityKind.TECHNICAL));
    private static final MapCodec<FacilityInteractiveBlock> WORKBENCH_CODEC = FacilityInteractiveBlock.simpleCodec(properties -> new FacilityInteractiveBlock((BlockBehaviour.Properties)properties, FacilityKind.WORKBENCH));
    private static final MapCodec<FacilityInteractiveBlock> TERMINAL_CODEC = FacilityInteractiveBlock.simpleCodec(properties -> new FacilityInteractiveBlock((BlockBehaviour.Properties)properties, FacilityKind.TERMINAL));
    private final FacilityKind kind;

    public FacilityInteractiveBlock(BlockBehaviour.Properties properties, FacilityKind kind) {
        super(properties);
        this.kind = kind;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue((Property)LIT, (Comparable)Boolean.valueOf(false)));
    }

    public FacilityKind kind() {
        return this.kind;
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return switch (this.kind) {
            default -> throw new MatchException(null, null);
            case FacilityKind.STORAGE -> STORAGE_CODEC;
            case FacilityKind.TECHNICAL -> TECHNICAL_CODEC;
            case FacilityKind.WORKBENCH -> WORKBENCH_CODEC;
            case FacilityKind.TERMINAL -> TERMINAL_CODEC;
        };
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, LIT});
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite());
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue((Property)FACING, (Comparable)rotation.rotate((Direction)state.getValue((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation((Direction)state.getValue((Property)FACING)));
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FacilityStorageBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FacilityStorageBlockEntity) {
            FacilityStorageBlockEntity furniture = (FacilityStorageBlockEntity)blockEntity;
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.openMenu((MenuProvider)furniture, buffer -> buffer.writeBlockPos(pos));
            }
            return InteractionResult.sidedSuccess((boolean)level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity blockEntity;
        if (!state.is(newState.getBlock()) && (blockEntity = level.getBlockEntity(pos)) instanceof FacilityStorageBlockEntity) {
            FacilityStorageBlockEntity furniture = (FacilityStorageBlockEntity)blockEntity;
            furniture.dropContents();
            level.updateNeighbourForOutputSignal(pos, (Block)this);
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        int n;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FacilityStorageBlockEntity) {
            FacilityStorageBlockEntity furniture = (FacilityStorageBlockEntity)blockEntity;
            n = furniture.comparatorSignal();
        } else {
            n = 0;
        }
        return n;
    }
}

