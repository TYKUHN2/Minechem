package minechem.block.tile;

import javax.annotation.Nullable;

import minechem.init.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class TileMinechemEnergyBase extends TileMinechemBase {
	/**
	 * Determines amount of energy we are allowed to input into the machine with a given update.
	 */
	private static int maxEnergyReceived = ModConfig.energyPacketSize;
	private int maxEnergy;
	private int energyStored;

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
		return hasCapability(capability, facing) ? CapabilityEnergy.ENERGY.cast(new IEnergyStorage() {

			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				int received = (maxReceive <= maxEnergyReceived ? maxReceive : maxEnergyReceived);
				received = (energyStored + received > maxEnergy ? maxEnergy - energyStored : received);
				if (!simulate) {
					energyStored += received;
					shouldUpdate = true;
				}
				return received;
			}

			@Override
			public int extractEnergy(int maxExtract, boolean simulate) {
				return 0;
			}

			@Override
			public int getEnergyStored() {
				return energyStored;
			}

			@Override
			public int getMaxEnergyStored() {
				return maxEnergy;
			}

			@Override
			public boolean canExtract() {
				return false;
			}

			@Override
			public boolean canReceive() {
				return true;
			}

		}) : null;
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

	protected int receiveEnergy(int maxReceive, boolean simulate) {
		return getForgeEnergyCap().receiveEnergy(maxReceive, simulate);
	}

	public int getMaxEnergyStored() {
		return getForgeEnergyCap().getMaxEnergyStored();
	}

	public int getEnergyStored() {
		return getForgeEnergyCap().getEnergyStored();
	}

	protected boolean useEnergy(int energy) {
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
		shouldUpdate = true;
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

	public abstract int getEnergyRequired();
}
