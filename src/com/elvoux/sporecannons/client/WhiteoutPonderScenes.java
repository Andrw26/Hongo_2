/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Harbinger.Spore.core.Sblocks
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.content.fluids.pipes.FluidPipeBlock
 *  com.simibubi.create.content.fluids.tank.FluidTankBlock
 *  com.simibubi.create.content.fluids.tank.FluidTankBlockEntity
 *  com.simibubi.create.content.kinetics.deployer.DeployerBlock
 *  com.simibubi.create.content.kinetics.gearbox.GearboxBlock
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity$Phase
 *  com.simibubi.create.content.kinetics.press.MechanicalPressBlock
 *  com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock
 *  com.simibubi.create.content.logistics.depot.DepotBlock
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.Selection
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.PipeBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.fluids.FluidStack
 *  rbasamoyai.createbigcannons.index.CBCBlocks
 *  rbasamoyai.createbigcannons.index.CBCItems
 *  rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntity
 *  rbasamoyai.createbigcannons.munitions.big_cannon.propellant.PowderChargeBlock
 */
package com.elvoux.sporecannons.client;

import com.Harbinger.Spore.core.Sblocks;
import com.elvoux.sporecannons.facility.FacilityFacingBlock;
import com.elvoux.sporecannons.facility.MultiblockPart;
import com.elvoux.sporecannons.facility.MultiblockStorageBlock;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlock;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlock;
import com.elvoux.sporecannons.munition.CDURefuelingStationBlock;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlock;
import com.elvoux.sporecannons.munition.FreezeShellBlock;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsBlocks;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import com.elvoux.sporecannons.registry.WhiteoutFacilityBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.logistics.depot.DepotBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.PowderChargeBlock;

public final class WhiteoutPonderScenes {
    private WhiteoutPonderScenes() {
    }

    public static void biomassCycle(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("whiteout_biomass_cycle", "Industrial Biomass Cycle");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos chamber = util.grid().at(1, 1, 2);
        BlockPos cultivator = util.grid().at(2, 1, 2);
        BlockPos processor = util.grid().at(3, 1, 2);
        scene.world().setBlock(chamber, ((MultiblockStorageBlock)((Object)WhiteoutFacilityBlocks.SAMPLE_CONTAINMENT_CHAMBER.get())).defaultBlockState(), false);
        scene.world().setBlock(cultivator, ((WhiteoutIndustryMachineBlock)((Object)SporeCannonsBlocks.BIOMASS_CULTIVATOR.get())).defaultBlockState(), false);
        scene.world().setBlock(processor, ((WhiteoutIndustryMachineBlock)((Object)SporeCannonsBlocks.CRYOGENIC_BIOMASS_PROCESSOR.get())).defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(chamber, processor), Direction.DOWN);
        scene.overlay().showText(80).text("The Sample Chamber stores recovered Spore material. Only datapack-tagged common biomass enters cultivation.").pointAt(util.vector().topOf(chamber)).placeNearTarget().attachKeyFrame();
        scene.idle(90);
        scene.overlay().showControls(util.vector().topOf(cultivator), Pointing.DOWN, 55).withItem(Items.BONE_MEAL.getDefaultInstance()).rightClick();
        scene.overlay().showText(85).text("The Cultivator retains the original culture and spends nutrient, water and Create rotation to grow one matching item.").pointAt(util.vector().topOf(cultivator)).placeNearTarget().attachKeyFrame();
        scene.idle(95);
        scene.overlay().showControls(util.vector().topOf(processor), Pointing.DOWN, 55).withItem(SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack()).rightClick();
        scene.overlay().showText(85).text("A charged coolant cartridge powers sixty biomass cycles and keeps its remaining charge when removed.").pointAt(util.vector().topOf(processor)).placeNearTarget().attachKeyFrame();
        scene.idle(95);
        scene.overlay().showText(90).text("With no biomass input, supplied water enters Deep Freezing mode. One full cartridge can freeze fifteen Blue Ice blocks.").pointAt(util.vector().centerOf(processor)).placeNearTarget().attachKeyFrame();
        scene.idle(100);
        scene.overlay().showControls(util.vector().topOf(processor), Pointing.DOWN, 55).withItem(Items.SNOWBALL.getDefaultInstance()).rightClick();
        scene.overlay().showText(95).text("A Snowball in the Processing Input acts as a retained crystallisation seed. Snow Production turns water into four Snowballs at a much lower coolant cost.").pointAt(util.vector().centerOf(processor)).placeNearTarget().attachKeyFrame();
        scene.idle(105);
        scene.markAsFinished();
    }

