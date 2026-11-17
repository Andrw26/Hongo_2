/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.phys.Vec3
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.field.WhiteoutEventLog;
import com.elvoux.sporecannons.field.WhiteoutEventType;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public final class CDUFieldTerminal {
    private static final Map<ServerLevel, LinkedHashMap<Long, Long>> RECENT_ACTIVATIONS = new WeakHashMap<ServerLevel, LinkedHashMap<Long, Long>>();

    private CDUFieldTerminal() {
    }

    public static void activationWarning(ServerLevel level, Vec3 center, FreezeShellTier tier) {
        if (!((Boolean)FreezeConfig.FIELD_REPORTS.get()).booleanValue()) {
            return;
        }
        BlockPos pos = BlockPos.containing((Position)center);
        boolean duplicate = CDUFieldTerminal.duplicateActivation(level, pos, tier);
        if (!duplicate) {
            CDUFieldTerminal.broadcast(level, center, tier, WhiteoutEventType.CDU_ACTIVATION, pos, tier.ordinal() + 1, 2, 0, 0);
        }
    }

    public static void strikeReport(ServerLevel level, Vec3 center, FreezeShellTier tier, int parasitesAffected) {
        BlockPos pos = BlockPos.containing((Position)center);
        CDUFieldTerminal.broadcast(level, center, tier, WhiteoutEventType.CDU_STRIKE, pos, tier.ordinal() + 1, parasitesAffected, tier.containmentRadius(), tier.containmentTicks() / 20);
    }

    public static void cleanupReport(ServerLevel level, Vec3 center, FreezeShellTier tier, int cleanedBlocks) {
        BlockPos pos = BlockPos.containing((Position)center);
        CDUFieldTerminal.broadcast(level, center, tier, WhiteoutEventType.CDU_CLEANUP, pos, tier.ordinal() + 1, cleanedBlocks, 0, 0);
    }

    public static void zoneReport(ServerLevel level, Vec3 center, FreezeShellTier tier, boolean displaced, int cleanedBlocks, int parasiteContacts) {
        BlockPos pos = BlockPos.containing((Position)center);
        CDUFieldTerminal.broadcast(level, center, tier, displaced ? WhiteoutEventType.ZONE_DISPLACED : WhiteoutEventType.ZONE_COMPLETE, pos, tier.ordinal() + 1, cleanedBlocks, parasiteContacts, 0);
    }

    public static void mineTriggered(ServerLevel level, Vec3 center, FreezeShellTier tier) {
        BlockPos pos = BlockPos.containing((Position)center);
        CDUFieldTerminal.broadcast(level, center, tier, WhiteoutEventType.WHITE_WATCH_TRIGGERED, pos, tier.ordinal() + 1, 0, 0, 0);
    }

    public static void projectorStatus(ServerLevel level, BlockPos position, String status, int rpm, int fieldSize, int coolantSeconds) {
        CDUFieldTerminal.broadcastProjector(level, position, WhiteoutEventType.PROJECTOR_STATUS, CDUFieldTerminal.statusCode(status), rpm, fieldSize, coolantSeconds);
    }

    public static void projectorCoolantWarning(ServerLevel level, BlockPos position, int percent, int coolantSeconds, int rpm) {
        CDUFieldTerminal.broadcastProjector(level, position, WhiteoutEventType.PROJECTOR_COOLANT_LOW, percent, coolantSeconds, rpm, 0);
    }

    public static boolean hasScanner(ServerPlayer player) {
        return (Boolean)FreezeConfig.FIELD_SCANNER.get() != false && player.getInventory().contains(stack -> stack.is((Item)SporeCannonsItems.CDU_FIELD_SCANNER.get()));
    }

    private static void broadcast(ServerLevel level, Vec3 center, FreezeShellTier tier, WhiteoutEventType type, BlockPos position, int valueA, int valueB, int valueC, int valueD) {
        if (!((Boolean)FreezeConfig.FIELD_REPORTS.get()).booleanValue()) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (!CDUFieldTerminal.canReceive(player, center, tier)) continue;
            WhiteoutEventLog.record(player, type, level, position, valueA, valueB, valueC, valueD);
        }
    }

    private static boolean canReceive(ServerPlayer player, Vec3 center, FreezeShellTier tier) {
        double range = CDUFieldTerminal.telemetryRange(tier);
        return !player.isSpectator() && CDUFieldTerminal.hasScanner(player) && player.distanceToSqr(center) <= range * range;
    }

    private static void broadcastProjector(ServerLevel level, BlockPos position, WhiteoutEventType type, int valueA, int valueB, int valueC, int valueD) {
        if (!((Boolean)FreezeConfig.FIELD_REPORTS.get()).booleanValue()) {
            return;
        }
        Vec3 center = Vec3.atCenterOf((Vec3i)position);
        double rangeSquared = 16384.0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || !CDUFieldTerminal.hasScanner(player) || !(player.distanceToSqr(center) <= rangeSquared)) continue;
            WhiteoutEventLog.record(player, type, level, position, valueA, valueB, valueC, valueD);
        }
    }

    private static int statusCode(String status) {
        return switch (status) {
            case "active" -> 0;
            case "stopped" -> 1;
            case "jammed" -> 2;
            case "depleted" -> 4;
            default -> 3;
        };
    }

    private static int telemetryRange(FreezeShellTier tier) {
        return switch (tier) {
            default -> throw new MatchException(null, null);
            case FreezeShellTier.I -> 128;
            case FreezeShellTier.II -> 192;
            case FreezeShellTier.III -> 256;
            case FreezeShellTier.IV -> 384;
            case FreezeShellTier.V -> 512;
            case FreezeShellTier.VI -> 1024;
        };
    }

    private static boolean duplicateActivation(ServerLevel level, BlockPos pos, FreezeShellTier tier) {
        long now = level.getGameTime();
        long key = pos.asLong() ^ (long)(tier.ordinal() + 1) << 56;
        LinkedHashMap recent = RECENT_ACTIVATIONS.computeIfAbsent(level, ignored -> new LinkedHashMap());
        Long previous = recent.put(key, now);
        if (recent.size() > 256) {
            recent.entrySet().removeIf(entry -> now - (Long)entry.getValue() > 200L);
        }
        return previous != null && now - previous <= 60L;
    }
}

