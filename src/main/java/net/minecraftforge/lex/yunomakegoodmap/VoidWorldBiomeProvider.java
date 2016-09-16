package net.minecraftforge.lex.yunomakegoodmap;

import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.lex.yunomakegoodmap.generators.IPlatformGenerator;

public class VoidWorldBiomeProvider extends BiomeProvider
{
    private World world;
    
    public VoidWorldBiomeProvider(World world)
    {
        super(world.getWorldInfo());
        this.world = world;
    }

    @Override
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random rand)
    {
        BlockPos ret = super.findBiomePosition(x, z, range, biomes, rand);
        if (x == 0 && z == 0 && !world.getWorldInfo().isInitialized())
        {
            if (ret == null)
            {
                ret = BlockPos.ORIGIN;
            }

            buildSpawn(world, new BlockPos(ret.getX(), world.provider.getAverageGroundLevel(), ret.getZ()));
        }
        return ret;
    }

    private void buildSpawn(World world, BlockPos pos)
    {
        FMLLog.info("[YUNoMakeGoodMap] Building spawn platform at: %d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
        IPlatformGenerator platform = YUNoMakeGoodMap.instance.getPlatformType(world);
        platform.generate(world, pos);
    }
}
