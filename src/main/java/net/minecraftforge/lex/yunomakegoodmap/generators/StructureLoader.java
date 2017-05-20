package net.minecraftforge.lex.yunomakegoodmap.generators;

import java.io.File;

import com.google.common.base.Strings;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.lex.yunomakegoodmap.StructureUtil;

public class StructureLoader implements IPlatformGenerator
{
    private String fileName;

    public StructureLoader(File baseDir, String fileName)
    {
        this.fileName = fileName;
    }

    @Override
    public void generate(World world, BlockPos pos)
    {
        PlacementSettings settings = new PlacementSettings();
        Template temp = null;
        String suffix = world.provider.getDimensionType().getSuffix();
        String opts = world.getWorldInfo().getGeneratorOptions() + suffix;

        if (!Strings.isNullOrEmpty(opts))
            temp = StructureUtil.loadTemplate(new ResourceLocation(opts), (WorldServer)world, true);
        if (temp == null)
            temp = StructureUtil.loadTemplate(new ResourceLocation("/config/", this.fileName + suffix), (WorldServer)world, !Strings.isNullOrEmpty(suffix));
        if (temp == null)
            return; //If we're not in the overworld, and we don't have a template...

        BlockPos spawn = StructureUtil.findSpawn(temp, settings);
        if (spawn != null)
        {
            pos = pos.subtract(spawn);
            world.setSpawnPoint(pos);
        }

        temp.addBlocksToWorld(world, pos, settings, 0); //Push to world, with no neighbor notifications!
        world.getPendingBlockUpdates(new StructureBoundingBox(pos, pos.add(temp.getSize())), true); //Remove block updates, so that sand doesn't fall!
    }
}
