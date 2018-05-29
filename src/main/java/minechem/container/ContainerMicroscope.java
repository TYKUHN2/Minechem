package minechem.container;

import minechem.block.tile.TileMicroscope;
import minechem.init.ModItems;
import minechem.inventory.slot.SlotChemistJournal;
import minechem.inventory.slot.SlotMicroscopeOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMicroscope extends Container {

	TileMicroscope microscope;
	InventoryPlayer inventoryPlayer;

	public ContainerMicroscope(InventoryPlayer inventoryPlayer, TileMicroscope microscope) {
		this.microscope = microscope;
		this.inventoryPlayer = inventoryPlayer;
		addSlotToContainer(new Slot(microscope, 0, 44, 45));
		addSlotToContainer(new SlotChemistJournal(microscope, 1, 80, 95));
		int slot = 0;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				addSlotToContainer(new SlotMicroscopeOutput(microscope, 2 + slot, 98 + (col * 18), 27 + (row * 18)));
				slot++;
			}
		}
		bindPlayerInventory(inventoryPlayer);
	}

	private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int inventoryY = 135;
		int hotBarY = 193;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, inventoryY + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, hotBarY));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return microscope.isUsableByPlayer(var1);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
		Slot slotObject = inventorySlots.get(slot);
		ItemStack stack = ItemStack.EMPTY;
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if (slot <= 1) {
				if (!mergeItemStack(stackInSlot, 2, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			if (slot != 1 && stack.getItem() == ModItems.journal && !getSlot(1).getHasStack()) {
				ItemStack copy = slotObject.decrStackSize(1);
				getSlot(1).putStack(copy);
				return ItemStack.EMPTY;
			}
			if (slot > 1 && stack.getItem() != ModItems.journal && !getSlot(0).getHasStack()) {
				ItemStack copy = slotObject.decrStackSize(1);
				getSlot(0).putStack(copy);
				return ItemStack.EMPTY;
			}
			if (slot > 37 && stackInSlot.getCount() == stack.getCount()) {
				if (!mergeItemStack(stackInSlot, 11, 38, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (slot < 38 && stackInSlot.getCount() == stack.getCount()) {
				if (!mergeItemStack(stackInSlot, 38, 47, false)) {
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
		return stack;
	}

}
