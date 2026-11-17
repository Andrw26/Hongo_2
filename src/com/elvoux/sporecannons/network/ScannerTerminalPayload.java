/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.elvoux.sporecannons.network;

import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.client.CDUFieldScannerScreen;
import com.elvoux.sporecannons.field.ProjectorTelemetry;
import com.elvoux.sporecannons.field.WhiteoutLogEntry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScannerTerminalPayload(int range, long serverGameTime, boolean unread, boolean soundEnabled, boolean projectorEventsEnabled, boolean deepScanning, int deepScanTicks, List<ProjectorTelemetry> projectors, List<WhiteoutLogEntry> log) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ScannerTerminalPayload> TYPE = new CustomPacketPayload.Type(SporeCannons.id("scanner_terminal"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScannerTerminalPayload> STREAM_CODEC = StreamCodec.ofMember(ScannerTerminalPayload::encode, ScannerTerminalPayload::decode);

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(this.range);
        buffer.writeVarLong(this.serverGameTime);
        buffer.writeBoolean(this.unread);
        buffer.writeBoolean(this.soundEnabled);
        buffer.writeBoolean(this.projectorEventsEnabled);
        buffer.writeBoolean(this.deepScanning);
        buffer.writeVarInt(this.deepScanTicks);
        buffer.writeVarInt(this.projectors.size());
        for (ProjectorTelemetry projector : this.projectors) {
            projector.encode(buffer);
        }
        buffer.writeVarInt(this.log.size());
        for (WhiteoutLogEntry entry : this.log) {
            entry.encode(buffer);
        }
    }

    private static ScannerTerminalPayload decode(RegistryFriendlyByteBuf buffer) {
        int range = buffer.readVarInt();
        long serverGameTime = buffer.readVarLong();
        boolean unread = buffer.readBoolean();
        boolean soundEnabled = buffer.readBoolean();
        boolean projectorEventsEnabled = buffer.readBoolean();
        boolean deepScanning = buffer.readBoolean();
        int deepScanTicks = buffer.readVarInt();
        int projectorCount = Math.min(32, Math.max(0, buffer.readVarInt()));
        ArrayList<ProjectorTelemetry> projectors = new ArrayList<ProjectorTelemetry>(projectorCount);
        for (int i = 0; i < projectorCount; ++i) {
            projectors.add(ProjectorTelemetry.decode(buffer));
        }
        int eventCount = Math.min(48, Math.max(0, buffer.readVarInt()));
        ArrayList<WhiteoutLogEntry> log = new ArrayList<WhiteoutLogEntry>(eventCount);
        for (int i = 0; i < eventCount; ++i) {
            log.add(WhiteoutLogEntry.decode(buffer));
        }
        return new ScannerTerminalPayload(range, serverGameTime, unread, soundEnabled, projectorEventsEnabled, deepScanning, deepScanTicks, List.copyOf(projectors), List.copyOf(log));
    }

    public static void handle(ScannerTerminalPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> CDUFieldScannerScreen.openOrUpdate(payload));
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

