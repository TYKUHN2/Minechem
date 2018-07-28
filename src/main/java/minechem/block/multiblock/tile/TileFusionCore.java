package minechem.block.multiblock.tile;

import javax.annotation.Nullable;

import minechem.init.ModConfig;
import minechem.init.ModItems;
import minechem.init.ModNetworking;
import minechem.item.ItemElement;
import minechem.item.blueprint.BlueprintFusion;
import minechem.item.element.ElementEnum;
import minechem.network.message.FusionUpdateMessage;
import minechem.utils.MinechemUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

public class TileFusionCore extends TileReactorCore implements ISidedInventory {

	public static boolean canProcess = false;
	public static int fusedResult = 0;
	public static int inputLeft = 0;
	public static int inputRight = 1;
	public static int output = 2;
	int timer;

	public TileFusionCore() {
		this(null);
	}

	public TileFusionCore(EnumFacing structureFacing) {
		super();
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		setBlueprint(new BlueprintFusion());
		setStructureFacing(structureFacing);
	}

	@Override
	public void update() {
		super.update();
		if (!structureFormed) {
			return;
		}
		++timer;
		if (!world.isRemote && structureFormed) {
			if (!canProcess) {
				if (getEnergyRequired() <= getEnergyStored() && inputsCanBeFused() && canOutput()) {
					canProcess = true;
				}
			}
			if (canProcess && useEnergy(getEnergyRequired())) {
				fuseInputs();
				removeInputs();
				canProcess = false;
			}
			else {
				fusedResult = 0;
			}
		}
		if (timer >= 20) {
			timer = 0;
			FusionUpdateMessage message = new FusionUpdateMessage(this, structureFormed);
			ModNetworking.INSTANCE.sendToDimension(message, getWorld().provider.getDimension());
			markDirty();
			IBlockState iblockstate = getWorld().getBlockState(getPos());
			if (iblockstate != null) {
				getWorld().notifyBlockUpdate(getPos(), iblockstate, iblockstate, 3);
			}
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing facing) {
		return false;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing facing) {
		return false;
	}

	private void fuseInputs() {
		if (inventory.get(output).isEmpty()) {
			inventory.set(output, new ItemStack(ModItems.element, 1, fusedResult));
		}
		else if (inventory.get(output).getItemDamage() == fusedResult) {
			inventory.get(output).grow(1);
		}
		else {
			canProcess = false;
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing facing) {
		switch (facing.getIndex() / 2) {
		case 0:
			return new int[] {
					output
			};
		case 1:
			return new int[] {
					inputLeft
			};
		case 2:
			return new int[] {
					inputRight
			};
		}
		return new int[0];
	}

	@Override
	public String getName() {
		return "container.minechemFusion";
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int i) {
		return ItemStack.EMPTY;
	}

	public boolean inputsCanBeFused() {
		if (!inventory.get(inputLeft).isEmpty() && !inventory.get(inputRight).isEmpty()) {
			if (inventory.get(inputLeft).getItem() instanceof ItemElement && inventory.get(inputRight).getItem() instanceof ItemElement) {
				int left = inventory.get(inputLeft).getItemDamage();
				int right = inventory.get(inputRight).getItemDamage();
				fusedResult = left + right;
				return (left > 0 && right > 0 && ElementEnum.getByID(fusedResult) != null);
			}
		}
		return false;

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		if (slot == inputLeft || slot == inputRight) {
			if (itemstack.getItem() instanceof ItemElement) {
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
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return structureFormed;
	}

	@Override
	public void openInventory(EntityPlayer entityPlayer) {
	}

	@Override
	public void closeInventory(EntityPlayer entityPlayer) {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		fusedResult = nbtTagCompound.getInteger("fusedResult");
		canProcess = nbtTagCompound.getBoolean("canProcess");
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		MinechemUtil.readTagListToItemStackList(nbtTagCompound.getTagList("inventory", Constants.NBT.TAG_COMPOUND));
	}

	private void removeInputs() {
		decrStackSize(inputLeft, 1);
		decrStackSize(inputRight, 1);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inventory.set(slot, itemstack);
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	public boolean canOutput() {
		if (inventory.get(output).isEmpty()) {
			return true;
		}
		else if (inventory.get(output).getItemDamage() == fusedResult) {
			return inventory.get(output).getCount() < 64;
		}
		return false;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setInteger("fusedResult", fusedResult);
		nbtTagCompound.setBoolean("canProcess", canProcess);
		NBTTagList inventoryTagList = MinechemUtil.writeItemStackListToTagList(inventory);
		nbtTagCompound.setTag("inventory", inventoryTagList);

		return nbtTagCompound;
	}

	@Override
	public int getEnergyRequired() {
		if (!inventory.get(inputLeft).isEmpty() && !inventory.get(inputRight).isEmpty() && inputsCanBeFused() && ModConfig.powerUseEnabled) {
			return (inventory.get(inputLeft).getItemDamage() + inventory.get(inputRight).getItemDamage()) * ModConfig.fusionMultiplier;
		}
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
