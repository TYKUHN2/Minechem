package minechem.block.multiblock.tile;

import javax.annotation.Nullable;

import minechem.block.tile.TileMinechemBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileGhostBlock extends TileMinechemBase {

	private IBlockState bpState;

	public void setRenderedBlockState(final IBlockState bpState) {
		this.bpState = bpState;
	}

	public IBlockState getRenderedBlockState() {
		return bpState;
	}

	public ItemStack getBlockAsItemStack() {
		if (bpState != null) {
			final Block block = getRenderedBlockState().getBlock();
			final int meta = block.getMetaFromState(getRenderedBlockState());
			return new ItemStack(block, 1, meta);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setString("blockID", getRenderedBlockState().getBlock().getRegistryName().toString());
		nbtTagCompound.setInteger("blockMeta", getRenderedBlockState().getBlock().getMetaFromState(getRenderedBlockState()));
		return nbtTagCompound;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void readFromNBT(final NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		if (nbtTagCompound.hasKey("blockID") && nbtTagCompound.hasKey("blockMeta")) {
			final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbtTagCompound.getString("blockID")));
			if (block != null) {
				setRenderedBlockState(block.getStateFromMeta(nbtTagCompound.getInteger("blockMeta")));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getMaxRenderDistanceSquared() {
		return Double.MAX_VALUE;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(final int i) {
		return ItemStack.EMPTY;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return false;
	}

	@Override
	public int getField(final int i) {
		return 0;
	}

	@Override
	public void setField(final int i, final int i1) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openInventory(final EntityPlayer entityplayer) {

	}

	@Override
	public void closeInventory(final EntityPlayer entityplayer) {

	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}
