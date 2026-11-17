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

import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
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

public final class CryogenicContainmentProjectorMenu
extends AbstractContainerMenu {
    private final CryogenicContainmentProjectorBlockEntity projector;

    public CryogenicContainmentProjectorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(id, inventory, (CryogenicContainmentProjectorBlockEntity)inventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public CryogenicContainmentProjectorMenu(int id, Inventory inventory, CryogenicContainmentProjectorBlockEntity projector) {
        super((MenuType)SporeCannonsMenus.CRYOGENIC_CONTAINMENT_PROJECTOR.get(), id);
        this.projector = projector;
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)projector.inventory(), 0, 89, 47));
        this.addPlayerInventory(inventory);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)inventory, col + row * 9 + 9, 16 + col * 18, 112 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)inventory, col, 16 + col * 18, 170));
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
        if (index == 0 ? !this.moveItemStackTo(stack, 1, this.slots.size(), true) : !this.moveItemStackTo(stack, 0, 1, false)) {
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
        return this.projector != null && !this.projector.isRemoved() && player.distanceToSqr(this.projector.getBlockPos().getCenter()) <= 64.0;
    }

    public boolean clickMenuButton(Player player, int id) {
        if (id != 0 && id != 1 || !this.stillValid(player)) {
            return false;
        }
        if (!player.level().isClientSide) {
            if (id == 0) {
                this.projector.cycleFieldMode();
            } else {
                this.projector.toggleCleanupMode();
            }
        }
        return true;
    }

    public CryogenicContainmentProjectorBlockEntity projector() {
        return this.projector;
    }
}

