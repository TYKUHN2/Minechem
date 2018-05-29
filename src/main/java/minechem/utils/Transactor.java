package minechem.utils;

import java.util.ArrayList;

import minechem.inventory.InventoryBounded;
import net.minecraft.item.ItemStack;

public class Transactor {

	InventoryBounded inventory;
	int maxStackSize;

	public Transactor(InventoryBounded var1) {
		maxStackSize = 64;
		inventory = var1;
	}

	public Transactor(InventoryBounded var1, int var2) {
		this(var1);
		maxStackSize = var2;
	}

	public int add(ItemStack var1, boolean var2) {
		int var3 = 0;
		int var4 = var1.getCount();

		for (int var5 = 0; var4 > 0 && var5 < inventory.getSizeInventory(); ++var5) {
			int var6 = putStackInSlot(var1, var4, var5, var2);
			var3 += var6;
			var4 -= var6;
		}

		return var3;
	}

	public ItemStack[] remove(int var1, boolean var2) {
		int var3 = var1;
		ArrayList<ItemStack> var4 = new ArrayList<ItemStack>();

		for (int var5 = 0; var3 > 0 && var5 < inventory.getSizeInventory(); ++var5) {
			ItemStack var6 = ItemStack.EMPTY;
			if (var2) {
				var6 = inventory.decrStackSize(var5, var3);
			}
			else {
				if (!inventory.getStackInSlot(var5).isEmpty()) {
					var6 = inventory.getStackInSlot(var5).copy();
					var6.setCount(Math.min(var1, var6.getCount()));
				}
			}

			if (!var6.isEmpty()) {
				var3 -= var6.getCount();
				var4.add(var6);
			}
		}

		return var4.toArray(new ItemStack[var4.size()]);
	}

	public ItemStack removeItem(boolean var1) {
		for (int var2 = 0; var2 < inventory.getSizeInventory(); ++var2) {
			ItemStack var3 = inventory.getStackInSlot(var2);
			if (!var3.isEmpty()) {
				if (var1) {
					return inventory.decrStackSize(var2, 1);
				}

				ItemStack var4 = var3.copy();
				var4.setCount(1);
				return var4;
			}
		}

		return ItemStack.EMPTY;
	}

	public int putStackInSlot(ItemStack var1, int var2, int var3, boolean var4) {
		ItemStack var5 = inventory.getStackInSlot(var3);
		if (var5.isEmpty()) {
			ItemStack var6 = var1.copy();
			var6.setCount(Math.min(var2, getMaxStackSize(var1)));
			if (var4) {
				inventory.setInventorySlotContents(var3, var6);
			}

			return var6.getCount();
		}
		else {
			return MinechemUtil.stacksAreSameKind(var1, var5) ? appendStackToSlot(var1, var2, var3, var4) : 0;
		}
	}

	public int appendStackToSlot(ItemStack var1, int var2, int var3, boolean var4) {
		ItemStack var5 = inventory.getStackInSlot(var3);
		if (var5.getCount() + var2 > getMaxStackSize(var5)) {
			int var6 = getMaxStackSize(var5) - var5.getCount();
			if (var4) {
				var5.grow(var6);
			}

			return var6;
		}
		else {
			if (var4) {
				var5.grow(var2);
			}

			return var2;
		}
	}

	public int getMaxStackSize(ItemStack var1) {
		return Math.min(var1.getMaxStackSize(), maxStackSize);
	}
}
