package minechem.item;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import minechem.api.RadiationInfo;
import minechem.block.tile.TileRadioactiveFluid;
import minechem.init.ModConfig;
import minechem.init.ModCreativeTab;
import minechem.init.ModFluids;
import minechem.init.ModGlobals;
import minechem.init.ModItems;
import minechem.init.ModRendering;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PharmacologyEffect;
import minechem.potion.PharmacologyEffectRegistry;
import minechem.radiation.RadiationEnum;
import minechem.utils.MinechemUtil;
import minechem.utils.TickTimeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class ItemMolecule extends ItemBase {

	public ItemMolecule() {
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ELEMENTS);
		setHasSubtypes(true);
		setUnlocalizedName("molecule");
		setRegistryName(ModGlobals.ID + ":molecule");
		ForgeRegistries.ITEMS.register(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderer() {
		for (int i = 0; i < MoleculeEnum.molecules.values().size(); i++) {
			MoleculeEnum molecule = MoleculeEnum.molecules.get(i);
			if (molecule != null) {
				ModelLoader.setCustomModelResourceLocation(this, molecule.id(), ModRendering.ITEM_MOLECULE_LOC);
			}
		}
	}

	public IItemColor getColorHandler() {
		return (stack, tintIndex) -> {
			MoleculeEnum molecule = MinechemUtil.getMolecule(stack);
			if (molecule == null) {
				return -1;
			}
			int color = 0xFFFFFFFF;
			if (tintIndex == 1) {
				color = molecule.getColor1();
			}
			if (tintIndex == 2) {
				color = molecule.getColor2();
			}
			return color;
		};
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		return MinechemUtil.getLocalString(MinechemUtil.getMolecule(itemStack).getUnlocalizedName(), true);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return getUnlocalizedName() + "." + MinechemUtil.getMolecule(par1ItemStack).name();
	}

	public String getFormulaWithSubscript(ItemStack itemstack) {
		String formula = MinechemUtil.getMolecule(itemstack).getFormula();
		return MinechemUtil.subscriptNumbers(formula);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<String> list, ITooltipFlag flagIn) {
		list.add("\u00A79" + getFormulaWithSubscript(itemstack));

		RadiationEnum radioactivity = RadiationInfo.getRadioactivity(itemstack);
		String radioactivityColor = radioactivity.getColour();

		String radioactiveName = MinechemUtil.getLocalString("element.property." + radioactivity.name(), true);
		String timeLeft = "";
		if (RadiationInfo.getRadioactivity(itemstack) != RadiationEnum.stable && itemstack.getTagCompound() != null) {
			long worldTime = world.getTotalWorldTime();
			timeLeft = TickTimeUtil.getTimeFromTicks(RadiationInfo.getRadioactivity(itemstack).getLife() - (worldTime - itemstack.getTagCompound().getLong("decayStart")));
		}
		list.add(radioactivityColor + radioactiveName + (timeLeft.equals("") ? "" : " (" + timeLeft + ")"));
		list.add(getRoomState(itemstack));
		MoleculeEnum molecule = MoleculeEnum.getById(itemstack.getItemDamage());
		if (PharmacologyEffectRegistry.hasEffect(molecule) && ModConfig.displayMoleculeEffects) {

			//if (PolytoolHelper.getTypeFromElement(ItemElement.getElement(itemstack), 1) != null) {
			// Polytool Detail
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				for (PharmacologyEffect effect : PharmacologyEffectRegistry.getEffects(molecule)) {
					if (!effect.toString().isEmpty()) {
						list.add(effect.getColour() + effect.toString());
					}
				}

			}
			else {
				list.add(TextFormatting.DARK_GREEN + MinechemUtil.getLocalString("effect.information", true));
			}
			//}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) {
			for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
				if (molecule != null) {
					list.add(new ItemStack(this, 1, molecule.id()));
				}
			}
		}
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity te = world.getTileEntity(pos);
		ItemStack stack = player.getHeldItem(hand);
		boolean result = !world.isRemote;
		if (te != null && te instanceof IFluidHandler && !player.isSneaking() && !(te instanceof IInventory)) {
			int filled = 0;
			//for (int i = 0; i < 6; i++) {
			FluidStack fluidStack = new FluidStack(FluidRegistry.WATER, 125);
			if (MinechemUtil.getMolecule(stack) != MoleculeEnum.water) {
				Fluid fluid = ModFluids.FLUID_MOLECULES.get(MinechemUtil.getMolecule(stack));
				if (fluid == null) {
					return super.onItemUseFirst(player, world, pos, facing, hitX, hitY, hitZ, hand);
				}
				fluidStack = new FluidStack(fluid, 125);

			}
			filled = ((IFluidHandler) te).fill(fluidStack, false);
			if (filled > 0) {
				if (result) {
					((IFluidHandler) te).fill(fluidStack, true);
				}
				if (!player.capabilities.isCreativeMode) {
					MinechemUtil.incPlayerInventory(stack, -1, player, new ItemStack(ModItems.element, 1, 0));
				}
				return (result || stack.getCount() <= 0) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
			}
			//}
			return result ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
		}
		return super.onItemUseFirst(player, world, pos, facing, hitX, hitY, hitZ, hand);
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		MatterState matterState = MoleculeEnum.molecules.get(stack.getItemDamage()).roomState();
		return matterState == MatterState.LIQUID ? EnumAction.DRINK : EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 16;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer) || MoleculeEnum.molecules.get(itemStack.getItemDamage()).roomState() != MatterState.LIQUID) {
			return itemStack;
		}

		EntityPlayer entityPlayer = (EntityPlayer) entity;

		if (!entityPlayer.capabilities.isCreativeMode) {
			itemStack.shrink(1);
		}

		if (world.isRemote)

		{
			//return itemStack;
		}

		MoleculeEnum molecule = MinechemUtil.getMolecule(itemStack);
		PharmacologyEffectRegistry.applyEffect(molecule, entityPlayer);
		world.playSound(entityPlayer, entityPlayer.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F); // Thanks mDiyo!
		return itemStack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		player.setActiveHand(hand);

		RayTraceResult rayTrace = rayTrace(world, player, false);
		if (rayTrace == null || player.isSneaking()) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
		}

		if (rayTrace.typeOfHit == RayTraceResult.Type.BLOCK && ModConfig.vialPlacing) {
			int blockX = rayTrace.getBlockPos().getX();
			int blockY = rayTrace.getBlockPos().getY();
			int blockZ = rayTrace.getBlockPos().getZ();

			EnumFacing dir = rayTrace.sideHit;
			blockX += dir.getFrontOffsetX();
			blockY += dir.getFrontOffsetY();
			blockZ += dir.getFrontOffsetZ();

			if (!player.canPlayerEdit(rayTrace.getBlockPos(), rayTrace.sideHit, itemStack)) {
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, emptyTube(itemStack, player, world, blockX, blockY, blockZ));
		}

		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
	}

	private ItemStack emptyTube(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);

		if (!world.isAirBlock(pos) && !world.getBlockState(pos).getMaterial().isSolid()) {
			IBlockState state = world.getBlockState(pos);

			state.getBlock().harvestBlock(world, player, pos, state, null, ItemStack.EMPTY);
			state.getBlock().breakBlock(world, pos, state);
			world.setBlockToAir(pos);
		}

		if (world.isAirBlock(pos)) {
			RadiationInfo radioactivity = ItemElement.getRadiationInfo(itemStack, world);
			long worldtime = world.getTotalWorldTime();
			long leftTime = radioactivity.radioactivity.getLife() - (worldtime - radioactivity.decayStarted);
			MoleculeEnum molecule = MinechemUtil.getMolecule(itemStack);
			Fluid fluid = ModFluids.FLUID_MOLECULES.get(molecule);
			if (fluid == null) {
				return itemStack;
			}
			if (!player.capabilities.isCreativeMode) {
				if (itemStack.getCount() >= 8) {
					itemStack.shrink(8);
				}
				else {
					int needs = 8 - itemStack.getCount();
					Set<ItemStack> otherItemsStacks = MinechemUtil.findItemStacks(player.inventory, itemStack.getItem(), itemStack.getItemDamage());
					otherItemsStacks.remove(itemStack);
					int free = 0;
					Iterator<ItemStack> it2 = otherItemsStacks.iterator();
					while (it2.hasNext()) {
						ItemStack stack = it2.next();
						free += stack.getCount();
					}
					if (free < needs) {
						return itemStack;
					}
					itemStack.setCount(0);

					Iterator<ItemStack> it = otherItemsStacks.iterator();
					while (it.hasNext()) {
						ItemStack stack = it.next();
						RadiationInfo anotherRadiation = ItemElement.getRadiationInfo(stack, world);
						long anotherLeft = anotherRadiation.radioactivity.getLife() - (worldtime - anotherRadiation.decayStarted);
						if (anotherLeft < leftTime) {
							radioactivity = anotherRadiation;
							leftTime = anotherLeft;
						}

						if (stack.getCount() >= needs) {
							stack.shrink(needs);
							needs = 0;
						}
						else {
							needs -= stack.getCount();
							stack.setCount(0);
						}

						if (stack.getCount() <= 0) {
							MinechemUtil.removeStackInInventory(player.inventory, stack);
						}

						if (needs == 0) {
							break;
						}
					}
				}
				ItemStack empties = MinechemUtil.addItemToInventory(player.inventory, new ItemStack(ModItems.element, 8, 0));
				MinechemUtil.throwItemStack(world, empties, x, y, z);
			}

			Block block = Blocks.FLOWING_WATER;
			if (MinechemUtil.getMolecule(itemStack) != MoleculeEnum.water) {
				block = ModFluids.FLUID_MOLECULE_BLOCKS.get(fluid);
			}
			world.setBlockState(pos, block.getStateFromMeta(0), 3);
			TileEntity tile = world.getTileEntity(pos);
			if (radioactivity.isRadioactive() && tile instanceof TileRadioactiveFluid) {
				((TileRadioactiveFluid) tile).info = radioactivity;
			}
		}
		return itemStack;
	}

	public static String getRoomState(ItemStack itemstack) {
		int id = itemstack.getItemDamage();
		return (MoleculeEnum.molecules.get(id) == null) ? "null" : MoleculeEnum.molecules.get(id).roomState().descriptiveName();
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		super.onCreated(itemStack, world, player);
		if (RadiationInfo.getRadioactivity(itemStack) != RadiationEnum.stable && itemStack.getTagCompound() == null) {
			RadiationInfo.setRadiationInfo(new RadiationInfo(itemStack, world.getTotalWorldTime(), world.getTotalWorldTime(), world.provider.getDimension(), RadiationInfo.getRadioactivity(itemStack)), itemStack);
		}
	}
}
