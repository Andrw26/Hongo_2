/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Harbinger.Spore.core.Sblocks
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  java.lang.MatchException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.Containers
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.elvoux.sporecannons.industry;

import com.Harbinger.Spore.core.Sblocks;
import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.industry.IndustryMachineKind;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlock;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineMenu;
import com.elvoux.sporecannons.munition.CDUCoolantCartridgeItem;
import com.elvoux.sporecannons.munition.WhiteoutKineticScaling;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public final class WhiteoutIndustryMachineBlockEntity
extends KineticBlockEntity
implements MenuProvider {
    public static final int INPUT_SLOT = 0;
    public static final int RESOURCE_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int TANK_CAPACITY = 4000;
    public static final TagKey<Item> CULTIVABLE_BIOMASS = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)SporeCannons.id("cultivable_biomass"));
    public static final TagKey<Item> CRYOGENIC_PROCESSABLE_BIOMASS = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)SporeCannons.id("cryogenic_processable_biomass"));
    public static final TagKey<Item> CULTIVATOR_NUTRIENTS = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)SporeCannons.id("cultivator_nutrients"));
    private final ItemStackHandler inventory = new ItemStackHandler(3){

        public int getSlotLimit(int slot) {
            return WhiteoutIndustryMachineBlockEntity.this.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR && slot == 1 ? 1 : super.getSlotLimit(slot);
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 2) {
                return false;
            }
            return switch (WhiteoutIndustryMachineBlockEntity.this.kind()) {
                default -> throw new MatchException(null, null);
                case IndustryMachineKind.CULTIVATOR -> {
                    if (slot == 0) {
                        yield stack.is(CULTIVABLE_BIOMASS);
                    }
                    yield stack.is(CULTIVATOR_NUTRIENTS);
                }
                case IndustryMachineKind.CRYOGENIC_PROCESSOR -> {
                    if (slot == 0) {
                        if (stack.is(CRYOGENIC_PROCESSABLE_BIOMASS) || stack.is(Items.SNOWBALL)) {
                            yield true;
                        }
                        yield false;
                    }
                    yield stack.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()) && CDUCoolantCartridgeItem.remaining(stack) > 0;
                }
            };
        }

        protected void onContentsChanged(int slot) {
            if (slot == 0 && WhiteoutIndustryMachineBlockEntity.this.level != null && !((WhiteoutIndustryMachineBlockEntity)WhiteoutIndustryMachineBlockEntity.this).level.isClientSide) {
                WhiteoutIndustryMachineBlockEntity.this.progress = 0.0f;
            }
            WhiteoutIndustryMachineBlockEntity.this.contentsChanged();
        }
    };
    private final FluidTank waterTank = new FluidTank(4000, stack -> stack.getFluid() == Fluids.WATER){

        protected void onContentsChanged() {
            WhiteoutIndustryMachineBlockEntity.this.contentsChanged();
        }
    };
    private final IItemHandler automation = new MachineItemHandler(false);
    private final IItemHandler outputAutomation = new MachineItemHandler(true);
    private final IFluidHandler waterInput = new FillOnlyWaterHandler();
    private float progress;

    public WhiteoutIndustryMachineBlockEntity(BlockPos pos, BlockState state) {
        this((BlockEntityType)SporeCannonsBlockEntities.WHITEOUT_INDUSTRY_MACHINE.get(), pos, state);
    }

    public WhiteoutIndustryMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public IndustryMachineKind kind() {
        IndustryMachineKind industryMachineKind;
        Block block = this.getBlockState().getBlock();
        if (block instanceof WhiteoutIndustryMachineBlock) {
            WhiteoutIndustryMachineBlock block2 = (WhiteoutIndustryMachineBlock)block;
            industryMachineKind = block2.kind();
        } else {
            industryMachineKind = IndustryMachineKind.CULTIVATOR;
        }
        return industryMachineKind;
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public float calculateStressApplied() {
        return this.kind() == IndustryMachineKind.CULTIVATOR ? 32.0f : 48.0f;
    }

    public void tick() {
        super.tick();
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        if (!this.canProcess()) {
            if (this.progress != 0.0f) {
                this.progress = 0.0f;
                this.contentsChanged();
            }
            return;
        }
        double multiplier = this.processingMultiplier();
        if (multiplier <= 0.0) {
            return;
        }
        this.progress += (float)multiplier;
        if (this.progress < (float)this.processTime()) {
            if (server.getGameTime() % 10L == 0L) {
                this.contentsChanged();
            }
            return;
        }
        this.progress = 0.0f;
        ItemStack input = this.inventory.getStackInSlot(0);
        ItemStack produced = this.expectedOutput();
        if (this.kind() == IndustryMachineKind.CULTIVATOR) {
            this.inventory.extractItem(1, 1, false);
            this.waterTank.drain(((Integer)FreezeConfig.CULTIVATOR_WATER_PER_CYCLE.get()).intValue(), IFluidHandler.FluidAction.EXECUTE);
        } else {
            int waterCost;
            if (this.isBiomassMode()) {
                this.inventory.extractItem(0, 1, false);
            }
            if ((waterCost = this.processorWaterCost()) > 0) {
                this.waterTank.drain(waterCost, IFluidHandler.FluidAction.EXECUTE);
            }
            this.consumeProcessorCoolant(this.processorCoolantCost());
        }
        ItemStack current = this.inventory.getStackInSlot(2);
        if (current.isEmpty()) {
            this.inventory.setStackInSlot(2, produced);
        } else {
            current.grow(produced.getCount());
        }
        server.playSound(null, this.worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.45f, this.kind() == IndustryMachineKind.CULTIVATOR ? 1.25f : 1.75f);
        this.contentsChanged();
    }

    private boolean canProcess() {
        ItemStack input = this.inventory.getStackInSlot(0);
        ItemStack resource = this.inventory.getStackInSlot(1);
        ItemStack result = this.expectedOutput();
        if (this.kind() == IndustryMachineKind.CULTIVATOR ? input.isEmpty() || resource.isEmpty() || this.waterTank.getFluidAmount() < (Integer)FreezeConfig.CULTIVATOR_WATER_PER_CYCLE.get() : !this.hasValidProcessorMode() || this.coolantRemaining() < this.processorCoolantCost() || this.waterTank.getFluidAmount() < this.processorWaterCost()) {
            return false;
        }
        ItemStack output = this.inventory.getStackInSlot(2);
        return output.isEmpty() || ItemStack.isSameItemSameComponents((ItemStack)output, (ItemStack)result) && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private void consumeProcessorCoolant(int amount) {
        ItemStack cartridge = this.inventory.getStackInSlot(1);
        int remaining = Math.max(0, CDUCoolantCartridgeItem.remaining(cartridge) - amount);
        if (remaining == 0) {
            this.inventory.setStackInSlot(1, new ItemStack((ItemLike)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()));
        } else {
            CDUCoolantCartridgeItem.setRemaining(cartridge, remaining);
        }
        this.contentsChanged();
    }

    private boolean isBiomassMode() {
        return this.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR && this.inventory.getStackInSlot(0).is(CRYOGENIC_PROCESSABLE_BIOMASS);
    }

    public boolean snowProductionMode() {
        return this.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR && this.inventory.getStackInSlot(0).is(Items.SNOWBALL);
    }

    private boolean hasValidProcessorMode() {
        return this.isBiomassMode() || this.snowProductionMode() || this.deepFreezingMode();
    }

    private ItemStack expectedOutput() {
        ItemStack input = this.inventory.getStackInSlot(0);
        if (this.kind() == IndustryMachineKind.CULTIVATOR) {
            return input.copyWithCount(1);
        }
        if (this.isBiomassMode()) {
            return new ItemStack((ItemLike)Sblocks.FROST_BURNED_BIOMASS.get());
        }
        if (this.snowProductionMode()) {
            return new ItemStack((ItemLike)Items.SNOWBALL, ((Integer)FreezeConfig.PROCESSOR_SNOW_OUTPUT.get()).intValue());
        }
        return this.deepFreezingMode() ? new ItemStack((ItemLike)Items.BLUE_ICE) : ItemStack.EMPTY;
    }

    public boolean addWaterBucket(Player player, ItemStack stack) {
        if (!stack.is(Items.WATER_BUCKET) || this.waterTank.getSpace() < 1000) {
            return false;
        }
        if (this.level == null || this.level.isClientSide) {
            return true;
        }
        this.waterTank.fill(new FluidStack((Fluid)Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            ItemStack bucket = new ItemStack((ItemLike)Items.BUCKET);
            if (!player.getInventory().add(bucket)) {
                player.drop(bucket, false);
            }
        }
        this.level.playSound(null, this.worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.8f, 1.0f);
        return true;
    }

    public ItemStackHandler inventory() {
        return this.inventory;
    }

    public int waterAmount() {
        return this.waterTank.getFluidAmount();
    }

    public int coolantRemaining() {
        ItemStack cartridge = this.inventory.getStackInSlot(1);
        return cartridge.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()) ? CDUCoolantCartridgeItem.remaining(cartridge) : 0;
    }

    public int maxCoolant() {
        return 54000;
    }

    public int remainingCycles() {
        return this.coolantRemaining() / this.processorCoolantCost();
    }

    public int maxCycles() {
        return this.maxCoolant() / this.processorCoolantCost();
    }

    public boolean deepFreezingMode() {
        return this.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR && this.inventory.getStackInSlot(0).isEmpty();
    }

    public String processorModeKey() {
        if (this.snowProductionMode()) {
            return "snow";
        }
        if (this.isBiomassMode()) {
            return "biomass";
        }
        return "deep_freezing";
    }

    public float kineticSpeed() {
        return this.getSpeed();
    }

    public boolean kineticOverstressed() {
        return this.isOverStressed();
    }

    public int processTime() {
        return this.kind() == IndustryMachineKind.CULTIVATOR ? (Integer)FreezeConfig.CULTIVATOR_PROCESS_TIME.get() : (this.snowProductionMode() ? (Integer)FreezeConfig.PROCESSOR_SNOW_PROCESS_TIME.get() : (this.deepFreezingMode() ? (Integer)FreezeConfig.PROCESSOR_BLUE_ICE_PROCESS_TIME.get() : (Integer)FreezeConfig.PROCESSOR_PROCESS_TIME.get()));
    }

    public int progressPercent() {
        return Math.min(100, Math.round(this.progress * 100.0f / (float)this.processTime()));
    }

    private int processorCoolantCost() {
        return this.snowProductionMode() ? (Integer)FreezeConfig.PROCESSOR_SNOW_COOLANT.get() : (this.deepFreezingMode() ? (Integer)FreezeConfig.PROCESSOR_BLUE_ICE_COOLANT.get() : (Integer)FreezeConfig.PROCESSOR_BIOMASS_COOLANT.get());
    }

    private int processorWaterCost() {
        if (this.snowProductionMode()) {
            return (Integer)FreezeConfig.PROCESSOR_SNOW_WATER.get();
        }
        return this.deepFreezingMode() ? (Integer)FreezeConfig.PROCESSOR_BLUE_ICE_WATER.get() : 0;
    }

    public double processingMultiplier() {
        return this.isOverStressed() ? 0.0 : WhiteoutKineticScaling.stationMultiplier(this.getSpeed());
    }

    public String statusKey() {
        if (this.kind() == IndustryMachineKind.CULTIVATOR) {
            if (this.inventory.getStackInSlot(0).isEmpty()) {
                return "input";
            }
            if (this.inventory.getStackInSlot(1).isEmpty()) {
                return "resource";
            }
            if (this.waterTank.getFluidAmount() < (Integer)FreezeConfig.CULTIVATOR_WATER_PER_CYCLE.get()) {
                return "water";
            }
        } else {
            if (!this.hasValidProcessorMode()) {
                return "input";
            }
            int requiredCoolant = this.processorCoolantCost();
            if (this.coolantRemaining() < requiredCoolant) {
                return this.coolantRemaining() == 0 ? "cartridge" : "charge";
            }
            if (this.waterTank.getFluidAmount() < this.processorWaterCost()) {
                return this.deepFreezingMode() ? "input_or_water" : "water";
            }
        }
        if (!this.canOutput()) {
            return "output";
        }
        if (this.isOverStressed()) {
            return "jammed";
        }
        if (Math.abs(this.getSpeed()) < 32.0f) {
            return "stopped";
        }
        return "processing";
    }

    private boolean canOutput() {
        ItemStack expected = this.expectedOutput();
        ItemStack output = this.inventory.getStackInSlot(2);
        return output.isEmpty() || !expected.isEmpty() && ItemStack.isSameItemSameComponents((ItemStack)output, (ItemStack)expected) && output.getCount() < output.getMaxStackSize();
    }

    public IItemHandler itemHandlerForSide(@Nullable Direction side) {
        return side == Direction.DOWN ? this.outputAutomation : this.automation;
    }

    public IFluidHandler fluidHandlerForSide(@Nullable Direction side) {
        return side != Direction.DOWN ? this.waterInput : null;
    }

    private void contentsChanged() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    public void dropContents() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        for (int slot = 0; slot < this.inventory.getSlots(); ++slot) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack((Level)this.level, (double)((double)this.worldPosition.getX() + 0.5), (double)((double)this.worldPosition.getY() + 0.5), (double)((double)this.worldPosition.getZ() + 0.5), (ItemStack)stack.copy());
            }
            this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    public Component getDisplayName() {
        return Component.translatable((String)("block.sporecannons." + this.kind().id()));
    }

    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WhiteoutIndustryMachineMenu(id, inventory, this);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        tag.put("Water", (Tag)this.waterTank.writeToNBT(registries, new CompoundTag()));
        tag.putFloat("Progress", this.progress);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if (tag.contains("Water")) {
            this.waterTank.readFromNBT(registries, tag.getCompound("Water"));
        }
        this.progress = tag.getFloat("Progress");
    }

    private final class MachineItemHandler
    implements IItemHandler {
        private final boolean outputOnly;

        private MachineItemHandler(boolean outputOnly) {
            this.outputOnly = outputOnly;
        }

        public int getSlots() {
            return 3;
        }

        public ItemStack getStackInSlot(int slot) {
            return WhiteoutIndustryMachineBlockEntity.this.inventory.getStackInSlot(slot);
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return !this.outputOnly && slot != 2 ? WhiteoutIndustryMachineBlockEntity.this.inventory.insertItem(slot, stack, simulate) : stack;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 2) {
                return WhiteoutIndustryMachineBlockEntity.this.inventory.extractItem(slot, amount, simulate);
            }
            if (!this.outputOnly && WhiteoutIndustryMachineBlockEntity.this.kind() == IndustryMachineKind.CRYOGENIC_PROCESSOR && slot == 1 && WhiteoutIndustryMachineBlockEntity.this.inventory.getStackInSlot(slot).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get())) {
                return WhiteoutIndustryMachineBlockEntity.this.inventory.extractItem(slot, amount, simulate);
            }
            return ItemStack.EMPTY;
        }

        public int getSlotLimit(int slot) {
            return WhiteoutIndustryMachineBlockEntity.this.inventory.getSlotLimit(slot);
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return !this.outputOnly && slot != 2 && WhiteoutIndustryMachineBlockEntity.this.inventory.isItemValid(slot, stack);
        }
    }

    private final class FillOnlyWaterHandler
    implements IFluidHandler {
        private FillOnlyWaterHandler() {
        }

        public int getTanks() {
            return 1;
        }

        public FluidStack getFluidInTank(int tank) {
            return WhiteoutIndustryMachineBlockEntity.this.waterTank.getFluidInTank(0);
        }

        public int getTankCapacity(int tank) {
            return 4000;
        }

        public boolean isFluidValid(int tank, FluidStack stack) {
            return WhiteoutIndustryMachineBlockEntity.this.waterTank.isFluidValid(stack);
        }

        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return WhiteoutIndustryMachineBlockEntity.this.waterTank.fill(resource, action);
        }

        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }

        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }
    }
}

