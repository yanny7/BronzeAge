package com.yanny.age.bronze.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BoostedFurnaceRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BoostedFurnaceRecipe> {
    private final BoostedFurnaceRecipeSerializer.IFactory<BoostedFurnaceRecipe> factory;

    public BoostedFurnaceRecipeSerializer(BoostedFurnaceRecipeSerializer.IFactory<BoostedFurnaceRecipe> factory) {
        this.factory = factory;
    }

    @Override
    @Nonnull
    public BoostedFurnaceRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        String s = JSONUtils.getString(json, "group", "");
        JsonElement jsonelement = JSONUtils.isJsonArray(json, "ingredient")
                ? JSONUtils.getJsonArray(json, "ingredient")
                : JSONUtils.getJsonObject(json, "ingredient");
        Ingredient ingredient = Ingredient.deserialize(jsonelement);
        ItemStack result;
        ItemStack secondResult = ItemStack.EMPTY;
        int meltingPoint;

        if (!json.has("result")) {
            throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        }

        if (!json.has("meltingPoint") && json.get("meltingPoint").isJsonPrimitive() && json.getAsJsonPrimitive("meltingPoint").isNumber()) {
            throw new com.google.gson.JsonSyntaxException("Missing meltingPoint, expected to find an integer value");
        }

        if (json.get("result").isJsonObject()) {
            result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
        } else {
            String s1 = JSONUtils.getString(json, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            //noinspection deprecation
            result = new ItemStack(Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
        }

        if (json.has("secondResult")) {
            if (json.get("secondResult").isJsonObject()) {
                secondResult = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "secondResult"));
            } else {
                String s1 = JSONUtils.getString(json, "secondResult");
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                //noinspection deprecation
                secondResult = new ItemStack(Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
            }
        }

        meltingPoint = json.getAsJsonPrimitive("meltingPoint").getAsInt();

        return this.factory.create(recipeId, s, ingredient, result, secondResult, meltingPoint);
    }

    @Nullable
    @Override
    public BoostedFurnaceRecipe read(@Nonnull ResourceLocation recipeId, PacketBuffer buffer) {
        String s = buffer.readString(32767);
        Ingredient ingredient = Ingredient.read(buffer);
        ItemStack result = buffer.readItemStack();
        ItemStack secondResult = buffer.readItemStack();
        int meltingPoint = buffer.readInt();

        return this.factory.create(recipeId, s, ingredient, result, secondResult, meltingPoint);
    }

    @Override
    public void write(PacketBuffer buffer, BoostedFurnaceRecipe recipe) {
        buffer.writeString(recipe.group);
        recipe.ingredient.write(buffer);
        buffer.writeItemStack(recipe.result);
        buffer.writeItemStack(recipe.secondResult);
        buffer.writeInt(recipe.meltingPoint);
    }

    public interface IFactory<T extends BoostedFurnaceRecipe> {
        T create(ResourceLocation resourceLocation, String group, Ingredient ingredient, ItemStack result, ItemStack secondResult, int meltingPoint);
    }
}
