package minechem.block.multiblock;

import javax.annotation.Nullable;

import minechem.api.ICustomRenderer;
import minechem.block.multiblock.tile.TileGhostBlock;
import minechem.tileentity.multiblock.ghostblock.GhostBlockItem;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGhost extends BlockContainer implements ICustomRenderer {

	public BlockGhost() {
		super(Material.IRON);
		setRegistryName("ghost_block");
		setUnlocalizedName("ghost_block");
		setLightLevel(0.5F);
		setHardness(1000F);
		setResistance(1000F);
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new GhostBlockItem(this).setRegistryName(getRegistryName()));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		if (!world.isRemote) {
			return super.getPickBlock(state, target, world, pos, player);
		}
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileGhostBlock) {
			TileGhostBlock ghostTile = (TileGhostBlock) te;
			return ghostTile.getBlockAsItemStack();
		}
		return super.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
		if (world.isRemote) {
			return true;
		}
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileGhostBlock) {
			TileGhostBlock ghostBlock = (TileGhostBlock) tileEntity;
			ItemStack blockAsStack = ghostBlock.getBlockAsItemStack();
			if (playerIsHoldingItem(player, blockAsStack)) {
				;
				world.setBlockState(pos, ghostBlock.getRenderedBlockState(), 3);
				if (!player.capabilities.isCreativeMode) {
					player.inventory.decrStackSize(player.inventory.currentItem, 1);
				}
				return true;
			}
		}
		return true;
	}

	private boolean playerIsHoldingItem(EntityPlayer entityPlayer, ItemStack itemstack) {
		ItemStack helditem = entityPlayer.inventory.getCurrentItem();
		if (!helditem.isEmpty() && !itemstack.isEmpty()) {
			if (helditem.getItem() == itemstack.getItem()) {
				if (helditem.getItemDamage() == itemstack.getItemDamage()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean bool) {
		return true;
	}

	/**
	 * Returns whether this block is collideable based on the arguments passed in Args: blockMetaData, unknownFlag
	 */

	@Override
	public int damageDropped(IBlockState state) {
		return state.getBlock().getMetaFromState(state);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing facing) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	// XXX: Maybe wrong replacement for getRenderBlockPass()
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int i) {
		return new TileGhostBlock();
	}

	/**
	 * When player places a ghost block delete it
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
		if (entity instanceof EntityPlayer) {
			//world.setBlockToAir(pos);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		for (int i = 0; i < 2; i++) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName(), "inventory"));
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.025F);
		stack = getItemBlockWithNBT(te);
		spawnAsEntity(worldIn, pos, stack);
	}

	private ItemStack getItemBlockWithNBT(@Nullable TileEntity te) {
		ItemStack stack = new ItemStack(this);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		if (te != null) {
			te.writeToNBT(nbttagcompound);
			stack.setTagInfo("BlockEntityTag", nbttagcompound);
		}
		return stack;
	}

	/*
	@SideOnly(Side.CLIENT)
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
	    return EnumBlockRenderType.MODEL;
	}
	*/
}
