package minechem.block.multiblock.tile;

import org.apache.commons.lang3.tuple.Pair;

import minechem.api.IMinechemBlueprint;
import minechem.block.tile.TileEntityProxy;
import minechem.block.tile.TileEntityProxy.EnergySettableMultiBlock;
import minechem.block.tile.TileMinechemEnergyBase;
import minechem.init.*;
import minechem.utils.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileReactorCore extends TileMinechemEnergyBase {

	int offsetX;
	int offsetY;
	int offsetZ;

	public IMinechemBlueprint blueprint;
	public IBlockState[][][] structure;
	protected boolean structureFormed;
	protected SafeTimeTracker tracker = new SafeTimeTracker();
	protected EnumFacing structureFacing;

	public TileReactorCore() {
		super(ModConfig.maxFusionStorage);
	}

	public LocalPosition getLocalPos() {
		return new LocalPosition(getPos().getX(), getPos().getY(), getPos().getZ(), structureFacing);
	}

	public void setBlueprint(final IMinechemBlueprint blueprint) {
		this.blueprint = blueprint;
		offsetX = 0;
		offsetY = 0;
		offsetZ = 0;
		if (blueprint != null) {
			structure = blueprint.getStructure();
			offsetX = pos.getX() - blueprint.getManagerPosX();
			offsetY = pos.getY() - blueprint.getManagerPosY();
			offsetZ = pos.getZ() - blueprint.getManagerPosZ();
		}
	}

	public IMinechemBlueprint getBlueprint() {
		return blueprint;
	}

	public void setStructureFacing(final EnumFacing structureFacing) {
		this.structureFacing = structureFacing;
	}

	public EnumFacing getStructureFacing() {
		return structureFacing;
	}

	private boolean checkComplete() {
		return BlueprintUtil.isStructureComplete(getBlueprint(), getStructureFacing(), getWorld(), getPos());
	}

	@Override
	public void update() {
		if (getWorld() == null || getWorld().isRemote) {
			return;
		}
		if (tracker.markTimeIfDelay(world, 40)) {
			if (structureFormed && !checkComplete()) {
				unlinkProxies();
				structureFormed = false;
			}
			if (!structureFormed && checkComplete()) {
				linkProxies();
				structureFormed = true;
			}
			if (!structureFormed) {
				scanForValidStructure();
			}
			super.update();
		}
	}

	private void scanForValidStructure() {
		final Pair<IMinechemBlueprint, EnumFacing> bp = BlueprintUtil.getBlueprintFromStructure(getWorld(), getPos());
		if (bp != null) {
			if (bp.getLeft() == ModBlueprints.fusion) {
				final TileFusionCore fusion = new TileFusionCore(bp.getRight());
				fusion.setWorld(getWorld());
				fusion.setPos(getPos());
				fusion.setBlockType(ModBlocks.reactor_core);
				//getWorld().removeTileEntity(getPos());
				getWorld().setTileEntity(getPos(), fusion);
				//getWorld().addTileEntity(fusion);
				fusion.setBlueprint(bp.getLeft());
				fusion.setStructureFacing(bp.getRight());
				fusion.structureFormed = true;
				fusion.linkProxies();
			}
			if (bp.getLeft() == ModBlueprints.fission) {
				final TileFissionCore fission = new TileFissionCore(bp.getRight());
				fission.setBlockType(ModBlocks.reactor_core);
				//getWorld().removeTileEntity(getPos());
				getWorld().setTileEntity(getPos(), fission);
				//getWorld().addTileEntity(fission);
				fission.setBlueprint(bp.getLeft());
				fission.setStructureFacing(bp.getRight());
				fission.structureFormed = true;
				fission.linkProxies();
			}
			if (!getWorld().isRemote) {
				markDirty();
				final IBlockState iblockstate = getWorld().getBlockState(getPos());
				if (iblockstate != null) {
					getWorld().notifyBlockUpdate(getPos(), iblockstate, iblockstate, 3);
				}
			}
		}
	}

	public void unlinkProxies() {
		if (getBlueprint() != null) {
			for (int y = 0; y < blueprint.ySize(); y++) {
				for (int x = 0; x < blueprint.xSize(); x++) {
					for (int z = 0; z < blueprint.zSize(); z++) {
						unlinkProxy(x, y, z);
					}
				}
			}
		}
	}

	private void unlinkProxy(final int x, final int y, final int z) {
		final int worldX = offsetX + x;
		final int worldY = offsetY + y;
		final int worldZ = offsetZ + z;
		final BlockPos tilePos = new BlockPos(worldX, worldY, worldZ);
		final TileEntity tileEntity = world.getTileEntity(tilePos);
		if (tileEntity != null && tileEntity instanceof TileEntityProxy) {
			if (((TileEntityProxy) tileEntity).getManager() != null) {
				world.setTileEntity(tilePos, new TileEntityProxy());
			}
		}
	}

	public void linkProxies() {
		if (getBlueprint() != null) {
			for (int y = 0; y < blueprint.ySize(); y++) {
				for (int x = 0; x < blueprint.xSize(); x++) {
					for (int z = 0; z < blueprint.zSize(); z++) {
						linkProxy(x, y, z);
					}
				}
			}
		}
	}

	private void linkProxy(final int x, final int y, final int z) {
		final int worldX = offsetX + x;
		final int worldY = offsetY + y;
		final int worldZ = offsetZ + z;
		final BlockPos tilePos = new BlockPos(worldX, worldY, worldZ);
		final TileEntity tileEntity = world.getTileEntity(tilePos);
		if (tileEntity != null && tileEntity instanceof TileEntityProxy) {
			((TileEntityProxy) tileEntity).setManager(this);
			final IEnergyStorage energyStorage = ((TileEntityProxy) tileEntity).getCapability(CapabilityEnergy.ENERGY, null);
			if (energyStorage instanceof EnergySettableMultiBlock) {
				((EnergySettableMultiBlock) energyStorage).setManager(this);
			}
			((TileEntityProxy) tileEntity).markDirty();
			final IBlockState iblockstate = getWorld().getBlockState(tilePos);
			if (iblockstate != null) {
				getWorld().notifyBlockUpdate(tilePos, iblockstate, iblockstate, 3);
			}
		}
	}

	/*private boolean isManager(final BlockPos pos) {
		return pos.getX() == blueprint.getManagerPosX() && pos.getY() == blueprint.getManagerPosY() && pos.getZ() == blueprint.getManagerPosZ();
	}*/

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("structureFormed", structureFormed);
		return nbt;
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		structureFormed = nbt.getBoolean("structureFormed");
	}

	/*private boolean areBlocksCorrect() {
		if (getBlueprint() != null) {
			for (int y = 0; y < blueprint.ySize(); y++) {
				for (int x = 0; x < blueprint.xSize(); x++) {
					for (int z = 0; z < blueprint.zSize(); z++) {
						final MultiBlockStatusEnum multiBlockStatusEnum = checkBlock(x, y, z);
						if (multiBlockStatusEnum == MultiBlockStatusEnum.INCORRECT) {
							return false;
						}

					}
				}
			}
			return true;
		}
		else {
			final List<IMinechemBlueprint> bpList = ModRegistries.MINECHEM_BLUEPRINTS.getValues();
			for (final IMinechemBlueprint bp : bpList) {
				for (int y = 0; y < bp.ySize(); y++) {
					for (int x = 0; x < bp.xSize(); x++) {
						for (int z = 0; z < bp.zSize(); z++) {
							final MultiBlockStatusEnum multiBlockStatusEnum = checkBlock(x, y, z);
							if (multiBlockStatusEnum == MultiBlockStatusEnum.INCORRECT) {
								return false;
							}
						}
					}
				}
			}
			return true;
		}
	}

	private MultiBlockStatusEnum checkBlock(final int x, final int y, final int z) {
		if (getBlueprint() != null) {
			if (x == blueprint.getManagerPosX() && y == blueprint.getManagerPosY() && z == blueprint.getManagerPosZ()) {
				return MultiBlockStatusEnum.CORRECT;
			}
			final LocalPosition.Pos3 worldPos = getLocalPos().getLocalPos(x, y, z);

			final int worldX = getPos().getX() + offsetX + x;
			final int worldY = getPos().getY() + offsetY + y;
			final int worldZ = getPos().getZ() + offsetZ + z;

			final IBlockState structureState = structure[y][x][z];
			final Block structureBlock = structureState.getBlock();
			final int structureBlockMetadata = structureBlock.getMetaFromState(structureState);
			final IBlockState state = world.getBlockState(new BlockPos(worldX, worldY, worldZ));
			final Block block = state.getBlock();
			final int blockMetadata = block.getMetaFromState(state);
			if (block == Blocks.AIR && structureState.getBlock() != Blocks.AIR) {
				return MultiBlockStatusEnum.INCORRECT;
			}
			else if (structureBlock == block && structureBlockMetadata == blockMetadata) {
				return MultiBlockStatusEnum.CORRECT;
			}
		}
		return MultiBlockStatusEnum.INCORRECT;
	}*/

	public static enum MultiBlockStatusEnum {
			CORRECT, INCORRECT
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(final EntityPlayer player) {
	}

	@Override
	public void closeInventory(final EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(final int index, final ItemStack stack) {
		return false;
	}

	@Override
	public int getField(final int id) {
		return 0;
	}

	@Override
	public void setField(final int id, final int value) {
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
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getEnergyRequired() {
		return 0;
	}

}
