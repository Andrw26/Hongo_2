/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.neoforge.common.NeoForge
 *  rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers
 *  rbasamoyai.createbigcannons.munitions.config.MunitionPropertiesHandler
 *  rbasamoyai.createbigcannons.munitions.config.PropertiesTypeHandler
 */
package com.elvoux.sporecannons;

import com.elvoux.sporecannons.ClientConfig;
import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.SporeCannonsCapabilities;
import com.elvoux.sporecannons.field.ActiveContainmentFields;
import com.elvoux.sporecannons.field.WhiteoutEventLog;
import com.elvoux.sporecannons.field.WhiteoutTerminalService;
import com.elvoux.sporecannons.munition.CDUActivationManager;
import com.elvoux.sporecannons.munition.ContainmentZoneManager;
import com.elvoux.sporecannons.munition.CryogenicSaturationManager;
import com.elvoux.sporecannons.munition.FreezeCleanupManager;
import com.elvoux.sporecannons.munition.FreezeExplosionManager;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.munition.FreezeSnowManager;
import com.elvoux.sporecannons.network.SporeCannonsNetwork;
import com.elvoux.sporecannons.registry.SporeCannonsBlockEntities;
import com.elvoux.sporecannons.registry.SporeCannonsBlocks;
import com.elvoux.sporecannons.registry.SporeCannonsCreativeModeTabs;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import com.elvoux.sporecannons.registry.SporeCannonsEntityTypes;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import com.elvoux.sporecannons.registry.SporeCannonsMenus;
import com.elvoux.sporecannons.registry.SporeCannonsSounds;
import com.elvoux.sporecannons.registry.WhiteoutFacilityBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.config.MunitionPropertiesHandler;
import rbasamoyai.createbigcannons.munitions.config.PropertiesTypeHandler;

@Mod(value="sporecannons")
public final class SporeCannons {
    public static final String MOD_ID = "sporecannons";

    public SporeCannons(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, (IConfigSpec)FreezeConfig.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)ClientConfig.SPEC);
        SporeCannonsBlocks.REGISTER.register(modBus);
        WhiteoutFacilityBlocks.REGISTER.register(modBus);
        SporeCannonsItems.REGISTER.register(modBus);
        SporeCannonsEntityTypes.REGISTER.register(modBus);
        SporeCannonsBlockEntities.REGISTER.register(modBus);
        SporeCannonsSounds.REGISTER.register(modBus);
        SporeCannonsCriteria.REGISTER.register(modBus);
        SporeCannonsCreativeModeTabs.REGISTER.register(modBus);
        SporeCannonsMenus.REGISTER.register(modBus);
        NeoForge.EVENT_BUS.addListener(FreezeCleanupManager::onLevelTick);
        NeoForge.EVENT_BUS.addListener(FreezeExplosionManager::onLevelTick);
        NeoForge.EVENT_BUS.addListener(FreezeSnowManager::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContainmentZoneManager::onLevelTick);
        NeoForge.EVENT_BUS.addListener(CryogenicSaturationManager::onEntityTick);
        NeoForge.EVENT_BUS.addListener(CryogenicSaturationManager::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(CryogenicSaturationManager::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(CDUActivationManager::onLevelTick);
        NeoForge.EVENT_BUS.addListener(WhiteoutEventLog::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(WhiteoutTerminalService::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ActiveContainmentFields::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ActiveContainmentFields::onLivingHeal);
        modBus.addListener(this::commonSetup);
        modBus.addListener(SporeCannonsNetwork::register);
        modBus.addListener(SporeCannonsCapabilities::register);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (FreezeShellTier tier : FreezeShellTier.values()) {
                MunitionPropertiesHandler.registerProjectileHandler((EntityType)((EntityType)SporeCannonsEntityTypes.FREEZE_SHELLS.get((Object)tier).get()), (PropertiesTypeHandler)CBCMunitionPropertiesHandlers.COMMON_SHELL_BIG_CANNON_PROJECTILE);
            }
            MunitionPropertiesHandler.registerProjectileHandler((EntityType)((EntityType)SporeCannonsEntityTypes.CRYOGENIC_MACHINE_GUN_BULLET.get()), (PropertiesTypeHandler)CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE);
            MunitionPropertiesHandler.registerProjectileHandler((EntityType)((EntityType)SporeCannonsEntityTypes.CRYOGENIC_AP_AUTOCANNON.get()), (PropertiesTypeHandler)CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE);
            MunitionPropertiesHandler.registerProjectileHandler((EntityType)((EntityType)SporeCannonsEntityTypes.CRYOGENIC_FLAK_AUTOCANNON.get()), (PropertiesTypeHandler)CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        });
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)MOD_ID, (String)path);
    }
}

