/*
 * Decompiled with CFR 0.152.
 */
package com.elvoux.sporecannons.munition;

public enum ContainmentFieldMode {
    RADIAL("radial"),
    WALL("wall");

    private final String serializedName;

    private ContainmentFieldMode(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public ContainmentFieldMode next() {
        return this == RADIAL ? WALL : RADIAL;
    }

    public static ContainmentFieldMode byName(String name) {
        for (ContainmentFieldMode mode : ContainmentFieldMode.values()) {
            if (!mode.serializedName.equals(name)) continue;
            return mode;
        }
        return RADIAL;
    }
}

