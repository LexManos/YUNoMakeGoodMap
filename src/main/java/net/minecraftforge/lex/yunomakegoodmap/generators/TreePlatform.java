package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreePlatform implements IPlatformGenerator
{

    @Override
    public void generate(World world, BlockPos pos)
    {
        if (world.provider.getDimension() == 0)
            buildTree(world, pos);
    }

    private void buildTree(World world, BlockPos pos)
    {
        world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        world.setBlockState(pos.up(6), Blocks.LEAVES.getDefaultState());
        for (int i = 1; i <= 5; i++)
            world.setBlockState(pos.up(i), Blocks.LOG.getDefaultState());
        for (int k = 3; k <= 5; k++)
        {
            int width = (k == 5 ? 1 : 2);
            for (int i = -width; i <= width; i++)
            {
                for (int j = -width; j <= width; j++)
                {
                    if (i != 0 || j != 0)
                        world.setBlockState(pos.add(i, k, j), Blocks.LEAVES.getDefaultState());
                }
            }
        }
    }
}
