/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.client.IItemDecorator
 *  rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.registry.SporeCannonsItems;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

final class CryogenicCartridgeDecorator
implements IItemDecorator {
    static final CryogenicCartridgeDecorator INSTANCE = new CryogenicCartridgeDecorator();
    private static final ResourceLocation AP_BADGE = ResourceLocation.fromNamespaceAndPath((String)"sporecannons", (String)"textures/gui/cryogenic_ap_cartridge_badge.png");
    private static final ResourceLocation FLAK_BADGE = ResourceLocation.fromNamespaceAndPath((String)"sporecannons", (String)"textures/gui/cryogenic_flak_cartridge_badge.png");

    private CryogenicCartridgeDecorator() {
    }

    public boolean render(GuiGraphics graphics, Font font, ItemStack stack, int x, int y) {
        ResourceLocation badge;
        ItemStack projectile = AutocannonCartridgeItem.getProjectileStack((ItemStack)stack);
        Object object = projectile.is((Item)SporeCannonsItems.CRYOGENIC_AP_AUTOCANNON_ROUND.get()) ? AP_BADGE : (badge = projectile.is((Item)SporeCannonsItems.CRYOGENIC_FLAK_AUTOCANNON_ROUND.get()) ? FLAK_BADGE : null);
        if (badge == null) {
            return false;
        }
        graphics.pose().pushPose();
        graphics.pose().translate(0.0f, 0.0f, 210.0f);
        graphics.blit(badge, x, y, 0.0f, 0.0f, 7, 7, 7, 7);
        graphics.pose().popPose();
        return false;
    }
}

