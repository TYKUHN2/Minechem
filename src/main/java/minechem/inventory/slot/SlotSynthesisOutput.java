package minechem.inventory.slot;

import minechem.block.tile.TileSynthesis;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotSynthesisOutput extends SlotCrafting {

	private final TileSynthesis synthesis;
	private boolean locked = false;

	public SlotSynthesisOutput(TileSynthesis inv, EntityPlayer player, int id, int x, int y) {
		super(player, inv.getFakeCraftingInv(), inv, id, x, y);
		synthesis = inv;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return !locked;//synthesis.canTakeOutputStack(false);
	}

	public SlotSynthesisOutput setLocked(boolean doLock) {
		locked = doLock;
		return this;
	}

}
