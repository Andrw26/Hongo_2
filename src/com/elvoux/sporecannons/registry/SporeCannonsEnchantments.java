/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.enchantment.Enchantment
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.SporeCannons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public final class SporeCannonsEnchantments {
    public static final ResourceKey<Enchantment> CRYOGENIC_INSULATION = ResourceKey.create((ResourceKey)Registries.ENCHANTMENT, (ResourceLocation)SporeCannons.id("cryogenic_insulation"));

    private SporeCannonsEnchantments() {
    }
}

