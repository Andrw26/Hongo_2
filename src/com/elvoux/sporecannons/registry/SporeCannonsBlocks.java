/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.neoforged.neoforge.registries.DeferredBlock
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.DeferredRegister$Blocks
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.industry.AssemblyKind;
import com.elvoux.sporecannons.industry.IndustryMachineKind;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlock;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlock;
import com.elvoux.sporecannons.munition.CDURefuelingStationBlock;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlock;
import com.elvoux.sporecannons.munition.FreezeShellBlock;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SporeCannonsBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks((String)"sporecannons");
    public static final Map<FreezeShellTier, DeferredBlock<FreezeShellBlock>> FREEZE_SHELLS = new EnumMap<FreezeShellTier, DeferredBlock<FreezeShellBlock>>(FreezeShellTier.class);
    public static final DeferredBlock<FreezeShellBlock> FREEZE_SHELL;
    public static final DeferredBlock<CDURefuelingStationBlock> CDU_REFUELING_STATION;
    public static final DeferredBlock<CryogenicContainmentProjectorBlock> CRYOGENIC_CONTAINMENT_PROJECTOR;
    public static final DeferredBlock<WhiteoutIndustryMachineBlock> BIOMASS_CULTIVATOR;
    public static final DeferredBlock<WhiteoutIndustryMachineBlock> CRYOGENIC_BIOMASS_PROCESSOR;
    public static final DeferredBlock<WhiteoutAssemblyControllerBlock> PROJECTOR_SERVICE_CONTROLLER;
    public static final DeferredBlock<WhiteoutAssemblyControllerBlock> REFUELLING_SERVICE_CONTROLLER;

    private SporeCannonsBlocks() {
    }

    static {
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            FREEZE_SHELLS.put(tier, (DeferredBlock<FreezeShellBlock>)REGISTER.register(tier.id(), () -> new FreezeShellBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).sound(SoundType.METAL).noOcclusion())));
        }
        FREEZE_SHELL = FREEZE_SHELLS.get((Object)FreezeShellTier.I);
        CDU_REFUELING_STATION = REGISTER.register("cdu_refueling_station", () -> new CDURefuelingStationBlock(BlockBehaviour.Properties.of().strength(4.0f, 8.0f).sound(SoundType.METAL).noOcclusion()));
        CRYOGENIC_CONTAINMENT_PROJECTOR = REGISTER.register("cryogenic_containment_projector", () -> new CryogenicContainmentProjectorBlock(BlockBehaviour.Properties.of().strength(4.0f, 8.0f).sound(SoundType.METAL).noOcclusion()));
        BIOMASS_CULTIVATOR = REGISTER.register("biomass_cultivator", () -> new WhiteoutIndustryMachineBlock(BlockBehaviour.Properties.of().strength(5.0f, 10.0f).sound(SoundType.METAL).noOcclusion(), IndustryMachineKind.CULTIVATOR));
        CRYOGENIC_BIOMASS_PROCESSOR = REGISTER.register("cryogenic_biomass_processor", () -> new WhiteoutIndustryMachineBlock(BlockBehaviour.Properties.of().strength(5.0f, 10.0f).sound(SoundType.METAL).noOcclusion(), IndustryMachineKind.CRYOGENIC_PROCESSOR));
        PROJECTOR_SERVICE_CONTROLLER = REGISTER.register("projector_service_controller", () -> new WhiteoutAssemblyControllerBlock(BlockBehaviour.Properties.of().strength(5.0f, 10.0f).sound(SoundType.METAL).noOcclusion(), AssemblyKind.PROJECTOR));
        REFUELLING_SERVICE_CONTROLLER = REGISTER.register("refuelling_service_controller", () -> new WhiteoutAssemblyControllerBlock(BlockBehaviour.Properties.of().strength(5.0f, 10.0f).sound(SoundType.METAL).noOcclusion(), AssemblyKind.REFUELLING));
    }
}

