/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Harbinger.Spore.core.Sblocks
 *  com.Harbinger.Spore.core.Sitems
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.gametest.framework.GameTest
 *  net.minecraft.gametest.framework.GameTestHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.gametest.GameTestHolder
 *  net.neoforged.neoforge.gametest.PrefixGameTestTemplate
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.elvoux.sporecannons.gametest;

import com.Harbinger.Spore.core.Sblocks;
import com.Harbinger.Spore.core.Sitems;
import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.facility.FacilityStorageBlockEntity;
import com.elvoux.sporecannons.facility.MultiblockStorageBlock;
import com.elvoux.sporecannons.facility.MultiblockStorageBlockEntity;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlock;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlockEntity;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlockEntity;
import com.elvoux.sporecannons.munition.CDUCoolantCartridgeItem;
import com.elvoux.sporecannons.munition.CDURefuelingStationBlockEntity;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.elvoux.sporecannons.munition.WhiteoutKineticScaling;
import com.elvoux.sporecannons.registry.SporeCannonsBlocks;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import com.elvoux.sporecannons.registry.WhiteoutFacilityBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

@GameTestHolder(value="sporecannons")
@PrefixGameTestTemplate(value=false)
public final class WhiteoutCompatibilityGameTests {
    private static final String EMPTY = "whiteout_containment_post_empty";

