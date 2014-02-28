package net.minecraftforge.lex.yunomakegoodmap;

import static net.minecraftforge.common.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.util.Hashtable;
import java.util.logging.Level;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mod(modid = "YUNoMakeGoodMap", name = "YUNoMakeGoodMap", version = "3.0")
public class YUNoMakeGoodMap 
{
    @Instance("YUNoMakeGoodMap")
    public static YUNoMakeGoodMap instance;
    private VoidWorldType worldType;
    private boolean overrideDefault = false;
    private String platformType = "grass";
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        Configuration config = null;
        File cfgFile = event.getSuggestedConfigurationFile();
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
        prop.comment = "Set to true to force the default world types to be void world. Use with caution.";
        overrideDefault = prop.getBoolean(overrideDefault);

        prop = config.get(CATEGORY_GENERAL, "platformType", platformType);
        prop.comment = "Set the type of platform to create in the overworld, Possible values: 'grass' A single grass block, 'tree' a small oak tree on a grass block.";
        platformType = prop.getString();


        if (config.hasChanged())
        {
            config.save();
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event) 
    {
        
        FMLLog.log(Level.INFO, "YUNoMakeGoodMap Initalized");
        LanguageRegistry.instance().addStringLocalization("generator.void", "Void World");

        for (int x = 0; x < WorldType.worldTypes.length; x++)
        {
            if (WorldType.worldTypes[x] == null)
            {
                FMLLog.log(Level.INFO, "YUNoMakeGoodMap Type ID: %d", x);
                worldType = new VoidWorldType(x);
                break;
            }
        }

        Hashtable<Integer, Class<? extends WorldProvider>> providers = ReflectionHelper.getPrivateValue(DimensionManager.class, null, "providers");
        providers.put(-1, WorldProviderHellVoid.class);
        providers.put(0,  WorldProviderSurfaceVoid.class);
        providers.put(1,  WorldProviderEndVoid.class);
    }

    public boolean shouldBeVoid(World world)
    {
        return overrideDefault || world.getWorldInfo().getTerrainType() == worldType;
    }

    public String getPlatformType()
    {
        if (platformType == null) platformType = "grass";
        if (platformType.equalsIgnoreCase("tree")) return "tree";
        return "grass";
    }
}
