package minechem.block.tile;

import javax.annotation.Nullable;

import minechem.radiation.RadiationEnum;
import minechem.utils.RadiationUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileRadioactiveFluid extends TileEntity {

	public RadiationUtil info;

	public TileRadioactiveFluid() {
		this(null);
	}

	public TileRadioactiveFluid(RadiationUtil info) {
		this.info = info;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.getBoolean("isNull")) {
			info = null;
		}
		else {
			info = new RadiationUtil(new ItemStack(tag.getCompoundTag("item")), tag.getLong("decayStart"), tag.getLong("lastUpdate"), tag.getInteger("dimensionID"), RadiationEnum.values()[tag.getInteger("radioactivity")]);
			info.radiationDamage = tag.getInteger("radiationDamage");
		}
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		super.deserializeNBT(tag);
		tag.setBoolean("isNull", info == null);

		if (info != null) {
			tag.setLong("lastUpdate", info.lastDecayUpdate);
			tag.setLong("decayStart", info.decayStarted);
			tag.setInteger("dimensionID", info.dimensionID);
			tag.setInteger("radiationDamage", info.radiationDamage);
			tag.setInteger("radioactivity", info.radioactivity.ordinal());
			NBTTagCompound item = new NBTTagCompound();
			info.itemstack.writeToNBT(item);
			tag.setTag("item", item);
		}
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		return new SPacketUpdateTileEntity(getPos(), 0, tagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
}
