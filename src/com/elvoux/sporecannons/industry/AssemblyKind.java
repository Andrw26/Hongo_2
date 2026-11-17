/*
 * Decompiled with CFR 0.152.
 */
package com.elvoux.sporecannons.industry;

public enum AssemblyKind {
    PROJECTOR("projector_service_controller"),
    REFUELLING("refuelling_service_controller");

    private final String id;

    private AssemblyKind(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }
}

