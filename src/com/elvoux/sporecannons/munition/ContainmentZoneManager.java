/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Harbinger.Spore.Sentities.Utility.InfectionTendril
 *  com.Harbinger.Spore.Sentities.Utility.ScentEntity
 *  com.Harbinger.Spore.core.Sblocks
 *  com.Harbinger.Spore.core.Seffects
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.elvoux.sporecannons.munition;

import com.Harbinger.Spore.Sentities.Utility.InfectionTendril;
import com.Harbinger.Spore.Sentities.Utility.ScentEntity;
import com.Harbinger.Spore.core.Sblocks;
import com.Harbinger.Spore.core.Seffects;
import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.field.WhiteoutTargeting;
import com.elvoux.sporecannons.munition.CDUFieldTerminal;
import com.elvoux.sporecannons.munition.CryogenicSaturationManager;
import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class ContainmentZoneManager {
    private static final String DATA_NAME = "sporecannons_containment_zones";
    private static final SavedData.Factory<ZoneSavedData> DATA_FACTORY = new SavedData.Factory(ZoneSavedData::new, ZoneSavedData::load);
    private static final BlockParticleOption ICE_CRYSTAL = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.BLUE_ICE.defaultBlockState());

    private ContainmentZoneManager() {
    }

    public static void activate(ServerLevel level, Vec3 center, FreezeShellTier tier, @Nullable ServerPlayer responsible) {
        if (!((Boolean)FreezeConfig.CONTAINMENT_ZONES.get()).booleanValue()) {
            return;
        }
        ZoneSavedData data = ContainmentZoneManager.data(level);
        ArrayDeque<Zone> zones = data.zones;
        for (Zone zone : zones) {
            if (!(zone.center.distanceTo(center) <= Math.max(8.0, (double)Math.min(zone.radius, tier.containmentRadius())))) continue;
            zone.reinforce(center, tier, level.getGameTime(), responsible);
            data.setDirty();
            return;
        }
        while (zones.size() >= (Integer)FreezeConfig.MAX_CONTAINMENT_ZONES.get()) {
            ContainmentZoneManager.finish(level, zones.removeFirst(), true);
        }
        zones.addLast(new Zone(center, tier, level.getGameTime(), responsible));
        data.setDirty();
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        if (!((Boolean)FreezeConfig.CONTAINMENT_ZONES.get()).booleanValue()) {
            return;
        }
        ZoneSavedData data = ContainmentZoneManager.data(level2);
        ArrayDeque<Zone> zones = data.zones;
        if (zones.isEmpty()) {
            return;
        }
        long now = level2.getGameTime();
        boolean changed = false;
        Iterator<Zone> iterator = zones.iterator();
        while (iterator.hasNext()) {
            Zone zone = iterator.next();
            if (now >= zone.expiresAt) {
                iterator.remove();
                ContainmentZoneManager.finish(level2, zone, false);
                changed = true;
                continue;
            }
            if ((now + (long)zone.phase) % 20L != 0L) continue;
            changed |= zone.suppressEntities(level2);
        }
        if (!zones.isEmpty()) {
            int budget = (Integer)FreezeConfig.CONTAINMENT_BLOCK_CHECKS_PER_TICK.get();
            int perZone = Math.max(1, budget / zones.size());
            for (Zone zone : zones) {
                changed |= zone.sampleBlocks(level2, perZone);
            }
        }
        if (changed) {
            data.setDirty();
        }
    }

    static int activeZones(ServerLevel level) {
        return ContainmentZoneManager.data((ServerLevel)level).zones.size();
    }

    static void clearForTests(ServerLevel level) {
        ZoneSavedData data = ContainmentZoneManager.data(level);
        data.zones.clear();
        data.setDirty();
    }

    private static ZoneSavedData data(ServerLevel level) {
        return (ZoneSavedData)level.getDataStorage().computeIfAbsent(DATA_FACTORY, DATA_NAME);
    }

    private static void finish(ServerLevel level, Zone zone, boolean displaced) {
        ServerPlayer player;
        ServerPlayer serverPlayer = player = zone.responsible == null ? null : level.getServer().getPlayerList().getPlayer(zone.responsible);
        if (player != null && !displaced && level.getGameTime() - zone.lastRegrowthAt >= 100L) {
            ((PlayerTrigger)SporeCannonsCriteria.ZONE_SECURED.get()).trigger(player);
        }
        if (((Boolean)FreezeConfig.FIELD_REPORTS.get()).booleanValue()) {
            CDUFieldTerminal.zoneReport(level, zone.center, zone.tier, displaced, zone.cleanedBlocks, zone.parasiteContacts);
        }
    }

    private static final class ZoneSavedData
    extends SavedData {
        private final ArrayDeque<Zone> zones = new ArrayDeque();

        private ZoneSavedData() {
        }

        private static ZoneSavedData load(CompoundTag root, HolderLookup.Provider registries) {
            ZoneSavedData data = new ZoneSavedData();
            ListTag list = root.getList("Zones", 10);
            for (int i = 0; i < list.size(); ++i) {
                CompoundTag tag = list.getCompound(i);
                try {
                    FreezeShellTier tier = FreezeShellTier.valueOf(tag.getString("Tier"));
                    UUID responsible = tag.hasUUID("Responsible") ? tag.getUUID("Responsible") : null;
                    data.zones.addLast(new Zone(new Vec3(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z")), tier, Mth.clamp((int)tag.getInt("Radius"), (int)1, (int)512), tag.getLong("ExpiresAt"), responsible, Math.floorMod(tag.getInt("Phase"), 20), Math.max(0, tag.getInt("CleanedBlocks")), Math.max(0, tag.getInt("ParasiteContacts")), tag.getLong("LastRegrowthAt")));
                    continue;
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            return data;
        }

        public CompoundTag save(CompoundTag root, HolderLookup.Provider registries) {
            ListTag list = new ListTag();
            for (Zone zone : this.zones) {
                CompoundTag tag = new CompoundTag();
                tag.putDouble("X", zone.center.x);
                tag.putDouble("Y", zone.center.y);
                tag.putDouble("Z", zone.center.z);
                tag.putString("Tier", zone.tier.name());
                tag.putInt("Radius", zone.radius);
                tag.putLong("ExpiresAt", zone.expiresAt);
                if (zone.responsible != null) {
                    tag.putUUID("Responsible", zone.responsible);
                }
                tag.putInt("Phase", zone.phase);
                tag.putInt("CleanedBlocks", zone.cleanedBlocks);
                tag.putInt("ParasiteContacts", zone.parasiteContacts);
                tag.putLong("LastRegrowthAt", zone.lastRegrowthAt);
                list.add((Object)tag);
            }
            root.put("Zones", (Tag)list);
            return root;
        }
    }

    private static final class Zone {
        private Vec3 center;
        private FreezeShellTier tier;
        private int radius;
        private long expiresAt;
        private UUID responsible;
        private final int phase;
        private int cleanedBlocks;
        private int parasiteContacts;
        private long lastRegrowthAt;

        private Zone(Vec3 center, FreezeShellTier tier, long now, @Nullable ServerPlayer responsible) {
            this.center = center;
            this.tier = tier;
            this.radius = tier.containmentRadius();
            this.expiresAt = now + (long)tier.containmentTicks();
            this.lastRegrowthAt = now;
            this.responsible = responsible == null ? null : responsible.getUUID();
            this.phase = Math.floorMod(center.hashCode(), 20);
        }

        private Zone(Vec3 center, FreezeShellTier tier, int radius, long expiresAt, @Nullable UUID responsible, int phase, int cleanedBlocks, int parasiteContacts, long lastRegrowthAt) {
            this.center = center;
            this.tier = tier;
            this.radius = radius;
            this.expiresAt = expiresAt;
            this.responsible = responsible;
            this.phase = phase;
            this.cleanedBlocks = cleanedBlocks;
            this.parasiteContacts = parasiteContacts;
            this.lastRegrowthAt = lastRegrowthAt;
        }

        private void reinforce(Vec3 newCenter, FreezeShellTier newTier, long now, @Nullable ServerPlayer newResponsible) {
            if (newTier.ordinal() >= this.tier.ordinal()) {
                this.center = newCenter;
                this.tier = newTier;
                this.radius = newTier.containmentRadius();
            }
            this.expiresAt = Math.max(this.expiresAt, now + (long)newTier.containmentTicks());
            if (newResponsible != null) {
                this.responsible = newResponsible.getUUID();
            }
        }

        private boolean suppressEntities(ServerLevel level) {
            boolean changed = false;
            double radiusSquared = (double)this.radius * (double)this.radius;
            AABB bounds = new AABB(this.center, this.center).inflate((double)this.radius);
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity.isAlive() && WhiteoutTargeting.isAffected((Entity)entity))) {
                if (target.distanceToSqr(this.center) > radiusSquared) continue;
                ++this.parasiteContacts;
                changed = true;
                CryogenicSaturationManager.add(target, 12.0 + (double)this.tier.ordinal() * 3.0, this.responsible == null ? null : level.getServer().getPlayerList().getPlayer(this.responsible));
                target.setTicksFrozen(Math.max(target.getTicksFrozen(), 100));
                MobEffectInstance existing = target.getEffect(Seffects.FROSTBITE);
                int duration = existing == null ? 80 : Math.max(80, existing.getDuration());
                int amplifier = Math.min(2, this.tier.ordinal() / 2);
                target.addEffect(new MobEffectInstance(Seffects.FROSTBITE, duration, amplifier));
            }
            for (ScentEntity scent : level.getEntitiesOfClass(ScentEntity.class, bounds)) {
                if (!(scent.distanceToSqr(this.center) <= radiusSquared)) continue;
                scent.discard();
                changed = true;
            }
            for (InfectionTendril tendril : level.getEntitiesOfClass(InfectionTendril.class, bounds)) {
                if (!(tendril.distanceToSqr(this.center) <= radiusSquared)) continue;
                tendril.discard();
                changed = true;
            }
            this.spawnAmbientCrystals(level, radiusSquared);
            return changed;
        }

        private void spawnAmbientCrystals(ServerLevel level, double radiusSquared) {
            if (!((Boolean)FreezeConfig.EXPLOSION_EFFECTS.get()).booleanValue()) {
                return;
            }
            for (ServerPlayer player : level.players()) {
                if (player.distanceToSqr(this.center) > radiusSquared) continue;
                double spread = Math.min(7.0, 3.0 + (double)this.tier.ordinal() * 0.8);
                if (player.isUnderWater()) {
                    level.sendParticles((ParticleOptions)ParticleTypes.BUBBLE_POP, player.getX(), player.getY() + 0.8, player.getZ(), 3 + this.tier.ordinal(), spread, 1.4, spread, 0.025);
                    continue;
                }
                if (level.dimensionType().ultraWarm()) {
                    level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, player.getX(), player.getY() + 0.35, player.getZ(), 2 + this.tier.ordinal() / 2, spread, 0.5, spread, 0.008);
                    continue;
                }
                if (level.dimension() == Level.END) {
                    level.sendParticles((ParticleOptions)ParticleTypes.END_ROD, player.getX(), player.getY() + 0.8, player.getZ(), 2 + this.tier.ordinal() / 2, spread, 1.2, spread, 0.012);
                    continue;
                }
                level.sendParticles((ParticleOptions)ICE_CRYSTAL, player.getX(), player.getY() + 0.25, player.getZ(), 2 + this.tier.ordinal() / 2, spread, 0.35, spread, 0.018);
                level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, player.getX(), player.getY() + 1.2, player.getZ(), 2 + this.tier.ordinal() / 2, spread, 1.0, spread, 0.015);
            }
        }

        private boolean sampleBlocks(ServerLevel level, int checks) {
            boolean changed = false;
            int verticalReach = Math.min(32, Math.max(8, this.radius / 3));
            for (int i = 0; i < checks; ++i) {
                BlockState cleaned;
                double angle = level.random.nextDouble() * Math.PI * 2.0;
                double distance = Math.sqrt(level.random.nextDouble()) * (double)this.radius;
                int x = Mth.floor((double)(this.center.x + Math.cos(angle) * distance));
                int z = Mth.floor((double)(this.center.z + Math.sin(angle) * distance));
                BlockPos chunkCheck = new BlockPos(x, Mth.floor((double)this.center.y), z);
                if (!level.hasChunkAt(chunkCheck)) continue;
                int y = (i & 1) == 0 ? level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1 : Mth.floor((double)this.center.y) + level.random.nextInt(verticalReach * 2 + 1) - verticalReach;
                BlockPos pos = new BlockPos(x, y = Mth.clamp((int)y, (int)level.getMinBuildHeight(), (int)(level.getMaxBuildHeight() - 1)), z);
                BlockState current = level.getBlockState(pos);
                if (current.is((Block)Sblocks.CRUSTED_BILE.get()) || current.is((Block)Sblocks.FROZEN_REMAINS.get()) || current.is((Block)Sblocks.FROST_BURNED_BIOMASS.get()) || (cleaned = FreezeShellProjectile.cleanedState(current)) == null || cleaned.equals(current) || !level.setBlock(pos, cleaned, 3)) continue;
                ++this.cleanedBlocks;
                this.lastRegrowthAt = level.getGameTime();
                changed = true;
            }
            return changed;
        }
    }
}

