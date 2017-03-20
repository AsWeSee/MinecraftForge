package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface IRecipe
{
    boolean matches(InventoryCrafting inv, World worldIn);

    ItemStack getCraftingResult(InventoryCrafting inv);

    int getRecipeSize();

    ItemStack getRecipeOutput();

    NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv);
}