    @GameTest(template="whiteout_containment_post_empty")
    public static void standaloneMachinesRetainLegacyTopology(GameTestHelper helper) {
        BlockPos projectorPos = new BlockPos(1, 1, 1);
        BlockPos stationPos = new BlockPos(3, 1, 1);
        helper.setBlock(projectorPos, (Block)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get());
        helper.setBlock(stationPos, (Block)SporeCannonsBlocks.CDU_REFUELING_STATION.get());
        helper.assertTrue(helper.getBlockEntity(projectorPos) instanceof CryogenicContainmentProjectorBlockEntity, "Standalone Projector must still create its original block entity");
        helper.assertTrue(helper.getBlockEntity(stationPos) instanceof CDURefuelingStationBlockEntity, "Standalone Station must still create its original block entity");
        CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)helper.getBlockEntity(projectorPos);
        CDURefuelingStationBlockEntity station = (CDURefuelingStationBlockEntity)helper.getBlockEntity(stationPos);
        helper.assertTrue(projector.inventory().getSlots() == 1, "Projector cartridge topology changed");
        helper.assertTrue(station.inventory().getSlots() == 2, "Station input topology changed");
        helper.assertTrue(Math.round(WhiteoutKineticScaling.projectorRadius(256.0f)) == 64L, "Legacy 256 RPM Projector radius must remain 64 blocks");
        helper.succeed();
    }

    @GameTest(template="whiteout_containment_post_empty")
    public static void industryMachinesExposeFilteredThreeSlotInventories(GameTestHelper helper) {
        BlockPos cultivatorPos = new BlockPos(1, 1, 1);
        BlockPos processorPos = new BlockPos(3, 1, 1);
        helper.setBlock(cultivatorPos, (Block)SporeCannonsBlocks.BIOMASS_CULTIVATOR.get());
        helper.setBlock(processorPos, (Block)SporeCannonsBlocks.CRYOGENIC_BIOMASS_PROCESSOR.get());
        helper.assertTrue(helper.getBlockEntity(cultivatorPos) instanceof WhiteoutIndustryMachineBlockEntity, "Cultivator block entity missing");
        helper.assertTrue(helper.getBlockEntity(processorPos) instanceof WhiteoutIndustryMachineBlockEntity, "Cryogenic Processor block entity missing");
        WhiteoutIndustryMachineBlockEntity cultivator = (WhiteoutIndustryMachineBlockEntity)helper.getBlockEntity(cultivatorPos);
        WhiteoutIndustryMachineBlockEntity processor = (WhiteoutIndustryMachineBlockEntity)helper.getBlockEntity(processorPos);
        helper.assertTrue(cultivator.inventory().getSlots() == 3 && processor.inventory().getSlots() == 3, "Industry inventory topology changed");
        helper.succeed();
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=360)
    public static void cryogenicProcessorUsesCartridgeChargeForBothModes(GameTestHelper helper) {
        BlockPos processorPos = new BlockPos(2, 1, 2);
        helper.setBlock(processorPos, (Block)SporeCannonsBlocks.CRYOGENIC_BIOMASS_PROCESSOR.get());
        helper.onEachTick(() -> {
            BlockEntity patt0$temp = helper.getBlockEntity(processorPos);
            if (patt0$temp instanceof WhiteoutIndustryMachineBlockEntity) {
                WhiteoutIndustryMachineBlockEntity processor = (WhiteoutIndustryMachineBlockEntity)patt0$temp;
                processor.setSpeed(256.0f);
            }
        });
        helper.runAfterDelay(10L, () -> {
            WhiteoutIndustryMachineBlockEntity processor = (WhiteoutIndustryMachineBlockEntity)helper.getBlockEntity(processorPos);
            helper.assertTrue(processor.inventory().isItemValid(1, SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack()), "Cryogenic Processor resource slot rejected a charged coolant cartridge");
            helper.assertTrue(!processor.inventory().isItemValid(1, Sitems.ICE_CANISTER.toStack()), "Cryogenic Processor still accepted an early Spore Ice Canister");
            helper.assertTrue(!processor.inventory().isItemValid(1, SporeCannonsItems.STABILIZED_CRYOGEL.toStack()), "Cryogenic Processor still accepted stabilized cryogel as a per-cycle fuel");
            ItemStack cartridge = SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack();
            CDUCoolantCartridgeItem.setRemaining(cartridge, (Integer)FreezeConfig.PROCESSOR_BLUE_ICE_COOLANT.get() + (Integer)FreezeConfig.PROCESSOR_BIOMASS_COOLANT.get());
            processor.inventory().setStackInSlot(1, cartridge);
            processor.fluidHandlerForSide(Direction.NORTH).fill(new FluidStack((Fluid)Fluids.WATER, 4000), IFluidHandler.FluidAction.EXECUTE);
        });
        helper.runAfterDelay(185L, () -> {
            WhiteoutIndustryMachineBlockEntity processor = (WhiteoutIndustryMachineBlockEntity)helper.getBlockEntity(processorPos);
            helper.assertTrue(processor.inventory().getStackInSlot(2).is(Blocks.BLUE_ICE.asItem()), "Deep Freezing did not produce Blue Ice from water and cryogenic charge");
            helper.assertTrue(processor.coolantRemaining() == ((Integer)FreezeConfig.PROCESSOR_BIOMASS_COOLANT.get()).intValue(), "Blue Ice did not consume the configured cartridge coolant amount");
            ItemStack partial = processor.inventory().extractItem(1, 1, false);
            helper.assertTrue(CDUCoolantCartridgeItem.remaining(partial) == (Integer)FreezeConfig.PROCESSOR_BIOMASS_COOLANT.get(), "A manually removed cartridge did not preserve its partial charge");
            processor.inventory().setStackInSlot(1, partial);
            processor.inventory().setStackInSlot(2, ItemStack.EMPTY);
            processor.inventory().setStackInSlot(0, Sitems.BIOMASS.toStack());
        });
        helper.runAfterDelay(285L, () -> {
            WhiteoutIndustryMachineBlockEntity processor = (WhiteoutIndustryMachineBlockEntity)helper.getBlockEntity(processorPos);
            helper.assertTrue(processor.inventory().getStackInSlot(2).is(Sblocks.FROST_BURNED_BIOMASS.asItem()), "Biomass Processing did not produce freezerburned biomass");
            helper.assertTrue(processor.coolantRemaining() == 0, "Biomass Processing did not consume the remaining cartridge coolant");
            helper.assertTrue(processor.inventory().getStackInSlot(1).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()), "Exhausted processor cartridge did not become a depleted cartridge");
            helper.assertTrue(processor.itemHandlerForSide(Direction.NORTH).extractItem(1, 1, true).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()), "Side automation could not extract the depleted cartridge");
            CompoundTag saved = processor.saveWithoutMetadata((HolderLookup.Provider)helper.getLevel().registryAccess());
            helper.assertTrue(saved.contains("Inventory"), "Processor cartridge inventory was not persisted for server reloads");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=140)
    public static void cryogenicProcessorRetainsSnowballSeed(GameTestHelper helper) {
        BlockPos processorPos = new BlockPos(2, 1, 2);
        helper.setBlock(processorPos, (Block)SporeCannonsBlocks.CRYOGENIC_BIOMASS_PROCESSOR.get());
        helper.onEachTick(() -> {
            BlockEntity patt0$temp = helper.getBlockEntity(processorPos);
            if (patt0$temp instanceof WhiteoutIndustryMachineBlockEntity) {
                WhiteoutIndustryMachineBlockEntity processor = (WhiteoutIndustryMachineBlockEntity)patt0$temp;
                processor.setSpeed(256.0f);
            }
        });
        helper.runAfterDelay(10L, () -> {
            WhiteoutIndustryMachineBlockEntity processor = (WhiteoutIndustryMachineBlockEntity)helper.getBlockEntity(processorPos);
            ItemStack cartridge = SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack();
            CDUCoolantCartridgeItem.setRemaining(cartridge, (Integer)FreezeConfig.PROCESSOR_SNOW_COOLANT.get());
            processor.inventory().setStackInSlot(1, cartridge);
            processor.fluidHandlerForSide(Direction.NORTH).fill(new FluidStack((Fluid)Fluids.WATER, ((Integer)FreezeConfig.PROCESSOR_SNOW_WATER.get()).intValue()), IFluidHandler.FluidAction.EXECUTE);
            ItemStack remainder = processor.itemHandlerForSide(Direction.NORTH).insertItem(0, new ItemStack((ItemLike)Items.SNOWBALL), false);
            helper.assertTrue(remainder.isEmpty() && processor.snowProductionMode(), "Side automation could not select Snow Production with a Snowball catalyst");
        });
        helper.runAfterDelay(90L, () -> {
            WhiteoutIndustryMachineBlockEntity processor = (WhiteoutIndustryMachineBlockEntity)helper.getBlockEntity(processorPos);
            ItemStack seed = processor.inventory().getStackInSlot(0);
            ItemStack output = processor.inventory().getStackInSlot(2);
            helper.assertTrue(seed.is(Items.SNOWBALL) && seed.getCount() == 1, "Snow Production consumed its crystallisation seed");
            helper.assertTrue(output.is(Items.SNOWBALL) && output.getCount() == ((Integer)FreezeConfig.PROCESSOR_SNOW_OUTPUT.get()).intValue(), "Snow Production did not create the configured Snowball batch");
            helper.assertTrue(processor.waterAmount() == 0, "Snow Production did not consume the configured water amount");
            helper.assertTrue(processor.inventory().getStackInSlot(1).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()), "Snow Production did not consume the configured cartridge charge");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=100)
    public static void projectorAssemblyHotSwapsAndDismantlesSafely(GameTestHelper helper) {
        BlockPos corePos = new BlockPos(2, 1, 2);
        BlockPos controllerPos = new BlockPos(2, 1, 1);
        BlockPos[] frame = new BlockPos[]{new BlockPos(1, 1, 1), new BlockPos(3, 1, 1), new BlockPos(1, 1, 2), new BlockPos(3, 1, 2)};
        helper.setBlock(corePos, (Block)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get());
        helper.setBlock(controllerPos, (Block)SporeCannonsBlocks.PROJECTOR_SERVICE_CONTROLLER.get());
        for (BlockPos pos : frame) {
            helper.setBlock(pos, (Block)WhiteoutFacilityBlocks.TECHNICAL_WALL_PANEL.get());
        }
        helper.runAfterDelay(10L, () -> {
            CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)helper.getBlockEntity(corePos);
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            projector.inventory().setStackInSlot(0, new ItemStack((ItemLike)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()));
            controller.inventory().setStackInSlot(0, new ItemStack((ItemLike)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()));
        });
        helper.runAfterDelay(50L, () -> {
            CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)helper.getBlockEntity(corePos);
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            helper.assertTrue(controller.formed(), "Projector service frame did not form");
            helper.assertTrue(projector.inventory().getStackInSlot(0).is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()), "Formed assembly did not hot-swap through its normal ticker: " + controller.linkedCoreDebug() + ", controllerIn=" + String.valueOf(controller.inventory().getStackInSlot(0)) + ", core=" + String.valueOf(projector.inventory().getStackInSlot(0)));
            helper.assertTrue(controller.inventory().getStackInSlot(6).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()), "Depleted cartridge was not preserved in the output magazine");
            helper.setBlock(frame[0], Blocks.AIR);
        });
        helper.runAfterDelay(80L, () -> {
            CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)helper.getBlockEntity(corePos);
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            helper.assertTrue(!controller.formed(), "Assembly stayed formed after its frame was dismantled");
            helper.assertTrue(projector.inventory().getStackInSlot(0).is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()), "Dismantling the service frame damaged the standalone Projector inventory");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=80)
    public static void refuellingAssemblyFeedsUnmodifiedStationInputs(GameTestHelper helper) {
        BlockPos stationPos = new BlockPos(2, 1, 2);
        BlockPos controllerPos = new BlockPos(2, 1, 1);
        helper.setBlock(stationPos, (Block)SporeCannonsBlocks.CDU_REFUELING_STATION.get());
        helper.setBlock(controllerPos, (Block)SporeCannonsBlocks.REFUELLING_SERVICE_CONTROLLER.get());
        for (BlockPos pos : new BlockPos[]{new BlockPos(1, 1, 1), new BlockPos(3, 1, 1), new BlockPos(1, 1, 2), new BlockPos(3, 1, 2)}) {
            helper.setBlock(pos, (Block)WhiteoutFacilityBlocks.TECHNICAL_WALL_PANEL.get());
        }
        helper.runAfterDelay(10L, () -> {
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            controller.inventory().setStackInSlot(0, new ItemStack((ItemLike)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()));
            controller.inventory().setStackInSlot(6, new ItemStack((ItemLike)SporeCannonsItems.STABILIZED_CRYOGEL.get()));
            controller.fluidHandlerForSide(null).fill(new FluidStack((Fluid)Fluids.WATER, 2000), IFluidHandler.FluidAction.EXECUTE);
        });
        helper.runAfterDelay(55L, () -> {
            CDURefuelingStationBlockEntity station = (CDURefuelingStationBlockEntity)helper.getBlockEntity(stationPos);
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            helper.assertTrue(controller.formed(), "Refuelling service frame did not form");
            helper.assertTrue(station.inventory().getStackInSlot(1).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()), "Controller did not feed Station through its normal ticker: " + controller.linkedCoreDebug() + ", controllerIn=" + String.valueOf(controller.inventory().getStackInSlot(0)) + ", station=" + String.valueOf(station.inventory().getStackInSlot(1)));
            helper.assertTrue(station.inventory().getStackInSlot(0).is((Item)SporeCannonsItems.STABILIZED_CRYOGEL.get()), "Controller did not feed the Station cryogel input");
            helper.assertTrue(station.waterAmount() > 0, "Controller did not feed the Station water tank");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=140)
    public static void storageCubeFormsBreaksAndReformsWithoutPhantomMembers(GameTestHelper helper) {
        BlockPos origin = new BlockPos(1, 1, 1);
        BlockPos removed = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 2; ++x) {
            for (int y = 1; y <= 2; ++y) {
                for (int z = 1; z <= 2; ++z) {
                    helper.setBlock(new BlockPos(x, y, z), (Block)WhiteoutFacilityBlocks.SAMPLE_CONTAINMENT_CHAMBER.get());
                }
            }
        }
        helper.runAfterDelay(20L, () -> {
            MultiblockStorageBlockEntity storage = (MultiblockStorageBlockEntity)helper.getBlockEntity(origin);
            helper.assertTrue(storage.structureSize() == 2, "Complete sample cube did not form as 2x2x2");
            helper.assertTrue(storage.members().size() == 8, "Formed sample cube did not expose all eight modules");
            helper.assertTrue(storage.automationHandler().getSlots() == 8 * MultiblockStorageBlock.StorageKind.SAMPLE.slotsPerModule(), "Formed sample cube exposed the wrong automation capacity");
            helper.setBlock(removed, Blocks.AIR);
        });
        helper.runAfterDelay(55L, () -> {
            MultiblockStorageBlockEntity storage = (MultiblockStorageBlockEntity)helper.getBlockEntity(origin);
            helper.assertTrue(storage.structureSize() == 1, "Broken sample cube kept a formed blockstate");
            helper.assertTrue(storage.members().size() == 1, "Incomplete sample cluster leaked neighbouring shards into one phantom inventory");
            helper.setBlock(removed, (Block)WhiteoutFacilityBlocks.SAMPLE_CONTAINMENT_CHAMBER.get());
        });
        helper.runAfterDelay(95L, () -> {
            MultiblockStorageBlockEntity storage = (MultiblockStorageBlockEntity)helper.getBlockEntity(origin);
            helper.assertTrue(storage.structureSize() == 2 && storage.members().size() == 8, "Sample cube did not reform after its final module was replaced");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=90)
    public static void assemblyRequiresOrientedTaggedFrame(GameTestHelper helper) {
        BlockPos controllerPos = new BlockPos(3, 1, 2);
        BlockPos corePos = new BlockPos(2, 1, 2);
        helper.setBlock(controllerPos, (BlockState)((WhiteoutAssemblyControllerBlock)((Object)SporeCannonsBlocks.PROJECTOR_SERVICE_CONTROLLER.get())).defaultBlockState().setValue((Property)WhiteoutAssemblyControllerBlock.FACING, (Comparable)Direction.EAST));
        helper.setBlock(corePos, (Block)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get());
        for (BlockPos pos : new BlockPos[]{new BlockPos(3, 1, 1), new BlockPos(3, 1, 3), new BlockPos(2, 1, 1), new BlockPos(2, 1, 3)}) {
            helper.setBlock(pos, (Block)WhiteoutFacilityBlocks.TECHNICAL_WALL_PANEL.get());
        }
        helper.runAfterDelay(35L, () -> {
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            helper.assertTrue(controller.formed(), "Rotated exact Projector frame did not form");
            helper.setBlock(new BlockPos(2, 1, 3), (Block)WhiteoutFacilityBlocks.LABORATORY_WALL.get());
        });
        helper.runAfterDelay(70L, () -> {
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            helper.assertTrue(!controller.formed(), "Assembly accepted an arbitrary facility block outside its casing tag");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=120)
    public static void assemblyPersistenceRevalidatesDerivedLink(GameTestHelper helper) {
        BlockPos stationPos = new BlockPos(2, 1, 2);
        BlockPos controllerPos = new BlockPos(2, 1, 1);
        helper.setBlock(stationPos, (Block)SporeCannonsBlocks.CDU_REFUELING_STATION.get());
        helper.setBlock(controllerPos, (Block)SporeCannonsBlocks.REFUELLING_SERVICE_CONTROLLER.get());
        for (BlockPos pos : new BlockPos[]{new BlockPos(1, 1, 1), new BlockPos(3, 1, 1), new BlockPos(1, 1, 2), new BlockPos(3, 1, 2)}) {
            helper.setBlock(pos, (Block)WhiteoutFacilityBlocks.STRUCTURAL_PANEL.get());
        }
        helper.runAfterDelay(30L, () -> {
            WhiteoutAssemblyControllerBlockEntity controller = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            controller.inventory().setStackInSlot(0, new ItemStack((ItemLike)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()));
            controller.fluidHandlerForSide(null).fill(new FluidStack((Fluid)Fluids.WATER, 3000), IFluidHandler.FluidAction.EXECUTE);
            CompoundTag saved = controller.saveWithoutMetadata((HolderLookup.Provider)helper.getLevel().registryAccess());
            helper.assertTrue(!saved.contains("Formed") && !saved.contains("LinkedCore"), "Assembly persisted a derived link that can become stale after reload");
            helper.setBlock(controllerPos, Blocks.AIR);
            helper.setBlock(controllerPos, (Block)SporeCannonsBlocks.REFUELLING_SERVICE_CONTROLLER.get());
            WhiteoutAssemblyControllerBlockEntity restored = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            restored.loadCustomOnly(saved, (HolderLookup.Provider)helper.getLevel().registryAccess());
        });
        helper.runAfterDelay(75L, () -> {
            WhiteoutAssemblyControllerBlockEntity restored = (WhiteoutAssemblyControllerBlockEntity)helper.getBlockEntity(controllerPos);
            CDURefuelingStationBlockEntity station = (CDURefuelingStationBlockEntity)helper.getBlockEntity(stationPos);
            helper.assertTrue(restored.formed(), "Reloaded assembly did not rediscover its Station core");
            helper.assertTrue(restored.waterAmount() + station.waterAmount() == 3000, "Reloaded assembly lost water while servicing its core");
            boolean cartridgePreserved = restored.inventory().getStackInSlot(0).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()) || station.inventory().getStackInSlot(1).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get());
            helper.assertTrue(cartridgePreserved, "Reloaded assembly lost its queued cartridge while servicing its core");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=90)
    public static void formedStorageExposesSharedInventoryOnEveryFace(GameTestHelper helper) {
        BlockPos origin = new BlockPos(1, 1, 1);
        for (int x = 1; x <= 2; ++x) {
            for (int y = 1; y <= 2; ++y) {
                for (int z = 1; z <= 2; ++z) {
                    helper.setBlock(new BlockPos(x, y, z), (Block)WhiteoutFacilityBlocks.CRYOGENIC_RESERVOIR.get());
                }
            }
        }
        helper.runAfterDelay(35L, () -> {
            for (int x = 1; x <= 2; ++x) {
                for (int y = 1; y <= 2; ++y) {
                    for (int z = 1; z <= 2; ++z) {
                        BlockPos member = new BlockPos(x, y, z);
                        for (Direction side : Direction.values()) {
                            IItemHandler handler = (IItemHandler)helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(member), (Object)side);
                            helper.assertTrue(handler != null && handler.getSlots() == 72, "A formed reservoir face did not expose the shared 8-module inventory: " + String.valueOf(member) + " / " + String.valueOf(side));
                        }
                    }
                }
            }
            MultiblockStorageBlockEntity storage = (MultiblockStorageBlockEntity)helper.getBlockEntity(origin);
            ItemStack cryogel = new ItemStack((ItemLike)SporeCannonsItems.STABILIZED_CRYOGEL.get());
            ItemStack remainder = storage.automationHandler().insertItem(0, cryogel, false);
            helper.assertTrue(remainder.isEmpty(), "Shared reservoir capability rejected valid cryogel input");
            helper.assertTrue(storage.automationHandler().extractItem(0, 1, false).is((Item)SporeCannonsItems.STABILIZED_CRYOGEL.get()), "Shared reservoir capability did not return inserted cryogel");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=60)
    public static void facilityStorageResolvesStructureLootTableLazily(GameTestHelper helper) {
        BlockPos cabinetPos = new BlockPos(1, 1, 1);
        helper.setBlock(cabinetPos, (Block)WhiteoutFacilityBlocks.STORAGE_CABINET.get());
        helper.runAfterDelay(10L, () -> {
            FacilityStorageBlockEntity cabinet = (FacilityStorageBlockEntity)helper.getBlockEntity(cabinetPos);
            CompoundTag saved = new CompoundTag();
            saved.putString("LootTable", "sporecannons:chests/regional_station_personnel_document");
            cabinet.loadCustomOnly(saved, (HolderLookup.Provider)helper.getLevel().registryAccess());
            helper.assertTrue(cabinet.inventory().getStackInSlot(0).is((Item)SporeCannonsItems.REGIONAL_STATION_DOCUMENTS.get("regional_station_personnel_notice").get()), "Facility storage did not resolve its delayed structure loot table");
            CompoundTag resolved = cabinet.saveWithoutMetadata((HolderLookup.Provider)helper.getLevel().registryAccess());
            helper.assertTrue(!resolved.contains("LootTable") && resolved.contains("Inventory"), "Resolved facility loot table was not replaced by persistent inventory data");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty", timeoutTicks=60)
    public static void multiblockStorageResolvesStructureLootTablesLazily(GameTestHelper helper) {
        BlockPos reservoirPos = new BlockPos(1, 1, 1);
        BlockPos chamberPos = new BlockPos(3, 1, 1);
        helper.setBlock(reservoirPos, (Block)WhiteoutFacilityBlocks.CRYOGENIC_RESERVOIR.get());
        helper.setBlock(chamberPos, (Block)WhiteoutFacilityBlocks.SAMPLE_CONTAINMENT_CHAMBER.get());
        helper.runAfterDelay(10L, () -> {
            MultiblockStorageBlockEntity reservoir = (MultiblockStorageBlockEntity)helper.getBlockEntity(reservoirPos);
            CompoundTag reservoirData = new CompoundTag();
            reservoirData.putString("LootTable", "sporecannons:chests/regional_station_cryogel_reservoir");
            reservoir.loadCustomOnly(reservoirData, (HolderLookup.Provider)helper.getLevel().registryAccess());
            helper.assertTrue(reservoir.inventory().getStackInSlot(0).is((Item)SporeCannonsItems.STABILIZED_CRYOGEL.get()), "Regional Station reservoir did not resolve its delayed cryogel loot");
            MultiblockStorageBlockEntity chamber = (MultiblockStorageBlockEntity)helper.getBlockEntity(chamberPos);
            CompoundTag chamberData = new CompoundTag();
            chamberData.putString("LootTable", "sporecannons:chests/regional_station_sample_chamber");
            chamber.loadCustomOnly(chamberData, (HolderLookup.Provider)helper.getLevel().registryAccess());
            helper.assertTrue(!chamber.inventory().getStackInSlot(0).isEmpty(), "Regional Station sample chamber did not resolve its delayed organ loot");
            CompoundTag resolved = chamber.saveWithoutMetadata((HolderLookup.Provider)helper.getLevel().registryAccess());
            helper.assertTrue(!resolved.contains("LootTable") && resolved.contains("Inventory"), "Resolved multiblock loot was not replaced by persistent inventory data");
            helper.succeed();
        });
    }

    @GameTest(template="whiteout_containment_post_empty")
    public static void regionalStationTemplateLoadsWithRotatableBounds(GameTestHelper helper) {
        StructureTemplate template = helper.getLevel().getStructureManager().getOrCreate(SporeCannons.id("regional_containment_station"));
        helper.assertTrue(template.getSize().equals((Object)new Vec3i(55, 37, 51)), "Regional Station template did not preserve its authored 55x37x51 bounds");
        helper.assertTrue(template.getSize(Rotation.CLOCKWISE_90).equals((Object)new Vec3i(51, 37, 55)), "Regional Station template did not expose correct rotated bounds");
        helper.succeed();
    }
}

