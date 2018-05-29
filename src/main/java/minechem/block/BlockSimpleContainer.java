package minechem.block;

import java.util.ArrayList;

import minechem.api.ICustomRenderer;
import minechem.utils.MinechemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockSimpleContainer extends BlockContainer implements ICustomRenderer {

	protected BlockSimpleContainer(Material material) {
		super(material);
		setHardness(2F);
		setResistance(50F);
	}

	public abstract void addStacksDroppedOnBlockBreak(TileEntity tileEntity, ArrayList<ItemStack> itemStacks);

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null) {
			ArrayList<ItemStack> droppedStacks = new ArrayList<ItemStack>();
			addStacksDroppedOnBlockBreak(tileEntity, droppedStacks);
			for (ItemStack itemstack : droppedStacks) {
				MinechemUtil.throwItemStack(world, itemstack, pos.getX(), pos.getY(), pos.getZ());
			}

			super.breakBlock(world, pos, state);
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

}
