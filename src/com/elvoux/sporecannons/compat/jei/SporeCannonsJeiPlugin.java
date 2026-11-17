/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Harbinger.Spore.core.Sblocks
 *  com.Harbinger.Spore.core.Sitems
 *  mezz.jei.api.IModPlugin
 *  mezz.jei.api.JeiPlugin
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.builder.IRecipeSlotBuilder
 *  mezz.jei.api.gui.drawable.IDrawable
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.helpers.IGuiHelper
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  mezz.jei.api.recipe.RecipeType
 *  mezz.jei.api.recipe.category.IRecipeCategory
 *  mezz.jei.api.registration.IRecipeCatalystRegistration
 *  mezz.jei.api.registration.IRecipeCategoryRegistration
 *  mezz.jei.api.registration.IRecipeRegistration
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 */
package com.elvoux.sporecannons.compat.jei;

import com.Harbinger.Spore.core.Sblocks;
import com.Harbinger.Spore.core.Sitems;
import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.industry.WhiteoutIndustryMachineBlockEntity;
import com.elvoux.sporecannons.registry.SporeCannonsBlocks;
import com.elvoux.sporecannons.registry.SporeCannonsItems;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

@JeiPlugin
public final class SporeCannonsJeiPlugin
implements IModPlugin {
    private static final RecipeType<WhiteoutProcess> CULTIVATION = RecipeType.create((String)"sporecannons", (String)"biomass_cultivation", WhiteoutProcess.class);
    private static final RecipeType<WhiteoutProcess> CRYOGENIC_PROCESSING = RecipeType.create((String)"sporecannons", (String)"cryogenic_processing", WhiteoutProcess.class);
    private static final RecipeType<WhiteoutProcess> CARTRIDGE_REFUELLING = RecipeType.create((String)"sporecannons", (String)"cartridge_refuelling", WhiteoutProcess.class);

    public ResourceLocation getPluginUid() {
        return SporeCannons.id("jei");
    }

    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new IRecipeCategory[]{new ProcessCategory(CULTIVATION, "biomass_cultivation", (ItemLike)SporeCannonsBlocks.BIOMASS_CULTIVATOR.get(), gui), new ProcessCategory(CRYOGENIC_PROCESSING, "cryogenic_processing", (ItemLike)SporeCannonsBlocks.CRYOGENIC_BIOMASS_PROCESSOR.get(), gui), new ProcessCategory(CARTRIDGE_REFUELLING, "cartridge_refuelling", (ItemLike)SporeCannonsBlocks.CDU_REFUELING_STATION.get(), gui)});
    }

    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CULTIVATION, List.of((Object)((Object)new WhiteoutProcess(SporeCannons.id("biomass_cultivation"), Ingredient.of(WhiteoutIndustryMachineBlockEntity.CULTIVABLE_BIOMASS), true, true, Ingredient.of(WhiteoutIndustryMachineBlockEntity.CULTIVATOR_NUTRIENTS), (Integer)FreezeConfig.CULTIVATOR_WATER_PER_CYCLE.get(), Ingredient.of(WhiteoutIndustryMachineBlockEntity.CULTIVABLE_BIOMASS), 1, 0, "cultivation"))));
        registration.addRecipes(CRYOGENIC_PROCESSING, List.of((Object)((Object)new WhiteoutProcess(SporeCannons.id("freezerburned_biomass"), Ingredient.of(WhiteoutIndustryMachineBlockEntity.CRYOGENIC_PROCESSABLE_BIOMASS), true, false, Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()}), 0, Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)Sblocks.FROST_BURNED_BIOMASS.get()}), 1, (Integer)FreezeConfig.PROCESSOR_BIOMASS_COOLANT.get(), "biomass")), (Object)((Object)new WhiteoutProcess(SporeCannons.id("deep_freezing_blue_ice"), Ingredient.EMPTY, false, false, Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()}), (Integer)FreezeConfig.PROCESSOR_BLUE_ICE_WATER.get(), Ingredient.of((ItemLike[])new ItemLike[]{Items.BLUE_ICE}), 1, (Integer)FreezeConfig.PROCESSOR_BLUE_ICE_COOLANT.get(), "deep_freezing")), (Object)((Object)new WhiteoutProcess(SporeCannons.id("snow_production"), Ingredient.of((ItemLike[])new ItemLike[]{Items.SNOWBALL}), true, true, Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()}), (Integer)FreezeConfig.PROCESSOR_SNOW_WATER.get(), Ingredient.of((ItemLike[])new ItemLike[]{Items.SNOWBALL}), (Integer)FreezeConfig.PROCESSOR_SNOW_OUTPUT.get(), (Integer)FreezeConfig.PROCESSOR_SNOW_COOLANT.get(), "snow"))));
        registration.addRecipes(CARTRIDGE_REFUELLING, List.of((Object)((Object)new WhiteoutProcess(SporeCannons.id("charged_cdu_coolant_cartridge"), Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.get()}), true, false, Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)SporeCannonsItems.STABILIZED_CRYOGEL.get()}), 1000, Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.get()}), 1, 0, "cartridge"))));
        registration.addItemStackInfo(List.of((Object)SporeCannonsItems.DEPLETED_CDU_COOLANT_CARTRIDGE.toStack(), (Object)SporeCannonsItems.CHARGED_CDU_COOLANT_CARTRIDGE.toStack()), new Component[]{Component.translatable((String)"jei.sporecannons.info.coolant_cartridge")});
        registration.addItemStackInfo(Sitems.ICE_CANISTER.toStack(), new Component[]{Component.translatable((String)"jei.sporecannons.info.ice_canister")});
        registration.addItemStackInfo(SporeCannonsItems.STABILIZED_CRYOGEL.toStack(), new Component[]{Component.translatable((String)"jei.sporecannons.info.stabilized_cryogel")});
    }

    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(CULTIVATION, new ItemLike[]{(ItemLike)SporeCannonsBlocks.BIOMASS_CULTIVATOR.get()});
        registration.addRecipeCatalysts(CRYOGENIC_PROCESSING, new ItemLike[]{(ItemLike)SporeCannonsBlocks.CRYOGENIC_BIOMASS_PROCESSOR.get()});
        registration.addRecipeCatalysts(CARTRIDGE_REFUELLING, new ItemLike[]{(ItemLike)SporeCannonsBlocks.CDU_REFUELING_STATION.get(), (ItemLike)SporeCannonsBlocks.REFUELLING_SERVICE_CONTROLLER.get()});
    }

    private static final class ProcessCategory
    implements IRecipeCategory<WhiteoutProcess> {
        private final RecipeType<WhiteoutProcess> type;
        private final Component title;
        private final IDrawable icon;
        private final IDrawable arrow;

        private ProcessCategory(RecipeType<WhiteoutProcess> type, String titleKey, ItemLike iconItem, IGuiHelper gui) {
            this.type = type;
            this.title = Component.translatable((String)("jei.sporecannons.category." + titleKey));
            this.icon = gui.createDrawableItemLike(iconItem);
            this.arrow = gui.getRecipeArrow();
        }

        public RecipeType<WhiteoutProcess> getRecipeType() {
            return this.type;
        }

        public Component getTitle() {
            return this.title;
        }

        public IDrawable getIcon() {
            return this.icon;
        }

        public int getWidth() {
            return 146;
        }

        public int getHeight() {
            return 62;
        }

        public void setRecipe(IRecipeLayoutBuilder builder, WhiteoutProcess recipe, IFocusGroup focuses) {
            if (recipe.hasPrimary()) {
                builder.addSlot(recipe.primaryCatalyst() ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT, 5, 20).setStandardSlotBackground().addIngredients(recipe.primary());
            }
            builder.addInputSlot(recipe.hasPrimary() ? 35 : 20, 20).setStandardSlotBackground().addIngredients(recipe.secondary());
            if (recipe.water() > 0) {
                builder.addInputSlot(recipe.hasPrimary() ? 65 : 50, 20).setStandardSlotBackground().setFluidRenderer(4000L, true, 16, 16).addFluidStack((Fluid)Fluids.WATER, (long)recipe.water());
            }
            IRecipeSlotBuilder output = builder.addOutputSlot(122, 20).setOutputSlotBackground();
            if (recipe.outputCount() > 1) {
                ItemStack stack = recipe.output().getItems()[0].copy();
                stack.setCount(recipe.outputCount());
                output.addItemStack(stack);
            } else {
                output.addIngredients(recipe.output());
            }
        }

        public void draw(WhiteoutProcess recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
            this.arrow.draw(graphics, 91, 20);
            int fullCycles = recipe.charge() <= 0 ? 0 : 54000 / recipe.charge();
            MutableComponent note = Component.translatable((String)("jei.sporecannons.process." + recipe.note()), (Object[])new Object[]{recipe.charge(), fullCycles, recipe.water(), recipe.outputCount()});
            String fitted = Minecraft.getInstance().font.plainSubstrByWidth(note.getString(), 140);
            graphics.drawString(Minecraft.getInstance().font, fitted, 3, 49, -7610904, false);
        }

        public ResourceLocation getRegistryName(WhiteoutProcess recipe) {
            return recipe.id();
        }
    }

    private record WhiteoutProcess(ResourceLocation id, Ingredient primary, boolean hasPrimary, boolean primaryCatalyst, Ingredient secondary, int water, Ingredient output, int outputCount, int charge, String note) {
    }
}

