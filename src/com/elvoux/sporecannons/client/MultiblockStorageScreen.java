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
import com.elvoux.sporecannons.facility.MultiblockStorageBlock;
import com.elvoux.sporecannons.facility.MultiblockStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class MultiblockStorageScreen
extends AbstractContainerScreen<MultiblockStorageMenu> {
    private static final int SCROLL_X = 203;
    private static final int SCROLL_Y = 36;
    private static final int SCROLL_HEIGHT = 108;
    private boolean draggingScrollbar;

    public MultiblockStorageScreen(MultiblockStorageMenu menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.imageWidth = 220;
        this.imageHeight = 245;
        this.inventoryLabelX = 29;
        this.inventoryLabelY = 150;
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int col;
        int x = this.leftPos;
        int y = this.topPos;
        MachineScreenGraphics.frame(graphics, x, y, this.imageWidth, this.imageHeight);
        int accent = ((MultiblockStorageMenu)this.menu).kind() == MultiblockStorageBlock.StorageKind.SAMPLE ? -7226024 : -13248275;
        MachineScreenGraphics.panel(graphics, x + 19, y + 27, 181, 122);
        graphics.fill(x + 22, y + 30, x + 197, y + 32, accent);
        int visible = ((MultiblockStorageMenu)this.menu).visibleSlots();
        for (int slot = 0; slot < 54; ++slot) {
            col = slot % 9;
            int row = slot / 9;
            int sx = x + 28 + col * 18;
            int sy = y + 35 + row * 18;
            if (slot < visible) {
                MachineScreenGraphics.slot(graphics, sx, sy);
                graphics.fill(sx + 2, sy + 2, sx + 16, sy + 3, accent);
                continue;
            }
            graphics.fill(sx, sy, sx + 18, sy + 18, -16248554);
            graphics.fill(sx + 2, sy + 2, sx + 16, sy + 16, -15721696);
        }
        this.drawScrollbar(graphics, x, y, accent);
        MachineScreenGraphics.inventoryPanel(graphics, x + 23, y + 155, 174, 85);
        for (int row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                MachineScreenGraphics.slot(graphics, x + 28 + col * 18, y + 159 + row * 18);
            }
        }
        for (int col2 = 0; col2 < 9; ++col2) {
            MachineScreenGraphics.slot(graphics, x + 28 + col2 * 18, y + 217);
        }
    }

    private void drawScrollbar(GuiGraphics graphics, int x, int y, int accent) {
        graphics.fill(x + 203, y + 36, x + 203 + 8, y + 36 + 108, -16644342);
        graphics.fill(x + 203 + 2, y + 36 + 2, x + 203 + 6, y + 36 + 108 - 2, -14601156);
        int pages = ((MultiblockStorageMenu)this.menu).maxPage() + 1;
        int thumbHeight = pages <= 1 ? 104 : Math.max(14, 104 / pages);
        int travel = 104 - thumbHeight;
        int thumbY = y + 36 + 2 + (((MultiblockStorageMenu)this.menu).maxPage() == 0 ? 0 : travel * ((MultiblockStorageMenu)this.menu).page() / ((MultiblockStorageMenu)this.menu).maxPage());
        graphics.fill(x + 203 + 1, thumbY, x + 203 + 7, thumbY + thumbHeight, -15303791);
        graphics.fill(x + 203 + 3, thumbY + 2, x + 203 + 5, thumbY + thumbHeight - 2, accent);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        MachineScreenGraphics.drawFittedTitle(graphics, this.font, this.title, this.imageWidth / 2, 7, 184);
        MutableComponent summary = Component.translatable((String)"gui.sporecannons.multiblock.summary", (Object[])new Object[]{((MultiblockStorageMenu)this.menu).structureSize(), ((MultiblockStorageMenu)this.menu).structureSize(), ((MultiblockStorageMenu)this.menu).structureSize(), ((MultiblockStorageMenu)this.menu).moduleCount(), ((MultiblockStorageMenu)this.menu).totalSlots()});
        MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)summary, this.imageWidth / 2, 19, 184, -8411215, 1);
        if (((MultiblockStorageMenu)this.menu).maxPage() > 0) {
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.multiblock.page", (Object[])new Object[]{((MultiblockStorageMenu)this.menu).page() + 1, ((MultiblockStorageMenu)this.menu).maxPage() + 1}), 169, 137, -8411215, false);
        }
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -8411215, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (((MultiblockStorageMenu)this.menu).maxPage() > 0) {
            this.selectPage(((MultiblockStorageMenu)this.menu).page() + (scrollY < 0.0 ? 1 : -1));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.overScrollbar(mouseX, mouseY) && ((MultiblockStorageMenu)this.menu).maxPage() > 0) {
            this.draggingScrollbar = true;
            this.updatePageFromMouse(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingScrollbar && button == 0) {
            this.updatePageFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.draggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean overScrollbar(double mouseX, double mouseY) {
        return mouseX >= (double)(this.leftPos + 203 - 2) && mouseX <= (double)(this.leftPos + 203 + 10) && mouseY >= (double)(this.topPos + 36) && mouseY <= (double)(this.topPos + 36 + 108);
    }

    private void updatePageFromMouse(double mouseY) {
        double fraction = (mouseY - ((double)(this.topPos + 36) + 7.0)) / 94.0;
        this.selectPage((int)Math.round(Math.max(0.0, Math.min(1.0, fraction)) * (double)((MultiblockStorageMenu)this.menu).maxPage()));
    }

    private void selectPage(int page) {
        int selected = Math.max(0, Math.min(((MultiblockStorageMenu)this.menu).maxPage(), page));
        if (selected == ((MultiblockStorageMenu)this.menu).page()) {
            return;
        }
        ((MultiblockStorageMenu)this.menu).selectClientPage(selected);
        if (this.minecraft != null && this.minecraft.gameMode != null && this.minecraft.player != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(((MultiblockStorageMenu)this.menu).containerId, selected);
        }
    }
}

