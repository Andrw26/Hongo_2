/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SnowLayerBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class FreezeSnowManager {
    private static final Map<ServerLevel, ArrayDeque<SnowTask>> TASKS = new WeakHashMap<ServerLevel, ArrayDeque<SnowTask>>();
    private static final Map<ServerLevel, ArrayDeque<SnowRecord>> MELTING = new WeakHashMap<ServerLevel, ArrayDeque<SnowRecord>>();

    private FreezeSnowManager() {
    }

    public static void schedule(ServerLevel level, BlockPos center, FreezeShellTier tier) {
        if (!((Boolean)FreezeConfig.PLACE_SNOW.get()).booleanValue() || level.dimensionType().ultraWarm()) {
            return;
        }
        TASKS.computeIfAbsent(level, ignored -> new ArrayDeque()).addLast(new SnowTask(level, center, tier));
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        FreezeSnowManager.processPlacement(level2);
        FreezeSnowManager.processMelting(level2);
    }

    private static void processPlacement(ServerLevel level) {
        ArrayDeque<SnowTask> tasks = TASKS.get(level);
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        int budget = (Integer)FreezeConfig.SNOW_COLUMNS_PER_TICK.get();
        while (budget > 0 && !tasks.isEmpty()) {
            SnowTask task = tasks.peekFirst();
            int used = task.process(budget);
            budget -= used;
            if (task.complete()) {
                tasks.removeFirst();
            }
            if (used != 0) continue;
            break;
        }
        if (tasks.isEmpty()) {
            TASKS.remove(level);
        }
    }

    private static void processMelting(ServerLevel level) {
        if (!((Boolean)FreezeConfig.TEMPORARY_SNOW.get()).booleanValue()) {
            return;
        }
        ArrayDeque<SnowRecord> records = MELTING.get(level);
        if (records == null || records.isEmpty()) {
            return;
        }
        long now = level.getGameTime();
        int budget = 512;
        while (budget-- > 0 && !records.isEmpty() && records.peekFirst().expiresAt <= now) {
            SnowRecord record = records.removeFirst();
            if (!level.hasChunkAt(record.pos) || !level.getBlockState(record.pos).equals(record.placed)) continue;
            level.setBlock(record.pos, record.previous, 3);
        }
        if (records.isEmpty()) {
            MELTING.remove(level);
        }
    }

    static boolean hasPendingTask(ServerLevel level) {
        ArrayDeque<SnowTask> tasks = TASKS.get(level);
        return tasks != null && !tasks.isEmpty();
    }

    static void clearForTests(ServerLevel level) {
        TASKS.remove(level);
        MELTING.remove(level);
    }

    private static List<Column> createColumns(int radius) {
        ArrayList<Column> result = new ArrayList<Column>((radius * 2 + 1) * (radius * 2 + 1));
        int radiusSquared = radius * radius;
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                int distanceSquared = x * x + z * z;
                if (distanceSquared > radiusSquared) continue;
                result.add(new Column(x, z, distanceSquared));
            }
        }
        result.sort(Comparator.comparingInt(Column::distanceSquared));
        return result;
    }

    private static double clusterNoise(int x, int z, long seed) {
        double scale = 6.0;
        int cellX = Mth.floor((double)((double)x / scale));
        int cellZ = Mth.floor((double)((double)z / scale));
        double localX = (double)x / scale - (double)cellX;
        double localZ = (double)z / scale - (double)cellZ;
        localX = localX * localX * (3.0 - 2.0 * localX);
        localZ = localZ * localZ * (3.0 - 2.0 * localZ);
        double top = Mth.lerp((double)localX, (double)FreezeSnowManager.hash01(cellX, cellZ, seed), (double)FreezeSnowManager.hash01(cellX + 1, cellZ, seed));
        double bottom = Mth.lerp((double)localX, (double)FreezeSnowManager.hash01(cellX, cellZ + 1, seed), (double)FreezeSnowManager.hash01(cellX + 1, cellZ + 1, seed));
        return Mth.lerp((double)localZ, (double)top, (double)bottom);
    }

    private static double hash01(int x, int z, long seed) {
        long value = seed ^ (long)x * 7146057691288625177L ^ (long)z * -7046029254386353131L;
        value = (value ^ value >>> 30) * -4658895280553007687L;
        value = (value ^ value >>> 27) * -7723592293110705685L;
        value ^= value >>> 31;
        return (double)(value >>> 11) * (double)1.110223E-16f;
    }

    private static final class SnowTask {
        private final ServerLevel level;
        private final BlockPos center;
        private final FreezeShellTier tier;
        private final List<Column> columns;
        private final long seed;
        private final boolean surfaceImpact;
        private int cursor;

        private SnowTask(ServerLevel level, BlockPos center, FreezeShellTier tier) {
            this.level = level;
            this.center = center;
            this.tier = tier;
            this.seed = level.random.nextLong();
            this.surfaceImpact = level.canSeeSky(center.above());
            this.columns = FreezeSnowManager.createColumns(tier.snowRadius());
        }

        private int process(int budget) {
            int used;
            for (used = 0; used < budget && !this.complete(); ++used) {
                this.place(this.columns.get(this.cursor++));
            }
            return used;
        }

        private void place(Column column) {
            int previousLayers;
            int x = this.center.getX() + column.x;
            int z = this.center.getZ() + column.z;
            BlockPos chunkCheck = new BlockPos(x, this.center.getY(), z);
            if (!this.level.hasChunkAt(chunkCheck)) {
                return;
            }
            double falloff = 1.0 - Math.sqrt(column.distanceSquared) / (double)this.tier.snowRadius();
            if (falloff <= 0.0) {
                return;
            }
            double density = (Double)FreezeConfig.SNOW_DENSITY.get() * (0.12 + 0.88 * Math.pow(falloff, 1.2));
            if (falloff < 0.82 && FreezeSnowManager.clusterNoise(x, z, this.seed) > density) {
                return;
            }
            BlockPos snowPos = this.findSnowPosition(x, z);
            if (snowPos == null || this.level.getBlockEntity(snowPos.below()) != null || this.level.getBlockEntity(snowPos) != null) {
                return;
            }
            BlockState previous = this.level.getBlockState(snowPos);
            int n = previousLayers = previous.is(Blocks.SNOW) ? (Integer)previous.getValue((Property)SnowLayerBlock.LAYERS) : 0;
            if (!previous.isAir() && !previous.is(Blocks.SNOW)) {
                return;
            }
            double jitter = FreezeSnowManager.hash01(x, z, this.seed ^ 0x9E3779B97F4A7C15L) * 1.5 - 0.25;
            int layers = 1 + (int)Math.floor((double)(this.tier.maximumSnowLayers() - 1) * Math.pow(falloff, 1.45) + jitter);
            layers = Mth.clamp((int)Math.max(layers, previousLayers), (int)1, (int)8);
            BlockState placed = (BlockState)Blocks.SNOW.defaultBlockState().setValue((Property)SnowLayerBlock.LAYERS, (Comparable)Integer.valueOf(layers));
            if (!placed.canSurvive((LevelReader)this.level, snowPos) || placed.equals(previous)) {
                return;
            }
            this.level.setBlock(snowPos, placed, 3);
            if (((Boolean)FreezeConfig.TEMPORARY_SNOW.get()).booleanValue()) {
                long expires = this.level.getGameTime() + (long)((Integer)FreezeConfig.SNOW_MELT_SECONDS.get()).intValue() * 20L;
                MELTING.computeIfAbsent(this.level, ignored -> new ArrayDeque()).addLast(new SnowRecord(snowPos.immutable(), previous, placed, expires));
            }
        }

        private BlockPos findSnowPosition(int x, int z) {
            BlockPos impactFloor = new BlockPos(x, this.center.getY(), z);
            BlockState impactState = this.level.getBlockState(impactFloor);
            if ((impactState.isAir() || impactState.is(Blocks.SNOW)) && !this.level.getBlockState(impactFloor.below()).isAir()) {
                return impactFloor;
            }
            if (this.surfaceImpact) {
                int y = this.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                return new BlockPos(x, y, z);
            }
            int top = Math.min(this.level.getMaxBuildHeight() - 1, this.center.getY() + 8);
            int bottom = Math.max(this.level.getMinBuildHeight() + 1, this.center.getY() - 24);
            for (int y = top; y >= bottom; --y) {
                BlockPos candidate = new BlockPos(x, y, z);
                BlockState state = this.level.getBlockState(candidate);
                if (!state.isAir() && !state.is(Blocks.SNOW) || this.level.getBlockState(candidate.below()).isAir()) continue;
                return candidate;
            }
            return null;
        }

        private boolean complete() {
            return this.cursor >= this.columns.size();
        }
    }

    private record SnowRecord(BlockPos pos, BlockState previous, BlockState placed, long expiresAt) {
    }

    private record Column(int x, int z, int distanceSquared) {
    }
}

