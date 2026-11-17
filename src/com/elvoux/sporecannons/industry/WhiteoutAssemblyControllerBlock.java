/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
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
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package com.elvoux.sporecannons.industry;

import com.elvoux.sporecannons.industry.AssemblyKind;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class WhiteoutAssemblyControllerBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final MapCodec<WhiteoutAssemblyControllerBlock> PROJECTOR_CODEC = WhiteoutAssemblyControllerBlock.simpleCodec(properties -> new WhiteoutAssemblyControllerBlock((BlockBehaviour.Properties)properties, AssemblyKind.PROJECTOR));
    private static final MapCodec<WhiteoutAssemblyControllerBlock> REFUELLING_CODEC = WhiteoutAssemblyControllerBlock.simpleCodec(properties -> new WhiteoutAssemblyControllerBlock((BlockBehaviour.Properties)properties, AssemblyKind.REFUELLING));
    private final AssemblyKind kind;

    public WhiteoutAssemblyControllerBlock(BlockBehaviour.Properties properties, AssemblyKind kind) {
        super(properties);
        this.kind = kind;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    public AssemblyKind kind() {
        return this.kind;
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return this.kind == AssemblyKind.PROJECTOR ? PROJECTOR_CODEC : REFUELLING_CODEC;
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WhiteoutAssemblyControllerBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return WhiteoutAssemblyControllerBlock.createTickerHelper(type, (BlockEntityType)((BlockEntityType)SporeCannonsBlockEntities.WHITEOUT_ASSEMBLY_CONTROLLER.get()), (world, pos, current, controller) -> controller.tick());
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        WhiteoutAssemblyControllerBlockEntity controller;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof WhiteoutAssemblyControllerBlockEntity && (controller = (WhiteoutAssemblyControllerBlockEntity)blockEntity).addWaterBucket(player, stack)) {
            return ItemInteractionResult.sidedSuccess((boolean)level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof WhiteoutAssemblyControllerBlockEntity)) {
            return InteractionResult.PASS;
        }
        WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)blockEntity;
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            serverPlayer.openMenu((MenuProvider)controller, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess((boolean)level.isClientSide);
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity blockEntity;
        if (!state.is(newState.getBlock()) && (blockEntity = level.getBlockEntity(pos)) instanceof WhiteoutAssemblyControllerBlockEntity) {
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)blockEntity;
            controller.dropContents();
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moved) {
        BlockEntity blockEntity;
        super.neighborChanged(state, level, pos, block, fromPos, moved);
        if (!level.isClientSide && (blockEntity = level.getBlockEntity(pos)) instanceof WhiteoutAssemblyControllerBlockEntity) {
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)blockEntity;
            controller.requestValidation();
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
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
}

