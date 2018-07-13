package minechem.block;

import java.util.ArrayList;

import minechem.block.tile.TileBlueprintProjector;
import minechem.client.render.RenderBlueprintProjector;
import minechem.client.render.RenderBlueprintProjector.ItemRenderBlueprintProjector;
import minechem.init.ModCreativeTab;
import minechem.init.ModGlobals;
import minechem.init.ModGlobals.Textures;
import minechem.init.ModRendering;
import minechem.item.blueprint.ItemBlueprint;
import minechem.item.blueprint.MinechemBlueprint;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBlueprintProjector extends BlockSimpleContainer {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockBlueprintProjector() {
		super(Material.IRON);
		setUnlocalizedName("blueprint_projector");
		setRegistryName(ModGlobals.ID + ":blueprint_projector");
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

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

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
		if (tileEntity instanceof TileBlueprintProjector) {
			//player.openGui(Minechem.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
			if (hand == EnumHand.MAIN_HAND) {
				player.sendMessage(new TextComponentString("Temporarily disabled"));
				return false;
			}
		}
		return false;
	}

	private ItemStack takeBlueprintFromProjector(TileBlueprintProjector projector) {
		MinechemBlueprint blueprint = projector.takeBlueprint();
		return ItemBlueprint.createItemStackFromBlueprint(blueprint);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileBlueprintProjector();
	}

	@Override
	public void addStacksDroppedOnBlockBreak(TileEntity tileEntity, ArrayList<ItemStack> itemStacks) {
		if (tileEntity instanceof TileBlueprintProjector) {
			TileBlueprintProjector projector = (TileBlueprintProjector) tileEntity;
			if (projector.hasBlueprint()) {
				itemStacks.add(takeBlueprintFromProjector(projector));
			}
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileBlueprintProjector) {
			TileBlueprintProjector projector = (TileBlueprintProjector) tileEntity;
			projector.destroyProjection();
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return super.shouldSideBeRendered(blockState, blockAccess, pos, side);//true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return super.isNormalCube(state, world, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return super.isSideSolid(base_state, world, pos, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube(IBlockState state) {
		return super.isBlockNormalCube(state);
	}

	@Override
	public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_) {
		return false;
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

}
