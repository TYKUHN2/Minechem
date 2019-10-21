package minechem.block;

import minechem.Minechem;
import minechem.block.tile.TileBlueprintProjector;
import minechem.client.render.RenderBlueprintProjector;
import minechem.client.render.RenderBlueprintProjector.ItemRenderBlueprintProjector;
import minechem.init.*;
import minechem.init.ModGlobals.Textures;
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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBlueprintProjector extends BlockSimpleContainer {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockBlueprintProjector() {
		super(Material.IRON);
		setUnlocalizedName("blueprint_projector");
		setRegistryName(ModGlobals.MODID + ":blueprint_projector");
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setLightLevel(0.7F);
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		//ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderBlueprintProjector());
		//ModelRegistryHelper.setParticleTexture(this, Textures.Sprite.MICROSCOPE);
		//ClientRegistry.bindTileEntitySpecialRenderer(TileBlueprintProjector.class, new RenderBlueprintProjector());
		//ModelLoader.setCustomStateMapper(this, blockIn -> Collections.emptyMap());
		ModRendering.setBlockRendering(this, new RenderBlueprintProjector(), TileBlueprintProjector.class, new ItemRenderBlueprintProjector(), Textures.Sprite.BLUEPRINT_PROJECTOR);
	}

	@Override
	public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(final World world, final BlockPos pos, final IBlockState state, final EntityLivingBase el, final ItemStack is) {
		world.setBlockState(pos, state.withProperty(FACING, el.getHorizontalFacing().getOpposite()), 2);
	}

	@Override
	public IBlockState getStateFromMeta(final int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(final IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState withRotation(final IBlockState state, final Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				FACING
		});
	}

	@Override
	public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {

		final TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileBlueprintProjector) {
			player.openGui(Minechem.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
			//if (hand == EnumHand.MAIN_HAND) {
			//	player.sendMessage(new TextComponentString("Temporarily disabled"));
			//	return false;
			//}
			return true;
		}
		return false;
	}

	/*private ItemStack takeBlueprintFromProjector(TileBlueprintProjector projector) {
		IMinechemBlueprint blueprint = projector.takeBlueprint();
		return BlueprintUtil.createStack(blueprint);
	}*/

	@Override
	public TileEntity createNewTileEntity(final World world, final int i) {
		return new TileBlueprintProjector();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(final IBlockState p_isOpaqueCube_1_) {
		return false;
	}

	@Override
	public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
		if (!worldIn.isRemote) {
			final IBlockState iblockstate = worldIn.getBlockState(pos.north());
			final IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
			final IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
			final IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
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
	/*
		@Override
		public void breakBlock(World world, BlockPos pos, IBlockState state) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof TileBlueprintProjector) {
				TileBlueprintProjector projector = (TileBlueprintProjector) tileEntity;
				if (projector.hasBlueprint()) {
					MinechemUtil.throwItemStack(world, takeBlueprintFromProjector(projector), pos.getX(), pos.getY(), pos.getZ());
				}
			}
			if (hasTileEntity(state) && !(this instanceof BlockContainer)) {
				world.removeTileEntity(pos);
			}
		}
		*/

}
