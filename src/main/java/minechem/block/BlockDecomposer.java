package minechem.block;

import minechem.Minechem;
import minechem.block.tile.TileDecomposer;
import minechem.client.render.RenderDecomposer;
import minechem.client.render.RenderDecomposer.ItemRenderDecomposer;
import minechem.init.ModCreativeTab;
import minechem.init.ModGlobals;
import minechem.init.ModGlobals.Textures;
import minechem.init.ModRendering;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDecomposer extends BlockSimpleContainer {

	public BlockDecomposer() {
		super(Material.IRON);
		setRegistryName(ModGlobals.MODID + ":chemical_decomposer");
		setUnlocalizedName("chemical_decomposer");
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		ModRendering.setBlockRendering(this, new RenderDecomposer(), TileDecomposer.class, new ItemRenderDecomposer(), Textures.Sprite.DECOMPOSER);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null || player.isSneaking() || !(tile instanceof TileDecomposer)) {
			return false;
		}
		if (!world.isRemote) {
			if (hand == EnumHand.MAIN_HAND && !player.getHeldItemMainhand().isEmpty() && (isFullBucket(player.getHeldItemMainhand()) || getFluidFromItemTank(player.getHeldItemMainhand()) != null)) {
				if (isFullBucket(player.getHeldItemMainhand()) || getFluidFromItemTank(player.getHeldItemMainhand()) != null) {
					TileDecomposer decomposerTile = (TileDecomposer) tile;
					if (doesTileContainFluid(decomposerTile)) {
						if (doesTileHaveFluidSpace(decomposerTile)) {
							if (getFluidFromBucket(player.getHeldItemMainhand()) != null) {
								FluidStack bucketFluid = getFluidFromBucket(player.getHeldItemMainhand());
								FluidStack tileFluid = decomposerTile.tank.getFluid();
								if (bucketFluid.isFluidEqual(tileFluid)) {
									decomposerTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP).fill(bucketFluid, true);

									if (!player.capabilities.isCreativeMode) {
										player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BUCKET));
									}
								}
							}
						}
					}
					else {
						if (getFluidFromBucket(player.getHeldItemMainhand()) != null) {
							FluidStack bucketFluid = getFluidFromBucket(player.getHeldItemMainhand());
							decomposerTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP).fill(bucketFluid, true);
							if (!player.capabilities.isCreativeMode) {
								player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BUCKET));
							}
						}
						else if (getFluidFromItemTank(player.getHeldItemMainhand()) != null) {
							FluidStack tankFluid = getFluidFromItemTank(player.getHeldItemMainhand());
							int added = decomposerTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP).fill(tankFluid, true);
							System.out.println("" + tankFluid.amount % 1000);
							if (!player.capabilities.isCreativeMode) {
								getFluidItemHandler(player.getHeldItemMainhand()).drain(added, !player.capabilities.isCreativeMode);
							}
						}
					}
				}
			}
			else {
				player.openGui(Minechem.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	private FluidStack getFluidFromItemTank(ItemStack tank) {
		IFluidHandlerItem handler = getFluidItemHandler(tank);
		if (handler != null) {
			FluidStack fluid = handler.getTankProperties()[0].getContents();
			return fluid;
		}
		return null;
	}

	private IFluidHandlerItem getFluidItemHandler(ItemStack tank) {
		if (tank.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			IFluidHandlerItem handler = tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			return handler;
		}
		return null;
	}

	private FluidStack getFluidFromBucket(ItemStack bucket) {
		Item item = bucket.getItem();
		if (item == Items.WATER_BUCKET) {
			return new FluidStack(FluidRegistry.WATER, 1000);
		}
		if (item == Items.LAVA_BUCKET) {
			return new FluidStack(FluidRegistry.LAVA, 1000);
		}
		if (item instanceof UniversalBucket || item == Items.BUCKET) {
			if (bucket.hasTagCompound()) {
				NBTTagCompound nbt = bucket.getTagCompound();
				if (nbt.hasKey("Tag")) {
					NBTTagCompound subNBT = nbt.getCompoundTag("Tag");
					if (subNBT.hasKey("FluidName")) {
						return new FluidStack(FluidRegistry.getFluid(subNBT.getString("FluidName")), 1000);
					}
				}
			}
		}
		return null;
	}

	private boolean doesTileContainFluid(TileDecomposer tile) {
		return tile.tank.getFluid() != null;
	}

	private boolean doesTileHaveFluidSpace(TileDecomposer tile) {
		int cap = tile.tank.getCapacity();
		int used = tile.tank.getFluidAmount();
		return cap - used >= 1000;
	}

	private boolean isFullBucket(ItemStack stack) {
		Item item = stack.getItem();
		boolean isBucketFull = false;
		if ((item instanceof UniversalBucket || item == Items.BUCKET) && stack.hasTagCompound()) {
			isBucketFull = true;
		}
		return isBucketFull || (item == Items.WATER_BUCKET) || (item == Items.LAVA_BUCKET);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileDecomposer();
	}

	/*
		@Override
		public void breakBlock(World world, BlockPos pos, IBlockState state) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof TileDecomposer) {
				NonNullList<ItemStack> drops = NonNullList.create();
				TileDecomposer decomposer = (TileDecomposer) tileEntity;
				for (int slot = 0; slot < decomposer.getSizeInventory(); slot++) {
					ItemStack itemstack = decomposer.getStackInSlot(slot);
					if (!itemstack.isEmpty()) {
						drops.add(itemstack);
					}
				}
				if (!drops.isEmpty()) {
					for (ItemStack stack : drops) {
						MinechemUtil.throwItemStack(world, stack, pos);
					}
				}
			}
		}
	*/
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_) {
		return false;
	}

}
