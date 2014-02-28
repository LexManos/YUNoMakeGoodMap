package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderSurfaceVoid extends WorldProviderSurface
{
    @Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(worldObj))
            return true;
        return super.canCoordinateBeSpawn(x, z);
    }

    @Override
    public ChunkCoordinates getRandomizedSpawnPoint()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(worldObj))
        {
            ChunkCoordinates spawn = new ChunkCoordinates(worldObj.getSpawnPoint());
            spawn.posY = worldObj.getTopSolidOrLiquidBlock(spawn.posX, spawn.posZ);
            return spawn;
        }
        else
        {
            return super.getRandomizedSpawnPoint();
        }
    }

    @Override
    protected void registerWorldChunkManager()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(worldObj))
            worldChunkMgr = new VoidWorldChunkManager(worldObj);
        else
            worldChunkMgr = terrainType.getChunkManager(worldObj);
    }

    @Override
    public IChunkProvider createChunkGenerator()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(worldObj))
            return new ChunkProviderFlatVoid(worldObj);
        return terrainType.getChunkGenerator(worldObj, field_82913_c);
    }
}
