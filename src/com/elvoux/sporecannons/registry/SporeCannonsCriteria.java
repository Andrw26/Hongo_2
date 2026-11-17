/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriterionTrigger
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.elvoux.sporecannons.registry;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SporeCannonsCriteria {
    public static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.TRIGGER_TYPE, (String)"sporecannons");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> FROSTBITE_APPLIED = REGISTER.register("frostbite_applied", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> TERRAIN_CLEANED = REGISTER.register("terrain_cleaned", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> HIGH_TIER_NETHER_DETONATION = REGISTER.register("high_tier_nether_detonation", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> TIER_SIX_DETONATION = REGISTER.register("tier_six_detonation", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> BRITTLE_SHATTER = REGISTER.register("brittle_shatter", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> SCAN_COMPLETED = REGISTER.register("scan_completed", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> ZONE_SECURED = REGISTER.register("zone_secured", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> MINE_TRIGGERED = REGISTER.register("mine_triggered", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CARTRIDGE_REFILLED = REGISTER.register("cartridge_refilled", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> PROJECTOR_ACTIVATED = REGISTER.register("projector_activated", PlayerTrigger::new);

    private SporeCannonsCriteria() {
    }
}

