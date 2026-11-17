/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 */
package com.elvoux.sporecannons.field;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record ProjectorTelemetry(BlockPos position, String status, String mode, int rpm, int fieldA, int fieldB, int fieldC, int coolantSeconds) {
    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.position);
        buffer.writeUtf(this.status, 24);
        buffer.writeUtf(this.mode, 24);
        buffer.writeVarInt(this.rpm);
        buffer.writeVarInt(this.fieldA);
        buffer.writeVarInt(this.fieldB);
        buffer.writeVarInt(this.fieldC);
        buffer.writeVarInt(this.coolantSeconds);
    }

    public static ProjectorTelemetry decode(RegistryFriendlyByteBuf buffer) {
        return new ProjectorTelemetry(buffer.readBlockPos(), buffer.readUtf(24), buffer.readUtf(24), buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt());
    }
}

