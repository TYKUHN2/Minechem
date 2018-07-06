package minechem.network.message;

import io.netty.buffer.ByteBuf;
import minechem.block.tile.TileSynthesis;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author p455w0rd
 *
 */
public class MessageSyncSynthesisMachine implements IMessage, IMessageHandler<MessageSyncSynthesisMachine, IMessage> {

	BlockPos pos;
	NBTTagCompound nbt;

	public MessageSyncSynthesisMachine() {
	}

	public MessageSyncSynthesisMachine(TileSynthesis tile) {
		pos = tile.getPos();
		nbt = tile.writeToNBT(new NBTTagCompound());
	}

	@Override
	public IMessage onMessage(MessageSyncSynthesisMachine message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;
		if (player != null) {
			World world = player.world;
			if (world != null) {
				TileSynthesis newTile = new TileSynthesis();
				newTile.readFromNBT(message.nbt);
				world.setTileEntity(message.pos, newTile);
			}
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		ByteBufUtils.writeTag(buf, nbt);
	}

}
