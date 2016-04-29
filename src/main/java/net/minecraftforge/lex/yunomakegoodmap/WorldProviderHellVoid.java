package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;

public class WorldProviderHellVoid extends WorldProviderHell
{
    public IChunkGenerator createChunkGenerator()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(worldObj))
            return new ChunkProviderHellVoid(worldObj, YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(worldObj), worldObj.getSeed());

        return new ChunkProviderHell(worldObj, YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(worldObj), worldObj.getSeed());
    }

    public static class ChunkProviderHellVoid extends ChunkProviderHell
    {
        private World world;

        public ChunkProviderHellVoid(World world, boolean shouldGenNetherFortress, long seed)
        {
            super(world, shouldGenNetherFortress, seed);
            this.world = world;
        }

        @Override
        public void populate(int x, int z)
        {
            if(YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(world))
                genNetherBridge.generateStructure(world, world.rand, new ChunkCoordIntPair(x, z));

            int spawnX = world.getWorldInfo().getSpawnX() / 8;
            int spawnY = world.getWorldInfo().getSpawnY();
            int spawnZ = world.getWorldInfo().getSpawnZ() / 8;
            if (x == spawnX / 16 && z == spawnZ / 16)
            {
                YUNoMakeGoodMap.instance.getPlatformType(world).generate(world, new BlockPos(spawnX, spawnY, spawnZ));
                // Spawn should always be within linking distance of this portal, if not, they need to move closer :/
            }
        }

        @Override
        public Chunk provideChunk(int x, int z)
        {
            ChunkPrimer data = new ChunkPrimer();

            if(YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(world))
                genNetherBridge.generate(world, x, z, data);
            else
                genNetherBridge.worldObj = world;

            Chunk ret = new Chunk(world, data, x, z);
            BiomeGenBase[] biomes = world.getBiomeProvider().loadBlockGeneratorData(null, x * 16, z * 16, 16, 16);
            byte[] ids = ret.getBiomeArray();

            for (int i = 0; i < ids.length; ++i)
            {
                ids[i] = (byte)BiomeGenBase.getIdForBiome(biomes[i]);
            }

            ret.generateSkylightMap();
            return ret;
        }
    }
}
