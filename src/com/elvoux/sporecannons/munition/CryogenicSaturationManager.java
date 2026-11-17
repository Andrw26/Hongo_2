/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.event.entity.living.LivingDeathEvent
 *  net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
 *  net.neoforged.neoforge.event.tick.EntityTickEvent$Post
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.field.WhiteoutTargeting;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public final class CryogenicSaturationManager {
    private static final String SATURATION = "SporeCannonsSaturation";
    private static final String EXPIRES_AT = "SporeCannonsSaturationExpires";
    private static final String BRITTLE = "SporeCannonsBrittle";
    private static final String RESPONSIBLE = "SporeCannonsCryoResponsible";
    private static final double BRITTLE_THRESHOLD = 100.0;
    private static final TagKey<EntityType<?>> BRITTLE_RESISTANT = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)SporeCannons.id("brittle_resistant"));

    private CryogenicSaturationManager() {
    }

    public static boolean isParasite(LivingEntity target) {
        return WhiteoutTargeting.isAffected((Entity)target);
    }

    public static boolean isBrittle(LivingEntity target) {
        return target.getPersistentData().getBoolean(BRITTLE);
    }

    public static double saturation(LivingEntity target) {
        return target.getPersistentData().getDouble(SATURATION);
    }

    public static boolean add(LivingEntity target, double amount, @Nullable Entity source) {
        boolean becameBrittle;
        if (!(((Boolean)FreezeConfig.CRYOGENIC_SATURATION.get()).booleanValue() && target.isAlive() && CryogenicSaturationManager.isParasite(target))) {
            return false;
        }
        CompoundTag data = target.getPersistentData();
        CryogenicSaturationManager.rememberResponsible(data, source);
        double resistance = target.getType().is(BRITTLE_RESISTANT) ? 0.5 : 1.0;
        double value = Math.min(100.0, data.getDouble(SATURATION) + amount * resistance);
        data.putDouble(SATURATION, value);
        data.putLong(EXPIRES_AT, target.level().getGameTime() + (long)((Integer)FreezeConfig.SATURATION_DECAY_SECONDS.get()).intValue() * 20L);
        boolean bl = becameBrittle = value >= 100.0 && !data.getBoolean(BRITTLE);
        if (becameBrittle) {
            data.putBoolean(BRITTLE, true);
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), 320));
            Level level = target.level();
            if (level instanceof ServerLevel) {
                ServerLevel level2 = (ServerLevel)level;
                level2.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, target.getX(), target.getY(0.55), target.getZ(), 28, (double)target.getBbWidth() * 0.65, (double)target.getBbHeight() * 0.35, (double)target.getBbWidth() * 0.65, 0.06);
                level2.sendParticles((ParticleOptions)ParticleTypes.ITEM_SNOWBALL, target.getX(), target.getY(0.55), target.getZ(), 16, (double)target.getBbWidth() * 0.55, (double)target.getBbHeight() * 0.3, (double)target.getBbWidth() * 0.55, 0.09);
                level2.playSound(null, target.blockPosition(), SoundEvents.GLASS_HIT, SoundSource.HOSTILE, 1.2f, 0.65f);
            }
        }
        return becameBrittle;
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!CryogenicSaturationManager.isBrittle(target) || event.getSource().is(DamageTypeTags.IS_FREEZING)) {
            return;
        }
        event.setAmount((float)((double)event.getAmount() * (Double)FreezeConfig.BRITTLE_DAMAGE_MULTIPLIER.get()));
    }

    public static void onEntityTick(EntityTickEvent.Post event) {
        LivingEntity target;
        block6: {
            block5: {
                Entity entity = event.getEntity();
                if (!(entity instanceof LivingEntity)) break block5;
                target = (LivingEntity)entity;
                if (!target.level().isClientSide && target.tickCount % 20 == 0) break block6;
            }
            return;
        }
        CompoundTag data = target.getPersistentData();
        if (!data.contains(SATURATION)) {
            return;
        }
        if (target.level().getGameTime() <= data.getLong(EXPIRES_AT)) {
            return;
        }
        data.remove(SATURATION);
        data.remove(EXPIRES_AT);
        data.remove(BRITTLE);
        data.remove(RESPONSIBLE);
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        ServerPlayer direct;
        ServerPlayer player;
        Level level;
        LivingEntity target = event.getEntity();
        if (!CryogenicSaturationManager.isBrittle(target) || !((level = target.level()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        level2.sendParticles((ParticleOptions)ParticleTypes.ITEM_SNOWBALL, target.getX(), target.getY(0.5), target.getZ(), 42, (double)target.getBbWidth() * 0.9, (double)target.getBbHeight() * 0.45, (double)target.getBbWidth() * 0.9, 0.16);
        level2.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, target.getX(), target.getY(0.5), target.getZ(), 34, (double)target.getBbWidth() * 0.8, (double)target.getBbHeight() * 0.4, (double)target.getBbWidth() * 0.8, 0.12);
        level2.playSound(null, target.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.7f, 0.72f);
        Entity entity = event.getSource().getEntity();
        ServerPlayer serverPlayer = player = entity instanceof ServerPlayer ? (direct = (ServerPlayer)entity) : CryogenicSaturationManager.findResponsible(level2, target.getPersistentData());
        if (player != null) {
            ((PlayerTrigger)SporeCannonsCriteria.BRITTLE_SHATTER.get()).trigger(player);
        }
    }

    private static void rememberResponsible(CompoundTag data, @Nullable Entity source) {
        if (source instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)source;
            data.putUUID(RESPONSIBLE, player.getUUID());
        }
    }

    @Nullable
    private static ServerPlayer findResponsible(ServerLevel level, CompoundTag data) {
        if (!data.hasUUID(RESPONSIBLE)) {
            return null;
        }
        UUID id = data.getUUID(RESPONSIBLE);
        return level.getServer().getPlayerList().getPlayer(id);
    }
}

