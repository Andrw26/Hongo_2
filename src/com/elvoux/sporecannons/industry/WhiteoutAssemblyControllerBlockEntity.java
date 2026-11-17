/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
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
 *  net.minecraft.world.level.block.entity.BlockEntity
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
package com.elvoux.sporecannons.industry;

import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.facility.MultiblockStorageBlock;
import com.elvoux.sporecannons.facility.MultiblockStorageBlockEntity;
import com.elvoux.sporecannons.industry.AssemblyKind;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlock;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerMenu;
import com.elvoux.sporecannons.munition.CDURefuelingStationBlockEntity;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
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
import net.minecraft.world.level.block.entity.BlockEntity;
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

public final class WhiteoutAssemblyControllerBlockEntity
extends BlockEntity
implements MenuProvider {
    public static final int TANK_CAPACITY = 16000;
    private static final TagKey<Block> PROJECTOR_CASING = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)SporeCannons.id("projector_assembly_casing"));
    private static final TagKey<Block> REFUELLING_CASING = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)SporeCannons.id("refuelling_assembly_casing"));
    private final ItemStackHandler inventory = new ItemStackHandler(12){

        public boolean isItemValid(int slot, ItemStack stack) {
            if (WhiteoutAssemblyControllerBlockEntity.this.kind() == AssemblyKind.PROJECTOR) {
                return slot < 6 && stack.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get());
            }
            if (slot < 6) {
                return stack.is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get());
            }
            if (slot < 9) {
                return stack.is((Item)SporeCannonsItems.STABILIZED_CRYOGEL.get());
            }
            return false;
        }

        protected void onContentsChanged(int slot) {
            WhiteoutAssemblyControllerBlockEntity.this.contentsChanged();
        }
    };
    private final FluidTank waterTank = new FluidTank(16000, stack -> stack.getFluid() == Fluids.WATER){

        protected void onContentsChanged() {
            WhiteoutAssemblyControllerBlockEntity.this.contentsChanged();
        }
    };
    private final IItemHandler automation = new ControllerItemHandler();
    private final IFluidHandler waterInput = new FillOnlyWaterHandler();
    private BlockPos linkedCore;
    private boolean formed;
    private int validationCooldown;
    private int reservoirScanCooldown;

    public WhiteoutAssemblyControllerBlockEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)SporeCannonsBlockEntities.WHITEOUT_ASSEMBLY_CONTROLLER.get(), pos, state);
    }

    public AssemblyKind kind() {
        AssemblyKind assemblyKind;
        Block block = this.getBlockState().getBlock();
        if (block instanceof WhiteoutAssemblyControllerBlock) {
            WhiteoutAssemblyControllerBlock block2 = (WhiteoutAssemblyControllerBlock)block;
            assemblyKind = block2.kind();
        } else {
            assemblyKind = AssemblyKind.PROJECTOR;
        }
        return assemblyKind;
    }

    public void tick() {
        CryogenicContainmentProjectorBlockEntity projector;
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.validationCooldown-- <= 0) {
            this.refreshAssembly();
            this.validationCooldown = 20;
        }
        if (!this.formed) {
            return;
        }
        BlockEntity core = this.linkedBlockEntity();
        if (this.kind() == AssemblyKind.PROJECTOR && core instanceof CryogenicContainmentProjectorBlockEntity && (projector = (CryogenicContainmentProjectorBlockEntity)core).coolantTicksRemaining() <= 0) {
            this.hotSwap(projector);
        } else if (this.kind() == AssemblyKind.REFUELLING && core instanceof CDURefuelingStationBlockEntity) {
            CDURefuelingStationBlockEntity station = (CDURefuelingStationBlockEntity)core;
            this.serviceStation(station);
        }
    }

    private void refreshAssembly() {
        BlockPos oldCore = this.linkedCore;
        boolean oldFormed = this.formed;
        BlockPos expectedCore = this.expectedCorePos();
        this.linkedCore = this.coreMatches(expectedCore) ? expectedCore : null;
        boolean bl = this.formed = this.linkedCore != null && this.casingPositions().stream().allMatch(this::isValidCasing);
        if (oldFormed != this.formed || (oldCore == null ? this.linkedCore != null : !oldCore.equals((Object)this.linkedCore))) {
            this.contentsChanged();
        }
    }

    private BlockPos expectedCorePos() {
        Direction front = (Direction)this.getBlockState().getValue((Property)WhiteoutAssemblyControllerBlock.FACING);
        return this.worldPosition.relative(front.getOpposite()).immutable();
    }

    private List<BlockPos> casingPositions() {
        Direction front = (Direction)this.getBlockState().getValue((Property)WhiteoutAssemblyControllerBlock.FACING);
        Direction left = front.getCounterClockWise();
        Direction right = front.getClockWise();
        BlockPos core = this.expectedCorePos();
        return List.of((Object)this.worldPosition.relative(left), (Object)this.worldPosition.relative(right), (Object)core.relative(left), (Object)core.relative(right));
    }

    private TagKey<Block> casingTag() {
        return this.kind() == AssemblyKind.PROJECTOR ? PROJECTOR_CASING : REFUELLING_CASING;
    }

    private boolean isLoaded(BlockPos pos) {
        return this.level != null && this.level.hasChunkAt(pos);
    }

    private boolean coreMatches(BlockPos pos) {
        if (!this.isLoaded(pos)) {
            return false;
        }
        BlockEntity core = this.level.getBlockEntity(pos);
        return this.kind() == AssemblyKind.PROJECTOR ? core instanceof CryogenicContainmentProjectorBlockEntity : core instanceof CDURefuelingStationBlockEntity;
    }

    private boolean isValidCasing(BlockPos pos) {
        return this.isLoaded(pos) && this.level.getBlockState(pos).is(this.casingTag());
    }

    @Nullable
    private BlockEntity linkedBlockEntity() {
        boolean matches;
        if (this.level == null) {
            return null;
        }
        BlockEntity current = this.linkedCore == null || !this.isLoaded(this.linkedCore) ? null : this.level.getBlockEntity(this.linkedCore);
        boolean bl = matches = this.kind() == AssemblyKind.PROJECTOR ? current instanceof CryogenicContainmentProjectorBlockEntity : current instanceof CDURefuelingStationBlockEntity;
        if (matches) {
            return current;
        }
        this.refreshAssembly();
        return this.linkedCore == null ? null : this.level.getBlockEntity(this.linkedCore);
    }

    private void serviceStation(CDURefuelingStationBlockEntity station) {
        ItemStack depleted;
        ItemStack remainder;
        ItemStack remainder2;
        ItemStack cartridge;
        int accepted = station.fillWaterFromAssembly(Math.min(1000, this.waterTank.getFluidAmount()));
        if (accepted > 0) {
            this.waterTank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
        }
        if ((cartridge = station.inventory().getStackInSlot(1)).is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()) && (remainder2 = this.insertIntoRange(cartridge.copy(), 9, 12, false)).isEmpty()) {
            station.inventory().setStackInSlot(1, ItemStack.EMPTY);
        }
        if (station.inventory().getStackInSlot(0).isEmpty()) {
            ItemStack gel = this.extractOne(6, 9);
            if (gel.isEmpty() && this.reservoirScanCooldown-- <= 0) {
                gel = this.pullCryogelFromReservoir();
                this.reservoirScanCooldown = 20;
            }
            if (!gel.isEmpty() && !(remainder = station.inventory().insertItem(0, gel, false)).isEmpty()) {
                this.insertIntoRange(remainder, 6, 9, false);
            }
        }
        if (station.inventory().getStackInSlot(1).isEmpty() && !(depleted = this.extractOne(0, 6)).isEmpty() && !(remainder = station.inventory().insertItem(1, depleted, false)).isEmpty()) {
            this.insertIntoRange(remainder, 0, 6, false);
        }
    }

    private ItemStack pullCryogelFromReservoir() {
        if (this.level == null) {
            return ItemStack.EMPTY;
        }
        for (BlockPos pos : BlockPos.betweenClosed((BlockPos)this.worldPosition.offset(-3, -2, -3), (BlockPos)this.worldPosition.offset(3, 2, 3))) {
            MultiblockStorageBlockEntity storage;
            BlockEntity blockEntity;
            if (!this.level.hasChunkAt(pos) || !((blockEntity = this.level.getBlockEntity(pos)) instanceof MultiblockStorageBlockEntity) || (storage = (MultiblockStorageBlockEntity)blockEntity).kind() != MultiblockStorageBlock.StorageKind.CRYOGEL) continue;
            IItemHandler handler = storage.automationHandler();
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                ItemStack extracted = handler.extractItem(slot, 1, false);
                if (extracted.isEmpty()) continue;
                return extracted;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean tryHotSwap(CryogenicContainmentProjectorBlockEntity projector) {
        if (projector.getLevel() == null || projector.getLevel().isClientSide) {
            return false;
        }
        BlockPos core = projector.getBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            WhiteoutAssemblyControllerBlockEntity controller;
            BlockEntity blockEntity;
            BlockPos pos = core.relative(direction);
            if (!projector.getLevel().hasChunkAt(pos) || !((blockEntity = projector.getLevel().getBlockEntity(pos)) instanceof WhiteoutAssemblyControllerBlockEntity) || (controller = (WhiteoutAssemblyControllerBlockEntity)blockEntity).kind() != AssemblyKind.PROJECTOR || !controller.formed || controller.linkedCore == null || !controller.linkedCore.equals((Object)core) || !controller.hotSwap(projector)) continue;
            return true;
        }
        return false;
    }

    private boolean hotSwap(CryogenicContainmentProjectorBlockEntity projector) {
        ItemStack charged;
        int chargedSlot = this.firstNonEmpty(0, 6);
        if (chargedSlot < 0) {
            return false;
        }
        ItemStack current = projector.inventory().getStackInSlot(0);
        if (!current.isEmpty()) {
            ItemStack depleted = new ItemStack((ItemLike)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get());
            if (!this.insertIntoRange(depleted, 6, 12, true).isEmpty()) {
                return false;
            }
            this.insertIntoRange(depleted, 6, 12, false);
        }
        if ((charged = this.inventory.extractItem(chargedSlot, 1, false)).isEmpty()) {
            return false;
        }
        projector.assemblySwapCartridge(charged);
        this.contentsChanged();
        return true;
    }

    private int firstNonEmpty(int start, int end) {
        for (int slot = start; slot < end; ++slot) {
            if (this.inventory.getStackInSlot(slot).isEmpty()) continue;
            return slot;
        }
        return -1;
    }

    private ItemStack extractOne(int start, int end) {
        int slot = this.firstNonEmpty(start, end);
        return slot < 0 ? ItemStack.EMPTY : this.inventory.extractItem(slot, 1, false);
    }

    private ItemStack insertIntoRange(ItemStack stack, int start, int end, boolean simulate) {
        ItemStack remainder = stack.copy();
        for (int slot = start; slot < end && !remainder.isEmpty(); ++slot) {
            int moved;
            boolean outputSlot;
            boolean bl = this.kind() == AssemblyKind.PROJECTOR ? slot >= 6 : (outputSlot = slot >= 9);
            if (!outputSlot && !this.inventory.isItemValid(slot, remainder)) continue;
            ItemStack existing = this.inventory.getStackInSlot(slot);
            if (existing.isEmpty()) {
                moved = Math.min(remainder.getCount(), this.inventory.getSlotLimit(slot));
                if (!simulate) {
                    this.inventory.setStackInSlot(slot, remainder.copyWithCount(moved));
                }
                remainder.shrink(moved);
                continue;
            }
            if (!ItemStack.isSameItemSameComponents((ItemStack)existing, (ItemStack)remainder)) continue;
            moved = Math.min(remainder.getCount(), Math.min(existing.getMaxStackSize(), this.inventory.getSlotLimit(slot)) - existing.getCount());
            if (moved > 0 && !simulate) {
                ItemStack updated = existing.copy();
                updated.grow(moved);
                this.inventory.setStackInSlot(slot, updated);
            }
            remainder.shrink(Math.max(0, moved));
        }
        return remainder;
    }

    public boolean addWaterBucket(Player player, ItemStack stack) {
        if (this.kind() != AssemblyKind.REFUELLING || !stack.is(Items.WATER_BUCKET) || this.waterTank.getSpace() < 1000) {
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
        return true;
    }

    public ItemStackHandler inventory() {
        return this.inventory;
    }

    public boolean formed() {
        return this.formed;
    }

    @Nullable
    public BlockPos linkedCorePos() {
        return this.linkedCore;
    }

    public void requestValidation() {
        this.validationCooldown = 0;
    }

    public boolean serviceLinkedCoreNow() {
        this.refreshAssembly();
        if (!this.formed) {
            return false;
        }
        BlockEntity core = this.linkedBlockEntity();
        if (this.kind() == AssemblyKind.PROJECTOR && core instanceof CryogenicContainmentProjectorBlockEntity) {
            CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)core;
            return projector.coolantTicksRemaining() <= 0 && this.hotSwap(projector);
        }
        if (this.kind() == AssemblyKind.REFUELLING && core instanceof CDURefuelingStationBlockEntity) {
            CDURefuelingStationBlockEntity station = (CDURefuelingStationBlockEntity)core;
            this.serviceStation(station);
            return true;
        }
        return false;
    }

    public String linkedCoreDebug() {
        Object object;
        BlockEntity core = this.linkedBlockEntity();
        String string = String.valueOf((Object)this.kind());
        String string2 = String.valueOf(this.linkedCore);
        String string3 = core == null ? "null" : core.getClass().getName();
        if (core instanceof CryogenicContainmentProjectorBlockEntity) {
            CryogenicContainmentProjectorBlockEntity projector = (CryogenicContainmentProjectorBlockEntity)core;
            object = ", coolant=" + projector.coolantTicksRemaining();
        } else {
            object = "";
        }
        return "kind=" + string + ", formed=" + this.formed + ", linked=" + string2 + ", core=" + string3 + (String)object;
    }

    public int waterAmount() {
        return this.waterTank.getFluidAmount();
    }

    public IItemHandler itemHandlerForSide(@Nullable Direction side) {
        return this.automation;
    }

    public IFluidHandler fluidHandlerForSide(@Nullable Direction side) {
        return this.kind() == AssemblyKind.REFUELLING ? this.waterInput : null;
    }

    private void contentsChanged() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
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
        return new WhiteoutAssemblyControllerMenu(id, inventory, this);
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        tag.put("Water", (Tag)this.waterTank.writeToNBT(registries, new CompoundTag()));
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if (tag.contains("Water")) {
            this.waterTank.readFromNBT(registries, tag.getCompound("Water"));
        }
        this.formed = false;
        this.linkedCore = null;
        this.validationCooldown = 0;
    }

    private final class ControllerItemHandler
    implements IItemHandler {
        private ControllerItemHandler() {
        }

        public int getSlots() {
            return WhiteoutAssemblyControllerBlockEntity.this.inventory.getSlots();
        }

        public ItemStack getStackInSlot(int slot) {
            return WhiteoutAssemblyControllerBlockEntity.this.inventory.getStackInSlot(slot);
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return WhiteoutAssemblyControllerBlockEntity.this.inventory.isItemValid(slot, stack) ? WhiteoutAssemblyControllerBlockEntity.this.inventory.insertItem(slot, stack, simulate) : stack;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            boolean output = WhiteoutAssemblyControllerBlockEntity.this.kind() == AssemblyKind.PROJECTOR ? slot >= 6 : slot >= 9;
            return output ? WhiteoutAssemblyControllerBlockEntity.this.inventory.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        public int getSlotLimit(int slot) {
            return WhiteoutAssemblyControllerBlockEntity.this.inventory.getSlotLimit(slot);
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return WhiteoutAssemblyControllerBlockEntity.this.inventory.isItemValid(slot, stack);
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
            return WhiteoutAssemblyControllerBlockEntity.this.waterTank.getFluidInTank(0);
        }

        public int getTankCapacity(int tank) {
            return 16000;
        }

        public boolean isFluidValid(int tank, FluidStack stack) {
            return WhiteoutAssemblyControllerBlockEntity.this.waterTank.isFluidValid(stack);
        }

        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return WhiteoutAssemblyControllerBlockEntity.this.waterTank.fill(resource, action);
        }

        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }

        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }
    }
}

