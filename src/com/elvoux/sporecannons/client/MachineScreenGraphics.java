/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  com.simibubi.create.AllBlocks
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.elvoux.sporecannons.client;

import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

final class MachineScreenGraphics {
    static final int CYAN = -13248275;
    static final int CYAN_GLOW = -15303791;
    static final int ICE = -4590337;
    static final int DARK = -16313060;
    static final int PANEL = -15456208;
    static final int PANEL_DARK = -16578034;
    static final int STEEL = -12430501;
    static final int STEEL_LIGHT = -9337202;
    static final int SLOT = -14995400;
    static final int SLOT_LIGHT = -11771286;
    static final int SPORE_RED = -6544330;
    static final int TEXT = -2295553;
    static final int TEXT_DIM = -8411215;

    private MachineScreenGraphics() {
    }

    static void frame(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x + 2, y + 3, x + width + 2, y + height + 3, -1728053248);
        graphics.fill(x, y, x + width, y + height, -16644599);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -9337202);
        graphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, -15194580);
        graphics.fill(x + 5, y + 5, x + width - 5, y + height - 5, -16313060);
        graphics.fill(x + 11, y + 2, x + width - 11, y + 5, -14141377);
        graphics.fill(x + 17, y + 3, x + width - 17, y + 4, -9337202);
        MachineScreenGraphics.glowStrip(graphics, x + width / 2 - 24, y + 2, 48);
        graphics.fill(x + 6, y + 18, x + width - 6, y + 21, -16644085);
        graphics.fill(x + 10, y + 19, x + width - 10, y + 20, -15303791);
        graphics.fill(x + 9, y + height - 6, x + width - 9, y + height - 3, -14272963);
        graphics.fill(x + 20, y + height - 5, x + width - 20, y + height - 4, -9337202);
        MachineScreenGraphics.glowStrip(graphics, x + width / 2 - 18, y + height - 6, 36);
        MachineScreenGraphics.cornerPlate(graphics, x + 2, y + 2, false, false);
        MachineScreenGraphics.cornerPlate(graphics, x + width - 12, y + 2, true, false);
        MachineScreenGraphics.cornerPlate(graphics, x + 2, y + height - 12, false, true);
        MachineScreenGraphics.cornerPlate(graphics, x + width - 12, y + height - 12, true, true);
        MachineScreenGraphics.frost(graphics, x + 5, y + 5, false);
        MachineScreenGraphics.frost(graphics, x + width - 13, y + 5, true);
        MachineScreenGraphics.fungalCable(graphics, x + 4, y + height - 28, false);
        MachineScreenGraphics.fungalCable(graphics, x + width - 9, y + height - 28, true);
    }

    static void panel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, -16578034);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -12430501);
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, -15456208);
        graphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, -16114910);
        graphics.fill(x + 3, y + 2, x + width - 3, y + 3, -15303791);
    }

    static void slot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, -16644342);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, -12430501);
        graphics.fill(x + 2, y + 2, x + 16, y + 16, -14995400);
        graphics.fill(x + 3, y + 3, x + 16, y + 4, -11771286);
        graphics.fill(x + 3, y + 4, x + 4, y + 16, -11771286);
        graphics.fill(x + 14, y + 14, x + 16, y + 16, -15303791);
    }

    static void inventoryPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        MachineScreenGraphics.panel(graphics, x, y, width, height);
        graphics.fill(x + 5, y + 5, x + width - 5, y + height - 5, -16247268);
        graphics.fill(x + 5, y + 5, x + width - 5, y + 6, -14730942);
    }

    static void horizontalBar(GuiGraphics graphics, int x, int y, int width, int filled, int color) {
        graphics.fill(x - 2, y - 2, x + width + 2, y + 8, -16644342);
        graphics.fill(x - 1, y - 1, x + width + 1, y + 7, -12430501);
        graphics.fill(x, y, x + width, y + 6, -16114910);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 2, -14795705);
        int safeFilled = Math.max(0, Math.min(width, filled));
        if (safeFilled > 0) {
            graphics.fill(x, y, x + safeFilled, y + 6, -15303791);
            graphics.fill(x + 1, y + 1, x + safeFilled, y + 5, color);
            graphics.fill(x + 1, y + 1, x + safeFilled, y + 2, -4590337);
        }
    }

    static void verticalTank(GuiGraphics graphics, int x, int y, int width, int height, int filledHeight, int color) {
        graphics.fill(x - 2, y - 2, x + width + 2, y + height + 2, -16644342);
        graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, -9337202);
        graphics.fill(x, y, x + width, y + height, -16114910);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -15717835);
        int safeFilled = Math.max(0, Math.min(height, filledHeight));
        if (safeFilled > 0) {
            int top = y + height - safeFilled;
            graphics.fill(x + 1, top, x + width - 1, y + height - 1, color);
            graphics.fill(x + 2, top, x + 3, y + height - 1, -8525569);
            graphics.fill(x + 1, top, x + width - 1, top + 1, -4590337);
        }
        for (int mark = 1; mark < 4; ++mark) {
            int markY = y + mark * height / 4;
            graphics.fill(x + width - 3, markY, x + width - 1, markY + 1, -9337202);
        }
    }

    static void drawFittedTitle(GuiGraphics graphics, Font font, Component text, int centerX, int y, int maxWidth) {
        int textWidth = Math.max(1, font.width((FormattedText)text));
        float scale = Math.min(1.0f, (float)maxWidth / (float)textWidth);
        graphics.pose().pushPose();
        graphics.pose().translate((float)centerX, (float)y, 120.0f);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.drawString(font, text, -textWidth / 2, 0, -2295553, false);
        graphics.pose().popPose();
    }

    static void drawCenteredWrapped(GuiGraphics graphics, Font font, Component text, int centerX, int y, int width, int color, int maxLines) {
        List lines = font.split((FormattedText)text, width);
        for (int i = 0; i < Math.min(maxLines, lines.size()); ++i) {
            FormattedCharSequence line = (FormattedCharSequence)lines.get(i);
            graphics.drawString(font, line, centerX - font.width(line) / 2, y + i * 9, color, false);
        }
    }

    static void kinetic(GuiGraphics graphics, Font font, int x, int y, float speed, boolean overstressed, float partialTick) {
        int color = MachineScreenGraphics.kineticColor(speed, overstressed);
        MachineScreenGraphics.panel(graphics, x, y, 52, 22);
        graphics.fill(x + 3, y + 3, x + 20, y + 20, color);
        graphics.fill(x + 4, y + 4, x + 19, y + 19, -16313060);
        float gameTime = Minecraft.getInstance().level == null ? 0.0f : (float)Minecraft.getInstance().level.getGameTime() + partialTick;
        float angle = speed == 0.0f ? 0.0f : gameTime * speed * 0.3f;
        graphics.pose().pushPose();
        graphics.pose().translate((float)(x + 11), (float)(y + 11), 100.0f);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(angle));
        graphics.renderItem(new ItemStack((ItemLike)AllBlocks.COGWHEEL.get()), -8, -8);
        graphics.pose().popPose();
        String rpm = Integer.toString(Math.round(Math.abs(speed)));
        graphics.drawString(font, rpm, x + 23, y + 3, color, false);
        graphics.drawString(font, "RPM", x + 23, y + 12, -8411215, false);
    }

    static int kineticColor(float speed, boolean overstressed) {
        if (overstressed) {
            return -3125931;
        }
        if (speed == 0.0f) {
            return -8682104;
        }
        if (Math.abs(speed) < 32.0f) {
            return -2516426;
        }
        return -13248275;
    }

    static List<Component> kineticTooltip(float speed, boolean overstressed) {
        ArrayList<Component> lines = new ArrayList<Component>();
        lines.add((Component)Component.translatable((String)"gui.sporecannons.kinetic.title").withStyle(ChatFormatting.AQUA));
        lines.add((Component)Component.translatable((String)"gui.sporecannons.kinetic.speed", (Object[])new Object[]{Math.round(Math.abs(speed))}).withStyle(ChatFormatting.GRAY));
        String state = overstressed ? "overstressed" : (speed == 0.0f ? "disconnected" : (Math.abs(speed) < 32.0f ? "slow" : "ready"));
        lines.add((Component)Component.translatable((String)("gui.sporecannons.kinetic." + state)).withStyle(overstressed ? ChatFormatting.RED : (state.equals("ready") ? ChatFormatting.AQUA : ChatFormatting.GOLD)));
        return lines;
    }

    private static void glowStrip(GuiGraphics graphics, int x, int y, int width) {
        graphics.fill(x, y, x + width, y + 3, -16379626);
        graphics.fill(x + 2, y + 1, x + width - 2, y + 2, -13248275);
        graphics.fill(x + 7, y + 1, x + width - 7, y + 2, -4590337);
    }

    private static void cornerPlate(GuiGraphics graphics, int x, int y, boolean right, boolean bottom) {
        int sx = right ? -1 : 1;
        int sy = bottom ? -1 : 1;
        int innerX = right ? x + 2 : x + 8;
        int innerY = bottom ? y + 2 : y + 8;
        graphics.fill(x, y, x + 10, y + 10, -16446706);
        graphics.fill(Math.min(x + 2, innerX), Math.min(y + 2, innerY), Math.max(x + 8, innerX), Math.max(y + 8, innerY), -12430501);
        graphics.fill(x + (right ? 2 : 7), y + (bottom ? 2 : 7), x + (right ? 3 : 8), y + (bottom ? 3 : 8), -4590337);
        graphics.fill(x + (right ? 7 : 2), y + (bottom ? 7 : 2), x + (right ? 8 : 3), y + (bottom ? 8 : 3), -15656675);
        graphics.fill(innerX, innerY, innerX + sx, innerY + sy, -13248275);
    }

    private static void frost(GuiGraphics graphics, int x, int y, boolean right) {
        int direction = right ? -1 : 1;
        MachineScreenGraphics.pixel(graphics, x, y, -4590337);
        MachineScreenGraphics.pixel(graphics, x + direction, y + 1, -9643796);
        MachineScreenGraphics.pixel(graphics, x + direction * 2, y + 2, -4590337);
        MachineScreenGraphics.pixel(graphics, x, y + 2, -8668729);
        MachineScreenGraphics.pixel(graphics, x + direction, y + 3, -11892328);
    }

    private static void fungalCable(GuiGraphics graphics, int x, int y, boolean right) {
        int direction = right ? -1 : 1;
        for (int i = 0; i < 5; ++i) {
            int px = x + direction * (i % 2);
            int py = y + i * 3;
            graphics.fill(px, py, px + 2, py + 4, i % 2 == 0 ? -6544330 : -10545371);
        }
        MachineScreenGraphics.pixel(graphics, x + direction * 2, y + 4, -44188);
        MachineScreenGraphics.pixel(graphics, x + direction, y + 12, -3065011);
    }

    private static void pixel(GuiGraphics graphics, int x, int y, int color) {
        graphics.fill(x, y, x + 1, y + 1, color);
    }
}

