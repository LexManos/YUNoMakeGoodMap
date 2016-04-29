package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPlatformGenerator
{
    // Generate your platform at the specified world and position.
    void generate(World world, BlockPos pos);
}
