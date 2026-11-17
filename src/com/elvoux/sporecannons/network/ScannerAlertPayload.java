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
import com.elvoux.sporecannons.client.WhiteoutClientAlerts;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScannerAlertPayload(boolean unread) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ScannerAlertPayload> TYPE = new CustomPacketPayload.Type(SporeCannons.id("scanner_alert"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScannerAlertPayload> STREAM_CODEC = StreamCodec.ofMember(ScannerAlertPayload::encode, ScannerAlertPayload::decode);

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.unread);
    }

    private static ScannerAlertPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ScannerAlertPayload(buffer.readBoolean());
    }

    public static void handle(ScannerAlertPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> WhiteoutClientAlerts.setUnread(payload.unread));
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

