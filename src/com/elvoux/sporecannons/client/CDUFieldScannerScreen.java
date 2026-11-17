/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.client.MachineScreenGraphics;
import com.elvoux.sporecannons.client.WhiteoutClientAlerts;
import com.elvoux.sporecannons.field.ProjectorTelemetry;
import com.elvoux.sporecannons.field.WhiteoutEventType;
import com.elvoux.sporecannons.field.WhiteoutLogEntry;
import com.elvoux.sporecannons.network.ScannerActionPayload;
import com.elvoux.sporecannons.network.ScannerTerminalPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

public final class CDUFieldScannerScreen
extends Screen {
    private static final int WINDOW_WIDTH = 320;
    private static final int WINDOW_HEIGHT = 216;
    private ScannerTerminalPayload snapshot;
    private Tab tab = Tab.SCAN;
    private int left;
    private int top;
    private TerminalButton scanButton;
    private TerminalButton deepButton;
    private TerminalButton refreshButton;
    private TerminalButton rangeButton;
    private TerminalButton soundButton;
    private TerminalButton projectorButton;
    private final List<TerminalButton> terminalButtons = new ArrayList<TerminalButton>();
    private int networkScroll;
    private int logScroll;
    private boolean draggingScrollbar;
    private int refreshTicks;

    private CDUFieldScannerScreen(ScannerTerminalPayload snapshot) {
        super((Component)Component.translatable((String)"gui.sporecannons.scanner.title"));
        this.snapshot = snapshot;
    }

    public static void openOrUpdate(ScannerTerminalPayload snapshot) {
        Minecraft minecraft = Minecraft.getInstance();
        WhiteoutClientAlerts.setUnread(snapshot.unread());
        Screen screen = minecraft.screen;
        if (screen instanceof CDUFieldScannerScreen) {
            CDUFieldScannerScreen screen2 = (CDUFieldScannerScreen)screen;
            screen2.snapshot = snapshot;
            screen2.clampScroll();
            screen2.updateRangeButton();
            screen2.updateStateButtons();
            if (screen2.tab == Tab.LOG && snapshot.unread()) {
                CDUFieldScannerScreen.request(ScannerActionPayload.Action.MARK_READ);
            }
            return;
        }
        minecraft.setScreen((Screen)new CDUFieldScannerScreen(snapshot));
    }

    protected void init() {
        this.terminalButtons.clear();
        this.left = (this.width - 320) / 2;
        this.top = (this.height - 216) / 2;
        this.addTerminalButton(new TerminalButton(this.left + 14, this.top + 25, 78, 18, (Component)Component.translatable((String)"gui.sporecannons.scanner.tab.scan"), () -> this.select(Tab.SCAN)));
        this.addTerminalButton(new TerminalButton(this.left + 96, this.top + 25, 96, 18, (Component)Component.translatable((String)"gui.sporecannons.scanner.tab.network"), () -> this.select(Tab.NETWORK)));
        this.addTerminalButton(new TerminalButton(this.left + 196, this.top + 25, 78, 18, (Component)Component.translatable((String)"gui.sporecannons.scanner.tab.log"), () -> this.select(Tab.LOG)));
        this.refreshButton = this.addTerminalButton(new TerminalButton(this.left + 278, this.top + 25, 28, 18, (Component)Component.literal((String)"\u21bb"), () -> CDUFieldScannerScreen.request(ScannerActionPayload.Action.REFRESH)));
        this.rangeButton = this.addTerminalButton(new TerminalButton(this.left + 236, this.top + 6, 70, 14, this.rangeLabel(), () -> CDUFieldScannerScreen.request(ScannerActionPayload.Action.CYCLE_RANGE)));
        this.soundButton = this.addTerminalButton(new TerminalButton(this.left + 18, this.top + 179, 70, 14, this.soundLabel(), () -> CDUFieldScannerScreen.request(ScannerActionPayload.Action.TOGGLE_SOUND)));
        this.projectorButton = this.addTerminalButton(new TerminalButton(this.left + 93, this.top + 179, 70, 14, this.projectorLabel(), () -> CDUFieldScannerScreen.request(ScannerActionPayload.Action.TOGGLE_PROJECTOR_EVENTS)));
        this.scanButton = this.addTerminalButton(new TerminalButton(this.left + 18, this.top + 196, 116, 16, (Component)Component.translatable((String)"gui.sporecannons.scanner.quick_scan"), () -> CDUFieldScannerScreen.request(ScannerActionPayload.Action.QUICK_SCAN)));
        this.deepButton = this.addTerminalButton(new TerminalButton(this.left + 139, this.top + 196, 163, 16, this.deepScanLabel(), () -> CDUFieldScannerScreen.request(ScannerActionPayload.Action.DEEP_SCAN)));
        this.updateStateButtons();
        this.updateButtons();
    }

    private void select(Tab tab) {
        this.tab = tab;
        this.draggingScrollbar = false;
        if (tab == Tab.LOG && this.snapshot.unread()) {
            CDUFieldScannerScreen.request(ScannerActionPayload.Action.MARK_READ);
        }
        this.updateButtons();
    }

    private void updateButtons() {
        if (this.scanButton != null) {
            boolean bl = this.scanButton.visible = this.tab == Tab.SCAN;
        }
        if (this.deepButton != null) {
            boolean bl = this.deepButton.visible = this.tab == Tab.SCAN;
        }
        if (this.refreshButton != null) {
            this.refreshButton.visible = this.tab != Tab.SCAN;
        }
    }

    private TerminalButton addTerminalButton(TerminalButton button) {
        this.terminalButtons.add(button);
        return (TerminalButton)this.addRenderableWidget((GuiEventListener)button);
    }

    private void updateRangeButton() {
        if (this.rangeButton != null) {
            this.rangeButton.setMessage(this.rangeLabel());
        }
    }

    private void updateStateButtons() {
        if (this.soundButton != null) {
            this.soundButton.setMessage(this.soundLabel());
        }
        if (this.projectorButton != null) {
            this.projectorButton.setMessage(this.projectorLabel());
        }
        if (this.deepButton != null) {
            this.deepButton.setMessage(this.deepScanLabel());
            this.deepButton.active = this.snapshot.deepScanTicks() <= 0;
        }
    }

    private Component soundLabel() {
        return Component.translatable((String)(this.snapshot.soundEnabled() ? "gui.sporecannons.scanner.sound_on" : "gui.sporecannons.scanner.sound_off"));
    }

    private Component projectorLabel() {
        return Component.translatable((String)(this.snapshot.projectorEventsEnabled() ? "gui.sporecannons.scanner.projectors_on" : "gui.sporecannons.scanner.projectors_off"));
    }

    private Component deepScanLabel() {
        if (this.snapshot.deepScanning()) {
            return Component.translatable((String)"gui.sporecannons.scanner.deep_scanning", (Object[])new Object[]{Math.max(1, (this.snapshot.deepScanTicks() + 19) / 20)});
        }
        if (this.snapshot.deepScanTicks() > 0) {
            return Component.translatable((String)"gui.sporecannons.scanner.deep_cooldown", (Object[])new Object[]{Math.max(1, (this.snapshot.deepScanTicks() + 19) / 20)});
        }
        return Component.translatable((String)"gui.sporecannons.scanner.deep_scan");
    }

    private Component rangeLabel() {
        return Component.translatable((String)"gui.sporecannons.scanner.range_button", (Object[])new Object[]{this.snapshot.range()});
    }

    private static void request(ScannerActionPayload.Action action) {
        PacketDistributor.sendToServer((CustomPacketPayload)new ScannerActionPayload(action), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public void tick() {
        super.tick();
        if (++this.refreshTicks >= 20) {
            this.refreshTicks = 0;
            CDUFieldScannerScreen.request(ScannerActionPayload.Action.REFRESH);
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        MachineScreenGraphics.frame(graphics, this.left, this.top, 320, 216);
        MachineScreenGraphics.drawFittedTitle(graphics, this.font, this.title, this.left + 120, this.top + 8, 205);
        MachineScreenGraphics.panel(graphics, this.left + 12, this.top + 48, 296, 127);
        switch (this.tab.ordinal()) {
            case 0: {
                this.renderScan(graphics);
                break;
            }
            case 1: {
                this.renderNetwork(graphics);
                break;
            }
            case 2: {
                this.renderLog(graphics);
            }
        }
        for (TerminalButton button : this.terminalButtons) {
            if (!button.visible) continue;
            button.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderScan(GuiGraphics graphics) {
        WhiteoutLogEntry scan = CDUFieldScannerScreen.latestScan(this.snapshot.log());
        int x = this.left + 22;
        int y = this.top + 58;
        graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.scan_header"), x, y, -13248275, false);
        if (scan == null) {
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.no_scan"), x, y + 20, -8411215, false);
        } else if (scan.type() == WhiteoutEventType.DEEP_SCAN_COMPLETE) {
            int tier = scan.valueD() & 0xFF;
            short depth = (short)(scan.valueD() >>> 8);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.deep_header"), x, y + 18, -4590337, false);
            this.drawLine(graphics, "gui.sporecannons.scanner.scan_signals", scan.valueB(), x, y + 35);
            this.drawLine(graphics, "gui.sporecannons.scanner.scan_parasites", scan.valueC(), x, y + 50);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.deep_epicentre", (Object[])new Object[]{scan.position().getX(), scan.position().getY(), scan.position().getZ()}), x, y + 65, -13248275, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.deep_depth", (Object[])new Object[]{(int)depth}), x, y + 80, -2295553, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.scan_tier", (Object[])new Object[]{CDUFieldScannerScreen.roman(tier)}), x, y + 95, -4590337, false);
        } else {
            int direction = (scan.valueD() >>> 8) - 1;
            int tier = scan.valueD() & 0xFF;
            MutableComponent focus = direction < 0 || direction >= Direction.values().length ? Component.translatable((String)"message.sporecannons.scanner_none") : Component.translatable((String)("gui.sporecannons.scanner.direction." + Direction.values()[direction].getName()));
            this.drawLine(graphics, "gui.sporecannons.scanner.scan_range", scan.valueA(), x, y + 18);
            this.drawLine(graphics, "gui.sporecannons.scanner.scan_signals", scan.valueB(), x, y + 33);
            this.drawLine(graphics, "gui.sporecannons.scanner.scan_parasites", scan.valueC(), x, y + 48);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.scan_focus", (Object[])new Object[]{focus}), x, y + 63, -2295553, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.scan_tier", (Object[])new Object[]{CDUFieldScannerScreen.roman(tier)}), x, y + 78, -4590337, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.scan_origin", (Object[])new Object[]{scan.position().getX(), scan.position().getY(), scan.position().getZ()}), x, y + 93, -8411215, false);
        }
        graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.scan_notice"), this.left + 157, this.top + 163, -8411215, false);
    }

    private void renderNetwork(GuiGraphics graphics) {
        int x = this.left + 20;
        int y = this.top + 55;
        List<ProjectorTelemetry> projectors = this.snapshot.projectors();
        graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.network_header", (Object[])new Object[]{projectors.size()}), x, y, -13248275, false);
        if (projectors.isEmpty()) {
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.network_empty"), x, y + 20, -8411215, false);
            return;
        }
        for (int i = 0; i < Math.min(7, projectors.size() - this.networkScroll); ++i) {
            ProjectorTelemetry projector = projectors.get(this.networkScroll + i);
            int rowY = y + 15 + i * 15;
            int color = CDUFieldScannerScreen.statusColor(projector.status());
            graphics.fill(x - 3, rowY - 2, this.left + 320 - 20, rowY + 11, i % 2 == 0 ? -15982552 : -16180960);
            graphics.drawString(this.font, (Component)Component.literal((String)String.format(Locale.ROOT, "%+d %+d %+d", projector.position().getX(), projector.position().getY(), projector.position().getZ())), x, rowY, -8411215, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)("gui.sporecannons.scanner.status." + projector.status())), x + 91, rowY, color, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)("gui.sporecannons.scanner.mode." + projector.mode())), x + 154, rowY, -2295553, false);
            String field = projector.mode().equals("wall") ? projector.fieldA() + "\u00d7" + projector.fieldB() + "\u00d7" + projector.fieldC() : "r" + projector.fieldA();
            graphics.drawString(this.font, field, x + 198, rowY, -13248275, false);
            graphics.drawString(this.font, projector.rpm() + "\u00b7" + CDUFieldScannerScreen.reserve(projector.coolantSeconds()), x + 233, rowY, -8411215, false);
        }
        this.renderScrollbar(graphics, projectors.size(), 7, this.networkScroll);
    }

    private void renderLog(GuiGraphics graphics) {
        int x = this.left + 20;
        int y = this.top + 55;
        graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.log_header", (Object[])new Object[]{this.snapshot.log().size()}), x, y, -13248275, false);
        if (this.snapshot.log().isEmpty()) {
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.sporecannons.scanner.log_empty"), x, y + 20, -8411215, false);
            return;
        }
        for (int i = 0; i < Math.min(8, this.snapshot.log().size() - this.logScroll); ++i) {
            WhiteoutLogEntry entry = this.snapshot.log().get(this.logScroll + i);
            int rowY = y + 15 + i * 13;
            long age = Math.max(0L, (this.snapshot.serverGameTime() - entry.gameTime()) / 20L);
            graphics.drawString(this.font, "T-" + CDUFieldScannerScreen.clock(age), x, rowY, -8411215, false);
            graphics.enableScissor(x + 42, y + 13, this.left + 320 - 22, this.top + 174);
            graphics.drawString(this.font, CDUFieldScannerScreen.eventText(entry), x + 43, rowY, CDUFieldScannerScreen.eventColor(entry.type()), false);
            graphics.disableScissor();
        }
        this.renderScrollbar(graphics, this.snapshot.log().size(), 8, this.logScroll);
    }

    private void renderScrollbar(GuiGraphics graphics, int total, int visible, int offset) {
        int x = this.left + 300;
        int y = this.top + 69;
        int height = 95;
        graphics.fill(x, y, x + 4, y + height, -16644085);
        graphics.fill(x + 1, y + 1, x + 3, y + height - 1, -12430501);
        if (total <= visible) {
            graphics.fill(x + 1, y + 1, x + 3, y + height - 1, -8411215);
            return;
        }
        int thumbHeight = Math.max(12, height * visible / total);
        int maxOffset = total - visible;
        int thumbY = y + (height - thumbHeight) * offset / maxOffset;
        graphics.fill(x, thumbY, x + 4, thumbY + thumbHeight, -15303791);
        graphics.fill(x + 1, thumbY + 1, x + 3, thumbY + thumbHeight - 1, -13248275);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.tab == Tab.NETWORK) {
            this.networkScroll = CDUFieldScannerScreen.scroll(this.networkScroll, this.snapshot.projectors().size(), 7, scrollY);
            return true;
        }
        if (this.tab == Tab.LOG) {
            this.logScroll = CDUFieldScannerScreen.scroll(this.logScroll, this.snapshot.log().size(), 8, scrollY);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.tab != Tab.SCAN && this.overScrollbar(mouseX, mouseY)) {
            this.draggingScrollbar = true;
            this.updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingScrollbar && button == 0) {
            this.updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.draggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean overScrollbar(double mouseX, double mouseY) {
        return mouseX >= (double)(this.left + 296) && mouseX <= (double)(this.left + 308) && mouseY >= (double)(this.top + 65) && mouseY <= (double)(this.top + 168);
    }

    private void updateScrollFromMouse(double mouseY) {
        int total = this.tab == Tab.NETWORK ? this.snapshot.projectors().size() : this.snapshot.log().size();
        int visible = this.tab == Tab.NETWORK ? 7 : 8;
        int max = Math.max(0, total - visible);
        int value = Mth.clamp((int)((int)Math.round((mouseY - (double)(this.top + 69)) / 95.0 * (double)max)), (int)0, (int)max);
        if (this.tab == Tab.NETWORK) {
            this.networkScroll = value;
        } else {
            this.logScroll = value;
        }
    }

    private static int scroll(int current, int total, int visible, double amount) {
        int max = Math.max(0, total - visible);
        return Mth.clamp((int)(current - (int)Math.signum(amount)), (int)0, (int)max);
    }

    private void clampScroll() {
        this.networkScroll = Mth.clamp((int)this.networkScroll, (int)0, (int)Math.max(0, this.snapshot.projectors().size() - 7));
        this.logScroll = Mth.clamp((int)this.logScroll, (int)0, (int)Math.max(0, this.snapshot.log().size() - 8));
    }

    private void drawLine(GuiGraphics graphics, String key, int value, int x, int y) {
        graphics.drawString(this.font, (Component)Component.translatable((String)key, (Object[])new Object[]{value}), x, y, -2295553, false);
    }

    private static WhiteoutLogEntry latestScan(List<WhiteoutLogEntry> log) {
        for (WhiteoutLogEntry entry : log) {
            if (entry.type() != WhiteoutEventType.SCAN_COMPLETE && entry.type() != WhiteoutEventType.DEEP_SCAN_COMPLETE) continue;
            return entry;
        }
        return null;
    }

    private static Component eventText(WhiteoutLogEntry entry) {
        String key = "gui.sporecannons.scanner.log." + entry.type().name().toLowerCase(Locale.ROOT);
        return switch (entry.type()) {
            default -> throw new MatchException(null, null);
            case WhiteoutEventType.SCAN_COMPLETE -> Component.translatable((String)key, (Object[])new Object[]{entry.valueA(), entry.valueB(), entry.valueC(), CDUFieldScannerScreen.roman(entry.valueD() & 0xFF)});
            case WhiteoutEventType.WHITE_WATCH_TRIGGERED -> Component.translatable((String)key, (Object[])new Object[]{CDUFieldScannerScreen.roman(entry.valueA())});
            case WhiteoutEventType.CDU_ACTIVATION -> Component.translatable((String)key, (Object[])new Object[]{CDUFieldScannerScreen.roman(entry.valueA()), entry.position().getX(), entry.position().getY(), entry.position().getZ()});
            case WhiteoutEventType.CDU_STRIKE -> Component.translatable((String)key, (Object[])new Object[]{CDUFieldScannerScreen.roman(entry.valueA()), entry.valueB()});
            case WhiteoutEventType.CDU_CLEANUP -> Component.translatable((String)key, (Object[])new Object[]{entry.valueB()});
            case WhiteoutEventType.ZONE_COMPLETE, WhiteoutEventType.ZONE_DISPLACED -> Component.translatable((String)key, (Object[])new Object[]{CDUFieldScannerScreen.roman(entry.valueA()), entry.valueB(), entry.valueC()});
            case WhiteoutEventType.PROJECTOR_STATUS -> Component.translatable((String)key, (Object[])new Object[]{CDUFieldScannerScreen.projectorStatus(entry.valueA()), entry.position().getX(), entry.position().getY(), entry.position().getZ()});
            case WhiteoutEventType.PROJECTOR_COOLANT_LOW -> Component.translatable((String)key, (Object[])new Object[]{entry.valueA(), CDUFieldScannerScreen.reserve(entry.valueB()), entry.position().getX(), entry.position().getY(), entry.position().getZ()});
            case WhiteoutEventType.DEEP_SCAN_COMPLETE -> Component.translatable((String)key, (Object[])new Object[]{entry.valueA(), entry.valueB(), entry.valueC(), entry.position().getX(), entry.position().getY(), entry.position().getZ()});
        };
    }

    private static int eventColor(WhiteoutEventType type) {
        return switch (type) {
            case WhiteoutEventType.CDU_ACTIVATION, WhiteoutEventType.CDU_STRIKE -> -38795;
            case WhiteoutEventType.PROJECTOR_COOLANT_LOW -> -19893;
            case WhiteoutEventType.PROJECTOR_STATUS -> -13248275;
            case WhiteoutEventType.DEEP_SCAN_COMPLETE -> -4590337;
            default -> -2295553;
        };
    }

    private static int statusColor(String status) {
        return switch (status) {
            case "active" -> -13248275;
            case "jammed" -> -38795;
            case "stopped" -> -19893;
            default -> -8411215;
        };
    }

    private static Component projectorStatus(int status) {
        String name = switch (status) {
            case 0 -> "active";
            case 1 -> "stopped";
            case 2 -> "jammed";
            case 4 -> "depleted";
            default -> "empty";
        };
        return Component.translatable((String)("gui.sporecannons.scanner.status." + name));
    }

    private static String roman(int tier) {
        return tier >= 1 && tier <= 6 ? (new String[]{"I", "II", "III", "IV", "V", "VI"})[tier - 1] : "\u2014";
    }

    private static String clock(long seconds) {
        return String.format(Locale.ROOT, "%02d:%02d", seconds / 60L, seconds % 60L);
    }

    private static String reserve(int seconds) {
        if (seconds <= 0) {
            return "0s";
        }
        if (seconds < 60) {
            return seconds + "s";
        }
        return seconds / 60 + "m";
    }

    public boolean isPauseScreen() {
        return false;
    }

    private static enum Tab {
        SCAN,
        NETWORK,
        LOG;

    }

    private final class TerminalButton
    extends AbstractButton {
        private final Runnable action;

        private TerminalButton(int x, int y, int width, int height, Component label, Runnable action) {
            super(x, y, width, height, label);
            this.action = action;
        }

        public void onPress() {
            this.action.run();
        }

        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            MachineScreenGraphics.panel(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            int color = this.active ? (this.isHoveredOrFocused() ? -4590337 : -13248275) : -8411215;
            graphics.drawString(CDUFieldScannerScreen.this.font, this.getMessage(), this.getX() + (this.getWidth() - CDUFieldScannerScreen.this.font.width((FormattedText)this.getMessage())) / 2, this.getY() + 5, color, false);
        }

        protected void updateWidgetNarration(NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }
}

