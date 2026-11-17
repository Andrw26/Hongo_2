/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.elvoux.sporecannons.facility;

import com.elvoux.sporecannons.facility.MultiblockStorageBlock;
import com.elvoux.sporecannons.facility.MultiblockStorageBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsMenus;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class MultiblockStorageMenu
extends AbstractContainerMenu {
    public static final int PAGE_SLOTS = 54;
    private final MultiblockStorageBlockEntity origin;
    private final MultiblockStorageBlock.StorageKind kind;
    private final PagedHandler pagedHandler;

    public MultiblockStorageMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(id, playerInventory, (MultiblockStorageBlockEntity)playerInventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public MultiblockStorageMenu(int id, Inventory playerInventory, MultiblockStorageBlockEntity origin) {
        super((MenuType)SporeCannonsMenus.MULTIBLOCK_STORAGE.get(), id);
        this.origin = origin;
        this.kind = origin == null ? MultiblockStorageBlock.StorageKind.SAMPLE : origin.kind();
        List handlers = origin == null ? List.of() : origin.members().stream().map(MultiblockStorageBlockEntity::inventory).toList();
        this.pagedHandler = new PagedHandler(handlers, this.kind.slotsPerModule());
        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.pagedHandler, col + row * 9, 29 + col * 18, 36 + row * 18));
            }
        }
        this.addPlayerInventory(playerInventory);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)inventory, col + row * 9 + 9, 29 + col * 18, 160 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)inventory, col, 29 + col * 18, 218));
        }
    }

    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id > this.maxPage()) {
            return false;
        }
        this.pagedHandler.setPage(id);
        this.broadcastChanges();
        return true;
    }

    public void selectClientPage(int page) {
        this.pagedHandler.setPage(Math.max(0, Math.min(this.maxPage(), page)));
    }

    public ItemStack quickMoveStack(Player player, int index) {
        int inventoryEnd;
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = (Slot)this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < 54 ? !this.moveItemStackTo(stack, 54, this.slots.size(), true) : !this.moveItemStackTo(stack, 0, 54, false) && (index < (inventoryEnd = 81) ? !this.moveItemStackTo(stack, inventoryEnd, this.slots.size(), false) : !this.moveItemStackTo(stack, 54, inventoryEnd, false))) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return copy;
    }

    public boolean stillValid(Player player) {
        return this.origin != null && !this.origin.isRemoved() && player.distanceToSqr(this.origin.getBlockPos().getCenter()) <= 64.0;
    }

    public MultiblockStorageBlock.StorageKind kind() {
        return this.kind;
    }

    public int page() {
        return this.pagedHandler.page();
    }

    public int maxPage() {
        return this.pagedHandler.maxPage();
    }

    public int totalSlots() {
        return this.pagedHandler.totalSlots();
    }

    public int visibleSlots() {
        return Math.max(0, Math.min(54, this.totalSlots() - this.page() * 54));
    }

    public int structureSize() {
        return this.origin == null ? 1 : this.origin.structureSize();
    }

    public int moduleCount() {
        return this.origin == null ? 1 : this.origin.members().size();
    }

    private static final class PagedHandler
    implements IItemHandlerModifiable {
        private final List<ItemStackHandler> handlers;
        private final int slotsPerModule;
        private int page;

        private PagedHandler(List<ItemStackHandler> handlers, int slotsPerModule) {
            this.handlers = handlers;
            this.slotsPerModule = slotsPerModule;
        }

        int page() {
            return this.page;
        }

        int totalSlots() {
            return this.handlers.size() * this.slotsPerModule;
        }

        int maxPage() {
            return Math.max(0, (this.totalSlots() - 1) / 54);
        }

        void setPage(int page) {
            this.page = Math.max(0, Math.min(this.maxPage(), page));
        }

        private int global(int slot) {
            return this.page * 54 + slot;
        }

        private ItemStackHandler handler(int global) {
            return this.handlers.get(global / this.slotsPerModule);
        }

        private int local(int global) {
            return global % this.slotsPerModule;
        }

        private boolean valid(int slot) {
            int global = this.global(slot);
            return slot >= 0 && slot < 54 && global < this.totalSlots();
        }

        public int getSlots() {
            return 54;
        }

        public ItemStack getStackInSlot(int slot) {
            return this.valid(slot) ? this.handler(this.global(slot)).getStackInSlot(this.local(this.global(slot))) : ItemStack.EMPTY;
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            if (this.valid(slot)) {
                this.handler(this.global(slot)).setStackInSlot(this.local(this.global(slot)), stack);
            }
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return this.valid(slot) ? this.handler(this.global(slot)).insertItem(this.local(this.global(slot)), stack, simulate) : stack;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return this.valid(slot) ? this.handler(this.global(slot)).extractItem(this.local(this.global(slot)), amount, simulate) : ItemStack.EMPTY;
        }

        public int getSlotLimit(int slot) {
            return this.valid(slot) ? this.handler(this.global(slot)).getSlotLimit(this.local(this.global(slot))) : 0;
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return this.valid(slot) && this.handler(this.global(slot)).isItemValid(this.local(this.global(slot)), stack);
        }
    }
}

