/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.BookViewScreen
 *  net.minecraft.client.gui.screens.inventory.BookViewScreen$BookAccess
 *  net.minecraft.network.chat.Component
 */
package com.elvoux.sporecannons.client;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;

public final class RegionalStationDocumentScreen {
    public static void open(List<Component> pages) {
        Minecraft.getInstance().setScreen((Screen)new BookViewScreen(new BookViewScreen.BookAccess(pages)));
    }

    private RegionalStationDocumentScreen() {
    }
}

