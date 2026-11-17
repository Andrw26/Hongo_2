/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.MobCategory
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.munition.CryogenicAPProjectile;
import com.elvoux.sporecannons.munition.CryogenicFlakProjectile;
import com.elvoux.sporecannons.munition.CryogenicMachineGunProjectile;
import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SporeCannonsEntityTypes {
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.ENTITY_TYPE, (String)"sporecannons");
    public static final Map<FreezeShellTier, DeferredHolder<EntityType<?>, EntityType<FreezeShellProjectile>>> FREEZE_SHELLS = new EnumMap(FreezeShellTier.class);
    public static final DeferredHolder<EntityType<?>, EntityType<FreezeShellProjectile>> FREEZE_SHELL;
    public static final DeferredHolder<EntityType<?>, EntityType<CryogenicMachineGunProjectile>> CRYOGENIC_MACHINE_GUN_BULLET;
    public static final DeferredHolder<EntityType<?>, EntityType<CryogenicAPProjectile>> CRYOGENIC_AP_AUTOCANNON;
    public static final DeferredHolder<EntityType<?>, EntityType<CryogenicFlakProjectile>> CRYOGENIC_FLAK_AUTOCANNON;

    private SporeCannonsEntityTypes() {
    }

    static {
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            FREEZE_SHELLS.put(tier, REGISTER.register(tier.id(), () -> EntityType.Builder.of(FreezeShellProjectile::new, (MobCategory)MobCategory.MISC).sized(0.8f, 0.8f).fireImmune().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(SporeCannons.id(tier.id()).toString())));
        }
        FREEZE_SHELL = FREEZE_SHELLS.get((Object)FreezeShellTier.I);
        CRYOGENIC_MACHINE_GUN_BULLET = REGISTER.register("cryogenic_machine_gun_bullet", () -> EntityType.Builder.of(CryogenicMachineGunProjectile::new, (MobCategory)MobCategory.MISC).sized(0.2f, 0.2f).fireImmune().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(SporeCannons.id("cryogenic_machine_gun_bullet").toString()));
        CRYOGENIC_AP_AUTOCANNON = REGISTER.register("cryogenic_ap_autocannon", () -> EntityType.Builder.of(CryogenicAPProjectile::new, (MobCategory)MobCategory.MISC).sized(0.2f, 0.2f).fireImmune().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(SporeCannons.id("cryogenic_ap_autocannon").toString()));
        CRYOGENIC_FLAK_AUTOCANNON = REGISTER.register("cryogenic_flak_autocannon", () -> EntityType.Builder.of(CryogenicFlakProjectile::new, (MobCategory)MobCategory.MISC).sized(0.2f, 0.2f).fireImmune().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(SporeCannons.id("cryogenic_flak_autocannon").toString()));
    }
}

