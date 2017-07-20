package net.minecraftforge.lex.yunomakegoodmap;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

public class NewSpawnPlatformCommand extends PlatformCommand
{
    @Override
    public String getName()
    {
        return "newSpawnPlatform";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "command.yunmgm.newspawn.usage";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {

        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, getPlatforms());
        else if (args.length == 2)
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        return Collections.<String> emptyList();
    }

    private BlockPos getPositionOfPlatform(World world, int platformNumber)
    {
        int intRoot = (int) Math.floor(Math.sqrt(platformNumber));

        int x = (int) ((Math.round(intRoot / 2.0) * Math.pow(-1, intRoot + 1.0)) + (Math.pow(-1, intRoot + 1.0)
                * (((intRoot * (intRoot + 1)) - platformNumber) - Math.abs((intRoot * (intRoot + 1)) - platformNumber))
                / 2));

        int z = (int) ((Math.round(intRoot / 2.0) * Math.pow(-1, intRoot)) + (Math.pow(-1, intRoot + 1.0)
                * (((intRoot * (intRoot + 1)) - platformNumber) + Math.abs((intRoot * (intRoot + 1)) - platformNumber))
                / 2));
        int platformDistance = YUNoMakeGoodMap.instance.getPlatformDistance(world);

        BlockPos spawnPoint = world.provider.getSpawnPoint();
        return new BlockPos(spawnPoint.getX() + x * platformDistance, world.provider.getAverageGroundLevel(),
                spawnPoint.getZ() + z * platformDistance);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 2)
            throw new WrongUsageException(getUsage(sender));

        EntityPlayer player = getPlayer(server, sender, args[1]);

        if (player != null)
        {
            PlacementSettings settings = new PlacementSettings();
            WorldServer world = (WorldServer) sender.getEntityWorld();

            int platformNumber = SpawnPlatformSavedData.get(world).addAndGetPlatformNumber();
            BlockPos pos = getPositionOfPlatform(world, platformNumber);

            Template temp = StructureUtil.loadTemplate(new ResourceLocation(args[0]), world, true);
            BlockPos spawn = StructureUtil.findSpawn(temp, settings);
            spawn = spawn == null ? pos : spawn.add(pos);

            sender.sendMessage(new TextComponentString("Building \"" + args[0] + "\" at " + pos.toString()));
            temp.addBlocksToWorld(world, pos, settings, 2); //Push to world, with no neighbor notifications!
            world.getPendingBlockUpdates(new StructureBoundingBox(pos, pos.add(temp.getSize())), true); //Remove block updates, so that sand doesn't fall!

            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).setPositionAndUpdate(spawn.getX() + 0.5, spawn.getY() + 1.6, spawn.getZ() + 0.5);
            }

            player.setSpawnChunk(spawn, true, world.provider.getDimension());
        }
        else
        {
            throw new WrongUsageException(getUsage(sender));
        }
    }
}
