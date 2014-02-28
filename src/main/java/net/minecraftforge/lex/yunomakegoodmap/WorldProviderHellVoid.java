package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;

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
        public ChunkProviderHellVoid(World world, long seed)
        {
            super(world, seed);
            this.world = world;
        }

        @Override public Chunk loadChunk(int x, int z){ return this.provideChunk(x, z); }
        @Override public void populate(IChunkProvider provider, int x, int z){}

        @Override public Chunk provideChunk(int x, int z)
        {
            Chunk ret = new Chunk(world, new byte[32768], x, z);
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
