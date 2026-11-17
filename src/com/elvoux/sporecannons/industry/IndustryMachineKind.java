/*
 * Decompiled with CFR 0.152.
 */
package com.elvoux.sporecannons.industry;

public enum IndustryMachineKind {
    CULTIVATOR("biomass_cultivator"),
    CRYOGENIC_PROCESSOR("cryogenic_biomass_processor");

    private final String id;

    private IndustryMachineKind(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }
}

