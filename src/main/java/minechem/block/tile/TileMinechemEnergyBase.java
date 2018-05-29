package minechem.block.tile;

import javax.annotation.Nullable;

// import cofh.api.energy.IEnergyReceiver;
import minechem.init.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

// @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid =
// "CoFHAPI|energy")
public abstract class TileMinechemEnergyBase extends TileMinechemBase //implements IEnergyReceiver
{
	/**
	 * Determines amount of energy we are allowed to input into the machine with a given update.
	 */
	private static int MAX_ENERGY_RECEIVED = ModConfig.energyPacketSize;

	/**
	 * Determines total amount of energy that this machine can store.
	 */
	private int MAX_ENERGY_STORED;

	/**
	 * Amount of energy stored
	 */
	private int energyStored;
	//protected int oldEnergyStored;

	public TileMinechemEnergyBase(int maxEnergy) {
		super();
		MAX_ENERGY_STORED = maxEnergy;
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
				int received = (maxReceive <= MAX_ENERGY_RECEIVED ? maxReceive : MAX_ENERGY_RECEIVED);
				received = (energyStored + received > MAX_ENERGY_STORED ? MAX_ENERGY_STORED - energyStored : received);
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
				return MAX_ENERGY_STORED;
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
