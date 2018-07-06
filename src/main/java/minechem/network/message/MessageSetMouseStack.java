package minechem.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author p455w0rd
 *
 */
public class MessageSetMouseStack implements IMessage, IMessageHandler<MessageSetMouseStack, IMessage> {

	ItemStack newStack;

	public MessageSetMouseStack() {
	}

	public MessageSetMouseStack(ItemStack newStack) {
		this.newStack = newStack.copy();
	}

	@Override
	public IMessage onMessage(MessageSetMouseStack message, MessageContext ctx) {
		Minecraft.getMinecraft().player.inventory.setItemStack(message.newStack);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		newStack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, newStack);
	}

}
