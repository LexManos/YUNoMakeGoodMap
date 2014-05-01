package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

/**
 * Generates a 'SkyBlock 2.1' map, you can see the original map located here:
 * http://www.minecraftforum.net/topic/600254-surv-skyblock/
 */
public class SkyBlock21 implements IPlatformGenerator
{
    @Override
    public void generate(World world, int x, int y, int z)
    {
        if (world.provider.dimensionId == 0)
            generateOverworld(world, x, y - 3, z);
        else if (world.provider.dimensionId == -1)
            generateNether(world, x - 3, y, z);
    }

    private void generateOverworld(World world, int x, int y, int z)
    {
        world.scheduledUpdatesAreImmediate = true;
        //Main platform:
        for (int i = -1; i < 5; i++)
        {
            for(int j = -1; j < 5; j++)
            {
                for (int k = 0; k < 3; k++)
                {
                    if (!(i > 1 && j > 1))
                    {
                        world.setBlock(x + i, y + k, z + j, (k == 2 ? Blocks.grass : Blocks.dirt));
                    }
                }
            }
        }
        world.setBlock(x, y, z, Blocks.bedrock);

        //Main platform chest:
        world.setBlock(x + 4, y + 3, z, Blocks.chest);
        world.setBlockMetadataWithNotify(x + 4,  y + 3, z, 4, 3);
        TileEntityChest chest = (TileEntityChest)world.getTileEntity(x + 4, y + 3, z);
        chest.setInventorySlotContents(0, new ItemStack(Items.lava_bucket));
        chest.setInventorySlotContents(1, new ItemStack(Blocks.ice));

        //Main Tree:
        for (int i = -2; i < 3; i++)
        {
            for (int j = -2; j < 3; j++)
            {
                for (int k = 0; k < 4; k++)
                {
                    if ((k < 2) ||
                        (k < 3 && Math.abs(i) < 2 && Math.abs(j) < 2) ||
                        (i == 0 && Math.abs(j) < 2) ||
                        (j == 0 && Math.abs(i) < 2))
                        world.setBlock(x - 1 + i, y + 6 + k, z + 4 + j, Blocks.leaves);
                }
            }
        }
        for (int i = 0; i < 6; i++)
            world.setBlock(x - 1, y + 3 + i, z + 4, Blocks.log);
        world.setBlockToAir(x + 1, y + 6, z + 2);
        world.setBlockToAir(x + 1, y + 7, z + 2);
        world.setBlockToAir(x + 1, y + 7, z + 6);
        world.setBlockToAir(x - 3, y + 6, z + 6);
        world.setBlockToAir(x - 3, y + 7, z + 6);
        world.setBlockToAir(x - 3, y + 7, z + 2);

        // Sand Island:
        world.setBlock(x - 67, y + 3, z + 3, Blocks.chest);
        world.setBlockMetadataWithNotify(x - 67, y + 4, z + 3, 5, 3); // For some reason this doesn't rotate the chest u.u
        world.setBlock(x - 68, y + 3, z + 4, Blocks.cactus, 0, 2);
        
        chest = (TileEntityChest)world.getTileEntity(x - 67, y + 3, z + 3);
        chest.setInventorySlotContents(0, new ItemStack(Blocks.obsidian, 10));
        chest.setInventorySlotContents(1, new ItemStack(Items.melon));
        chest.setInventorySlotContents(2, new ItemStack(Items.pumpkin_seeds));

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    world.setBlock(x - 66 - i, y + 0 + j, z + 2 + k, Blocks.sand, 0, 2);
        
        world.scheduledUpdatesAreImmediate = false;
    }

    private void generateNether(World world, int x, int y, int z)
    {
        world.scheduledUpdatesAreImmediate = true;
        
        for (int i = -1; i < 2; i++)
            for (int j = 0; j < 3; j++)
                for (int k = -1; k < 2; k++)
                    world.setBlock(x + i, y - 1 + j, z + k, Blocks.glowstone);

        world.setBlock(x + 1, y + 2, z - 1, Blocks.chest);
        //world.setBlockMetadataWithNotify(x + 1, y + 2, z - 1, 5, 3); // For some reason this doesn't rotate the chest u.u        
        TileEntityChest chest = (TileEntityChest)world.getTileEntity(x + 1, y + 2, z - 1);
        chest.setInventorySlotContents(0, new ItemStack(Blocks.sapling, 1, 2));
        chest.setInventorySlotContents(1, new ItemStack(Blocks.reeds));
        chest.setInventorySlotContents(2, new ItemStack(Blocks.ice));

        world.setBlock(x + 1, y + 2, z,     Blocks.brown_mushroom, 0, 2);
        world.setBlock(x - 1, y + 2, z + 1, Blocks.red_mushroom,   0, 2);

        //Portal Frame
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                if ((i == 0 || i == 3) && (j == 0 || j == 4)) continue;
                if ((i == 1 || i == 2) && (j >  0 && j <  4)) continue;
                world.setBlock(x + 2, y + 1 + j, z - 1 + i, Blocks.obsidian);
            }
        }
        for (int j = 0; j < 3; j++)
        {
            world.setBlock(x + 2, y + 2 + j, z + 0, Blocks.portal, 0, 2);
            world.setBlock(x + 2, y + 2 + j, z + 1, Blocks.portal, 0, 2);
        }
        world.scheduledUpdatesAreImmediate = false;
    }
}
