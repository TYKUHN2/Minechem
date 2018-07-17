package minechem.tileentity.multiblock.ghostblock;

import javax.annotation.Nullable;

import minechem.api.ICustomRenderer;
import minechem.init.ModBlocks;
import minechem.tileentity.multiblock.fusion.FusionBlock.FusionBlockType;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GhostBlock extends BlockContainer implements ICustomRenderer {

	static PropertyEnum<FusionBlockType> TYPE = PropertyEnum.<FusionBlockType>create("type", FusionBlockType.class);

	public GhostBlock() {
		super(Material.IRON);
		setRegistryName("ghost_block");
		setUnlocalizedName("ghost_block");
		setLightLevel(0.5F);
		setHardness(1000F);
		setResistance(1000F);
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new GhostBlockItem(this).setRegistryName(getRegistryName()));
		setDefaultState(blockState.getBaseState().withProperty(TYPE, FusionBlockType.FUSION));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				TYPE
		});
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta > 2) {
			meta = 0;
		}
		return blockState.getBaseState().withProperty(TYPE, FusionBlockType.values()[meta]);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
		if (world.isRemote) {
			return true;
		}
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof GhostBlockTileEntity) {
			GhostBlockTileEntity ghostBlock = (GhostBlockTileEntity) tileEntity;
			ItemStack blockAsStack = ghostBlock.getBlockAsItemStack();
			if (playerIsHoldingItem(player, blockAsStack)) {
				IBlockState newState = ModBlocks.reactor.getStateFromMeta(blockAsStack.getItemDamage());
				world.setBlockState(pos, newState, 3);
				if (!player.capabilities.isCreativeMode) {
					player.inventory.decrStackSize(player.inventory.currentItem, 1);
				}
				return true;
			}
		}
		return false;
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
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been cleared to be reused)
	 */
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D);
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

	// XXX: Maybe wrong replacement for getRenderBlockPass()
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int i) {
		return new GhostBlockTileEntity();
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
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName(), "type=" + FusionBlockType.values()[i].getName()));
		}
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
