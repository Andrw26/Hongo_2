/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile$ImpactResult
 *  rbasamoyai.createbigcannons.munitions.ProjectileContext
 *  rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonProjectile
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CryogenicAutocannonEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonProjectile;

public final class CryogenicAPProjectile
extends APAutocannonProjectile {
    public CryogenicAPProjectile(EntityType<? extends APAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    public void tick() {
        Level level;
        super.tick();
        if (!this.isRemoved() && (level = this.level()) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CryogenicAutocannonEffects.projectileTrail(serverLevel, this.position(), CryogenicAutocannonEffects.TrailType.ARMOR_PIERCING, this.tickCount);
        }
    }

    protected boolean onHitEntity(Entity entity, ProjectileContext context) {
        boolean result = super.onHitEntity(entity, context);
        if (!this.level().isClientSide) {
            CryogenicAutocannonEffects.armorPiercingHit(entity, this.getOwner());
        }
        return result;
    }

    protected boolean onImpact(HitResult hitResult, AbstractCannonProjectile.ImpactResult impactResult, ProjectileContext context) {
        boolean result = super.onImpact(hitResult, impactResult, context);
        if (hitResult instanceof BlockHitResult) {
            BlockHitResult blockHit = (BlockHitResult)hitResult;
            Level level = this.level();
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                CryogenicAutocannonEffects.armorPiercingBlockImpact(serverLevel, blockHit.getBlockPos(), hitResult.getLocation());
            }
        }
        return result;
    }
}

