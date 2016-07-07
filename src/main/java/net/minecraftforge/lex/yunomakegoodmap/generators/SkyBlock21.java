package net.minecraftforge.lex.yunomakegoodmap.generators;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockPortal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Generates a 'SkyBlock 2.1' map, you can see the original map located here:
 * http://www.minecraftforum.net/topic/600254-surv-skyblock/
 */
public class SkyBlock21 implements IPlatformGenerator
{
    @Override
    public void generate(World world, BlockPos pos)
    {
        if (world.provider.getDimension() == 0)
            generateOverworld(world, pos.down(3));
        else if (world.provider.getDimension() == -1)
            generateNether(world, pos.west(3));
    }

    private void generateOverworld(World world, BlockPos pos)
    {
        //world.scheduledUpdatesAreImmediate = true;
        //Main platform:
        for (int i = -1; i < 5; i++)
        {
            for(int j = -1; j < 5; j++)
            {
                for (int k = 0; k < 3; k++)
                {
                    if (!(i > 1 && j > 1))
                    {
                        world.setBlockState(pos.add(i, k, j), (k == 2 ? Blocks.GRASS.getDefaultState() : Blocks.DIRT.getDefaultState()));
                    }
                }
            }
        }
        world.setBlockState(pos, Blocks.BEDROCK.getDefaultState());

        //Main platform chest:
        world.setBlockState(pos.add(4, 3, 0), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST), 3);
        TileEntityChest chest = (TileEntityChest)world.getTileEntity(pos.add(4, 3, 0));
        chest.setInventorySlotContents(0, new ItemStack(Items.LAVA_BUCKET));
        chest.setInventorySlotContents(1, new ItemStack(Blocks.ICE));

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
                        world.setBlockState(pos.add(-1 + i, 6 + k, 4 + j), Blocks.LEAVES.getDefaultState());
                }
            }
        }
        for (int i = 0; i < 6; i++)
            world.setBlockState(pos.add(-1, 3 + i, 4), Blocks.LOG.getDefaultState());
        world.setBlockToAir(pos.add(1, 6, 2));
        world.setBlockToAir(pos.add(1, 7, 2));
        world.setBlockToAir(pos.add(1, 7, 6));
        world.setBlockToAir(pos.add(-3, 6, 6));
        world.setBlockToAir(pos.add(-3, 7, 6));
        world.setBlockToAir(pos.add(-3, 7, 2));

        // Sand Island:
        world.setBlockState(pos.add(-67, 3, 3), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST), 3);
        world.setBlockState(pos.add(-68, 3, 4), Blocks.CACTUS.getDefaultState(), 2);
        
        chest = (TileEntityChest)world.getTileEntity(pos.add(-67, 3, 3));
        chest.setInventorySlotContents(0, new ItemStack(Blocks.OBSIDIAN, 10));
        chest.setInventorySlotContents(1, new ItemStack(Items.MELON));
        chest.setInventorySlotContents(2, new ItemStack(Items.PUMPKIN_SEEDS));

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    world.setBlockState(pos.add(-66 - i, j, 2 + k), Blocks.SAND.getDefaultState(), 2);
        
        //world.scheduledUpdatesAreImmediate = false;
    }

    private void generateNether(World world, BlockPos pos)
    {
        //world.scheduledUpdatesAreImmediate = true;
        
        for (int i = -1; i < 2; i++)
            for (int j = 0; j < 3; j++)
                for (int k = -1; k < 2; k++)
                    world.setBlockState(pos.add(i, -1 + j, k), Blocks.GLOWSTONE.getDefaultState());

        world.setBlockState(pos.add(1, 2, -1), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST), 3);
        TileEntityChest chest = (TileEntityChest)world.getTileEntity(pos.add(1, 2, -1));
        chest.setInventorySlotContents(0, new ItemStack(Blocks.SAPLING, 1, 2));
        chest.setInventorySlotContents(1, new ItemStack(Items.REEDS));
        chest.setInventorySlotContents(2, new ItemStack(Blocks.ICE));

        world.setBlockState(pos.add(1, 2, 0),  Blocks.BROWN_MUSHROOM.getDefaultState(), 2);
        world.setBlockState(pos.add(-1, 2, 1), Blocks.RED_MUSHROOM.getDefaultState(),   2);

        //Portal Frame
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                if ((i == 0 || i == 3) && (j == 0 || j == 4)) continue;
                if ((i == 1 || i == 2) && (j >  0 && j <  4)) continue;
                world.setBlockState(pos.add(2, 1 + j, -1 + i), Blocks.OBSIDIAN.getDefaultState());
            }
        }
        for (int j = 0; j < 3; j++)
        {
            world.setBlockState(pos.add(2, 2 + j, 0), Blocks.PORTAL.getDefaultState().withProperty(BlockPortal.AXIS, EnumFacing.Axis.Z), 2);
            world.setBlockState(pos.add(2, 2 + j, 1), Blocks.PORTAL.getDefaultState().withProperty(BlockPortal.AXIS, EnumFacing.Axis.Z), 2);
        }
        //world.scheduledUpdatesAreImmediate = false;
    }
}
