/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
 */
package com.elvoux.sporecannons.client;

import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid="sporecannons", value={Dist.CLIENT})
public final class WhiteoutFacilityTooltips {
    private static final Map<String, String> CATEGORIES = Map.ofEntries((Map.Entry[])new Map.Entry[]{Map.entry((Object)"laboratory_wall", (Object)"structural"), Map.entry((Object)"reinforced_wall_panel", (Object)"structural"), Map.entry((Object)"structural_panel", (Object)"structural"), Map.entry((Object)"ceiling_panel", (Object)"structural"), Map.entry((Object)"laboratory_floor", (Object)"structural"), Map.entry((Object)"technical_wall_panel", (Object)"technical_shell"), Map.entry((Object)"ventilation_block", (Object)"technical_shell"), Map.entry((Object)"grated_floor", (Object)"technical_shell"), Map.entry((Object)"hazard_marked_floor", (Object)"technical_shell"), Map.entry((Object)"laboratory_glass", (Object)"glass"), Map.entry((Object)"reinforced_laboratory_glass", (Object)"glass"), Map.entry((Object)"frosted_laboratory_glass", (Object)"glass"), Map.entry((Object)"laboratory_glass_frame", (Object)"glass"), Map.entry((Object)"glass_partition", (Object)"glass"), Map.entry((Object)"sample_containment_chamber", (Object)"sample_storage"), Map.entry((Object)"cryogenic_reservoir", (Object)"cryogel_storage"), Map.entry((Object)"utility_pipe", (Object)"utility"), Map.entry((Object)"communication_cable_block", (Object)"communication"), Map.entry((Object)"laboratory_workbench", (Object)"workbench"), Map.entry((Object)"storage_cabinet", (Object)"storage"), Map.entry((Object)"technical_equipment_cabinet", (Object)"technical_storage"), Map.entry((Object)"laboratory_terminal", (Object)"terminal"), Map.entry((Object)"laboratory_door", (Object)"door"), Map.entry((Object)"whiteout_warning_panel", (Object)"warning"), Map.entry((Object)"industrial_laboratory_light", (Object)"light"), Map.entry((Object)"whiteout_facility_sign", (Object)"sign"), Map.entry((Object)"biomass_cultivator", (Object)"cultivator"), Map.entry((Object)"cryogenic_biomass_processor", (Object)"cryogenic_processor"), Map.entry((Object)"projector_service_controller", (Object)"projector_controller"), Map.entry((Object)"refuelling_service_controller", (Object)"refuelling_controller")});

    private WhiteoutFacilityTooltips() {
    }

    @SubscribeEvent
    public static void appendDescription(ItemTooltipEvent event) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey((Object)event.getItemStack().getItem());
        if (!id.getNamespace().equals("sporecannons")) {
            return;
        }
        String category = CATEGORIES.get(id.getPath());
        if (category == null && id.getPath().startsWith("memory_card_")) {
            category = "memory_card";
        }
        if (category == null) {
            return;
        }
        event.getToolTip().add(Component.translatable((String)("tooltip.sporecannons.facility." + category)).withStyle(ChatFormatting.AQUA));
    }
}

