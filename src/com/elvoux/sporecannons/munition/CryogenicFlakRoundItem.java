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
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.level.Level
 *  rbasamoyai.createbigcannons.index.CBCDataComponents
 *  rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers
 *  rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile
 *  rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent
 *  rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectileProperties
 *  rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CryogenicFlakProjectile;
import com.elvoux.sporecannons.registry.SporeCannonsEntityTypes;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectileProperties;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

public final class CryogenicFlakRoundItem
extends FlakAutocannonRoundItem {
    public CryogenicFlakRoundItem(Item.Properties properties) {
        super(properties);
    }

    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        CryogenicFlakProjectile projectile = (CryogenicFlakProjectile)((EntityType)SporeCannonsEntityTypes.CRYOGENIC_FLAK_AUTOCANNON.get()).create(level);
        if (projectile != null && stack.has(CBCDataComponents.FUZE)) {
            ItemContainerContents contents = (ItemContainerContents)stack.getOrDefault(CBCDataComponents.FUZE, (Object)ItemContainerContents.EMPTY);
            projectile.setFuze(contents.copyOne());
        }
        return projectile;
    }

    public EntityType<?> getEntityType(ItemStack stack) {
        return (EntityType)SporeCannonsEntityTypes.CRYOGENIC_FLAK_AUTOCANNON.get();
    }

    public AutocannonProjectilePropertiesComponent getAutocannonProperties(ItemStack stack) {
        return ((FlakAutocannonProjectileProperties)CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON.getPropertiesOf(this.getEntityType(stack))).autocannonProperties();
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_flak_autocannon_round").withStyle(ChatFormatting.AQUA));
    }
}

