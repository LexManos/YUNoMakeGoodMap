package net.minecraftforge.lex.yunomakegoodmap;

import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraftforge.lex.yunomakegoodmap.generators.IPlatformGenerator;
import cpw.mods.fml.common.FMLLog;

public class VoidWorldChunkManager extends WorldChunkManager
{
    private World world;
    
    public VoidWorldChunkManager(World world)
    {
        super(world);
        this.world = world;
    }

    @Override
    public ChunkPosition findBiomePosition(int x, int z, int range, @SuppressWarnings("rawtypes") List biomes, Random rand)
    {
        ChunkPosition ret = super.findBiomePosition(x, z, range, biomes, rand);
        if (x == 0 && z == 0 && !world.getWorldInfo().isInitialized())
        {
            if (ret == null)
            {
                ret = new ChunkPosition(0, 0, 0);
            }

            buildSpawn(world, ret.x, world.provider.getAverageGroundLevel(), ret.z);
        }
        return ret;
    }

    private void buildSpawn(World world, int x, int y, int z)
    {
        FMLLog.info("[YUNoMakeGoodMap] Building spawn platform at: %d, %d, %d", x, y, z);
        IPlatformGenerator platform = YUNoMakeGoodMap.instance.getPlatformType(world);
        platform.generate(world, x, y, z);
    }
}
