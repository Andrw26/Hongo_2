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
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.elvoux.sporecannons.industry;

import com.elvoux.sporecannons.industry.IndustryMachineKind;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlockEntity;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public final class WhiteoutIndustryMachineBlock
extends KineticBlock
implements IBE<WhiteoutIndustryMachineBlockEntity> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private final IndustryMachineKind kind;

    public WhiteoutIndustryMachineBlock(BlockBehaviour.Properties properties, IndustryMachineKind kind) {
        super(properties);
        this.kind = kind;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    public IndustryMachineKind kind() {
        return this.kind;
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    public Class<WhiteoutIndustryMachineBlockEntity> getBlockEntityClass() {
        return WhiteoutIndustryMachineBlockEntity.class;
    }

    public BlockEntityType<? extends WhiteoutIndustryMachineBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SporeCannonsBlockEntities.WHITEOUT_INDUSTRY_MACHINE.get();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        WhiteoutIndustryMachineBlockEntity machine = (WhiteoutIndustryMachineBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        return machine != null && machine.addWaterBucket(player, stack) ? ItemInteractionResult.sidedSuccess((boolean)level.isClientSide) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        WhiteoutIndustryMachineBlockEntity machine = (WhiteoutIndustryMachineBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        if (machine == null) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            serverPlayer.openMenu((MenuProvider)machine, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess((boolean)level.isClientSide);
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        WhiteoutIndustryMachineBlockEntity machine;
        if (!state.is(newState.getBlock()) && (machine = (WhiteoutIndustryMachineBlockEntity)this.getBlockEntity((BlockGetter)level, pos)) != null) {
            machine.dropContents();
        }
        IBE.onRemove((BlockState)state, (Level)level, (BlockPos)pos, (BlockState)newState);
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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{FACING});
    }
}

