/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 */
package com.elvoux.sporecannons.munition;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CDUComponentItem
extends Item {
    private final String translationKey;

    public CDUComponentItem(Item.Properties properties, String id) {
        super(properties);
        this.translationKey = "tooltip.sporecannons." + id;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add((Component)Component.translatable((String)this.translationKey).withStyle(ChatFormatting.AQUA));
        tooltip.add((Component)Component.translatable((String)(this.translationKey + ".lore")).withStyle(ChatFormatting.DARK_GRAY));
    }
}

