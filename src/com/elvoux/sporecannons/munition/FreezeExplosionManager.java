/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 *  net.neoforged.neoforge.network.PacketDistributor
 *  org.joml.Vector3f
 *  rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeEffect
 *  rbasamoyai.ritchiesprojectilelib.network.ClientboundShakeScreenPacket
 *  rbasamoyai.ritchiesprojectilelib.network.RPLNetwork
 *  rbasamoyai.ritchiesprojectilelib.network.RootPacket
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.munition.CryogenicInsulation;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.network.ColdScreenPayload;
import com.elvoux.sporecannons.registry.SporeCannonsSounds;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
import rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeEffect;
import rbasamoyai.ritchiesprojectilelib.network.ClientboundShakeScreenPacket;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

public final class FreezeExplosionManager {
    private static final Map<ServerLevel, ArrayDeque<ExplosionTask>> TASKS = new WeakHashMap<ServerLevel, ArrayDeque<ExplosionTask>>();
    private static final BlockParticleOption ICE_SHARD = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.PACKED_ICE.defaultBlockState());
    private static final DustParticleOptions CYAN_GLOW = new DustParticleOptions(new Vector3f(0.12f, 0.92f, 1.0f), 1.15f);
    private static final DustParticleOptions PALE_FROST = new DustParticleOptions(new Vector3f(0.72f, 0.96f, 1.0f), 1.45f);
    private static final DustParticleOptions DEEP_CRYO = new DustParticleOptions(new Vector3f(0.04f, 0.42f, 0.72f), 0.95f);

    private FreezeExplosionManager() {
    }

    public static void trigger(ServerLevel level, Vec3 center, FreezeShellTier tier) {
        if (!((Boolean)FreezeConfig.EXPLOSION_EFFECTS.get()).booleanValue()) {
            return;
        }
        ExplosionTask task = new ExplosionTask(level, center, tier);
        task.start();
        TASKS.computeIfAbsent(level, ignored -> new ArrayDeque()).addLast(task);
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        ArrayDeque<ExplosionTask> tasks = TASKS.get(level2);
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        int count = tasks.size();
        for (int i = 0; i < count; ++i) {
            ExplosionTask task = tasks.removeFirst();
            if (!task.tick()) continue;
            tasks.addLast(task);
        }
        if (tasks.isEmpty()) {
            TASKS.remove(level2);
        }
    }

    private static final class ExplosionTask {
        private final ServerLevel level;
        private final Vec3 center;
        private final FreezeShellTier tier;
        private final int effectTicks;
        private final int waveTicks;
        private int age;

        private ExplosionTask(ServerLevel level, Vec3 center, FreezeShellTier tier) {
            this.level = level;
            this.center = center;
            this.tier = tier;
            this.effectTicks = Math.max(28 + tier.ordinal() * 10, tier.snowfallTicks());
            this.waveTicks = 10 + tier.ordinal() * 10;
        }

        private void start() {
            int power = this.tier.ordinal();
            int burst = 28 + power * 22;
            this.level.sendParticles((ParticleOptions)ParticleTypes.FLASH, this.center.x, this.center.y, this.center.z, 1 + power / 2, 0.15, 0.15, 0.15, 0.0);
            this.level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, this.center.x, this.center.y, this.center.z, burst, 1.5 + (double)power, 1.0 + (double)power * 0.5, 1.5 + (double)power, 0.12 + (double)power * 0.015);
            this.level.sendParticles((ParticleOptions)ICE_SHARD, this.center.x, this.center.y, this.center.z, 18 + power * 14, 1.2 + (double)power * 0.45, 0.8 + (double)power * 0.25, 1.2 + (double)power * 0.45, 0.18 + (double)power * 0.02);
            this.level.sendParticles((ParticleOptions)CYAN_GLOW, this.center.x, this.center.y + 0.5, this.center.z, 22 + power * 18, 1.0 + (double)power * 0.5, 0.8 + (double)power * 0.25, 1.0 + (double)power * 0.5, 0.08 + (double)power * 0.012);
            if (power >= 1) {
                this.level.sendParticles((ParticleOptions)ParticleTypes.GUST_EMITTER_SMALL, this.center.x, this.center.y + 0.25, this.center.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
            if (power >= 3) {
                this.level.sendParticles((ParticleOptions)ParticleTypes.GUST_EMITTER_LARGE, this.center.x, this.center.y + 0.5, this.center.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
            float volume = 4.0f + (float)power * 2.4f;
            this.level.playSound(null, BlockPos.containing((Position)this.center), (SoundEvent)SporeCannonsSounds.FREEZE_SHELL_DETONATIONS.get((Object)this.tier).get(), SoundSource.BLOCKS, volume, 1.0f);
            this.level.playSound(null, BlockPos.containing((Position)this.center), (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, volume, 0.9f - (float)power * 0.065f);
            this.level.playSound(null, BlockPos.containing((Position)this.center), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, Math.min(8.0f, volume * 0.65f), 1.3f - (float)power * 0.08f);
            if (power >= 2) {
                this.level.playSound(null, BlockPos.containing((Position)this.center), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, Math.min(10.0f, volume * 0.75f), 0.78f - (float)power * 0.045f);
            }
            this.sendScreenShake();
            this.sendColdScreenEffect();
        }

        private boolean tick() {
            ++this.age;
            int power = this.tier.ordinal();
            if (this.age <= this.waveTicks) {
                this.spawnShockwave(this.age, 0, 1.0);
                this.spawnPlayerShockFront(this.age, 1.0, 0);
            }
            if (power >= 3 && this.age > 5 && this.age - 5 <= this.waveTicks) {
                this.spawnShockwave(this.age - 5, 1, 0.78);
                this.spawnPlayerShockFront(this.age - 5, 0.78, 1);
            }
            if (power >= 5 && this.age > 10 && this.age - 10 <= this.waveTicks) {
                this.spawnShockwave(this.age - 10, 2, 0.58);
                this.spawnPlayerShockFront(this.age - 10, 0.58, 2);
            }
            if (this.age % 3 == 0 && ((Boolean)FreezeConfig.LOCAL_SNOWFALL.get()).booleanValue()) {
                this.spawnLocalWeather();
            }
            if (this.age <= 18 + power * 3 && this.age % 2 == 0) {
                this.spawnImpactCrown();
            }
            if (power >= 3 && this.age <= 20 + power * 3 && this.age % 2 == 0) {
                this.spawnCryoColumn();
            }
            if (power >= 3 && this.age <= 50 + power * 8 && this.age % 4 == 0) {
                this.spawnAuroraCurtain();
            }
            if (power >= 4 && this.age <= 8) {
                this.level.sendParticles((ParticleOptions)ParticleTypes.REVERSE_PORTAL, this.center.x, this.center.y + 0.5, this.center.z, 12 + power * 4, 2.0 + (double)power, 1.5 + (double)power * 0.4, 2.0 + (double)power, 0.12);
            }
            if (power >= 4 && this.age == 9) {
                this.spawnSecondaryBurst();
            }
            if (((Boolean)FreezeConfig.DELAYED_AUDIO.get()).booleanValue()) {
                this.playDelayedAudio();
            }
            return this.age < this.effectTicks;
        }

        private void spawnShockwave(int ringAge, int ringIndex, double scale) {
            int power = this.tier.ordinal();
            double progress = (double)ringAge / (double)this.waveTicks;
            double eased = 1.0 - Math.pow(1.0 - progress, 2.0);
            double radius = Math.max(0.75, (double)this.tier.entityRadius() * eased * scale);
            int points = Math.max(24, Math.min(104, 30 + power * 14 - ringIndex * 8));
            double rotation = (double)ringAge * (0.17 + (double)power * 0.011) + (double)ringIndex * 0.31;
            for (int i = 0; i < points; ++i) {
                double z;
                double angle = Math.PI * 2 * (double)i / (double)points + rotation;
                double x = this.center.x + Math.cos(angle) * radius;
                BlockPos pos = BlockPos.containing((double)x, (double)this.center.y, (double)(z = this.center.z + Math.sin(angle) * radius));
                if (!this.level.hasChunkAt(pos)) continue;
                double y = this.center.y + 0.3 + Math.sin(angle * 3.0 + (double)ringAge * 0.25) * (0.2 + (double)power * 0.05);
                this.level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, x, y, z, 3, 0.35, 0.18 + (double)power * 0.05, 0.35, 0.035);
                this.level.sendParticles((ParticleOptions)(ringIndex == 0 ? CYAN_GLOW : PALE_FROST), x, y + 0.15, z, 1, 0.12, 0.18, 0.12, 0.01);
                if (power >= 1 && (i & 1) == 0) {
                    this.level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, x, y - 0.15, z, 2, 0.55, 0.16, 0.55, 0.008);
                }
                if (power < 2 || i % 4 != 0 || ringIndex != 0) continue;
                this.level.sendParticles((ParticleOptions)ICE_SHARD, x, y + 0.2, z, 3, 0.35, 0.65, 0.35, 0.1);
            }
        }

        private void spawnPlayerShockFront(int ringAge, double scale, int ringIndex) {
            int power = this.tier.ordinal();
            double progress = (double)ringAge / (double)this.waveTicks;
            double eased = 1.0 - Math.pow(1.0 - progress, 2.0);
            double radius = (double)this.tier.entityRadius() * eased * scale;
            double width = 4.0 + (double)power * 1.25;
            for (ServerPlayer player : this.level.players()) {
                double distance = player.position().distanceTo(this.center);
                if (Math.abs(distance - radius) > width) continue;
                int density = 10 + power * 5 - ringIndex * 2;
                double spread = 2.8 + (double)power * 0.65;
                this.level.sendParticles((ParticleOptions)ParticleTypes.GUST, player.getX(), player.getY() + 0.6, player.getZ(), 2 + power / 2, spread, 1.2, spread, 0.12);
                this.level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, player.getX(), player.getY() + 1.5, player.getZ(), density, spread, 1.9 + (double)power * 0.25, spread, 0.11);
                this.level.sendParticles((ParticleOptions)(ringIndex == 0 ? CYAN_GLOW : PALE_FROST), player.getX(), player.getY() + 0.9, player.getZ(), Math.max(6, density / 2), spread, 1.4, spread, 0.05);
                this.level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, player.getX(), player.getY() + 0.25, player.getZ(), 5 + power * 2, spread, 0.7, spread, 0.035);
                if (power < 3) continue;
                this.level.sendParticles((ParticleOptions)ICE_SHARD, player.getX(), player.getY() + 0.8, player.getZ(), 5 + power * 2, spread * 0.75, 1.5, spread * 0.75, 0.12);
            }
        }

        private void spawnImpactCrown() {
            int power = this.tier.ordinal();
            double radius = 2.2 + (double)power * 0.85 + (double)this.age * (0.08 + (double)power * 0.015);
            int points = 12 + power * 4;
            for (int i = 0; i < points; ++i) {
                double angle = Math.PI * 2 * (double)i / (double)points + (double)this.age * 0.19;
                double x = this.center.x + Math.cos(angle) * radius;
                double z = this.center.z + Math.sin(angle) * radius;
                double y = this.center.y + 0.5 + Math.sin(angle * 2.0 + (double)this.age * 0.3) * (1.0 + (double)power * 0.25);
                this.level.sendParticles((ParticleOptions)((i & 1) == 0 ? CYAN_GLOW : DEEP_CRYO), x, y, z, 1, 0.08, 0.15, 0.08, 0.015);
                if (power < 2 || i % 4 != 0) continue;
                this.level.sendParticles((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, 0.08, 0.16, 0.08, 0.015);
            }
        }

        private void spawnAuroraCurtain() {
            int power = this.tier.ordinal();
            for (ServerPlayer player : this.level.players()) {
                if (player.position().distanceTo(this.center) > (double)this.tier.entityRadius() + 24.0) continue;
                double spread = 3.5 + (double)power * 0.7;
                double height = 4.0 + (double)power * 0.8;
                this.level.sendParticles((ParticleOptions)CYAN_GLOW, player.getX(), player.getY() + height, player.getZ(), 4 + power, spread, 1.5, spread, 0.025);
                this.level.sendParticles((ParticleOptions)DEEP_CRYO, player.getX(), player.getY() + height * 0.72, player.getZ(), 3 + power, spread * 0.8, 1.8, spread * 0.8, 0.018);
                if (power < 4) continue;
                this.level.sendParticles((ParticleOptions)ParticleTypes.END_ROD, player.getX(), player.getY() + height, player.getZ(), 2 + power / 2, spread, 1.4, spread, 0.02);
            }
        }

        private void spawnLocalWeather() {
            int power = this.tier.ordinal();
            double waveRadius = (double)this.tier.entityRadius() * (1.0 - Math.pow(1.0 - Math.min(1.0, (double)this.age / (double)this.waveTicks), 2.0));
            for (ServerPlayer player : this.level.players()) {
                double distance = player.position().distanceTo(this.center);
                if (distance > (double)this.tier.entityRadius() + 16.0 || waveRadius + 14.0 < distance) continue;
                int flakes = (int)Math.round((double)(11 + power * 5) * (Double)FreezeConfig.SNOWFALL_INTENSITY.get());
                boolean outdoors = this.level.canSeeSky(player.blockPosition().above());
                if (player.isUnderWater()) {
                    this.level.sendParticles((ParticleOptions)ParticleTypes.BUBBLE, player.getX(), player.getY() + 0.8, player.getZ(), Math.max(4, flakes), 3.5 + (double)power, 1.7, 3.5 + (double)power, 0.08);
                    this.level.sendParticles((ParticleOptions)ParticleTypes.BUBBLE_POP, player.getX(), player.getY() + 0.8, player.getZ(), Math.max(2, flakes / 2), 3.0 + (double)power, 1.5, 3.0 + (double)power, 0.05);
                } else if (this.level.dimensionType().ultraWarm()) {
                    this.level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, player.getX(), player.getY() + 0.7, player.getZ(), Math.max(2, flakes / 2), 4.0 + (double)power, 2.0, 4.0 + (double)power, 0.025);
                } else if (this.level.dimension() == Level.END) {
                    this.level.sendParticles((ParticleOptions)ParticleTypes.END_ROD, player.getX(), player.getY() + 2.0, player.getZ(), Math.max(3, flakes / 2), 4.0 + (double)power, 2.2, 4.0 + (double)power, 0.025);
                    this.level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, player.getX(), player.getY() + 3.0, player.getZ(), Math.max(3, flakes / 2), 4.0 + (double)power, 2.0, 4.0 + (double)power, 0.025);
                } else if (outdoors) {
                    this.level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, player.getX(), player.getY() + 4.0 + (double)power * 0.4, player.getZ(), flakes, 4.0 + (double)power, 2.5 + (double)power * 0.35, 4.0 + (double)power, 0.035);
                } else {
                    this.level.sendParticles((ParticleOptions)ICE_SHARD, player.getX(), player.getY() + 1.0, player.getZ(), Math.max(2, flakes / 3), 3.0 + (double)power * 0.5, 1.3, 3.0 + (double)power * 0.5, 0.025);
                }
                if (power < 2 && outdoors) continue;
                this.level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, player.getX(), player.getY() + 0.5, player.getZ(), 3 + power, 3.5 + (double)power, 1.1, 3.5 + (double)power, 0.008);
            }
        }

        private void spawnCryoColumn() {
            int power = this.tier.ordinal();
            double height = 12.0 + (double)power * 8.0;
            double y = this.center.y + this.level.random.nextDouble() * height;
            this.level.sendParticles((ParticleOptions)ParticleTypes.END_ROD, this.center.x, y, this.center.z, 3 + power, 0.8 + (double)power * 0.35, 1.8, 0.8 + (double)power * 0.35, 0.03);
            this.level.sendParticles((ParticleOptions)CYAN_GLOW, this.center.x, y, this.center.z, 5 + power * 2, 1.0 + (double)power * 0.4, 2.2, 1.0 + (double)power * 0.4, 0.035);
            this.level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, this.center.x, y, this.center.z, 5 + power * 2, 1.4 + (double)power * 0.45, 1.5, 1.4 + (double)power * 0.45, 0.04);
        }

        private void spawnSecondaryBurst() {
            int power = this.tier.ordinal();
            this.level.sendParticles((ParticleOptions)ParticleTypes.FLASH, this.center.x, this.center.y + 0.5, this.center.z, 2, 0.4, 0.4, 0.4, 0.0);
            this.level.sendParticles((ParticleOptions)ICE_SHARD, this.center.x, this.center.y + 0.5, this.center.z, 70 + power * 20, 3.0 + (double)power, 2.0 + (double)power * 0.5, 3.0 + (double)power, 0.24);
            this.level.sendParticles((ParticleOptions)ParticleTypes.GUST_EMITTER_LARGE, this.center.x, this.center.y + 0.5, this.center.z, 1, 0.0, 0.0, 0.0, 0.0);
            this.level.sendParticles((ParticleOptions)ParticleTypes.SONIC_BOOM, this.center.x, this.center.y + 1.0, this.center.z, 1, 0.0, 0.0, 0.0, 0.0);
            this.level.sendParticles((ParticleOptions)CYAN_GLOW, this.center.x, this.center.y + 0.8, this.center.z, 80 + power * 24, 4.0 + (double)power, 2.5 + (double)power * 0.6, 4.0 + (double)power, 0.18);
            this.level.playSound(null, BlockPos.containing((Position)this.center), (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 8.0f + (float)power, 0.55f - (float)(power - 4) * 0.05f);
        }

        private void playDelayedAudio() {
            double reach = (double)this.tier.entityRadius() + 192.0;
            int power = this.tier.ordinal();
            for (ServerPlayer player : this.level.players()) {
                int arrival;
                double distance = player.position().distanceTo(this.center);
                if (distance <= 24.0 || distance > reach || this.age != (arrival = Math.max(1, Mth.floor((double)(distance / 17.0))))) continue;
                float falloff = (float)Mth.clamp((double)(1.0 - distance / reach), (double)0.12, (double)1.0);
                float volume = (2.2f + (float)power * 0.85f) * falloff;
                float pitch = 0.96f - (float)power * 0.055f;
                player.playNotifySound((SoundEvent)SporeCannonsSounds.FREEZE_SHELL_DETONATIONS.get((Object)this.tier).get(), SoundSource.BLOCKS, volume, pitch);
                player.playNotifySound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, volume * 0.72f, Math.max(0.5f, pitch - 0.18f));
                if (player.isUnderWater()) {
                    player.playNotifySound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, SoundSource.AMBIENT, volume * 0.45f, 0.65f);
                    continue;
                }
                if (this.level.dimensionType().ultraWarm()) {
                    player.playNotifySound(SoundEvents.FIRE_EXTINGUISH, SoundSource.AMBIENT, volume * 0.55f, 0.7f);
                    continue;
                }
                if (this.level.dimension() == Level.END) {
                    player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, volume * 0.45f, 0.6f);
                    continue;
                }
                if (this.level.canSeeSky(player.blockPosition().above())) continue;
                player.playNotifySound(SoundEvents.GLASS_BREAK, SoundSource.AMBIENT, volume * 0.35f, 0.55f);
            }
        }

        private void sendScreenShake() {
            if (!((Boolean)FreezeConfig.SCREEN_SHAKE.get()).booleanValue()) {
                return;
            }
            int power = this.tier.ordinal();
            double reach = (double)this.tier.entityRadius() + 48.0;
            for (ServerPlayer player : this.level.players()) {
                double distance = player.position().distanceTo(this.center);
                if (distance > reach) continue;
                float exposure = (float)Mth.clamp((double)(1.0 - distance / reach), (double)0.12, (double)1.0);
                float magnitude = (0.9f + (float)power * 0.58f) * exposure;
                float jitter = (0.12f + (float)power * 0.035f) * exposure;
                int delay = Mth.floor((double)(distance / (10.0 + (double)power * 1.5)));
                ScreenShakeEffect shake = new ScreenShakeEffect(delay, magnitude, jitter, this.center.x, this.center.y, this.center.z);
                RPLNetwork.sendToClientPlayer((RootPacket)new ClientboundShakeScreenPacket(shake), (ServerPlayer)player);
            }
        }

        private void sendColdScreenEffect() {
            if (!((Boolean)FreezeConfig.COLD_SCREEN_PULSE.get()).booleanValue()) {
                return;
            }
            float[] baseIntensity = new float[]{0.12f, 0.16f, 0.22f, 0.28f, 0.36f, 0.45f};
            double reach = (double)this.tier.entityRadius() + 48.0;
            double minimumDot = Math.cos(Math.toRadians(80.0));
            double fullDot = Math.cos(Math.toRadians(15.0));
            for (ServerPlayer player : this.level.players()) {
                double cover;
                double distanceExposure;
                double dot;
                Vec3 fromEye = this.center.subtract(player.getEyePosition());
                double distance = fromEye.length();
                if (distance < 0.001 || distance > reach || (dot = player.getLookAngle().normalize().dot(fromEye.scale(1.0 / distance))) <= minimumDot) continue;
                double gaze = Mth.clamp((double)((dot - minimumDot) / (fullDot - minimumDot)), (double)0.0, (double)1.0);
                double exposure = gaze * (distanceExposure = Mth.clamp((double)(1.0 - 0.8 * distance / reach), (double)0.2, (double)1.0)) * (cover = this.isCovered(player) ? (Double)FreezeConfig.COVER_MULTIPLIER.get() : 1.0);
                if (exposure < 0.04) continue;
                int duration = Math.max(10, (int)Math.round((double)this.tier.screenEffectTicks() * (0.35 + 0.65 * exposure)));
                int delay = Mth.floor((double)(distance / (10.0 + (double)this.tier.ordinal() * 1.5)));
                float strength = (float)Mth.clamp((double)((double)baseIntensity[this.tier.ordinal()] * (0.35 + 0.65 * exposure)), (double)0.03, (double)0.55);
                float insulation = CryogenicInsulation.screenMultiplier((LivingEntity)player);
                PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new ColdScreenPayload(delay, Math.max(4, Math.round((float)duration * insulation)), strength * insulation), (CustomPacketPayload[])new CustomPacketPayload[0]);
            }
        }

        private boolean isCovered(ServerPlayer player) {
            BlockHitResult hit = this.level.clip(new ClipContext(player.getEyePosition(), this.center, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)player));
            return hit.getType() != HitResult.Type.MISS && hit.getLocation().distanceToSqr(this.center) > 0.36;
        }
    }
}

