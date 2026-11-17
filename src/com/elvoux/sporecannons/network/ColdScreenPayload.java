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
import com.elvoux.sporecannons.client.ColdScreenOverlay;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ColdScreenPayload(int delayTicks, int durationTicks, float intensity) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ColdScreenPayload> TYPE = new CustomPacketPayload.Type(SporeCannons.id("cold_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ColdScreenPayload> STREAM_CODEC = StreamCodec.ofMember(ColdScreenPayload::encode, ColdScreenPayload::decode);

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(this.delayTicks);
        buffer.writeVarInt(this.durationTicks);
        buffer.writeFloat(this.intensity);
    }

    private static ColdScreenPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ColdScreenPayload(buffer.readVarInt(), buffer.readVarInt(), buffer.readFloat());
    }

    public static void handle(ColdScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ColdScreenOverlay.trigger(payload));
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

