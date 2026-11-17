/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CDUFieldTerminal;
import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsSounds;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class CDUActivationManager {
    public static final int ACTIVATION_TICKS = 40;
    private static final Map<ServerLevel, ArrayDeque<PendingActivation>> PENDING = new WeakHashMap<ServerLevel, ArrayDeque<PendingActivation>>();

    private CDUActivationManager() {
    }

    public static void schedule(ServerLevel level, Vec3 center, FreezeShellTier tier, @Nullable UUID responsiblePlayer, Cue cue) {
        CDUActivationManager.signal(level, center, tier, cue);
        PENDING.computeIfAbsent(level, ignored -> new ArrayDeque()).addLast(new PendingActivation(center, tier, responsiblePlayer, 40));
    }

    public static void signalProjectile(ServerLevel level, Vec3 center, FreezeShellTier tier, Cue cue) {
        CDUActivationManager.signal(level, center, tier, cue);
    }

    public static void signalMine(ServerLevel level, Vec3 center, FreezeShellTier tier) {
        CDUActivationManager.signal(level, center, tier, Cue.MINE);
    }

    private static void signal(ServerLevel level, Vec3 center, FreezeShellTier tier, Cue cue) {
        BlockPos pos = BlockPos.containing((Position)center);
        level.playSound(null, pos, CDUActivationManager.activationSound(tier), SoundSource.BLOCKS, 1.15f, 1.0f);
        if (cue == Cue.STANDARD) {
            level.playSound(null, pos, (SoundEvent)SporeCannonsSounds.STANDARD_FUZE_TRIGGER.get(), SoundSource.BLOCKS, 0.9f, 1.0f);
        } else if (cue == Cue.SENSOR) {
            level.playSound(null, pos, (SoundEvent)SporeCannonsSounds.SENSOR_FUZE_LOCK.get(), SoundSource.BLOCKS, 0.95f, 1.0f);
        }
        CDUFieldTerminal.activationWarning(level, center, tier);
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        ArrayDeque<PendingActivation> queue = PENDING.get(level2);
        if (queue == null || queue.isEmpty()) {
            return;
        }
        int count = queue.size();
        for (int i = 0; i < count; ++i) {
            PendingActivation activation = queue.removeFirst();
            --activation.ticks;
            if (activation.ticks <= 0) {
                FreezeShellProjectile.completeDetonation(level2, activation.center, activation.tier, activation.responsiblePlayer);
                continue;
            }
            queue.addLast(activation);
        }
        if (queue.isEmpty()) {
            PENDING.remove(level2);
        }
    }

    private static SoundEvent activationSound(FreezeShellTier tier) {
        return switch (tier) {
            default -> throw new MatchException(null, null);
            case FreezeShellTier.I, FreezeShellTier.II -> (SoundEvent)SporeCannonsSounds.CDU_ACTIVATION_LIGHT.get();
            case FreezeShellTier.III, FreezeShellTier.IV -> (SoundEvent)SporeCannonsSounds.CDU_ACTIVATION_REINFORCED.get();
            case FreezeShellTier.V -> (SoundEvent)SporeCannonsSounds.CDU_ACTIVATION_STRATEGIC.get();
            case FreezeShellTier.VI -> (SoundEvent)SporeCannonsSounds.CDU_ACTIVATION_ABSOLUTE_ZERO.get();
        };
    }

    public static enum Cue {
        STANDARD,
        SENSOR,
        MINE;

    }

    private static final class PendingActivation {
        private final Vec3 center;
        private final FreezeShellTier tier;
        @Nullable
        private final UUID responsiblePlayer;
        private int ticks;

        private PendingActivation(Vec3 center, FreezeShellTier tier, @Nullable UUID responsiblePlayer, int ticks) {
            this.center = center;
            this.tier = tier;
            this.responsiblePlayer = responsiblePlayer;
            this.ticks = ticks;
        }
    }
}

