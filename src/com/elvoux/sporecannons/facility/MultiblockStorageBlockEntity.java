/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
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
 *  net.minecraft.world.level.LevelAccessor
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
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.elvoux.sporecannons.facility;

import com.elvoux.sporecannons.facility.MultiblockStorageBlock;
import com.elvoux.sporecannons.facility.MultiblockStorageMenu;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.LevelAccessor;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class MultiblockStorageBlockEntity
extends BlockEntity
implements MenuProvider {
    private ResourceKey<LootTable> lootTable;
    private long lootTableSeed;
    private final ResizableInventory inventory = new ResizableInventory(48){

        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot >= MultiblockStorageBlockEntity.this.slotsPerModule()) {
                return false;
            }
            if (MultiblockStorageBlockEntity.this.kind() == MultiblockStorageBlock.StorageKind.CRYOGEL) {
                return stack.is((Item)SporeCannonsItems.STABILIZED_CRYOGEL.get());
            }
            return BuiltInRegistries.ITEM.getKey((Object)stack.getItem()).getNamespace().equals("spore");
        }

        protected void onContentsChanged(int slot) {
            MultiblockStorageBlockEntity.this.setChanged();
        }
    };
    private final IItemHandler automationHandler = new StructureItemHandler();

    public MultiblockStorageBlockEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)SporeCannonsBlockEntities.MULTIBLOCK_STORAGE.get(), pos, state);
    }

    public MultiblockStorageBlock.StorageKind kind() {
        MultiblockStorageBlock.StorageKind storageKind;
        Block block = this.getBlockState().getBlock();
        if (block instanceof MultiblockStorageBlock) {
            MultiblockStorageBlock block2 = (MultiblockStorageBlock)block;
            storageKind = block2.kind();
        } else {
            storageKind = MultiblockStorageBlock.StorageKind.SAMPLE;
        }
        return storageKind;
    }

    public int slotsPerModule() {
        return this.kind().slotsPerModule();
    }

    public int structureSize() {
        return (Integer)this.getBlockState().getValue((Property)MultiblockStorageBlock.SIZE);
    }

    public ItemStackHandler inventory() {
        this.unpackLootTable(null);
        return this.inventory;
    }

    public IItemHandler automationHandler() {
        return this.automationHandler;
    }

    public List<MultiblockStorageBlockEntity> members() {
        ArrayList<MultiblockStorageBlockEntity> result = new ArrayList<MultiblockStorageBlockEntity>();
        if (this.level == null) {
            return List.of((Object)((Object)this));
        }
        for (BlockPos pos : MultiblockStorageBlock.structurePositions((LevelAccessor)this.level, this.worldPosition)) {
            MultiblockStorageBlockEntity member;
            BlockEntity blockEntity = this.level.getBlockEntity(pos);
            if (!(blockEntity instanceof MultiblockStorageBlockEntity) || (member = (MultiblockStorageBlockEntity)blockEntity).kind() != this.kind()) continue;
            result.add(member);
        }
        return result.isEmpty() ? List.of((Object)((Object)this)) : result;
    }

    public void dropLocalContents() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        this.unpackLootTable(null);
        for (int slot = 0; slot < this.slotsPerModule(); ++slot) {
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
        for (MultiblockStorageBlockEntity member : this.members()) {
            member.unpackLootTable(player);
        }
        return new MultiblockStorageMenu(id, playerInventory, this);
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
            for (int slot = 0; slot < this.slotsPerModule() && !remaining.isEmpty(); ++slot) {
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
            this.inventory.growPreserving(48);
        }
    }

    private static class ResizableInventory
    extends ItemStackHandler {
        ResizableInventory(int size) {
            super(size);
        }

        void growPreserving(int size) {
            int slot;
            if (this.getSlots() >= size) {
                return;
            }
            ArrayList<ItemStack> saved = new ArrayList<ItemStack>(this.getSlots());
            for (slot = 0; slot < this.getSlots(); ++slot) {
                saved.add(this.getStackInSlot(slot).copy());
            }
            this.setSize(size);
            for (slot = 0; slot < saved.size(); ++slot) {
                this.setStackInSlot(slot, (ItemStack)saved.get(slot));
            }
        }
    }

    private final class StructureItemHandler
    implements IItemHandler {
        private StructureItemHandler() {
        }

        private List<MultiblockStorageBlockEntity> currentMembers() {
            List<MultiblockStorageBlockEntity> current = MultiblockStorageBlockEntity.this.members();
            for (MultiblockStorageBlockEntity member : current) {
                member.unpackLootTable(null);
            }
            return current;
        }

        private int totalSlots(List<MultiblockStorageBlockEntity> current) {
            return current.size() * MultiblockStorageBlockEntity.this.slotsPerModule();
        }

        private boolean valid(int slot, List<MultiblockStorageBlockEntity> current) {
            return slot >= 0 && slot < this.totalSlots(current);
        }

        private MultiblockStorageBlockEntity owner(int slot, List<MultiblockStorageBlockEntity> current) {
            return current.get(slot / MultiblockStorageBlockEntity.this.slotsPerModule());
        }

        private int localSlot(int slot) {
            return slot % MultiblockStorageBlockEntity.this.slotsPerModule();
        }

        public int getSlots() {
            return this.totalSlots(this.currentMembers());
        }

        public ItemStack getStackInSlot(int slot) {
            List<MultiblockStorageBlockEntity> current = this.currentMembers();
            return this.valid(slot, current) ? this.owner((int)slot, current).inventory.getStackInSlot(this.localSlot(slot)) : ItemStack.EMPTY;
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            List<MultiblockStorageBlockEntity> current = this.currentMembers();
            return this.valid(slot, current) ? this.owner((int)slot, current).inventory.insertItem(this.localSlot(slot), stack, simulate) : stack;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            List<MultiblockStorageBlockEntity> current = this.currentMembers();
            return this.valid(slot, current) ? this.owner((int)slot, current).inventory.extractItem(this.localSlot(slot), amount, simulate) : ItemStack.EMPTY;
        }

        public int getSlotLimit(int slot) {
            List<MultiblockStorageBlockEntity> current = this.currentMembers();
            return this.valid(slot, current) ? this.owner((int)slot, current).inventory.getSlotLimit(this.localSlot(slot)) : 0;
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            List<MultiblockStorageBlockEntity> current = this.currentMembers();
            return this.valid(slot, current) && this.owner((int)slot, current).inventory.isItemValid(this.localSlot(slot), stack);
        }
    }
}

