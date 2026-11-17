/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.block.Block
 *  rbasamoyai.createbigcannons.munitions.FuzedProjectileBlockItem
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.FreezeShellTier;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import rbasamoyai.createbigcannons.munitions.FuzedProjectileBlockItem;

public final class FreezeShellItem
extends FuzedProjectileBlockItem {
    public FreezeShellItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        FreezeShellTier tier = FreezeShellTier.fromBlock(this.getBlock());
        tooltip.add((Component)Component.translatable((String)("tooltip.sporecannons." + tier.id() + ".lore")).withStyle(ChatFormatting.AQUA));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.freeze_shell.effect").withStyle(ChatFormatting.AQUA));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.freeze_shell.hazard").withStyle(ChatFormatting.RED));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.freeze_shell.stats", (Object[])new Object[]{tier.entityRadius(), tier.blockRadius(), Math.round(tier.damage()), tier.roman()}).withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.freeze_shell.speed", (Object[])new Object[]{tier.addedChargePower()}).withStyle(ChatFormatting.BLUE));
        if (tier != FreezeShellTier.I) {
            tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.freeze_shell.staged").withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.freeze_shell.fuze").withStyle(ChatFormatting.GRAY));
        if (Screen.hasShiftDown()) {
            tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.protocol.cdu_expanded").withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add((Component)Component.translatable((String)("tooltip.sporecannons." + tier.id() + ".record")).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.protocol.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}

