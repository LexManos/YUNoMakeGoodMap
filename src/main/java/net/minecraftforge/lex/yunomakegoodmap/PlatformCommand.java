package net.minecraftforge.lex.yunomakegoodmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import net.minecraft.block.BlockStructure;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.tileentity.TileEntityStructure.Mode;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class PlatformCommand extends CommandBase
{
    private List<ResourceLocation> platforms = Lists.newArrayList();

    @Override
    public String getCommandName()
    {
        return "platform";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3; //Admins?
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "command.yunmgm.platform.usage";
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        boolean spawn = "spawn".equalsIgnoreCase(args[0]) || "preview".equalsIgnoreCase(args[0]);

        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, Lists.newArrayList("list", "spawn", "preview"));
        else if (args.length == 2 && spawn)
            return getListOfStringsMatchingLastWord(args, getPlatforms());
        else if (args.length == 3 && spawn)
            return Lists.newArrayList("0", "90", "180", "270");

        return Collections.<String>emptyList();
    }

    protected List<ResourceLocation> getPlatforms()
    {
        if (platforms.size() == 0)
        {
            for (ModContainer mc : Loader.instance().getModList())
            {
                File src = mc.getSource();
                if (src == null)
                    continue;

                InputStream is = getClass().getResourceAsStream("/assets/" + mc.getModId() + "/structures/SkyBlockPlatforms.txt");
                if (is == null)
                    continue;
                try
                {
                    for (String line : CharStreams.readLines(new InputStreamReader(is)))
                    {
                        if (getClass().getResourceAsStream("/assets/" + mc.getModId() + "/structures/" + line + ".nbt") != null)
                            platforms.add(new ResourceLocation(mc.getModId(), line));
                    }
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            for (File f : YUNoMakeGoodMap.instance.getStructFolder().listFiles())
            {
                if (!f.isFile() || !f.getName().endsWith(".nbt"))
                    continue;
                platforms.add(new ResourceLocation("/config/", f.getName().substring(0, f.getName().length() - 4)));
            }
        }

        return platforms;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
            throw new WrongUsageException(getCommandUsage(sender));

        String cmd = args[0].toLowerCase(Locale.ENGLISH);
        if ("list".equals(cmd))
        {
            sender.addChatMessage(new TextComponentString("Known Platforms:"));
            for (ResourceLocation rl : getPlatforms())
            {
                sender.addChatMessage(new TextComponentString("  " + rl.toString()));
            }
        }
        else if ("spawn".equals(cmd) || "preview".equals(cmd))
        {
            if (args.length < 2)
                throw new WrongUsageException(getCommandUsage(sender));

            Entity ent = sender.getCommandSenderEntity();
            PlacementSettings settings = new PlacementSettings();
            WorldServer world = (WorldServer)sender.getEntityWorld();

            if (args.length >= 3)
            {
                //TODO: Preview doesnt quite work correctly with rotations....
                String rot = args[2].toLowerCase(Locale.ENGLISH);
                if ("0".equals(rot) || "none".equals(rot))
                    settings.setRotation(Rotation.NONE);
                else if ("90".equals(rot))
                    settings.setRotation(Rotation.CLOCKWISE_90);
                else if ("180".equals(rot))
                    settings.setRotation(Rotation.CLOCKWISE_180);
                else if ("270".equals(rot))
                    settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
                else
                    throw new WrongUsageException("Only rotations none, 0, 90, 180, and 270 allowed.");
            }

            BlockPos pos;
            if (args.length >= 6)
                pos = CommandBase.parseBlockPos(sender, args, 3, false);
            else if (ent != null)
                pos = ent.getPosition();
            else
                throw new WrongUsageException("Must specify a position if the command sender is not an entity");

            Template temp = StructureUtil.loadTemplate(new ResourceLocation(args[1]), world, true);

            BlockPos spawn = StructureUtil.findSpawn(temp, settings);
            if (spawn != null)
                pos = pos.subtract(spawn);

            if ("spawn".equals(cmd))
            {
                sender.addChatMessage(new TextComponentString("Building \"" + args[1] +"\" at " + pos.toString()));
                temp.func_189962_a(world, pos, settings, 2); //Push to world, with no neighbor notifications!
                world.getPendingBlockUpdates(new StructureBoundingBox(pos, pos.add(temp.getSize())), true); //Remove block updates, so that sand doesn't fall!
            }
            else
            {
                BlockPos tpos = pos.down();
                if (spawn != null)
                    tpos = tpos.add(spawn);
                sender.addChatMessage(new TextComponentString("Previewing \"" + args[1] +"\" at " + pos.toString()));
                world.setBlockState(tpos, Blocks.STRUCTURE_BLOCK.getDefaultState().withProperty(BlockStructure.MODE, TileEntityStructure.Mode.LOAD));
                TileEntityStructure te = (TileEntityStructure)world.getTileEntity(tpos);
                if (spawn != null)
                    te.setPosition(te.getPosition().subtract(spawn));
                te.setSize(temp.getSize());
                te.setMode(Mode.LOAD);
                te.markDirty();
            }
        }
        else
            throw new WrongUsageException(getCommandUsage(sender));
    }
}
