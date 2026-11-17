/*
 * Decompiled with CFR 0.152.
 */
package com.elvoux.sporecannons.facility;

public enum FacilityKind {
    STORAGE(12),
    TECHNICAL(16),
    WORKBENCH(8),
    TERMINAL(2);

    private final int slots;

    private FacilityKind(int slots) {
        this.slots = slots;
    }

    public int slots() {
        return this.slots;
    }
}

