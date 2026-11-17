/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.elvoux.sporecannons.network;

import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.field.WhiteoutEventLog;
import com.elvoux.sporecannons.field.WhiteoutTerminalService;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScannerActionPayload(Action action) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ScannerActionPayload> TYPE = new CustomPacketPayload.Type(SporeCannons.id("scanner_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScannerActionPayload> STREAM_CODEC = StreamCodec.ofMember(ScannerActionPayload::encode, ScannerActionPayload::decode);

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(this.action.ordinal());
    }

    private static ScannerActionPayload decode(RegistryFriendlyByteBuf buffer) {
        Action[] values = Action.values();
        int ordinal = Math.max(0, Math.min(values.length - 1, buffer.readVarInt()));
        return new ScannerActionPayload(values[ordinal]);
    }

    public static void handle(ScannerActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player patt0$temp = context.player();
            if (!(patt0$temp instanceof ServerPlayer)) {
                return;
            }
            ServerPlayer player = (ServerPlayer)patt0$temp;
            switch (payload.action.ordinal()) {
                case 0: {
                    WhiteoutTerminalService.sendSnapshot(player);
                    break;
                }
                case 1: {
                    WhiteoutTerminalService.quickScan(player);
                    break;
                }
                case 2: {
                    WhiteoutTerminalService.cycleRange(player);
                    break;
                }
                case 3: {
                    WhiteoutEventLog.markRead(player);
                    WhiteoutTerminalService.sendSnapshot(player);
                    break;
                }
                case 4: {
                    WhiteoutTerminalService.deepScan(player);
                    break;
                }
                case 5: {
                    WhiteoutEventLog.toggleSound(player);
                    WhiteoutTerminalService.sendSnapshot(player);
                    break;
                }
                case 6: {
                    WhiteoutEventLog.toggleProjectorEvents(player);
                    WhiteoutTerminalService.sendSnapshot(player);
                }
            }
        });
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static enum Action {
        REFRESH,
        QUICK_SCAN,
        CYCLE_RANGE,
        MARK_READ,
        DEEP_SCAN,
        TOGGLE_SOUND,
        TOGGLE_PROJECTOR_EVENTS;

    }
}

