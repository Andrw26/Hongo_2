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
package com.elvoux.sporecannons.industry;

import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlockEntity;
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

public final class WhiteoutIndustryMachineMenu
extends AbstractContainerMenu {
    private final WhiteoutIndustryMachineBlockEntity machine;

    public WhiteoutIndustryMachineMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(id, inventory, (WhiteoutIndustryMachineBlockEntity)inventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public WhiteoutIndustryMachineMenu(int id, Inventory inventory, WhiteoutIndustryMachineBlockEntity machine) {
        super((MenuType)SporeCannonsMenus.WHITEOUT_INDUSTRY_MACHINE.get(), id);
        this.machine = machine;
        if (machine != null) {
            this.addSlot((Slot)new SlotItemHandler((IItemHandler)machine.inventory(), 0, 48, 58));
            this.addSlot((Slot)new SlotItemHandler((IItemHandler)machine.inventory(), 1, 88, 58));
            this.addSlot((Slot)new SlotItemHandler((IItemHandler)machine.inventory(), 2, 144, 58));
        }
        this.addPlayerInventory(inventory);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)inventory, col + row * 9 + 9, 16 + col * 18, 124 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)inventory, col, 16 + col * 18, 182));
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
        if (index < 3 ? !this.moveItemStackTo(stack, 3, this.slots.size(), true) : !this.moveItemStackTo(stack, 0, 2, false)) {
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
        return this.machine != null && !this.machine.isRemoved() && player.distanceToSqr(this.machine.getBlockPos().getCenter()) <= 64.0;
    }

    public WhiteoutIndustryMachineBlockEntity machine() {
        return this.machine;
    }
}

