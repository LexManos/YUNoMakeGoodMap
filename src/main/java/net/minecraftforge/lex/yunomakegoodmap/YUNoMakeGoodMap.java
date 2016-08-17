package net.minecraftforge.lex.yunomakegoodmap;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import net.minecraft.init.Blocks;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.lex.yunomakegoodmap.generators.IPlatformGenerator;
import net.minecraftforge.lex.yunomakegoodmap.generators.SingleBlockPlatform;
import net.minecraftforge.lex.yunomakegoodmap.generators.SkyBlock21;
import net.minecraftforge.lex.yunomakegoodmap.generators.StructureLoader;
import net.minecraftforge.lex.yunomakegoodmap.generators.TreePlatform;
import org.apache.logging.log4j.Level;
import com.google.common.collect.Maps;

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
    private Map<String, IPlatformGenerator> generators = Maps.newHashMap();
    private File configDir = null;
    private File structDir = null;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
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
                       "  'grass' A single grass block\n" +
                       "  'tree' a small oak tree on a grass block\n" +
                       "  'skyblock21' For SkyBlock v2.1 platforms\n" +
                       "  'struct:STRUCT_NAME' for custom Structure Files, located in /config/" + NAME +"/structures/\n" +
                       "    Example: 'struct:COBBLE_GEN' to use /config/" + NAME + "/structures/COBBLE_GEN.nbt");
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

        if (config.hasChanged())
        {
            config.save();
        }

        generators.put("grass", new SingleBlockPlatform(Blocks.GRASS.getDefaultState()));
        generators.put("tree", new TreePlatform());
        generators.put("skyblock21", new SkyBlock21());

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
        if (platformType == null) platformType = "grass";

        IPlatformGenerator ret = null;
        if (platformType.startsWith("struct:"))
            ret = new StructureLoader(this.structDir, platformType.substring(7));
        else
            ret = generators.get(platformType.toLowerCase(Locale.ENGLISH));

        if (ret == null)
        {
            platformType = "grass";
            ret = generators.get(platformType);
        }
        return ret;
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
}
