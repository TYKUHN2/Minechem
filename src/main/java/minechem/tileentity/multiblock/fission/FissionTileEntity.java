package minechem.tileentity.multiblock.fission;

import javax.annotation.Nullable;

import minechem.init.ModConfig;
import minechem.init.ModItems;
import minechem.init.ModNetworking;
import minechem.item.ItemElement;
import minechem.item.blueprint.BlueprintFission;
import minechem.item.element.ElementEnum;
import minechem.network.message.FissionUpdateMessage;
import minechem.tileentity.multiblock.MultiBlockTileEntity;
import minechem.utils.MinechemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class FissionTileEntity extends MultiBlockTileEntity implements ISidedInventory {

	public static int[] kInput = {
			0
	};
	public static int[] kOutput = {
			2
	};

	public static int kStartInput = 0;

	public FissionTileEntity() {
		super(ModConfig.maxFissionStorage);
		inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		setBlueprint(new BlueprintFission());
	}

	@Override
	public void update() {
		super.update();
		if (!completeStructure) {
			return;
		}
		if (!world.isRemote) {
			if (!inventory.get(kStartInput).isEmpty()) {
				if (inputIsFissionable()) {
					if (useEnergy(getEnergyRequired())) {
						ItemStack fissionResult = getFissionOutput();
						addToOutput(fissionResult);
						removeInputs();
					}
				}
			}
			FissionUpdateMessage message = new FissionUpdateMessage(this);
			ModNetworking.INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), ModConfig.UpdateRadius));
		}
	}

	public boolean inputIsFissionable() {
		ItemStack fissionResult = getFissionOutput();
		if (!fissionResult.isEmpty()) {
			if (inventory.get(kOutput[0]).isEmpty()) {
				return true;
			}
			boolean sameItem = fissionResult.getItem() == inventory.get(kOutput[0]).getItem() && fissionResult.getItemDamage() == inventory.get(kOutput[0]).getItemDamage();
			return inventory.get(kOutput[0]).getCount() < 64 && sameItem;
		}
		return false;
	}

	private void addToOutput(ItemStack fusionResult) {
		if (fusionResult.isEmpty()) {
			return;
		}

		if (inventory.get(kOutput[0]).isEmpty()) {
			ItemStack output = fusionResult.copy();
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
			int mass = MinechemUtil.getElement(inventory.get(kInput[0])).atomicNumber();
			int newMass = mass / 2;
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
	public ItemStack removeStackFromSlot(int i) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
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
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return completeStructure;

	}

	@Override
	public void openInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public void closeInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		NBTTagList inventoryTagList = MinechemUtil.writeItemStackListToTagList(inventory);
		nbtTagCompound.setTag("inventory", inventoryTagList);

		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		MinechemUtil.readTagListToItemStackList(nbtTagCompound.getTagList("inventory", Constants.NBT.TAG_COMPOUND));
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
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
		switch (enumFacing) {
		case DOWN:
			return FissionTileEntity.kOutput;
		default:
			return FissionTileEntity.kInput;
		}
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemStack, EnumFacing facing) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemStack, EnumFacing facing) {
		return false;
	}

	@Override
	public int getEnergyRequired() {
		if (!inventory.get(0).isEmpty()) {
			return (inventory.get(0).getItemDamage()) * ModConfig.fissionMultiplier;
		}
		return 0;
	}

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
