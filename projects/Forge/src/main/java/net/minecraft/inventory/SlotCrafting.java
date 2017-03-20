package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.NonNullList;

public class SlotCrafting extends Slot
{
    private final InventoryCrafting craftMatrix;
    private final EntityPlayer player;
    private int amountCrafted;

    public SlotCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
    {
        super(inventoryIn, slotIndex, xPosition, yPosition);
        this.player = player;
        this.craftMatrix = craftingInventory;
    }

    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            this.amountCrafted += Math.min(amount, this.getStack().getCount());
        }

        return super.decrStackSize(amount);
    }

    protected void onCrafting(ItemStack stack, int amount)
    {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }

    protected void onSwapCraft(int p_190900_1_)
    {
        this.amountCrafted += p_190900_1_;
    }

    protected void onCrafting(ItemStack stack)
    {
        if (this.amountCrafted > 0)
        {
            stack.onCrafting(this.player.world, this.player, this.amountCrafted);
            net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(this.player, stack, craftMatrix);
        }

        this.amountCrafted = 0;

        if (stack.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE))
        {
            this.player.addStat(AchievementList.BUILD_WORK_BENCH);
        }

        if (stack.getItem() instanceof ItemPickaxe)
        {
            this.player.addStat(AchievementList.BUILD_PICKAXE);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.FURNACE))
        {
            this.player.addStat(AchievementList.BUILD_FURNACE);
        }

        if (stack.getItem() instanceof ItemHoe)
        {
            this.player.addStat(AchievementList.BUILD_HOE);
        }

        if (stack.getItem() == Items.BREAD)
        {
            this.player.addStat(AchievementList.MAKE_BREAD);
        }

        if (stack.getItem() == Items.CAKE)
        {
            this.player.addStat(AchievementList.BAKE_CAKE);
        }

        if (stack.getItem() instanceof ItemPickaxe && ((ItemPickaxe)stack.getItem()).getToolMaterial() != Item.ToolMaterial.WOOD)
        {
            this.player.addStat(AchievementList.BUILD_BETTER_PICKAXE);
        }

        if (stack.getItem() instanceof ItemSword)
        {
            this.player.addStat(AchievementList.BUILD_SWORD);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.ENCHANTING_TABLE))
        {
            this.player.addStat(AchievementList.ENCHANTMENTS);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.BOOKSHELF))
        {
            this.player.addStat(AchievementList.BOOKCASE);
        }
    }

    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
    {
        this.onCrafting(stack);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
        NonNullList<ItemStack> nonnulllist = CraftingManager.getInstance().getRemainingItems(this.craftMatrix, thePlayer.world);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
            ItemStack itemstack1 = (ItemStack)nonnulllist.get(i);

            if (!itemstack.isEmpty())
            {
                this.craftMatrix.decrStackSize(i, 1);
                itemstack = this.craftMatrix.getStackInSlot(i);
            }

            if (!itemstack1.isEmpty())
            {
                if (itemstack.isEmpty())
                {
                    this.craftMatrix.setInventorySlotContents(i, itemstack1);
                }
                else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
                {
                    itemstack1.grow(itemstack.getCount());
                    this.craftMatrix.setInventorySlotContents(i, itemstack1);
                }
                else if (!this.player.inventory.addItemStackToInventory(itemstack1))
                {
                    this.player.dropItem(itemstack1, false);
                }
            }
        }

        return stack;
    }
}