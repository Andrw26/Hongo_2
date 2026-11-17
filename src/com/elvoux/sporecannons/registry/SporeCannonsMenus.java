/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.inventory.MenuType
 *  net.neoforged.neoforge.common.extensions.IMenuTypeExtension
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.elvoux.sporecannons.registry;

import com.elvoux.sporecannons.facility.FacilityMenu;
import com.elvoux.sporecannons.facility.MultiblockStorageMenu;
import com.elvoux.sporecannons.industry.WhiteoutAssemblyControllerMenu;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineMenu;
import com.elvoux.sporecannons.munition.CDURefuelingStationMenu;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SporeCannonsMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.MENU, (String)"sporecannons");
    public static final DeferredHolder<MenuType<?>, MenuType<CryogenicContainmentProjectorMenu>> CRYOGENIC_CONTAINMENT_PROJECTOR = REGISTER.register("cryogenic_containment_projector", () -> IMenuTypeExtension.create(CryogenicContainmentProjectorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<CDURefuelingStationMenu>> CDU_REFUELING_STATION = REGISTER.register("cdu_refueling_station", () -> IMenuTypeExtension.create(CDURefuelingStationMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<FacilityMenu>> FACILITY_FURNITURE = REGISTER.register("facility_furniture", () -> IMenuTypeExtension.create(FacilityMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<MultiblockStorageMenu>> MULTIBLOCK_STORAGE = REGISTER.register("multiblock_storage", () -> IMenuTypeExtension.create(MultiblockStorageMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<WhiteoutIndustryMachineMenu>> WHITEOUT_INDUSTRY_MACHINE = REGISTER.register("whiteout_industry_machine", () -> IMenuTypeExtension.create(WhiteoutIndustryMachineMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<WhiteoutAssemblyControllerMenu>> WHITEOUT_ASSEMBLY_CONTROLLER = REGISTER.register("whiteout_assembly_controller", () -> IMenuTypeExtension.create(WhiteoutAssemblyControllerMenu::new));

    private SporeCannonsMenus() {
    }
}

