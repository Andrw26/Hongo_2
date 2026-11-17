/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.foundation.PonderIndex
 *  net.minecraft.client.renderer.item.ItemProperties
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  net.neoforged.neoforge.client.IItemDecorator
 *  net.neoforged.neoforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent
 *  net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
 *  rbasamoyai.createbigcannons.index.CBCItems
 *  rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer
 *  rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonProjectileRenderer
 *  rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntityRenderer
 *  rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockVisual
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.client.CDURefuelingStationScreen;
import com.elvoux.sporecannons.client.CryogenicCartridgeDecorator;
import com.elvoux.sporecannons.client.CryogenicContainmentProjectorScreen;
import com.elvoux.sporecannons.client.FacilityScreen;
import com.elvoux.sporecannons.client.MultiblockStorageScreen;
import com.elvoux.sporecannons.client.SporeCannonsPonderPlugin;
import com.elvoux.sporecannons.client.WhiteoutAssemblyControllerScreen;
import com.elvoux.sporecannons.client.WhiteoutClientAlerts;
import com.elvoux.sporecannons.client.WhiteoutIndustryMachineScreen;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsEntityTypes;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import com.elvoux.sporecannons.registry.SporeCannonsMenus;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonProjectileRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntityRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockVisual;

@EventBusSubscriber(modid="sporecannons", bus=EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public final class SporeCannonsClient {
    private SporeCannonsClient() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin((PonderPlugin)new SporeCannonsPonderPlugin());
        event.enqueueWork(() -> {
            SimpleBlockEntityVisualizer.builder((BlockEntityType)((BlockEntityType)SporeCannonsBlockEntities.FREEZE_SHELL.get())).factory(FuzedBlockVisual::new).apply();
            ItemProperties.register((Item)((Item)SporeCannonsItems.CDU_FIELD_SCANNER.get()), (ResourceLocation)SporeCannons.id("unread"), (stack, level, entity, seed) -> WhiteoutClientAlerts.hasUnread() ? 1.0f : 0.0f);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            event.registerEntityRenderer((EntityType)SporeCannonsEntityTypes.FREEZE_SHELLS.get((Object)tier).get(), BigCannonProjectileRenderer::new);
        }
        event.registerBlockEntityRenderer((BlockEntityType)SporeCannonsBlockEntities.FREEZE_SHELL.get(), FuzedBlockEntityRenderer::new);
        event.registerEntityRenderer((EntityType)SporeCannonsEntityTypes.CRYOGENIC_MACHINE_GUN_BULLET.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer((EntityType)SporeCannonsEntityTypes.CRYOGENIC_AP_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer((EntityType)SporeCannonsEntityTypes.CRYOGENIC_FLAK_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register((MenuType)SporeCannonsMenus.CRYOGENIC_CONTAINMENT_PROJECTOR.get(), CryogenicContainmentProjectorScreen::new);
        event.register((MenuType)SporeCannonsMenus.CDU_REFUELING_STATION.get(), CDURefuelingStationScreen::new);
        event.register((MenuType)SporeCannonsMenus.FACILITY_FURNITURE.get(), FacilityScreen::new);
        event.register((MenuType)SporeCannonsMenus.MULTIBLOCK_STORAGE.get(), MultiblockStorageScreen::new);
        event.register((MenuType)SporeCannonsMenus.WHITEOUT_INDUSTRY_MACHINE.get(), WhiteoutIndustryMachineScreen::new);
        event.register((MenuType)SporeCannonsMenus.WHITEOUT_ASSEMBLY_CONTROLLER.get(), WhiteoutAssemblyControllerScreen::new);
    }

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        event.register((ItemLike)CBCItems.AUTOCANNON_CARTRIDGE.get(), (IItemDecorator)CryogenicCartridgeDecorator.INSTANCE);
    }
}

