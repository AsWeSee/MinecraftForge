package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory
{
    private final IMerchant theMerchant;
    private final NonNullList<ItemStack> theInventory = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
    private final EntityPlayer player;
    private MerchantRecipe currentRecipe;
    private int currentRecipeIndex;

    public InventoryMerchant(EntityPlayer thePlayerIn, IMerchant theMerchantIn)
    {
        this.player = thePlayerIn;
        this.theMerchant = theMerchantIn;
    }

    public int getSizeInventory()
    {
        return this.theInventory.size();
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.theInventory)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    public ItemStack getStackInSlot(int index)
    {
        return (ItemStack)this.theInventory.get(index);
    }

    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = (ItemStack)this.theInventory.get(index);

        if (index == 2 && !itemstack.isEmpty())
        {
            return ItemStackHelper.getAndSplit(this.theInventory, index, itemstack.getCount());
        }
        else
        {
            ItemStack itemstack1 = ItemStackHelper.getAndSplit(this.theInventory, index, count);

            if (!itemstack1.isEmpty() && this.inventoryResetNeededOnSlotChange(index))
            {
                this.resetRecipeAndSlots();
            }

            return itemstack1;
        }
    }

    private boolean inventoryResetNeededOnSlotChange(int slotIn)
    {
        return slotIn == 0 || slotIn == 1;
    }

    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.theInventory, index);
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.theInventory.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (this.inventoryResetNeededOnSlotChange(index))
        {
            this.resetRecipeAndSlots();
        }
    }

    public String getName()
    {
        return "mob.villager";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public ITextComponent getDisplayName()
    {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.theMerchant.getCustomer() == player;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    public void markDirty()
    {
        this.resetRecipeAndSlots();
    }

    public void resetRecipeAndSlots()
    {
        this.currentRecipe = null;
        ItemStack itemstack = (ItemStack)this.theInventory.get(0);
        ItemStack itemstack1 = (ItemStack)this.theInventory.get(1);

        if (itemstack.isEmpty())
        {
            itemstack = itemstack1;
            itemstack1 = ItemStack.EMPTY;
        }

        if (itemstack.isEmpty())
        {
            this.setInventorySlotContents(2, ItemStack.EMPTY);
        }
        else
        {
            MerchantRecipeList merchantrecipelist = this.theMerchant.getRecipes(this.player);

            if (merchantrecipelist != null)
            {
                MerchantRecipe merchantrecipe = merchantrecipelist.canRecipeBeUsed(itemstack, itemstack1, this.currentRecipeIndex);

                if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled())
                {
                    this.currentRecipe = merchantrecipe;
                    this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
                }
                else if (!itemstack1.isEmpty())
                {
                    merchantrecipe = merchantrecipelist.canRecipeBeUsed(itemstack1, itemstack, this.currentRecipeIndex);

                    if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled())
                    {
                        this.currentRecipe = merchantrecipe;
                        this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
                    }
                    else
                    {
                        this.setInventorySlotContents(2, ItemStack.EMPTY);
                    }
                }
                else
                {
                    this.setInventorySlotContents(2, ItemStack.EMPTY);
                }
            }

            this.theMerchant.verifySellingItem(this.getStackInSlot(2));
        }
    }

    public MerchantRecipe getCurrentRecipe()
    {
        return this.currentRecipe;
    }

    public void setCurrentRecipeIndex(int currentRecipeIndexIn)
    {
        this.currentRecipeIndex = currentRecipeIndexIn;
        this.resetRecipeAndSlots();
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        this.theInventory.clear();
    }
}