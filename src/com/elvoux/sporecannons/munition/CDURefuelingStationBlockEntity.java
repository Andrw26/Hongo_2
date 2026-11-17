/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
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
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
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
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CDURefuelingStationBlock;
import com.elvoux.sporecannons.munition.CDURefuelingStationMenu;
import com.elvoux.sporecannons.munition.WhiteoutKineticScaling;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import java.util.UUID;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public final class CDURefuelingStationBlockEntity
extends KineticBlockEntity
implements MenuProvider {
    public static final float MINIMUM_SPEED = 32.0f;
    public static final int PROCESS_TIME = 160;
    public static final int TANK_CAPACITY = 4000;
    public static final int WATER_PER_CARTRIDGE = 1000;
    public static final int CRYOGEL_SLOT = 0;
    public static final int CARTRIDGE_SLOT = 1;
    private final ItemStackHandler inventory = new ItemStackHandler(2){

        public int getSlotLimit(int slot) {
            return slot == 1 ? 1 : 64;
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 ? stack.is((Item)SporeCannonsItems.STABILIZED_CRYOGEL.get()) : stack.is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get());
        }

        protected void onContentsChanged(int slot) {
            CDURefuelingStationBlockEntity.this.contentsChanged();
        }
    };
    private final FluidTank waterTank = new FluidTank(4000, stack -> stack.getFluid() == Fluids.WATER){

        protected void onContentsChanged() {
            CDURefuelingStationBlockEntity.this.contentsChanged();
        }
    };
    private final IItemHandler cryogelInput = new StationItemHandler(true, false, 0);
    private final IItemHandler cartridgeAccess = new StationItemHandler(true, true, 1);
    private final IFluidHandler waterInput = new FillOnlyFluidHandler();
    private float progress;
    private UUID operator;

    public CDURefuelingStationBlockEntity(BlockPos pos, BlockState state) {
        this((BlockEntityType)SporeCannonsBlockEntities.CDU_REFUELING_STATION.get(), pos, state);
    }

    public CDURefuelingStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public float calculateStressApplied() {
        return 16.0f;
    }

    public void tick() {
        ServerPlayer player;
        super.tick();
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        ServerLevel server = (ServerLevel)this.level;
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
        if (server.getGameTime() % 10L == 0L) {
            server.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, (double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 1.05, (double)this.worldPosition.getZ() + 0.5, 3, 0.22, 0.08, 0.22, 0.015);
        }
        if (this.progress < 160.0f) {
            if (server.getGameTime() % 5L == 0L) {
                this.contentsChanged();
            }
            return;
        }
        this.progress = 0.0f;
        this.inventory.extractItem(0, 1, false);
        this.waterTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
        this.inventory.setStackInSlot(1, new ItemStack((ItemLike)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()));
        server.playSound(null, this.worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.75f, 1.7f);
        server.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, (double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 1.1, (double)this.worldPosition.getZ() + 0.5, 18, 0.32, 0.18, 0.32, 0.035);
        ServerPlayer serverPlayer = player = this.operator == null ? null : server.getServer().getPlayerList().getPlayer(this.operator);
        if (player != null) {
            ((PlayerTrigger)SporeCannonsCriteria.CARTRIDGE_REFILLED.get()).trigger(player);
        }
        this.contentsChanged();
    }

    private boolean canProcess() {
        return this.inventory.getStackInSlot(1).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()) && !this.inventory.getStackInSlot(0).isEmpty() && this.waterTank.getFluidAmount() >= 1000;
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
        this.operator = player.getUUID();
        this.level.playSound(null, this.worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.8f, 1.0f);
        return true;
    }

    public ItemStackHandler inventory() {
        return this.inventory;
    }

    public int waterAmount() {
        return this.waterTank.getFluidAmount();
    }

    public int fillWaterFromAssembly(int amount) {
        if (amount <= 0) {
            return 0;
        }
        return this.waterTank.fill(new FluidStack((Fluid)Fluids.WATER, amount), IFluidHandler.FluidAction.EXECUTE);
    }

    public int progressPercent() {
        return Math.min(100, (int)(this.progress * 100.0f / 160.0f));
    }

    public float kineticSpeed() {
        return this.getSpeed();
    }

    public boolean kineticOverstressed() {
        return this.isOverStressed();
    }

    public double processingMultiplier() {
        return this.isOverStressed() ? 0.0 : WhiteoutKineticScaling.stationMultiplier(this.getSpeed());
    }

    public double estimatedProcessSeconds() {
        double multiplier = this.processingMultiplier();
        return multiplier <= 0.0 ? 0.0 : 160.0 / multiplier / 20.0;
    }

    public String statusKey() {
        ItemStack cartridge = this.inventory.getStackInSlot(1);
        if (cartridge.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get())) {
            return "complete";
        }
        if (cartridge.isEmpty()) {
            return "empty";
        }
        if (this.waterTank.getFluidAmount() < 1000) {
            return "water";
        }
        if (this.inventory.getStackInSlot(0).isEmpty()) {
            return "cryogel";
        }
        if (this.isOverStressed()) {
            return "jammed";
        }
        if (Math.abs(this.getSpeed()) < 32.0f) {
            return "stopped";
        }
        return "processing";
    }

    public IItemHandler itemHandlerForSide(@Nullable Direction side) {
        if (side == null) {
            return this.inventory;
        }
        if (side == Direction.DOWN) {
            return null;
        }
        if (side == Direction.UP) {
            return this.cryogelInput;
        }
        Direction front = (Direction)this.getBlockState().getValue((Property)CDURefuelingStationBlock.FACING);
        return side.getAxis().isHorizontal() && side.getAxis() != front.getAxis() ? this.cartridgeAccess : null;
    }

    public IFluidHandler fluidHandlerForSide(@Nullable Direction side) {
        if (side == null) {
            return this.waterInput;
        }
        Direction front = (Direction)this.getBlockState().getValue((Property)CDURefuelingStationBlock.FACING);
        return side == front ? this.waterInput : null;
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
        return Component.translatable((String)"block.sporecannons.cdu_refueling_station");
    }

    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CDURefuelingStationMenu(id, inventory, this);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        tag.put("WaterTank", (Tag)this.waterTank.writeToNBT(registries, new CompoundTag()));
        tag.putFloat("Progress", this.progress);
        if (this.operator != null) {
            tag.putUUID("Operator", this.operator);
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if (tag.contains("WaterTank")) {
            this.waterTank.readFromNBT(registries, tag.getCompound("WaterTank"));
        }
        this.progress = tag.getFloat("Progress");
        this.operator = tag.hasUUID("Operator") ? tag.getUUID("Operator") : null;
    }

    private final class StationItemHandler
    implements IItemHandler {
        private final boolean insert;
        private final boolean extract;
        private final int realSlot;

        private StationItemHandler(boolean insert, boolean extract, int realSlot) {
            this.insert = insert;
            this.extract = extract;
            this.realSlot = realSlot;
        }

        public int getSlots() {
            return 1;
        }

        public ItemStack getStackInSlot(int slot) {
            return CDURefuelingStationBlockEntity.this.inventory.getStackInSlot(this.realSlot);
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return this.insert ? CDURefuelingStationBlockEntity.this.inventory.insertItem(this.realSlot, stack, simulate) : stack;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!this.extract || !CDURefuelingStationBlockEntity.this.inventory.getStackInSlot(this.realSlot).is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get())) {
                return ItemStack.EMPTY;
            }
            return CDURefuelingStationBlockEntity.this.inventory.extractItem(this.realSlot, amount, simulate);
        }

        public int getSlotLimit(int slot) {
            return CDURefuelingStationBlockEntity.this.inventory.getSlotLimit(this.realSlot);
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return this.insert && CDURefuelingStationBlockEntity.this.inventory.isItemValid(this.realSlot, stack);
        }
    }

    private final class FillOnlyFluidHandler
    implements IFluidHandler {
        private FillOnlyFluidHandler() {
        }

        public int getTanks() {
            return 1;
        }

        public FluidStack getFluidInTank(int tank) {
            return CDURefuelingStationBlockEntity.this.waterTank.getFluidInTank(0);
        }

        public int getTankCapacity(int tank) {
            return 4000;
        }

        public boolean isFluidValid(int tank, FluidStack stack) {
            return CDURefuelingStationBlockEntity.this.waterTank.isFluidValid(stack);
        }

        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return CDURefuelingStationBlockEntity.this.waterTank.fill(resource, action);
        }

        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }

        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }
    }
}

