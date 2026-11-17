/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Harbinger.Spore.Sentities.Utility.InfectionTendril
 *  com.Harbinger.Spore.Sentities.Utility.ScentEntity
 *  com.Harbinger.Spore.core.Sblocks
 *  com.Harbinger.Spore.core.Seffects
 *  com.Harbinger.Spore.core.Sfluids
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.EntityTypeTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  rbasamoyai.createbigcannons.munitions.big_cannon.he_shell.HEShellProjectile
 */
package com.elvoux.sporecannons.munition;

import com.Harbinger.Spore.Sentities.Utility.InfectionTendril;
import com.Harbinger.Spore.Sentities.Utility.ScentEntity;
import com.Harbinger.Spore.core.Sblocks;
import com.Harbinger.Spore.core.Seffects;
import com.Harbinger.Spore.core.Sfluids;
import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.munition.CDUActivationManager;
import com.elvoux.sporecannons.munition.CDUFieldTerminal;
import com.elvoux.sporecannons.munition.ContainmentZoneManager;
import com.elvoux.sporecannons.munition.CryogenicInsulation;
import com.elvoux.sporecannons.munition.CryogenicMineFuzeItem;
import com.elvoux.sporecannons.munition.CryogenicSaturationManager;
import com.elvoux.sporecannons.munition.FreezeCleanupManager;
import com.elvoux.sporecannons.munition.FreezeExplosionManager;
import com.elvoux.sporecannons.munition.FreezeShellBlock;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.munition.FreezeSnowManager;
import com.elvoux.sporecannons.registry.SporeCannonsBlocks;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.big_cannon.he_shell.HEShellProjectile;

