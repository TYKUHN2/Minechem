package minechem.block.multiblock.tile;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import minechem.api.IMinechemBlueprint;
import minechem.block.tile.TileEntityProxy;
import minechem.block.tile.TileEntityProxy.EnergySettableMultiBlock;
import minechem.block.tile.TileMinechemEnergyBase;
import minechem.init.ModBlocks;
import minechem.init.ModBlueprints;
import minechem.init.ModConfig;
import minechem.init.ModRegistries;
import minechem.utils.BlueprintUtil;
import minechem.utils.LocalPosition;
import minechem.utils.SafeTimeTracker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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

	public void setBlueprint(IMinechemBlueprint blueprint) {
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

	public void setStructureFacing(EnumFacing structureFacing) {
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
			if (structureFormed && !checkComplete()) {//!areBlocksCorrect()) {
				unlinkProxies();
				structureFormed = false;
			}
			if (!structureFormed && checkComplete()) {//areBlocksCorrect()) {
				linkProxies();
				structureFormed = true;
			}
			if (!structureFormed) {
				scanForValidStructure();
			}
			super.update();
			//markDirty();
			//IBlockState iblockstate = getWorld().getBlockState(getPos());
			//if (iblockstate != null) {
			//	getWorld().notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
			//}
		}
	}

	private void scanForValidStructure() {
		Pair<IMinechemBlueprint, EnumFacing> bp = BlueprintUtil.getBlueprintFromStructure(getWorld(), getPos());
		if (bp != null) {
			if (bp.getLeft() == ModBlueprints.fusion) {
				TileFusionCore fusion = new TileFusionCore(bp.getRight());
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
				TileFissionCore fission = new TileFissionCore(bp.getRight());
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
				IBlockState iblockstate = getWorld().getBlockState(getPos());
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
			//world.setBlockState(getPos(), ModBlocks.reactor_core.getDefaultState(), 3);
			/*
			TileReactorCore core = new TileReactorCore();
			core.setWorld(getWorld());
			core.setPos(getPos());
			core.setBlueprint(null);
			core.setStructureFacing(null);
			ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, core, ModBlocks.reactor_core, "blockType", "field_145854_h");
			getWorld().setTileEntity(getPos(), core);
			//getWorld().addTileEntity(fission);
			core.structureFormed = false;
			*/
		}
	}

	private void unlinkProxy(int x, int y, int z) {
		int worldX = offsetX + x;//getPos().getX() + x;
		int worldY = offsetY + y;//getPos().getY() + y;
		int worldZ = offsetZ + z;//getPos().getZ() + z;
		BlockPos tilePos = new BlockPos(worldX, worldY, worldZ);
		TileEntity tileEntity = world.getTileEntity(tilePos);
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

	private void linkProxy(int x, int y, int z) {
		int worldX = offsetX + x;//getPos().getX() + x;
		int worldY = offsetY + y;//getPos().getY() + y;
		int worldZ = offsetZ + z;//getPos().getZ() + z;
		BlockPos tilePos = new BlockPos(worldX, worldY, worldZ);
		TileEntity tileEntity = world.getTileEntity(tilePos);
		if (tileEntity != null && tileEntity instanceof TileEntityProxy) {
			((TileEntityProxy) tileEntity).setManager(this);
			IEnergyStorage energyStorage = ((TileEntityProxy) tileEntity).getCapability(CapabilityEnergy.ENERGY, null);
			if (energyStorage instanceof EnergySettableMultiBlock) {
				((EnergySettableMultiBlock) energyStorage).setManager(this);
			}
			((TileEntityProxy) tileEntity).markDirty();
			IBlockState iblockstate = getWorld().getBlockState(tilePos);
			if (iblockstate != null) {
				getWorld().notifyBlockUpdate(tilePos, iblockstate, iblockstate, 3);
			}
		}
	}

	private boolean isManager(BlockPos pos) {
		return pos.getX() == blueprint.getManagerPosX() && pos.getY() == blueprint.getManagerPosY() && pos.getZ() == blueprint.getManagerPosZ();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("structureFormed", structureFormed);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		structureFormed = nbt.getBoolean("structureFormed");
	}

	private boolean areBlocksCorrect() {
		if (getBlueprint() != null) {
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
		else {
			List<IMinechemBlueprint> bpList = ModRegistries.MINECHEM_BLUEPRINTS.getValues();
			for (IMinechemBlueprint bp : bpList) {
				for (int y = 0; y < bp.ySize(); y++) {
					for (int x = 0; x < bp.xSize(); x++) {
						for (int z = 0; z < bp.zSize(); z++) {
							MultiBlockStatusEnum multiBlockStatusEnum = checkBlock(x, y, z);
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

	private MultiBlockStatusEnum checkBlock(int x, int y, int z) {
		if (getBlueprint() != null) {
			if (x == blueprint.getManagerPosX() && y == blueprint.getManagerPosY() && z == blueprint.getManagerPosZ()) {
				return MultiBlockStatusEnum.CORRECT;
			}
			LocalPosition.Pos3 worldPos = getLocalPos().getLocalPos(x, y, z);

			int worldX = getPos().getX() + (offsetX + x);
			int worldY = getPos().getY() + (offsetY + y);
			int worldZ = getPos().getZ() + (offsetZ + z);

			IBlockState structureState = structure[y][x][z];
			Block structureBlock = structureState.getBlock();
			int structureBlockMetadata = structureBlock.getMetaFromState(structureState);
			IBlockState state = world.getBlockState(new BlockPos(worldX, worldY, worldZ));
			Block block = state.getBlock();
			int blockMetadata = block.getMetaFromState(state);
			if (block == Blocks.AIR && structureState.getBlock() != Blocks.AIR) {
				return MultiBlockStatusEnum.INCORRECT;
			}
			else if (structureBlock == block && structureBlockMetadata == blockMetadata) {
				return MultiBlockStatusEnum.CORRECT;
			}
		}
		return MultiBlockStatusEnum.INCORRECT;
	}

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
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
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
