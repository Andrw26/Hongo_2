/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.client.MachineScreenGraphics;
import com.elvoux.sporecannons.industry.AssemblyKind;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class WhiteoutAssemblyControllerScreen
extends AbstractContainerScreen<WhiteoutAssemblyControllerMenu> {
    public WhiteoutAssemblyControllerScreen(WhiteoutAssemblyControllerMenu menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.imageWidth = 214;
        this.imageHeight = 197;
        this.inventoryLabelX = 26;
        this.inventoryLabelY = 104;
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        MachineScreenGraphics.frame(graphics, x, y, this.imageWidth, this.imageHeight);
        MachineScreenGraphics.panel(graphics, x + 16, y + 26, 182, 72);
        AssemblyKind kind = ((WhiteoutAssemblyControllerMenu)this.menu).kind();
        if (kind == AssemblyKind.PROJECTOR) {
            for (slot = 0; slot < 6; ++slot) {
                MachineScreenGraphics.slot(graphics, x + 34 + slot * 18, y + 41);
                MachineScreenGraphics.slot(graphics, x + 34 + slot * 18, y + 69);
            }
        } else {
            for (slot = 0; slot < 6; ++slot) {
                MachineScreenGraphics.slot(graphics, x + 25 + slot * 18, y + 41);
            }
            for (slot = 0; slot < 3; ++slot) {
                MachineScreenGraphics.slot(graphics, x + 52 + slot * 18, y + 69);
                MachineScreenGraphics.slot(graphics, x + 124 + slot * 18, y + 69);
            }
            int water = ((WhiteoutAssemblyControllerMenu)this.menu).waterAmount();
            MachineScreenGraphics.verticalTank(graphics, x + 180, y + 37, 9, 43, water * 43 / 16000, -14186017);
        }
        MachineScreenGraphics.inventoryPanel(graphics, x + 20, y + 110, 174, 82);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                MachineScreenGraphics.slot(graphics, x + 25 + col * 18, y + 115 + row * 18);
            }
        }
        for (int col = 0; col < 9; ++col) {
            MachineScreenGraphics.slot(graphics, x + 25 + col * 18, y + 173);
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String id = ((WhiteoutAssemblyControllerMenu)this.menu).kind().id();
        MachineScreenGraphics.drawFittedTitle(graphics, this.font, (Component)Component.translatable((String)("block.sporecannons." + id)), this.imageWidth / 2, 7, 190);
        MutableComponent status = Component.translatable((String)("gui.sporecannons.assembly." + (((WhiteoutAssemblyControllerMenu)this.menu).formed() ? "formed" : "incomplete")));
        MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)status, this.imageWidth / 2, 88, 174, ((WhiteoutAssemblyControllerMenu)this.menu).formed() ? -13248275 : -42647, 1);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -8411215, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

