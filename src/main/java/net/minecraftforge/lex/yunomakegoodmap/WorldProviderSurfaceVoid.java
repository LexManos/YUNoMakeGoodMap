package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderSurfaceVoid extends WorldProviderSurface
{
    @Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(world))
            return true;
        return super.canCoordinateBeSpawn(x, z);
    }

    @Override
    public BlockPos getRandomizedSpawnPoint()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(world))
        {
            BlockPos spawn = new BlockPos(world.getSpawnPoint());
            if (!YUNoMakeGoodMap.instance.isExactSpawn())
                spawn = world.getTopSolidOrLiquidBlock(spawn);
            return spawn;
        }
        else
        {
            return super.getRandomizedSpawnPoint();
        }
    }

    @Override
    public IChunkGenerator createChunkGenerator()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(world))
            return new ChunkGeneratorFlatVoid(world);
        return super.createChunkGenerator();
    }
}
