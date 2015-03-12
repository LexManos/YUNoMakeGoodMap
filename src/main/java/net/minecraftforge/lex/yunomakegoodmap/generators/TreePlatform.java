package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TreePlatform implements IPlatformGenerator
{

    @Override
    public void generate(World world, BlockPos pos)
    {
        if (world.provider.getDimensionId() == 0)
            buildTree(world, pos);
    }

    private void buildTree(World world, BlockPos pos)
    {
        world.setBlockState(pos, Blocks.dirt.getDefaultState());
        world.setBlockState(pos.up(6), Blocks.leaves.getDefaultState());
        for (int i = 1; i <= 5; i++)
            world.setBlockState(pos.up(i), Blocks.log.getDefaultState());
        for (int k = 3; k <= 5; k++)
        {
            int width = (k == 5 ? 1 : 2);
            for (int i = -width; i <= width; i++)
            {
                for (int j = -width; j <= width; j++)
                {
                    if (i != 0 || j != 0)
                        world.setBlockState(pos.add(i, k, j), Blocks.leaves.getDefaultState());
                }
            }
        }
    }
}
