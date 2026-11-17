/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.Containers
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.elvoux.sporecannons.facility;

import com.elvoux.sporecannons.facility.FacilityInteractiveBlock;
import com.elvoux.sporecannons.facility.FacilityKind;
import com.elvoux.sporecannons.facility.FacilityMenu;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class FacilityStorageBlockEntity
extends BlockEntity
implements MenuProvider {
    private ResourceKey<LootTable> lootTable;
    private long lootTableSeed;
    private final ItemStackHandler inventory = new ItemStackHandler(16){

        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot >= FacilityStorageBlockEntity.this.kind().slots()) {
                return false;
            }
            if (FacilityStorageBlockEntity.this.kind() != FacilityKind.TERMINAL) {
                return true;
            }
            return slot == 0 ? FacilityStorageBlockEntity.isMemoryCard(stack) : slot == 1 && stack.is((Item)SporeCannonsItems.CDU_FIELD_SCANNER.get());
        }

        public int getSlotLimit(int slot) {
            return FacilityStorageBlockEntity.this.kind() == FacilityKind.TERMINAL ? 1 : 64;
        }

        protected void onContentsChanged(int slot) {
            FacilityStorageBlockEntity.this.contentsChanged();
        }
    };

    public FacilityStorageBlockEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)SporeCannonsBlockEntities.FACILITY_STORAGE.get(), pos, state);
    }

    public FacilityKind kind() {
        FacilityKind facilityKind;
        Block block = this.getBlockState().getBlock();
        if (block instanceof FacilityInteractiveBlock) {
            FacilityInteractiveBlock block2 = (FacilityInteractiveBlock)block;
            facilityKind = block2.kind();
        } else {
            facilityKind = FacilityKind.STORAGE;
        }
        return facilityKind;
    }

    public ItemStackHandler inventory() {
        this.unpackLootTable(null);
        return this.inventory;
    }

    private static boolean isMemoryCard(ItemStack stack) {
        return stack.is((Item)SporeCannonsItems.MEMORY_CARD_LOGISTICS.get()) || stack.is((Item)SporeCannonsItems.MEMORY_CARD_CONTAINMENT.get()) || stack.is((Item)SporeCannonsItems.MEMORY_CARD_PERSONNEL.get()) || stack.is((Item)SporeCannonsItems.MEMORY_CARD_INCIDENT.get()) || stack.is((Item)SporeCannonsItems.MEMORY_CARD_DIRECTIVE.get());
    }

    public int comparatorSignal() {
        this.unpackLootTable(null);
        int slots = this.kind().slots();
        float fullness = 0.0f;
        for (int slot = 0; slot < slots; ++slot) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            fullness += (float)stack.getCount() / (float)Math.min(this.inventory.getSlotLimit(slot), stack.getMaxStackSize());
        }
        return slots == 0 ? 0 : (int)Math.floor(fullness / (float)slots * 14.0f) + (fullness > 0.0f ? 1 : 0);
    }

    private void contentsChanged() {
        this.setChanged();
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        BlockState state = this.getBlockState();
        if (this.kind() == FacilityKind.TERMINAL) {
            boolean lit;
            boolean bl = lit = !this.inventory.getStackInSlot(0).isEmpty() && !this.inventory.getStackInSlot(1).isEmpty();
            if ((Boolean)state.getValue((Property)FacilityInteractiveBlock.LIT) != lit) {
                this.level.setBlock(this.worldPosition, (BlockState)state.setValue((Property)FacilityInteractiveBlock.LIT, (Comparable)Boolean.valueOf(lit)), 3);
                state = this.getBlockState();
            }
        }
        this.level.sendBlockUpdated(this.worldPosition, state, state, 2);
        this.level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
    }

    public void dropContents() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        this.unpackLootTable(null);
        for (int slot = 0; slot < this.kind().slots(); ++slot) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack((Level)this.level, (double)((double)this.worldPosition.getX() + 0.5), (double)((double)this.worldPosition.getY() + 0.5), (double)((double)this.worldPosition.getZ() + 0.5), (ItemStack)stack.copy());
            }
            this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        this.unpackLootTable(player);
        return new FacilityMenu(id, playerInventory, this);
    }

    private void unpackLootTable(Player player) {
        Level level;
        if (this.lootTable == null || !((level = this.level) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(this.lootTable);
        LootParams.Builder params = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, (Object)Vec3.atCenterOf((Vec3i)this.worldPosition));
        if (player != null) {
            params.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, (Object)player);
        }
        this.lootTable = null;
        for (ItemStack generated : table.getRandomItems(params.create(LootContextParamSets.CHEST), this.lootTableSeed)) {
            ItemStack remaining = generated.copy();
            for (int slot = 0; slot < this.kind().slots() && !remaining.isEmpty(); ++slot) {
                remaining = this.inventory.insertItem(slot, remaining, false);
            }
        }
        this.setChanged();
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.lootTable != null) {
            tag.putString("LootTable", this.lootTable.location().toString());
            if (this.lootTableSeed != 0L) {
                tag.putLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            tag.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        }
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.lootTable = null;
        this.lootTableSeed = 0L;
        if (tag.contains("LootTable")) {
            ResourceLocation id = ResourceLocation.tryParse((String)tag.getString("LootTable"));
            if (id != null) {
                this.lootTable = ResourceKey.create((ResourceKey)Registries.LOOT_TABLE, (ResourceLocation)id);
            }
            this.lootTableSeed = tag.getLong("LootTableSeed");
        } else if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }
}

