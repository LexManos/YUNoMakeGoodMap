package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.block.BlockFalling;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import java.util.Random;

public class ChunkProviderFlatVoid extends ChunkProviderFlat
{
    private World world;
    private Random rand;

    public ChunkProviderFlatVoid(World world)
    {
        super(world, world.getSeed(), false, null);
        this.world = world;
        this.rand = new Random(world.getSeed());
    }

    //@Override private void generate(byte[] par1ArrayOfByte){}
    @Override public Chunk provideChunk(BlockPos pos){ return this.provideChunk(pos.getX() >> 4, pos.getZ() >> 4); }

    @Override public void populate(IChunkProvider provider, int x, int z)
    {
        BlockFalling.fallInstantly = true;

        this.rand.setSeed(this.world.getSeed());
        long i1 = this.rand.nextLong() / 2L * 2L + 1L;
        long j1 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)x * i1 + (long)z * j1 ^ world.getSeed());

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(provider, world, rand, x, z, false));

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(provider, world, rand, x, z, false));
        BlockFalling.fallInstantly = false;
    }

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
