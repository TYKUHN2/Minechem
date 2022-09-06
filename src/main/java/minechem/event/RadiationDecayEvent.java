package minechem.event;

import minechem.init.ModItems;
import minechem.item.ItemElement;
import minechem.utils.MinechemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RadiationDecayEvent extends Event {

	private final ItemStack before;
	private final ItemStack after;
	private final int damage;
	private final long time;
	private final IInventory inventory;
	private final EntityPlayer player;

	public RadiationDecayEvent(IInventory inventory, int damage, long time, ItemStack before, ItemStack after, EntityPlayer player) {
		this.inventory = inventory;
		this.after = after;
		this.before = before;
		this.damage = damage;
		this.time = time;
		this.player = player;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public IInventory getInventory() {
		return inventory;
	}

	public ItemStack getBefore() {
		return before;
	}

	public ItemStack getAfter() {
		return after;
	}

	public int getDamage() {
		return damage;
	}

	public long getTime() {
		return time;
	}

	public String getLongName(ItemStack stack) {
		Item item = stack.getItem();
		if (item == ModItems.element) {
			return ItemElement.getLongName(stack);
		}
		else if (item == ModItems.molecule) {
			return MinechemUtil.getLocalString(MinechemUtil.getMolecule(stack).getUnlocalizedName(), true);
		}
		return "null";
	}

}
