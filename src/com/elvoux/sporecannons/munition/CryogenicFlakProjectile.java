/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CryogenicAutocannonEffects;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public final class CryogenicFlakProjectile
extends FlakAutocannonProjectile {
    public CryogenicFlakProjectile(EntityType<? extends FlakAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    public void tick() {
        Level level;
        super.tick();
        if (!this.isRemoved() && (level = this.level()) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CryogenicAutocannonEffects.projectileTrail(serverLevel, this.position(), CryogenicAutocannonEffects.TrailType.FLAK, this.tickCount);
        }
    }

    protected void detonate(Position position) {
        Level level = this.level();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CryogenicAutocannonEffects.flakBurst(serverLevel, position, this.getOwner());
        }
        super.detonate(position);
    }
}

