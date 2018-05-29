package minechem.inventory.slot;

import minechem.init.ModItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFakeChemical extends SlotFake
{

    public SlotFakeChemical(IInventory iInventory, int id, int x, int y)
    {
        super(iInventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return itemStack.getItem() == ModItems.element || itemStack.getItem() == ModItems.molecule;
    }

}
