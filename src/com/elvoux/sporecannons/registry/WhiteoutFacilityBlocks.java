/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.TransparentBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.properties.BlockSetType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.registries.DeferredBlock
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.DeferredRegister$Blocks
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.facility.FacilityFacingBlock;
import com.elvoux.sporecannons.facility.FacilityInteractiveBlock;
import com.elvoux.sporecannons.facility.FacilityKind;
import com.elvoux.sporecannons.facility.FacilityMountedBlock;
import com.elvoux.sporecannons.facility.FacilityMountedToggleBlock;
import com.elvoux.sporecannons.facility.FacilityPipeBlock;
import com.elvoux.sporecannons.facility.FacilityThinBlock;
import com.elvoux.sporecannons.facility.FacilityToggleBlock;
import com.elvoux.sporecannons.facility.MultiblockStorageBlock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WhiteoutFacilityBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks((String)"sporecannons");
    public static final Map<String, DeferredBlock<? extends Block>> ALL = new LinkedHashMap<String, DeferredBlock<? extends Block>>();
    public static final Set<String> DOUBLE_HIGH_ITEMS = Set.of((Object)"laboratory_door");
    public static final DeferredBlock<Block> LABORATORY_WALL = WhiteoutFacilityBlocks.cube("laboratory_wall", WhiteoutFacilityBlocks.metal(3.0f));
    public static final DeferredBlock<Block> REINFORCED_WALL_PANEL = WhiteoutFacilityBlocks.cube("reinforced_wall_panel", WhiteoutFacilityBlocks.metal(5.0f));
    public static final DeferredBlock<Block> STRUCTURAL_PANEL = WhiteoutFacilityBlocks.cube("structural_panel", WhiteoutFacilityBlocks.metal(4.0f));
    public static final DeferredBlock<FacilityFacingBlock> TECHNICAL_WALL_PANEL = WhiteoutFacilityBlocks.facing("technical_wall_panel", WhiteoutFacilityBlocks.metal(3.5f));
    public static final DeferredBlock<FacilityFacingBlock> VENTILATION_BLOCK = WhiteoutFacilityBlocks.facing("ventilation_block", WhiteoutFacilityBlocks.metal(3.0f));
    public static final DeferredBlock<Block> CEILING_PANEL = WhiteoutFacilityBlocks.cube("ceiling_panel", WhiteoutFacilityBlocks.metal(3.0f));
    public static final DeferredBlock<Block> LABORATORY_FLOOR = WhiteoutFacilityBlocks.cube("laboratory_floor", WhiteoutFacilityBlocks.metal(4.0f));
    public static final DeferredBlock<Block> GRATED_FLOOR = WhiteoutFacilityBlocks.cube("grated_floor", WhiteoutFacilityBlocks.metal(4.0f));
    public static final DeferredBlock<Block> HAZARD_MARKED_FLOOR = WhiteoutFacilityBlocks.cube("hazard_marked_floor", WhiteoutFacilityBlocks.metal(4.0f));
    public static final DeferredBlock<TransparentBlock> LABORATORY_GLASS = WhiteoutFacilityBlocks.glass("laboratory_glass", 1.5f);
    public static final DeferredBlock<TransparentBlock> REINFORCED_LABORATORY_GLASS = WhiteoutFacilityBlocks.glass("reinforced_laboratory_glass", 3.0f);
    public static final DeferredBlock<TransparentBlock> FROSTED_LABORATORY_GLASS = WhiteoutFacilityBlocks.glass("frosted_laboratory_glass", 2.0f);
    public static final DeferredBlock<TransparentBlock> LABORATORY_GLASS_FRAME = WhiteoutFacilityBlocks.glass("laboratory_glass_frame", 3.5f);
    public static final DeferredBlock<FacilityThinBlock> GLASS_PARTITION = WhiteoutFacilityBlocks.register("glass_partition", () -> new FacilityThinBlock(WhiteoutFacilityBlocks.glassProperties(2.0f)));
    public static final DeferredBlock<MultiblockStorageBlock> SAMPLE_CONTAINMENT_CHAMBER = WhiteoutFacilityBlocks.register("sample_containment_chamber", () -> new MultiblockStorageBlock(WhiteoutFacilityBlocks.metal(4.0f).noOcclusion().lightLevel(state -> 5), MultiblockStorageBlock.StorageKind.SAMPLE));
    public static final DeferredBlock<MultiblockStorageBlock> CRYOGENIC_RESERVOIR = WhiteoutFacilityBlocks.register("cryogenic_reservoir", () -> new MultiblockStorageBlock(WhiteoutFacilityBlocks.metal(4.0f).noOcclusion().lightLevel(state -> 8), MultiblockStorageBlock.StorageKind.CRYOGEL));
    public static final DeferredBlock<FacilityPipeBlock> UTILITY_PIPE = WhiteoutFacilityBlocks.register("utility_pipe", () -> new FacilityPipeBlock(WhiteoutFacilityBlocks.metal(3.5f).noOcclusion()));
    public static final DeferredBlock<FacilityFacingBlock> COMMUNICATION_CABLE_BLOCK = WhiteoutFacilityBlocks.facing("communication_cable_block", WhiteoutFacilityBlocks.metal(3.0f));
    public static final DeferredBlock<FacilityInteractiveBlock> LABORATORY_WORKBENCH = WhiteoutFacilityBlocks.interactive("laboratory_workbench", WhiteoutFacilityBlocks.metal(3.5f).noOcclusion(), FacilityKind.WORKBENCH);
    public static final DeferredBlock<FacilityInteractiveBlock> STORAGE_CABINET = WhiteoutFacilityBlocks.interactive("storage_cabinet", WhiteoutFacilityBlocks.metal(3.5f), FacilityKind.STORAGE);
    public static final DeferredBlock<FacilityInteractiveBlock> TECHNICAL_EQUIPMENT_CABINET = WhiteoutFacilityBlocks.interactive("technical_equipment_cabinet", WhiteoutFacilityBlocks.metal(4.0f), FacilityKind.TECHNICAL);
    public static final DeferredBlock<FacilityInteractiveBlock> LABORATORY_TERMINAL = WhiteoutFacilityBlocks.interactive("laboratory_terminal", WhiteoutFacilityBlocks.metal(3.5f).noOcclusion().lightLevel(state -> (Boolean)state.getValue((Property)FacilityInteractiveBlock.LIT) != false ? 7 : 0), FacilityKind.TERMINAL);
    public static final DeferredBlock<DoorBlock> LABORATORY_DOOR = WhiteoutFacilityBlocks.register("laboratory_door", () -> new DoorBlock(BlockSetType.COPPER, WhiteoutFacilityBlocks.metal(5.0f).noOcclusion()));
    public static final DeferredBlock<FacilityMountedToggleBlock> WHITEOUT_WARNING_PANEL = WhiteoutFacilityBlocks.register("whiteout_warning_panel", () -> new FacilityMountedToggleBlock(WhiteoutFacilityBlocks.metal(3.0f).noOcclusion().lightLevel(state -> (Boolean)state.getValue((Property)FacilityToggleBlock.LIT) != false ? 8 : 0)));
    public static final DeferredBlock<FacilityMountedToggleBlock> INDUSTRIAL_LABORATORY_LIGHT = WhiteoutFacilityBlocks.register("industrial_laboratory_light", () -> new FacilityMountedToggleBlock(WhiteoutFacilityBlocks.metal(3.0f).noOcclusion().lightLevel(state -> (Boolean)state.getValue((Property)FacilityToggleBlock.LIT) != false ? 15 : 0)));
    public static final DeferredBlock<FacilityMountedBlock> WHITEOUT_FACILITY_SIGN = WhiteoutFacilityBlocks.register("whiteout_facility_sign", () -> new FacilityMountedBlock(WhiteoutFacilityBlocks.metal(3.0f).noOcclusion()));

    private static BlockBehaviour.Properties metal(float strength) {
        return BlockBehaviour.Properties.of().strength(strength, 8.0f).sound(SoundType.METAL).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties glassProperties(float strength) {
        return BlockBehaviour.Properties.of().strength(strength, 6.0f).sound(SoundType.GLASS).requiresCorrectToolForDrops().noOcclusion();
    }

    private static DeferredBlock<Block> cube(String id, BlockBehaviour.Properties properties) {
        return WhiteoutFacilityBlocks.register(id, () -> new Block(properties));
    }

    private static DeferredBlock<TransparentBlock> glass(String id, float strength) {
        return WhiteoutFacilityBlocks.register(id, () -> new TransparentBlock(WhiteoutFacilityBlocks.glassProperties(strength)));
    }

    private static DeferredBlock<FacilityFacingBlock> facing(String id, BlockBehaviour.Properties properties) {
        return WhiteoutFacilityBlocks.register(id, () -> new FacilityFacingBlock(properties));
    }

    private static DeferredBlock<FacilityInteractiveBlock> interactive(String id, BlockBehaviour.Properties properties, FacilityKind kind) {
        return WhiteoutFacilityBlocks.register(id, () -> new FacilityInteractiveBlock(properties, kind));
    }

    private static <T extends Block> DeferredBlock<T> register(String id, Supplier<T> supplier) {
        DeferredBlock block = REGISTER.register(id, supplier);
        ALL.put(id, (DeferredBlock<? extends Block>)block);
        return block;
    }

    private WhiteoutFacilityBlocks() {
    }
}

