/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.BookViewScreen
 *  net.minecraft.client.gui.screens.inventory.BookViewScreen$BookAccess
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.munition.CryogelResearchLogItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;

public final class CryogelResearchLogScreen {
    public static void open() {
        Minecraft.getInstance().setScreen((Screen)new BookViewScreen(new BookViewScreen.BookAccess(CryogelResearchLogItem.pages())));
    }

    private CryogelResearchLogScreen() {
    }
}

