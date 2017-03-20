package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCoal extends Item
{
    public ItemCoal()
    {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.MATERIALS);
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return stack.getMetadata() == 1 ? "item.charcoal" : "item.coal";
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        subItems.add(new ItemStack(itemIn, 1, 0));
        subItems.add(new ItemStack(itemIn, 1, 1));
    }
}