    public static void assemblies(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("whiteout_assemblies", "WHITEOUT Service Assemblies");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos core = util.grid().at(2, 1, 2);
        BlockPos controller = util.grid().at(2, 1, 1);
        scene.world().setBlock(core, ((CryogenicContainmentProjectorBlock)((Object)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get())).defaultBlockState(), false);
        scene.world().setBlock(controller, ((WhiteoutAssemblyControllerBlock)((Object)SporeCannonsBlocks.PROJECTOR_SERVICE_CONTROLLER.get())).defaultBlockState(), false);
        for (BlockPos support : new BlockPos[]{util.grid().at(1, 1, 1), util.grid().at(3, 1, 1), util.grid().at(1, 1, 2), util.grid().at(3, 1, 2)}) {
            scene.world().setBlock(support, ((FacilityFacingBlock)((Object)WhiteoutFacilityBlocks.TECHNICAL_WALL_PANEL.get())).defaultBlockState(), false);
        }
        scene.world().showSection(util.select().fromTo(1, 1, 1, 3, 1, 2), Direction.DOWN);
        scene.overlay().showText(90).text("Face the controller outward with its core directly behind it. Tagged casings fill both side columns of the oriented 3x2 service frame.").pointAt(util.vector().topOf(controller)).placeNearTarget().attachKeyFrame();
        scene.idle(100);
        scene.overlay().showControls(util.vector().topOf(controller), Pointing.DOWN, 55).withItem(SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack()).rightClick();
        scene.overlay().showText(95).text("Projector infrastructure stores charged cartridges and performs a transactional hot-swap without dropping the field.").pointAt(util.vector().topOf(core)).placeNearTarget().attachKeyFrame();
        scene.idle(105);
        scene.world().setBlock(core, ((CDURefuelingStationBlock)((Object)SporeCannonsBlocks.CDU_REFUELING_STATION.get())).defaultBlockState(), true);
        scene.world().setBlock(controller, ((WhiteoutAssemblyControllerBlock)((Object)SporeCannonsBlocks.REFUELLING_SERVICE_CONTROLLER.get())).defaultBlockState(), true);
        scene.idle(20);
        scene.overlay().showText(95).text("The Refuelling controller instead queues depleted cartridges, water and cryogel around an unchanged Station core.").pointAt(util.vector().topOf(controller)).placeNearTarget().attachKeyFrame();
        scene.idle(105);
        scene.overlay().showText(90).text("Remove the controller or frame and the original Projector or Station immediately continues as a complete field unit.").pointAt(util.vector().topOf(core)).placeNearTarget().attachKeyFrame();
        scene.idle(100);
        scene.markAsFinished();
    }

    public static void sampleStorage(SceneBuilder scene, SceneBuildingUtil util) {
        WhiteoutPonderScenes.multiblockStorage(scene, util, true);
    }

    public static void cryogelStorage(SceneBuilder scene, SceneBuildingUtil util) {
        WhiteoutPonderScenes.multiblockStorage(scene, util, false);
    }

