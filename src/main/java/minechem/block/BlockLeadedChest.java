package minechem.block;

import java.util.ArrayList;

import minechem.Minechem;
import minechem.block.tile.TileLeadedChest;
import minechem.client.render.RenderLeadedChest;
import minechem.client.render.RenderLeadedChest.ItemRenderLeadedChest;
import minechem.init.ModCreativeTab;
import minechem.init.ModGlobals;
import minechem.init.ModGlobals.Textures;
import minechem.init.ModRendering;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLeadedChest extends BlockSimpleContainer {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockLeadedChest() {
		super(Material.WOOD);
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setHardness(2.0F);
		setResistance(5.0F);
		setUnlocalizedName("leaded_chest");
		this.setRegistryName(ModGlobals.ID + ":leaded_chest");
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		//ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderLeadedChest());
		//ModelRegistryHelper.setParticleTexture(this, Textures.Sprite.MICROSCOPE);
		//ClientRegistry.bindTileEntitySpecialRenderer(TileLeadedChest.class, new RenderLeadedChest());
		//ModelLoader.setCustomStateMapper(this, blockIn -> Collections.emptyMap());
		ModRendering.setBlockRendering(this, new RenderLeadedChest(), TileLeadedChest.class, new ItemRenderLeadedChest(), Textures.Sprite.LEADED_CHEST);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			IBlockState iblockstate = worldIn.getBlockState(pos.north());
			IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
			IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
			IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
			EnumFacing enumfacing = state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
				enumfacing = EnumFacing.SOUTH;
			}
			else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
				enumfacing = EnumFacing.NORTH;
			}
			else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
				enumfacing = EnumFacing.EAST;
			}
			else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
				enumfacing = EnumFacing.WEST;
			}

			worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase el, ItemStack is) {
		world.setBlockState(pos, state.withProperty(FACING, el.getHorizontalFacing().getOpposite()), 2);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return getDefaultState().withProperty(FACING, enumfacing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				FACING
		});
	}

	/*
	@Override
	public void breakBlock(World world, BlockPos blockPos, IBlockState blockState)
	{
	    this.dropItems(world, blockPos);
	    super.onBlockDestroyedByPlayer(world, blockPos, blockState);
	}
	*/
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileLeadedChest();
	}

	/*
	private void dropItems(World world, BlockPos blockPos)
	{

	    TileEntity te = world.getTileEntity(blockPos);
	    if (te instanceof IInventory)
	    {
	        IInventory inventory = (IInventory) te;

	        int invSize = inventory.getSizeInventory();
	        for (int i = 0; i < invSize; i++)
	        {
	            MinechemUtil.throwItemStack(world, inventory.getStackInSlot(i), blockPos.getX(), blockPos.getY(), blockPos.getZ());
	        }
	    }
	}
	*/
	@Override
	public void addStacksDroppedOnBlockBreak(TileEntity tileEntity, ArrayList<ItemStack> itemStacks) {
		TileLeadedChest chest = (TileLeadedChest) tileEntity;
		for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
			ItemStack itemstack = chest.getStackInSlot(slot);
			if (!itemstack.isEmpty()) {
				itemStacks.add(itemstack);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileLeadedChest leadedChest = (TileLeadedChest) world.getTileEntity(pos);
			if (leadedChest == null || player.isSneaking()) {
				return false;
			}
			player.openGui(Minechem.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	/*
	@Override
	public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState state, EntityLivingBase el, ItemStack is)
	{
	    EnumFacing facing = null;
	    int facingI = MathHelper.floor(el.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

	    if (facingI == 0)
	    {
	        facing = EnumFacing.NORTH;
	    }

	    if (facingI == 1)
	    {
	        facing = EnumFacing.EAST;
	    }

	    if (facingI == 2)
	    {
	        facing = EnumFacing.SOUTH;
	    }

	    if (facingI == 3)
	    {
	        facing = EnumFacing.WEST;
	    }

	    world.setBlockState(blockPos, state.withProperty(FACING, facing), 2);
	}
	*/

}
