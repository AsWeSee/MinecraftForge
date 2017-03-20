package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAir extends Item
{
    private final Block block;

    public ItemAir(Block blockIn)
    {
        this.block = blockIn;
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return this.block.getUnlocalizedName();
    }

    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, playerIn, tooltip, advanced);
        this.block.addInformation(stack, playerIn, tooltip, advanced);
    }
}