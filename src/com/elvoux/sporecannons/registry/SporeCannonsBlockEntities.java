/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntity
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.facility.FacilityStorageBlockEntity;
import com.elvoux.sporecannons.facility.MultiblockStorageBlockEntity;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlockEntity;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlockEntity;
import com.elvoux.sporecannons.munition.CDURefuelingStationBlockEntity;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsBlocks;
import com.elvoux.sporecannons.registry.WhiteoutFacilityBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntity;

public final class SporeCannonsBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.BLOCK_ENTITY_TYPE, (String)"sporecannons");
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FuzedBlockEntity>> FREEZE_SHELL = REGISTER.register("freeze_shell", () -> BlockEntityType.Builder.of((pos, state) -> new FuzedBlockEntity((BlockEntityType)FREEZE_SHELL.get(), pos, state), (Block[])((Block[])SporeCannonsBlocks.FREEZE_SHELLS.values().stream().map(holder -> (Block)holder.get()).toArray(Block[]::new))).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CDURefuelingStationBlockEntity>> CDU_REFUELING_STATION = REGISTER.register("cdu_refueling_station", () -> BlockEntityType.Builder.of(CDURefuelingStationBlockEntity::new, (Block[])new Block[]{(Block)SporeCannonsBlocks.CDU_REFUELING_STATION.get()}).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CryogenicContainmentProjectorBlockEntity>> CRYOGENIC_CONTAINMENT_PROJECTOR = REGISTER.register("cryogenic_containment_projector", () -> BlockEntityType.Builder.of(CryogenicContainmentProjectorBlockEntity::new, (Block[])new Block[]{(Block)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get()}).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FacilityStorageBlockEntity>> FACILITY_STORAGE = REGISTER.register("facility_storage", () -> BlockEntityType.Builder.of(FacilityStorageBlockEntity::new, (Block[])new Block[]{(Block)WhiteoutFacilityBlocks.STORAGE_CABINET.get(), (Block)WhiteoutFacilityBlocks.TECHNICAL_EQUIPMENT_CABINET.get(), (Block)WhiteoutFacilityBlocks.LABORATORY_WORKBENCH.get(), (Block)WhiteoutFacilityBlocks.LABORATORY_TERMINAL.get()}).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiblockStorageBlockEntity>> MULTIBLOCK_STORAGE = REGISTER.register("multiblock_storage", () -> BlockEntityType.Builder.of(MultiblockStorageBlockEntity::new, (Block[])new Block[]{(Block)WhiteoutFacilityBlocks.SAMPLE_CONTAINMENT_CHAMBER.get(), (Block)WhiteoutFacilityBlocks.CRYOGENIC_RESERVOIR.get()}).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WhiteoutIndustryMachineBlockEntity>> WHITEOUT_INDUSTRY_MACHINE = REGISTER.register("whiteout_industry_machine", () -> BlockEntityType.Builder.of(WhiteoutIndustryMachineBlockEntity::new, (Block[])new Block[]{(Block)SporeCannonsBlocks.BIOMASS_CULTIVATOR.get(), (Block)SporeCannonsBlocks.CRYOGENIC_BIOMASS_PROCESSOR.get()}).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WhiteoutAssemblyControllerBlockEntity>> WHITEOUT_ASSEMBLY_CONTROLLER = REGISTER.register("whiteout_assembly_controller", () -> BlockEntityType.Builder.of(WhiteoutAssemblyControllerBlockEntity::new, (Block[])new Block[]{(Block)SporeCannonsBlocks.PROJECTOR_SERVICE_CONTROLLER.get(), (Block)SporeCannonsBlocks.REFUELLING_SERVICE_CONTROLLER.get()}).build(null));

    private SporeCannonsBlockEntities() {
    }
}

