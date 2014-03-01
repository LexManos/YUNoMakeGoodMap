package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class TreePlatform implements IPlatformGenerator
{

    @Override
    public void generate(World world, int x, int y, int z)
    {
        if (world.provider.dimensionId == 0)
            buildTree(world, x, y, z);
    }

    private void buildTree(World world, int x, int y, int z)
    {
        world.setBlock(x, y, z, Block.grass.blockID);
        world.setBlock(x, y + 6, z, Block.leaves.blockID);
        for (int i = 1; i <= 5; i++)
            world.setBlock(x, y + i, z, Block.wood.blockID);
        for (int k = 3; k <= 5; k++)
        {
            int width = (k == 5 ? 1 : 2);
            for (int i = -width; i <= width; i++)
            {
                for (int j = -width; j <= width; j++)
                {
                    if (i != 0 || j != 0)
                        world.setBlock(x + i, y + k, z + j, Block.leaves.blockID);
                }
            }
        }
    }
}
