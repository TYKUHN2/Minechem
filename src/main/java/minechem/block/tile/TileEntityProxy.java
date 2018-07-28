package minechem.block.tile;

import javax.annotation.Nullable;

import minechem.block.multiblock.tile.TileFissionCore;
import minechem.block.multiblock.tile.TileFusionCore;
import minechem.block.multiblock.tile.TileReactorCore;
import minechem.init.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityProxy extends TileMinechemEnergyBase implements ISidedInventory {

	public TileReactorCore manager;
	int managerXOffset;
	int managerYOffset;
	int managerZOffset;
	final EnergySettableMultiBlock energyStorage = new EnergySettableMultiBlock(null);

	public TileEntityProxy() {
		super(ModConfig.energyPacketSize);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
		return hasCapability(capability, facing) ? CapabilityEnergy.ENERGY.cast(energyStorage) : null;
	}

	public TileReactorCore getManager() {
		int xx = getPos().getX();
		int yy = getPos().getY();
		int zz = getPos().getZ();
		boolean posMatch = managerXOffset == xx && managerYOffset == yy && managerZOffset == zz;

		if (!posMatch && manager == null) {
			TileEntity te = world.getTileEntity(new BlockPos(getPos().getX() + managerXOffset, getPos().getY() + managerYOffset, getPos().getZ() + managerZOffset));
			if (te instanceof TileFusionCore) {
				manager = (TileFusionCore) te;
			}
			else if (te instanceof TileFissionCore) {
				manager = (TileFissionCore) te;
			}
			else if (te instanceof TileReactorCore) {
				manager = (TileReactorCore) te;
			}
			else {
				manager = null;
			}
			energyStorage.setManager(manager);

		}
		return manager;
	}

	@Override
	public void update() {
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		if (manager != null) {
			nbtTagCompound.setInteger("managerXOffset", manager.getPos().getX());
			nbtTagCompound.setInteger("managerYOffset", manager.getPos().getY());
			nbtTagCompound.setInteger("managerZOffset", manager.getPos().getZ());
		}

		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		managerXOffset = nbtTagCompound.getInteger("managerXOffset");
		managerYOffset = nbtTagCompound.getInteger("managerYOffset");
		managerZOffset = nbtTagCompound.getInteger("managerZOffset");
		int xx = getPos().getX();
		int yy = getPos().getY();
		int zz = getPos().getZ();
		boolean posMatch = managerXOffset == xx && managerYOffset == yy && managerZOffset == zz;
		if (world != null && !posMatch) {
			BlockPos tilePos = new BlockPos(getPos().getX() + managerXOffset, getPos().getY() + managerYOffset, getPos().getZ() + managerZOffset);
			TileEntity te = world.getTileEntity(tilePos);
			if (te instanceof TileFusionCore) {
				manager = (TileFusionCore) te;
			}
			else if (te instanceof TileFissionCore) {
				manager = (TileFissionCore) te;
			}
			else if (te instanceof TileReactorCore) {
				manager = (TileReactorCore) te;
			}
			else {
				//manager = null;
			}
			energyStorage.setManager(manager);
		}

	}

	public void setManager(TileReactorCore managerTileEntity) {
		manager = managerTileEntity;
		if (managerTileEntity != null) {
			managerXOffset = managerTileEntity.getPos().getX() - pos.getX();
			managerYOffset = managerTileEntity.getPos().getY() - pos.getY();
			managerZOffset = managerTileEntity.getPos().getZ() - pos.getZ();
			energyStorage.setManager(manager);
			markDirty();
			IBlockState iblockstate = getWorld().getBlockState(getPos());
			if (iblockstate != null) {
				getWorld().notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		if (manager != null) {
			return ((ISidedInventory) manager).getSizeInventory();
		}
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (manager != null && manager instanceof ISidedInventory) {
			return ((ISidedInventory) manager).getStackInSlot(i);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (manager != null && manager instanceof ISidedInventory) {
			return ((ISidedInventory) manager).decrStackSize(i, j);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		if (manager != null && manager instanceof ISidedInventory) {
			return ((ISidedInventory) manager).removeStackFromSlot(i);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (manager != null && manager instanceof ISidedInventory) {
			((ISidedInventory) manager).setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getName() {
		return "Multiblock Minechem proxy";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		if (manager != null) {
			return ((ISidedInventory) manager).getInventoryStackLimit();
		}
		return 0;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (manager != null) {
			return ((ISidedInventory) manager).isItemValidForSlot(i, itemstack);
		}
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
	public int[] getSlotsForFace(EnumFacing enumFacing) {
		if (manager != null) {
			return ((ISidedInventory) manager).getSlotsForFace(enumFacing);
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing facing) {
		// Cannot insert items into reactor with automation disabled.
		return ModConfig.AllowAutomation && isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing facing) {
		// Cannot extract items from reactor with automation disabled.
		// Can only extract from the bottom.
		return ModConfig.AllowAutomation && facing.getIndex() == 0 && slot == 2;
	}

	@Override
	public int getEnergyRequired() {
		return 0;
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

	@Override
	public void setEnergy(int amount) {
		if (getForgeEnergyCap() instanceof EnergySettableMultiBlock) {
			((EnergySettableMultiBlock) getForgeEnergyCap()).setEnergy(amount);
		}
	}

	public static class EnergySettableMultiBlock implements IEnergyStorage {

		private TileReactorCore manager;

		public EnergySettableMultiBlock(TileReactorCore manager) {
			this.manager = manager;
		}

		public void setManager(TileReactorCore manager) {
			this.manager = manager;
		}

		public TileReactorCore getManager() {
			return manager;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!canReceive() || getManager() == null) {
				return 0;
			}
			return getManager().receiveEnergy(maxReceive, simulate);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			if (getManager() == null) {
				return 0;
			}
			return getManager().getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			if (getManager() == null) {
				return 0;
			}
			return getManager().getMaxEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return true;
		}

		public void setEnergy(int amount) {
			if (getManager() != null) {
				getManager().setEnergy(Math.abs(amount));
			}
		}
	}

}
