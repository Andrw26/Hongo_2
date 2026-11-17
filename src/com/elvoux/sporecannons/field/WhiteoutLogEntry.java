/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 */
package com.elvoux.sporecannons.field;

import com.elvoux.sporecannons.field.WhiteoutEventType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record WhiteoutLogEntry(WhiteoutEventType type, long gameTime, String dimension, BlockPos position, int valueA, int valueB, int valueC, int valueD) {
    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(this.type.ordinal());
        buffer.writeVarLong(this.gameTime);
        buffer.writeUtf(this.dimension, 128);
        buffer.writeBlockPos(this.position);
        buffer.writeVarInt(this.valueA);
        buffer.writeVarInt(this.valueB);
        buffer.writeVarInt(this.valueC);
        buffer.writeVarInt(this.valueD);
    }

    public static WhiteoutLogEntry decode(RegistryFriendlyByteBuf buffer) {
        WhiteoutEventType[] values = WhiteoutEventType.values();
        int ordinal = Math.max(0, Math.min(values.length - 1, buffer.readVarInt()));
        return new WhiteoutLogEntry(values[ordinal], buffer.readVarLong(), buffer.readUtf(128), buffer.readBlockPos(), buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt());
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", this.type.name());
        tag.putLong("Time", this.gameTime);
        tag.putString("Dimension", this.dimension);
        tag.putLong("Position", this.position.asLong());
        tag.putInt("A", this.valueA);
        tag.putInt("B", this.valueB);
        tag.putInt("C", this.valueC);
        tag.putInt("D", this.valueD);
        return tag;
    }

    public static WhiteoutLogEntry load(CompoundTag tag) {
        try {
            return new WhiteoutLogEntry(WhiteoutEventType.valueOf(tag.getString("Type")), tag.getLong("Time"), tag.getString("Dimension"), BlockPos.of((long)tag.getLong("Position")), tag.getInt("A"), tag.getInt("B"), tag.getInt("C"), tag.getInt("D"));
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}

