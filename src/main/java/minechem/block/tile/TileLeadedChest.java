package minechem.block.tile;

import minechem.block.BlockLeadedChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

public class TileLeadedChest extends TileEntity implements ITickable, IInventory {

	private final NonNullList<ItemStack> inventory;
	private final int stackLimit = 64;
	public float lidAngle;
	public float prevLidAngle;
	public int numPlayersUsing;
	private int ticksSinceSync;

	public TileLeadedChest() {
		inventory = NonNullList.withSize(9, ItemStack.EMPTY);
	}

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		if (slot < 9) {
			return inventory.get(slot);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty()) {
			if (stack.getCount() <= amount) {
				setInventorySlotContents(slot, ItemStack.EMPTY);
			}
			else {
				stack = stack.splitStack(amount);
				if (stack.getCount() == 0) {
					setInventorySlotContents(slot, ItemStack.EMPTY);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(final int slot) {
		final ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty()) {
			setInventorySlotContents(slot, ItemStack.EMPTY);
		}
		return stack;
	}

	@Override
	public String getName() {
		return "container.leadedchest";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer player) {
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack stack) {
		// water through a sieve
		// ripples through a broken dam
		// anything goes here
		return true;

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
	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		final NBTTagList nbttaglist = nbttagcompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final int j = nbttagcompound1.getByte("Slot") & 0xff;
			if (j >= 0 && j < inventory.size()) {
				inventory.set(j, new ItemStack(nbttagcompound1));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		final NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventory.size(); i++) {
			if (!inventory.get(i).isEmpty()) {
				final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventory.get(i).writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
		return nbttagcompound;
	}

	@Override
	public void update() {
		final int i = pos.getX();
		final int j = pos.getY();
		final int k = pos.getZ();
		++ticksSinceSync;

		if (!world.isRemote && numPlayersUsing != 0 && (ticksSinceSync + i + j + k) % 200 == 0) {
			numPlayersUsing = 0;
			for (final EntityPlayer entityplayer : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(i - 5.0F, j - 5.0F, k - 5.0F, i + 1 + 5.0F, j + 1 + 5.0F, k + 1 + 5.0F))) {
				if (entityplayer.openContainer instanceof ContainerChest) {
					final IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

					if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).isPartOfLargeChest(this)) {
						++numPlayersUsing;
					}
				}
			}
		}
		prevLidAngle = lidAngle;
		if (numPlayersUsing > 0 && lidAngle == 0.0F) {
			final double d1 = i + 0.5D;
			final double d2 = k + 0.5D;
			world.playSound((EntityPlayer) null, d1, j + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}
		if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F) {
			final float f2 = lidAngle;
			if (numPlayersUsing > 0) {
				lidAngle += 0.1F;
			}
			else {
				lidAngle -= 0.1F;
			}
			if (lidAngle > 1.0F) {
				lidAngle = 1.0F;
			}
			if (lidAngle < 0.5F && f2 >= 0.5F) {
				final double d3 = i + 0.5D;
				final double d0 = k + 0.5D;
				world.playSound((EntityPlayer) null, d3, j + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			}
			if (lidAngle < 0.0F) {
				lidAngle = 0.0F;
			}
		}
	}

	@Override
	public boolean receiveClientEvent(final int id, final int type) {
		if (id == 1) {
			numPlayersUsing = type;
			return true;
		}
		else {
			return super.receiveClientEvent(id, type);
		}
	}

	@Override
	public void openInventory(final EntityPlayer player) {
		if (!player.isSpectator()) {
			if (numPlayersUsing < 0) {
				numPlayersUsing = 0;
			}
			++numPlayersUsing;
			world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
			world.notifyNeighborsOfStateChange(pos.down(), getBlockType(), true);
		}
	}

	@Override
	public void closeInventory(final EntityPlayer player) {
		if (!player.isSpectator() && getBlockType() instanceof BlockLeadedChest) {
			--numPlayersUsing;
			world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
			world.notifyNeighborsOfStateChange(pos.down(), getBlockType(), true);
		}
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inventory.size(); i++) {
			if (!inventory.get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

}
