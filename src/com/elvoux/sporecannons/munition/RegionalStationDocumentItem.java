/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.stats.Stats
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.client.RegionalStationDocumentScreen;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class RegionalStationDocumentItem
extends Item {
    private final String recordId;
    private final int pages;

    public RegionalStationDocumentItem(Item.Properties properties, String recordId, int pages) {
        super(properties);
        this.recordId = recordId;
        this.pages = pages;
    }

    public List<Component> pages() {
        return IntStream.rangeClosed(1, this.pages).mapToObj(page -> Component.translatable((String)("document.sporecannons.regional_station." + this.recordId + ".page_" + page))).toList();
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            RegionalStationDocumentScreen.open(this.pages());
        }
        player.awardStat(Stats.ITEM_USED.get((Object)this));
        return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide());
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.regional_station_document").withStyle(ChatFormatting.AQUA));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.regional_station_document.use").withStyle(ChatFormatting.DARK_GRAY));
    }
}

