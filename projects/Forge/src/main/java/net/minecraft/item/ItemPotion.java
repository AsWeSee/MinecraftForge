package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPotion extends Item
{
    public ItemPotion()
    {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.BREWING);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance()
    {
        return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), PotionTypes.WATER);
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;

        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode)
        {
            stack.shrink(1);
        }

        if (!worldIn.isRemote)
        {
            for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(stack))
            {
                if (potioneffect.getPotion().isInstant())
                {
                    potioneffect.getPotion().affectEntity(entityplayer, entityplayer, entityLiving, potioneffect.getAmplifier(), 1.0D);
                }
                else
                {
                    entityLiving.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }

        if (entityplayer != null)
        {
            entityplayer.addStat(StatList.getObjectUseStats(this));
        }

        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode)
        {
            if (stack.isEmpty())
            {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityplayer != null)
            {
                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
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

    public String getItemStackDisplayName(ItemStack stack)
    {
        return I18n.translateToLocal(PotionUtils.getPotionFromItem(stack).getNamePrefixed("potion.effect."));
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return super.hasEffect(stack) || !PotionUtils.getEffectsFromStack(stack).isEmpty();
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (PotionType potiontype : PotionType.REGISTRY)
        {
            if (potiontype != PotionTypes.EMPTY)
            {
                subItems.add(PotionUtils.addPotionToItemStack(new ItemStack(itemIn), potiontype));
            }
        }
    }
}