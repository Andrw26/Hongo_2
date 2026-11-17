/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.DataSlot
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.elvoux.sporecannons.industry;

import com.elvoux.sporecannons.industry.AssemblyKind;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class WhiteoutAssemblyControllerMenu
extends AbstractContainerMenu {
    private final WhiteoutAssemblyControllerBlockEntity controller;
    private final AssemblyKind kind;
    private int syncedFormed;
    private int syncedWater;

    public WhiteoutAssemblyControllerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(id, inventory, (WhiteoutAssemblyControllerBlockEntity)inventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public WhiteoutAssemblyControllerMenu(int id, Inventory inventory, WhiteoutAssemblyControllerBlockEntity controller) {
        super((MenuType)SporeCannonsMenus.WHITEOUT_ASSEMBLY_CONTROLLER.get(), id);
        this.controller = controller;
        AssemblyKind assemblyKind = this.kind = controller == null ? AssemblyKind.PROJECTOR : controller.kind();
        if (controller != null) {
            this.addControllerSlots(controller);
        }
        this.addPlayerInventory(inventory);
        this.syncedFormed = controller != null && controller.formed() ? 1 : 0;
        this.syncedWater = controller == null ? 0 : controller.waterAmount();
        final boolean client = inventory.player.level().isClientSide;
        this.addDataSlot(new DataSlot(){

            public int get() {
                return !client && WhiteoutAssemblyControllerMenu.this.controller != null && WhiteoutAssemblyControllerMenu.this.controller.formed() ? 1 : WhiteoutAssemblyControllerMenu.this.syncedFormed;
            }

            public void set(int value) {
                WhiteoutAssemblyControllerMenu.this.syncedFormed = value;
            }
        });
        this.addDataSlot(new DataSlot(){

            public int get() {
                return !client && WhiteoutAssemblyControllerMenu.this.controller != null ? WhiteoutAssemblyControllerMenu.this.controller.waterAmount() : WhiteoutAssemblyControllerMenu.this.syncedWater;
            }

            public void set(int value) {
                WhiteoutAssemblyControllerMenu.this.syncedWater = value;
            }
        });
    }

    private void addControllerSlots(WhiteoutAssemblyControllerBlockEntity controller) {
        if (controller.kind() == AssemblyKind.PROJECTOR) {
            int slot;
            for (slot = 0; slot < 6; ++slot) {
                this.addSlot((Slot)new SlotItemHandler((IItemHandler)controller.inventory(), slot, 35 + slot * 18, 42));
            }
            for (slot = 6; slot < 12; ++slot) {
                this.addSlot((Slot)new SlotItemHandler((IItemHandler)controller.inventory(), slot, 35 + (slot - 6) * 18, 70));
            }
        } else {
            int slot;
            for (slot = 0; slot < 6; ++slot) {
                this.addSlot((Slot)new SlotItemHandler((IItemHandler)controller.inventory(), slot, 26 + slot * 18, 42));
            }
            for (slot = 6; slot < 9; ++slot) {
                this.addSlot((Slot)new SlotItemHandler((IItemHandler)controller.inventory(), slot, 53 + (slot - 6) * 18, 70));
            }
            for (slot = 9; slot < 12; ++slot) {
                this.addSlot((Slot)new SlotItemHandler((IItemHandler)controller.inventory(), slot, 125 + (slot - 9) * 18, 70));
            }
        }
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)inventory, col + row * 9 + 9, 26 + col * 18, 116 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)inventory, col, 26 + col * 18, 174));
        }
    }

    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = (Slot)this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < 12 ? !this.moveItemStackTo(stack, 12, this.slots.size(), true) : !this.moveItemStackTo(stack, 0, 12, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    public boolean stillValid(Player player) {
        return this.controller != null && !this.controller.isRemoved() && player.distanceToSqr(this.controller.getBlockPos().getCenter()) <= 64.0;
    }

    public WhiteoutAssemblyControllerBlockEntity controller() {
        return this.controller;
    }

    public AssemblyKind kind() {
        return this.kind;
    }

    public boolean formed() {
        return this.syncedFormed != 0;
    }

    public int waterAmount() {
        return this.syncedWater;
    }
}

