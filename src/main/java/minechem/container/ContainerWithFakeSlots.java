package minechem.container;

import minechem.inventory.slot.SlotFake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ContainerWithFakeSlots extends Container {

	public static int MOUSE_LEFT = 0;

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return false;
	}

	/**
	 * returns a list if itemStacks, for each non-fake slot.
	 */
	@Override
	public NonNullList<ItemStack> getInventory() {
		NonNullList<ItemStack> arraylist = NonNullList.withSize(inventorySlots.size(), ItemStack.EMPTY);

		for (int i = 0; i < inventorySlots.size(); ++i) {
			Slot slot = (inventorySlots.get(i));
			arraylist.set(i, (slot instanceof SlotFake) ? ItemStack.EMPTY : slot.getStack());
		}

		return arraylist;
	}

	@Override
	public ItemStack slotClick(int slotNum, int mouseButton, ClickType clickType, EntityPlayer entityPlayer) {
		Slot slot = null;
		if (slotNum >= 0 && slotNum < inventorySlots.size()) {
			slot = getSlot(slotNum);
		}
		if (slot instanceof SlotFake && clickType != ClickType.CLONE) {
			ItemStack stackOnMouse = entityPlayer.inventory.getItemStack();
			if (!stackOnMouse.isEmpty() && slot.isItemValid(stackOnMouse)) {
				if (mouseButton == MOUSE_LEFT) {
					addStackToSlot(stackOnMouse, slot, stackOnMouse.getCount(), true);
				}
				else {
					addStackToSlot(stackOnMouse, slot, 1);
				}
			}
			else {
				slot.putStack(ItemStack.EMPTY);
			}
			return ItemStack.EMPTY;
		}
		else {
			return super.slotClick(slotNum, mouseButton, clickType, entityPlayer);//superSlotClick(slotNum, mouseButton, clickType, entityPlayer);
			//return superSlotClick(slotNum, mouseButton, clickType, entityPlayer);
		}
	}

	/*
	
	TODO: Check if the ClickTypes are matching
	
	0 = Basic Click  = PICKUP
	1 = Shift Click  = QUICK_MOVE
	2 = Hotbar       = SWAP
	3 = Pick Block   = CLONE
	4 = Drop         = THROW
	5 = ?            = QUICK_CRAFT
	6 = Double Click = PICKUP_ALL
	
	 */

	public ItemStack superSlotClick(int slotNum, int mouseButton, ClickType clickType, EntityPlayer entityPlayer) {
		ItemStack itemstack = ItemStack.EMPTY;
		InventoryPlayer inventoryplayer = entityPlayer.inventory;
		int i1;
		ItemStack itemstack3;

		if (clickType == ClickType.QUICK_CRAFT) {
			int l = dragEvent;
			dragEvent = getDragEvent(mouseButton);

			if ((l != 1 || dragEvent != 2) && l != dragEvent) {
				resetDrag();
			}
			else if (inventoryplayer.getItemStack().isEmpty()) {
				resetDrag();
			}
			else if (dragEvent == 0) {
				dragMode = extractDragMode(mouseButton);

				if (isValidDragMode(dragMode, entityPlayer)) {
					dragEvent = 1;
					dragSlots.clear();
				}
				else {
					resetDrag();
				}
			}
			else if (dragEvent == 1) {
				Slot slot = inventorySlots.get(slotNum);

				if (slot != null && canAddItemToSlot(slot, inventoryplayer.getItemStack(), true) && slot.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().getCount() > dragSlots.size() && canDragIntoSlot(slot)) {
					dragSlots.add(slot);
				}
			}
			else if (dragEvent == 2) {
				if (!dragSlots.isEmpty()) {
					itemstack3 = inventoryplayer.getItemStack().copy();
					i1 = inventoryplayer.getItemStack().getCount();

					for (Slot slot1 : dragSlots) {
						if (slot1 != null && canAddItemToSlot(slot1, inventoryplayer.getItemStack(), true) && slot1.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().getCount() >= dragSlots.size() && canDragIntoSlot(slot1)) {
							ItemStack itemstack1 = itemstack3.copy();
							int j1 = slot1.getHasStack() ? slot1.getStack().getCount() : 0;
							computeStackSize(dragSlots, dragMode, itemstack1, j1);

							if (itemstack1.getCount() > itemstack1.getMaxStackSize()) {
								itemstack1.setCount(itemstack1.getMaxStackSize());
							}

							if (itemstack1.getCount() > slot1.getSlotStackLimit()) {
								itemstack1.setCount(slot1.getSlotStackLimit());
							}

							i1 -= itemstack1.getCount() - j1;
							slot1.putStack(itemstack1);
						}
					}

					itemstack3.setCount(i1);

					if (itemstack3.getCount() <= 0) {
						itemstack3 = ItemStack.EMPTY;
					}

					inventoryplayer.setItemStack(itemstack3);
				}

				resetDrag();
			}
			else {
				resetDrag();
			}
		}
		else if (dragEvent != 0) {
			resetDrag();
		}
		else {
			Slot slot2;
			int l1;
			ItemStack itemstack5;

			if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (mouseButton == 0 || mouseButton == 1)) {
				if (slotNum == -999) {
					if (!inventoryplayer.getItemStack().isEmpty()) {
						if (mouseButton == 0) {
							entityPlayer.dropItem(inventoryplayer.getItemStack(), true);
							inventoryplayer.setItemStack(ItemStack.EMPTY);
						}

						if (mouseButton == 1) {
							entityPlayer.dropItem(inventoryplayer.getItemStack().splitStack(1), true);

							if (inventoryplayer.getItemStack().getCount() == 0) {
								inventoryplayer.setItemStack(ItemStack.EMPTY);
							}
						}
					}
				}
				else if (clickType == ClickType.QUICK_MOVE) {
					if (slotNum < 0) {
						return ItemStack.EMPTY;
					}

					slot2 = inventorySlots.get(slotNum);

					if (slot2 != null && slot2.canTakeStack(entityPlayer)) {
						itemstack3 = transferStackInSlot(entityPlayer, slotNum);

						if (!itemstack3.isEmpty()) {
							Item item = itemstack3.getItem();
							itemstack = itemstack3.copy();

							if (!slot2.getStack().isEmpty() && slot2.getStack().getItem() == item) {
								//TODO make sure this works
								super.slotClick(slotNum, mouseButton, clickType, entityPlayer);
							}
						}
					}
				}
				else {
					if (slotNum < 0) {
						return ItemStack.EMPTY;
					}

					slot2 = inventorySlots.get(slotNum);

					if (slot2 != null) {
						itemstack3 = slot2.getStack();
						ItemStack itemstack4 = inventoryplayer.getItemStack();

						if (!itemstack3.isEmpty()) {
							itemstack = itemstack3.copy();
						}

						if (itemstack3.isEmpty()) {
							if (!itemstack4.isEmpty() && slot2.isItemValid(itemstack4)) {
								l1 = mouseButton == 0 ? itemstack4.getCount() : 1;

								if (l1 > slot2.getSlotStackLimit()) {
									l1 = slot2.getSlotStackLimit();
								}

								if (itemstack4.getCount() >= l1) {
									slot2.putStack(itemstack4.splitStack(l1));
								}

								if (itemstack4.getCount() == 0) {
									inventoryplayer.setItemStack(ItemStack.EMPTY);
								}
							}
						}
						else if (slot2.canTakeStack(entityPlayer)) {
							if (itemstack4.isEmpty()) {
								l1 = mouseButton == 0 ? itemstack3.getCount() : (itemstack3.getCount() + 1) / 2;
								itemstack5 = slot2.decrStackSize(l1);
								inventoryplayer.setItemStack(itemstack5);

								if (itemstack3.getCount() == 0) {
									slot2.putStack(ItemStack.EMPTY);
								}

								slot2.onTake(entityPlayer, inventoryplayer.getItemStack());
							}
							else if (slot2.isItemValid(itemstack4)) {
								if (itemstack3.getItem() == itemstack4.getItem() && itemstack3.getItemDamage() == itemstack4.getItemDamage() && ItemStack.areItemStackTagsEqual(itemstack3, itemstack4)) {
									l1 = mouseButton == 0 ? itemstack4.getCount() : 1;

									if (l1 > slot2.getSlotStackLimit() - itemstack3.getCount()) {
										l1 = slot2.getSlotStackLimit() - itemstack3.getCount();
									}

									if (l1 > itemstack4.getMaxStackSize() - itemstack3.getCount()) {
										l1 = itemstack4.getMaxStackSize() - itemstack3.getCount();
									}

									itemstack4.splitStack(l1);

									if (itemstack4.getCount() == 0) {
										inventoryplayer.setItemStack(ItemStack.EMPTY);
									}

									itemstack3.grow(l1);
								}
								else if (itemstack4.getCount() <= slot2.getSlotStackLimit()) {
									slot2.putStack(itemstack4);
									inventoryplayer.setItemStack(itemstack3);
								}
							}
							else if (itemstack3.getItem() == itemstack4.getItem() && itemstack4.getMaxStackSize() > 1 && (!itemstack3.getHasSubtypes() || itemstack3.getItemDamage() == itemstack4.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack3, itemstack4)) {
								l1 = itemstack3.getCount();

								if (l1 > 0 && l1 + itemstack4.getCount() <= itemstack4.getMaxStackSize()) {
									itemstack4.grow(l1);
									itemstack3 = slot2.decrStackSize(l1);

									if (itemstack3.getCount() == 0) {
										slot2.putStack(ItemStack.EMPTY);
									}

									slot2.onTake(entityPlayer, inventoryplayer.getItemStack());
								}
							}
						}

						slot2.onSlotChanged();
					}
				}
			}
			else if (clickType == ClickType.SWAP && mouseButton >= 0 && mouseButton < 9) {
				slot2 = inventorySlots.get(slotNum);

				if (slot2.canTakeStack(entityPlayer)) {
					itemstack3 = inventoryplayer.getStackInSlot(mouseButton);
					boolean flag = itemstack3.isEmpty() || slot2.inventory == inventoryplayer && slot2.isItemValid(itemstack3);
					l1 = -1;

					if (!flag) {
						l1 = inventoryplayer.getFirstEmptyStack();
						flag |= l1 > -1;
					}

					if (slot2.getHasStack() && flag) {
						itemstack5 = slot2.getStack();
						inventoryplayer.setInventorySlotContents(mouseButton, itemstack5.copy());

						if ((slot2.inventory != inventoryplayer || !slot2.isItemValid(itemstack3)) && !itemstack3.isEmpty()) {
							if (l1 > -1) {
								inventoryplayer.addItemStackToInventory(itemstack3);
								slot2.decrStackSize(itemstack5.getCount());
								slot2.putStack(ItemStack.EMPTY);
								slot2.onTake(entityPlayer, itemstack5);
							}
						}
						else {
							slot2.decrStackSize(itemstack5.getCount());
							slot2.putStack(itemstack3);
							slot2.onTake(entityPlayer, itemstack5);
						}
					}
					else if (!slot2.getHasStack() && !itemstack3.isEmpty() && slot2.isItemValid(itemstack3)) {
						inventoryplayer.setInventorySlotContents(mouseButton, ItemStack.EMPTY);
						slot2.putStack(itemstack3);
					}
				}
			}
			else if (clickType == ClickType.CLONE && entityPlayer.capabilities.isCreativeMode && inventoryplayer.getItemStack().isEmpty() && slotNum >= 0) {
				slot2 = inventorySlots.get(slotNum);

				if (slot2 != null && slot2.getHasStack()) {
					itemstack3 = slot2.getStack().copy();
					itemstack3.setCount(itemstack3.getMaxStackSize());
					inventoryplayer.setItemStack(itemstack3);
				}
			}
			else if (clickType == ClickType.THROW && inventoryplayer.getItemStack().isEmpty() && slotNum >= 0) {
				slot2 = inventorySlots.get(slotNum);

				if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(entityPlayer)) {
					itemstack3 = slot2.decrStackSize(mouseButton == 0 ? 1 : slot2.getStack().getCount());
					slot2.onTake(entityPlayer, itemstack3);
					entityPlayer.dropItem(itemstack3, true);
				}
			}
			else if (clickType == ClickType.PICKUP_ALL && slotNum >= 0) {
				slot2 = inventorySlots.get(slotNum);
				itemstack3 = inventoryplayer.getItemStack();

				if (!itemstack3.isEmpty() && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(entityPlayer))) {
					i1 = mouseButton == 0 ? 0 : inventorySlots.size() - 1;
					l1 = mouseButton == 0 ? 1 : -1;

					for (int i2 = 0; i2 < 2; ++i2) {
						for (int j2 = i1; j2 >= 0 && j2 < inventorySlots.size() && itemstack3.getCount() < itemstack3.getMaxStackSize(); j2 += l1) {
							Slot slot3 = inventorySlots.get(j2);
							if (slot3 instanceof SlotFake) {
								continue;
							}
							if (slot3.getHasStack() && canAddItemToSlot(slot3, itemstack3, true) && slot3.canTakeStack(entityPlayer) && canMergeSlot(itemstack3, slot3) && (i2 != 0 || slot3.getStack().getCount() != slot3.getStack().getMaxStackSize())) {
								int k1 = Math.min(itemstack3.getMaxStackSize() - itemstack3.getCount(), slot3.getStack().getCount());
								ItemStack itemstack2 = slot3.decrStackSize(k1);
								itemstack3.grow(k1);

								if (itemstack2.getCount() <= 0) {
									slot3.putStack(ItemStack.EMPTY);
								}

								slot3.onTake(entityPlayer, itemstack2);
							}
						}
					}
				}

				detectAndSendChanges();
			}
		}

		return itemstack;
	}

	protected void addStackToSlot(ItemStack stackOnMouse, Slot slot, int amount) {
		addStackToSlot(stackOnMouse, slot, amount, false);
	}

	protected void addStackToSlot(ItemStack stackOnMouse, Slot slot, int amount, boolean override) {
		ItemStack stackInSlot = slot.inventory.getStackInSlot(slot.slotNumber);
		if (!stackInSlot.isEmpty() && !override) {
			stackInSlot.setCount(Math.min(stackInSlot.getCount() + amount, slot.inventory.getInventoryStackLimit()));
		}
		else {
			ItemStack copyStack = stackOnMouse.copy();
			copyStack.setCount(amount);
			slot.putStack(copyStack);
		}
	}

}
