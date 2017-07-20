package net.minecraftforge.lex.yunomakegoodmap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class SpawnPlatformSavedData extends WorldSavedData
{
    private static final String DATA_NAME = YUNoMakeGoodMap.MODID + "_data";
    private static final String PLATFORM_NUMBER = "platformNumber";

    public SpawnPlatformSavedData(String name)
    {
        super(name);
    }

    public SpawnPlatformSavedData()
    {
        super(DATA_NAME);
    }

    private int platformNumber = 1;

    public int addAndGetPlatformNumber()
    {
        int no = ++platformNumber;
        markDirty();
        return no;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        int no = nbt.getInteger(PLATFORM_NUMBER);
        platformNumber = no == 0 ? 1 : no;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger(PLATFORM_NUMBER, platformNumber);
        return compound;
    }

    public static SpawnPlatformSavedData get(World world)
    {
        SpawnPlatformSavedData instance = (SpawnPlatformSavedData) world.getMapStorage()
                .getOrLoadData(SpawnPlatformSavedData.class, DATA_NAME);

        if (instance == null)
        {
            instance = new SpawnPlatformSavedData();
            world.getMapStorage().setData(DATA_NAME, instance);
        }
        return instance;
    }

}
