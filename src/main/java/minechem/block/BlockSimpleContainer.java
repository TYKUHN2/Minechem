package minechem.block;

import javax.annotation.Nullable;

import minechem.api.ICustomRenderer;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
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

	/*
		@Override
		public void breakBlock(World world, BlockPos pos, IBlockState state) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity != null) {
				ArrayList<ItemStack> droppedStacks = new ArrayList<ItemStack>();
				if (tileEntity instanceof TileReactorCore && tileEntity instanceof IInventory) {
					IInventory inv = (IInventory) tileEntity;
					for (int i = 0; i < inv.getSizeInventory(); i++) {
						if (!inv.getStackInSlot(i).isEmpty()) {
							droppedStacks.add(inv.getStackInSlot(i));
						}
					}
				}
				for (ItemStack itemstack : droppedStacks) {
					MinechemUtil.throwItemStack(world, itemstack, pos.getX(), pos.getY(), pos.getZ());
				}

				super.breakBlock(world, pos, state);
			}
		}
	*/
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName().replace("tile.", "block.");
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.025F);
		stack = getItemBlockWithNBT(te);
		spawnAsEntity(worldIn, pos, stack);
	}

	private ItemStack getItemBlockWithNBT(@Nullable TileEntity te) {
		ItemStack stack = getSilkTouchDrop(te.getWorld().getBlockState(te.getPos()));//new ItemStack(this);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		if (te != null) {
			te.writeToNBT(nbttagcompound);
			stack.setTagInfo("BlockEntityTag", nbttagcompound);
		}
		return stack;
	}

}
