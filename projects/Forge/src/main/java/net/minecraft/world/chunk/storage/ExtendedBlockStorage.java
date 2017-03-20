package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage
{
    private final int yBase;
    private int blockRefCount;
    private int tickRefCount;
    private final BlockStateContainer data;
    private NibbleArray blocklightArray;
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(int y, boolean storeSkylight)
    {
        this.yBase = y;
        this.data = new BlockStateContainer();
        this.blocklightArray = new NibbleArray();

        if (storeSkylight)
        {
            this.skylightArray = new NibbleArray();
        }
    }

    public IBlockState get(int x, int y, int z)
    {
        return this.data.get(x, y, z);
    }

    public void set(int x, int y, int z, IBlockState state)
    {
        if (state instanceof net.minecraftforge.common.property.IExtendedBlockState)
            state = ((net.minecraftforge.common.property.IExtendedBlockState) state).getClean();
        IBlockState iblockstate = this.get(x, y, z);
        Block block = iblockstate.getBlock();
        Block block1 = state.getBlock();

        if (block != Blocks.AIR)
        {
            --this.blockRefCount;

            if (block.getTickRandomly())
            {
                --this.tickRefCount;
            }
        }

        if (block1 != Blocks.AIR)
        {
            ++this.blockRefCount;

            if (block1.getTickRandomly())
            {
                ++this.tickRefCount;
            }
        }

        this.data.set(x, y, z, state);
    }

    public boolean isEmpty()
    {
        return this.blockRefCount == 0;
    }

    public boolean getNeedsRandomTick()
    {
        return this.tickRefCount > 0;
    }

    public int getYLocation()
    {
        return this.yBase;
    }

    public void setExtSkylightValue(int x, int y, int z, int value)
    {
        this.skylightArray.set(x, y, z, value);
    }

    public int getExtSkylightValue(int x, int y, int z)
    {
        return this.skylightArray.get(x, y, z);
    }

    public void setExtBlocklightValue(int x, int y, int z, int value)
    {
        this.blocklightArray.set(x, y, z, value);
    }

    public int getExtBlocklightValue(int x, int y, int z)
    {
        return this.blocklightArray.get(x, y, z);
    }

    public void removeInvalidBlocks()
    {
        this.blockRefCount = 0;
        this.tickRefCount = 0;

        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                for (int k = 0; k < 16; ++k)
                {
                    Block block = this.get(i, j, k).getBlock();

                    if (block != Blocks.AIR)
                    {
                        ++this.blockRefCount;

                        if (block.getTickRandomly())
                        {
                            ++this.tickRefCount;
                        }
                    }
                }
            }
        }
    }

    public BlockStateContainer getData()
    {
        return this.data;
    }

    public NibbleArray getBlocklightArray()
    {
        return this.blocklightArray;
    }

    public NibbleArray getSkylightArray()
    {
        return this.skylightArray;
    }

    public void setBlocklightArray(NibbleArray newBlocklightArray)
    {
        this.blocklightArray = newBlocklightArray;
    }

    public void setSkylightArray(NibbleArray newSkylightArray)
    {
        this.skylightArray = newSkylightArray;
    }
}