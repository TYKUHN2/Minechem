package minechem.fluid;

import minechem.api.RadiationInfo;
import minechem.block.tile.RadiationFluidTileEntity;
import minechem.radiation.RadiationEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MinechemBucketReceiver implements IBehaviorDispenseItem {

	public static void init() {
		IBehaviorDispenseItem source = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.BUCKET);
		MinechemBucketReceiver receiver = new MinechemBucketReceiver(source);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.BUCKET, receiver);
	}

	public final IBehaviorDispenseItem source;

	public MinechemBucketReceiver(IBehaviorDispenseItem source) {
		this.source = source;
	}

	@Override
	public ItemStack dispense(IBlockSource blockSource, ItemStack itemstack) {
		IPosition position = BlockDispenser.getDispensePosition(blockSource);
		World world = blockSource.getWorld();
		BlockPos pos = new BlockPos(position.getX(), position.getY(), position.getZ());
		IBlockState front = world.getBlockState(pos);

		if (front.getBlock() instanceof MinechemFluidBlock) {
			MinechemFluidBlock fluidBlock = (MinechemFluidBlock) front.getBlock();
			if (fluidBlock.getFluid() instanceof MinechemFluid) {
				MinechemFluid fluid = (MinechemFluid) fluidBlock.getFluid();
				ItemStack newstack = MinechemUtil.getBucketForChemical(fluid.getChemical());
				if (newstack.isEmpty()) {
					return ItemStack.EMPTY;
				}
				TileEntity tile = world.getTileEntity(pos);
				if (tile != null && fluid.getChemical().radioactivity() != RadiationEnum.stable) {
					RadiationInfo.setRadiationInfo(((RadiationFluidTileEntity) tile).info, newstack);
				}

				world.destroyBlock(pos, true);
				itemstack.shrink(1);

				if (itemstack.getCount() <= 0) {
					return newstack;
				}
				else {
					TileEntity inventoryTile = blockSource.getBlockTileEntity();
					if (inventoryTile instanceof IInventory) {
						ItemStack stack = MinechemUtil.addItemToInventory((IInventory) blockSource.getBlockTileEntity(), newstack);
						if (!stack.isEmpty()) {
							MinechemUtil.throwItemStack(world, stack, pos.getX(), pos.getY(), pos.getZ());
						}
					}
					else {
						MinechemUtil.throwItemStack(world, newstack, pos.getX(), pos.getY(), pos.getZ());
					}
				}
			}

			return itemstack;
		}

		return source.dispense(blockSource, itemstack);
	}

}
