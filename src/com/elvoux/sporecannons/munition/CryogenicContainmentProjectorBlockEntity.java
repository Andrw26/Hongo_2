/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Harbinger.Spore.Sentities.Utility.InfectionTendril
 *  com.Harbinger.Spore.Sentities.Utility.ScentEntity
 *  com.Harbinger.Spore.core.Seffects
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Containers
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.elvoux.sporecannons.munition;

import com.Harbinger.Spore.Sentities.Utility.InfectionTendril;
import com.Harbinger.Spore.Sentities.Utility.ScentEntity;
import com.Harbinger.Spore.core.Seffects;
import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.field.ActiveContainmentFields;
import com.elvoux.sporecannons.field.WhiteoutTargeting;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerBlockEntity;
import com.elvoux.sporecannons.munition.CDUCoolantCartridgeItem;
import com.elvoux.sporecannons.munition.CDUFieldTerminal;
import com.elvoux.sporecannons.munition.ContainmentFieldGeometry;
import com.elvoux.sporecannons.munition.ContainmentFieldMode;
import com.elvoux.sporecannons.munition.ContainmentProjectorRenderRegistry;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlock;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorMenu;
import com.elvoux.sporecannons.munition.CryogenicSaturationManager;
import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.munition.WhiteoutKineticScaling;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import com.elvoux.sporecannons.registry.SporeCannonsSounds;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import java.util.UUID;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public final class CryogenicContainmentProjectorBlockEntity
extends KineticBlockEntity
implements MenuProvider {
    public static final float MINIMUM_SPEED = 32.0f;
    public static final int BASE_FIELD_RADIUS = 16;
    public static final int CARTRIDGE_TICKS = 54000;
    private static final int LEGACY_CARTRIDGE_TICKS = 18000;
    private static final int BLOCK_CHECKS_PER_SECOND = 96;
    private final ItemStackHandler inventory = new ItemStackHandler(1){

        public int getSlotLimit(int slot) {
            return 1;
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get());
        }

        protected void onContentsChanged(int slot) {
            CryogenicContainmentProjectorBlockEntity.this.inventoryChanged();
        }
    };
    private final IItemHandler topInput = new SidedProjectorHandler(true, false);
    private final IItemHandler sideOutput = new SidedProjectorHandler(false, true);
    private final IItemHandler unsidedAutomation = new SidedProjectorHandler(true, true);
    private int coolantTicks;
    private double coolantFraction;
    private double blockCheckFraction;
    private int activeFieldTicks;
    private int scanCursor;
    private long parasitesSuppressed;
    private long signalsRemoved;
    private long blocksCleaned;
    private UUID operator;
    private boolean awardedActivation;
    private ContainmentFieldMode fieldMode = ContainmentFieldMode.RADIAL;
    private boolean cleanupEnabled = true;
    private String lastReportedStatus = "";
    private int coolantWarningStage;

    public CryogenicContainmentProjectorBlockEntity(BlockPos pos, BlockState state) {
        this((BlockEntityType)SporeCannonsBlockEntities.CRYOGENIC_CONTAINMENT_PROJECTOR.get(), pos, state);
    }

    public CryogenicContainmentProjectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public float calculateStressApplied() {
        return 48.0f;
    }

    public void tick() {
        super.tick();
        if (this.level != null && this.level.isClientSide) {
            ContainmentProjectorRenderRegistry.track(this);
            return;
        }
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        if (this.coolantTicks <= 0) {
            WhiteoutAssemblyControllerBlockEntity.tryHotSwap(this);
        }
        boolean active = this.coolantTicks > 0 && this.isPowered();
        boolean wasActive = this.getBlockState().hasProperty((Property)CryogenicContainmentProjectorBlock.ACTIVE) && (Boolean)this.getBlockState().getValue((Property)CryogenicContainmentProjectorBlock.ACTIVE) != false;
        this.updateActiveState(active);
        if (active && !wasActive) {
            server.playSound(null, this.worldPosition, (SoundEvent)SporeCannonsSounds.CONTAINMENT_PROJECTOR_STARTUP.get(), SoundSource.BLOCKS, 0.9f, 1.0f);
        }
        this.reportStatusIfChanged(server);
        if (!active) {
            ActiveContainmentFields.remove(server, this.worldPosition);
            return;
        }
        ContainmentFieldGeometry geometry = this.fieldGeometry();
        ActiveContainmentFields.track(server, this.worldPosition, geometry);
        this.enforcePhysicalContainment(server, geometry);
        this.consumeCoolant();
        if (this.coolantTicks == 0) {
            WhiteoutAssemblyControllerBlockEntity.tryHotSwap(this);
        }
        this.reportCoolantWarning(server);
        if (!this.awardedActivation) {
            ServerPlayer player;
            ServerPlayer serverPlayer = player = this.operator == null ? null : server.getServer().getPlayerList().getPlayer(this.operator);
            if (player != null) {
                ((PlayerTrigger)SporeCannonsCriteria.PROJECTOR_ACTIVATED.get()).trigger(player);
            }
            this.awardedActivation = true;
        }
        this.activeFieldTicks = (this.activeFieldTicks + 1) % 20;
        this.operateField(server, geometry);
        if (this.coolantTicks == 0) {
            this.coolantFraction = 0.0;
            this.activeFieldTicks = 0;
            this.updateActiveState(false);
            this.inventory.setStackInSlot(0, new ItemStack((ItemLike)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()));
            server.playSound(null, this.worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.7f, 1.2f);
            this.reportStatusIfChanged(server);
        } else if (server.getGameTime() % 20L == 0L) {
            this.setChanged();
            this.sendData();
        }
    }

    private void consumeCoolant() {
        this.coolantFraction += this.coolantConsumptionMultiplier();
        int consumed = (int)Math.floor(this.coolantFraction + 1.0E-9);
        if (consumed <= 0) {
            return;
        }
        this.coolantFraction -= (double)consumed;
        this.coolantTicks = Math.max(0, this.coolantTicks - consumed);
        ItemStack cartridge = this.inventory.getStackInSlot(0);
        if (cartridge.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get())) {
            CDUCoolantCartridgeItem.setRemaining(cartridge, this.coolantTicks);
        }
    }

    private boolean isPowered() {
        return Math.abs(this.getSpeed()) >= 32.0f && !this.isOverStressed();
    }

    private void updateActiveState(boolean active) {
        BlockState state = this.getBlockState();
        if (!state.hasProperty((Property)CryogenicContainmentProjectorBlock.ACTIVE) || (Boolean)state.getValue((Property)CryogenicContainmentProjectorBlock.ACTIVE) == active) {
            return;
        }
        this.level.setBlock(this.worldPosition, (BlockState)state.setValue((Property)CryogenicContainmentProjectorBlock.ACTIVE, (Comparable)Boolean.valueOf(active)), 2);
    }

    private void reportStatusIfChanged(ServerLevel server) {
        String status = this.statusKey();
        if (status.equals(this.lastReportedStatus)) {
            return;
        }
        String previous = this.lastReportedStatus;
        this.lastReportedStatus = status;
        this.setChanged();
        if (!previous.isEmpty() && status.equals("depleted") && !previous.equals("depleted")) {
            CDUFieldTerminal.projectorStatus(server, this.worldPosition, status, Math.round(Math.abs(this.getSpeed())), this.fieldMode == ContainmentFieldMode.WALL ? (int)Math.round(this.wallWidth()) : (int)Math.round(this.currentFieldRadius()), this.estimatedRuntimeSeconds());
        }
    }

    private void reportCoolantWarning(ServerLevel server) {
        if (this.coolantTicks <= 5400 && this.coolantWarningStage < 2) {
            this.coolantWarningStage = 2;
            CDUFieldTerminal.projectorCoolantWarning(server, this.worldPosition, 10, this.estimatedRuntimeSeconds(), Math.round(Math.abs(this.getSpeed())));
            this.setChanged();
        } else if (this.coolantTicks <= 13500 && this.coolantWarningStage < 1) {
            this.coolantWarningStage = 1;
            CDUFieldTerminal.projectorCoolantWarning(server, this.worldPosition, 25, this.estimatedRuntimeSeconds(), Math.round(Math.abs(this.getSpeed())));
            this.setChanged();
        }
    }

    private void operateField(ServerLevel server, ContainmentFieldGeometry geometry) {
        int phase = Math.floorMod(this.worldPosition.hashCode(), 20);
        if (Math.floorMod(server.getGameTime() + (long)phase, 20L) == 0L) {
            this.suppressFieldBiology(server, geometry);
        }
        if (this.cleanupEnabled) {
            double coverageScale = this.fieldMode == ContainmentFieldMode.RADIAL ? geometry.extent() * geometry.extent() / 256.0 : geometry.width() / 32.0;
            this.blockCheckFraction += 96.0 * coverageScale / 20.0;
            int blockChecks = (int)Math.floor(this.blockCheckFraction);
            this.blockCheckFraction -= (double)blockChecks;
            this.scanBlocks(server, geometry, blockChecks);
        } else {
            this.blockCheckFraction = 0.0;
        }
        if (Math.floorMod(server.getGameTime() + (long)phase, 100L) == 0L) {
            server.playSound(null, this.worldPosition, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.25f, 1.65f);
        }
    }

    private void suppressFieldBiology(ServerLevel server, ContainmentFieldGeometry geometry) {
        AABB bounds = geometry.bounds(this.worldPosition);
        for (LivingEntity target : server.getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity.isAlive() && WhiteoutTargeting.isAffected((Entity)entity))) {
            if (!geometry.contains(this.worldPosition, target.getX(), target.getY(), target.getZ())) continue;
            CryogenicSaturationManager.add(target, 7.5, null);
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), 100));
            target.addEffect(new MobEffectInstance(Seffects.FROSTBITE, 60, 0));
            ++this.parasitesSuppressed;
        }
        for (ScentEntity scent : server.getEntitiesOfClass(ScentEntity.class, bounds)) {
            if (!geometry.contains(this.worldPosition, scent.getX(), scent.getY(), scent.getZ())) continue;
            scent.discard();
            ++this.signalsRemoved;
        }
        for (InfectionTendril tendril : server.getEntitiesOfClass(InfectionTendril.class, bounds)) {
            if (!geometry.contains(this.worldPosition, tendril.getX(), tendril.getY(), tendril.getZ())) continue;
            tendril.discard();
            ++this.signalsRemoved;
        }
        this.setChanged();
    }

    private void scanBlocks(ServerLevel server, ContainmentFieldGeometry geometry, int blockChecks) {
        int volume = geometry.scanVolume();
        for (int i = 0; i < blockChecks; ++i) {
            BlockState current;
            int index = Math.floorMod(this.scanCursor++, volume);
            BlockPos offset = geometry.scanOffset(index);
            BlockPos targetPos = this.worldPosition.offset((Vec3i)offset);
            if (!geometry.containsBlockOffset(offset) || !server.hasChunkAt(targetPos) || WhiteoutTargeting.isBlockImmune(current = server.getBlockState(targetPos))) continue;
            BlockState cleaned = FreezeShellProjectile.cleanedState(current);
            if (cleaned == null && WhiteoutTargeting.isExplicitlyCleanable(current)) {
                BlockState blockState = cleaned = current.getFluidState().isEmpty() ? Blocks.AIR.defaultBlockState() : current.getFluidState().createLegacyBlock();
            }
            if (cleaned == null || !server.setBlock(targetPos, cleaned, 3)) continue;
            ++this.blocksCleaned;
            this.setChanged();
        }
    }

    private void enforcePhysicalContainment(ServerLevel server, ContainmentFieldGeometry geometry) {
        AABB query = geometry.bounds(this.worldPosition).inflate(2.0);
        for (LivingEntity target : server.getEntitiesOfClass(LivingEntity.class, query, entity -> entity.isAlive() && WhiteoutTargeting.isAffected((Entity)entity))) {
            boolean rejected;
            double oldX = target.xOld;
            double oldY = target.yOld;
            double oldZ = target.zOld;
            boolean bl = rejected = geometry.mode() == ContainmentFieldMode.RADIAL ? this.rejectRadialEntry(target, geometry, oldX, oldY, oldZ) : this.rejectWallCrossing(target, geometry, oldX, oldY, oldZ);
            if (!rejected) continue;
            target.hurt(target.damageSources().freeze(), 1.0f);
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), 80));
            target.addEffect(new MobEffectInstance(Seffects.FROSTBITE, 40, 0));
            CryogenicSaturationManager.add(target, 4.0, null);
            ++this.parasitesSuppressed;
        }
    }

    private boolean rejectRadialEntry(LivingEntity target, ContainmentFieldGeometry geometry, double oldX, double oldY, double oldZ) {
        double cx = (double)this.worldPosition.getX() + 0.5;
        double cy = (double)this.worldPosition.getY() + 0.7;
        double cz = (double)this.worldPosition.getZ() + 0.5;
        double radius = geometry.extent();
        double oldDx = oldX - cx;
        double oldDy = oldY - cy;
        double oldDz = oldZ - cz;
        double newDx = target.getX() - cx;
        double newDy = target.getY() - cy;
        double newDz = target.getZ() - cz;
        double oldDistance = oldDx * oldDx + oldDy * oldDy + oldDz * oldDz;
        double newDistance = newDx * newDx + newDy * newDy + newDz * newDz;
        if (oldDistance < radius * radius || newDistance >= radius * radius) {
            return false;
        }
        Vec3 outward = new Vec3(oldDx, oldDy, oldDz);
        if (outward.lengthSqr() < 1.0E-6) {
            outward = new Vec3(0.0, 0.0, 1.0);
        }
        outward = outward.normalize();
        double safeRadius = radius + (double)target.getBbWidth() * 0.5 + 0.15;
        target.teleportTo(cx + outward.x * safeRadius, cy + outward.y * safeRadius, cz + outward.z * safeRadius);
        target.setDeltaMovement(outward.scale(0.48).add(0.0, 0.08, 0.0));
        target.hasImpulse = true;
        return true;
    }

    private boolean rejectWallCrossing(LivingEntity target, ContainmentFieldGeometry geometry, double oldX, double oldY, double oldZ) {
        boolean crossedThrough;
        double cx = (double)this.worldPosition.getX() + 0.5;
        double cy = (double)this.worldPosition.getY() + 0.7;
        double cz = (double)this.worldPosition.getZ() + 0.5;
        boolean normalIsZ = geometry.facing().getAxis() == Direction.Axis.Z;
        double oldNormal = normalIsZ ? oldZ - cz : oldX - cx;
        double newNormal = normalIsZ ? target.getZ() - cz : target.getX() - cx;
        double oldLateral = normalIsZ ? oldX - cx : oldZ - cz;
        double newLateral = normalIsZ ? target.getX() - cx : target.getZ() - cz;
        double halfDepth = (double)geometry.wallDepth() / 2.0;
        double margin = (double)target.getBbWidth() * 0.5 + 0.12;
        boolean reachesFace = Math.min(Math.abs(oldLateral), Math.abs(newLateral)) <= geometry.extent() + 0.5 + margin && Math.min(Math.abs(oldY - cy), Math.abs(target.getY() - cy)) <= (double)geometry.wallHeight() / 2.0 + (double)target.getBbHeight();
        boolean enteredSlab = Math.abs(oldNormal) > halfDepth + margin && Math.abs(newNormal) <= halfDepth + margin;
        boolean bl = crossedThrough = oldNormal * newNormal < 0.0 && Math.abs(oldNormal) > halfDepth && Math.abs(newNormal) > halfDepth;
        if (!reachesFace || !enteredSlab && !crossedThrough) {
            return false;
        }
        double side = oldNormal < 0.0 ? -1.0 : 1.0;
        double safeNormal = side * (halfDepth + margin + 0.1);
        double safeX = normalIsZ ? target.getX() : cx + safeNormal;
        double safeZ = normalIsZ ? cz + safeNormal : target.getZ();
        target.teleportTo(safeX, target.getY(), safeZ);
        Vec3 push = normalIsZ ? new Vec3(0.0, 0.08, side * 0.52) : new Vec3(side * 0.52, 0.08, 0.0);
        target.setDeltaMovement(push);
        target.hasImpulse = true;
        return true;
    }

    public ItemStackHandler inventory() {
        return this.inventory;
    }

    public void assemblySwapCartridge(ItemStack charged) {
        if (!charged.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get())) {
            return;
        }
        this.inventory.setStackInSlot(0, charged.copyWithCount(1));
    }

    public IItemHandler itemHandlerForSide(@Nullable Direction side) {
        if (side == null) {
            return this.unsidedAutomation;
        }
        if (side == Direction.DOWN) {
            return null;
        }
        return side == Direction.UP ? this.topInput : this.sideOutput;
    }

    private void inventoryChanged() {
        Level level;
        boolean interruptedWhileActive;
        ItemStack stack = this.inventory.getStackInSlot(0);
        boolean bl = interruptedWhileActive = this.coolantTicks > 0 && this.isPowered() && !stack.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get());
        if (stack.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get())) {
            this.coolantTicks = CDUCoolantCartridgeItem.remaining(stack);
            this.coolantFraction = 0.0;
            this.activeFieldTicks = 0;
            this.blocksCleaned = 0L;
            this.signalsRemoved = 0L;
            this.parasitesSuppressed = 0L;
            this.awardedActivation = false;
            this.coolantWarningStage = this.coolantTicks <= 5400 ? 2 : (this.coolantTicks <= 13500 ? 1 : 0);
        } else {
            this.coolantTicks = 0;
            this.coolantFraction = 0.0;
            this.activeFieldTicks = 0;
        }
        if (interruptedWhileActive && (level = this.level) instanceof ServerLevel) {
            ServerLevel server = (ServerLevel)level;
            ActiveContainmentFields.remove(server, this.worldPosition);
            this.updateActiveState(false);
            server.playSound(null, this.worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.7f, 1.2f);
        }
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    public boolean loadDirect(Player player, ItemStack stack) {
        if (!stack.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()) || !this.inventory.getStackInSlot(0).isEmpty()) {
            return false;
        }
        if (this.level == null || this.level.isClientSide) {
            return true;
        }
        this.inventory.setStackInSlot(0, stack.copyWithCount(1));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        this.operator = player.getUUID();
        return true;
    }

    public int reserveSeconds() {
        return this.coolantTicks / 20;
    }

    public int estimatedRuntimeSeconds() {
        double consumption = this.coolantConsumptionMultiplier();
        return consumption <= 0.0 ? this.reserveSeconds() : (int)Math.ceil((double)this.coolantTicks / (20.0 * consumption));
    }

    public int coolantTicksRemaining() {
        return this.coolantTicks;
    }

    public float kineticSpeed() {
        return this.getSpeed();
    }

    public boolean kineticOverstressed() {
        return this.isOverStressed();
    }

    public double currentFieldRadius() {
        return this.isOverStressed() ? 0.0 : WhiteoutKineticScaling.projectorRadius(this.getSpeed());
    }

    public double coolantConsumptionMultiplier() {
        if (this.isOverStressed()) {
            return 0.0;
        }
        double consumption = WhiteoutKineticScaling.projectorConsumption(this.getSpeed());
        return this.fieldMode == ContainmentFieldMode.WALL ? consumption * (Double)FreezeConfig.PROJECTOR_WALL_CONSUMPTION_MULTIPLIER.get() : consumption;
    }

    public ContainmentFieldMode fieldMode() {
        return this.fieldMode;
    }

    public Direction fieldFacing() {
        BlockState state = this.getBlockState();
        return state.hasProperty((Property)CryogenicContainmentProjectorBlock.FACING) ? (Direction)state.getValue((Property)CryogenicContainmentProjectorBlock.FACING) : Direction.NORTH;
    }

    public int wallDepth() {
        return (Integer)FreezeConfig.PROJECTOR_WALL_DEPTH.get();
    }

    public int wallHeight() {
        int configuredBase = (Integer)FreezeConfig.PROJECTOR_WALL_HEIGHT.get();
        int scaled = (int)Math.round(WhiteoutKineticScaling.projectorWallHeight(this.getSpeed()));
        return Math.max(3, Math.min(64, configuredBase + scaled - 9));
    }

    public double wallHalfWidth() {
        return this.currentFieldRadius() * 1.5;
    }

    public double wallWidth() {
        return this.wallHalfWidth() * 2.0 + 1.0;
    }

    public ContainmentFieldGeometry fieldGeometry() {
        double extent = this.fieldMode == ContainmentFieldMode.WALL ? this.wallHalfWidth() : this.currentFieldRadius();
        return new ContainmentFieldGeometry(this.fieldMode, this.fieldFacing(), extent, this.wallDepth(), this.wallHeight());
    }

    public AABB getRenderBoundingBox() {
        return this.fieldGeometry().bounds(this.worldPosition).inflate(1.0);
    }

    public void cycleFieldMode() {
        this.fieldMode = this.fieldMode.next();
        this.scanCursor = 0;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    public boolean cleanupEnabled() {
        return this.cleanupEnabled;
    }

    public void toggleCleanupMode() {
        this.cleanupEnabled = !this.cleanupEnabled;
        this.blockCheckFraction = 0.0;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    public boolean canExtractCartridge() {
        return true;
    }

    public int comparatorLevel() {
        return this.coolantTicks <= 0 || this.isOverStressed() ? 0 : Math.max(1, this.coolantTicks * 15 / 54000);
    }

    public String statusKey() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        if (stack.is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get())) {
            return "depleted";
        }
        if (stack.isEmpty()) {
            return "empty";
        }
        if (this.isOverStressed()) {
            return "jammed";
        }
        if (Math.abs(this.getSpeed()) < 32.0f) {
            return "stopped";
        }
        return "active";
    }

    public long parasitesSuppressed() {
        return this.parasitesSuppressed;
    }

    public long signalsRemoved() {
        return this.signalsRemoved;
    }

    public long blocksCleaned() {
        return this.blocksCleaned;
    }

    public void dropContents() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        ItemStack stack = this.inventory.getStackInSlot(0);
        if (!stack.isEmpty()) {
            ItemStack dropped = stack.copy();
            if (dropped.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get())) {
                CDUCoolantCartridgeItem.setRemaining(dropped, this.coolantTicks);
            }
            Containers.dropItemStack((Level)this.level, (double)((double)this.worldPosition.getX() + 0.5), (double)((double)this.worldPosition.getY() + 0.5), (double)((double)this.worldPosition.getZ() + 0.5), (ItemStack)dropped);
            this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    public Component getDisplayName() {
        return Component.translatable((String)"block.sporecannons.cryogenic_containment_projector");
    }

    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CryogenicContainmentProjectorMenu(id, inventory, this);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        tag.putInt("CoolantTicks", this.coolantTicks);
        tag.putDouble("CoolantFraction", this.coolantFraction);
        tag.putDouble("BlockCheckFraction", this.blockCheckFraction);
        tag.putInt("ActiveFieldTicks", this.activeFieldTicks);
        tag.putInt("ScanCursor", this.scanCursor);
        tag.putLong("ParasitesSuppressed", this.parasitesSuppressed);
        tag.putLong("SignalsRemoved", this.signalsRemoved);
        tag.putLong("BlocksCleaned", this.blocksCleaned);
        tag.putBoolean("AwardedActivation", this.awardedActivation);
        tag.putString("FieldMode", this.fieldMode.serializedName());
        tag.putBoolean("CleanupEnabled", this.cleanupEnabled);
        tag.putString("LastReportedStatus", this.lastReportedStatus);
        tag.putInt("CoolantWarningStage", this.coolantWarningStage);
        if (this.operator != null) {
            tag.putUUID("Operator", this.operator);
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        int savedCoolant = tag.getInt("CoolantTicks");
        ItemStack cartridge = this.inventory.getStackInSlot(0);
        if (cartridge.is((Item)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get())) {
            if (CDUCoolantCartridgeItem.hasStoredCharge(cartridge)) {
                this.coolantTicks = CDUCoolantCartridgeItem.remaining(cartridge);
            } else {
                this.coolantTicks = savedCoolant > 0 ? Math.min(54000, (int)Math.round((double)savedCoolant * 54000.0 / 18000.0)) : 54000;
                CDUCoolantCartridgeItem.setRemaining(cartridge, this.coolantTicks);
            }
        } else {
            this.coolantTicks = 0;
        }
        this.coolantFraction = tag.getDouble("CoolantFraction");
        this.blockCheckFraction = tag.getDouble("BlockCheckFraction");
        this.activeFieldTicks = tag.getInt("ActiveFieldTicks");
        this.scanCursor = tag.getInt("ScanCursor");
        this.parasitesSuppressed = tag.getLong("ParasitesSuppressed");
        this.signalsRemoved = tag.getLong("SignalsRemoved");
        this.blocksCleaned = tag.getLong("BlocksCleaned");
        this.awardedActivation = tag.getBoolean("AwardedActivation");
        this.fieldMode = ContainmentFieldMode.byName(tag.getString("FieldMode"));
        this.cleanupEnabled = !tag.contains("CleanupEnabled") || tag.getBoolean("CleanupEnabled");
        this.lastReportedStatus = tag.getString("LastReportedStatus");
        this.coolantWarningStage = tag.getInt("CoolantWarningStage");
        this.operator = tag.hasUUID("Operator") ? tag.getUUID("Operator") : null;
    }

    private final class SidedProjectorHandler
    implements IItemHandler {
        private final boolean insert;
        private final boolean extract;

        private SidedProjectorHandler(boolean insert, boolean extract) {
            this.insert = insert;
            this.extract = extract;
        }

        public int getSlots() {
            return 1;
        }

        public ItemStack getStackInSlot(int slot) {
            return CryogenicContainmentProjectorBlockEntity.this.inventory.getStackInSlot(0);
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return this.insert ? CryogenicContainmentProjectorBlockEntity.this.inventory.insertItem(0, stack, simulate) : stack;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return this.extract && CryogenicContainmentProjectorBlockEntity.this.inventory.getStackInSlot(0).is((Item)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()) ? CryogenicContainmentProjectorBlockEntity.this.inventory.extractItem(0, amount, simulate) : ItemStack.EMPTY;
        }

        public int getSlotLimit(int slot) {
            return 1;
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return this.insert && CryogenicContainmentProjectorBlockEntity.this.inventory.isItemValid(0, stack);
        }
    }
}

