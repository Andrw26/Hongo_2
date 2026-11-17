/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package com.elvoux.sporecannons.field;

import com.elvoux.sporecannons.field.WhiteoutEventType;
import com.elvoux.sporecannons.field.WhiteoutLogEntry;
import com.elvoux.sporecannons.network.ScannerAlertPayload;
import com.elvoux.sporecannons.registry.SporeCannonsSounds;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class WhiteoutEventLog
extends SavedData {
    public static final int MAX_ENTRIES = 48;
    private static final String DATA_NAME = "sporecannons_whiteout_event_log";
    private static final SavedData.Factory<WhiteoutEventLog> FACTORY = new SavedData.Factory(WhiteoutEventLog::new, WhiteoutEventLog::load);
    private final Map<UUID, ArrayDeque<WhiteoutLogEntry>> entries = new LinkedHashMap<UUID, ArrayDeque<WhiteoutLogEntry>>();
    private final Set<UUID> unread = new LinkedHashSet<UUID>();
    private final Set<UUID> soundMuted = new LinkedHashSet<UUID>();
    private final Set<UUID> projectorEventsIgnored = new LinkedHashSet<UUID>();

    private WhiteoutEventLog() {
    }

    public static void record(ServerPlayer player, WhiteoutEventType type, ServerLevel level, BlockPos position, int valueA, int valueB, int valueC, int valueD) {
        WhiteoutEventLog data = WhiteoutEventLog.data(player.server);
        if (WhiteoutEventLog.isProjectorEvent(type) && data.projectorEventsIgnored.contains(player.getUUID())) {
            return;
        }
        ArrayDeque playerEntries = data.entries.computeIfAbsent(player.getUUID(), ignored -> new ArrayDeque());
        WhiteoutLogEntry next = new WhiteoutLogEntry(type, level.getGameTime(), level.dimension().location().toString(), position.immutable(), valueA, valueB, valueC, valueD);
        WhiteoutLogEntry newest = (WhiteoutLogEntry)((Object)playerEntries.peekFirst());
        if (newest != null && WhiteoutEventLog.duplicate(newest, next)) {
            return;
        }
        playerEntries.addFirst(next);
        while (playerEntries.size() > 48) {
            playerEntries.removeLast();
        }
        if (type != WhiteoutEventType.SCAN_COMPLETE && type != WhiteoutEventType.DEEP_SCAN_COMPLETE && data.unread.add(player.getUUID())) {
            PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new ScannerAlertPayload(true), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
        if (WhiteoutEventLog.isExplosiveWarning(type) && !data.soundMuted.contains(player.getUUID())) {
            player.playNotifySound((SoundEvent)SporeCannonsSounds.SCANNER_REMOTE_WARNING.get(), SoundSource.PLAYERS, 0.72f, 1.0f);
        }
        data.setDirty();
    }

    public static boolean hasUnread(ServerPlayer player) {
        return WhiteoutEventLog.data((MinecraftServer)player.server).unread.contains(player.getUUID());
    }

    public static void markRead(ServerPlayer player) {
        WhiteoutEventLog data = WhiteoutEventLog.data(player.server);
        if (data.unread.remove(player.getUUID())) {
            data.setDirty();
        }
        PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new ScannerAlertPayload(false), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static boolean soundEnabled(ServerPlayer player) {
        return !WhiteoutEventLog.data((MinecraftServer)player.server).soundMuted.contains(player.getUUID());
    }

    public static boolean projectorEventsEnabled(ServerPlayer player) {
        return !WhiteoutEventLog.data((MinecraftServer)player.server).projectorEventsIgnored.contains(player.getUUID());
    }

    public static void toggleSound(ServerPlayer player) {
        WhiteoutEventLog data = WhiteoutEventLog.data(player.server);
        WhiteoutEventLog.toggle(data.soundMuted, player.getUUID());
        data.setDirty();
    }

    public static void toggleProjectorEvents(ServerPlayer player) {
        WhiteoutEventLog data = WhiteoutEventLog.data(player.server);
        WhiteoutEventLog.toggle(data.projectorEventsIgnored, player.getUUID());
        data.setDirty();
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            PacketDistributor.sendToPlayer((ServerPlayer)player2, (CustomPacketPayload)new ScannerAlertPayload(WhiteoutEventLog.hasUnread(player2)), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
    }

    public static List<WhiteoutLogEntry> entries(ServerPlayer player) {
        ArrayDeque<WhiteoutLogEntry> stored = WhiteoutEventLog.data((MinecraftServer)player.server).entries.get(player.getUUID());
        if (stored == null) {
            return List.of();
        }
        ArrayList<WhiteoutLogEntry> filtered = new ArrayList<WhiteoutLogEntry>(stored.size());
        for (WhiteoutLogEntry entry : stored) {
            if (entry.type() == WhiteoutEventType.PROJECTOR_STATUS && entry.valueA() != 4 || !filtered.isEmpty() && WhiteoutEventLog.duplicate((WhiteoutLogEntry)((Object)filtered.get(filtered.size() - 1)), entry)) continue;
            filtered.add(entry);
        }
        return List.copyOf(filtered);
    }

    private static boolean duplicate(WhiteoutLogEntry first, WhiteoutLogEntry second) {
        return first.type() == second.type() && first.position().equals((Object)second.position()) && first.valueA() == second.valueA() && first.valueB() == second.valueB() && first.valueC() == second.valueC() && first.valueD() == second.valueD() && Math.abs(first.gameTime() - second.gameTime()) <= 60L;
    }

    private static boolean isProjectorEvent(WhiteoutEventType type) {
        return type == WhiteoutEventType.PROJECTOR_STATUS || type == WhiteoutEventType.PROJECTOR_COOLANT_LOW;
    }

    private static boolean isExplosiveWarning(WhiteoutEventType type) {
        return type == WhiteoutEventType.CDU_ACTIVATION || type == WhiteoutEventType.WHITE_WATCH_TRIGGERED;
    }

    private static void toggle(Set<UUID> set, UUID player) {
        if (!set.remove(player)) {
            set.add(player);
        }
    }

    private static WhiteoutEventLog data(MinecraftServer server) {
        return (WhiteoutEventLog)server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static WhiteoutEventLog load(CompoundTag root, HolderLookup.Provider registries) {
        WhiteoutEventLog data = new WhiteoutEventLog();
        ListTag players = root.getList("Players", 10);
        for (int i = 0; i < players.size(); ++i) {
            CompoundTag playerTag = players.getCompound(i);
            if (!playerTag.hasUUID("Player")) continue;
            ArrayDeque<WhiteoutLogEntry> queue = new ArrayDeque<WhiteoutLogEntry>();
            ListTag eventTags = playerTag.getList("Events", 10);
            for (int j = 0; j < eventTags.size() && queue.size() < 48; ++j) {
                WhiteoutLogEntry entry = WhiteoutLogEntry.load(eventTags.getCompound(j));
                if (entry == null) continue;
                queue.addLast(entry);
            }
            if (!queue.isEmpty()) {
                data.entries.put(playerTag.getUUID("Player"), queue);
            }
            if (playerTag.getBoolean("Unread")) {
                data.unread.add(playerTag.getUUID("Player"));
            }
            if (playerTag.getBoolean("SoundMuted")) {
                data.soundMuted.add(playerTag.getUUID("Player"));
            }
            if (!playerTag.getBoolean("ProjectorEventsIgnored")) continue;
            data.projectorEventsIgnored.add(playerTag.getUUID("Player"));
        }
        return data;
    }

    public CompoundTag save(CompoundTag root, HolderLookup.Provider registries) {
        ListTag players = new ListTag();
        LinkedHashSet<UUID> playersToSave = new LinkedHashSet<UUID>(this.entries.keySet());
        playersToSave.addAll(this.unread);
        playersToSave.addAll(this.soundMuted);
        playersToSave.addAll(this.projectorEventsIgnored);
        for (UUID player : playersToSave) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("Player", player);
            ListTag eventTags = new ListTag();
            for (WhiteoutLogEntry entry : this.entries.getOrDefault(player, new ArrayDeque())) {
                eventTags.add((Object)entry.save());
            }
            playerTag.put("Events", (Tag)eventTags);
            playerTag.putBoolean("Unread", this.unread.contains(player));
            playerTag.putBoolean("SoundMuted", this.soundMuted.contains(player));
            playerTag.putBoolean("ProjectorEventsIgnored", this.projectorEventsIgnored.contains(player));
            players.add((Object)playerTag);
        }
        root.put("Players", (Tag)players);
        return root;
    }
}

