package minechem.tileentity.multiblock.ghostblock;

import javax.annotation.Nullable;

import minechem.block.tile.TileMinechemBase;
import minechem.init.ModConfig;
import minechem.init.ModLogger;
import minechem.init.ModNetworking;
import minechem.item.blueprint.BlueprintBlock;
import minechem.item.blueprint.MinechemBlueprint;
import minechem.network.message.GhostBlockMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GhostBlockTileEntity extends TileMinechemBase {

	private MinechemBlueprint blueprint;
	private int blockID;

	public void setBlueprintAndID(MinechemBlueprint blueprint, int blockID) {
		setBlueprint(blueprint);
		setBlockID(blockID);

		BlueprintBlock bp = blueprint.getBlockLookup().get(this.blockID);

		world.setBlockState(pos, bp.block.getStateFromMeta(bp.metadata), 3);
		if (world != null && !world.isRemote) {
			GhostBlockMessage message = new GhostBlockMessage(this);
			ModNetworking.INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), ModConfig.UpdateRadius));
		}
	}

	public void setBlueprint(MinechemBlueprint blueprint) {
		this.blueprint = blueprint;
	}

	public MinechemBlueprint getBlueprint() {
		return blueprint;
	}

	public void setBlockID(int blockID) {
		this.blockID = blockID;
	}

	public int getBlockID() {
		return blockID;
	}

	public ItemStack getBlockAsItemStack() {
		try {
			BlueprintBlock blueprintBlock = blueprint.getBlockLookup().get(blockID);
			if (blueprintBlock != null) {
				return new ItemStack(blueprintBlock.block, 1, blueprintBlock.metadata);
			}
		}
		catch (Exception e) {
			ModLogger.debug("Block generated an exception at: " + pos.toString());
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		if (blueprint != null) {
			nbtTagCompound.setInteger("blueprintID", blueprint.id);
		}

		nbtTagCompound.setInteger("blockID", blockID);
		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		blockID = nbtTagCompound.getInteger("blockID");
		int blueprintID = nbtTagCompound.getInteger("blueprintID");
		blueprint = MinechemBlueprint.blueprints.get(blueprintID);
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int i) {
		return ItemStack.EMPTY;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
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
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer entityplayer) {

	}

	@Override
	public void closeInventory(EntityPlayer entityplayer) {

	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}
