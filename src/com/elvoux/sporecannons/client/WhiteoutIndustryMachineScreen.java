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
import com.elvoux.sporecannons.industry.IndustryMachineKind;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlockEntity;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class WhiteoutIndustryMachineScreen
extends AbstractContainerScreen<WhiteoutIndustryMachineMenu> {
    public WhiteoutIndustryMachineScreen(WhiteoutIndustryMachineMenu menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.imageWidth = 194;
        this.imageHeight = 205;
        this.inventoryLabelX = 16;
        this.inventoryLabelY = 112;
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        MachineScreenGraphics.frame(graphics, x, y, this.imageWidth, this.imageHeight);
        MachineScreenGraphics.panel(graphics, x + 15, y + 25, 164, 84);
        MachineScreenGraphics.slot(graphics, x + 47, y + 57);
        MachineScreenGraphics.slot(graphics, x + 87, y + 57);
        MachineScreenGraphics.slot(graphics, x + 143, y + 57);
        WhiteoutIndustryMachineBlockEntity machine = ((WhiteoutIndustryMachineMenu)this.menu).machine();
        int progress = machine == null ? 0 : machine.progressPercent();
        MachineScreenGraphics.horizontalBar(graphics, x + 108, y + 63, 28, progress * 28 / 100, -13248275);
        if (machine != null) {
            int tank = machine.waterAmount() * 38 / 4000;
            MachineScreenGraphics.verticalTank(graphics, x + 22, y + 43, 10, 38, tank, -14186017);
        }
        if (machine != null && machine.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR) {
            int charge = machine.coolantRemaining() * 72 / machine.maxCoolant();
            MachineScreenGraphics.horizontalBar(graphics, x + 43, y + 29, 72, charge, -13248275);
        }
        MachineScreenGraphics.kinetic(graphics, this.font, x + 122, y + 27, machine == null ? 0.0f : machine.kineticSpeed(), machine != null && machine.kineticOverstressed(), partialTick);
        MachineScreenGraphics.inventoryPanel(graphics, x + 10, y + 118, 174, 82);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                MachineScreenGraphics.slot(graphics, x + 15 + col * 18, y + 123 + row * 18);
            }
        }
        for (int col = 0; col < 9; ++col) {
            MachineScreenGraphics.slot(graphics, x + 15 + col * 18, y + 181);
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        WhiteoutIndustryMachineBlockEntity machine = ((WhiteoutIndustryMachineMenu)this.menu).machine();
        String id = machine == null ? "biomass_cultivator" : machine.kind().id();
        MachineScreenGraphics.drawFittedTitle(graphics, this.font, (Component)Component.translatable((String)("block.sporecannons." + id)), this.imageWidth / 2, 7, 174);
        if (machine != null && machine.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR) {
            MachineScreenGraphics.drawFittedTitle(graphics, this.font, (Component)Component.translatable((String)"gui.sporecannons.industry.cartridge_cycles", (Object[])new Object[]{Math.round((float)machine.coolantRemaining() * 100.0f / (float)machine.maxCoolant()), machine.remainingCycles()}), 79, 40, 116);
            MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)Component.translatable((String)("gui.sporecannons.industry.mode." + machine.processorModeKey())), this.imageWidth / 2, 80, 150, -13248275, 1);
        }
        MutableComponent status = Component.translatable((String)("gui.sporecannons.industry.status." + (machine == null ? "stopped" : machine.statusKey())));
        int statusY = machine != null && machine.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR ? 94 : 84;
        MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)status, this.imageWidth / 2, statusY, 150, -13248275, 1);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -8411215, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        WhiteoutIndustryMachineBlockEntity machine = ((WhiteoutIndustryMachineMenu)this.menu).machine();
        if (machine != null && machine.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR && this.isHovering(47, 57, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, (Component)Component.translatable((String)"gui.sporecannons.industry.processing_input"), mouseX, mouseY);
        }
    }
}

