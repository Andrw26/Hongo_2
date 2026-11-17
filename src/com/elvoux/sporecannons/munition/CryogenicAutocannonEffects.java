/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Harbinger.Spore.core.Seffects
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.EntityTypeTags
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.network.PacketDistributor
 *  org.joml.Vector3f
 *  rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeEffect
 *  rbasamoyai.ritchiesprojectilelib.network.ClientboundShakeScreenPacket
 *  rbasamoyai.ritchiesprojectilelib.network.RPLNetwork
 *  rbasamoyai.ritchiesprojectilelib.network.RootPacket
 */
package com.elvoux.sporecannons.munition;

import com.Harbinger.Spore.core.Seffects;
import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.munition.CryogenicInsulation;
import com.elvoux.sporecannons.munition.CryogenicSaturationManager;
import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.network.ColdScreenPayload;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
import rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeEffect;
import rbasamoyai.ritchiesprojectilelib.network.ClientboundShakeScreenPacket;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

final class CryogenicAutocannonEffects {
    private static final String MG_HITS = "SporeCannonsCryoMgHits";
    private static final String MG_LAST_HIT = "SporeCannonsCryoMgLastHit";
    private static final BlockParticleOption ICE_SHARD = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.PACKED_ICE.defaultBlockState());
    private static final DustParticleOptions CYAN_GLOW = new DustParticleOptions(new Vector3f(0.12f, 0.92f, 1.0f), 0.9f);
    private static final DustParticleOptions PALE_FROST = new DustParticleOptions(new Vector3f(0.72f, 0.96f, 1.0f), 1.15f);
    private static final DustParticleOptions DEEP_CRYO = new DustParticleOptions(new Vector3f(0.04f, 0.42f, 0.72f), 0.75f);

    private CryogenicAutocannonEffects() {
    }

    static void machineGunHit(Entity entity) {
        CryogenicAutocannonEffects.machineGunHit(entity, null);
    }

    static void machineGunHit(Entity entity, @Nullable Entity source) {
        long lastHit;
        LivingEntity target;
        Level level = entity.level();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            CryogenicAutocannonEffects.machineGunImpact(level2, entity.position(), false);
        }
        if (!(entity instanceof LivingEntity) || !CryogenicAutocannonEffects.canAffect(target = (LivingEntity)entity)) {
            return;
        }
        target.setTicksFrozen(Math.max(target.getTicksFrozen(), CryogenicInsulation.protectedFrozenTicks(target, 70)));
        if (!CryogenicAutocannonEffects.isParasite(target)) {
            return;
        }
        long now = target.level().getGameTime();
        int hits = now - (lastHit = target.getPersistentData().getLong(MG_LAST_HIT)) > 100L ? 0 : target.getPersistentData().getInt(MG_HITS);
        target.getPersistentData().putLong(MG_LAST_HIT, now);
        target.getPersistentData().putInt(MG_HITS, ++hits);
        double before = CryogenicSaturationManager.saturation(target);
        CryogenicSaturationManager.add(target, 25.0, source);
        CryogenicAutocannonEffects.hurtWithCold(target, 1.5f);
        CryogenicAutocannonEffects.saturationFeedback(target, before);
        if (hits < 4) {
            return;
        }
        target.getPersistentData().putInt(MG_HITS, 0);
        Level level3 = target.level();
        if (level3 instanceof ServerLevel) {
            ServerLevel level4 = (ServerLevel)level3;
            CryogenicAutocannonEffects.machineGunImpact(level4, target.position().add(0.0, (double)target.getBbHeight() * 0.5, 0.0), true);
        }
        CryogenicAutocannonEffects.applyFrostbite(target, 160, 0);
        CryogenicAutocannonEffects.triggerFrostbiteAdvancement(source, target.level(), target.position());
        CryogenicAutocannonEffects.hurtWithCold(target, 4.0f);
    }

    static void armorPiercingHit(Entity entity) {
        CryogenicAutocannonEffects.armorPiercingHit(entity, null);
    }

    static void armorPiercingHit(Entity entity, @Nullable Entity source) {
        LivingEntity target;
        Level level = entity.level();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            CryogenicAutocannonEffects.armorPiercingImpact(level2, entity.position().add(0.0, (double)entity.getBbHeight() * 0.5, 0.0));
        }
        if (!(entity instanceof LivingEntity) || !CryogenicAutocannonEffects.canAffect(target = (LivingEntity)entity)) {
            return;
        }
        target.setTicksFrozen(Math.max(target.getTicksFrozen(), CryogenicInsulation.protectedFrozenTicks(target, 180)));
        if (CryogenicAutocannonEffects.isParasite(target)) {
            CryogenicSaturationManager.add(target, 100.0, source);
            CryogenicAutocannonEffects.applyFrostbite(target, 200, 0);
            CryogenicAutocannonEffects.triggerFrostbiteAdvancement(source, target.level(), target.position());
            CryogenicAutocannonEffects.hurtWithCold(target, 12.0f);
        } else {
            CryogenicAutocannonEffects.hurtWithCold(target, 3.0f);
        }
    }

    static void flakBurst(ServerLevel level, Position position) {
        CryogenicAutocannonEffects.flakBurst(level, position, null);
    }

    static void flakBurst(ServerLevel level, Position position, @Nullable Entity source) {
        Vec3 center = new Vec3(position.x(), position.y(), position.z());
        double radius = 6.0;
        CryogenicAutocannonEffects.affectTerrain(level, BlockPos.containing((Position)center), 3, false, true);
        boolean appliedFrostbite = false;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(radius))) {
            double distance = target.position().distanceTo(center);
            if (distance > radius || !CryogenicAutocannonEffects.canAffect(target)) continue;
            double falloff = 1.0 - 0.65 * distance / radius;
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), CryogenicInsulation.protectedFrozenTicks(target, (int)Math.round(160.0 * falloff))));
            if (CryogenicAutocannonEffects.isParasite(target)) {
                CryogenicSaturationManager.add(target, 65.0 * falloff, source);
                CryogenicAutocannonEffects.applyFrostbite(target, 240, 0);
                appliedFrostbite = true;
                CryogenicAutocannonEffects.hurtWithCold(target, (float)(7.0 * falloff));
                continue;
            }
            CryogenicAutocannonEffects.hurtWithCold(target, (float)(4.0 * falloff));
        }
        level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, center.x, center.y, center.z, 110, 3.2, 2.4, 3.2, 0.14);
        level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, center.x, center.y, center.z, 38, 2.8, 1.6, 2.8, 0.035);
        if (((Boolean)FreezeConfig.AUTOCANNON_VISUAL_EFFECTS.get()).booleanValue()) {
            level.sendParticles((ParticleOptions)ParticleTypes.FLASH, center.x, center.y, center.z, 1, 0.08, 0.08, 0.08, 0.0);
            level.sendParticles((ParticleOptions)ParticleTypes.GUST_EMITTER_SMALL, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
            level.sendParticles((ParticleOptions)ICE_SHARD, center.x, center.y, center.z, 56, 2.6, 2.0, 2.6, 0.2);
            level.sendParticles((ParticleOptions)CYAN_GLOW, center.x, center.y, center.z, 72, 2.8, 2.1, 2.8, 0.15);
            level.sendParticles((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, center.x, center.y, center.z, 18, 2.0, 1.6, 2.0, 0.08);
            CryogenicAutocannonEffects.spawnRings(level, center, 2.0, 4.0, 6.0);
            CryogenicAutocannonEffects.playImpactSounds(level, center, 1.6f, 0.76f);
            CryogenicAutocannonEffects.pulsePlayers(level, center, 34.0, 55, 0.18f, 0.75f, 0.08f);
        }
        if (appliedFrostbite) {
            CryogenicAutocannonEffects.triggerFrostbiteAdvancement(source, (Level)level, center);
        }
    }

    static void machineGunBlockImpact(ServerLevel level, BlockPos hitPos, Vec3 center) {
        CryogenicAutocannonEffects.affectTerrain(level, hitPos, 0, false, false);
        CryogenicAutocannonEffects.machineGunImpact(level, center, false);
    }

    static void armorPiercingBlockImpact(ServerLevel level, BlockPos hitPos, Vec3 center) {
        CryogenicAutocannonEffects.affectTerrain(level, hitPos, 1, true, true);
        CryogenicAutocannonEffects.armorPiercingImpact(level, center);
    }

    private static void affectTerrain(ServerLevel level, BlockPos center, int radius, boolean shatterFresh, boolean freezeLava) {
        boolean changed = false;
        int radiusSquared = radius * radius;
        for (BlockPos pos : BlockPos.betweenClosed((BlockPos)center.offset(-radius, -radius, -radius), (BlockPos)center.offset(radius, radius, radius))) {
            if (pos.distSqr((Vec3i)center) > (double)radiusSquared) continue;
            BlockState current = level.getBlockState(pos);
            BlockState cleaned = FreezeShellProjectile.lightCleanupState(current, shatterFresh);
            if (cleaned == null && freezeLava && current.getFluidState().is(FluidTags.LAVA)) {
                BlockState blockState = cleaned = current.getFluidState().isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.STONE.defaultBlockState();
            }
            if (cleaned == null || cleaned.equals(current) || !level.setBlock(pos, cleaned, 3)) continue;
            changed = true;
        }
        if (changed) {
            level.sendParticles((ParticleOptions)ICE_SHARD, (double)center.getX() + 0.5, (double)center.getY() + 0.5, (double)center.getZ() + 0.5, 14 + radius * 8, 0.45 + (double)radius, 0.45 + (double)radius, 0.45 + (double)radius, 0.12);
            level.playSound(null, center, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.8f + (float)radius * 0.2f, 1.15f);
        }
    }

    private static void saturationFeedback(LivingEntity target, double before) {
        Level level;
        int beforeStage;
        double after = CryogenicSaturationManager.saturation(target);
        int afterStage = (int)(after / 25.0);
        if (afterStage <= (beforeStage = (int)(before / 25.0)) || !((level = target.level()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        level2.sendParticles((ParticleOptions)(afterStage >= 4 ? ICE_SHARD : ParticleTypes.SNOWFLAKE), target.getX(), target.getY() + (double)target.getBbHeight() * 0.55, target.getZ(), 4 + afterStage * 2, 0.3, 0.35, 0.3, 0.035);
    }

    static void projectileTrail(ServerLevel level, Vec3 position, TrailType type, int tickCount) {
        if (!((Boolean)FreezeConfig.AUTOCANNON_TRAILS.get()).booleanValue() || tickCount <= 1) {
            return;
        }
        switch (type.ordinal()) {
            case 0: {
                if ((tickCount & 1) != 0) {
                    return;
                }
                level.sendParticles((ParticleOptions)CYAN_GLOW, position.x, position.y, position.z, 1, 0.035, 0.035, 0.035, 0.0);
                if (tickCount % 4 != 0) break;
                level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, position.x, position.y, position.z, 1, 0.04, 0.04, 0.04, 0.005);
                break;
            }
            case 1: {
                level.sendParticles((ParticleOptions)((tickCount & 1) == 0 ? PALE_FROST : DEEP_CRYO), position.x, position.y, position.z, 2, 0.07, 0.07, 0.07, 0.008);
                if (tickCount % 3 != 0) break;
                level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, position.x, position.y, position.z, 2, 0.08, 0.08, 0.08, 0.012);
                break;
            }
            case 2: {
                if ((tickCount & 1) != 0) {
                    return;
                }
                level.sendParticles((ParticleOptions)CYAN_GLOW, position.x, position.y, position.z, 2, 0.11, 0.11, 0.11, 0.012);
                level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, position.x, position.y, position.z, 2, 0.13, 0.13, 0.13, 0.018);
                if (tickCount % 4 != 0) break;
                level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, position.x, position.y, position.z, 1, 0.08, 0.08, 0.08, 0.005);
            }
        }
    }

    private static void machineGunImpact(ServerLevel level, Vec3 center, boolean frostPop) {
        if (!((Boolean)FreezeConfig.AUTOCANNON_VISUAL_EFFECTS.get()).booleanValue()) {
            return;
        }
        int multiplier = frostPop ? 3 : 1;
        level.sendParticles((ParticleOptions)CYAN_GLOW, center.x, center.y, center.z, 4 * multiplier, 0.3 * (double)multiplier, 0.3 * (double)multiplier, 0.3 * (double)multiplier, 0.06);
        level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, center.x, center.y, center.z, 3 * multiplier, 0.25 * (double)multiplier, 0.25 * (double)multiplier, 0.25 * (double)multiplier, 0.05);
        level.sendParticles((ParticleOptions)ICE_SHARD, center.x, center.y, center.z, 2 * multiplier, 0.2 * (double)multiplier, 0.2 * (double)multiplier, 0.2 * (double)multiplier, 0.09);
        if (frostPop) {
            level.sendParticles((ParticleOptions)ParticleTypes.GUST_EMITTER_SMALL, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
            CryogenicAutocannonEffects.spawnRings(level, center, 1.0, 2.0);
            CryogenicAutocannonEffects.playImpactSounds(level, center, 0.65f, 1.28f);
            CryogenicAutocannonEffects.pulsePlayers(level, center, 14.0, 18, 0.07f, 0.22f, 0.025f);
        }
    }

    private static void armorPiercingImpact(ServerLevel level, Vec3 center) {
        if (!((Boolean)FreezeConfig.AUTOCANNON_VISUAL_EFFECTS.get()).booleanValue()) {
            return;
        }
        level.sendParticles((ParticleOptions)ParticleTypes.GUST_EMITTER_SMALL, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles((ParticleOptions)PALE_FROST, center.x, center.y, center.z, 28, 1.15, 0.95, 1.15, 0.12);
        level.sendParticles((ParticleOptions)DEEP_CRYO, center.x, center.y, center.z, 18, 0.9, 0.75, 0.9, 0.11);
        level.sendParticles((ParticleOptions)ICE_SHARD, center.x, center.y, center.z, 22, 1.0, 0.9, 1.0, 0.16);
        level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, center.x, center.y, center.z, 30, 1.3, 1.1, 1.3, 0.1);
        CryogenicAutocannonEffects.spawnRings(level, center, 1.4, 2.8, 4.2);
        CryogenicAutocannonEffects.playImpactSounds(level, center, 1.0f, 0.92f);
        CryogenicAutocannonEffects.pulsePlayers(level, center, 24.0, 34, 0.12f, 0.46f, 0.05f);
    }

    private static void spawnRings(ServerLevel level, Vec3 center, double ... radii) {
        for (int ring = 0; ring < radii.length; ++ring) {
            double radius = radii[ring];
            int points = Math.max(12, Mth.ceil((double)(radius * 7.0)));
            for (int i = 0; i < points; ++i) {
                double angle = Math.PI * 2 * (double)i / (double)points + (double)ring * 0.22;
                double x = center.x + Math.cos(angle) * radius;
                double z = center.z + Math.sin(angle) * radius;
                double y = center.y + Math.sin(angle * 3.0) * 0.18;
                level.sendParticles((ParticleOptions)((ring & 1) == 0 ? CYAN_GLOW : PALE_FROST), x, y, z, 1, 0.04, 0.08, 0.04, 0.0);
                if (ring != radii.length - 1 || i % 3 != 0) continue;
                level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, x, y + 0.15, z, 1, 0.08, 0.12, 0.08, 0.015);
            }
        }
    }

    private static void playImpactSounds(ServerLevel level, Vec3 center, float volume, float pitch) {
        BlockPos pos = BlockPos.containing((Position)center);
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, volume, pitch);
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, volume * 0.7f, Math.max(0.55f, pitch - 0.18f));
    }

    private static void pulsePlayers(ServerLevel level, Vec3 center, double reach, int duration, float screenStrength, float shakeMagnitude, float jitter) {
        for (ServerPlayer player : level.players()) {
            double distance = player.position().distanceTo(center);
            if (distance > reach) continue;
            float exposure = (float)Mth.clamp((double)(1.0 - distance / reach), (double)0.18, (double)1.0);
            int delay = Mth.floor((double)(distance / 18.0));
            if (((Boolean)FreezeConfig.COLD_SCREEN_PULSE.get()).booleanValue()) {
                float insulation = CryogenicInsulation.screenMultiplier((LivingEntity)player);
                PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new ColdScreenPayload(delay, Math.max(4, Math.round((float)duration * exposure * insulation)), screenStrength * exposure * insulation), (CustomPacketPayload[])new CustomPacketPayload[0]);
            }
            if (!((Boolean)FreezeConfig.SCREEN_SHAKE.get()).booleanValue() || !(shakeMagnitude > 0.0f)) continue;
            ScreenShakeEffect shake = new ScreenShakeEffect(delay, shakeMagnitude * exposure, jitter * exposure, center.x, center.y, center.z);
            RPLNetwork.sendToClientPlayer((RootPacket)new ClientboundShakeScreenPacket(shake), (ServerPlayer)player);
        }
    }

    private static boolean canAffect(LivingEntity target) {
        Player player;
        if (target instanceof Player && ((player = (Player)target).isCreative() || player.isSpectator())) {
            return false;
        }
        return CryogenicAutocannonEffects.isParasite(target) || (Boolean)FreezeConfig.AUTOCANNON_AFFECT_NON_SPORE.get() != false;
    }

    private static boolean isParasite(LivingEntity target) {
        return target.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES);
    }

    private static void applyFrostbite(LivingEntity target, int duration, int amplifier) {
        MobEffectInstance existing = target.getEffect(Seffects.FROSTBITE);
        int finalDuration = existing == null ? duration : Math.max(duration, existing.getDuration());
        int finalAmplifier = existing == null ? amplifier : Math.max(amplifier, existing.getAmplifier());
        target.addEffect(new MobEffectInstance(Seffects.FROSTBITE, finalDuration, finalAmplifier));
    }

    private static void hurtWithCold(LivingEntity target, float amount) {
        float scaled = (float)((double)amount * (Double)FreezeConfig.AUTOCANNON_COLD_MULTIPLIER.get()) * CryogenicInsulation.damageMultiplier(target);
        if (scaled > 0.0f) {
            target.hurt(target.damageSources().freeze(), scaled);
        }
    }

    private static void triggerFrostbiteAdvancement(@Nullable Entity source, Level level, Vec3 position) {
        ServerPlayer player = null;
        if (source instanceof ServerPlayer) {
            ServerPlayer serverPlayer;
            player = serverPlayer = (ServerPlayer)source;
        } else {
            Player nearest = level.getNearestPlayer(position.x, position.y, position.z, 64.0, entity -> {
                ServerPlayer candidate;
                return entity instanceof ServerPlayer && !(candidate = (ServerPlayer)entity).isSpectator();
            });
            if (nearest instanceof ServerPlayer) {
                ServerPlayer serverPlayer;
                player = serverPlayer = (ServerPlayer)nearest;
            }
        }
        if (player != null) {
            ((PlayerTrigger)SporeCannonsCriteria.FROSTBITE_APPLIED.get()).trigger(player);
        }
    }

    static enum TrailType {
        MACHINE_GUN,
        ARMOR_PIERCING,
        FLAK;

    }
}

