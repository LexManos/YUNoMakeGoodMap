package net.minecraftforge.lex.yunomakegoodmap;

import java.lang.reflect.Field;
import java.util.Random;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.MapGenBase;

public class WorldProviderHellVoid extends WorldProviderHell
{
    public IChunkProvider createChunkGenerator()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(worldObj))
            return new ChunkProviderHellVoid(worldObj, worldObj.getSeed());

        return new ChunkProviderHell(worldObj, worldObj.getSeed());
    }

    public static class ChunkProviderHellVoid extends ChunkProviderHell
    {
        private World world;
        private Field genWorldF;

        public ChunkProviderHellVoid(World world, long seed)
        {
            super(world, seed);
            this.world = world;
            genWorldF = ReflectionHelper.findField(MapGenBase.class, "field_75039_c", "worldObj");
            genWorldF.setAccessible(true);
        }

        @Override public Chunk loadChunk(int x, int z){ return this.provideChunk(x, z); }
        @Override
        public void populate(IChunkProvider provider, int x, int z)
        {
            if (YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(world))
                genNetherBridge.generateStructuresInChunk(world, world.rand, x, z);

            int spawnX = world.getWorldInfo().getSpawnX() / 8;
            int spawnY = world.getWorldInfo().getSpawnY();
            int spawnZ = world.getWorldInfo().getSpawnZ() / 8;
            if (x == spawnX / 16 && z == spawnZ / 16)
            {
                YUNoMakeGoodMap.instance.getPlatformType(world).generate(world, spawnX, spawnY, spawnZ);
                // Spawn should always be within linking distance of this portal, if not, they need to move closer :/
            }
        }

        @Override
        public Chunk provideChunk(int x, int z)
        {
            Block[] data =  new Block[32768];

            if (YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(world))
                genNetherBridge.func_151539_a(this, world, x, z, data);
            else
            {
                try
                {
                    genWorldF.set(genNetherBridge, world);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Failed to set world object, either enable nether fortres gen or find a fix:", e);
                }
            }

            Chunk ret = new Chunk(world, data, x, z);
            BiomeGenBase[] biomes = world.getWorldChunkManager().loadBlockGeneratorData(null, x * 16, z * 16, 16, 16);
            byte[] ids = ret.getBiomeArray();

            for (int i = 0; i < ids.length; ++i)
            {
                ids[i] = (byte)biomes[i].biomeID;
            }

            ret.generateSkylightMap();
            return ret;
        }
    }
}
