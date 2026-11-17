/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package com.elvoux.sporecannons.facility;

import net.minecraft.util.StringRepresentable;

public enum MultiblockPart implements StringRepresentable
{
    SINGLE("single"),
    MIN("min"),
    MIDDLE("middle"),
    MAX("max");

    private final String name;

    private MultiblockPart(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}

