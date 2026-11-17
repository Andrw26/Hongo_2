/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.client.MachineScreenGraphics;
import com.elvoux.sporecannons.facility.FacilityKind;
import com.elvoux.sporecannons.facility.FacilityMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class FacilityScreen
extends AbstractContainerScreen<FacilityMenu> {
    public FacilityScreen(FacilityMenu menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 224;
        this.inventoryLabelX = 47;
        this.inventoryLabelY = 136;
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        MachineScreenGraphics.frame(graphics, x, y, this.imageWidth, this.imageHeight);
        MachineScreenGraphics.inventoryPanel(graphics, x + 41, y + 142, 174, 77);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                MachineScreenGraphics.slot(graphics, x + 46 + col * 18, y + 146 + row * 18);
            }
        }
        for (int col = 0; col < 9; ++col) {
            MachineScreenGraphics.slot(graphics, x + 46 + col * 18, y + 204);
        }
        switch (((FacilityMenu)this.menu).kind()) {
            case STORAGE: {
                this.renderStorage(graphics, x, y);
                break;
            }
            case TECHNICAL: {
                this.renderTechnical(graphics, x, y);
                break;
            }
            case WORKBENCH: {
                this.renderWorkbench(graphics, x, y);
                break;
            }
            case TERMINAL: {
                this.renderTerminal(graphics, x, y);
            }
        }
    }

    private void renderStorage(GuiGraphics graphics, int x, int y) {
        MachineScreenGraphics.panel(graphics, x + 80, y + 27, 96, 72);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 4; ++col) {
                MachineScreenGraphics.slot(graphics, x + 91 + col * 18, y + 37 + row * 18);
            }
        }
    }

    private void renderTechnical(GuiGraphics graphics, int x, int y) {
        MachineScreenGraphics.panel(graphics, x + 15, y + 24, 146, 116);
        for (int row = 0; row < 5; ++row) {
            for (int col = 0; col < 7; ++col) {
                MachineScreenGraphics.slot(graphics, x + 24 + col * 18, y + 30 + row * 18);
            }
        }
        graphics.fill(x + 47, y + 55, x + 126, y + 58, -15320249);
        graphics.fill(x + 85, y + 49, x + 88, y + 101, -13904152);
        graphics.fill(x + 61, y + 73, x + 116, y + 76, -8834242);
        graphics.fill(x + 107, y + 57, x + 110, y + 93, -2909387);
        for (int px : new int[]{61, 85, 107, 125}) {
            graphics.fill(x + px, y + 54, x + px + 3, y + 59, -13248275);
        }
    }

    private void renderWorkbench(GuiGraphics graphics, int x, int y) {
        MachineScreenGraphics.panel(graphics, x + 17, y + 29, 40, 94);
        MachineScreenGraphics.panel(graphics, x + 199, y + 29, 40, 94);
        MachineScreenGraphics.panel(graphics, x + 89, y + 27, 78, 93);
        MachineScreenGraphics.slot(graphics, x + 118, y + 32);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                MachineScreenGraphics.slot(graphics, x + 100 + col * 18, y + 61 + row * 18);
            }
        }
        for (int slot = 0; slot < 4; ++slot) {
            MachineScreenGraphics.slot(graphics, x + 28, y + 43 + slot * 18);
            MachineScreenGraphics.slot(graphics, x + 208, y + 43 + slot * 18);
        }
        graphics.fill(x + 126, y + 52, x + 130, y + 57, -13248275);
    }

    private void renderTerminal(GuiGraphics graphics, int x, int y) {
        MachineScreenGraphics.panel(graphics, x + 14, y + 32, 39, 76);
        MachineScreenGraphics.slot(graphics, x + 23, y + 47);
        MachineScreenGraphics.slot(graphics, x + 23, y + 77);
        MachineScreenGraphics.panel(graphics, x + 59, y + 29, 181, 101);
        graphics.fill(x + 64, y + 36, x + 235, y + 124, -16577519);
        graphics.fill(x + 64, y + 36, x + 235, y + 38, -15303791);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        MachineScreenGraphics.drawFittedTitle(graphics, this.font, this.title, this.imageWidth / 2, 7, 212);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -8411215, false);
        if (((FacilityMenu)this.menu).kind() == FacilityKind.TERMINAL) {
            this.renderTerminalText(graphics);
        }
    }

    private void renderTerminalText(GuiGraphics graphics) {
        if (((FacilityMenu)this.menu).furniture() == null) {
            return;
        }
        ItemStack card = ((FacilityMenu)this.menu).furniture().inventory().getStackInSlot(0);
        ItemStack fieldTerminal = ((FacilityMenu)this.menu).furniture().inventory().getStackInSlot(1);
        if (card.isEmpty()) {
            MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)Component.translatable((String)"gui.sporecannons.facility.terminal.insert_storage"), 149, 68, 156, -8411215, 3);
            return;
        }
        if (fieldTerminal.isEmpty()) {
            MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)Component.translatable((String)"gui.sporecannons.facility.terminal.access_denied"), 149, 63, 156, -42647, 4);
            return;
        }
        String path = BuiltInRegistries.ITEM.getKey((Object)card.getItem()).getPath();
        graphics.drawString(this.font, (Component)Component.translatable((String)("archive.sporecannons." + path + ".title")), 68, 40, -13248275, false);
        List lines = this.font.split((FormattedText)Component.translatable((String)("archive.sporecannons." + path + ".body")), 160);
        for (int line = 0; line < Math.min(8, lines.size()); ++line) {
            graphics.drawString(this.font, (FormattedCharSequence)lines.get(line), 68, 55 + line * 9, -2295553, false);
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

