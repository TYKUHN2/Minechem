package minechem.container;

import minechem.inventory.InventoryPolytool;
import minechem.inventory.slot.SlotPolytool;
import minechem.item.ItemElement;
import minechem.item.polytool.PolytoolHelper;
import minechem.utils.MinechemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPolytool extends Container {
	public InventoryPlayer player;
	//public ArrayList infusionsToUpdate = new ArrayList();

	public ContainerPolytool(EntityPlayer invPlayer) {
		player = invPlayer.inventory;

		addSlotToContainer(new SlotPolytool(new InventoryPolytool(player.getCurrentItem(), invPlayer), 0, 8, 17));

		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(player, x, 8 + 18 * x, 64 + 130));
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + 18 * x, 64 + 72 + y * 18));
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {

		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
		Slot slotObject = inventorySlots.get(slot);
		ItemStack stack = ItemStack.EMPTY;

		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if (slot == 0) {
				return ItemStack.EMPTY;
			}
			else {
				if (stackInSlot.getCount() == 64 && (stackInSlot.getItem() instanceof ItemElement) && PolytoolHelper.getTypeFromElement(MinechemUtil.getElement(stackInSlot), 1) != null) {
					if (!mergeItemStack(stackInSlot, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				else if (slot < 10) {
					if (!mergeItemStack(stackInSlot, 10, 37, true)) {
						return ItemStack.EMPTY;
					}
				}
				else if (slot > 9) {
					if (!mergeItemStack(stackInSlot, 1, 10, true)) {
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
}
