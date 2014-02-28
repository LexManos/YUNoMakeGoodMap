package net.minecraftforge.lex.yunomakegoodmap;

import java.util.Hashtable;
import java.util.logging.Level;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mod(modid = "YUNoMakeGoodMap", name = "YUNoMakeGoodMap", version = "3.0")
public class YUNoMakeGoodMap 
{
    @Instance("YUNoMakeGoodMap")
    public static YUNoMakeGoodMap instance;
    private VoidWorldType worldType;
    
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
        providers.put(1,  WorldProviderEndVoid.class);
    }

    public boolean shouldBeVoid(World world)
    {
        return world.getWorldInfo().getTerrainType() == worldType;
    }
}
