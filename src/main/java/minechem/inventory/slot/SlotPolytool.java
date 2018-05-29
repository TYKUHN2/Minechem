package minechem.inventory.slot;

import minechem.item.ItemElement;
import minechem.item.polytool.PolytoolHelper;
import minechem.utils.MinechemUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPolytool extends Slot {

	public SlotPolytool(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {

		return itemstack.isEmpty() || (itemstack.getCount() == 64 && (itemstack.getItem() instanceof ItemElement) && PolytoolHelper.getTypeFromElement(MinechemUtil.getElement(itemstack), 1) != null);

	}

}
