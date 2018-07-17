package minechem.block.tile;

import minechem.api.IMinechemBlueprint;
import minechem.init.ModBlocks;
import minechem.init.ModBlueprints;
import minechem.init.ModItems;
import minechem.tileentity.multiblock.MultiBlockStatusEnum;
import minechem.tileentity.multiblock.MultiBlockTileEntity;
import minechem.tileentity.multiblock.fusion.FusionTileEntity;
import minechem.tileentity.multiblock.ghostblock.GhostBlockTileEntity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * @author p455w0rd
 *
 */
public class TileBlueprintProjector extends TileMinechemEnergyBase {

	private static int air;
	IMinechemBlueprint blueprint;
	boolean isComplete = false;
	IBlockState[][][] structure;

	public TileBlueprintProjector() {
		super(1000000);
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
	}

	@Override
	public void update() {
		if (blueprint != null && !isComplete) {
			projectBlueprint();
			playSound();
		}
	}

	private void playSound() {
		ticks++;
		if (ticks >= 20) {
			ticks = 0;
			if (blueprint != null) {
				//getWorld().playSound(null, getPos(), ModSounds.BLUEPRINT_PROJECTOR, SoundCategory.BLOCKS, 0.2F, 1.0F);
			}
		}
	}

	private void projectBlueprint() {

		int facing = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
		EnumFacing direction = EnumFacing.values()[facing].getOpposite();
		LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), direction);
		position.moveForwards(blueprint.zSize() + 1);
		position.moveLeft(Math.floor(blueprint.xSize() / 2));
		boolean shouldProjectGhostBlocks = true;
		int totalIncorrectCount = blueprint.getTotalSize();
		for (int x = 0; x < blueprint.xSize(); ++x) {
			int verticalIncorrectCount = blueprint.ySize() * blueprint.zSize();
			for (int y = 0; y < blueprint.ySize(); ++y) {
				for (int z = 0; z < blueprint.zSize(); ++z) {
					if (shouldProjectGhostBlocks) {
						MultiBlockStatusEnum multiBlockStatusEnum = isManagerBlock(x, y, z) ? MultiBlockStatusEnum.CORRECT : projectGhostBlock(x, y, z, position);
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
		if (!(totalIncorrectCount != 0 || isComplete && getWorld().getTileEntity(new BlockPos(blueprint.getManagerPosX(), blueprint.getManagerPosY(), blueprint.getManagerPosZ())) instanceof MultiBlockTileEntity)) {
			isComplete = true;
			buildStructure(position);
		}
	}

	private void buildStructure(LocalPosition position) {

		IBlockState[][][] resultStructure = blueprint.getStructure();
		TileEntity managerTileEntity = buildManagerBlock(position);
		for (int x = 0; x < blueprint.xSize(); ++x) {
			for (int y = 0; y < blueprint.ySize(); ++y) {
				for (int z = 0; z < blueprint.zSize(); ++z) {
					if (isManagerBlock(x, y, z)) {
						continue;
					}
					IBlockState state = resultStructure[y][x][z];
					setBlock(new BlockPos(x, y, z), position, state, managerTileEntity);
				}
			}
		}

	}

	private boolean isManagerBlock(int x, int y, int z) {
		return x == blueprint.getManagerPosX() && y == blueprint.getManagerPosY() && z == blueprint.getManagerPosZ();
	}

	private TileEntity buildManagerBlock(LocalPosition position) {

		//BlueprintBlock managerBlock = blueprint.getManagerBlock();
		//if (managerBlock != null) {
		LocalPosition.Pos3 worldPos = position.getLocalPos(blueprint.getManagerPosX(), blueprint.getManagerPosY(), blueprint.getManagerPosZ());
		getWorld().setBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z), ModBlocks.reactor.getStateFromMeta(2), 3);
		if (blueprint == ModBlueprints.fusion && getWorld().getTileEntity(new BlockPos(worldPos.x, worldPos.y, worldPos.z)) == null) {
			FusionTileEntity fusion = new FusionTileEntity();
			fusion.setWorld(getWorld());
			fusion.setPos(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
			ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, fusion, ModBlocks.reactor, "blockType", "field_145854_h");
			getWorld().addTileEntity(fusion);
		}
		return getWorld().getTileEntity(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
		//}

		//return null;
	}

	private void setBlock(BlockPos posIn, LocalPosition position, IBlockState state, TileEntity managerTileEntity) {
		LocalPosition.Pos3 worldPos = position.getLocalPos(posIn);
		if (state.getBlock() == Blocks.AIR) {
			return;
		}
		else {
			TileEntity te;
			//BlueprintBlock blueprintBlock = blockLookup.get(structureId);
			//if (blueprintBlock.type == BlueprintBlock.Type.MANAGER) {
			//	return;
			//}
			getWorld().setBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z), state, 3);
			//if (blueprintBlock.type == BlueprintBlock.Type.PROXY && (te = getWorld().getTileEntity(new BlockPos(worldPos.x, worldPos.y, worldPos.z))) instanceof TileEntityProxy) {
			//	TileEntityProxy proxy = (TileEntityProxy) te;
			//}
		}
	}

	private MultiBlockStatusEnum projectGhostBlock(int x, int y, int z, LocalPosition position) {

		LocalPosition.Pos3 worldPos = position.getLocalPos(x, y, z);
		IBlockState structureState = structure[y][x][z];
		IBlockState state = getWorld().getBlockState(new BlockPos(x, y, z));
		Block block = state.getBlock();
		int blockMetadata = block.getMetaFromState(state);

		if (block.isAir(state, getWorld(), new BlockPos(x, y, z))) {
			createGhostBlock(worldPos.x, worldPos.y, worldPos.z, structureState);
			return MultiBlockStatusEnum.INCORRECT;
		}
		else if (structureState == blueprint.getStructure()[y][x][z]) {
			return MultiBlockStatusEnum.CORRECT;
		}

		return MultiBlockStatusEnum.INCORRECT;
	}

	private void createGhostBlock(int x, int y, int z, IBlockState state) {
		getWorld().setBlockState(new BlockPos(x, y, z), ModBlocks.ghostBlock.getStateFromMeta(0), 3);
		TileEntity tileEntity = getWorld().getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof GhostBlockTileEntity) {
			GhostBlockTileEntity ghostBlock = (GhostBlockTileEntity) tileEntity;
			ghostBlock.setRenderedBlockState(state);
			//ghostBlock.setBlueprintAndID(blueprint, blockID);
		}
	}

	public void destroyProjection() {

		if (blueprint == null) {
			return;
		}
		int facing = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
		EnumFacing direction = EnumFacing.values()[facing].getOpposite();
		LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), direction);
		position.moveForwards(blueprint.zSize() + 1);
		position.moveLeft(Math.floor(blueprint.xSize() / 2));
		for (int x = 0; x < blueprint.xSize(); ++x) {
			for (int y = 0; y < blueprint.ySize(); ++y) {
				for (int z = 0; z < blueprint.zSize(); ++z) {
					destroyGhostBlock(x, y, z, position);
				}
			}
		}

	}

	private void destroyGhostBlock(int x, int y, int z, LocalPosition position) {
		LocalPosition.Pos3 worldPos = position.getLocalPos(x, y, z);
		Block block = getWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
		//if (block == ModBlocks.ghostBlock) {
		getWorld().setBlockToAir(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
		//}
	}

	public int getFacing() {
		return getWorld().getBlockState(pos).getBlock().getMetaFromState(getWorld().getBlockState(pos));
	}

	public void setBlueprint(IMinechemBlueprint blueprint) {
		if (blueprint != null) {
			this.blueprint = blueprint;
			structure = blueprint.getStructure();
		}
		else {
			destroyProjection();
			this.blueprint = null;
			structure = null;
			isComplete = false;
		}
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
		setBlueprint(null);
		return super.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		super.setInventorySlotContents(slot, itemstack);
		if (!itemstack.isEmpty()) {
			IMinechemBlueprint blueprint = BlueprintUtil.getBlueprint(itemstack);
			setBlueprint(blueprint);
		}
	}

	@Override
	public String getName() {
		return "container.blueprintProjector";
	}

	// Does inventory has custom name.
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
		ItemStack blueprintStack = inventory.get(0);
		if (!blueprintStack.isEmpty()) {
			NBTTagCompound blueprintNBT = new NBTTagCompound();
			blueprintStack.writeToNBT(blueprintNBT);
			nbtTagCompound.setTag("blueprint", blueprintNBT);
		}
		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);;
		isComplete = false;
		NBTTagCompound blueprintNBT = (NBTTagCompound) nbtTagCompound.getTag("blueprint");
		if (blueprintNBT != null) {
			ItemStack blueprintStack = new ItemStack(blueprintNBT);
			IMinechemBlueprint blueprint = BlueprintUtil.getBlueprint(blueprintStack);
			setBlueprint(blueprint);
			inventory.set(0, blueprintStack);
		}
	}

	public IMinechemBlueprint getBlueprint() {
		return blueprint;
	}

	// Is item valid.
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