package net.minecraftforge.lex.yunomakegoodmap;

import java.util.logging.Level;

import net.minecraft.world.WorldType;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "YUNoMakeGoodMap", name = "YUNoMakeGoodMap", version = "2.1")
public class YUNoMakeGoodMap 
{
    @Instance("YUNoMakeGoodMap")
    public static YUNoMakeGoodMap instance;
    private VoidWorldType worldType;
    
    @Init
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
    }
}
