package minechem.container;

import java.util.ArrayList;
import java.util.List;

import minechem.api.INoDecay;
import minechem.api.IRadiationShield;
import minechem.block.tile.TileSynthesis;
import minechem.init.ModItems;
import minechem.inventory.slot.SlotChemical;
import minechem.inventory.slot.SlotChemistJournal;
import minechem.inventory.slot.SlotFake;
import minechem.inventory.slot.SlotSynthesisOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSynthesis extends ContainerWithFakeSlots implements IRadiationShield, INoDecay {

	private TileSynthesis synthesis;

	public ContainerSynthesis(InventoryPlayer inventoryPlayer, TileSynthesis synthesis) {
		this.synthesis = synthesis;
		addSlotToContainer(new SlotSynthesisOutput(synthesis, TileSynthesis.kStartOutput, 134, 18));
		bindRecipeMatrixSlots();
		bindStorageSlots();
		addSlotToContainer(new SlotChemistJournal(synthesis, TileSynthesis.kStartJournal, 26, 36));
		bindPlayerInventory(inventoryPlayer);
	}

	private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 123 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 181));
		}
	}

	private void bindRecipeMatrixSlots() {
		int slot = 0;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				addSlotToContainer(new SlotFake(synthesis, TileSynthesis.kStartRecipe + slot, 62 + (col * 18), 18 + (row * 18)) {
					@Override
					public boolean isItemValid(ItemStack itemstack) {
						return itemstack.getItem() == ModItems.element || itemstack.getItem() == ModItems.molecule;
					}
				});
				slot++;
			}
		}
	}

	private void bindStorageSlots() {
		int slot = 0;
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 9; col++) {
				addSlotToContainer(new SlotChemical(synthesis, TileSynthesis.kStartStorage + slot, 8 + (col * 18), 84 + (row * 18)));
				slot++;
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return synthesis.isUsableByPlayer(var1);
	}

	public void craftMaxmimum() {
		int amount = 0;
		ItemStack outputItem = synthesis.getCurrentRecipe().getOutput();
		for (int slot = 29; slot < inventorySlots.size(); slot++) {
			ItemStack stack = getSlot(slot).getStack();
			if (!stack.isEmpty()) {
				amount += outputItem.getMaxStackSize();
			}
			else if (stack.isItemEqual(outputItem)) {
				amount += outputItem.getMaxStackSize() - stack.getCount();
			}
		}

		List<ItemStack> outputs = synthesis.getOutput(amount);
		for (ItemStack output : outputs) {
			mergeItemStack(output, synthesis.getSizeInventory(), inventorySlots.size(), true);
		}
	}

	@Override
	public List<ItemStack> getPlayerInventory() {
		List<ItemStack> playerInventory = new ArrayList<ItemStack>();
		for (int slot = TileSynthesis.kStartJournal + 1; slot < inventorySlots.size(); slot++) {
			ItemStack stack = getSlot(slot).getStack();
			if (!stack.isEmpty()) {
				playerInventory.add(stack);
			}
		}
		return playerInventory;
	}

	@Override
	public float getRadiationReductionFactor(int baseDamage, ItemStack itemstack, EntityPlayer player) {
		return 0.4F;
	}

	@Override
	public List<ItemStack> getStorageInventory() {
		List<ItemStack> storageInventory = new ArrayList<ItemStack>();
		for (int slot = 0; slot <= TileSynthesis.kStartJournal; slot++) {
			ItemStack stack = getSlot(slot).getStack();
			if (!stack.isEmpty()) {
				storageInventory.add(stack);
			}
		}
		return storageInventory;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
		Slot slotObject = inventorySlots.get(slot);
		ItemStack stack = ItemStack.EMPTY;

		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if (slot != TileSynthesis.kStartJournal && stack.getItem() == ModItems.journal && !getSlot(TileSynthesis.kStartJournal).getHasStack()) {
				ItemStack copystack = slotObject.decrStackSize(1);
				getSlot(TileSynthesis.kStartJournal).putStack(copystack);
				return ItemStack.EMPTY;
			}
			else if (slot == TileSynthesis.kStartOutput) {
				craftMaxmimum();
				return ItemStack.EMPTY;
			}
			else if (slot >= synthesis.getSizeInventory() && slot < inventorySlots.size() && (stackInSlot.getItem() == ModItems.element || stackInSlot.getItem() == ModItems.molecule)) {
				if (!mergeItemStack(stackInSlot, TileSynthesis.kStartStorage, TileSynthesis.kStartStorage + TileSynthesis.kStorage.length, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (slot >= TileSynthesis.kStartStorage && slot < TileSynthesis.kStartStorage + TileSynthesis.kStorage.length) {
				if (!mergeItemStack(stackInSlot, synthesis.getSizeInventory(), inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			else if (slot == TileSynthesis.kStartJournal) {
				if (!mergeItemStack(stackInSlot, synthesis.getSizeInventory(), inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			else if (slot < 47 && stackInSlot.getCount() == stack.getCount()) {
				if (!mergeItemStack(stackInSlot, 47, 56, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (slot > 46 && stackInSlot.getCount() == stack.getCount()) {
				if (!mergeItemStack(stackInSlot, 20, 47, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (stackInSlot.getCount() == 0) {
				slotObject.putStack(ItemStack.EMPTY);
			}
			else {
				slotObject.onSlotChanged();
			}
			if (stackInSlot.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}
			slotObject.onTake(entityPlayer, stackInSlot);
		}
		return ItemStack.EMPTY;//stack;
	}

}
