package net.minecraftforge.lex.yunomakegoodmap;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class WorldProviderHellVoid extends WorldProviderHell
{
    @Override
    public IChunkGenerator createChunkGenerator()
    {
        if (YUNoMakeGoodMap.instance.shouldBeVoid(world))
            return new ChunkGeneratorHellVoid(world, YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(world), world.getSeed());

        return new ChunkGeneratorHell(world, YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(world), world.getSeed());
    }

    public static class ChunkGeneratorHellVoid extends ChunkGeneratorHell
    {
        private World world;
        private Random hellRNG;

        public ChunkGeneratorHellVoid(World world, boolean shouldGenNetherFortress, long seed)
        {
            super(world, shouldGenNetherFortress, seed);
            this.world = world;
            this.hellRNG = new Random(seed);
            this.genNetherBridge = (MapGenNetherBridge)TerrainGen.getModdedMapGen(genNetherBridge, InitMapGenEvent.EventType.NETHER_BRIDGE);
        }

        @Override
        public void populate(int x, int z)
        {
            if(YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(world))
                genNetherBridge.generateStructure(world, hellRNG, new ChunkPos(x, z));

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
        public Chunk generateChunk(int x, int z)
        {
            ChunkPrimer data = new ChunkPrimer();

            if(YUNoMakeGoodMap.instance.shouldGenerateNetherFortress(world))
                genNetherBridge.generate(world, x, z, data);
            else
                genNetherBridge.generate(world, x, z, null);

            Chunk ret = new Chunk(world, data, x, z);
            Biome[] biomes = world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
            byte[] ids = ret.getBiomeArray();

            for (int i = 0; i < ids.length; ++i)
            {
                ids[i] = (byte)Biome.getIdForBiome(biomes[i]);
            }

            ret.generateSkylightMap();
            return ret;
        }
    }
}
