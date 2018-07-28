package minechem.container;

import minechem.block.tile.TileBlueprintProjector;
import minechem.item.blueprint.SlotBlueprint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBlueprintProjector extends Container {

	TileBlueprintProjector projector;
	InventoryPlayer inventoryPlayer;

	public ContainerBlueprintProjector(InventoryPlayer inventoryPlayer, TileBlueprintProjector projector) {
		this.inventoryPlayer = inventoryPlayer;
		this.projector = projector;
		addSlotToContainer(new SlotBlueprint(projector, 0, 24, 28));
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int inventoryY = 122;
		int hotBarY = 180;
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
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
		Slot slotObject = inventorySlots.get(slot);
		ItemStack stack = ItemStack.EMPTY;
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if (slot == 0) {
				if (!mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			else {
				if (projector.isItemValidForSlot(slot, stackInSlot) && !getSlot(0).getHasStack()) {
					ItemStack copy = slotObject.decrStackSize(1);
					getSlot(0).putStack(copy);
					return ItemStack.EMPTY;
				}
				if (slot < 28 && stackInSlot.getCount() == stack.getCount()) {
					if (!mergeItemStack(stackInSlot, 28, 37, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot > 27 && stackInSlot.getCount() == stack.getCount()) {
					if (!mergeItemStack(stackInSlot, 1, 28, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (stackInSlot.getCount() == 0) {
				if (slot == 0) {
					projector.setBlueprint(null);
				}
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
