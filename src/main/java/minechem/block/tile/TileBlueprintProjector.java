package minechem.block.tile;

import minechem.api.IMinechemBlueprint;
import minechem.block.multiblock.tile.TileFissionCore;
import minechem.block.multiblock.tile.TileFusionCore;
import minechem.block.multiblock.tile.TileGhostBlock;
import minechem.block.multiblock.tile.TileReactorCore;
import minechem.block.multiblock.tile.TileReactorCore.MultiBlockStatusEnum;
import minechem.init.ModBlocks;
import minechem.init.ModBlueprints;
import minechem.init.ModItems;
import minechem.init.ModRegistries;
import minechem.init.ModSounds;
import minechem.utils.BlueprintUtil;
import minechem.utils.LocalPosition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * @author p455w0rd
 *
 */
public class TileBlueprintProjector extends TileMinechemEnergyBase {

	IMinechemBlueprint blueprint, lastBlueprint;
	IBlockState[][][] structure;
	int timer = 0;
	boolean needToBuild = true;

	public TileBlueprintProjector() {
		super(100000);
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
	}

	@Override
	public void update() {
		super.update();
		timer++;
		if (timer > 20) {
			timer = 0;
		}
		else {
			return;
		}
		if (world.isRemote) {
			return;
		}
		if (blueprint != null) {
			if (isComplete()) {
				if (getEnergyStored() >= getEnergyRequired()) {
					projectBlueprint();
					playSound();
				}
			}
			else {
				if (getEnergyStored() >= getEnergyRequired()) {
					projectBlueprint();
					playSound();
					useEnergy(getEnergyRequired());
				}
			}
		}
		if (timer % 5 == 0) {
			destroyProjection();
			markDirty();
			IBlockState iblockstate = getWorld().getBlockState(getPos());
			if (iblockstate != null) {
				getWorld().notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
			}
		}
	}

	private boolean isComplete() {
		return BlueprintUtil.isStructureComplete(getBlueprint(), getFacing(), getWorld(), getPos());
	}

	private void playSound() {
		//if (timer % 5 == 0) {
		if (blueprint != null) {
			getWorld().playSound(null, getPos(), ModSounds.BLUEPRINT_PROJECTOR, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
		//}
	}

	private EnumFacing getFacing() {
		int facing = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
		return EnumFacing.values()[facing].getOpposite();
	}

	private void projectBlueprint() {
		if (world == null) {
			return;
		}

		EnumFacing direction = getFacing();
		LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), direction);
		LocalPosition worldPos = new LocalPosition(blueprint.getManagerPosX(), blueprint.getManagerPosY(), blueprint.getManagerPosZ(), direction);

		position.moveForwards(blueprint.zSize() + 1);
		position.moveLeft(Math.floor(blueprint.xSize() / 2));

		LocalPosition.Pos3 mgrPos = position.getLocalPos(blueprint.getManagerPosX(), blueprint.getManagerPosY(), blueprint.getManagerPosZ());

		int totalIncorrectCount = 0;
		if (!isComplete()) {
			if (!needToBuild) {
				needToBuild = true;
			}
			boolean shouldProjectGhostBlocks = true;
			totalIncorrectCount = blueprint.getTotalSize();
			for (int x = 0; x < blueprint.xSize(); ++x) {
				int verticalIncorrectCount = blueprint.ySize() * blueprint.zSize();
				for (int y = 0; y < blueprint.ySize(); ++y) {
					for (int z = 0; z < blueprint.zSize(); ++z) {
						if (shouldProjectGhostBlocks) {
							MultiBlockStatusEnum multiBlockStatusEnum = projectGhostBlock(x, y, z, position);//isManagerBlock(x, y, z) ? MultiBlockStatusEnum.CORRECT : projectGhostBlock(x, y, z, position);
							if (multiBlockStatusEnum != MultiBlockStatusEnum.CORRECT) {
								continue;
							}
							--verticalIncorrectCount;
							--totalIncorrectCount;
							continue;
						}
						destroyGhostBlock(x, y, z, position);
					}
				}
				if (verticalIncorrectCount == 0) {
					continue;
				}
				shouldProjectGhostBlocks = false;
			}

			if (getWorld().getTileEntity(new BlockPos(mgrPos.x, mgrPos.y, mgrPos.z)) instanceof TileReactorCore) {
				((TileReactorCore) getWorld().getTileEntity(new BlockPos(mgrPos.x, mgrPos.y, mgrPos.z))).unlinkProxies();
			}
			//getWorld().setBlockState(new BlockPos(mgrPos.x, mgrPos.y, mgrPos.z), Blocks.AIR.getDefaultState(), 3);
		}
		if (!(totalIncorrectCount != 0 || isComplete() && getWorld().getTileEntity(new BlockPos(mgrPos.x, mgrPos.y, mgrPos.z)) instanceof TileReactorCore)) {
			buildStructure(position);
		}
	}

