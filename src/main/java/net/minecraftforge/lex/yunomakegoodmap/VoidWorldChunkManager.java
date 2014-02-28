package net.minecraftforge.lex.yunomakegoodmap;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntitySign;
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
    public ChunkPosition findBiomePosition(int x, int z, int range, List biomes, Random rand)
    {
        ChunkPosition ret = super.findBiomePosition(x, z, range, biomes, rand);
        if (x == 0 && z == 0 && !world.getWorldInfo().isInitialized())
        {
            if (ret == null)
            {
                ret = new ChunkPosition(0, 0, 0);
            }

            int y = world.provider.getAverageGroundLevel();
            FMLLog.info("Building spawn platform at: %d, %d, %d", ret.x, y, ret.z);
            world.setBlock(ret.x, y, ret.z, Block.grass.blockID);
        }
        return ret;
    }
}
