/*
 * Decompiled with CFR 0.152.
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public final class ContainmentProjectorRenderRegistry {
    private static final Set<CryogenicContainmentProjectorBlockEntity> TRACKED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));

    private ContainmentProjectorRenderRegistry() {
    }

    public static void track(CryogenicContainmentProjectorBlockEntity projector) {
        TRACKED.add(projector);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<CryogenicContainmentProjectorBlockEntity> snapshot() {
        Set<CryogenicContainmentProjectorBlockEntity> set = TRACKED;
        synchronized (set) {
            return new ArrayList<CryogenicContainmentProjectorBlockEntity>(TRACKED);
        }
    }
}

