package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
            generateOverworld(world, x, y, z);
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
                        world.setBlock(x + i, y + k, z + j, (k == 2 ? Block.grass.blockID : Block.dirt.blockID));
                    }
                }
            }
        }
        world.setBlock(x, y, z, Block.bedrock.blockID);

        //Main platform chest:
        world.setBlock(x + 4, y + 3, z, Block.chest.blockID);
        world.setBlockMetadataWithNotify(x + 4,  y + 3, z, 4, 3);
        TileEntityChest chest = (TileEntityChest)world.getBlockTileEntity(x + 4, y + 3, z);
        chest.setInventorySlotContents(0, new ItemStack(Item.bucketLava));
        chest.setInventorySlotContents(1, new ItemStack(Block.ice));

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
                        world.setBlock(x - 1 + i, y + 6 + k, z + 4 + j, Block.leaves.blockID);
                }
            }
        }
        for (int i = 0; i < 6; i++)
            world.setBlock(x - 1, y + 3 + i, z + 4, Block.wood.blockID);
        world.setBlockToAir(x + 1, y + 6, z + 2);
        world.setBlockToAir(x + 1, y + 7, z + 2);
        world.setBlockToAir(x + 1, y + 7, z + 6);
        world.setBlockToAir(x - 3, y + 6, z + 6);
        world.setBlockToAir(x - 3, y + 7, z + 6);
        world.setBlockToAir(x - 3, y + 7, z + 2);

        // Sand Island:
        world.setBlock(x - 67, y + 3, z + 3, Block.chest.blockID);
        world.setBlockMetadataWithNotify(x - 67, y + 4, z + 3, 5, 3); // For some reason this doesn't rotate the chest u.u
        world.setBlock(x - 68, y + 3, z + 4, Block.cactus.blockID, 0, 2);
        
        chest = (TileEntityChest)world.getBlockTileEntity(x - 67, y + 3, z + 3);
        chest.setInventorySlotContents(0, new ItemStack(Block.obsidian, 10));
        chest.setInventorySlotContents(1, new ItemStack(Item.melon));
        chest.setInventorySlotContents(2, new ItemStack(Item.pumpkinSeeds));

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    world.setBlock(x - 66 - i, y + 0 + j, z + 2 + k, Block.sand.blockID, 0, 2);
        
        world.scheduledUpdatesAreImmediate = false;
    }
}
