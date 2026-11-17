/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.event.entity.living.LivingHealEvent
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.elvoux.sporecannons.field;

import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.field.WhiteoutTargeting;
import com.elvoux.sporecannons.munition.ContainmentFieldGeometry;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class ActiveContainmentFields {
    private static final Map<ServerLevel, Map<BlockPos, ActiveField>> FIELDS = new WeakHashMap<ServerLevel, Map<BlockPos, ActiveField>>();

    private ActiveContainmentFields() {
    }

    public static void track(ServerLevel level, BlockPos origin, ContainmentFieldGeometry geometry) {
        FIELDS.computeIfAbsent(level, ignored -> new HashMap()).put(origin.immutable(), new ActiveField(geometry, level.getGameTime()));
    }

    public static void remove(ServerLevel level, BlockPos origin) {
        Map<BlockPos, ActiveField> fields = FIELDS.get(level);
        if (fields == null) {
            return;
        }
        fields.remove(origin);
        if (fields.isEmpty()) {
            FIELDS.remove(level);
        }
    }

    public static void onLivingHeal(LivingHealEvent event) {
        block6: {
            block5: {
                LivingEntity entity = event.getEntity();
                Level level = entity.level();
                if (!(level instanceof ServerLevel)) break block5;
                ServerLevel level2 = (ServerLevel)level;
                if (WhiteoutTargeting.isAffected((Entity)entity) && ActiveContainmentFields.contains(level2, entity)) break block6;
            }
            return;
        }
        float multiplier = ((Double)FreezeConfig.PROJECTOR_REGENERATION_MULTIPLIER.get()).floatValue();
        if (multiplier <= 0.0f) {
            event.setCanceled(true);
        } else {
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        Map<BlockPos, ActiveField> fields = FIELDS.get(level2);
        if (fields == null) {
            return;
        }
        long oldestValidTick = level2.getGameTime() - 2L;
        fields.entrySet().removeIf(entry -> ((ActiveField)((Object)((Object)entry.getValue()))).lastSeen() < oldestValidTick);
        if (fields.isEmpty()) {
            FIELDS.remove(level2);
        }
    }

    private static boolean contains(ServerLevel level, LivingEntity entity) {
        Map<BlockPos, ActiveField> fields = FIELDS.get(level);
        if (fields == null || fields.isEmpty()) {
            return false;
        }
        for (Map.Entry<BlockPos, ActiveField> entry : fields.entrySet()) {
            if (!entry.getValue().geometry().contains(entry.getKey(), entity.getX(), entity.getY(), entity.getZ())) continue;
            return true;
        }
        return false;
    }

    private record ActiveField(ContainmentFieldGeometry geometry, long lastSeen) {
    }
}

