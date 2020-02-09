package com.yanny.age.bronze.subscribers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yanny.age.bronze.config.Config;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static com.yanny.age.bronze.Reference.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {

    private static final Set<ResourceLocation> RECIPES_TO_REMOVE = Sets.newHashSet(
            new ResourceLocation("minecraft", "iron_ingot"),     // removed smelting iron
            new ResourceLocation("minecraft", "iron_ingot_from_blasting")     // removed smelting iron
    );
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Set<ResourceLocation> RECIPES_TO_ADD = Sets.newHashSet(
    );
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Set<ResourceLocation> ADVANCEMENTS_TO_REMOVE = Sets.newHashSet(
    );

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void FMLServerStartingEvent(FMLServerStartingEvent event) {
        if (Config.removeVanillaRecipes) {
            RecipeManager recipeManager = event.getServer().getRecipeManager();
            Class<?> recipeManagerClass = recipeManager.getClass();

            try {
                Field recipes = recipeManagerClass.getDeclaredFields()[2];
                recipes.setAccessible(true);
                Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipesMap = (Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>>) recipes.get(recipeManager);
                Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map = Maps.newHashMap();
                recipesMap.forEach((iRecipeType, resourceLocationIRecipeMap) -> {
                    Map<ResourceLocation, IRecipe<?>> map1 = map.computeIfAbsent(iRecipeType, (recipeType) -> Maps.newHashMap());
                    resourceLocationIRecipeMap.forEach(map1::put);
                    RECIPES_TO_REMOVE.forEach(map1::remove);
                    RECIPES_TO_ADD.forEach(resourceLocation -> {
                        IRecipe<?> recipe = map1.remove(resourceLocation);

                        if (recipe != null) {
                            map1.put(new ResourceLocation("minecraft", resourceLocation.getPath()), recipe);
                        }
                    });
                });
                recipes.set(recipeManager, ImmutableMap.copyOf(map));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            AdvancementManager advancementManager = event.getServer().getAdvancementManager();
            Class<?> advancementManagerClass = advancementManager.getClass();
            Field advancements = advancementManagerClass.getDeclaredFields()[2];
            advancements.setAccessible(true);

            try {
                AdvancementList advancementList = (AdvancementList) advancements.get(advancementManager);
                Class<?> list = advancementList.getClass();
                Field listField = list.getDeclaredFields()[1];
                listField.setAccessible(true);
                Map<ResourceLocation, Advancement> map = (Map<ResourceLocation, Advancement>) listField.get(advancementList);
                ADVANCEMENTS_TO_REMOVE.forEach(map::remove);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /*@SubscribeEvent
    public static void rightClickInfo(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote) {
            return;
        }

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Chunk chunk = event.getWorld().getChunkAt(event.getPos());
        Map<Block, MutableInt> stats = new HashMap<>();

        for (int y = 0; y < 255; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    pos.setPos(x, y, z);
                    Block block = chunk.getBlockState(pos).getBlock();

                    if (stats.get(block) == null) {
                        stats.put(block, new MutableInt(0));
                    }

                    stats.get(block).increment();
                }
            }
        }

        System.out.println("-----------------------------------------------------------------");
        //stats.forEach((block, mutableInt) -> System.out.println(block + ": " + mutableInt));
        stats.entrySet().stream().filter(value -> Objects.requireNonNull(value.getKey().getRegistryName()).getNamespace().equals(MODID)).sorted(Map.Entry.comparingByValue())
                .forEach(map -> System.out.println(map.getKey() + ": " + map.getValue()));
        System.out.println("-----------------------------------------------------------------");
    }*/
}
