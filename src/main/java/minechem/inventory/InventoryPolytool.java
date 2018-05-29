package minechem.inventory;

import javax.annotation.Nullable;

import minechem.init.ModNetworking;
import minechem.item.ItemElement;
import minechem.item.ItemPolytool;
import minechem.item.polytool.PolytoolHelper;
import minechem.item.polytool.PolytoolUpgradeType;
import minechem.network.message.PolytoolUpdateMessage;
import minechem.utils.MinechemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryPolytool implements IInventory {
	public ItemStack item = ItemStack.EMPTY;
	public EntityPlayer player;

	public InventoryPolytool(ItemStack currentItem, EntityPlayer player) {
		this.player = player;
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return item;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {

		ItemStack itemstack = getStackInSlot(i);

		if (!itemstack.isEmpty()) {
			if (itemstack.getCount() <= j) {
				setInventorySlotContents(i, ItemStack.EMPTY);
			}
			else {
				itemstack = itemstack.splitStack(j);
			}
		}
		return itemstack;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int i) {
		return item;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		item = itemstack;

		if (!itemstack.isEmpty() && itemstack.getCount() == 64 && (itemstack.getItem() instanceof ItemElement) && PolytoolHelper.getTypeFromElement(MinechemUtil.getElement(itemstack), 1) != null && ItemPolytool.validAlloyInfusion(player.getActiveItemStack(), itemstack)) {
			item = ItemStack.EMPTY;
			PolytoolUpgradeType upgrade = PolytoolHelper.getTypeFromElement(MinechemUtil.getElement(itemstack), 1);
			ItemPolytool.addTypeToNBT(player.inventory.getCurrentItem(), upgrade);

			if (!player.world.isRemote) {
				PolytoolUpdateMessage message = new PolytoolUpdateMessage(upgrade);
				ModNetworking.INSTANCE.sendTo(message, (EntityPlayerMP) player);
			}
		}

	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public void closeInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {

		return itemstack.isEmpty() || itemstack.getCount() == 64 && (itemstack.getItem() instanceof ItemElement) && PolytoolHelper.getTypeFromElement(MinechemUtil.getElement(itemstack), 1) != null;
	}

	@Override
	public int getField(int i) {
		return 0;
	}

	@Override
	public void setField(int i, int i1) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public String getName() {
		return "Polytool Inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("Polytool Inventory");
	}

	@Override
	public boolean isEmpty() {
		return item.isEmpty();
	}

}
