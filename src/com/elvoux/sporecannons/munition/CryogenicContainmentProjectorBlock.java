/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlock
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
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

import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
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

public final class CryogenicContainmentProjectorBlock
extends KineticBlock
implements IBE<CryogenicContainmentProjectorBlockEntity> {
    public static final BooleanProperty ACTIVE = BooleanProperty.create((String)"active");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public CryogenicContainmentProjectorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)ACTIVE, (Comparable)Boolean.valueOf(false))).setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{ACTIVE, FACING});
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite());
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue((Property)FACING, (Comparable)rotation.rotate((Direction)state.getValue((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue((Property)FACING)));
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    public Class<CryogenicContainmentProjectorBlockEntity> getBlockEntityClass() {
        return CryogenicContainmentProjectorBlockEntity.class;
    }

    public BlockEntityType<? extends CryogenicContainmentProjectorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SporeCannonsBlockEntities.CRYOGENIC_CONTAINMENT_PROJECTOR.get();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        return projector != null && projector.loadDirect(player, stack) ? ItemInteractionResult.sidedSuccess((boolean)level.isClientSide) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        if (projector == null) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            serverPlayer.openMenu((MenuProvider)projector, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess((boolean)level.isClientSide);
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        CryogenicContainmentProjectorBlockEntity projector;
        if (!state.is(newState.getBlock()) && (projector = (CryogenicContainmentProjectorBlockEntity)this.getBlockEntity((BlockGetter)level, pos)) != null) {
            projector.dropContents();
        }
        IBE.onRemove((BlockState)state, (Level)level, (BlockPos)pos, (BlockState)newState);
    }

    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        return projector == null ? 0 : projector.comparatorLevel();
    }
}