public final class FreezeShellProjectile
extends HEShellProjectile {
    private boolean detonated;
    private CDUActivationManager.Cue activationCue = CDUActivationManager.Cue.STANDARD;
    private long activationDueGameTime = -1L;
    private Vec3 activationCenter = Vec3.ZERO;
    @Nullable
    private UUID activationResponsiblePlayer;
    private boolean activationMarker;
    private ItemStack activationFuze = ItemStack.EMPTY;
    private static final TagKey<Block> REMOVABLE_GROWTHS = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)SporeCannons.id("removable_growths"));
    private static final TagKey<Block> FREEZABLE_BIOMASS = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)SporeCannons.id("freezable_biomass"));
    private static final TagKey<Block> FUNGAL_BLOCKS = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"spore", (String)"fungal_blocks"));
    private static final TagKey<Block> CDU_REMOVABLE_FOLIAGE = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"spore", (String)"removable_foliage"));
    private static final TagKey<Block> CDU_BIOMASS = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"spore", (String)"biomass_to_membrane"));

    public FreezeShellProjectile(EntityType<? extends FreezeShellProjectile> type, Level level) {
        super(type, level);
    }

    protected void detonate(Position position) {
        UUID responsiblePlayerId;
        ServerLevel level;
        block6: {
            block5: {
                Level level2 = this.level();
                if (!(level2 instanceof ServerLevel)) break block5;
                level = (ServerLevel)level2;
                if (!this.detonated) break block6;
            }
            return;
        }
        this.detonated = true;
        FreezeShellTier tier = FreezeShellTier.fromEntityType(this.getType());
        Vec3 center = new Vec3(position.x(), position.y(), position.z());
        ServerPlayer responsiblePlayer = this.findResponsiblePlayer(level, center, tier);
        UUID uUID = responsiblePlayerId = responsiblePlayer == null ? null : responsiblePlayer.getUUID();
        if (this.activationCue == CDUActivationManager.Cue.MINE) {
            FreezeShellProjectile.completeDetonation(level, center, tier, responsiblePlayerId);
        } else {
            this.spawnActivationMarker(level, center, responsiblePlayerId);
            CDUActivationManager.signalProjectile(level, center, tier, this.activationCue);
        }
    }

    private void spawnActivationMarker(ServerLevel level, Vec3 center, @Nullable UUID responsiblePlayerId) {
        FreezeShellProjectile marker = (FreezeShellProjectile)this.getType().create((Level)level);
        if (marker == null) {
            return;
        }
        marker.setFuze(this.activationFuze.copy());
        marker.activationCue = this.activationCue;
        marker.activationMarker = true;
        marker.detonated = true;
        marker.activationCenter = center;
        marker.activationResponsiblePlayer = responsiblePlayerId;
        marker.activationDueGameTime = level.getGameTime() + 40L;
        marker.setOrientation(this.getOrientation());
        marker.pinForActivation();
        level.addFreshEntity((Entity)marker);
    }

    public void tick() {
        if (this.activationMarker) {
            ServerLevel level;
            this.baseTick();
            this.pinForActivation();
            Level level2 = this.level();
            if (level2 instanceof ServerLevel && (level = (ServerLevel)level2).getGameTime() >= this.activationDueGameTime) {
                FreezeShellTier tier = FreezeShellTier.fromEntityType(this.getType());
                this.activationDueGameTime = -1L;
                FreezeShellProjectile.completeDetonation(level, this.activationCenter, tier, this.activationResponsiblePlayer);
                this.discard();
            }
            return;
        }
        super.tick();
    }

    private void pinForActivation() {
        this.setPos(this.activationCenter.x, this.activationCenter.y, this.activationCenter.z);
        this.setDeltaMovement(Vec3.ZERO);
        this.setNoGravity(true);
        this.setInGround(true);
        this.setGroundPos(this.activationCenter);
    }

    public void setFuze(ItemStack stack) {
        super.setFuze(stack);
        ItemStack itemStack = this.activationFuze = stack == null ? ItemStack.EMPTY : stack.copy();
        this.activationCue = stack != null && stack.is((Item)SporeCannonsItems.CRYOGENIC_SENSOR_FUZE.get()) ? CDUActivationManager.Cue.SENSOR : (stack != null && stack.is((Item)SporeCannonsItems.CRYOGENIC_MINE_FUZE.get()) && CryogenicMineFuzeItem.completedGroundCountdown(stack) ? CDUActivationManager.Cue.MINE : CDUActivationManager.Cue.STANDARD);
    }

    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeBoolean(this.activationMarker);
        if (this.activationMarker) {
            buffer.writeDouble(this.activationCenter.x);
            buffer.writeDouble(this.activationCenter.y);
            buffer.writeDouble(this.activationCenter.z);
        }
    }

    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        super.readSpawnData(buffer);
        this.activationMarker = buffer.readBoolean();
        if (this.activationMarker) {
            this.activationCenter = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            this.setNoGravity(true);
            this.setInGround(true);
            this.setGroundPos(this.activationCenter);
        }
    }

    static void completeDetonation(ServerLevel level, Vec3 center, FreezeShellTier tier, @Nullable UUID responsiblePlayerId) {
        ServerPlayer responsiblePlayer;
        ServerPlayer serverPlayer = responsiblePlayer = responsiblePlayerId == null ? null : level.getServer().getPlayerList().getPlayer(responsiblePlayerId);
        if (responsiblePlayer != null) {
            if (tier == FreezeShellTier.VI) {
                ((PlayerTrigger)SporeCannonsCriteria.TIER_SIX_DETONATION.get()).trigger(responsiblePlayer);
            }
            if (tier.ordinal() >= FreezeShellTier.IV.ordinal() && level.dimension() == Level.NETHER) {
                ((PlayerTrigger)SporeCannonsCriteria.HIGH_TIER_NETHER_DETONATION.get()).trigger(responsiblePlayer);
            }
        }
        int parasitesAffected = FreezeShellProjectile.applyColdPulse(level, center, tier, responsiblePlayer);
        FreezeShellProjectile.removeInfectionEntities(level, center, tier.entityRadius());
        if (((Boolean)FreezeConfig.CLEAN_BLOCKS.get()).booleanValue()) {
            FreezeCleanupManager.schedule(level, center, tier, responsiblePlayer);
        }
        FreezeSnowManager.schedule(level, BlockPos.containing((Position)center), tier);
        ContainmentZoneManager.activate(level, center, tier, responsiblePlayer);
        CDUFieldTerminal.strikeReport(level, center, tier, parasitesAffected);
        FreezeExplosionManager.trigger(level, center, tier);
    }

    public BlockState getRenderedBlockState() {
        FreezeShellTier tier = FreezeShellTier.fromEntityType(this.getType());
        return (BlockState)((FreezeShellBlock)((Object)SporeCannonsBlocks.FREEZE_SHELLS.get((Object)tier).get())).defaultBlockState().setValue((Property)BlockStateProperties.FACING, (Comparable)Direction.NORTH);
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("SporeCannonsDetonated", this.detonated);
        tag.putString("SporeCannonsActivationCue", this.activationCue.name());
        if (this.activationDueGameTime >= 0L) {
            tag.putBoolean("SporeCannonsActivationMarker", this.activationMarker);
            tag.putLong("SporeCannonsActivationDue", this.activationDueGameTime);
            tag.putDouble("SporeCannonsActivationX", this.activationCenter.x);
            tag.putDouble("SporeCannonsActivationY", this.activationCenter.y);
            tag.putDouble("SporeCannonsActivationZ", this.activationCenter.z);
            if (this.activationResponsiblePlayer != null) {
                tag.putUUID("SporeCannonsActivationOwner", this.activationResponsiblePlayer);
            }
        }
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.detonated = tag.getBoolean("SporeCannonsDetonated");
        try {
            this.activationCue = CDUActivationManager.Cue.valueOf(tag.getString("SporeCannonsActivationCue"));
        }
        catch (IllegalArgumentException ignored) {
            this.activationCue = CDUActivationManager.Cue.STANDARD;
        }
        if (tag.contains("SporeCannonsActivationDue")) {
            this.activationMarker = tag.getBoolean("SporeCannonsActivationMarker");
            this.activationDueGameTime = tag.getLong("SporeCannonsActivationDue");
            this.activationCenter = new Vec3(tag.getDouble("SporeCannonsActivationX"), tag.getDouble("SporeCannonsActivationY"), tag.getDouble("SporeCannonsActivationZ"));
            this.activationResponsiblePlayer = tag.hasUUID("SporeCannonsActivationOwner") ? tag.getUUID("SporeCannonsActivationOwner") : null;
        }
    }

    private static int applyColdPulse(ServerLevel level, Vec3 center, FreezeShellTier tier, @Nullable ServerPlayer responsiblePlayer) {
        int radius = tier.entityRadius();
        AABB area = new AABB(center, center).inflate((double)radius);
        boolean appliedFrostbite = false;
        int parasitesAffected = 0;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area)) {
            if (!target.isAlive() || target.distanceToSqr(center) > (double)(radius * radius)) continue;
            if (target.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                FreezeShellProjectile.freezeParasite(target, tier, responsiblePlayer);
                appliedFrostbite = true;
                ++parasitesAffected;
                continue;
            }
            FreezeShellProjectile.freezeCollateral(level, center, target, tier);
        }
        if (appliedFrostbite && responsiblePlayer != null) {
            ((PlayerTrigger)SporeCannonsCriteria.FROSTBITE_APPLIED.get()).trigger(responsiblePlayer);
        }
        return parasitesAffected;
    }

    @Nullable
    private ServerPlayer findResponsiblePlayer(ServerLevel level, Vec3 center, FreezeShellTier tier) {
        ServerPlayer player;
        Entity entity2 = this.getOwner();
        if (entity2 instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)entity2;
            return player2;
        }
        Player nearest = level.getNearestPlayer(center.x, center.y, center.z, Math.max(96.0, (double)tier.entityRadius() + 64.0), entity -> {
            ServerPlayer player;
            return entity instanceof ServerPlayer && !(player = (ServerPlayer)entity).isSpectator();
        });
        return nearest instanceof ServerPlayer ? (player = (ServerPlayer)nearest) : null;
    }

    private static void freezeParasite(LivingEntity target, FreezeShellTier tier, @Nullable ServerPlayer responsiblePlayer) {
        CryogenicSaturationManager.add(target, 100.0 + (double)tier.ordinal() * 20.0, (Entity)responsiblePlayer);
        MobEffectInstance existing = target.getEffect(Seffects.FROSTBITE);
        int amplifier = existing == null ? tier.frostbiteAmplifier() : Math.max(tier.frostbiteAmplifier(), Math.min(5, existing.getAmplifier() + 1));
        target.addEffect(new MobEffectInstance(Seffects.FROSTBITE, tier.duration(), amplifier));
        target.setTicksFrozen(Math.max(target.getTicksFrozen(), 240 + tier.ordinal() * 40));
        target.hurt(target.damageSources().freeze(), tier.damage());
    }

    private static void freezeCollateral(ServerLevel level, Vec3 center, LivingEntity target, FreezeShellTier tier) {
        if (target instanceof Player) {
            Player player = (Player)target;
            if (!((Boolean)FreezeConfig.AFFECT_PLAYERS.get()).booleanValue() || player.isCreative() || player.isSpectator()) {
                return;
            }
        } else if (!((Boolean)FreezeConfig.AFFECT_NORMAL_MOBS.get()).booleanValue()) {
            return;
        }
        double radius = tier.entityRadius();
        double distance = Math.sqrt(target.distanceToSqr(center));
        double exposure = 1.0 - 0.8 * Math.min(1.0, distance / radius);
        if (((Boolean)FreezeConfig.RESPECT_COVER.get()).booleanValue() && FreezeShellProjectile.isBehindCover(level, center, target)) {
            exposure *= ((Double)FreezeConfig.COVER_MULTIPLIER.get()).doubleValue();
        }
        float damage = (float)((double)tier.collateralDamage() * (Double)FreezeConfig.COLLATERAL_DAMAGE_MULTIPLIER.get() * exposure);
        int frozenTicks = CryogenicInsulation.protectedFrozenTicks(target, (int)Math.round((double)tier.collateralFrozenTicks() * exposure));
        damage *= CryogenicInsulation.damageMultiplier(target);
        target.setTicksFrozen(Math.max(target.getTicksFrozen(), frozenTicks));
        if (damage > 0.0f) {
            target.hurt(target.damageSources().freeze(), damage);
        }
    }

    private static boolean isBehindCover(ServerLevel level, Vec3 center, LivingEntity target) {
        BlockHitResult hit = level.clip(new ClipContext(center, target.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)target));
        if (hit.getType() == HitResult.Type.MISS) {
            return false;
        }
        return hit.getLocation().distanceToSqr(target.getEyePosition()) > 0.36;
    }

    private static void removeInfectionEntities(ServerLevel level, Vec3 center, int radius) {
        AABB area = new AABB(center, center).inflate((double)radius);
        for (ScentEntity entity : level.getEntitiesOfClass(ScentEntity.class, area)) {
            if (!(entity.distanceToSqr(center) <= (double)(radius * radius))) continue;
            entity.discard();
        }
        for (ScentEntity entity : level.getEntitiesOfClass(InfectionTendril.class, area)) {
            if (!(entity.distanceToSqr(center) <= (double)(radius * radius))) continue;
            entity.discard();
        }
    }

    static BlockState cleanedState(BlockState state) {
        return FreezeShellProjectile.cleanedState(state, false);
    }

    static BlockState lightCleanupState(BlockState state) {
        return FreezeShellProjectile.lightCleanupState(state, false);
    }

    static BlockState lightCleanupState(BlockState state, boolean shatterFreshBile) {
        BlockState cleaned = FreezeShellProjectile.cleanedState(state, shatterFreshBile);
        if (cleaned == null || cleaned.is((Block)Sblocks.FROST_BURNED_BIOMASS.get()) || cleaned.is((Block)Sblocks.FROZEN_REMAINS.get())) {
            return null;
        }
        return cleaned;
    }

    static BlockState cleanedState(BlockState state, boolean shatterFreshBile) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey((Object)state.getBlock());
        if (key == null) {
            return null;
        }
        if ("spore".equals(key.getNamespace())) {
            BlockState restored;
            switch (key.getPath()) {
                case "infested_dirt": {
                    BlockState blockState = Blocks.DIRT.defaultBlockState();
                    break;
                }
                case "infested_stone": {
                    BlockState blockState = Blocks.STONE.defaultBlockState();
                    break;
                }
                case "infested_netherrack": {
                    BlockState blockState = Blocks.NETHERRACK.defaultBlockState();
                    break;
                }
                case "infested_soul_sand": {
                    BlockState blockState = Blocks.SOUL_SAND.defaultBlockState();
                    break;
                }
                case "infested_end_stone": {
                    BlockState blockState = Blocks.END_STONE.defaultBlockState();
                    break;
                }
                case "infested_sand": {
                    BlockState blockState = Blocks.SAND.defaultBlockState();
                    break;
                }
                case "infested_gravel": {
                    BlockState blockState = Blocks.GRAVEL.defaultBlockState();
                    break;
                }
                case "infested_deepslate": {
                    BlockState blockState = Blocks.DEEPSLATE.defaultBlockState();
                    break;
                }
                case "infested_red_sand": {
                    BlockState blockState = Blocks.RED_SAND.defaultBlockState();
                    break;
                }
                case "infested_clay": {
                    BlockState blockState = Blocks.CLAY.defaultBlockState();
                    break;
                }
                case "infested_cobbled_deepslate": {
                    BlockState blockState = Blocks.COBBLED_DEEPSLATE.defaultBlockState();
                    break;
                }
                case "infested_cobblestone": {
                    BlockState blockState = Blocks.COBBLESTONE.defaultBlockState();
                    break;
                }
                case "infested_stone_bricks": {
                    BlockState blockState = Blocks.STONE_BRICKS.defaultBlockState();
                    break;
                }
                case "infested_bricks": {
                    BlockState blockState = Blocks.BRICKS.defaultBlockState();
                    break;
                }
                case "infested_laboratory_block": {
                    BlockState blockState = ((Block)Sblocks.LAB_BLOCK.get()).defaultBlockState();
                    break;
                }
                case "infested_laboratory_block1": {
                    BlockState blockState = ((Block)Sblocks.LAB_BLOCK1.get()).defaultBlockState();
                    break;
                }
                case "infested_laboratory_block2": {
                    BlockState blockState = ((Block)Sblocks.LAB_BLOCK2.get()).defaultBlockState();
                    break;
                }
                case "infested_laboratory_block3": {
                    BlockState blockState = ((Block)Sblocks.LAB_BLOCK3.get()).defaultBlockState();
                    break;
                }
                default: {
                    BlockState blockState = restored = null;
                }
            }
            if (restored != null) {
                return restored;
            }
        }
        if (state.is(Blocks.MYCELIUM)) {
            return Blocks.DIRT.defaultBlockState();
        }
        if (state.is((Block)Sblocks.REMAINS.get())) {
            return ((Block)Sblocks.FROZEN_REMAINS.get()).defaultBlockState();
        }
        if (state.is((Block)Sblocks.CRUSTED_BILE.get()) || "crusted_bile".equals(key.getPath())) {
            return Blocks.AIR.defaultBlockState();
        }
        if (state.is((Block)Sblocks.BILE.get()) || state.getFluidState().is((Fluid)Sfluids.BILE_FLUID_SOURCE.get()) || state.getFluidState().is((Fluid)Sfluids.BILE_FLUID_FLOWING.get()) || "bile".equals(key.getPath()) || "bile_lump".equals(key.getPath())) {
            return shatterFreshBile ? Blocks.AIR.defaultBlockState() : ((Block)Sblocks.CRUSTED_BILE.get()).defaultBlockState();
        }
        if (state.is((Block)Sblocks.BIOMASS_BLOCK.get()) || state.is(FREEZABLE_BIOMASS) || state.is(CDU_BIOMASS) || "spore".equals(key.getNamespace()) && state.is(FUNGAL_BLOCKS)) {
            return ((Block)Sblocks.FROST_BURNED_BIOMASS.get()).defaultBlockState();
        }
        if (state.is(REMOVABLE_GROWTHS) || state.is(CDU_REMOVABLE_FOLIAGE)) {
            return state.getFluidState().createLegacyBlock();
        }
        return null;
    }
}

