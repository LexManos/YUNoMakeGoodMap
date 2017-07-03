package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VoidWorldType extends WorldType
{
    public VoidWorldType()
    {
        super("void");
    }

    @Override
    public BiomeProvider getBiomeProvider(World world)
    {
        return new VoidWorldBiomeProvider(world);
    }

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
    {
        return new ChunkGeneratorFlatVoid(world);
    }

    @Override
    public int getSpawnFuzz(WorldServer world, MinecraftServer server)
    {
        return 1;
    }

    @Override
    public boolean isCustomizable()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void onCustomizeButton(Minecraft mc, GuiCreateWorld guiCreateWorld)
    {
        mc.displayGuiScreen(new GuiCustomizeWorld(guiCreateWorld));
    }
}
