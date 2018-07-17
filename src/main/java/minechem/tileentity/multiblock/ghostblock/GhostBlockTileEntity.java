package minechem.tileentity.multiblock.ghostblock;

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

public class GhostBlockTileEntity extends TileMinechemBase {

	private IBlockState bpState;

	public void setRenderedBlockState(IBlockState bpState) {
		this.bpState = bpState;
	}

	public IBlockState getRenderedBlockState() {
		return bpState;
	}

	public ItemStack getBlockAsItemStack() {
		Block block = getRenderedBlockState().getBlock();
		int meta = block.getMetaFromState(getRenderedBlockState());
		return new ItemStack(block, 1, meta);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setString("blockID", getRenderedBlockState().getBlock().getRegistryName().toString());
		nbtTagCompound.setInteger("blockMeta", getRenderedBlockState().getBlock().getMetaFromState(getRenderedBlockState()));
		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		if (nbtTagCompound.hasKey("blockID") && nbtTagCompound.hasKey("blockMeta")) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbtTagCompound.getString("blockID")));
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
	public ItemStack removeStackFromSlot(int i) {
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
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public int getField(int i) {
		return 0;
	}

	@Override
	public void setField(int i, int i1) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer entityplayer) {

	}

	@Override
	public void closeInventory(EntityPlayer entityplayer) {

	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}
