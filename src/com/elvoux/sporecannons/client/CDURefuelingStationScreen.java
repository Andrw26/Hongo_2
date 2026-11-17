/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.client.MachineScreenGraphics;
import com.elvoux.sporecannons.munition.CDURefuelingStationBlockEntity;
import com.elvoux.sporecannons.munition.CDURefuelingStationMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class CDURefuelingStationScreen
extends AbstractContainerScreen<CDURefuelingStationMenu> {
    private static final int WATER_X = 16;
    private static final int WATER_Y = 28;
    private static final int WATER_WIDTH = 12;
    private static final int WATER_HEIGHT = 46;
    private static final int KINETIC_X = 136;
    private static final int KINETIC_Y = 23;

    public CDURefuelingStationScreen(CDURefuelingStationMenu menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.imageWidth = 194;
        this.imageHeight = 185;
        this.inventoryLabelX = 16;
        this.inventoryLabelY = 92;
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        MachineScreenGraphics.frame(graphics, x, y, this.imageWidth, this.imageHeight);
        MachineScreenGraphics.panel(graphics, x + 8, y + 23, 126, 52);
        MachineScreenGraphics.inventoryPanel(graphics, x + 10, y + 98, 174, 82);
        MachineScreenGraphics.slot(graphics, x + 51, y + 27);
        MachineScreenGraphics.slot(graphics, x + 121, y + 51);
        MachineScreenGraphics.panel(graphics, x + 39, y + 76, 149, 14);
        CDURefuelingStationBlockEntity be = ((CDURefuelingStationMenu)this.menu).station();
        int progress = be == null ? 0 : be.progressPercent();
        MachineScreenGraphics.horizontalBar(graphics, x + 76, y + 34, 36, progress * 36 / 100, -13248275);
        int water = be == null ? 0 : be.waterAmount();
        int waterHeight = water * 46 / 4000;
        MachineScreenGraphics.verticalTank(graphics, x + 16, y + 28, 12, 46, waterHeight, -14186017);
        graphics.fill(x + 69, y + 35, x + 74, y + 37, -15303791);
        graphics.fill(x + 113, y + 37, x + 116, y + 56, -15303791);
        graphics.fill(x + 116, y + 54, x + 120, y + 56, -15303791);
        graphics.fill(x + 118, y + 53, x + 120, y + 58, -13248275);
        MachineScreenGraphics.kinetic(graphics, this.font, x + 136, y + 23, be == null ? 0.0f : be.kineticSpeed(), be != null && be.kineticOverstressed(), partialTick);
        if (be != null && be.processingMultiplier() > 0.0) {
            MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)Component.translatable((String)"gui.sporecannons.refueling.performance.short", (Object[])new Object[]{CDURefuelingStationScreen.decimal(be.processingMultiplier(), 2), CDURefuelingStationScreen.decimal(be.estimatedProcessSeconds(), 1)}), x + 94, y + 45, 48, -8411215, 2);
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                MachineScreenGraphics.slot(graphics, x + 15 + col * 18, y + 103 + row * 18);
            }
        }
        for (int col = 0; col < 9; ++col) {
            MachineScreenGraphics.slot(graphics, x + 15 + col * 18, y + 161);
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        MachineScreenGraphics.drawFittedTitle(graphics, this.font, (Component)Component.translatable((String)"gui.sporecannons.refueling.title"), this.imageWidth / 2, 7, 170);
        CDURefuelingStationBlockEntity be = ((CDURefuelingStationMenu)this.menu).station();
        MutableComponent status = Component.translatable((String)("gui.sporecannons.refueling.status." + (be == null ? "empty" : be.statusKey())));
        MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)status, 113, 79, 142, -13248275, 1);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -8411215, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        this.renderMachineTooltip(graphics, mouseX, mouseY);
    }

    private void renderMachineTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        CDURefuelingStationBlockEntity be = ((CDURefuelingStationMenu)this.menu).station();
        if (be == null) {
            return;
        }
        if (this.isHovering(15, 27, 14, 48, mouseX, mouseY)) {
            int water = be.waterAmount();
            graphics.renderTooltip(this.font, List.of((Object)Component.translatable((String)"gui.sporecannons.refueling.water", (Object[])new Object[]{water, 4000}).withStyle(ChatFormatting.BLUE), (Object)Component.translatable((String)"gui.sporecannons.refueling.water_buckets", (Object[])new Object[]{water / 1000, 4}).withStyle(ChatFormatting.GRAY), (Object)Component.translatable((String)"gui.sporecannons.refueling.water_input").withStyle(ChatFormatting.DARK_GRAY)), Optional.empty(), mouseX, mouseY);
        } else if (this.isHovering(136, 23, 52, 22, mouseX, mouseY)) {
            ArrayList<Component> lines = new ArrayList<Component>(MachineScreenGraphics.kineticTooltip(be.kineticSpeed(), be.kineticOverstressed()));
            if (be.processingMultiplier() > 0.0) {
                lines.add((Component)Component.translatable((String)"gui.sporecannons.refueling.performance", (Object[])new Object[]{CDURefuelingStationScreen.decimal(be.processingMultiplier(), 2), CDURefuelingStationScreen.decimal(be.estimatedProcessSeconds(), 1)}).withStyle(ChatFormatting.AQUA));
            }
            graphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
        } else if (this.isHovering(51, 27, 18, 18, mouseX, mouseY) && be.inventory().getStackInSlot(0).isEmpty()) {
            graphics.renderTooltip(this.font, (Component)Component.translatable((String)"gui.sporecannons.refueling.slot.cryogel"), mouseX, mouseY);
        } else if (this.isHovering(121, 51, 18, 18, mouseX, mouseY) && be.inventory().getStackInSlot(1).isEmpty()) {
            graphics.renderTooltip(this.font, (Component)Component.translatable((String)"gui.sporecannons.refueling.slot.cartridge"), mouseX, mouseY);
        } else if (this.isHovering(76, 34, 36, 7, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, (Component)Component.translatable((String)"gui.sporecannons.refueling.progress", (Object[])new Object[]{be.progressPercent()}), mouseX, mouseY);
        }
    }

    private static String decimal(double value, int places) {
        return String.format(Locale.ROOT, "%1$." + places + "f", value);
    }
}

