/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$BooleanValue
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  net.neoforged.neoforge.common.ModConfigSpec$DoubleValue
 *  net.neoforged.neoforge.common.ModConfigSpec$IntValue
 */
package com.elvoux.sporecannons;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class FreezeConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue ENTITY_RADIUS;
    public static final ModConfigSpec.IntValue BLOCK_RADIUS;
    public static final ModConfigSpec.DoubleValue DAMAGE;
    public static final ModConfigSpec.IntValue DURATION;
    public static final ModConfigSpec.BooleanValue CLEAN_BLOCKS;
    public static final ModConfigSpec.IntValue BLOCK_CHECKS_PER_TICK;
    public static final ModConfigSpec.BooleanValue AFFECT_PLAYERS;
    public static final ModConfigSpec.BooleanValue AFFECT_NORMAL_MOBS;
    public static final ModConfigSpec.DoubleValue COLLATERAL_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue RESPECT_COVER;
    public static final ModConfigSpec.DoubleValue COVER_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue EXPLOSION_EFFECTS;
    public static final ModConfigSpec.BooleanValue SCREEN_SHAKE;
    public static final ModConfigSpec.BooleanValue COLD_SCREEN_PULSE;
    public static final ModConfigSpec.BooleanValue LOCAL_SNOWFALL;
    public static final ModConfigSpec.DoubleValue SNOWFALL_INTENSITY;
    public static final ModConfigSpec.BooleanValue DELAYED_AUDIO;
    public static final ModConfigSpec.BooleanValue PLACE_SNOW;
    public static final ModConfigSpec.DoubleValue SNOW_DENSITY;
    public static final ModConfigSpec.BooleanValue TEMPORARY_SNOW;
    public static final ModConfigSpec.IntValue SNOW_MELT_SECONDS;
    public static final ModConfigSpec.IntValue SNOW_COLUMNS_PER_TICK;
    public static final ModConfigSpec.BooleanValue AUTOCANNON_AFFECT_NON_SPORE;
    public static final ModConfigSpec.DoubleValue AUTOCANNON_COLD_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue AUTOCANNON_VISUAL_EFFECTS;
    public static final ModConfigSpec.BooleanValue AUTOCANNON_TRAILS;
    public static final ModConfigSpec.BooleanValue CRYOGENIC_SATURATION;
    public static final ModConfigSpec.IntValue SATURATION_DECAY_SECONDS;
    public static final ModConfigSpec.DoubleValue BRITTLE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue CONTAINMENT_ZONES;
    public static final ModConfigSpec.IntValue CONTAINMENT_BLOCK_CHECKS_PER_TICK;
    public static final ModConfigSpec.IntValue MAX_CONTAINMENT_ZONES;
    public static final ModConfigSpec.BooleanValue FIELD_REPORTS;
    public static final ModConfigSpec.BooleanValue FIELD_SCANNER;
    public static final ModConfigSpec.IntValue SCANNER_COOLDOWN_SECONDS;
    public static final ModConfigSpec.IntValue PROJECTOR_WALL_DEPTH;
    public static final ModConfigSpec.IntValue PROJECTOR_WALL_HEIGHT;
    public static final ModConfigSpec.DoubleValue PROJECTOR_WALL_CONSUMPTION_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue PROJECTOR_REGENERATION_MULTIPLIER;
    public static final ModConfigSpec.IntValue CULTIVATOR_PROCESS_TIME;
    public static final ModConfigSpec.IntValue CULTIVATOR_WATER_PER_CYCLE;
    public static final ModConfigSpec.IntValue PROCESSOR_PROCESS_TIME;
    public static final ModConfigSpec.IntValue PROCESSOR_BIOMASS_COOLANT;
    public static final ModConfigSpec.IntValue PROCESSOR_BLUE_ICE_COOLANT;
    public static final ModConfigSpec.IntValue PROCESSOR_BLUE_ICE_WATER;
    public static final ModConfigSpec.IntValue PROCESSOR_BLUE_ICE_PROCESS_TIME;
    public static final ModConfigSpec.IntValue PROCESSOR_SNOW_COOLANT;
    public static final ModConfigSpec.IntValue PROCESSOR_SNOW_WATER;
    public static final ModConfigSpec.IntValue PROCESSOR_SNOW_PROCESS_TIME;
    public static final ModConfigSpec.IntValue PROCESSOR_SNOW_OUTPUT;

    private FreezeConfig() {
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("freeze_shell");
        ENTITY_RADIUS = builder.comment("Tier I parasite pulse radius. Tier VI is approximately 500 blocks across.").defineInRange("entity_radius", 8, 1, 64);
        BLOCK_RADIUS = builder.comment("Tier I CDU terrain-cleaning radius. Tier VI is approximately 500 blocks across.").defineInRange("block_radius", 6, 1, 64);
        DAMAGE = builder.comment("Tier I immediate freeze damage. Tier VI deals sixteen times this value.").defineInRange("damage", 40.0, 0.0, 10000.0);
        DURATION = builder.comment("Tier I Spore Frostbite duration (ticks). Higher tiers last longer.").defineInRange("frostbite_duration", 600, 20, 12000);
        CLEAN_BLOCKS = builder.comment("Apply the CDU terrain effect: restore infection and freeze fungal matter.").define("clean_infection", true);
        BLOCK_CHECKS_PER_TICK = builder.comment("Maximum queued terrain checks per level tick. Large shells are staged to prevent server freezes.").defineInRange("block_checks_per_tick", 32768, 1024, 262144);
        builder.pop();
        builder.push("collateral_cold");
        AFFECT_PLAYERS = builder.comment("Let the radial cryogenic pulse damage and freeze survival/adventure players.").define("affect_players", true);
        AFFECT_NORMAL_MOBS = builder.comment("Let the radial cryogenic pulse damage and freeze non-Spore living mobs.").define("affect_normal_mobs", true);
        COLLATERAL_DAMAGE_MULTIPLIER = builder.comment("Multiplier for non-Spore cold damage. Default tier-center damage is 6/10/16/24/32/40.").defineInRange("damage_multiplier", 1.0, 0.0, 10.0);
        RESPECT_COVER = builder.comment("Reduce collateral damage and freezing when solid blocks shield the target from the impact.").define("respect_cover", true);
        COVER_MULTIPLIER = builder.comment("Exposure multiplier behind cover. 0 blocks the pulse completely; 1 makes cover ineffective.").defineInRange("cover_multiplier", 0.25, 0.0, 1.0);
        builder.pop();
        builder.push("explosion_edition");
        EXPLOSION_EFFECTS = builder.comment("Enable tier-specific staged shockwaves, ice fog, crystal bursts and layered sounds.").define("effects", true);
        SCREEN_SHAKE = builder.comment("Enable distance-scaled Ritchie's Projectile Library camera shake.").define("screen_shake", true);
        COLD_SCREEN_PULSE = builder.comment("Send direction-, distance- and cover-aware turquoise afterglow to nearby clients.").define("cold_screen_pulse", true);
        LOCAL_SNOWFALL = builder.comment("Create a local particle blizzard around players reached by the cryogenic wave.").define("local_snowfall", true);
        SNOWFALL_INTENSITY = builder.comment("Multiplier for local snowfall particle density.").defineInRange("snowfall_intensity", 1.0, 0.0, 3.0);
        DELAYED_AUDIO = builder.comment("Add a distance-delayed detonation and low rumble for remote observers.").define("delayed_audio", true);
        builder.pop();
        builder.push("snowfall");
        PLACE_SNOW = builder.comment("Place a patchy vanilla snow blanket near the impact. Ultra-warm dimensions are excluded.").define("place_snow", true);
        SNOW_DENSITY = builder.comment("Multiplier for patchy snow coverage around the impact.").defineInRange("snow_density", 0.7, 0.0, 1.0);
        TEMPORARY_SNOW = builder.comment("Remove unchanged snow placed by the shell after the configured delay.").define("temporary_snow", true);
        SNOW_MELT_SECONDS = builder.comment("Lifetime of shell-created snow before gradual cleanup.").defineInRange("snow_melt_time_seconds", 300, 10, 3600);
        SNOW_COLUMNS_PER_TICK = builder.comment("Maximum snow-placement columns processed per level tick.").defineInRange("snow_columns_per_tick", 512, 32, 4096);
        builder.pop();
        builder.push("cryogenic_autocannon");
        AUTOCANNON_AFFECT_NON_SPORE = builder.comment("Let cryogenic autocannon ammunition freeze players and ordinary mobs on direct or flak hits.").define("affect_non_spore", true);
        AUTOCANNON_COLD_MULTIPLIER = builder.comment("Multiplier for the bonus cold damage of cryogenic autocannon ammunition.").defineInRange("cold_damage_multiplier", 1.0, 0.0, 10.0);
        AUTOCANNON_VISUAL_EFFECTS = builder.comment("Enable impact rings, crystal bursts, cold screen pulses and compact blizzards for cryogenic autocannon ammunition.").define("visual_effects", true);
        AUTOCANNON_TRAILS = builder.comment("Enable lightweight tier-specific particle trails for cryogenic autocannon projectiles.").define("projectile_trails", true);
        builder.pop();
        builder.push("whiteout_protocol");
        CRYOGENIC_SATURATION = builder.comment("Let cryogenic weapons build saturation and make parasites brittle.").define("cryogenic_saturation", true);
        SATURATION_DECAY_SECONDS = builder.comment("Seconds without a cryogenic hit before partial saturation is lost.").defineInRange("saturation_decay_seconds", 20, 1, 300);
        BRITTLE_DAMAGE_MULTIPLIER = builder.comment("Damage multiplier for non-freezing damage against fully saturated parasites.").defineInRange("brittle_damage_multiplier", 1.5, 1.0, 5.0);
        CONTAINMENT_ZONES = builder.comment("Leave a bounded residual field that suppresses parasites and newly grown infection.").define("containment_zones", true);
        CONTAINMENT_BLOCK_CHECKS_PER_TICK = builder.comment("Global random block checks shared by all residual containment zones in a dimension.").defineInRange("containment_block_checks_per_tick", 256, 16, 4096);
        MAX_CONTAINMENT_ZONES = builder.comment("Maximum simultaneous residual zones per dimension. Oldest zones close first.").defineInRange("max_containment_zones", 8, 1, 32);
        FIELD_REPORTS = builder.comment("Send concise CDU strike and containment reports to the responsible player.").define("field_reports", true);
        FIELD_SCANNER = builder.comment("Enable the handheld CDU field scanner and its loaded-chunk infection survey.").define("field_scanner", true);
        SCANNER_COOLDOWN_SECONDS = builder.comment("Cooldown after a completed field scan.").defineInRange("scanner_cooldown_seconds", 8, 1, 120);
        PROJECTOR_WALL_DEPTH = builder.comment("Containment Projector Wall Mode thickness in blocks. Odd values keep the field centred.").defineInRange("projector_wall_depth", 3, 1, 9);
        PROJECTOR_WALL_HEIGHT = builder.comment("Wall Mode vertical coverage at 32 RPM. Speed scales it up to 32 blocks at 256 RPM with the default value.").defineInRange("projector_wall_height", 9, 3, 33);
        PROJECTOR_WALL_CONSUMPTION_MULTIPLIER = builder.comment("Additional coolant cost for the long perimeter coverage offered by Wall Mode.").defineInRange("projector_wall_consumption_multiplier", 1.25, 1.0, 4.0);
        PROJECTOR_REGENERATION_MULTIPLIER = builder.comment("Healing multiplier for affected entities inside an active Projector field. Zero blocks regeneration.").defineInRange("projector_regeneration_multiplier", 0.0, 0.0, 1.0);
        builder.pop();
        builder.push("whiteout_industry");
        CULTIVATOR_PROCESS_TIME = builder.comment("Base work units needed to cultivate one common biomass item. At 32 RPM, one work unit is completed per tick.").defineInRange("cultivator_process_time", 320, 20, 72000);
        CULTIVATOR_WATER_PER_CYCLE = builder.comment("Water consumed by one Biomass Cultivator cycle, in millibuckets.").defineInRange("cultivator_water_per_cycle", 250, 0, 4000);
        PROCESSOR_PROCESS_TIME = builder.comment("Base work units needed to cryogenically process one biomass item.").defineInRange("cryogenic_processor_process_time", 240, 20, 72000);
        PROCESSOR_BIOMASS_COOLANT = builder.comment("Coolant units consumed from a CDU cartridge for one biomass processing cycle. A full cartridge contains 54000 units.").defineInRange("cryogenic_processor_biomass_coolant", 900, 1, 54000);
        PROCESSOR_BLUE_ICE_COOLANT = builder.comment("Coolant units consumed from a CDU cartridge to freeze one Blue Ice block.").defineInRange("cryogenic_processor_blue_ice_coolant", 3600, 1, 54000);
        PROCESSOR_BLUE_ICE_WATER = builder.comment("Water consumed to freeze one Blue Ice block, in millibuckets.").defineInRange("cryogenic_processor_blue_ice_water", 1000, 1, 4000);
        PROCESSOR_BLUE_ICE_PROCESS_TIME = builder.comment("Base work units needed to deep-freeze one Blue Ice block.").defineInRange("cryogenic_processor_blue_ice_process_time", 480, 20, 72000);
        PROCESSOR_SNOW_COOLANT = builder.comment("Coolant units consumed to produce one batch of Snowballs. The Snowball input is a retained crystallisation seed.").defineInRange("cryogenic_processor_snow_coolant", 450, 1, 54000);
        PROCESSOR_SNOW_WATER = builder.comment("Water consumed to produce one batch of Snowballs, in millibuckets.").defineInRange("cryogenic_processor_snow_water", 250, 1, 4000);
        PROCESSOR_SNOW_PROCESS_TIME = builder.comment("Base work units needed to produce one batch of Snowballs.").defineInRange("cryogenic_processor_snow_process_time", 160, 20, 72000);
        PROCESSOR_SNOW_OUTPUT = builder.comment("Number of Snowballs produced per snow production cycle.").defineInRange("cryogenic_processor_snow_output", 4, 1, 64);
        builder.pop();
        SPEC = builder.build();
    }
}

