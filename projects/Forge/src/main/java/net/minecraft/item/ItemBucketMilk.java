package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBucketMilk extends Item
{
    public ItemBucketMilk()
    {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.MISC);
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if (!worldIn.isRemote) entityLiving.curePotionEffects(stack); // FORGE - move up so stack.shrink does not turn stack into air
        if (entityLiving instanceof EntityPlayer && !((EntityPlayer)entityLiving).capabilities.isCreativeMode)
        {
            stack.shrink(1);
        }

        if (false) // FORGE - stack sensitive version at top of method
        if (!worldIn.isRemote)
        {
            entityLiving.clearActivePotions();
        }

        if (entityLiving instanceof EntityPlayer)
        {
            ((EntityPlayer)entityLiving).addStat(StatList.getObjectUseStats(this));
        }

        return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        playerIn.setActiveHand(handIn);
        return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, net.minecraft.nbt.NBTTagCompound nbt) {
        return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
    }
}