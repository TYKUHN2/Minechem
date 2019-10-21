package minechem.block.fluid;

import java.util.Random;

import minechem.api.ICustomRenderer;
import minechem.block.tile.TileRadioactiveFluid;
import minechem.fluid.*;
import minechem.fluid.reaction.ChemicalFluidReactionHandler;
import minechem.handler.HandlerExplosiveFluid;
import minechem.init.*;
import minechem.item.MatterState;
import minechem.item.MinechemChemicalType;
import minechem.radiation.RadiationEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class BlockFluidMinechem extends BlockFluidClassic implements ITileEntityProvider, ICustomRenderer {

	private final boolean isRadioactivity;
	public static final Material materialFluidBlock = Material.WATER;
	private final boolean solid;

	public BlockFluidMinechem(final FluidMinechem fluid, final Material material) {
		super(fluid, material);
		setQuantaPerBlock(fluid.getQuanta());

		if (fluid instanceof FluidElement) {
			isRadioactivity = ((FluidElement) fluid).element.radioactivity() != RadiationEnum.stable;
		}
		else if (fluid instanceof FluidMolecule) {
			isRadioactivity = ((FluidMolecule) fluid).molecule.radioactivity() != RadiationEnum.stable;
		}
		else {
			isRadioactivity = false;
		}

		//isBlockContainer = true;
		solid = fluid.getChemical().roomState() == MatterState.SOLID;
		stack = new FluidStack(fluid, 1000);
		setUnlocalizedName(getFluid().getName());
		setRegistryName(getFluid().getName());
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
		fluid.setBlock(this);
		//fluid.setTemperature(500);
		if (!FluidRegistry.getBucketFluids().contains(fluid)) {
			FluidRegistry.addBucketForFluid(fluid);
		}
	}

	@Override
	public String getUnlocalizedName() {
		final String fluidUnlocalizedName = getFluid().getUnlocalizedName();
		return fluidUnlocalizedName.substring(0, fluidUnlocalizedName.length() - 5);// Splits off ".name"
	}

	@Override
	public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

		checkStatus(worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

	public void checkStatus(final World world, final int x, final int y, final int z) {
		if (world.isRemote) {
			return;
		}

		if (ModConfig.reactionFluidMeetFluid) {
			for (final EnumFacing face : EnumFacing.values()) {
				if (checkToReact(world, x + face.getFrontOffsetX(), y + face.getFrontOffsetY(), z + face.getFrontOffsetZ(), x, y, z)) {
					return;
				}
			}
		}

		checkToExplode(world, x, y, z);
	}

	private boolean checkToReact(final World world, final int dx, final int dy, final int dz, final int sx, final int sy, final int sz) {
		return ChemicalFluidReactionHandler.checkToReact(this, world.getBlockState(new BlockPos(dx, dy, dz)).getBlock(), world, dx, dy, dz, sx, sy, sz);
	}

	private void checkToExplode(final World world, final int x, final int y, final int z) {
		final MinechemChemicalType type = MinechemUtil.getChemical(this);
		final float level = HandlerExplosiveFluid.getInstance().getExplosiveFluid(type);
		if (Float.isNaN(level)) {
			return;
		}

		boolean flag = false;
		for (final EnumFacing face : EnumFacing.values()) {
			if (HandlerExplosiveFluid.getInstance().existingFireSource(world.getBlockState(new BlockPos(x + face.getFrontOffsetX(), y + face.getFrontOffsetY(), z + face.getFrontOffsetZ())).getBlock())) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			return;
		}

		world.destroyBlock(new BlockPos(x, y, z), true);
		world.setBlockToAir(new BlockPos(x, y, z));
		world.createExplosion(null, x, y, z, HandlerExplosiveFluid.getInstance().getExplosiveFluid(type), true);
	}

	@Override
	public boolean hasTileEntity(final IBlockState state) {
		return isRadioactivity && state.getBlock().getMetaFromState(state) == 0;
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int i) {
		return hasTileEntity(getStateFromMeta(i)) ? new TileRadioactiveFluid() : null;
	}

	@Override
	public boolean eventReceived(final IBlockState state, final World world, final BlockPos pos, final int eventID, final int eventParameter) {
		super.eventReceived(state, world, pos, eventID, eventParameter);

		final TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(eventID, eventParameter) : false;
	}

	@Override
	public void onBlockDestroyedByExplosion(final World world, final BlockPos pos, final Explosion explosion) {
		if (world.isRemote) {
			return;
		}

		final MinechemChemicalType type = MinechemUtil.getChemical(this);
		world.destroyBlock(pos, true);
		world.setBlockToAir(pos);
		world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), HandlerExplosiveFluid.getInstance().getExplosiveFluid(type), true);
	}

	@Override
	public void updateTick(final World world, final BlockPos pos, final IBlockState state, final Random rand) {
		if (!solid) {
			super.updateTick(world, pos, state, rand);
		}
		checkStatus(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void onBlockAdded(final World world, final BlockPos pos, final IBlockState state) {
		super.onBlockAdded(world, pos, state);
		checkStatus(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(final IBlockState stateIn, final World worldIn, final BlockPos pos, final Random rand) {
		final double d0 = pos.getX();
		final double d1 = pos.getY();
		final double d2 = pos.getZ();

		if (blockMaterial == ModMaterial.FLUID) {
			final int i = stateIn.getValue(LEVEL).intValue();

			if (i > 0 && i < 8) {
				if (rand.nextInt(64) == 0) {
					worldIn.playSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() + 0.5F, false);
				}
			}
			else if (rand.nextInt(10) == 0) {
				worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, d0 + rand.nextFloat(), d1 + rand.nextFloat(), d2 + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			}
		}

		if (blockMaterial == Material.LAVA && worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR && !worldIn.getBlockState(pos.up()).isOpaqueCube()) {
			if (rand.nextInt(100) == 0) {
				final double d8 = d0 + rand.nextFloat();
				final double d4 = d1 + stateIn.getBoundingBox(worldIn, pos).maxY;
				final double d6 = d2 + rand.nextFloat();
				worldIn.spawnParticle(EnumParticleTypes.LAVA, d8, d4, d6, 0.0D, 0.0D, 0.0D);
				worldIn.playSound(d8, d4, d6, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}

			if (rand.nextInt(200) == 0) {
				worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}
		}

		if (rand.nextInt(10) == 0 && worldIn.getBlockState(pos.down()).isTopSolid()) {
			final Material material = worldIn.getBlockState(pos.down(2)).getMaterial();

			if (!material.blocksMovement() && !material.isLiquid()) {
				final double d3 = d0 + rand.nextFloat();
				final double d5 = d1 - 1.05D;
				final double d7 = d2 + rand.nextFloat();

				if (blockMaterial == ModMaterial.FLUID) {
					worldIn.spawnParticle(EnumParticleTypes.DRIP_LAVA, d3, d5, d7, 0.0D, 1.0D, 0.0D);
				}
				else {
					worldIn.spawnParticle(EnumParticleTypes.DRIP_LAVA, d3, d5, d7, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderer() {
		final Item item = Item.getItemFromBlock(this);
		ModelBakery.registerItemVariants(item);
		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(ModGlobals.MODID + ":fluid", getFluid().getName());
		ModelLoader.setCustomMeshDefinition(item, stack -> modelResourceLocation);
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(final IBlockState p_178132_1_) {
				return modelResourceLocation;
			}
		});
	}
}
