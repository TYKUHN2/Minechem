package minechem.container;

import java.util.ArrayList;
import java.util.List;

import minechem.api.INoDecay;
import minechem.api.IRadiationShield;
import minechem.block.tile.TileDecomposer;
import minechem.init.ModConfig;
import minechem.inventory.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDecomposer extends Container implements IRadiationShield, INoDecay {

	protected TileDecomposer decomposer;
	protected final int kPlayerInventorySlotStart;
	protected final int kPlayerInventorySlotEnd;
	protected final int kDecomposerInventoryEnd;

	public ContainerDecomposer(InventoryPlayer inventoryPlayer, TileDecomposer decomposer) {
		this.decomposer = decomposer;
		kPlayerInventorySlotStart = decomposer.getSizeInventory();
		kPlayerInventorySlotEnd = kPlayerInventorySlotStart + (9 * 4);
		kDecomposerInventoryEnd = decomposer.getSizeInventory();

		addSlotToContainer(new Slot(decomposer, 0, 80, 16) {

		});
		bindOutputSlots();
		bindPlayerInventory(inventoryPlayer);
	}

	private void bindOutputSlots() {
		int x = 8;
		int y = 62;
		int slot = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotOutput(decomposer, 1 + slot, x + (j * 18), y + (i * 18)));
				slot++;
			}
		}
	}

	private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 160));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return true;//decomposer.isUsableByPlayer(entityPlayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
		Slot slotObject = inventorySlots.get(slot);
		ItemStack stack = ItemStack.EMPTY;

		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if (slot < kDecomposerInventoryEnd) {
				if (!mergeItemStack(stackInSlot, kPlayerInventorySlotStart, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			else {
				if (decomposer.isItemValidForSlot(0, stackInSlot)) {
					if (!mergeItemStack(stackInSlot, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot < 37 && stackInSlot.getCount() == stack.getCount()) {
					if (!mergeItemStack(stackInSlot, 37, 46, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot > 36 && stackInSlot.getCount() == stack.getCount()) {
					if (!mergeItemStack(stackInSlot, 10, 37, false)) {
						return ItemStack.EMPTY;
					}
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
		return stack;
	}

	@Override
	public float getRadiationReductionFactor(int baseDamage, ItemStack itemstack, EntityPlayer player) {
		return 0.4F;
	}

	@Override
	public List<ItemStack> getStorageInventory() {
		if (ModConfig.decaySafeMachines) {
			return new ArrayList<ItemStack>();
		}
		else {
			List<ItemStack> storageInventory = new ArrayList<ItemStack>();
			for (int slot = 0; slot <= 18; slot++) {
				ItemStack stack = getSlot(slot).getStack();
				if (!stack.isEmpty()) {
					storageInventory.add(stack);
				}
			}
			return storageInventory;
		}
	}

	@Override
	public List<ItemStack> getPlayerInventory() {
		if (ModConfig.decaySafeMachines) {
			List<ItemStack> inv = new ArrayList<ItemStack>();
			for (int slot = 0; slot < inventorySlots.size(); slot++) {
				ItemStack stack = getSlot(slot).getStack();
				if (!stack.isEmpty()) {
					inv.add(stack);
				}
			}
			return inv;
		}
		else {
			List<ItemStack> playerInventory = new ArrayList<ItemStack>();
			for (int slot = 19; slot < inventorySlots.size(); slot++) {
				ItemStack stack = getSlot(slot).getStack();
				if (!stack.isEmpty()) {
					playerInventory.add(stack);
				}
			}
			return playerInventory;
		}
	}

}
