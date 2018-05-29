package minechem.container;

import java.util.ArrayList;
import java.util.List;

import minechem.api.INoDecay;
import minechem.block.tile.TileLeadedChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerLeadedChest extends Container implements INoDecay {

	protected TileLeadedChest leadedchest;

	public ContainerLeadedChest(InventoryPlayer inventoryPlayer, TileLeadedChest leadedChest) {
		leadedchest = leadedChest;
		leadedChest.openInventory(inventoryPlayer.player);
		bindOutputSlots();
		bindPlayerInventory(inventoryPlayer);
	}

	private void bindOutputSlots() {
		int x = 8;
		int y = 18;
		int j = 0;
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(leadedchest, i, x + (j * 18), y));
			j++;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return leadedchest.isUsableByPlayer(entityplayer);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		leadedchest.closeInventory(playerIn);
	}

	private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int inventoryY = 50;
		int hotBarY = 108;
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
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slotObject = inventorySlots.get(slot);

		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			if (slot < 9) {
				if (!mergeItemStack(stackInSlot, 9, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			else if (!mergeItemStack(stackInSlot, 0, 9, false)) {
				return ItemStack.EMPTY;
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
			slotObject.onTake(player, stackInSlot);

		}
		return stack;
	}

	@Override
	public List<ItemStack> getStorageInventory() {
		List<ItemStack> storageInventory = new ArrayList<ItemStack>();
		for (int slot = 0; slot < 9; slot++) {
			ItemStack stack = getSlot(slot).getStack();
			if (!stack.isEmpty()) {
				storageInventory.add(stack);
			}
		}
		return storageInventory;
	}

	@Override
	public List<ItemStack> getPlayerInventory() {
		List<ItemStack> playerInventory = new ArrayList<ItemStack>();
		for (int slot = 9; slot < inventorySlots.size(); slot++) {
			ItemStack stack = getSlot(slot).getStack();
			if (!stack.isEmpty()) {
				playerInventory.add(stack);
			}
		}
		return playerInventory;
	}
}
