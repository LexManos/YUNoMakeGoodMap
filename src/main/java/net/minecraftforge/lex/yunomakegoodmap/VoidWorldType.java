package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

public class VoidWorldType extends WorldType
{
    public VoidWorldType()
    {
        super("void");
    }

    @Override
    public WorldChunkManager getChunkManager(World world)
    {
        return new VoidWorldChunkManager(world);
    }

    @Override
    public IChunkProvider getChunkGenerator(World world, String generatorOptions)
    {
        return new ChunkProviderFlatVoid(world);
    }
    
    @Override
    public int getSpawnFuzz()
    {
    	return 1;
    }
}
