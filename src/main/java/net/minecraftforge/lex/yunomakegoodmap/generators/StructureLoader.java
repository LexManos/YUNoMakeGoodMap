package net.minecraftforge.lex.yunomakegoodmap.generators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.apache.commons.compress.utils.IOUtils;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.lex.yunomakegoodmap.YUNoMakeGoodMap;

public class StructureLoader implements IPlatformGenerator
{
    private File baseDir;
    private String fileName;

    public StructureLoader(File baseDir, String fileName)
    {
        this.baseDir = baseDir;
        this.fileName = fileName;
    }

    private Template loadTemplate(String name, WorldServer world)
    {
        File file = new File(this.baseDir, name + ".nbt");
        if (file.exists())
        {
            try
            {
                return loadTemplate(new FileInputStream(file));
            }
            catch (FileNotFoundException e) //literally cant happen but whatever..
            {
                e.printStackTrace();
                return getDefault(world);
            }
        }
        else
        {

            ResourceLocation res = new ResourceLocation(name.indexOf(':') != -1 ? name : YUNoMakeGoodMap.MODID + ":" + name);
            Template ret = loadTemplate(StructureLoader.class.getResourceAsStream("/assets/" + res.getResourceDomain() + "/structures/" + res.getResourcePath() + ".nbt")); //We're on the server we don't have Resource Packs.
            if (ret != null)
                return ret;

            //Cant find it, lets load the one shipped with this mod.
            (new FileNotFoundException(file.toString())).printStackTrace();
            return getDefault(world);
        }
    }

    private Template loadTemplate(InputStream is)
    {
        if (is == null)
            return null;
        try
        {
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(is);
            Template template = new Template();
            template.read(nbt);
            return template;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (is != null)
                IOUtils.closeQuietly(is);
        }
        return null;
    }

    private Template getDefault(WorldServer world)
    {
        Template temp = loadTemplate(StructureLoader.class.getResourceAsStream("/assets/" + YUNoMakeGoodMap.MODID + "/structures/SINGLE_GRASS.nbt"));
        if (temp != null)
            return temp; //Loaded from the jar!

        //If we are SO screwed, that we cant find the default, create one that is 1 block of dirt.
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("author", "LexManos");
        nbt.setInteger("version", 1);
        nbt.setTag("entities", new NBTTagList());
        nbt.setTag("size", getPosNBT(1, 1, 1));

        NBTTagList list = new NBTTagList();
        list.appendTag(NBTUtil.func_190009_a(new NBTTagCompound(), Blocks.GRASS.getDefaultState()));
        nbt.setTag("palette", list);

        list = new NBTTagList();
        NBTTagCompound block = new NBTTagCompound();
        block.setTag("pos", getPosNBT(0, 0, 0));
        block.setInteger("state", 0); //0 hardcoded because we are only using one block
        list.appendTag(block);
        nbt.setTag("blocks", list);

        Template ret = new Template();
        ret.read(nbt);
        return ret;
    }

    private NBTTagList getPosNBT(int x, int y, int z)
    {
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagInt(x));
        list.appendTag(new NBTTagInt(y));
        list.appendTag(new NBTTagInt(z));
        return list;
    }

    private BlockPos findSpawn(Template temp, PlacementSettings settings)
    {
        for (Entry<BlockPos, String> e : temp.getDataBlocks(new BlockPos(0,0,0), settings).entrySet())
        {
            if ("SPAWN_POINT".equals(e.getValue()))
                return e.getKey();
        }
        return null;
    }

    @Override
    public void generate(World world, BlockPos pos)
    {
        PlacementSettings settings = new PlacementSettings();
        Template temp = loadTemplate(this.fileName, (WorldServer)world);

        BlockPos spawn = findSpawn(temp, settings);
        if (spawn != null)
        {
            pos = pos.subtract(spawn);
            world.setSpawnPoint(pos);
        }

        temp.func_189962_a(world, pos, settings, 0); //Push to world, with no neighbor notifications!
        world.getPendingBlockUpdates(new StructureBoundingBox(pos, pos.add(temp.getSize())), true); //Remove block updates, so that sand doesn't fall!
    }
}
