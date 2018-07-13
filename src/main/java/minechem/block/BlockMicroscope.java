package minechem.block;

import java.util.ArrayList;

import minechem.Minechem;
import minechem.block.tile.TileMicroscope;
import minechem.client.render.RenderMicroscope;
import minechem.client.render.RenderMicroscope.ItemRenderMicroscope;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMicroscope extends BlockSimpleContainer {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockMicroscope() {
		super(Material.IRON);
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setUnlocalizedName("microscope");
		setRegistryName(ModGlobals.ID + ":microscope");
		setLightLevel(0.5F);
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		setDefaultFacing(worldIn, pos, state);
	}

	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
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

		//int facing = MathHelper.floor(el.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		//world.setBlockState(pos, state.getBlock().getStateFromMeta(facing), 2);
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

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}

		player.openGui(Minechem.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int i) {
		return new TileMicroscope();
	}

	@Override
	public void addStacksDroppedOnBlockBreak(TileEntity tileEntity, ArrayList<ItemStack> itemStacks) {
		TileMicroscope decomposer = (TileMicroscope) tileEntity;
		for (int slot = 0; slot < decomposer.getSizeInventory(); slot++) {
			ItemStack itemstack = decomposer.getStackInSlot(slot);
			if (!itemstack.isEmpty()) {
				itemStacks.add(itemstack);
			}
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		//ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderMicroscope());
		//ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
		//@Override
		//protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		//	return new ModelResourceLocation(getRegistryName(), "inventory");
		//}
		//});
		//ModelRegistryHelper.setParticleTexture(this, Textures.Sprite.MICROSCOPE);
		//ClientRegistry.bindTileEntitySpecialRenderer(TileMicroscope.class, new RenderMicroscope());
		ModRendering.setBlockRendering(this, new RenderMicroscope(), TileMicroscope.class, new ItemRenderMicroscope(), Textures.Sprite.MICROSCOPE);
	}

}
