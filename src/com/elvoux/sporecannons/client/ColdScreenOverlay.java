/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.client.event.RenderGuiEvent$Post
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.ClientConfig;
import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.network.ColdScreenPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid="sporecannons", value={Dist.CLIENT})
public final class ColdScreenOverlay {
    private static final ResourceLocation FROST = SporeCannons.id("textures/gui/frost_vignette.png");
    private static int delay;
    private static int duration;
    private static int remaining;
    private static float intensity;

    private ColdScreenOverlay() {
    }

    public static void trigger(ColdScreenPayload payload) {
        if (!((Boolean)ClientConfig.COLD_SCREEN_EFFECT.get()).booleanValue()) {
            return;
        }
        int cap = (Integer)ClientConfig.MAX_DURATION_SECONDS.get() * 20;
        int requestedDuration = Math.min(cap, payload.durationTicks());
        float requestedIntensity = Mth.clamp((float)payload.intensity(), (float)0.0f, (float)1.0f);
        if (remaining > 0 && delay == 0) {
            duration = Math.max(duration, requestedDuration);
            remaining = Math.min(cap, Math.max(remaining, requestedDuration));
            intensity = Math.max(intensity, requestedIntensity);
        } else if (payload.delayTicks() <= delay || remaining <= 0) {
            delay = Math.max(0, payload.delayTicks());
            duration = requestedDuration;
            remaining = requestedDuration;
            intensity = requestedIntensity;
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused()) {
            return;
        }
        if (delay > 0) {
            --delay;
        } else if (remaining > 0 && --remaining == 0) {
            intensity = 0.0f;
        }
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        float flashWindow;
        float flash;
        int flashAlpha;
        if (delay > 0 || remaining <= 0 || duration <= 0 || !((Boolean)ClientConfig.COLD_SCREEN_EFFECT.get()).booleanValue()) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        float elapsed = 1.0f - (float)remaining / (float)duration;
        float fade = Mth.clamp((float)((float)remaining / Math.max(20.0f, (float)duration * 0.35f)), (float)0.0f, (float)1.0f);
        float accessibility = (Boolean)ClientConfig.PHOTOSENSITIVE_MODE.get() != false ? 0.35f : 1.0f;
        float personal = ((Double)ClientConfig.EFFECT_INTENSITY.get()).floatValue() * accessibility;
        float strength = intensity * personal * fade;
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int tintAlpha = Mth.clamp((int)((int)(strength * 105.0f)), (int)0, (int)120);
        graphics.fill(0, 0, width, height, tintAlpha << 24 | 0x21BFD0);
        if (((Boolean)ClientConfig.CYAN_FLASH.get()).booleanValue() && !((Boolean)ClientConfig.PHOTOSENSITIVE_MODE.get()).booleanValue() && (flashAlpha = Mth.clamp((int)((int)((flash = 1.0f - Mth.clamp((float)(elapsed / (flashWindow = (Boolean)ClientConfig.REDUCED_FLASHING.get() != false ? 0.035f : 0.075f)), (float)0.0f, (float)1.0f)) * strength * 190.0f)), (int)0, (int)190)) > 0) {
            graphics.fill(0, 0, width, height, flashAlpha << 24 | 0xD8FFFF);
        }
        if (((Boolean)ClientConfig.FROST_VIGNETTE.get()).booleanValue()) {
            float vignetteAlpha = Mth.clamp((float)(strength * (0.65f + elapsed * 0.35f)), (float)0.0f, (float)0.85f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)vignetteAlpha);
            graphics.blit(FROST, 0, 0, 0.0f, 0.0f, width, height, 256, 256);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.disableBlend();
        }
    }
}

