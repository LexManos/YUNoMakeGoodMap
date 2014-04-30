package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.init.Blocks;
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
        world.setBlock(x, y, z, Blocks.grass);
        world.setBlock(x, y + 6, z, Blocks.leaves);
        for (int i = 1; i <= 5; i++)
            world.setBlock(x, y + i, z, Blocks.log);
        for (int k = 3; k <= 5; k++)
        {
            int width = (k == 5 ? 1 : 2);
            for (int i = -width; i <= width; i++)
            {
                for (int j = -width; j <= width; j++)
                {
                    if (i != 0 || j != 0)
                        world.setBlock(x + i, y + k, z + j, Blocks.leaves);
                }
            }
        }
    }
}
