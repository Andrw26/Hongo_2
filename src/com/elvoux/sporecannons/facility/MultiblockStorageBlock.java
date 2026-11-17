/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package com.elvoux.sporecannons.facility;

import com.elvoux.sporecannons.facility.MultiblockPart;
import com.elvoux.sporecannons.facility.MultiblockStorageBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.mojang.serialization.MapCodec;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class MultiblockStorageBlock
extends BaseEntityBlock {
    public static final EnumProperty<MultiblockPart> X_PART = EnumProperty.create((String)"x_part", MultiblockPart.class);
    public static final EnumProperty<MultiblockPart> Y_PART = EnumProperty.create((String)"y_part", MultiblockPart.class);
    public static final EnumProperty<MultiblockPart> Z_PART = EnumProperty.create((String)"z_part", MultiblockPart.class);
    public static final IntegerProperty SIZE = IntegerProperty.create((String)"size", (int)1, (int)3);
    private static final MapCodec<MultiblockStorageBlock> SAMPLE_CODEC = MultiblockStorageBlock.simpleCodec(properties -> new MultiblockStorageBlock((BlockBehaviour.Properties)properties, StorageKind.SAMPLE));
    private static final MapCodec<MultiblockStorageBlock> CRYOGEL_CODEC = MultiblockStorageBlock.simpleCodec(properties -> new MultiblockStorageBlock((BlockBehaviour.Properties)properties, StorageKind.CRYOGEL));
    private final StorageKind kind;
    private static final Comparator<BlockPos> POSITION_ORDER = Comparator.comparingInt(pos -> pos.getY()).thenComparingInt(pos -> pos.getZ()).thenComparingInt(pos -> pos.getX());

    public MultiblockStorageBlock(BlockBehaviour.Properties properties, StorageKind kind) {
        super(properties);
        this.kind = kind;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(X_PART, (Comparable)((Object)MultiblockPart.SINGLE))).setValue(Y_PART, (Comparable)((Object)MultiblockPart.SINGLE))).setValue(Z_PART, (Comparable)((Object)MultiblockPart.SINGLE))).setValue((Property)SIZE, (Comparable)Integer.valueOf(1)));
    }

    public StorageKind kind() {
        return this.kind;
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return this.kind == StorageKind.SAMPLE ? SAMPLE_CODEC : CRYOGEL_CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{X_PART, Y_PART, Z_PART, SIZE});
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MultiblockStorageBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return MultiblockStorageBlock.createTickerHelper(type, (BlockEntityType)((BlockEntityType)SporeCannonsBlockEntities.MULTIBLOCK_STORAGE.get()), (world, pos, current, storage) -> {
            if (world.getGameTime() % 100L == (long)Math.floorMod(pos.hashCode(), 100)) {
                MultiblockStorageBlock.refreshStructure((ServerLevel)world, pos);
            }
        });
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MultiblockStorageBlockEntity) {
            MultiblockStorageBlockEntity storage = (MultiblockStorageBlockEntity)blockEntity;
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.openMenu((MenuProvider)storage, buffer -> buffer.writeBlockPos(pos));
            }
            return InteractionResult.sidedSuccess((boolean)level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (!level.isClientSide) {
            level.scheduleTick(pos, (Block)this, 1);
        }
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moved) {
        super.neighborChanged(state, level, pos, block, fromPos, moved);
        if (!level.isClientSide) {
            level.scheduleTick(pos, (Block)this, 1);
        }
    }

    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        MultiblockStorageBlock.refreshStructure(level, pos);
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        Direction[] directionArray;
        if (!state.is(newState.getBlock()) && (directionArray = level.getBlockEntity(pos)) instanceof MultiblockStorageBlockEntity) {
            MultiblockStorageBlockEntity storage = (MultiblockStorageBlockEntity)directionArray;
            storage.dropLocalContents();
            for (Direction direction : Direction.values()) {
                BlockPos neighbour = pos.relative(direction);
                if (!level.getBlockState(neighbour).is((Block)this)) continue;
                level.scheduleTick(neighbour, (Block)this, 1);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    public static List<BlockPos> structurePositions(LevelAccessor level, BlockPos origin) {
        Block block = level.getBlockState(origin).getBlock();
        List<BlockPos> connected = MultiblockStorageBlock.connectedPositions(level, origin, block);
        int size = MultiblockStorageBlock.detectCube(level, connected, block);
        if (size < 2) {
            return List.of((Object)origin.immutable());
        }
        connected.sort(POSITION_ORDER);
        return connected;
    }

    private static void refreshStructure(ServerLevel level, BlockPos origin) {
        Block block = level.getBlockState(origin).getBlock();
        if (!(block instanceof MultiblockStorageBlock)) {
            return;
        }
        List<BlockPos> connected = MultiblockStorageBlock.connectedPositions((LevelAccessor)level, origin, block);
        int size = MultiblockStorageBlock.detectCube((LevelAccessor)level, connected, block);
        if (size < 2) {
            for (BlockPos pos : connected) {
                MultiblockStorageBlock.setPartState(level, pos, 1, MultiblockPart.SINGLE, MultiblockPart.SINGLE, MultiblockPart.SINGLE);
            }
            return;
        }
        int minX = connected.stream().mapToInt(Vec3i::getX).min().orElse(origin.getX());
        int minY = connected.stream().mapToInt(Vec3i::getY).min().orElse(origin.getY());
        int minZ = connected.stream().mapToInt(Vec3i::getZ).min().orElse(origin.getZ());
        for (BlockPos pos : connected) {
            MultiblockStorageBlock.setPartState(level, pos, size, MultiblockStorageBlock.part(pos.getX() - minX, size), MultiblockStorageBlock.part(pos.getY() - minY, size), MultiblockStorageBlock.part(pos.getZ() - minZ, size));
        }
    }

    private static void setPartState(ServerLevel level, BlockPos pos, int size, MultiblockPart x, MultiblockPart y, MultiblockPart z) {
        BlockState desired;
        BlockState state = level.getBlockState(pos);
        if (!state.equals(desired = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue((Property)SIZE, (Comparable)Integer.valueOf(size))).setValue(X_PART, (Comparable)((Object)x))).setValue(Y_PART, (Comparable)((Object)y))).setValue(Z_PART, (Comparable)((Object)z)))) {
            level.setBlock(pos, desired, 2);
        }
    }

    private static MultiblockPart part(int coordinate, int size) {
        if (coordinate == 0) {
            return MultiblockPart.MIN;
        }
        if (coordinate == size - 1) {
            return MultiblockPart.MAX;
        }
        return MultiblockPart.MIDDLE;
    }

    private static int detectCube(LevelAccessor level, List<BlockPos> positions, Block block) {
        if (positions.size() == 1) {
            return 1;
        }
        if (positions.size() != 8 && positions.size() != 27) {
            return 0;
        }
        int minX = positions.stream().mapToInt(Vec3i::getX).min().orElse(0);
        int minY = positions.stream().mapToInt(Vec3i::getY).min().orElse(0);
        int minZ = positions.stream().mapToInt(Vec3i::getZ).min().orElse(0);
        int maxX = positions.stream().mapToInt(Vec3i::getX).max().orElse(0);
        int maxY = positions.stream().mapToInt(Vec3i::getY).max().orElse(0);
        int maxZ = positions.stream().mapToInt(Vec3i::getZ).max().orElse(0);
        int size = maxX - minX + 1;
        if (size != 2 && size != 3 || maxY - minY + 1 != size || maxZ - minZ + 1 != size) {
            return 0;
        }
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (level.getBlockState(new BlockPos(x, y, z)).is(block)) continue;
                    return 0;
                }
            }
        }
        return size;
    }

    private static List<BlockPos> connectedPositions(LevelAccessor level, BlockPos origin, Block block) {
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        queue.add(origin.immutable());
        while (!queue.isEmpty() && visited.size() <= 64) {
            BlockPos current = (BlockPos)queue.removeFirst();
            if (visited.contains(current) || !level.getBlockState(current).is(block)) continue;
            visited.add(current);
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (visited.contains(next) || !level.getBlockState(next).is(block)) continue;
                queue.addLast(next.immutable());
            }
        }
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (BlockPos pos : visited) {
            if (!level.getBlockState(pos).is(block)) continue;
            result.add(pos);
        }
        return result;
    }

    public static enum StorageKind {
        SAMPLE(48),
        CRYOGEL(9);

        private final int slotsPerModule;

        private StorageKind(int slotsPerModule) {
            this.slotsPerModule = slotsPerModule;
        }

        public int slotsPerModule() {
            return this.slotsPerModule;
        }
    }
}

