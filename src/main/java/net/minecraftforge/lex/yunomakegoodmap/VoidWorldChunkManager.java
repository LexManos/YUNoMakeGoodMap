package net.minecraftforge.lex.yunomakegoodmap;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;
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
        String platform = YUNoMakeGoodMap.instance.getPlatformType(world);
        if (platform.equals("tree")) buildTree(world, x, y, z);
        else world.setBlock(x, y, z, Block.grass.blockID);
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
