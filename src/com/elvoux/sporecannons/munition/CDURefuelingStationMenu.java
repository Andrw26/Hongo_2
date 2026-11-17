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
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CDURefuelingStationBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class CDURefuelingStationMenu
extends AbstractContainerMenu {
    private final CDURefuelingStationBlockEntity station;

    public CDURefuelingStationMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(id, inventory, (CDURefuelingStationBlockEntity)inventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public CDURefuelingStationMenu(int id, Inventory inventory, CDURefuelingStationBlockEntity station) {
        super((MenuType)SporeCannonsMenus.CDU_REFUELING_STATION.get(), id);
        this.station = station;
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)station.inventory(), 0, 52, 28));
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)station.inventory(), 1, 122, 52));
        this.addPlayerInventory(inventory);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)inventory, col + row * 9 + 9, 16 + col * 18, 104 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)inventory, col, 16 + col * 18, 162));
        }
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (!slot.hasItem()) {
            return empty;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < 2 ? !this.moveItemStackTo(stack, 2, this.slots.size(), true) : !this.moveItemStackTo(stack, 0, 2, false)) {
            return empty;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    public boolean stillValid(Player player) {
        return this.station != null && !this.station.isRemoved() && player.distanceToSqr(this.station.getBlockPos().getCenter()) <= 64.0;
    }

    public CDURefuelingStationBlockEntity station() {
        return this.station;
    }
}

