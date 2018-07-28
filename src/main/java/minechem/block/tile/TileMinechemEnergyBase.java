package minechem.block.tile;

import javax.annotation.Nullable;

import minechem.init.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileMinechemEnergyBase extends TileMinechemBase {
	/**
	 * Determines amount of energy we are allowed to input into the machine with a given update.
	 */
	private static int maxEnergyReceived = ModConfig.energyPacketSize;
	private int maxEnergy;
	public int energyStored;

	public TileMinechemEnergyBase(int maxEnergy) {
		super();
		this.maxEnergy = maxEnergy;
		energyStored = 0;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
		return hasCapability(capability, facing) ? CapabilityEnergy.ENERGY.cast(new EnergySettable(this)) : null;
	}

	public void syncEnergyValue(int syncAt) {
		if (getEnergyStored() > syncAt) {
			useEnergy(getEnergyStored() - syncAt);
		}
		else if (getEnergyStored() < syncAt) {
			receiveEnergy(syncAt - getEnergyStored(), false);
		}
	}

	protected IEnergyStorage getForgeEnergyCap() {
		return getCapability(CapabilityEnergy.ENERGY, null);
	}

	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (getForgeEnergyCap() != null) {
			return getForgeEnergyCap().receiveEnergy(maxReceive, simulate);
		}
		return 0;
	}

	public int getMaxEnergyStored() {
		if (getForgeEnergyCap() != null) {
			return getForgeEnergyCap().getMaxEnergyStored();
		}
		return 0;
	}

	public int getEnergyStored() {
		if (getForgeEnergyCap() != null) {
			return getForgeEnergyCap().getEnergyStored();
		}
		return 0;
	}

	public void setEnergy(int amount) {
		if (getForgeEnergyCap() != null && getForgeEnergyCap() instanceof EnergySettable) {
			((EnergySettable) getForgeEnergyCap()).setEnergy(amount);
		}
	}

	public boolean useEnergy(int energy) {
		if (!ModConfig.powerUseEnabled) {
			return true;
		}
		else if (getEnergyStored() - energy < 0) {
			return false;
		}
		else if (getEnergyStored() - energy > getMaxEnergyStored()) {
			return false;
		}
		energyStored -= energy;
		//shouldUpdate = true;
		super.markDirty();
		return true;
	}

	public int getPowerRemainingScaled(double scale) {
		return (int) (getEnergyStored() * (scale / getMaxEnergyStored()));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("energy", getEnergyStored());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energyStored = nbt.getInteger("energy");
	}

	public int getEnergyRequired() {
		return 0;
	}

	public static class EnergySettable implements IEnergyStorage {

		private final TileMinechemEnergyBase tile;

		public EnergySettable(TileMinechemEnergyBase tile) {
			this.tile = tile;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!canReceive()) {
				return 0;
			}

			int energyReceived = Math.min(tile.maxEnergy - tile.energyStored, Math.max(maxEnergyReceived, maxReceive));
			if (!simulate) {
				tile.energyStored += energyReceived;
			}
			return energyReceived;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return tile.energyStored;
		}

		@Override
		public int getMaxEnergyStored() {
			return tile.maxEnergy;
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
			tile.energyStored = Math.abs(amount);
		}

	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
}
