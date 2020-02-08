package com.yanny.age.bronze.recipes;

import com.yanny.age.bronze.subscribers.BlockSubscriber;
import com.yanny.age.bronze.subscribers.RecipeSubscriber;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BoostedFurnaceRecipe implements IRecipe<IInventory> {
    public static final IRecipeType<BoostedFurnaceRecipe> boosted_furnace = IRecipeType.register("boosted_furnace");

    private final IRecipeType<?> type;
    private final ResourceLocation id;
    final String group;
    final Ingredient ingredient;
    final ItemStack result;
    final ItemStack secondResult;
    final int meltingPoint;

    public BoostedFurnaceRecipe(ResourceLocation resourceLocation, String group, Ingredient ingredient, ItemStack result, ItemStack secondResult, int meltingPoint) {
        type = boosted_furnace;
        id = resourceLocation;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.secondResult = secondResult;
        this.meltingPoint = meltingPoint;
    }

    @Override
    public boolean matches(IInventory inv, @Nonnull World worldIn) {
        return this.ingredient.test(inv.getStackInSlot(0));
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nullable IInventory inv) {
        return this.result.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput() {
        return result;
    }

    public ItemStack getSecondResult() {
        return secondResult;
    }

    public int getMeltingPoint() {
        return meltingPoint;
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        //noinspection ConstantConditions
        return RecipeSubscriber.boosted_furnace;
    }

    @Override
    @Nonnull
    public IRecipeType<?> getType() {
        return type;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    @Nonnull
    public ItemStack getIcon() {
        return new ItemStack(BlockSubscriber.boosted_furnace);
    }
}
