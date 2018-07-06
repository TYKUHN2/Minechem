package minechem.network.message;

import io.netty.buffer.ByteBuf;
import minechem.init.ModItems;
import minechem.item.ItemChemistJournal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageActiveItem implements IMessage, IMessageHandler<MessageActiveItem, IMessage> {
	private int itemID, itemDMG, slot;

	public MessageActiveItem() {

	}

	public MessageActiveItem(ItemStack activeStack, EntityPlayer player) {
		itemID = Item.getIdFromItem(activeStack.getItem());
		itemDMG = activeStack.getItemDamage();
		slot = player.inventory.currentItem;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		itemID = buf.readInt();
		itemDMG = buf.readInt();
		slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(itemID);
		buf.writeInt(itemDMG);
		buf.writeInt(slot);
	}

	@Override
	public IMessage onMessage(MessageActiveItem message, MessageContext ctx) {
		ItemStack journal = ctx.getServerHandler().player.inventory.mainInventory.get(message.slot);
		if (!journal.isEmpty() && journal.getItem() instanceof ItemChemistJournal) {
			ItemStack activeStack = new ItemStack(Item.getItemById(message.itemID), 1, message.itemDMG);
			ModItems.journal.setActiveStack(activeStack, journal);
		}
		return null;
	}
}
