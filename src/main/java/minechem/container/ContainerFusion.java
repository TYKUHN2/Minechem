package minechem.container;

import minechem.api.IRadiationShield;
import minechem.block.multiblock.tile.TileFusionCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFusion extends Container implements IRadiationShield {

	TileFusionCore fusion;
	InventoryPlayer inventoryPlayer;

	public ContainerFusion(InventoryPlayer inventoryPlayer, TileFusionCore fusion) {
		this.inventoryPlayer = inventoryPlayer;
		this.fusion = fusion;

		addSlotToContainer(new Slot(fusion, TileFusionCore.inputLeft, 22, 46));
		addSlotToContainer(new Slot(fusion, TileFusionCore.inputRight, 138, 46));
		addSlotToContainer(new Slot(fusion, TileFusionCore.output, 80, 46));

		bindPlayerInventory(inventoryPlayer);
	}

	private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 145));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return fusion.isUsableByPlayer(var1);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
		Slot slotObject = inventorySlots.get(slot);
		ItemStack stack = ItemStack.EMPTY;

		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			if (slot < fusion.getSizeInventory()) {
				if (!mergeItemStack(stackInSlot, fusion.getSizeInventory(), inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			else {
				if (fusion.isItemValidForSlot(1, stackInSlot)) {
					if (!mergeItemStack(stackInSlot, TileFusionCore.inputLeft, TileFusionCore.inputRight + 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot < 30 && stackInSlot.getCount() == stack.getCount()) {
					if (!mergeItemStack(stackInSlot, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot > 29 && stackInSlot.getCount() == stack.getCount()) {
					if (!mergeItemStack(stackInSlot, 3, 30, false)) {
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
		return 1.0F;
	}

}
