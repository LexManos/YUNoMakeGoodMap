package net.minecraftforge.lex.yunomakegoodmap;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;
import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.lex.yunomakegoodmap.generators.IPlatformGenerator;
import net.minecraftforge.lex.yunomakegoodmap.generators.StructureLoader;
import org.apache.logging.log4j.Level;

@Mod(modid = YUNoMakeGoodMap.MODID, name = YUNoMakeGoodMap.NAME, version = "@MOD_VERSION@", dependencies = "after: BiomesOPlenty")
public class YUNoMakeGoodMap
{
    public static final String NAME = "YUNoMakeGoodMap";
    public static final String MODID = "yunomakegoodmap";

    @Instance(MODID)
    public static YUNoMakeGoodMap instance;
    private VoidWorldType worldType;
    private boolean overrideDefault = false;
    private String platformType = "grass";
    private boolean generateSpikes = false;
    private boolean generateNetherFortress = false;
    private boolean generateEndCities = false;
    private int platformDistance = 1000;
    private File configDir = null;
    private File structDir = null;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);

        Configuration config = null;

        this.configDir = new File(event.getModConfigurationDirectory(), NAME);
        this.structDir = new File(this.configDir, "structures");
        if (!this.structDir.exists())
            this.structDir.mkdirs();

        File cfgFile = new File(this.configDir, NAME + ".cfg");

        if (event.getSuggestedConfigurationFile().exists() && !cfgFile.exists()) //Migrate the old config, whoo!
            event.getSuggestedConfigurationFile().renameTo(cfgFile);

        try
        {
            config = new Configuration(cfgFile);
        }
        catch (Exception e)
        {
            FMLLog.severe("[YUNoMakeGoodMap] Error loading config, deleting file and resetting: ");
            e.printStackTrace();

            if (cfgFile.exists())
                cfgFile.delete();

            config = new Configuration(cfgFile);
        }

        Property prop;

        prop = config.get(CATEGORY_GENERAL, "overrideDefault", overrideDefault);
        prop.setComment("Set to true to force the default world types to be void world. Use with caution.");
        overrideDefault = prop.getBoolean(overrideDefault);

        prop = config.get(CATEGORY_GENERAL, "platformType", platformType);
        prop.setComment("Set the type of platform to create in the overworld, Possible values: \n" +
                       "  'STRUCT_NAME' for custom Structure Files, located in /config/" + NAME +"/structures/\n" +
                       "    Example: 'struct:COBBLE_GEN' to use /config/" + NAME + "/structures/COBBLE_GEN.nbt\n" +
                       "  \n" +
                       "  Default ones provided with this mod:\n" +
                       "  'SINGLE_GRASS' A single grass block\n" +
                       "  'TREE' a small oak tree on a grass block\n" +
                       "  'SKYBLOCK21' For SkyBlock v2.1 platforms\n" +
                       "  'COBBLE_GEN' Small platform with a pre-built cobble gen\n" +
                       "  \n" +
                       "  Other mods can supply platforms as well just need to specify it by using modid:STRUCT_NAME\n" +
                       "  Which will try and load /assets/modid/structures/STRUCT_NAME.nbt");
        platformType = prop.getString();

        prop = config.get(CATEGORY_GENERAL, "generateSpikes", generateSpikes);
        prop.setComment("Set to true to enable generation of the obsidian 'spikes' in the end.");
        generateSpikes = prop.getBoolean(generateSpikes);

        prop = config.get(CATEGORY_GENERAL, "generateNetherFortress", generateNetherFortress);
        prop.setComment("Set to true to enable generation of the nether fortresses.");
        generateNetherFortress = prop.getBoolean(generateNetherFortress);

        prop = config.get(CATEGORY_GENERAL, "generateEndCities", generateEndCities);
        prop.setComment("Set to true to enable generation of the end cities.");
        generateEndCities = prop.getBoolean(generateEndCities);

        prop = config.get(CATEGORY_GENERAL, "platformDistance", platformDistance);
        prop.setComment("the x and z spacing between platforms generated by newSpawnPlatform");
        platformDistance = prop.getInt(platformDistance);

        if (config.hasChanged())
        {
            config.save();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        FMLLog.log(Level.INFO, "YUNoMakeGoodMap Initalized");
        worldType = new VoidWorldType();

        DimensionManager.unregisterDimension(-1);
        DimensionManager.unregisterDimension(0);
        DimensionManager.unregisterDimension(1);
        DimensionManager.registerDimension(-1, DimensionType.register("Nether", "_nether", -1, WorldProviderHellVoid.class, false));
        DimensionManager.registerDimension(0,  DimensionType.register("Overworld", "", 0, WorldProviderSurfaceVoid.class, true));
        DimensionManager.registerDimension(1,  DimensionType.register("The End", "_end", 1, WorldProviderEndVoid.class, false));
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new PlatformCommand());
        event.registerServerCommand(new NewSpawnPlatformCommand());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        //Load a 3x3 around spawn to make sure that it populates and calls our hooks.
        if (!event.getWorld().isRemote && event.getWorld() instanceof WorldServer)
        {
            WorldServer world = (WorldServer)event.getWorld();
            int spawnX = (int)(event.getWorld().getWorldInfo().getSpawnX() / world.provider.getMovementFactor() / 16);
            int spawnZ = (int)(event.getWorld().getWorldInfo().getSpawnZ() / world.provider.getMovementFactor() / 16);
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    world.getChunkProvider().loadChunk(spawnX + x, spawnZ + z);
        }
    }

    public boolean shouldBeVoid(World world)
    {
        return overrideDefault || world.getWorldInfo().getTerrainType() == worldType;
    }

    public IPlatformGenerator getPlatformType(World world)
    {
        if (platformType == null) platformType = "SINGLE_GRASS";

        //Backwards compatibility:
             if (platformType.equals("grass"))       platformType = "SINGLE_GRASS";
        else if (platformType.equals("tree"))        platformType = "TREE";
        else if (platformType.equals("skyblock21"))  platformType = "SKYBLOCK21";
        else if (platformType.startsWith("struct:")) platformType =  platformType.substring(7);

        return new StructureLoader(this.structDir, platformType);
    }

    public boolean shouldGenerateSpikes(World world)
    {
        return generateSpikes;
    }

    public boolean shouldGenerateNetherFortress(World world)
    {
        return generateNetherFortress;
    }

    public boolean shouldGenerateEndCities(World world)
    {
        return generateEndCities;
    }
    
    public int getPlatformDistance(World world) 
    {
    	return platformDistance;
    }

    public File getStructFolder()
    {
        return this.structDir;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT) //Modders should never do this, im just lazy, and I KNOW what im doing.
    public void onOpenGui(GuiOpenEvent e)
    {
        //If we're opening the new world screen from the world selection, default to void world.
        if (e.getGui() instanceof GuiCreateWorld && Minecraft.getMinecraft().currentScreen instanceof GuiWorldSelection)
        {
            //Auto-select void world.
            GuiCreateWorld cw = (GuiCreateWorld)e.getGui();
            ReflectionHelper.setPrivateValue(GuiCreateWorld.class, cw, worldType.getWorldTypeID(),
                    "field_146331_K", "selectedIndex");
        }
    }
}
