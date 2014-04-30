package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class SingleBlockPlatform implements IPlatformGenerator
{
    private Block block;

    public SingleBlockPlatform(Block block)
    {
        this.block = block;
    }

    @Override
    public void generate(World world, int x, int y, int z)
    {
        if (world.provider.dimensionId == 0)
            world.setBlock(x, y, z, block);
    }
}
