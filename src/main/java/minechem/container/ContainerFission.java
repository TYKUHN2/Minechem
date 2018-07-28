package minechem.container;

import minechem.api.IRadiationShield;
import minechem.block.multiblock.tile.TileFissionCore;
import minechem.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFission extends Container implements IRadiationShield {

	protected TileFissionCore fission;
	protected final int kPlayerInventorySlotStart;
	protected final int kPlayerInventorySlotEnd;
	protected final int kDecomposerInventoryEnd;

	public ContainerFission(InventoryPlayer inventoryPlayer, TileFissionCore fission) {
		this.fission = fission;
		kPlayerInventorySlotStart = fission.getSizeInventory();
		kPlayerInventorySlotEnd = kPlayerInventorySlotStart + (9 * 4);
		kDecomposerInventoryEnd = fission.getSizeInventory();

		addSlotToContainer(new Slot(fission, TileFissionCore.kInput[0], 80, 16));
		bindOutputSlot();
		bindPlayerInventory(inventoryPlayer);
	}

	private void bindOutputSlot() {
		int x = 8;
		int y = 62;
		int j = 0;
		addSlotToContainer(new Slot(fission, 2, x + (4 * 18), y));
	}

	private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return fission.isUsableByPlayer(entityPlayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
		Slot slotObject = inventorySlots.get(slot);
		ItemStack stack = ItemStack.EMPTY;

		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if (slot < 2) {
				if (!mergeItemStack(stackInSlot, kPlayerInventorySlotStart, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			else {
				if (stackInSlot.getItem() == ModItems.element && stackInSlot.getItemDamage() > 0) {
					if (!mergeItemStack(stackInSlot, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot < 29 && stackInSlot.getCount() == stack.getCount()) {
					if (!mergeItemStack(stackInSlot, 29, 38, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot > 28 && stackInSlot.getCount() == stack.getCount()) {
					if (!mergeItemStack(stackInSlot, 2, 29, false)) {
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

}
