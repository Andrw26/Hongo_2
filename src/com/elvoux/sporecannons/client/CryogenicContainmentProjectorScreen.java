/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.client.MachineScreenGraphics;
import com.elvoux.sporecannons.munition.ContainmentFieldMode;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorMenu;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class CryogenicContainmentProjectorScreen
extends AbstractContainerScreen<CryogenicContainmentProjectorMenu> {
    private static final int RESERVE_X = 89;
    private static final int RESERVE_Y = 73;
    private static final int RESERVE_WIDTH = 81;
    private static final int KINETIC_X = 136;
    private static final int KINETIC_Y = 23;
    private AbstractButton modeButton;
    private AbstractButton cleanupButton;

    public CryogenicContainmentProjectorScreen(CryogenicContainmentProjectorMenu menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.imageWidth = 194;
        this.imageHeight = 193;
        this.inventoryLabelX = 16;
        this.inventoryLabelY = 100;
    }

    protected void init() {
        super.init();
        this.modeButton = (AbstractButton)this.addRenderableWidget((GuiEventListener)new FieldModeButton(this.leftPos + 9, this.topPos + 48));
        this.cleanupButton = (AbstractButton)this.addRenderableWidget((GuiEventListener)new CleanupModeButton(this.leftPos + 9, this.topPos + 69));
    }

    protected void containerTick() {
        super.containerTick();
        if (this.modeButton != null) {
            this.modeButton.setMessage(this.modeLabel());
        }
        if (this.cleanupButton != null) {
            this.cleanupButton.setMessage(this.cleanupLabel());
        }
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        MachineScreenGraphics.frame(graphics, x, y, this.imageWidth, this.imageHeight);
        MachineScreenGraphics.panel(graphics, x + 7, y + 22, 123, 22);
        MachineScreenGraphics.slot(graphics, x + 88, y + 46);
        MachineScreenGraphics.panel(graphics, x + 109, y + 46, 77, 20);
        MachineScreenGraphics.inventoryPanel(graphics, x + 10, y + 106, 174, 82);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                MachineScreenGraphics.slot(graphics, x + 15 + col * 18, y + 111 + row * 18);
            }
        }
        for (int col = 0; col < 9; ++col) {
            MachineScreenGraphics.slot(graphics, x + 15 + col * 18, y + 169);
        }
        CryogenicContainmentProjectorBlockEntity be = ((CryogenicContainmentProjectorMenu)this.menu).projector();
        int reserve = be == null ? 0 : be.reserveSeconds();
        int bar = Math.min(81, reserve * 81 / 2700);
        MachineScreenGraphics.horizontalBar(graphics, x + 89, y + 73, 81, bar, -13248275);
        MachineScreenGraphics.kinetic(graphics, this.font, x + 136, y + 23, be == null ? 0.0f : be.kineticSpeed(), be != null && be.kineticOverstressed(), partialTick);
        if (be != null && be.currentFieldRadius() > 0.0) {
            MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, CryogenicContainmentProjectorScreen.shortPerformance(be), x + 147, y + 48, 72, -13248275, 1);
            MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)Component.translatable((String)"gui.sporecannons.projector.consumption.short", (Object[])new Object[]{CryogenicContainmentProjectorScreen.decimal(be.coolantConsumptionMultiplier(), 2)}), x + 147, y + 57, 72, -8411215, 1);
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        MachineScreenGraphics.drawFittedTitle(graphics, this.font, (Component)Component.translatable((String)"gui.sporecannons.projector.title"), this.imageWidth / 2, 7, 170);
        CryogenicContainmentProjectorBlockEntity be = ((CryogenicContainmentProjectorMenu)this.menu).projector();
        MutableComponent status = Component.translatable((String)("gui.sporecannons.projector.status." + (be == null ? "empty" : be.statusKey())), (Object[])new Object[]{CryogenicContainmentProjectorScreen.duration(be == null ? 0 : be.estimatedRuntimeSeconds())});
        MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)status, 68, 25, 115, -13248275, 2);
        if (be != null) {
            MachineScreenGraphics.drawCenteredWrapped(graphics, this.font, (Component)Component.translatable((String)"gui.sporecannons.projector.statistics", (Object[])new Object[]{be.parasitesSuppressed(), be.signalsRemoved(), be.blocksCleaned()}), this.imageWidth / 2, 84, 180, -8411215, 2);
        }
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -8411215, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        this.renderMachineTooltip(graphics, mouseX, mouseY);
    }

    private void renderMachineTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        CryogenicContainmentProjectorBlockEntity be = ((CryogenicContainmentProjectorMenu)this.menu).projector();
        if (be == null) {
            return;
        }
        if (this.isHovering(88, 72, 83, 8, mouseX, mouseY)) {
            int percent = Math.round(100.0f * (float)be.coolantTicksRemaining() / 54000.0f);
            ArrayList<MutableComponent> lines = new ArrayList<MutableComponent>();
            lines.add(Component.translatable((String)"gui.sporecannons.projector.reserve", (Object[])new Object[]{percent}).withStyle(ChatFormatting.AQUA));
            if (be.coolantConsumptionMultiplier() > 0.0) {
                lines.add(CryogenicContainmentProjectorScreen.performance(be).copy().withStyle(ChatFormatting.AQUA));
                lines.add(Component.translatable((String)"gui.sporecannons.projector.runtime", (Object[])new Object[]{CryogenicContainmentProjectorScreen.duration(be.estimatedRuntimeSeconds())}).withStyle(ChatFormatting.GRAY));
            }
            if (be.statusKey().equals("stopped") || be.statusKey().equals("jammed")) {
                lines.add(Component.translatable((String)"gui.sporecannons.projector.reserve_paused").withStyle(ChatFormatting.GRAY));
            }
            graphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
        } else if (this.isHovering(136, 23, 52, 22, mouseX, mouseY)) {
            ArrayList<Component> lines = new ArrayList<Component>(MachineScreenGraphics.kineticTooltip(be.kineticSpeed(), be.kineticOverstressed()));
            if (be.currentFieldRadius() > 0.0) {
                lines.add((Component)CryogenicContainmentProjectorScreen.performance(be).copy().withStyle(ChatFormatting.AQUA));
            }
            graphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
        }
    }

    private Component modeLabel() {
        CryogenicContainmentProjectorBlockEntity be = ((CryogenicContainmentProjectorMenu)this.menu).projector();
        String mode = be == null ? "radial" : be.fieldMode().serializedName();
        return Component.translatable((String)("gui.sporecannons.projector.mode." + mode));
    }

    private Component cleanupLabel() {
        CryogenicContainmentProjectorBlockEntity be = ((CryogenicContainmentProjectorMenu)this.menu).projector();
        return Component.translatable((String)("gui.sporecannons.projector.cleanup." + (be == null || be.cleanupEnabled() ? "on" : "off")));
    }

    private static Component shortPerformance(CryogenicContainmentProjectorBlockEntity be) {
        if (be.fieldMode() == ContainmentFieldMode.WALL) {
            return Component.translatable((String)"gui.sporecannons.projector.performance.wall.short", (Object[])new Object[]{CryogenicContainmentProjectorScreen.decimal(be.wallWidth(), 0), be.wallDepth(), be.wallHeight()});
        }
        return Component.translatable((String)"gui.sporecannons.projector.performance.short", (Object[])new Object[]{CryogenicContainmentProjectorScreen.decimal(be.currentFieldRadius(), 1)});
    }

    private static Component performance(CryogenicContainmentProjectorBlockEntity be) {
        if (be.fieldMode() == ContainmentFieldMode.WALL) {
            return Component.translatable((String)"gui.sporecannons.projector.performance.wall", (Object[])new Object[]{CryogenicContainmentProjectorScreen.decimal(be.wallWidth(), 0), be.wallDepth(), be.wallHeight(), CryogenicContainmentProjectorScreen.decimal(be.coolantConsumptionMultiplier(), 2)});
        }
        return Component.translatable((String)"gui.sporecannons.projector.performance", (Object[])new Object[]{CryogenicContainmentProjectorScreen.decimal(be.currentFieldRadius(), 1), CryogenicContainmentProjectorScreen.decimal(be.coolantConsumptionMultiplier(), 2)});
    }

    private static String decimal(double value, int places) {
        return String.format(Locale.ROOT, "%1$." + places + "f", value);
    }

    private static Component duration(int seconds) {
        int safeSeconds = Math.max(0, seconds);
        return Component.translatable((String)"gui.sporecannons.projector.duration", (Object[])new Object[]{safeSeconds / 60, safeSeconds % 60});
    }

    private final class FieldModeButton
    extends AbstractButton {
        private FieldModeButton(int x, int y) {
            super(x, y, 72, 18, CryogenicContainmentProjectorScreen.this.modeLabel());
        }

        public void onPress() {
            if (CryogenicContainmentProjectorScreen.this.minecraft == null || ((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).minecraft.gameMode == null || ((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).minecraft.player == null) {
                return;
            }
            ((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).minecraft.gameMode.handleInventoryButtonClick(((CryogenicContainmentProjectorMenu)((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).menu).containerId, 0);
            ((CryogenicContainmentProjectorMenu)CryogenicContainmentProjectorScreen.this.menu).clickMenuButton((Player)((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).minecraft.player, 0);
            this.setMessage(CryogenicContainmentProjectorScreen.this.modeLabel());
        }

        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            MachineScreenGraphics.panel(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            if (this.isHoveredOrFocused()) {
                graphics.fill(this.getX() + 3, this.getY() + 2, this.getRight() - 3, this.getY() + 3, -4590337);
            }
            int color = this.isHoveredOrFocused() ? -4590337 : -13248275;
            graphics.drawString(CryogenicContainmentProjectorScreen.this.font, this.getMessage(), this.getX() + (this.getWidth() - CryogenicContainmentProjectorScreen.this.font.width((FormattedText)this.getMessage())) / 2, this.getY() + 5, color, false);
        }

        protected void updateWidgetNarration(NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }

    private final class CleanupModeButton
    extends AbstractButton {
        private CleanupModeButton(int x, int y) {
            super(x, y, 72, 14, CryogenicContainmentProjectorScreen.this.cleanupLabel());
        }

        public void onPress() {
            if (CryogenicContainmentProjectorScreen.this.minecraft == null || ((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).minecraft.gameMode == null || ((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).minecraft.player == null) {
                return;
            }
            ((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).minecraft.gameMode.handleInventoryButtonClick(((CryogenicContainmentProjectorMenu)((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).menu).containerId, 1);
            ((CryogenicContainmentProjectorMenu)CryogenicContainmentProjectorScreen.this.menu).clickMenuButton((Player)((CryogenicContainmentProjectorScreen)CryogenicContainmentProjectorScreen.this).minecraft.player, 1);
            this.setMessage(CryogenicContainmentProjectorScreen.this.cleanupLabel());
        }

        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            MachineScreenGraphics.panel(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            if (this.isHoveredOrFocused()) {
                graphics.fill(this.getX() + 3, this.getY() + 2, this.getRight() - 3, this.getY() + 3, -4590337);
            }
            int color = this.isHoveredOrFocused() ? -4590337 : -13248275;
            Component message = this.getMessage();
            graphics.drawString(CryogenicContainmentProjectorScreen.this.font, message, this.getX() + (this.getWidth() - CryogenicContainmentProjectorScreen.this.font.width((FormattedText)message)) / 2, this.getY() + 3, color, false);
        }

        protected void updateWidgetNarration(NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }
}