    private static void multiblockStorage(SceneBuilder scene, SceneBuildingUtil util, boolean samples) {
        int z;
        int y;
        int x;
        String id = samples ? "sample_storage" : "cryogel_storage";
        String title = samples ? "Modular Sample Containment" : "Modular Cryogel Reserve";
        MultiblockStorageBlock block = samples ? (MultiblockStorageBlock)((Object)WhiteoutFacilityBlocks.SAMPLE_CONTAINMENT_CHAMBER.get()) : (MultiblockStorageBlock)((Object)WhiteoutFacilityBlocks.CRYOGENIC_RESERVOIR.get());
        scene.title(id, title);
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos centre = util.grid().at(2, 1, 2);
        scene.world().setBlock(centre, block.defaultBlockState(), false);
        scene.world().showSection(util.select().position(centre), Direction.DOWN);
        scene.overlay().showControls(util.vector().topOf(centre), Pointing.DOWN, 40).withItem(block.asItem().getDefaultInstance()).rightClick();
        scene.overlay().showText(70).text(samples ? "A single chamber stores forty-eight original Spore item stacks." : "A single reservoir stores nine stacks of stabilized cryogel.").pointAt(util.vector().topOf(centre)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        for (x = 1; x <= 2; ++x) {
            for (y = 1; y <= 2; ++y) {
                for (z = 1; z <= 2; ++z) {
                    scene.world().setBlock(util.grid().at(x, y, z), WhiteoutPonderScenes.formedState(block.defaultBlockState(), 2, x == 1 ? MultiblockPart.MIN : MultiblockPart.MAX, y == 1 ? MultiblockPart.MIN : MultiblockPart.MAX, z == 1 ? MultiblockPart.MIN : MultiblockPart.MAX), false);
                }
            }
        }
        scene.world().showSection(util.select().fromTo(1, 1, 1, 2, 2, 2), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().fromTo(1, 1, 1, 2, 2, 2), 80).colored(PonderPalette.BLUE).text("Complete 2\u00d72\u00d72 cubes merge into one seamless storage structure and one interface.").placeNearTarget().attachKeyFrame();
        scene.idle(90);
        for (x = 1; x <= 3; ++x) {
            for (y = 1; y <= 3; ++y) {
                for (z = 1; z <= 3; ++z) {
                    scene.world().setBlock(util.grid().at(x, y, z), WhiteoutPonderScenes.formedState(block.defaultBlockState(), 3, WhiteoutPonderScenes.ponderPart(x), WhiteoutPonderScenes.ponderPart(y), WhiteoutPonderScenes.ponderPart(z)), false);
                }
            }
        }
        scene.world().showSection(util.select().fromTo(1, 1, 1, 3, 3, 3), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().fromTo(1, 1, 1, 3, 3, 3), 85).colored(PonderPalette.BLUE).text("A complete 3\u00d73\u00d73 cube is the maximum size. Use the scrollbar to reach every storage page.").placeNearTarget().attachKeyFrame();
        scene.idle(95);
        scene.overlay().showText(75).text("Every module face exposes the shared inventory to hoppers and Create logistics. Each module keeps its contents when dismantled.").pointAt(util.vector().centerOf(2, 2, 2)).placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.markAsFinished();
    }

    private static BlockState formedState(BlockState state, int size, MultiblockPart x, MultiblockPart y, MultiblockPart z) {
        return (BlockState)((BlockState)((BlockState)((BlockState)state.setValue((Property)MultiblockStorageBlock.SIZE, (Comparable)Integer.valueOf(size))).setValue(MultiblockStorageBlock.X_PART, (Comparable)((Object)x))).setValue(MultiblockStorageBlock.Y_PART, (Comparable)((Object)y))).setValue(MultiblockStorageBlock.Z_PART, (Comparable)((Object)z));
    }

    private static MultiblockPart ponderPart(int coordinate) {
        return coordinate == 1 ? MultiblockPart.MIN : (coordinate == 3 ? MultiblockPart.MAX : MultiblockPart.MIDDLE);
    }

    public static void scanner(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cdu_scanner", "Surveying an Outbreak");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos focus = util.grid().at(2, 1, 2);
        scene.world().setBlock(focus, ((Block)Sblocks.INFESTED_STONE.get()).defaultBlockState(), false);
        scene.world().setBlock(focus.east(), ((Block)Sblocks.BIOMASS_BLOCK.get()).defaultBlockState(), false);
        scene.world().setBlock(focus.north(), Blocks.MYCELIUM.defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(focus.north(), focus.east()), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showControls(util.vector().topOf(focus), Pointing.DOWN, 45).withItem(SporeCannonsItems.CDU_FIELD_SCANNER.toStack()).rightClick();
        scene.overlay().showText(70).text("Hold use for two seconds to scan loaded terrain.").pointAt(util.vector().topOf(focus)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.overlay().showOutlineWithText(util.select().fromTo(focus.north(), focus.east()), 70).colored(PonderPalette.RED).text("The report counts infection signals and parasites, then recommends a shell tier.").placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.overlay().showControls(util.vector().topOf(focus.west()), Pointing.DOWN, 45).withItem(SporeCannonsItems.CDU_FIELD_SCANNER.toStack()).whileSneaking().rightClick();
        scene.overlay().showText(70).text("Sneak and use to select a 32, 64 or 128 block survey radius.").pointAt(util.vector().topOf(focus.west())).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void sensorFuze(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cryogenic_sensor_fuze", "Selective Cryogenic Detonation");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos shell = util.grid().at(2, 1, 2);
        BlockPos signal = util.grid().at(3, 1, 2);
        scene.world().setBlock(shell, ((FreezeShellBlock)((Object)SporeCannonsBlocks.FREEZE_SHELL.get())).defaultBlockState(), false);
        scene.world().showSection(util.select().position(shell), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showControls(util.vector().topOf(shell), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_SENSOR_FUZE.toStack()).rightClick();
        scene.overlay().showText(70).text("Fit the sensor fuze to a CDU shell like any CBC nose fuze.").pointAt(util.vector().topOf(shell)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.world().setBlock(signal, ((Block)Sblocks.BIOMASS_BLOCK.get()).defaultBlockState(), false);
        scene.world().showSection(util.select().position(signal), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(signal), 75).colored(PonderPalette.RED).text("Choose Parasites, Infection or Hybrid mode. Players and ordinary mobs are ignored.").placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.overlay().showControls(util.vector().topOf(shell), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_SENSOR_FUZE.toStack()).whileSneaking().rightClick();
        scene.overlay().showText(70).text("Sneak and use to select a 2, 4 or 8 block trigger radius.").pointAt(util.vector().topOf(shell)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void mineFuze(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cryogenic_mine_fuze", "White Watch Ground Mine");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos shell = util.grid().at(2, 1, 2);
        BlockPos infection = util.grid().at(3, 1, 2);
        scene.world().setBlock(shell, (BlockState)((FreezeShellBlock)((Object)SporeCannonsBlocks.FREEZE_SHELL.get())).defaultBlockState().setValue((Property)BlockStateProperties.FACING, (Comparable)Direction.UP), false);
        scene.world().showSection(util.select().position(shell), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showControls(util.vector().topOf(shell), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_MINE_FUZE.toStack()).rightClick();
        scene.overlay().showText(70).text("Fit White Watch to a placed CDU shell. It arms after three seconds.").pointAt(util.vector().topOf(shell)).placeNearTarget().attachKeyFrame();
        scene.idle(75);
        scene.world().modifyBlockEntity(shell, FuzedBlockEntity.class, blockEntity -> blockEntity.setFuze(SporeCannonsItems.CRYOGENIC_MINE_FUZE.toStack()));
        scene.effects().indicateSuccess(shell);
        scene.world().setBlock(infection, ((Block)Sblocks.BIOMASS_BLOCK.get()).defaultBlockState(), false);
        scene.world().showSection(util.select().position(infection), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(infection), 75).colored(PonderPalette.RED).text("Parasites or infection trigger a two-second warning. Players and ordinary mobs do not.").placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.overlay().showControls(util.vector().topOf(shell), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_MINE_FUZE.toStack()).whileSneaking().rightClick();
        scene.overlay().showText(70).text("Choose Parasites, Infection or Containment mode and a 4, 8 or 12 block radius.").pointAt(util.vector().topOf(shell)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void bileCleanup(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("bile_cleanup", "Permafrost Decontamination");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos bile = util.grid().at(2, 1, 2);
        scene.world().setBlock(bile, ((LiquidBlock)Sblocks.BILE.get()).defaultBlockState(), false);
        scene.world().showSection(util.select().position(bile), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showControls(util.vector().topOf(bile), Pointing.DOWN, 45).withItem(SporeCannonsItems.FREEZE_SHELL.toStack()).leftClick();
        scene.overlay().showText(70).text("A CDU pulse solidifies exposed bile into a brittle crust.").pointAt(util.vector().topOf(bile)).placeNearTarget().attachKeyFrame();
        scene.idle(75);
        scene.world().setBlock(bile, ((Block)Sblocks.CRUSTED_BILE.get()).defaultBlockState(), true);
        scene.effects().indicateSuccess(bile);
        scene.overlay().showControls(util.vector().topOf(bile), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_AP_AUTOCANNON_ROUND.toStack()).leftClick();
        scene.overlay().showText(75).text("A second cold impact shatters hardened bile without leaving item drops.").pointAt(util.vector().topOf(bile)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.world().destroyBlock(bile);
        scene.effects().indicateSuccess(bile);
        scene.overlay().showText(75).text("Levels III\u2013VI can complete both stages near the center; the clearing radius grows by tier.").pointAt(util.vector().centerOf(bile)).placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.markAsFinished();
    }

    public static void saturation(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cryogenic_saturation", "Cryogenic Saturation and Brittleness");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos target = util.grid().at(2, 1, 2);
        scene.world().setBlock(target, ((Block)Sblocks.BIOMASS_BLOCK.get()).defaultBlockState(), false);
        scene.world().showSection(util.select().position(target), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showControls(util.vector().topOf(target.west()), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_MACHINE_GUN_ROUND.toStack()).leftClick();
        scene.overlay().showText(70).text("Cryogenic MG, AP, flak and large shells build one shared cold saturation.").pointAt(util.vector().topOf(target)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.overlay().showControls(util.vector().topOf(target.east()), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_AP_AUTOCANNON_ROUND.toStack()).leftClick();
        scene.world().setBlock(target, Blocks.PACKED_ICE.defaultBlockState(), true);
        scene.overlay().showOutlineWithText(util.select().position(target), 75).colored(PonderPalette.BLUE).text("At full saturation a parasite becomes brittle and takes increased non-freezing damage.").placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.world().destroyBlock(target);
        scene.effects().indicateSuccess(target);
        scene.overlay().showText(65).text("Killing a brittle parasite shatters its frozen shell.").pointAt(util.vector().centerOf(target)).placeNearTarget().attachKeyFrame();
        scene.idle(75);
        scene.markAsFinished();
    }

    public static void cryogenicManufacturing(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cryogenic_manufacturing", "Industrial Cryogenic Assembly");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos inputDepot = util.grid().at(1, 1, 2);
        BlockPos injectionDepot = util.grid().at(2, 1, 2);
        BlockPos sealingDepot = util.grid().at(3, 1, 2);
        BlockPos deployer = injectionDepot.above();
        BlockPos press = sealingDepot.above();
        scene.world().setBlock(inputDepot, ((DepotBlock)AllBlocks.DEPOT.get()).defaultBlockState(), false);
        scene.world().setBlock(injectionDepot, ((DepotBlock)AllBlocks.DEPOT.get()).defaultBlockState(), false);
        scene.world().setBlock(sealingDepot, ((DepotBlock)AllBlocks.DEPOT.get()).defaultBlockState(), false);
        scene.world().setBlock(deployer, (BlockState)((DeployerBlock)AllBlocks.DEPLOYER.get()).defaultBlockState().setValue((Property)BlockStateProperties.FACING, (Comparable)Direction.DOWN), false);
        scene.world().setBlock(press, ((MechanicalPressBlock)AllBlocks.MECHANICAL_PRESS.get()).defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(inputDepot, press), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(inputDepot), Pointing.DOWN, 45).withItem(CBCItems.MACHINE_GUN_ROUND.asStack()).rightClick();
        scene.overlay().showText(65).text("Feed ordinary CBC ammunition to a depot or belt. Each round is processed individually.").pointAt(util.vector().topOf(inputDepot)).placeNearTarget().attachKeyFrame();
        scene.idle(70);
        scene.overlay().showControls(util.vector().centerOf(deployer), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_MICROCELL.toStack()).rightClick();
        scene.overlay().showText(65).text("A Deployer injects one microcell. Flak repeats this stage for a second dose.").pointAt(util.vector().centerOf(injectionDepot)).placeNearTarget().attachKeyFrame();
        scene.idle(70);
        scene.effects().indicateSuccess(sealingDepot);
        scene.overlay().showControls(util.vector().topOf(sealingDepot), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_MACHINE_GUN_ROUND.toStack());
        scene.overlay().showText(70).text("The Mechanical Press seals the payload. Mechanical Arms can move every stage between depots.").pointAt(util.vector().topOf(sealingDepot)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void refuelingStation(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cdu_refueling_station", "Servicing a CDU Coolant Cartridge");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos station = util.grid().at(2, 1, 2);
        BlockPos stationShaft = station.below();
        scene.world().setBlock(stationShaft, (BlockState)((ShaftBlock)AllBlocks.SHAFT.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.Y), false);
        scene.world().setBlock(station, ((CDURefuelingStationBlock)((Object)SporeCannonsBlocks.CDU_REFUELING_STATION.get())).defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(stationShaft, station), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(station), Pointing.DOWN, 45).withItem(SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.toStack()).rightClick();
        scene.overlay().showText(65).text("Use the two-slot interface: cryogel above and the depleted cartridge in the lower interface slot. Supply water by bucket or through the front pipe port.").pointAt(util.vector().topOf(station)).placeNearTarget().attachKeyFrame();
        scene.idle(75);
        scene.overlay().showOutlineWithText(util.select().position(station), 70).colored(PonderPalette.BLUE).text("At 32 RPM charging takes eight seconds. Higher speed smoothly shortens the cycle, up to 3x at 256 RPM.").placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.effects().indicateSuccess(station);
        scene.overlay().showControls(util.vector().topOf(station), Pointing.DOWN, 45).withItem(SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack()).rightClick();
        scene.overlay().showText(65).text("Automation supplies cryogel from above and handles cartridges through the left or right service bay. The underside is reserved for the Create shaft.").pointAt(util.vector().topOf(station)).placeNearTarget().attachKeyFrame();
        scene.idle(75);
        scene.markAsFinished();
    }

    public static void containmentProjector(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cryogenic_containment_projector", "Sustained Cryogenic Containment");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos projector = util.grid().at(2, 1, 2);
        BlockPos projectorShaft = projector.below();
        BlockPos infection = util.grid().at(3, 1, 2);
        scene.world().setBlock(projectorShaft, (BlockState)((ShaftBlock)AllBlocks.SHAFT.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.Y), false);
        scene.world().setBlock(projector, ((CryogenicContainmentProjectorBlock)((Object)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get())).defaultBlockState(), false);
        scene.world().setBlock(infection, ((Block)Sblocks.BIOMASS_BLOCK.get()).defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(projectorShaft, infection), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(projector), Pointing.DOWN, 45).withItem(SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack()).rightClick();
        scene.overlay().showText(70).text("Insert a charged cartridge and supply at least 32 RPM to establish the field.").pointAt(util.vector().topOf(projector)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.world().setBlock(infection, Blocks.PACKED_ICE.defaultBlockState(), true);
        scene.effects().indicateSuccess(infection);
        scene.overlay().showOutlineWithText(util.select().fromTo(projector, infection), 75).colored(PonderPalette.BLUE).text("From 32 to 256 RPM its radius scales from 12 to 64 blocks. Maximum coverage demands 48 stress impact and consumes coolant at 4x rate.").placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.overlay().showText(75).text("One cartridge lasts fifteen active minutes. Stopped or overstressed machinery preserves its reserve.").pointAt(util.vector().topOf(projector)).placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.overlay().showText(70).text("Automation inserts charged cartridges from above and extracts depleted cartridges through any horizontal side.").pointAt(util.vector().centerOf(projector.east())).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(80).text("Wall Mode spans 1.5x the comparable radial diameter at three blocks deep. Height scales from 9 to 32 blocks and coolant cost is 25% higher.").pointAt(util.vector().topOf(projector)).placeNearTarget().attachKeyFrame();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void coolantAutomation(SceneBuilder scene, SceneBuildingUtil util) {
        CreateSceneBuilder create = new CreateSceneBuilder(scene);
        scene.title("coolant_automation", "Automating the Coolant Cartridge Cycle");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos waterTank = util.grid().at(0, 1, 1);
        BlockPos waterPipe = util.grid().at(0, 2, 1);
        BlockPos station = util.grid().at(1, 2, 1);
        BlockPos projector = util.grid().at(3, 2, 1);
        BlockPos stationShaft = station.below();
        BlockPos projectorShaft = projector.below();
        BlockPos stationDepot = util.grid().at(1, 1, 3);
        BlockPos arm = util.grid().at(2, 1, 3);
        BlockPos projectorDepot = util.grid().at(3, 1, 3);
        scene.world().setBlock(station, (BlockState)((BlockState)((CDURefuelingStationBlock)((Object)SporeCannonsBlocks.CDU_REFUELING_STATION.get())).defaultBlockState().setValue((Property)CDURefuelingStationBlock.FACING, (Comparable)Direction.WEST)).setValue((Property)CDURefuelingStationBlock.PIPE_CONNECTED, (Comparable)Boolean.valueOf(true)), false);
        scene.world().setBlock(projector, ((CryogenicContainmentProjectorBlock)((Object)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get())).defaultBlockState(), false);
        scene.world().setBlock(stationShaft, (BlockState)((ShaftBlock)AllBlocks.SHAFT.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.Y), false);
        scene.world().setBlock(projectorShaft, (BlockState)((ShaftBlock)AllBlocks.SHAFT.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.Y), false);
        scene.world().setBlock(waterTank, ((FluidTankBlock)AllBlocks.FLUID_TANK.get()).defaultBlockState(), false);
        scene.world().modifyBlockEntity(waterTank, FluidTankBlockEntity.class, tank -> tank.getTankInventory().setFluid(new FluidStack((Fluid)Fluids.WATER, 8000)));
        scene.world().setBlock(waterPipe, (BlockState)((BlockState)((FluidPipeBlock)AllBlocks.FLUID_PIPE.get()).defaultBlockState().setValue((Property)PipeBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN), (Comparable)Boolean.valueOf(true))).setValue((Property)PipeBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST), (Comparable)Boolean.valueOf(true)), false);
        scene.world().setBlock(stationDepot, ((DepotBlock)AllBlocks.DEPOT.get()).defaultBlockState(), false);
        scene.world().setBlock(arm, ((ArmBlock)AllBlocks.MECHANICAL_ARM.get()).defaultBlockState(), false);
        scene.world().setBlock(projectorDepot, ((DepotBlock)AllBlocks.DEPOT.get()).defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(waterTank, projector), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(stationDepot, projectorDepot), Direction.DOWN);
        create.world().setKineticSpeed(util.select().position(arm), 64.0f);
        scene.idle(20);
        scene.overlay().showControls(util.vector().centerOf(waterPipe), Pointing.RIGHT, 45).withItem(Items.WATER_BUCKET.getDefaultInstance());
        scene.overlay().showControls(util.vector().topOf(station), Pointing.DOWN, 45).withItem(SporeCannonsItems.STABILIZED_CRYOGEL.toStack());
        scene.overlay().showText(75).text("Feed water through the front fluid pipe and cryogel from above. Both machines need at least 32 RPM from below.").pointAt(util.vector().topOf(station)).placeNearTarget().attachKeyFrame();
        scene.idle(85);
        WhiteoutPonderScenes.configureArm(scene, util, arm, projectorDepot, stationDepot);
        create.world().createItemOnBeltLike(projectorDepot, Direction.UP, SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.toStack());
        scene.overlay().showText(75).text("A filtered Funnel, Belt or Mechanical Arm returns a depleted cartridge to a left or right Refuelling Station bay.").pointAt(util.vector().topOf(arm)).placeNearTarget().attachKeyFrame();
        create.world().instructArm(arm, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        create.world().removeItemsFromBelt(projectorDepot);
        create.world().instructArm(arm, ArmBlockEntity.Phase.SEARCH_OUTPUTS, SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.toStack(), -1);
        scene.idle(20);
        create.world().instructArm(arm, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.toStack(), 0);
        scene.idle(24);
        create.world().createItemOnBeltLike(stationDepot, Direction.UP, SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.toStack());
        create.world().instructArm(arm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.idle(25);
        create.world().removeItemsFromBelt(stationDepot);
        scene.effects().indicateSuccess(station);
        WhiteoutPonderScenes.configureArm(scene, util, arm, stationDepot, projectorDepot);
        create.world().createItemOnBeltLike(stationDepot, Direction.UP, SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack());
        scene.overlay().showText(75).text("When charging completes, the same side port exposes a charged cartridge. Route it into the top of the Projector.").pointAt(util.vector().topOf(arm)).placeNearTarget().attachKeyFrame();
        create.world().instructArm(arm, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        create.world().removeItemsFromBelt(stationDepot);
        create.world().instructArm(arm, ArmBlockEntity.Phase.SEARCH_OUTPUTS, SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack(), -1);
        scene.idle(20);
        create.world().instructArm(arm, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack(), 0);
        scene.idle(24);
        create.world().createItemOnBeltLike(projectorDepot, Direction.UP, SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack());
        create.world().instructArm(arm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.idle(25);
        create.world().removeItemsFromBelt(projectorDepot);
        scene.effects().indicateSuccess(projector);
        scene.overlay().showControls(util.vector().centerOf(projector.east()), Pointing.LEFT, 50).withItem(SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.toStack());
        scene.overlay().showText(85).text("When exhausted, the cartridge becomes available on any horizontal Projector side. Filter it back to the Station to close the loop.").pointAt(util.vector().centerOf(projector.east())).placeNearTarget().attachKeyFrame();
        scene.idle(95);
        scene.overlay().showControls(util.vector().topOf(arm), Pointing.DOWN, 55).withItem(AllBlocks.MECHANICAL_ARM.asStack());
        scene.overlay().showText(90).text("The machines only expose honest inputs and outputs. The layout is yours: use filters to keep charged and depleted cartridges on the correct routes.").pointAt(util.vector().centerOf(2, 2, 2)).placeNearTarget().attachKeyFrame();
        scene.idle(100);
        scene.markAsFinished();
    }

    private static void configureArm(SceneBuilder scene, SceneBuildingUtil util, BlockPos arm, BlockPos input, BlockPos output) {
        ListTag points = new ListTag();
        points.add((Object)WhiteoutPonderScenes.armPoint(arm, input, "TAKE"));
        points.add((Object)WhiteoutPonderScenes.armPoint(arm, output, "DEPOSIT"));
        scene.world().modifyBlockEntityNBT(util.select().position(arm), ArmBlockEntity.class, tag -> tag.put("InteractionPoints", (Tag)points));
    }

    private static CompoundTag armPoint(BlockPos arm, BlockPos point, String mode) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", "create:depot");
        tag.put("Pos", NbtUtils.writeBlockPos((BlockPos)point.subtract((Vec3i)arm)));
        tag.putString("Mode", mode);
        return tag;
    }

    public static void machineKinetics(SceneBuilder scene, SceneBuildingUtil util) {
        CreateSceneBuilder create = new CreateSceneBuilder(scene);
        scene.title("machine_kinetics", "Powering CDU Field Machinery");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos driveA = util.grid().at(0, 1, 2);
        BlockPos driveB = util.grid().at(1, 1, 2);
        BlockPos gearbox = util.grid().at(2, 1, 2);
        BlockPos verticalShaft = util.grid().at(2, 2, 2);
        BlockPos machine = util.grid().at(2, 3, 2);
        Selection drive = util.select().fromTo(driveA, verticalShaft);
        scene.world().setBlock(machine, ((CryogenicContainmentProjectorBlock)((Object)SporeCannonsBlocks.CRYOGENIC_CONTAINMENT_PROJECTOR.get())).defaultBlockState(), false);
        scene.world().showSection(util.select().position(machine), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().position(machine), 70).colored(PonderPalette.BLUE).text("The kinetic connector is underneath. The bottom face is reserved exclusively for a Create shaft.").pointAt(util.vector().centerOf(machine.below())).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.world().setBlock(verticalShaft, (BlockState)((ShaftBlock)AllBlocks.SHAFT.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.Y), false);
        scene.world().showSection(util.select().position(verticalShaft), Direction.UP);
        scene.overlay().showControls(util.vector().centerOf(verticalShaft), Pointing.DOWN, 45).withItem(AllBlocks.SHAFT.asStack()).rightClick();
        scene.overlay().showText(70).text("Place a vertical shaft directly below the machine. A gearbox can turn the base network upward into it.").pointAt(util.vector().centerOf(verticalShaft)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.world().setBlock(gearbox, (BlockState)((GearboxBlock)AllBlocks.GEARBOX.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.Z), false);
        scene.world().setBlock(driveA, (BlockState)((ShaftBlock)AllBlocks.SHAFT.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.X), false);
        scene.world().setBlock(driveB, (BlockState)((ShaftBlock)AllBlocks.SHAFT.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.X), false);
        scene.world().showSection(util.select().fromTo(driveA, gearbox), Direction.EAST);
        create.world().setKineticSpeed(drive, 16.0f);
        create.effects().rotationSpeedIndicator(verticalShaft);
        scene.overlay().showText(70).text("Below 32 RPM the machine remains on standby and performs no work.").pointAt(util.vector().centerOf(verticalShaft)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        create.world().setKineticSpeed(drive, 64.0f);
        create.effects().rotationSpeedIndicator(verticalShaft);
        create.effects().rotationDirectionIndicator(driveB);
        scene.effects().indicateSuccess(machine);
        scene.overlay().showText(85).text("At or above 32 RPM, more speed improves performance: the Station charges faster, while the Projector expands its field and consumes coolant faster.").pointAt(util.vector().topOf(machine)).placeNearTarget().attachKeyFrame();
        scene.idle(95);
        scene.markAsFinished();
    }

    public static void largeShellLoading(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("large_shell_loading", "Loading a Cryogenic Shell");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos shell = util.grid().at(2, 1, 2);
        scene.world().setBlock(shell, (BlockState)((FreezeShellBlock)((Object)SporeCannonsBlocks.FREEZE_SHELL.get())).defaultBlockState().setValue((Property)BlockStateProperties.FACING, (Comparable)Direction.UP), false);
        scene.world().showSection(util.select().position(shell), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.BLUE, (Object)"fuze_nose", util.select().position(shell.above()), 65);
        scene.overlay().showLine(PonderPalette.BLUE, util.vector().topOf(shell), util.vector().of((double)shell.getX() + 0.5, (double)shell.getY() + 2.0, (double)shell.getZ() + 0.5), 65);
        scene.overlay().showControls(util.vector().topOf(shell), Pointing.DOWN, 45).withItem(SporeCannonsItems.CRYOGENIC_SENSOR_FUZE.toStack()).rightClick();
        scene.overlay().showText(65).text("Place a compatible CBC fuze on the pointed nose of the shell.").pointAt(util.vector().topOf(shell)).placeNearTarget().attachKeyFrame();
        scene.idle(70);
        scene.world().modifyBlockEntity(shell, FuzedBlockEntity.class, blockEntity -> blockEntity.setFuze(CBCItems.IMPACT_FUZE.asStack()));
        scene.effects().indicateSuccess(shell.above());
        scene.overlay().showOutlineWithText(util.select().position(shell), 65).colored(PonderPalette.BLUE).text("The grey cap confirms that the fuze is installed and the shell is complete.").placeNearTarget().attachKeyFrame();
        scene.idle(70);
        scene.world().destroyBlock(shell);
        BlockPos chargeRear = util.grid().at(1, 1, 2);
        BlockPos chargeFront = util.grid().at(2, 1, 2);
        BlockPos loadedShell = util.grid().at(3, 1, 2);
        scene.world().setBlock(chargeRear, (BlockState)((PowderChargeBlock)CBCBlocks.POWDER_CHARGE.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.X), false);
        scene.world().setBlock(chargeFront, (BlockState)((PowderChargeBlock)CBCBlocks.POWDER_CHARGE.get()).defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.X), false);
        scene.world().setBlock(loadedShell, (BlockState)((FreezeShellBlock)((Object)SporeCannonsBlocks.FREEZE_SHELL.get())).defaultBlockState().setValue((Property)BlockStateProperties.FACING, (Comparable)Direction.EAST), false);
        scene.world().showSection(util.select().fromTo(chargeRear, loadedShell), Direction.DOWN);
        scene.world().modifyBlockEntity(loadedShell, FuzedBlockEntity.class, blockEntity -> blockEntity.setFuze(CBCItems.IMPACT_FUZE.asStack()));
        scene.overlay().showBigLine(PonderPalette.BLUE, util.vector().centerOf(chargeRear), util.vector().centerOf(loadedShell).add(1.0, 0.0, 0.0), 80);
        scene.overlay().showOutlineWithText(util.select().position(loadedShell), 80).colored(PonderPalette.BLUE).text("Load the shell nose-first toward the muzzle. Ram the powder charges behind it, toward the breech.").placeNearTarget().attachKeyFrame();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void arsenalTiers(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("arsenal_tiers", "CDU Payload Levels I\u2013VI");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos[] positions = new BlockPos[]{util.grid().at(1, 1, 1), util.grid().at(2, 1, 1), util.grid().at(3, 1, 1), util.grid().at(1, 1, 3), util.grid().at(2, 1, 3), util.grid().at(3, 1, 3)};
        FreezeShellTier[] tiers = FreezeShellTier.values();
        for (int i = 0; i < tiers.length; ++i) {
            scene.world().setBlock(positions[i], ((FreezeShellBlock)((Object)SporeCannonsBlocks.FREEZE_SHELLS.get((Object)tiers[i]).get())).defaultBlockState(), false);
            scene.world().showSection(util.select().position(positions[i]), Direction.DOWN);
            scene.idle(5);
        }
        scene.overlay().showOutlineWithText(util.select().fromTo(positions[0], positions[2]), 70).colored(PonderPalette.BLUE).text("Levels I\u2013III handle precision strikes, local outbreaks and hardened nests.").placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.overlay().showOutlineWithText(util.select().fromTo(positions[3], positions[4]), 70).colored(PonderPalette.RED).text("Levels IV\u2013V are fortification and strategic containment weapons.").placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.overlay().showOutlineWithText(util.select().position(positions[5]), 75).colored(PonderPalette.RED).text("Level VI is a battlefield-scale device. Clear the area and expect a long-lived Whiteout field.").placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.markAsFinished();
    }

    public static void containmentField(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("containment_field", "Residual Whiteout Containment");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos center = util.grid().at(2, 1, 2);
        BlockPos west = center.west();
        BlockPos east = center.east();
        scene.world().setBlock(west, ((Block)Sblocks.INFESTED_STONE.get()).defaultBlockState(), false);
        scene.world().setBlock(center, ((Block)Sblocks.BIOMASS_BLOCK.get()).defaultBlockState(), false);
        scene.world().setBlock(east, Blocks.MYCELIUM.defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(west, east), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showControls(util.vector().topOf(center), Pointing.DOWN, 45).withItem(SporeCannonsItems.FREEZE_SHELLS.get((Object)FreezeShellTier.III).toStack()).leftClick();
        scene.overlay().showText(70).text("Every large CDU strike establishes a residual containment field.").pointAt(util.vector().topOf(center)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.world().setBlock(west, Blocks.STONE.defaultBlockState(), true);
        scene.world().setBlock(center, Blocks.PACKED_ICE.defaultBlockState(), true);
        scene.world().setBlock(east, Blocks.DIRT.defaultBlockState(), true);
        scene.effects().indicateSuccess(center);
        scene.overlay().showOutlineWithText(util.select().fromTo(west, east), 75).colored(PonderPalette.BLUE).text("The field suppresses nearby parasites and removes infection that tries to regrow.").placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.overlay().showText(75).text("Fields last from 10 seconds at Level I to 20 minutes at Level VI and never force-load chunks.").pointAt(util.vector().topOf(center)).placeNearTarget().attachKeyFrame();
        scene.idle(85);
        scene.markAsFinished();
    }
}

