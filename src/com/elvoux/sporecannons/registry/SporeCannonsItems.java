/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.DoubleHighBlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.registries.DeferredBlock
 *  net.neoforged.neoforge.registries.DeferredItem
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.DeferredRegister$Items
 *  rbasamoyai.createbigcannons.munitions.FuzedProjectileBlockItem
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.munition.CDUComponentItem;
import com.elvoux.sporecannons.munition.CDUCoolantCartridgeItem;
import com.elvoux.sporecannons.munition.CDUFieldScannerItem;
import com.elvoux.sporecannons.munition.CryogelResearchLogItem;
import com.elvoux.sporecannons.munition.CryogenicAPRoundItem;
import com.elvoux.sporecannons.munition.CryogenicFlakRoundItem;
import com.elvoux.sporecannons.munition.CryogenicMachineGunRoundItem;
import com.elvoux.sporecannons.munition.CryogenicMicrocellItem;
import com.elvoux.sporecannons.munition.CryogenicMineFuzeItem;
import com.elvoux.sporecannons.munition.CryogenicSensorFuzeItem;
import com.elvoux.sporecannons.munition.FreezeShellItem;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.munition.RegionalStationDocumentItem;
import com.elvoux.sporecannons.munition.WhiteoutFieldManualItem;
import com.elvoux.sporecannons.registry.SporeCannonsBlocks;
import com.elvoux.sporecannons.registry.WhiteoutFacilityBlocks;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.munitions.FuzedProjectileBlockItem;

