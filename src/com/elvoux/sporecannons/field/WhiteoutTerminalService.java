/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package com.elvoux.sporecannons.field;

import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.field.ProjectorTelemetry;
import com.elvoux.sporecannons.field.WhiteoutEventLog;
import com.elvoux.sporecannons.munition.CDUFieldScannerItem;
import com.elvoux.sporecannons.munition.ContainmentFieldMode;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.elvoux.sporecannons.network.ScannerTerminalPayload;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class WhiteoutTerminalService {
    private static final int MAX_PROJECTORS = 24;
    private static final int DEEP_SCAN_DURATION_TICKS = 100;
    private static final int DEEP_SCAN_COOLDOWN_TICKS = 600;
    private static final Map<UUID, PendingDeepScan> DEEP_SCANS = new HashMap<UUID, PendingDeepScan>();
    private static final Map<UUID, Long> DEEP_SCAN_READY_AT = new HashMap<UUID, Long>();

    private WhiteoutTerminalService() {
    }

    public static void open(ServerPlayer player) {
        WhiteoutTerminalService.sendSnapshot(player);
    }

    public static void sendSnapshot(ServerPlayer player) {
        ItemStack scanner = WhiteoutTerminalService.scanner(player);
        if (scanner.isEmpty() || !((Boolean)FreezeConfig.FIELD_SCANNER.get()).booleanValue()) {
            return;
        }
        int range = CDUFieldScannerItem.range(scanner);
        DeepScanState deep = WhiteoutTerminalService.deepScanState(player);
        PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new ScannerTerminalPayload(range, player.serverLevel().getGameTime(), WhiteoutEventLog.hasUnread(player), WhiteoutEventLog.soundEnabled(player), WhiteoutEventLog.projectorEventsEnabled(player), deep.scanning(), deep.ticks(), WhiteoutTerminalService.nearbyProjectors(player.serverLevel(), player.blockPosition(), range), WhiteoutEventLog.entries(player)), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static void quickScan(ServerPlayer player) {
        ItemStack scanner = WhiteoutTerminalService.scanner(player);
        if (scanner.isEmpty() || !((Boolean)FreezeConfig.FIELD_SCANNER.get()).booleanValue()) {
            return;
        }
        if (!player.getCooldowns().isOnCooldown(scanner.getItem())) {
            CDUFieldScannerItem.performScan(player.serverLevel(), player, CDUFieldScannerItem.range(scanner));
            player.getCooldowns().addCooldown(scanner.getItem(), (Integer)FreezeConfig.SCANNER_COOLDOWN_SECONDS.get() * 20);
        }
        WhiteoutTerminalService.sendSnapshot(player);
    }

    public static void cycleRange(ServerPlayer player) {
        ItemStack scanner = WhiteoutTerminalService.scanner(player);
        if (scanner.isEmpty() || !((Boolean)FreezeConfig.FIELD_SCANNER.get()).booleanValue()) {
            return;
        }
        CDUFieldScannerItem.cycleRange(scanner);
        WhiteoutTerminalService.sendSnapshot(player);
    }

    public static void deepScan(ServerPlayer player) {
        ItemStack scanner = WhiteoutTerminalService.scanner(player);
        if (scanner.isEmpty() || !((Boolean)FreezeConfig.FIELD_SCANNER.get()).booleanValue()) {
            return;
        }
        long now = player.serverLevel().getGameTime();
        UUID id = player.getUUID();
        if (DEEP_SCANS.containsKey(id) || now < DEEP_SCAN_READY_AT.getOrDefault(id, 0L)) {
            WhiteoutTerminalService.sendSnapshot(player);
            return;
        }
        DEEP_SCANS.put(id, new PendingDeepScan(player.serverLevel(), now + 100L, CDUFieldScannerItem.range(scanner)));
        DEEP_SCAN_READY_AT.put(id, now + 100L + 600L);
        WhiteoutTerminalService.sendSnapshot(player);
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        ServerLevel level;
        block4: {
            block3: {
                Level level2 = event.getLevel();
                if (!(level2 instanceof ServerLevel)) break block3;
                level = (ServerLevel)level2;
                if (!DEEP_SCANS.isEmpty()) break block4;
            }
            return;
        }
        long now = level.getGameTime();
        Iterator<Map.Entry<UUID, PendingDeepScan>> iterator = DEEP_SCANS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PendingDeepScan> entry = iterator.next();
            PendingDeepScan pending = entry.getValue();
            if (pending.level() != level || now < pending.finishesAt()) continue;
            iterator.remove();
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(entry.getKey());
            if (player == null || player.serverLevel() != level || WhiteoutTerminalService.scanner(player).isEmpty()) continue;
            CDUFieldScannerItem.performDeepScan(level, player, pending.range());
            WhiteoutTerminalService.sendSnapshot(player);
        }
    }

    private static DeepScanState deepScanState(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        PendingDeepScan pending = DEEP_SCANS.get(player.getUUID());
        if (pending != null && pending.level() == player.serverLevel()) {
            return new DeepScanState(true, (int)Math.max(0L, pending.finishesAt() - now));
        }
        long ready = DEEP_SCAN_READY_AT.getOrDefault(player.getUUID(), 0L);
        return new DeepScanState(false, (int)Math.max(0L, ready - now));
    }

    private static ItemStack scanner(ServerPlayer player) {
        ItemStack main = player.getMainHandItem();
        if (main.is((Item)SporeCannonsItems.CDU_FIELD_SCANNER.get())) {
            return main;
        }
        ItemStack off = player.getOffhandItem();
        return off.is((Item)SporeCannonsItems.CDU_FIELD_SCANNER.get()) ? off : ItemStack.EMPTY;
    }

    private static List<ProjectorTelemetry> nearbyProjectors(ServerLevel level, BlockPos origin, int range) {
        ArrayList<ProjectorTelemetry> result = new ArrayList<ProjectorTelemetry>();
        int minChunkX = SectionPos.blockToSectionCoord((int)(origin.getX() - range));
        int maxChunkX = SectionPos.blockToSectionCoord((int)(origin.getX() + range));
        int minChunkZ = SectionPos.blockToSectionCoord((int)(origin.getZ() - range));
        int maxChunkZ = SectionPos.blockToSectionCoord((int)(origin.getZ() + range));
        long rangeSquared = (long)range * (long)range;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) continue;
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    CryogenicContainmentProjectorBlockEntity projector;
                    if (!(blockEntity instanceof CryogenicContainmentProjectorBlockEntity) || (projector = (CryogenicContainmentProjectorBlockEntity)blockEntity).getBlockPos().distSqr((Vec3i)origin) > (double)rangeSquared) continue;
                    boolean wall = projector.fieldMode() == ContainmentFieldMode.WALL;
                    result.add(new ProjectorTelemetry(projector.getBlockPos().immutable(), projector.statusKey(), projector.fieldMode().serializedName(), Math.round(Math.abs(projector.kineticSpeed())), wall ? Mth.floor((double)projector.wallWidth()) : Mth.floor((double)projector.currentFieldRadius()), wall ? projector.wallDepth() : 0, wall ? projector.wallHeight() : 0, projector.estimatedRuntimeSeconds()));
                }
            }
        }
        result.sort(Comparator.comparingDouble(entry -> entry.position().distSqr((Vec3i)origin)));
        return result.size() <= 24 ? List.copyOf(result) : List.copyOf(result.subList(0, 24));
    }

    private record DeepScanState(boolean scanning, int ticks) {
    }

    private record PendingDeepScan(ServerLevel level, long finishesAt, int range) {
    }
}

