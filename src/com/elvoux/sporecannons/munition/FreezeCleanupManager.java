/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.munition.CDUFieldTerminal;
import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class FreezeCleanupManager {
    private static final Map<ServerLevel, ArrayDeque<CleanupTask>> TASKS = new WeakHashMap<ServerLevel, ArrayDeque<CleanupTask>>();

    private FreezeCleanupManager() {
    }

    public static void schedule(ServerLevel level, Vec3 impact, FreezeShellTier tier, @Nullable ServerPlayer responsiblePlayer) {
        CleanupTask task = new CleanupTask(level, BlockPos.containing((Position)impact), tier, responsiblePlayer == null ? null : responsiblePlayer.getUUID());
        if (tier == FreezeShellTier.I) {
            task.process(Integer.MAX_VALUE);
            task.finishReport();
            return;
        }
        TASKS.computeIfAbsent(level, ignored -> new ArrayDeque()).addLast(task);
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        ArrayDeque<CleanupTask> tasks = TASKS.get(level2);
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        int budget = (Integer)FreezeConfig.BLOCK_CHECKS_PER_TICK.get();
        while (budget > 0 && !tasks.isEmpty()) {
            CleanupTask task = tasks.peekFirst();
            int used = task.process(budget);
            budget -= used;
            if (task.complete()) {
                tasks.removeFirst();
                task.finishReport();
            }
            if (used != 0) continue;
            break;
        }
        if (tasks.isEmpty()) {
            TASKS.remove(level2);
        }
    }

    static boolean hasPendingTask(ServerLevel level) {
        ArrayDeque<CleanupTask> tasks = TASKS.get(level);
        return tasks != null && !tasks.isEmpty();
    }

    static void clearForTests(ServerLevel level) {
        TASKS.remove(level);
    }

    private static final class CleanupTask {
        private final ServerLevel level;
        private final BlockPos center;
        private final FreezeShellTier tier;
        private final int radius;
        private final long radiusSquared;
        @Nullable
        private final UUID responsiblePlayerId;
        private boolean advancementTriggered;
        private boolean reportSent;
        private int cleanedBlocks;
        private int x;
        private int z;
        private int y = Integer.MIN_VALUE;
        private int columnMaxY;

        private CleanupTask(ServerLevel level, BlockPos center, FreezeShellTier tier, @Nullable UUID responsiblePlayerId) {
            this.level = level;
            this.center = center;
            this.tier = tier;
            this.radius = tier.blockRadius();
            this.radiusSquared = (long)this.radius * (long)this.radius;
            this.responsiblePlayerId = responsiblePlayerId;
            this.x = -this.radius;
            this.z = -this.radius;
        }

        private int process(int budget) {
            int used = 0;
            while (used < budget && !this.complete()) {
                boolean decontamination;
                if (this.y == Integer.MIN_VALUE) {
                    ++used;
                    long horizontalDistance = (long)this.x * (long)this.x + (long)this.z * (long)this.z;
                    BlockPos columnPos = new BlockPos(this.center.getX() + this.x, this.center.getY(), this.center.getZ() + this.z);
                    if (horizontalDistance > this.radiusSquared || !this.level.hasChunkAt(columnPos)) {
                        this.advanceColumn();
                        continue;
                    }
                    int verticalReach = (int)Math.floor(Math.sqrt(this.radiusSquared - horizontalDistance));
                    this.y = Math.max(this.level.getMinBuildHeight(), this.center.getY() - verticalReach);
                    this.columnMaxY = Math.min(this.level.getMaxBuildHeight() - 1, this.center.getY() + verticalReach);
                }
                BlockPos pos = new BlockPos(this.center.getX() + this.x, this.y, this.center.getZ() + this.z);
                BlockState current = this.level.getBlockState(pos);
                double distanceSquared = pos.distSqr((Vec3i)this.center);
                int shatterRadius = this.tier.freshBileShatterRadius();
                BlockState cleaned = FreezeShellProjectile.cleanedState(current, shatterRadius > 0 && distanceSquared <= (double)((long)shatterRadius * (long)shatterRadius));
                boolean bl = decontamination = cleaned != null;
                if (cleaned == null && current.is(Blocks.WATER)) {
                    cleaned = this.frozenWaterState(distanceSquared);
                }
                if (cleaned == null && distanceSquared <= (double)((long)this.tier.snowRadius() * (long)this.tier.snowRadius()) && current.getFluidState().is(FluidTags.LAVA)) {
                    BlockState blockState = cleaned = current.getFluidState().isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.STONE.defaultBlockState();
                }
                if (cleaned != null && !cleaned.equals(current) && this.level.setBlock(pos, cleaned, 3) && decontamination) {
                    ++this.cleanedBlocks;
                    this.triggerTerrainAdvancement();
                }
                ++used;
                if (this.y >= this.columnMaxY) {
                    this.advanceColumn();
                    continue;
                }
                ++this.y;
            }
            return used;
        }

        private void advanceColumn() {
            this.y = Integer.MIN_VALUE;
            ++this.z;
            if (this.z > this.radius) {
                this.z = -this.radius;
                ++this.x;
            }
        }

        private BlockState frozenWaterState(double distanceSquared) {
            double normalized = Math.sqrt(distanceSquared) / Math.max(1.0, (double)this.radius);
            if (normalized <= 0.18) {
                return Blocks.BLUE_ICE.defaultBlockState();
            }
            if (normalized <= 0.55) {
                return Blocks.PACKED_ICE.defaultBlockState();
            }
            return Blocks.ICE.defaultBlockState();
        }

        private boolean complete() {
            return this.x > this.radius;
        }

        private void triggerTerrainAdvancement() {
            if (this.advancementTriggered || this.responsiblePlayerId == null) {
                return;
            }
            ServerPlayer player = this.level.getServer().getPlayerList().getPlayer(this.responsiblePlayerId);
            if (player == null) {
                return;
            }
            ((PlayerTrigger)SporeCannonsCriteria.TERRAIN_CLEANED.get()).trigger(player);
            this.advancementTriggered = true;
        }

        private void finishReport() {
            if (this.reportSent || !((Boolean)FreezeConfig.FIELD_REPORTS.get()).booleanValue()) {
                return;
            }
            this.reportSent = true;
            CDUFieldTerminal.cleanupReport(this.level, Vec3.atCenterOf((Vec3i)this.center), this.tier, this.cleanedBlocks);
        }
    }
}

