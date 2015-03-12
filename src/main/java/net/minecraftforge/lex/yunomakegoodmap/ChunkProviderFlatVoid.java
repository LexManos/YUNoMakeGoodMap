package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
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
    @Override public Chunk provideChunk(BlockPos pos){ return this.provideChunk(pos.getX() >> 4, pos.getZ() >> 4); }
    @Override public void populate(IChunkProvider par1IChunkProvider, int par2, int par3){}

    @Override public Chunk provideChunk(int x, int z)
    {
        Chunk ret = new Chunk(world, new ChunkPrimer(), x, z);
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
