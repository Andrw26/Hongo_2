/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$BooleanValue
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  net.neoforged.neoforge.common.ModConfigSpec$DoubleValue
 *  net.neoforged.neoforge.common.ModConfigSpec$EnumValue
 *  net.neoforged.neoforge.common.ModConfigSpec$IntValue
 */
package com.elvoux.sporecannons;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue COLD_SCREEN_EFFECT;
    public static final ModConfigSpec.BooleanValue CYAN_FLASH;
    public static final ModConfigSpec.BooleanValue FROST_VIGNETTE;
    public static final ModConfigSpec.DoubleValue EFFECT_INTENSITY;
    public static final ModConfigSpec.IntValue MAX_DURATION_SECONDS;
    public static final ModConfigSpec.BooleanValue REDUCED_FLASHING;
    public static final ModConfigSpec.BooleanValue PHOTOSENSITIVE_MODE;
    public static final ModConfigSpec.EnumValue<ContainmentFieldEffects> CONTAINMENT_FIELD_EFFECTS;

    private ClientConfig() {
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("cryogenic_screen_effect");
        COLD_SCREEN_EFFECT = builder.comment("Enable the turquoise cryogenic afterglow.").define("enabled", true);
        CYAN_FLASH = builder.comment("Enable the brief cyan-white flash at the start of exposure.").define("cyan_flash", true);
        FROST_VIGNETTE = builder.comment("Render frost around the edge of the screen.").define("frost_vignette", true);
        EFFECT_INTENSITY = builder.comment("Personal intensity multiplier for the complete screen effect.").defineInRange("intensity", 1.0, 0.0, 2.0);
        MAX_DURATION_SECONDS = builder.comment("Personal cap for one cryogenic afterglow.").defineInRange("max_duration_seconds", 30, 1, 60);
        REDUCED_FLASHING = builder.comment("Shorten and soften the initial bright flash.").define("reduced_flashing", false);
        PHOTOSENSITIVE_MODE = builder.comment("Accessibility mode: removes the white flash and strongly reduces overlay intensity.").define("photosensitive_mode", false);
        builder.pop();
        builder.push("containment_field");
        CONTAINMENT_FIELD_EFFECTS = builder.comment("Client-only Projector field visualization quality. Does not affect server mechanics.").defineEnum("effects", (Enum)ContainmentFieldEffects.NORMAL);
        builder.pop();
        SPEC = builder.build();
    }

    public static enum ContainmentFieldEffects {
        OFF,
        LOW,
        NORMAL,
        HIGH;

    }
}

