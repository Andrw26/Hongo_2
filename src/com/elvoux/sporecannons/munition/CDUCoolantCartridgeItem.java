/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.CDUComponentItem;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

public final class CDUCoolantCartridgeItem
extends CDUComponentItem {
    public static final int MAX_COOLANT_UNITS = 54000;
    private static final String COOLANT_UNITS = "SporeCannonsCoolantUnits";

    public CDUCoolantCartridgeItem(Item.Properties properties) {
        super(properties, "charged_cdu_coolant_cartridge");
    }

    public static int remaining(ItemStack stack) {
        CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        return tag.contains(COOLANT_UNITS) ? Mth.clamp((int)tag.getInt(COOLANT_UNITS), (int)0, (int)54000) : 54000;
    }

    public static boolean hasStoredCharge(ItemStack stack) {
        return ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY)).copyTag().contains(COOLANT_UNITS);
    }

    public static void setRemaining(ItemStack stack, int units) {
        int clamped = Mth.clamp((int)units, (int)0, (int)54000);
        CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, tag -> tag.putInt(COOLANT_UNITS, clamped));
    }

    public boolean isBarVisible(ItemStack stack) {
        return CDUCoolantCartridgeItem.remaining(stack) < 54000;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0f * (float)CDUCoolantCartridgeItem.remaining(stack) / 54000.0f);
    }

    public int getBarColor(ItemStack stack) {
        return 3528941;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int percent = Math.round(100.0f * (float)CDUCoolantCartridgeItem.remaining(stack) / 54000.0f);
        int secondsAt32Rpm = CDUCoolantCartridgeItem.remaining(stack) / 15;
        String runtime = String.format(Locale.ROOT, "%d:%02d", secondsAt32Rpm / 60, secondsAt32Rpm % 60);
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.charged_cdu_coolant_cartridge.charge", (Object[])new Object[]{percent, runtime}).withStyle(ChatFormatting.GRAY));
    }
}

