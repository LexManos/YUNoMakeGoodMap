package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.feature.WorldGenSpikes;

public class WorldProviderEndVoid extends WorldProviderEnd
{

    public IChunkProvider createChunkGenerator()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(worldObj))
            return new ChunkProviderEndVoid(worldObj, worldObj.getSeed());
        return new ChunkProviderEnd(worldObj, worldObj.getSeed());
    }

    public static class ChunkProviderEndVoid extends ChunkProviderEnd
    {
        private World world;
        private WorldGenSpikes spikes = new WorldGenSpikes(0);

        public ChunkProviderEndVoid(World world, long seed)
        {
            super(world, seed);
            this.world = world;
        }

        @Override public Chunk loadChunk(int x, int z){ return this.provideChunk(x, z); }
        @Override public void populate(IChunkProvider provider, int x, int z)
        {
            if (YUNoMakeGoodMap.instance.shouldBeVoid(world))
            {
                if (x > -5 && x < 5 && z > -5 && z < 5 && world.rand.nextInt(5) == 0)
                {
                    spikes.generate(world, world.rand,
                            x*16 + world.rand.nextInt(16) + 8,
                            world.provider.getAverageGroundLevel(),
                            z*16 + world.rand.nextInt(16) + 8);
                }
            }

            if (x == 0 && z == 0)
            {
                EntityDragon dragon = new EntityDragon(world);
                dragon.setLocationAndAngles(0.0D, 128.0D, 0.0D, world.rand.nextFloat() * 360.0F, 0.0F);
                world.spawnEntityInWorld(dragon);
            }
        }

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
