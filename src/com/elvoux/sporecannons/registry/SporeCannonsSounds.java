/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.level.block.SoundType
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SporeCannonsSounds {
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create((ResourceKey)Registries.SOUND_EVENT, (String)"sporecannons");
    public static final DeferredHolder<SoundEvent, SoundEvent> FREEZE_SHELL_PLACE = SporeCannonsSounds.register("freeze_shell_place");
    public static final DeferredHolder<SoundEvent, SoundEvent> FREEZE_SHELL_REMOVED = SporeCannonsSounds.register("freeze_shell_removed");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINE_FUZE_PLACE = SporeCannonsSounds.register("mine_fuze_place");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINE_FUZE_READY = SporeCannonsSounds.register("mine_fuze_ready");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINE_FUZE_SIGNAL = SporeCannonsSounds.registerFixed("mine_fuze_signal", 6.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> MINE_FUZE_TRIGGER = SporeCannonsSounds.registerFixed("mine_fuze_trigger", 10.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> SCANNER_REMOTE_WARNING = SporeCannonsSounds.registerFixed("scanner_remote_warning", 6.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> CONTAINMENT_PROJECTOR_STARTUP = SporeCannonsSounds.registerFixed("containment_projector_startup", 48.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> STANDARD_FUZE_TRIGGER = SporeCannonsSounds.registerFixed("standard_fuze_trigger", 64.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> SENSOR_FUZE_LOCK = SporeCannonsSounds.registerFixed("sensor_fuze_lock", 80.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> CDU_ACTIVATION_LIGHT = SporeCannonsSounds.registerFixed("cdu_activation_light", 96.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> CDU_ACTIVATION_REINFORCED = SporeCannonsSounds.registerFixed("cdu_activation_reinforced", 160.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> CDU_ACTIVATION_STRATEGIC = SporeCannonsSounds.registerFixed("cdu_activation_strategic", 256.0f);
    public static final DeferredHolder<SoundEvent, SoundEvent> CDU_ACTIVATION_ABSOLUTE_ZERO = SporeCannonsSounds.registerFixed("cdu_activation_absolute_zero", 512.0f);
    public static final Map<FreezeShellTier, DeferredHolder<SoundEvent, SoundEvent>> FREEZE_SHELL_DETONATIONS = new EnumMap<FreezeShellTier, DeferredHolder<SoundEvent, SoundEvent>>(FreezeShellTier.class);
    private static SoundType freezeShellSoundType;

    private static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
        return REGISTER.register(id, () -> SoundEvent.createVariableRangeEvent((ResourceLocation)SporeCannons.id(id)));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerFixed(String id, float range) {
        return REGISTER.register(id, () -> SoundEvent.createFixedRangeEvent((ResourceLocation)SporeCannons.id(id), (float)range));
    }

    public static SoundType freezeShellSoundType() {
        if (freezeShellSoundType == null) {
            freezeShellSoundType = new SoundType(1.0f, 1.0f, (SoundEvent)FREEZE_SHELL_REMOVED.get(), SoundType.METAL.getStepSound(), (SoundEvent)FREEZE_SHELL_PLACE.get(), SoundType.METAL.getHitSound(), SoundType.METAL.getFallSound());
        }
        return freezeShellSoundType;
    }

    private SporeCannonsSounds() {
    }

    static {
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            String id = "freeze_shell_detonation_" + tier.name().toLowerCase(Locale.ROOT);
            FREEZE_SHELL_DETONATIONS.put(tier, SporeCannonsSounds.register(id));
        }
    }
}

