/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 *  rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers
 *  rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile
 *  rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonRoundItem
 *  rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent
 *  rbasamoyai.createbigcannons.munitions.autocannon.config.InertAutocannonProjectileProperties
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.registry.SporeCannonsEntityTypes;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonRoundItem;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.autocannon.config.InertAutocannonProjectileProperties;

public final class CryogenicAPRoundItem
extends APAutocannonRoundItem {
    public CryogenicAPRoundItem(Item.Properties properties) {
        super(properties);
    }

    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        return (AbstractAutocannonProjectile)((EntityType)SporeCannonsEntityTypes.CRYOGENIC_AP_AUTOCANNON.get()).create(level);
    }

    public EntityType<?> getEntityType(ItemStack stack) {
        return (EntityType)SporeCannonsEntityTypes.CRYOGENIC_AP_AUTOCANNON.get();
    }

    public AutocannonProjectilePropertiesComponent getAutocannonProperties(ItemStack stack) {
        return ((InertAutocannonProjectileProperties)CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE.getPropertiesOf(this.getEntityType(stack))).autocannonProperties();
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_ap_autocannon_round").withStyle(ChatFormatting.AQUA));
    }
}

