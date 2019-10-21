package minechem.block.multiblock.tile;

import javax.annotation.Nullable;

import minechem.init.*;
import minechem.item.ItemElement;
import minechem.item.blueprint.BlueprintFission;
import minechem.item.element.ElementEnum;
import minechem.network.message.FissionUpdateMessage;
import minechem.utils.MinechemUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileFissionCore extends TileReactorCore implements ISidedInventory {

	public static int[] kInput = {
			0
	};
	public static int[] kOutput = {
			2
	};

	public static int kStartInput = 0;
	int timer;

	public TileFissionCore() {
		this(null);
	}

	public TileFissionCore(final EnumFacing structureFacing) {
		super();
		inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		setBlueprint(new BlueprintFission());
		setStructureFacing(structureFacing);
	}

	@Override
	public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing)) : super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public void update() {
		super.update();
		++timer;
		if (!world.isRemote && structureFormed) {
			if (!inventory.get(kStartInput).isEmpty()) {
				if (inputIsFissionable()) {
					if (useEnergy(getEnergyRequired())) {
						final ItemStack fissionResult = getFissionOutput();
						addToOutput(fissionResult);
						removeInputs();
					}
				}
			}

		}
		if (timer >= 20) {
			timer = 0;
			final FissionUpdateMessage message = new FissionUpdateMessage(this, structureFormed);
			ModNetworking.INSTANCE.sendToDimension(message, getWorld().provider.getDimension());
			markDirty();
			final IBlockState iblockstate = getWorld().getBlockState(getPos());
			if (iblockstate != null) {
				getWorld().notifyBlockUpdate(getPos(), iblockstate, iblockstate, 3);
			}
		}
	}

	public boolean inputIsFissionable() {
		final ItemStack fissionResult = getFissionOutput();
		if (!fissionResult.isEmpty()) {
			if (inventory.get(kOutput[0]).isEmpty()) {
				return true;
			}
			final boolean sameItem = fissionResult.getItem() == inventory.get(kOutput[0]).getItem() && fissionResult.getItemDamage() == inventory.get(kOutput[0]).getItemDamage();
			return inventory.get(kOutput[0]).getCount() < 64 && sameItem;
		}
		return false;
	}

	private void addToOutput(final ItemStack fusionResult) {
		if (fusionResult.isEmpty()) {
			return;
		}
		if (inventory.get(kOutput[0]).isEmpty()) {
			final ItemStack output = fusionResult.copy();
			inventory.set(kOutput[0], output);
		}
		else {
			inventory.get(kOutput[0]).grow(2);
		}
	}

	private void removeInputs() {
		decrStackSize(kInput[0], 1);
	}

	private ItemStack getFissionOutput() {
		if (!inventory.get(kInput[0]).isEmpty() && inventory.get(kInput[0]).getItem() instanceof ItemElement && inventory.get(kInput[0]).getItemDamage() > 0) {
			final int mass = MinechemUtil.getElement(inventory.get(kInput[0])).atomicNumber();
			final int newMass = mass / 2;
			if (newMass > 0 && ElementEnum.getByID(newMass) != null) {
				return new ItemStack(ModItems.element, 2, newMass);
			}
			else {
				return ItemStack.EMPTY;
			}
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(final int i) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack itemstack) {
		inventory.set(slot, itemstack);
	}

	@Override
	public String getName() {
		return "container.minechemFission";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer entityPlayer) {
		return structureFormed;
	}

	@Override
	public void openInventory(final EntityPlayer entityPlayer) {

	}

	@Override
	public void closeInventory(final EntityPlayer entityPlayer) {

	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		final NBTTagList inventoryTagList = MinechemUtil.writeItemStackListToTagList(inventory);
		nbtTagCompound.setTag("inventory", inventoryTagList);

		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		//inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		inventory = MinechemUtil.readTagListToItemStackList(nbtTagCompound.getTagList("inventory", Constants.NBT.TAG_COMPOUND));
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack itemstack) {
		if (slot != 2 && itemstack.getItem() instanceof ItemElement) {
			if (slot == 1 && itemstack.getItemDamage() == 91) {
				return true;
			}
			if (slot == 0) {
				return true;
			}
		}
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
	public int[] getSlotsForFace(final EnumFacing enumFacing) {
		return new int[] {
				0, 2
		};
	}

	@Override
	public boolean canInsertItem(final int i, final ItemStack itemStack, final EnumFacing facing) {
		return false;
	}

	@Override
	public boolean canExtractItem(final int i, final ItemStack itemStack, final EnumFacing facing) {
		return i == 2 && !getStackInSlot(i).isEmpty();
	}

	@Override
	public int getEnergyRequired() {
		if (!inventory.get(0).isEmpty()) {
			return inventory.get(0).getItemDamage() * ModConfig.fissionMultiplier;
		}
		return 0;
	}

	/*
		@Override
		public int receiveEnergy(int i, boolean b) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return 0;
		}

		@Override
		public int getMaxEnergyStored() {
			return 0;
		}
	*/
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
