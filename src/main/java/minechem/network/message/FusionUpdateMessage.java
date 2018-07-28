package minechem.network.message;

import io.netty.buffer.ByteBuf;
import minechem.api.IMinechemBlueprint;
import minechem.block.multiblock.tile.TileFusionCore;
import minechem.block.multiblock.tile.TileReactorCore;
import minechem.block.tile.TileMinechemEnergyBase.EnergySettable;
import minechem.init.ModRegistries;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FusionUpdateMessage implements IMessage, IMessageHandler<FusionUpdateMessage, IMessage> {

	private BlockPos pos;
	private int energyStored;
	private IMinechemBlueprint blueprint;
	private EnumFacing facing;
	private boolean doLink;

	public FusionUpdateMessage() {

	}

	public FusionUpdateMessage(TileFusionCore tile, boolean doLink) {
		pos = tile.getPos();
		energyStored = tile.getEnergyStored();
		if (tile.getBlueprint() != null) {
			blueprint = tile.getBlueprint();
		}
		if (tile.getStructureFacing() != null) {
			facing = tile.getStructureFacing();
		}
		this.doLink = doLink;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		energyStored = buf.readInt();
		String bp = ByteBufUtils.readUTF8String(buf);
		if (!bp.isEmpty()) {
			blueprint = ModRegistries.MINECHEM_BLUEPRINTS.getValue(new ResourceLocation(bp));
		}
		int facingindex = buf.readInt();
		facing = facingindex == -1 ? null : EnumFacing.values()[facingindex];
		doLink = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(energyStored);
		String bp = "";
		if (blueprint != null && blueprint.getRegistryName() != null) {
			bp = blueprint.getRegistryName().toString();
		}
		ByteBufUtils.writeUTF8String(buf, bp);
		buf.writeInt(facing == null ? -1 : facing.ordinal());
		buf.writeBoolean(doLink);
	}

	@Override
	public IMessage onMessage(FusionUpdateMessage message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().world.getTileEntity(message.pos);
		if (tileEntity != null) {
			TileFusionCore te;
			if (!message.doLink && tileEntity instanceof TileReactorCore) {
				((TileReactorCore) tileEntity).unlinkProxies();
			}
			if (!(tileEntity instanceof TileFusionCore)) {
				te = new TileFusionCore();
				te.setWorld(tileEntity.getWorld());
				te.setPos(message.pos);
				te.setBlockType(tileEntity.getWorld().getBlockState(message.pos).getBlock());
			}
			else {
				te = (TileFusionCore) tileEntity;
			}
			//te.syncEnergyValue(message.energyStored);
			IEnergyStorage energyStorage = te.getCapability(CapabilityEnergy.ENERGY, null);
			if (energyStorage != null) {
				if (energyStorage instanceof EnergySettable) {
					((EnergySettable) te.getCapability(CapabilityEnergy.ENERGY, null)).setEnergy(message.energyStored);
				}
			}
			te.setBlueprint(message.blueprint);
			te.setStructureFacing(message.facing);
			if (message.doLink) {
				te.linkProxies();
			}
			else {
				te.unlinkProxies();
			}
		}
		return null;
	}
}
