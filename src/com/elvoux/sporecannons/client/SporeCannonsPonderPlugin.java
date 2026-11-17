/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
 *  net.createmod.ponder.api.registration.PonderTagRegistrationHelper
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.ItemLike
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.client.WhiteoutPonderScenes;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import java.util.Arrays;
import java.util.Locale;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public final class SporeCannonsPonderPlugin
implements PonderPlugin {
    public static final ResourceLocation WHITEOUT_PROTOCOL = SporeCannons.id("whiteout_protocol");

    public String getModId() {
        return "sporecannons";
    }

    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        Object[] manufacturingIds = new ResourceLocation[]{SporeCannons.id("stabilized_cryogel"), SporeCannons.id("cryogenic_microcell"), SporeCannons.id("thermal_lining"), SporeCannons.id("pressure_regulator"), SporeCannons.id("cdu_core_i"), SporeCannons.id("cdu_core_ii"), SporeCannons.id("cdu_core_iii"), SporeCannons.id("cdu_core_iv"), SporeCannons.id("cdu_core_v"), SporeCannons.id("cdu_core_vi"), SporeCannons.id("cryogenic_machine_gun_round"), SporeCannons.id("cryogenic_ap_autocannon_round"), SporeCannons.id("cryogenic_flak_autocannon_round")};
        helper.forComponents(manufacturingIds).addStoryBoard("cryogenic_manufacturing", WhiteoutPonderScenes::cryogenicManufacturing, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("cdu_field_scanner")}).addStoryBoard("cdu_scanner", WhiteoutPonderScenes::scanner, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("cryogenic_sensor_fuze")}).addStoryBoard("cryogenic_sensor_fuze", WhiteoutPonderScenes::sensorFuze, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("cryogenic_mine_fuze")}).addStoryBoard("cryogenic_mine_fuze", WhiteoutPonderScenes::mineFuze, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("cdu_refueling_station")}).addStoryBoard("cdu_refueling_station", WhiteoutPonderScenes::refuelingStation, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("cryogenic_containment_projector")}).addStoryBoard("cryogenic_containment_projector", WhiteoutPonderScenes::containmentProjector, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("cdu_refueling_station"), SporeCannons.id("cryogenic_containment_projector")}).addStoryBoard("machine_kinetics", WhiteoutPonderScenes::machineKinetics, new ResourceLocation[]{WHITEOUT_PROTOCOL}).addStoryBoard("coolant_automation", WhiteoutPonderScenes::coolantAutomation, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("cryogenic_machine_gun_round"), SporeCannons.id("cryogenic_ap_autocannon_round"), SporeCannons.id("cryogenic_flak_autocannon_round"), SporeCannons.id(FreezeShellTier.I.id())}).addStoryBoard("cryogenic_saturation", WhiteoutPonderScenes::saturation, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("sample_containment_chamber")}).addStoryBoard("sample_storage", WhiteoutPonderScenes::sampleStorage, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("cryogenic_reservoir")}).addStoryBoard("cryogel_storage", WhiteoutPonderScenes::cryogelStorage, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("biomass_cultivator"), SporeCannons.id("cryogenic_biomass_processor")}).addStoryBoard("whiteout_biomass_cycle", WhiteoutPonderScenes::biomassCycle, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        helper.forComponents((Object[])new ResourceLocation[]{SporeCannons.id("projector_service_controller"), SporeCannons.id("refuelling_service_controller"), SporeCannons.id("cryogenic_containment_projector"), SporeCannons.id("cdu_refueling_station")}).addStoryBoard("whiteout_assemblies", WhiteoutPonderScenes::assemblies, new ResourceLocation[]{WHITEOUT_PROTOCOL});
        Object[] shellIds = (ResourceLocation[])Arrays.stream(FreezeShellTier.values()).map(tier -> SporeCannons.id(tier.id())).toArray(ResourceLocation[]::new);
        helper.forComponents(shellIds).addStoryBoard("large_shell_loading", WhiteoutPonderScenes::largeShellLoading, new ResourceLocation[]{WHITEOUT_PROTOCOL}).addStoryBoard("arsenal_tiers", WhiteoutPonderScenes::arsenalTiers, new ResourceLocation[]{WHITEOUT_PROTOCOL}).addStoryBoard("containment_field", WhiteoutPonderScenes::containmentField, new ResourceLocation[]{WHITEOUT_PROTOCOL}).addStoryBoard("bile_cleanup", WhiteoutPonderScenes::bileCleanup, new ResourceLocation[]{WHITEOUT_PROTOCOL});
    }

    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(WHITEOUT_PROTOCOL).title("Whiteout Protocol").description("Field systems for detecting, freezing and containing Spore infection").item((ItemLike)SporeCannonsItems.CDU_FIELD_SCANNER.get()).addToIndex().register();
        helper.addToTag(WHITEOUT_PROTOCOL).add((Object)SporeCannons.id("stabilized_cryogel")).add((Object)SporeCannons.id("cryogenic_microcell")).add((Object)SporeCannons.id("thermal_lining")).add((Object)SporeCannons.id("pressure_regulator")).add((Object)SporeCannons.id("whiteout_field_manual")).add((Object)SporeCannons.id("depleted_cdu_coolant_cartridge")).add((Object)SporeCannons.id("charged_cdu_coolant_cartridge")).add((Object)SporeCannons.id("cdu_refueling_station")).add((Object)SporeCannons.id("cryogenic_containment_projector")).add((Object)SporeCannons.id("sample_containment_chamber")).add((Object)SporeCannons.id("cryogenic_reservoir")).add((Object)SporeCannons.id("biomass_cultivator")).add((Object)SporeCannons.id("cryogenic_biomass_processor")).add((Object)SporeCannons.id("projector_service_controller")).add((Object)SporeCannons.id("refuelling_service_controller")).add((Object)SporeCannons.id("cdu_field_scanner")).add((Object)SporeCannons.id("cryogenic_sensor_fuze")).add((Object)SporeCannons.id("cryogenic_mine_fuze")).add((Object)SporeCannons.id("cryogenic_machine_gun_round")).add((Object)SporeCannons.id("cryogenic_ap_autocannon_round")).add((Object)SporeCannons.id("cryogenic_flak_autocannon_round")).add((Object)SporeCannons.id(FreezeShellTier.I.id()));
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            helper.addToTag(WHITEOUT_PROTOCOL).add((Object)SporeCannons.id("cdu_core_" + tier.name().toLowerCase(Locale.ROOT))).add((Object)SporeCannons.id(tier.id()));
        }
    }
}

