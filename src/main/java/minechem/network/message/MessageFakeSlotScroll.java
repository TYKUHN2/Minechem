package minechem.network.message;

import java.util.List;

import io.netty.buffer.ByteBuf;
import minechem.client.gui.GuiSynthesis.ScrollDirection;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.*;

/**
 * @author p455w0rd
 *
 */
public class MessageFakeSlotScroll implements IMessage, IMessageHandler<MessageFakeSlotScroll, IMessage> {

	ScrollDirection direction;
	int slotId;

	public MessageFakeSlotScroll() {
	}

	public MessageFakeSlotScroll(final ScrollDirection direction, final int slotId) {
		this.direction = direction;
		this.slotId = slotId;
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
		direction = ScrollDirection.values()[buf.readInt()];
		slotId = buf.readInt();
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		buf.writeInt(direction.ordinal());
		buf.writeInt(slotId);
	}

	@Override
	public IMessage onMessage(final MessageFakeSlotScroll message, final MessageContext ctx) {
		final EntityPlayerMP player = ctx.getServerHandler().player;
		boolean shrink = false;
		switch (message.direction) {
		case UP:
			shrink = true;
			break;
		default:
		case IDLE:
		case DOWN:
			break;
		}

		final ItemStack mouseStack = player.inventory.getItemStack();
		final List<Slot> slots = player.openContainer.inventorySlots;
		final Slot slot = slots == null || slots.isEmpty() ? null : slots.get(message.slotId);
		final ItemStack slotStack = slot == null ? ItemStack.EMPTY : slot.getStack();
		if (!slotStack.isEmpty() && !mouseStack.isEmpty() && slotStack.isItemEqual(mouseStack)) {
			if (slotStack.getCount() < slotStack.getMaxStackSize()) {
				if (shrink) {
					slotStack.shrink(1);
				}
				else {
					slotStack.grow(1);
				}
				//mouseStack.grow(1);
				//updateHeld(player);
			}
		}
		else if (slotStack.isEmpty() && !mouseStack.isEmpty()) {
			final ItemStack newStack = mouseStack.copy();
			newStack.setCount(1);
			slot.putStack(newStack);
		}
		else if (!slotStack.isEmpty() && mouseStack.isEmpty()) {
			if (slotStack.getCount() < slotStack.getMaxStackSize()) {
				if (shrink) {
					slotStack.shrink(1);
				}
				else {
					slotStack.grow(1);
				}
			}
		}
		return null;
	}

	//private void updateHeld(final EntityPlayerMP p) {
	//	ModNetworking.INSTANCE.sendTo(new MessageSetMouseStack(p.inventory.getItemStack()), p);
	//}

}