public final class SporeCannonsItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems((String)"sporecannons");
    public static final Map<FreezeShellTier, DeferredItem<FuzedProjectileBlockItem>> FREEZE_SHELLS = new EnumMap<FreezeShellTier, DeferredItem<FuzedProjectileBlockItem>>(FreezeShellTier.class);
    public static final DeferredItem<FuzedProjectileBlockItem> FREEZE_SHELL;
    public static final DeferredItem<CryogenicMicrocellItem> CRYOGENIC_MICROCELL;
    public static final DeferredItem<CDUComponentItem> STABILIZED_CRYOGEL;
    public static final DeferredItem<CDUComponentItem> THERMAL_LINING;
    public static final DeferredItem<CDUComponentItem> PRESSURE_REGULATOR;
    public static final DeferredItem<CDUComponentItem> COMMUNICATION_MODULE;
    public static final Map<FreezeShellTier, DeferredItem<CDUComponentItem>> CDU_CORES;
    public static final DeferredItem<CDUComponentItem> INCOMPLETE_CDU_ASSEMBLY;
    public static final DeferredItem<CryogenicMachineGunRoundItem> CRYOGENIC_MACHINE_GUN_ROUND;
    public static final DeferredItem<CryogenicAPRoundItem> CRYOGENIC_AP_AUTOCANNON_ROUND;
    public static final DeferredItem<CryogenicFlakRoundItem> CRYOGENIC_FLAK_AUTOCANNON_ROUND;
    public static final DeferredItem<CDUFieldScannerItem> CDU_FIELD_SCANNER;
    public static final DeferredItem<CryogenicSensorFuzeItem> CRYOGENIC_SENSOR_FUZE;
    public static final DeferredItem<CryogenicMineFuzeItem> CRYOGENIC_MINE_FUZE;
    public static final DeferredItem<WhiteoutFieldManualItem> WHITEOUT_FIELD_MANUAL;
    public static final DeferredItem<CryogelResearchLogItem> CRYOGEL_RESEARCH_LOG;
    public static final Map<String, DeferredItem<RegionalStationDocumentItem>> REGIONAL_STATION_DOCUMENTS;
    public static final DeferredItem<Item> MEMORY_CARD_LOGISTICS;
    public static final DeferredItem<Item> MEMORY_CARD_CONTAINMENT;
    public static final DeferredItem<Item> MEMORY_CARD_PERSONNEL;
    public static final DeferredItem<Item> MEMORY_CARD_INCIDENT;
    public static final DeferredItem<Item> MEMORY_CARD_DIRECTIVE;
    public static final DeferredItem<CDUComponentItem> DEPLETED_CDU_COOLANT_CARTRIDGE;
    public static final DeferredItem<CDUCoolantCartridgeItem> CHARGED_CDU_COOLANT_CARTRIDGE;
    public static final DeferredItem<BlockItem> CDU_REFUELING_STATION;
    public static final DeferredItem<BlockItem> CRYOGENIC_CONTAINMENT_PROJECTOR;
    public static final DeferredItem<BlockItem> BIOMASS_CULTIVATOR;
    public static final DeferredItem<BlockItem> CRYOGENIC_BIOMASS_PROCESSOR;
    public static final DeferredItem<BlockItem> PROJECTOR_SERVICE_CONTROLLER;
    public static final DeferredItem<BlockItem> REFUELLING_SERVICE_CONTROLLER;
    public static final Map<String, DeferredItem<BlockItem>> WHITEOUT_FACILITY_ITEMS;

    private static DeferredItem<Item> memoryCard(String id) {
        return REGISTER.register(id, () -> new Item(new Item.Properties().stacksTo(1)));
    }

    private static void regionalDocument(String id, String recordId) {
        REGIONAL_STATION_DOCUMENTS.put(id, (DeferredItem<RegionalStationDocumentItem>)REGISTER.register(id, () -> new RegionalStationDocumentItem(new Item.Properties().stacksTo(1), recordId, 2)));
    }

    private static DeferredItem<BlockItem> blockItem(String id, DeferredBlock<? extends Block> block) {
        return REGISTER.register(id, () -> new BlockItem((Block)block.get(), new Item.Properties()));
    }

    private SporeCannonsItems() {
    }

    static {
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            FREEZE_SHELLS.put(tier, (DeferredItem<FuzedProjectileBlockItem>)REGISTER.register(tier.id(), () -> new FreezeShellItem((Block)SporeCannonsBlocks.FREEZE_SHELLS.get((Object)tier).get(), new Item.Properties())));
        }
        FREEZE_SHELL = FREEZE_SHELLS.get((Object)FreezeShellTier.I);
        CRYOGENIC_MICROCELL = REGISTER.register("cryogenic_microcell", () -> new CryogenicMicrocellItem(new Item.Properties()));
        STABILIZED_CRYOGEL = REGISTER.register("stabilized_cryogel", () -> new CDUComponentItem(new Item.Properties(), "stabilized_cryogel"));
        THERMAL_LINING = REGISTER.register("thermal_lining", () -> new CDUComponentItem(new Item.Properties(), "thermal_lining"));
        PRESSURE_REGULATOR = REGISTER.register("pressure_regulator", () -> new CDUComponentItem(new Item.Properties(), "pressure_regulator"));
        COMMUNICATION_MODULE = REGISTER.register("communication_module", () -> new CDUComponentItem(new Item.Properties().stacksTo(16), "communication_module"));
        CDU_CORES = new EnumMap<FreezeShellTier, DeferredItem<CDUComponentItem>>(FreezeShellTier.class);
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            String id2 = "cdu_core_" + tier.name().toLowerCase(Locale.ROOT);
            CDU_CORES.put(tier, (DeferredItem<CDUComponentItem>)REGISTER.register(id2, () -> new CDUComponentItem(new Item.Properties().stacksTo(16), "cdu_core")));
        }
        INCOMPLETE_CDU_ASSEMBLY = REGISTER.register("incomplete_cdu_assembly", () -> new CDUComponentItem(new Item.Properties().stacksTo(1), "incomplete_cdu_assembly"));
        CRYOGENIC_MACHINE_GUN_ROUND = REGISTER.register("cryogenic_machine_gun_round", () -> new CryogenicMachineGunRoundItem(new Item.Properties()));
        CRYOGENIC_AP_AUTOCANNON_ROUND = REGISTER.register("cryogenic_ap_autocannon_round", () -> new CryogenicAPRoundItem(new Item.Properties()));
        CRYOGENIC_FLAK_AUTOCANNON_ROUND = REGISTER.register("cryogenic_flak_autocannon_round", () -> new CryogenicFlakRoundItem(new Item.Properties()));
        CDU_FIELD_SCANNER = REGISTER.register("cdu_field_scanner", () -> new CDUFieldScannerItem(new Item.Properties().stacksTo(1)));
        CRYOGENIC_SENSOR_FUZE = REGISTER.register("cryogenic_sensor_fuze", () -> new CryogenicSensorFuzeItem(new Item.Properties().stacksTo(1)));
        CRYOGENIC_MINE_FUZE = REGISTER.register("cryogenic_mine_fuze", () -> new CryogenicMineFuzeItem(new Item.Properties().stacksTo(1)));
        WHITEOUT_FIELD_MANUAL = REGISTER.register("whiteout_field_manual", () -> new WhiteoutFieldManualItem(new Item.Properties().stacksTo(1)));
        CRYOGEL_RESEARCH_LOG = REGISTER.register("cryogel_research_log", () -> new CryogelResearchLogItem(new Item.Properties().stacksTo(1)));
        REGIONAL_STATION_DOCUMENTS = new LinkedHashMap<String, DeferredItem<RegionalStationDocumentItem>>();
        SporeCannonsItems.regionalDocument("regional_station_routing_order", "routing_order");
        SporeCannonsItems.regionalDocument("regional_station_personnel_notice", "personnel_notice");
        SporeCannonsItems.regionalDocument("regional_station_logistics_manifest", "logistics_manifest");
        SporeCannonsItems.regionalDocument("regional_station_containment_report", "containment_report");
        SporeCannonsItems.regionalDocument("regional_station_final_status", "final_status");
        MEMORY_CARD_LOGISTICS = SporeCannonsItems.memoryCard("memory_card_logistics");
        MEMORY_CARD_CONTAINMENT = SporeCannonsItems.memoryCard("memory_card_containment");
        MEMORY_CARD_PERSONNEL = SporeCannonsItems.memoryCard("memory_card_personnel");
        MEMORY_CARD_INCIDENT = SporeCannonsItems.memoryCard("memory_card_incident");
        MEMORY_CARD_DIRECTIVE = SporeCannonsItems.memoryCard("memory_card_directive");
        DEPLETED_CDU_COOLANT_CARTRIDGE = REGISTER.register("depleted_cdu_coolant_cartridge", () -> new CDUComponentItem(new Item.Properties().stacksTo(16), "depleted_cdu_coolant_cartridge"));
        CHARGED_CDU_COOLANT_CARTRIDGE = REGISTER.register("charged_cdu_coolant_cartridge", () -> new CDUCoolantCartridgeItem(new Item.Properties().stacksTo(16)));
        CDU_REFUELING_STATION = REGISTER.register("cdu_refueling_station", () -> new BlockItem((Block)SporeCannonsBlocks.CDU_REFUELING_STATION.get(), new Item.Properties()));
        CRYOGENIC_CONTAINMENT_PROJECTOR = REGISTER.register("cryogenic_containment_projector", () -> new BlockItem((Block)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get(), new Item.Properties()));
        BIOMASS_CULTIVATOR = SporeCannonsItems.blockItem("biomass_cultivator", SporeCannonsBlocks.BIOMASS_CULTIVATOR);
        CRYOGENIC_BIOMASS_PROCESSOR = SporeCannonsItems.blockItem("cryogenic_biomass_processor", SporeCannonsBlocks.CRYOGENIC_BIOMASS_PROCESSOR);
        PROJECTOR_SERVICE_CONTROLLER = SporeCannonsItems.blockItem("projector_service_controller", SporeCannonsBlocks.PROJECTOR_SERVICE_CONTROLLER);
        REFUELLING_SERVICE_CONTROLLER = SporeCannonsItems.blockItem("refuelling_service_controller", SporeCannonsBlocks.REFUELLING_SERVICE_CONTROLLER);
        WHITEOUT_FACILITY_ITEMS = new LinkedHashMap<String, DeferredItem<BlockItem>>();
        WhiteoutFacilityBlocks.ALL.forEach((id, block) -> WHITEOUT_FACILITY_ITEMS.put((String)id, (DeferredItem<BlockItem>)REGISTER.register(id, () -> WhiteoutFacilityBlocks.DOUBLE_HIGH_ITEMS.contains(id) ? new DoubleHighBlockItem((Block)block.get(), new Item.Properties()) : new BlockItem((Block)block.get(), new Item.Properties()))));
    }
}