	private void buildStructure(LocalPosition position) {
		if (needToBuild) {
			IBlockState[][][] resultStructure = blueprint.getStructure();
			TileEntity managerTileEntity = buildManagerBlock(position);
			if (managerTileEntity != null && managerTileEntity instanceof TileReactorCore) {
				for (int x = 0; x < blueprint.xSize(); ++x) {
					for (int y = 0; y < blueprint.ySize(); ++y) {
						for (int z = 0; z < blueprint.zSize(); ++z) {
							if (isManagerBlock(x, y, z)) {
								continue;
							}
							IBlockState state = resultStructure[y][x][z];

							//setBlock(new BlockPos(x, y, z), position, state);
						}
					}
				}
				((TileReactorCore) managerTileEntity).linkProxies();
			}
			needToBuild = false;
		}
	}

	private void setBlock(BlockPos posIn, LocalPosition position, IBlockState state) {
		if (state.getBlock() == Blocks.AIR) {
			return;
		}
		else {
			LocalPosition.Pos3 worldPos = position.getLocalPos(posIn);
			getWorld().setBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z), state, 3);
		}
	}

	private boolean isManagerBlock(int x, int y, int z) {
		return x == blueprint.getManagerPosX() && y == blueprint.getManagerPosY() && z == blueprint.getManagerPosZ();
	}

	private TileEntity getManager() {
		if (getBlueprint() != null) {
			EnumFacing direction = getFacing();
			LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), direction);
			position.moveForwards(blueprint.zSize() + 1);
			position.moveLeft(Math.floor(blueprint.xSize() / 2));
			LocalPosition.Pos3 worldPos = position.getLocalPos(blueprint.getManagerPosX(), blueprint.getManagerPosY(), blueprint.getManagerPosZ());
			return getWorld().getTileEntity(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
		}
		return null;
	}

	private TileEntity buildManagerBlock(LocalPosition position) {
		LocalPosition.Pos3 worldPos = position.getLocalPos(blueprint.getManagerPosX(), blueprint.getManagerPosY(), blueprint.getManagerPosZ());
		//getWorld().setBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z), ModBlocks.reactor_core.getDefaultState(), 3);
		//if (getWorld().getTileEntity(new BlockPos(worldPos.x, worldPos.y, worldPos.z)) == null) {
		if (getWorld().getBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z)) == ModBlocks.reactor_core) {
			if (getWorld().getTileEntity(new BlockPos(worldPos.x, worldPos.y, worldPos.z)) != null) {
				getWorld().removeTileEntity(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
			}
			if (blueprint == ModBlueprints.fusion) {
				TileFusionCore fusion = new TileFusionCore(position.orientation);
				fusion.setWorld(getWorld());
				fusion.setPos(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
				ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, fusion, ModBlocks.reactor_core, "blockType", "field_145854_h");
				getWorld().addTileEntity(fusion);
			}
			if (blueprint == ModBlueprints.fission) {
				TileFissionCore fission = new TileFissionCore(position.orientation);
				fission.setWorld(getWorld());
				fission.setPos(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
				ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, fission, ModBlocks.reactor_core, "blockType", "field_145854_h");
				getWorld().addTileEntity(fission);
			}
		}
		//}
		return getWorld().getTileEntity(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
	}

	private MultiBlockStatusEnum projectGhostBlock(int x, int y, int z, LocalPosition position) {
		LocalPosition.Pos3 worldPos = position.getLocalPos(x, y, z);
		IBlockState structureState = structure[y][x][z];
		IBlockState state = getWorld().getBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
		Block block = state.getBlock();
		Block structureBlock = structureState.getBlock();
		int blockMetadata = block.getMetaFromState(state);
		int structureBlockMetadata = structureBlock.getMetaFromState(structureState);
		if (block == Blocks.AIR && structureState.getBlock() != Blocks.AIR) {
			createGhostBlock(worldPos.x, worldPos.y, worldPos.z, structureState);
			return MultiBlockStatusEnum.INCORRECT;
		}
		else if (structureBlock == block && structureBlockMetadata == blockMetadata) {
			return MultiBlockStatusEnum.CORRECT;
		}
		return MultiBlockStatusEnum.INCORRECT;
	}

	private void createGhostBlock(int x, int y, int z, IBlockState state) {
		getWorld().setBlockState(new BlockPos(x, y, z), ModBlocks.ghostBlock.getStateFromMeta(0), 3);
		TileEntity tileEntity = getWorld().getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof TileGhostBlock) {
			TileGhostBlock ghostBlock = (TileGhostBlock) tileEntity;
			ghostBlock.setRenderedBlockState(state);
		}
	}

	public void destroyProjection() {
		destroyProjection(true);
	}

	public void destroyProjection(boolean override) {
		if ((getEnergyStored() >= getEnergyRequired() && blueprint != null) && !override) {
			return;
		}
		if (blueprint == null && lastBlueprint != null) {
			structure = lastBlueprint.getStructure();
			LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), getFacing());
			position.moveForwards(lastBlueprint.xSize() + 1);
			position.moveLeft(Math.floor(lastBlueprint.xSize() / 2));
			for (int x = 0; x < lastBlueprint.xSize(); ++x) {
				for (int y = 0; y < lastBlueprint.ySize(); ++y) {
					for (int z = 0; z < lastBlueprint.zSize(); ++z) {
						destroyGhostBlock(x, y, z, position);
					}
				}
			}
			structure = null;
			lastBlueprint = null;
		}
	}

	private void destroyGhostBlock(int x, int y, int z, LocalPosition position) {
		LocalPosition.Pos3 worldPos = position.getLocalPos(x, y, z);
		Block block = getWorld().getBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z)).getBlock();
		if (block == ModBlocks.ghostBlock) {
			getWorld().setBlockToAir(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
		}
	}

	/*
		public int getFacing() {
			return getWorld().getBlockState(pos).getBlock().getMetaFromState(getWorld().getBlockState(pos));
		}
	*/
	public void setBlueprint(IMinechemBlueprint blueprint) {
		destroyProjection();
		if (blueprint != null) {
			this.blueprint = blueprint;
			lastBlueprint = blueprint;
			structure = blueprint.getStructure();
		}
		else {
			this.blueprint = null;
			structure = null;
		}
		markDirty();
	}

	public IMinechemBlueprint takeBlueprint() {
		IMinechemBlueprint blueprint = this.blueprint;
		setBlueprint(null);

		return blueprint;
	}

	public boolean hasBlueprint() {
		return blueprint != null;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack returnStack = super.decrStackSize(slot, amount);
		destroyProjection();
		setBlueprint(null);

		return returnStack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		super.setInventorySlotContents(slot, itemstack);
		if (!itemstack.isEmpty()) {
			IMinechemBlueprint blueprint = BlueprintUtil.getBlueprint(itemstack);
			destroyProjection();
			setBlueprint(blueprint);
		}
	}

	@Override
	public String getName() {
		return "container.blueprintprojector";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		ItemStack blueprintStack = getStackInSlot(0);
		if (!blueprintStack.isEmpty()) {
			nbtTagCompound.setTag("blueprint", blueprintStack.writeToNBT(new NBTTagCompound()));
		}
		nbtTagCompound.setString("lastBlueprint", lastBlueprint == null ? "" : lastBlueprint.getRegistryName().toString());
		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		//isComplete = false;
		NBTTagCompound blueprintNBT = (NBTTagCompound) nbtTagCompound.getTag("blueprint");
		if (blueprintNBT != null) {
			ItemStack blueprintStack = new ItemStack(blueprintNBT);
			IMinechemBlueprint blueprint = BlueprintUtil.getBlueprint(blueprintStack);
			setBlueprint(blueprint);
			inventory.set(0, blueprintStack);
			setInventorySlotContents(0, blueprintStack);
		}
		if (nbtTagCompound.hasKey("lastBlueprint", Constants.NBT.TAG_STRING)) {
			String regName = nbtTagCompound.getString("lastBlueprint");
			if (!regName.isEmpty()) {
				lastBlueprint = ModRegistries.MINECHEM_BLUEPRINTS.getValue(new ResourceLocation(regName));
			}
		}
	}

	public IMinechemBlueprint getBlueprint() {
		return blueprint;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.getItem() == ModItems.blueprint && BlueprintUtil.getBlueprint(itemstack) != null;
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
		return true;
	}

	@Override
	public void openInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public void closeInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inventory.size(); i++) {
			if (!inventory.get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getEnergyRequired() {
		return 100;
	}

}