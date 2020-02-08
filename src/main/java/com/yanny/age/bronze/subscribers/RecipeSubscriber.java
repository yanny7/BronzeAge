package com.yanny.age.bronze.subscribers;

import com.yanny.age.bronze.recipes.BoostedFurnaceRecipe;
import com.yanny.age.bronze.recipes.BoostedFurnaceRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static com.yanny.age.bronze.Reference.MODID;

@SuppressWarnings("unused")
@ObjectHolder(MODID)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeSubscriber {
    public static final BoostedFurnaceRecipeSerializer boosted_furnace = null;

    @SubscribeEvent
    public static void registerTileEntity(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
        registry.register(new BoostedFurnaceRecipeSerializer(BoostedFurnaceRecipe::new).setRegistryName(MODID, "boosted_furnace"));
    }
}
