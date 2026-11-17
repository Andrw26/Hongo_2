/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.EnchantedBookItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.EnchantmentInstance
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  rbasamoyai.createbigcannons.ModGroup
 *  rbasamoyai.createbigcannons.index.CBCItems
 *  rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsEnchantments;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import com.elvoux.sporecannons.registry.WhiteoutFacilityBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.ModGroup;
import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

public final class SporeCannonsCreativeModeTabs {
    private static final ResourceLocation CBC_TAB = ModGroup.MAIN_TAB_KEY.location();
    private static final ResourceLocation ARSENAL_BACKGROUND = ResourceLocation.fromNamespaceAndPath((String)"sporecannons", (String)"textures/gui/container/creative_inventory/cryogenic_arsenal.png");
    private static final ResourceLocation ARSENAL_SCROLLER = ResourceLocation.fromNamespaceAndPath((String)"sporecannons", (String)"container/creative_inventory/cryogenic_scroller");
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (String)"sporecannons");
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CRYOGENIC_ARSENAL = REGISTER.register("cryogenic_arsenal", () -> CreativeModeTab.builder().title((Component)Component.translatable((String)"itemGroup.sporecannons.cryogenic_arsenal")).icon(() -> new ItemStack((ItemLike)SporeCannonsItems.FREEZE_SHELLS.get((Object)FreezeShellTier.VI).get())).backgroundTexture(ARSENAL_BACKGROUND).withScrollBarSpriteLocation(ARSENAL_SCROLLER).withLabelColor(0xDDF8FF).withSlotColor(1882577133).withTabsBefore(new ResourceLocation[]{CBC_TAB}).displayItems((parameters, output) -> {
        output.accept((ItemLike)SporeCannonsItems.STABILIZED_CRYOGEL.get());
        output.accept((ItemLike)SporeCannonsItems.CRYOGENIC_MICROCELL.get());
        output.accept((ItemLike)SporeCannonsItems.THERMAL_LINING.get());
        output.accept((ItemLike)SporeCannonsItems.PRESSURE_REGULATOR.get());
        output.accept((ItemLike)SporeCannonsItems.COMMUNICATION_MODULE.get());
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            output.accept((ItemLike)SporeCannonsItems.CDU_CORES.get((Object)tier).get());
        }
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            output.accept((ItemLike)SporeCannonsItems.FREEZE_SHELLS.get((Object)tier).get());
        }
        output.accept((ItemLike)SporeCannonsItems.CDU_FIELD_SCANNER.get());
        output.accept((ItemLike)SporeCannonsItems.WHITEOUT_FIELD_MANUAL.get());
        output.accept((ItemLike)SporeCannonsItems.CRYOGEL_RESEARCH_LOG.get());
        SporeCannonsItems.REGIONAL_STATION_DOCUMENTS.values().forEach(document -> output.accept((ItemLike)document.get()));
        output.accept((ItemLike)SporeCannonsItems.MEMORY_CARD_LOGISTICS.get());
        output.accept((ItemLike)SporeCannonsItems.MEMORY_CARD_CONTAINMENT.get());
        output.accept((ItemLike)SporeCannonsItems.MEMORY_CARD_PERSONNEL.get());
        output.accept((ItemLike)SporeCannonsItems.MEMORY_CARD_INCIDENT.get());
        output.accept((ItemLike)SporeCannonsItems.MEMORY_CARD_DIRECTIVE.get());
        output.accept((ItemLike)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get());
        output.accept((ItemLike)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get());
        output.accept((ItemLike)SporeCannonsItems.CDU_REFUELING_STATION.get());
        output.accept((ItemLike)SporeCannonsItems.CRYOGENIC_CONTAINMENT_PROJECTOR.get());
        output.accept((ItemLike)SporeCannonsItems.BIOMASS_CULTIVATOR.get());
        output.accept((ItemLike)SporeCannonsItems.CRYOGENIC_BIOMASS_PROCESSOR.get());
        output.accept((ItemLike)SporeCannonsItems.PROJECTOR_SERVICE_CONTROLLER.get());
        output.accept((ItemLike)SporeCannonsItems.REFUELLING_SERVICE_CONTROLLER.get());
        output.accept((ItemLike)SporeCannonsItems.CRYOGENIC_SENSOR_FUZE.get());
        output.accept((ItemLike)SporeCannonsItems.CRYOGENIC_MINE_FUZE.get());
        output.accept((ItemLike)SporeCannonsItems.CRYOGENIC_MACHINE_GUN_ROUND.get());
        output.accept((ItemLike)SporeCannonsItems.CRYOGENIC_AP_AUTOCANNON_ROUND.get());
        output.accept(SporeCannonsCreativeModeTabs.loadedAPCartridge());
        output.accept((ItemLike)SporeCannonsItems.CRYOGENIC_FLAK_AUTOCANNON_ROUND.get());
        output.accept(SporeCannonsCreativeModeTabs.loadedFlakCartridge());
        WhiteoutFacilityBlocks.ALL.values().forEach(block -> output.accept((ItemLike)block.get()));
        Holder.Reference insulation = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SporeCannonsEnchantments.CRYOGENIC_INSULATION);
        output.accept(EnchantedBookItem.createForEnchantment((EnchantmentInstance)new EnchantmentInstance((Holder)insulation, 4)));
    }).build());

    public static ItemStack loadedAPCartridge() {
        return SporeCannonsCreativeModeTabs.loadedCartridge(new ItemStack((ItemLike)SporeCannonsItems.CRYOGENIC_AP_AUTOCANNON_ROUND.get()));
    }

    public static ItemStack loadedFlakCartridge() {
        return SporeCannonsCreativeModeTabs.loadedCartridge(new ItemStack((ItemLike)SporeCannonsItems.CRYOGENIC_FLAK_AUTOCANNON_ROUND.get()));
    }

    private static ItemStack loadedCartridge(ItemStack projectile) {
        ItemStack cartridge = CBCItems.AUTOCANNON_CARTRIDGE.asStack();
        AutocannonCartridgeItem.writeProjectile((ItemStack)projectile, (ItemStack)cartridge);
        return cartridge;
    }

    private SporeCannonsCreativeModeTabs() {
    }
}

