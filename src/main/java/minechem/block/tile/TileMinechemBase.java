package minechem.block.tile;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

public abstract class TileMinechemBase extends TileEntity implements ITickable, IInventory {

	protected long ticks = 0;
	protected boolean shouldUpdate = false;

	public void setBlockType(Block block) {
		blockType = block;
	}

	@Override
	public void update() {
		if (ticks >= Long.MAX_VALUE) {
			ticks = 1;
		}
		ticks++;
	}

	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>create();

	/*
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
	    NBTTagCompound tagCompound = new NBTTagCompound();
	    this.writeToNBT(tagCompound);
	    return new SPacketUpdateTileEntity(pos, 0, tagCompound);
	}
	*/

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return super.writeToNBT(nbt);
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inventory.size() > var1 ? inventory.get(var1) : ItemStack.EMPTY;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 255, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (!inventory.get(slot).isEmpty()) {
			ItemStack itemstack;
			if (inventory.get(slot).getCount() <= amount) {

				itemstack = inventory.get(slot);
				inventory.set(slot, ItemStack.EMPTY);
				return itemstack;
			}
			else {
				itemstack = inventory.get(slot).splitStack(amount);
				if (inventory.get(slot).getCount() == 0) {
					inventory.set(slot, ItemStack.EMPTY);
				}
				return itemstack;
			}
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		if (!inventory.get(slot).isEmpty()) {
			ItemStack itemstack = inventory.get(slot);
			inventory.set(slot, ItemStack.EMPTY);
			return itemstack;
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit()) {
			itemstack.setCount(getInventoryStackLimit());
		}
		inventory.set(slot, itemstack);
	}
}
