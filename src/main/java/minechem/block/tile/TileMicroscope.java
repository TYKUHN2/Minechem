package minechem.block.tile;

import javax.annotation.Nullable;

import minechem.api.recipe.ISynthesisRecipe;
import minechem.init.ModItems;
import minechem.item.ItemChemistJournal;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class TileMicroscope extends TileMinechemBase implements IInventory {

	public static int[] kInput = {
			0
	};
	public static int[] kJournal = {
			1
	};

	public boolean isShaped = true;

	public TileMicroscope() {
		inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
	}

	public void onInspectItemStack(ItemStack itemstack) {
		ISynthesisRecipe synthesisRecipe = RecipeHandlerSynthesis.getRecipeFromOutput(itemstack);
		RecipeDecomposer decomposerRecipe = RecipeHandlerDecomposer.instance.getRecipe(itemstack);
		if (!inventory.get(1).isEmpty() && (synthesisRecipe != null || decomposerRecipe != null)) {
			ModItems.journal.addItemStackToJournal(itemstack, inventory.get(1), world);
		}
	}

	@Override
	public int getSizeInventory() {
		return 11;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (slot >= 0 && slot < inventory.size()) {
			ItemStack itemstack = inventory.get(slot);
			inventory.set(slot, ItemStack.EMPTY);
			return itemstack;
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int i) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		inventory.set(slot, itemStack);
		if (slot == 0 && !itemStack.isEmpty() && !world.isRemote) {
			onInspectItemStack(itemStack);
		}
		if (slot == 1 && !itemStack.isEmpty() && !inventory.get(0).isEmpty() && !world.isRemote) {
			onInspectItemStack(inventory.get(0));
		}
	}

	@Override
	public String getName() {
		return "container.microscope";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		double dist = entityPlayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		return world.getTileEntity(pos) != this ? false : dist <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	public int getFacing() {
		return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		ItemStack inpectingStack = inventory.get(0);
		if (!inpectingStack.isEmpty()) {
			NBTTagCompound inspectingStackTag = inpectingStack.writeToNBT(new NBTTagCompound());
			nbtTagCompound.setTag("inspectingStack", inspectingStackTag);
		}
		ItemStack journal = inventory.get(1);
		if (!journal.isEmpty()) {
			NBTTagCompound journalTag = journal.writeToNBT(new NBTTagCompound());
			nbtTagCompound.setTag("journal", journalTag);
		}

		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		NBTTagCompound inspectingStackTag = nbtTagCompound.getCompoundTag("inspectingStack");
		NBTTagCompound journalTag = nbtTagCompound.getCompoundTag("journal");
		ItemStack inspectingStack = new ItemStack(inspectingStackTag);
		ItemStack journalStack = new ItemStack(journalTag);
		inventory.set(0, inspectingStack);
		inventory.set(1, journalStack);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i == kInput[0] || (i == kJournal[0] && itemstack.getItem() instanceof ItemChemistJournal);
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
		if (!isEmpty()) {
			for (int i = 0; i < inventory.size(); i++) {
				inventory.set(i, ItemStack.EMPTY);
			}
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
