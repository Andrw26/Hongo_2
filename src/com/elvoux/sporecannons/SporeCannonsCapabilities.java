/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.elvoux.sporecannons;

import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlockEntity;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlockEntity;
import com.elvoux.sporecannons.munition.CDURefuelingStationBlockEntity;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class SporeCannonsCapabilities {
    private SporeCannonsCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.CRYOGENIC_CONTAINMENT_PROJECTOR.get(), CryogenicContainmentProjectorBlockEntity::itemHandlerForSide);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.CDU_REFUELING_STATION.get(), CDURefuelingStationBlockEntity::itemHandlerForSide);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.CDU_REFUELING_STATION.get(), CDURefuelingStationBlockEntity::fluidHandlerForSide);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.FACILITY_STORAGE.get(), (furniture, side) -> furniture.inventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.MULTIBLOCK_STORAGE.get(), (storage, side) -> storage.automationHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.WHITEOUT_INDUSTRY_MACHINE.get(), WhiteoutIndustryMachineBlockEntity::itemHandlerForSide);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.WHITEOUT_INDUSTRY_MACHINE.get(), WhiteoutIndustryMachineBlockEntity::fluidHandlerForSide);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.WHITEOUT_ASSEMBLY_CONTROLLER.get(), WhiteoutAssemblyControllerBlockEntity::itemHandlerForSide);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)SporeCannonsBlockEntities.WHITEOUT_ASSEMBLY_CONTROLLER.get(), WhiteoutAssemblyControllerBlockEntity::fluidHandlerForSide);
    }
}

