package minechem.block;

import java.util.ArrayList;

import minechem.Minechem;
import minechem.block.tile.TileSynthesis;
import minechem.client.render.RenderSynthesis;
import minechem.client.render.RenderSynthesis.ItemRenderSynthesis;
import minechem.init.ModCreativeTab;
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
import net.minecraft.item.Item;
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

/**
 * Chemical Synthesizer block. Its associated TileEntitySynthesis's inventory has many specialized slots, including some "ghost" slots whose contents don't really exist and shouldn't be able
 * to be extracted or dumped when the block is broken. See {@link minechem.block.tile.TileSynthesis} for details of the inventory slots.
 */
public class BlockSynthesis extends BlockSimpleContainer {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockSynthesis() {
		super(Material.IRON);
		setRegistryName("chemical_synthesizer");
		setUnlocalizedName("chemical_synthesizer");
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		ForgeRegistries.BLOCKS.register(this);
		Item item = new ItemBlock(this).setRegistryName(getRegistryName());
		//Minechem.PROXY.setTEISR(item, new ItemRenderSynthesis());
		ForgeRegistries.ITEMS.register(item);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		//ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderSynthesis());
		//final ModelResourceLocation modelLoc = new ModelResourceLocation(new ResourceLocation(ModGlobals.ID, "teisr"), "inventory");
		//register(modelLoc, renderer);
		//ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), stack -> modelLoc);
		//ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, modelLoc);
		//ModelRegistryHelper.setParticleTexture(this, Textures.Sprite.MICROSCOPE);
		//ClientRegistry.bindTileEntitySpecialRenderer(TileSynthesis.class, new RenderSynthesis());
		ModRendering.setBlockRendering(this, new RenderSynthesis(), TileSynthesis.class, new ItemRenderSynthesis(), Textures.Sprite.SYNTHESIZER);
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
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
		if (!world.isRemote) {
			/*
			SynthesisUpdateMessage message = new SynthesisUpdateMessage((TileSynthesis) tileEntity);
			if (player instanceof EntityPlayerMP) {
				MessageHandler.INSTANCE.sendTo(message, (EntityPlayerMP) player);
			}
			else {
				MessageHandler.INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), ModConfig.UpdateRadius));
			}
			*/
		}
		player.openGui(Minechem.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileSynthesis();
	}

	//TODO:Find replacement
	@Override
	public void addStacksDroppedOnBlockBreak(TileEntity tileEntity, ArrayList<ItemStack> itemStacks) {
		TileSynthesis synthesizer = (TileSynthesis) tileEntity;
		/*
		for (int slot : TileSynthesis.kRealSlots) {
			if (synthesizer.isRealItemSlot(slot)) {
				ItemStack itemstack = synthesizer.getStackInSlot(slot);
				if (!itemstack.isEmpty()) {
					itemStacks.add(itemstack);
				}
			}
		}
		*/
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

}
