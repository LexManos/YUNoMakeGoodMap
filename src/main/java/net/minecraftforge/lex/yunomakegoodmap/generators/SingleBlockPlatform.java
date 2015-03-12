package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class SingleBlockPlatform implements IPlatformGenerator
{
    private IBlockState state;

    public SingleBlockPlatform(IBlockState state)
    {
        this.state = state;
    }

    @Override
    public void generate(World world, BlockPos pos)
    {
        if (world.provider.getDimensionId() == 0)
            world.setBlockState(pos, state);
    }
}
