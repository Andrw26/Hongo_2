/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.registry.SporeCannonsEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class CryogenicInsulation {
    private static final int MAX_TOTAL_LEVEL = 16;
    private static final float DAMAGE_REDUCTION_PER_LEVEL = 0.04f;
    private static final float MAX_DAMAGE_REDUCTION = 0.65f;
    private static final float FREEZE_REDUCTION_PER_LEVEL = 0.05f;
    private static final float MAX_FREEZE_REDUCTION = 0.75f;

    private CryogenicInsulation() {
    }

    public static int totalLevel(LivingEntity wearer) {
        Holder.Reference enchantment = wearer.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SporeCannonsEnchantments.CRYOGENIC_INSULATION);
        int total = 0;
        for (ItemStack armor : wearer.getArmorSlots()) {
            total += armor.getEnchantmentLevel((Holder)enchantment);
        }
        return Math.min(16, total);
    }

    public static float damageMultiplier(LivingEntity wearer) {
        float reduction = Math.min(0.65f, (float)CryogenicInsulation.totalLevel(wearer) * 0.04f);
        return 1.0f - reduction;
    }

    public static float screenMultiplier(LivingEntity wearer) {
        return CryogenicInsulation.damageMultiplier(wearer);
    }

    public static int protectedFrozenTicks(LivingEntity wearer, int ticks) {
        float reduction = Math.min(0.75f, (float)CryogenicInsulation.totalLevel(wearer) * 0.05f);
        return Math.max(0, Math.round((float)ticks * (1.0f - reduction)));
    }
}

