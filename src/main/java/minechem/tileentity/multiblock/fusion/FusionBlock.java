package minechem.tileentity.multiblock.fusion;

import java.util.ArrayList;

import minechem.Minechem;
import minechem.block.BlockSimpleContainer;
import minechem.block.tile.TileEntityProxy;
import minechem.init.ModCreativeTab;
import minechem.tileentity.multiblock.MultiBlockTileEntity;
import minechem.tileentity.multiblock.fission.FissionTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FusionBlock extends BlockSimpleContainer {

	static PropertyEnum<FusionBlockType> TYPE = PropertyEnum.<FusionBlockType>create("type", FusionBlockType.class);

	public FusionBlock() {
		super(Material.IRON);
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setUnlocalizedName("fusion_wall");
		setRegistryName("fusion_wall");
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new FusionItemBlock(this).setRegistryName(getRegistryName()));
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

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		for (int i = 0; i < 3; i++) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName(), "type=" + FusionBlockType.values()[i].getName()));
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity == null) {
			//return false;
		}
		if (!world.isRemote) {
			player.openGui(Minechem.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public void addStacksDroppedOnBlockBreak(TileEntity tileEntity, ArrayList<ItemStack> itemStacks) {
		// Should not drop blocks if this is a proxy
		if (tileEntity instanceof MultiBlockTileEntity && tileEntity instanceof IInventory) {
			IInventory inv = (IInventory) tileEntity;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				if (!inv.getStackInSlot(i).isEmpty()) {
					itemStacks.add(inv.getStackInSlot(i));
				}
			}
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		int metadata = state.getBlock().getMetaFromState(state);
		if (metadata == 1) {
			return new FusionTileEntity();
		}
		if (metadata == 2) {
			return new FissionTileEntity();
		}
		else {
			return new TileEntityProxy();
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	// Do not drop if this is a reactor core
	/*
	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return state.getBlock().getMetaFromState(state) < 2 ? 1 : 0;
	}
	*/

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
		for (int i = 0; i <= 2; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityProxy();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	public static enum FusionBlockType implements IStringSerializable {
			FUSION("fusion_wall"),
			TUNGSTEN("tungsten_plating"),
			CORE("fusion_core");

		private final String name;

		private FusionBlockType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

}
