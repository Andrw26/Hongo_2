/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.ResultContainer
 *  net.minecraft.world.inventory.ResultSlot
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.inventory.TransientCraftingContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingInput
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.elvoux.sporecannons.facility;

import com.elvoux.sporecannons.facility.FacilityKind;
import com.elvoux.sporecannons.facility.FacilityStorageBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsMenus;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class FacilityMenu
extends AbstractContainerMenu {
    private static final int[][] TECHNICAL_POSITIONS = new int[][]{{25, 31}, {43, 31}, {61, 31}, {79, 31}, {97, 31}, {115, 31}, {133, 31}, {25, 49}, {133, 49}, {25, 67}, {133, 67}, {25, 85}, {133, 85}, {25, 103}, {79, 103}, {133, 103}};
    private final FacilityStorageBlockEntity furniture;
    private final FacilityKind kind;
    private final Player player;
    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots;
    private int machineSlotCount;

    public FacilityMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(id, playerInventory, (FacilityStorageBlockEntity)playerInventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public FacilityMenu(int id, Inventory playerInventory, FacilityStorageBlockEntity furniture) {
        super((MenuType)SporeCannonsMenus.FACILITY_FURNITURE.get(), id);
        this.furniture = furniture;
        this.kind = furniture == null ? FacilityKind.STORAGE : furniture.kind();
        this.player = playerInventory.player;
        this.craftSlots = this.kind == FacilityKind.WORKBENCH ? new TransientCraftingContainer((AbstractContainerMenu)this, 3, 3) : null;
        this.resultSlots = this.kind == FacilityKind.WORKBENCH ? new ResultContainer() : null;
        this.addMachineSlots();
        this.addPlayerInventory(playerInventory, 47, 147);
    }

    private void addMachineSlots() {
        if (this.furniture == null) {
            return;
        }
        switch (this.kind) {
            case STORAGE: {
                for (int row = 0; row < 3; ++row) {
                    for (int col = 0; col < 4; ++col) {
                        this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.furniture.inventory(), col + row * 4, 92 + col * 18, 38 + row * 18));
                    }
                }
                this.machineSlotCount = 12;
                break;
            }
            case TECHNICAL: {
                for (int slot = 0; slot < TECHNICAL_POSITIONS.length; ++slot) {
                    int[] pos = TECHNICAL_POSITIONS[slot];
                    this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.furniture.inventory(), slot, pos[0], pos[1]));
                }
                this.machineSlotCount = 16;
                break;
            }
            case WORKBENCH: {
                int slot;
                this.addSlot((Slot)new ResultSlot(this.player, this.craftSlots, (Container)this.resultSlots, 0, 119, 33));
                for (int row = 0; row < 3; ++row) {
                    for (int col = 0; col < 3; ++col) {
                        this.addSlot(new Slot((Container)this.craftSlots, col + row * 3, 101 + col * 18, 62 + row * 18));
                    }
                }
                for (slot = 0; slot < 4; ++slot) {
                    this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.furniture.inventory(), slot, 29, 44 + slot * 18));
                }
                for (slot = 4; slot < 8; ++slot) {
                    this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.furniture.inventory(), slot, 209, 44 + (slot - 4) * 18));
                }
                this.machineSlotCount = 18;
                break;
            }
            case TERMINAL: {
                this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.furniture.inventory(), 0, 24, 48));
                this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.furniture.inventory(), 1, 24, 78));
                this.machineSlotCount = 2;
            }
        }
    }

    private void addPlayerInventory(Inventory inventory, int x, int y) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)inventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)inventory, col, x + col * 18, y + 58));
        }
    }

    public void slotsChanged(Container container) {
        if (this.kind == FacilityKind.WORKBENCH && this.craftSlots != null) {
            this.updateCraftingResult();
        }
        super.slotsChanged(container);
    }

    private void updateCraftingResult() {
        ItemStack assembled;
        Player player;
        Level level = this.player.level();
        if (level.isClientSide || !((player = this.player) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        CraftingInput input = this.craftSlots.asCraftInput();
        ItemStack result = ItemStack.EMPTY;
        Optional recipe = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, (RecipeInput)input, level);
        if (recipe.isPresent() && this.resultSlots.setRecipeUsed(level, serverPlayer, (RecipeHolder)recipe.get()) && (assembled = ((CraftingRecipe)((RecipeHolder)recipe.get()).value()).assemble((RecipeInput)input, (HolderLookup.Provider)level.registryAccess())).isItemEnabled(level.enabledFeatures())) {
            result = assembled;
        }
        this.resultSlots.setItem(0, result);
        this.setRemoteSlot(0, result);
        serverPlayer.connection.send((Packet)new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, result));
    }

    public void removed(Player player) {
        super.removed(player);
        if (this.craftSlots != null) {
            this.clearContainer(player, (Container)this.craftSlots);
        }
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
        int playerStart = this.machineSlotCount;
        if (this.kind == FacilityKind.WORKBENCH && index == 0) {
            if (!this.moveItemStackTo(stack, playerStart, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copy);
        } else if (index < playerStart ? !this.moveItemStackTo(stack, playerStart, this.slots.size(), true) : !this.moveItemStackTo(stack, this.kind == FacilityKind.WORKBENCH ? 10 : 0, playerStart, false) && (index < (inventoryEnd = playerStart + 27) ? !this.moveItemStackTo(stack, inventoryEnd, this.slots.size(), false) : !this.moveItemStackTo(stack, playerStart, inventoryEnd, false))) {
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
        return this.furniture != null && !this.furniture.isRemoved() && player.distanceToSqr(this.furniture.getBlockPos().getCenter()) <= 64.0;
    }

    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return this.resultSlots == null || slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    public FacilityStorageBlockEntity furniture() {
        return this.furniture;
    }

    public FacilityKind kind() {
        return this.kind;
    }
}

