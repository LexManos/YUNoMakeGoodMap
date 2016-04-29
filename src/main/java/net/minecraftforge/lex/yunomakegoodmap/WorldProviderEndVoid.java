package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.feature.WorldGenSpikes;

public class WorldProviderEndVoid extends WorldProviderEnd
{
    @Override
    public IChunkGenerator createChunkGenerator()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(worldObj))
            return new ChunkProviderEndVoid(worldObj, this.worldObj.getWorldInfo().isMapFeaturesEnabled(), worldObj.getSeed());
        return new ChunkProviderEnd(worldObj, this.worldObj.getWorldInfo().isMapFeaturesEnabled(), worldObj.getSeed());
    }

    public static class ChunkProviderEndVoid extends ChunkProviderEnd
    {
        private World world;
        private WorldGenSpikes spikes = new WorldGenSpikes(/* Blocks.AIR */);

        public ChunkProviderEndVoid(World world, boolean mapFeaturesEnabled, long seed)
        {
            super(world, mapFeaturesEnabled, seed);
            this.world = world;
        }

        @Override public void populate(int x, int z)
        {
            if (YUNoMakeGoodMap.instance.shouldBeVoid(world))
            {
                if (x > -5 && x < 5 && z > -5 && z < 5 && world.rand.nextInt(5) == 0)
                {
                    spikes.generate(world, world.rand, new BlockPos(
                            x*16 + world.rand.nextInt(16) + 8,
                            world.provider.getAverageGroundLevel(),
                            z*16 + world.rand.nextInt(16) + 8));
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
            Chunk ret = new Chunk(world, new ChunkPrimer(), x, z);
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
