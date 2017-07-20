package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorFlat;

public class ChunkGeneratorFlatVoid extends ChunkGeneratorFlat
{
    private World world;

    public ChunkGeneratorFlatVoid(World world)
    {
        super(world, world.getSeed(), false, null);
        this.world = world;
        if (this.world.getWorldType() != WorldType.FLAT)
            this.world.setSeaLevel(63); //Fixup sea level as they now calculate it in flat worlds.
    }

    @Override public void populate(int par2, int par3){}

    @Override public Chunk generateChunk(int x, int z)
    {
        Chunk ret = new Chunk(world, new ChunkPrimer(), x, z);
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
