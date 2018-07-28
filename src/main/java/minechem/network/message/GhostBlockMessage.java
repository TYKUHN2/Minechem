package minechem.network.message;

import io.netty.buffer.ByteBuf;
import minechem.block.multiblock.tile.TileGhostBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GhostBlockMessage implements IMessage, IMessageHandler<GhostBlockMessage, IMessage> {
	private int posX, posY, posZ;

	public GhostBlockMessage() {

	}

	public GhostBlockMessage(TileGhostBlock tile) {
		posX = tile.getPos().getX();
		posY = tile.getPos().getY();
		posZ = tile.getPos().getZ();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		posX = buf.readInt();
		posY = buf.readInt();
		posZ = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(posX);
		buf.writeInt(posY);
		buf.writeInt(posZ);
	}

	@Override
	public IMessage onMessage(GhostBlockMessage message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().world.getTileEntity(new BlockPos(message.posX, message.posY, message.posZ));
		if (tileEntity instanceof TileGhostBlock) {
			// ((GhostBlockTileEntity) tileEntity).setBlueprintAndID(MinechemBlueprint.blueprints.get(message.blueprintID), message.ghostblockID);
		}
		return null;
	}
}
