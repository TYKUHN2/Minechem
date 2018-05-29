package minechem.fluid;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;

public class MinechemBucketDispenser implements IBehaviorDispenseItem {

	public static final MinechemBucketDispenser dispenser = new MinechemBucketDispenser();

	@Override
	public ItemStack dispense(IBlockSource blockSource, ItemStack itemstack) {
		IPosition position = BlockDispenser.getDispensePosition(blockSource);
		World world = blockSource.getWorld();
		BlockPos pos = new BlockPos(position.getX(), position.getY(), position.getZ());
		IBlockState front = world.getBlockState(pos);

		if (!front.getMaterial().isSolid()) {
			world.destroyBlock(pos, true);
			world.setBlockToAir(pos);

			UniversalBucket item = (UniversalBucket) itemstack.getItem();
			/*
			Block block = item.block;
			world.setBlockState(pos, block.getStateFromMeta(0), 3);

			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && item.chemical.radioactivity() != RadiationEnum.stable) {
				int dimensionID = itemstack.getTagCompound().getInteger("dimensionID");
				long lastUpdate = itemstack.getTagCompound().getLong("lastUpdate");
				long decayStart = itemstack.getTagCompound().getLong("decayStart");
				RadiationInfo radioactivity = new RadiationInfo(itemstack, decayStart, lastUpdate, dimensionID, item.chemical.radioactivity());

				((RadiationFluidTileEntity) tile).info = radioactivity;
			}
			*/
			//itemstack.setItem(Items.BUCKET);
			if (FluidUtil.tryPlaceFluid(null, world, pos, itemstack, item.getFluid(itemstack)) != FluidActionResult.FAILURE) {
				return new ItemStack(Items.BUCKET);
			}
		}
		return itemstack;
	}

}
