package minechem.tileentity.multiblock;

import minechem.block.tile.TileEntityProxy;
import minechem.block.tile.TileMinechemEnergyBase;
import minechem.item.blueprint.MinechemBlueprint;
import minechem.utils.SafeTimeTracker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class MultiBlockTileEntity extends TileMinechemEnergyBase {

	public MultiBlockTileEntity(int maxEnergy) {
		super(maxEnergy);
	}

	int offsetX;
	int offsetY;
	int offsetZ;

	public MinechemBlueprint blueprint;
	IBlockState[][][] structure;
	protected boolean completeStructure;
	SafeTimeTracker tracker = new SafeTimeTracker();

	public void setBlueprint(MinechemBlueprint blueprint) {
		this.blueprint = blueprint;
		structure = blueprint.getStructure();
		offsetX = pos.getX() - blueprint.getManagerPosX();
		offsetY = pos.getY() - blueprint.getManagerPosY();
		offsetZ = pos.getZ() - blueprint.getManagerPosZ();
	}

	@Override
	public void update() {
		if (tracker.markTimeIfDelay(world, 40)) {
			if (completeStructure && !areBlocksCorrect()) {
				completeStructure = false;
				unlinkProxies();
			}
			if (!completeStructure && areBlocksCorrect()) {
				completeStructure = true;
				linkProxies();

			}
		}
	}

	private void unlinkProxies() {
		for (int y = 0; y < blueprint.ySize(); y++) {
			for (int x = 0; x < blueprint.xSize(); x++) {
				for (int z = 0; z < blueprint.zSize(); z++) {
					unlinkProxy(x, y, z);
				}
			}
		}
	}

	private void unlinkProxy(int x, int y, int z) {
		int worldX = pos.getX() + offsetX + x;
		int worldY = pos.getY() + offsetY + y;
		int worldZ = pos.getZ() + offsetZ + z;
		TileEntity tileEntity = world.getTileEntity(new BlockPos(worldX, worldY, worldZ));
		if (tileEntity != null && tileEntity instanceof TileEntityProxy) {
			((TileEntityProxy) tileEntity).setManager(null);
		}
	}

	private void linkProxies() {
		for (int y = 0; y < blueprint.ySize(); y++) {
			for (int x = 0; x < blueprint.xSize(); x++) {
				for (int z = 0; z < blueprint.zSize(); z++) {
					linkProxy(x, y, z);
				}
			}
		}
	}

	private void linkProxy(int x, int y, int z) {
		int worldX = pos.getX() + offsetX + x;
		int worldY = pos.getY() + offsetY + y;
		int worldZ = pos.getZ() + offsetZ + z;
		/*
		Map<Integer, BlueprintBlock> lut = blueprint.getBlockLookup();
		TileEntity tileEntity = world.getTileEntity(new BlockPos(worldX, worldY, worldZ));
		if (tileEntity != null && tileEntity instanceof TileEntityProxy) {
			((TileEntityProxy) tileEntity).setManager(this);
		}
		*/
	}

	private boolean areBlocksCorrect() {
		for (int y = 0; y < blueprint.ySize(); y++) {
			for (int x = 0; x < blueprint.xSize(); x++) {
				for (int z = 0; z < blueprint.zSize(); z++) {
					MultiBlockStatusEnum multiBlockStatusEnum = checkBlock(x, y, z);
					if (multiBlockStatusEnum == MultiBlockStatusEnum.INCORRECT) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private MultiBlockStatusEnum checkBlock(int x, int y, int z) {
		return MultiBlockStatusEnum.INCORRECT;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setBoolean("completeStructure", completeStructure);

		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		completeStructure = false;
	}
}
