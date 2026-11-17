/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 */
package com.elvoux.sporecannons.network;

import com.elvoux.sporecannons.network.ColdScreenPayload;
import com.elvoux.sporecannons.network.ScannerActionPayload;
import com.elvoux.sporecannons.network.ScannerAlertPayload;
import com.elvoux.sporecannons.network.ScannerTerminalPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class SporeCannonsNetwork {
    private SporeCannonsNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("4.4-dev.4").playToClient(ColdScreenPayload.TYPE, ColdScreenPayload.STREAM_CODEC, ColdScreenPayload::handle).playToClient(ScannerTerminalPayload.TYPE, ScannerTerminalPayload.STREAM_CODEC, ScannerTerminalPayload::handle).playToClient(ScannerAlertPayload.TYPE, ScannerAlertPayload.STREAM_CODEC, ScannerAlertPayload::handle).playToServer(ScannerActionPayload.TYPE, ScannerActionPayload.STREAM_CODEC, ScannerActionPayload::handle);
    }
}

