package minechem.inventory.slot;

import minechem.block.tile.TileSynthesis;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSynthesisOutput extends Slot {

	//private final TileSynthesis synthesis;
	private boolean locked = false;
	//private final EntityPlayer player;

	public SlotSynthesisOutput(final TileSynthesis inv, final int id, final int x, final int y) {
		super(inv, id, x, y);
		//synthesis = inv;
		//this.player = player;
	}

	@Override
	public boolean isItemValid(final ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
		return !locked;//synthesis.canTakeOutputStack(false);
	}

	public SlotSynthesisOutput setLocked(final boolean doLock) {
		locked = doLock;
		return this;
	}

}
