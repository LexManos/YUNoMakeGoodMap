package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkProviderFlat;

public class ChunkProviderFlatVoid extends ChunkProviderFlat
{
    private World world;

    public ChunkProviderFlatVoid(World world)
    {
        super(world, world.getSeed(), false, null);
        this.world = world;
    }

    //@Override private void generate(byte[] par1ArrayOfByte){}
    @Override public void populate(int x, int z){}

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
