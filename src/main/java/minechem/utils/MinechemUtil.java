package minechem.utils;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import minechem.block.fluid.BlockFluidMinechem;
import minechem.fluid.*;
import minechem.init.*;
import minechem.item.*;
import minechem.item.element.Element;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@SuppressWarnings("deprecation")
public final class MinechemUtil {

	public static final Random random = new Random();

	private MinechemUtil() {
	}

	public static ItemStack addItemToInventory(final IInventory inventory, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		for (int i = 0, l = inventory.getSizeInventory(); i < l; i++) {
			final ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) {
				final int append = itemStack.getCount() > inventory.getInventoryStackLimit() ? inventory.getInventoryStackLimit() : itemStack.getCount();
				final ItemStack newStack = itemStack.copy();
				newStack.setCount(append);
				inventory.setInventorySlotContents(i, newStack);
				itemStack.shrink(append);
			}
			else if (stack.getItem() == itemStack.getItem() && stack.getItemDamage() == itemStack.getItemDamage()) {
				final int free = inventory.getInventoryStackLimit() - stack.getCount();
				final int append = itemStack.getCount() > free ? free : itemStack.getCount();
				itemStack.shrink(append);
				stack.grow(append);
				inventory.setInventorySlotContents(i, stack);
			}

			if (itemStack.getCount() <= 0) {
				itemStack = ItemStack.EMPTY;
				break;
			}
		}
		return itemStack;
	}

	public static void throwItemStack(final World world, final ItemStack stack, final BlockPos pos) {
		throwItemStack(world, stack, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void throwItemStack(final World world, final ItemStack itemStack, final double x, final double y, final double z) {
		if (!itemStack.isEmpty()) {
			final float f = random.nextFloat() * 0.8F + 0.1F;
			final float f1 = random.nextFloat() * 0.8F + 0.1F;
			final float f2 = random.nextFloat() * 0.8F + 0.1F;

			final EntityItem entityitem = new EntityItem(world, (float) x + f, (float) y + f1, (float) z + f2, itemStack);
			final float f3 = 0.05F;
			entityitem.motionX = (float) random.nextGaussian() * f3;
			entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
			entityitem.motionZ = (float) random.nextGaussian() * f3;
			world.spawnEntity(entityitem);
		}
	}

	public static ItemStack createItemStack(final MinechemChemicalType chemical, final int amount) {
		ItemStack itemStack = ItemStack.EMPTY;
		if (chemical instanceof ElementEnum) {
			itemStack = ItemElement.createStackOf(ElementEnum.getByID(((ElementEnum) chemical).atomicNumber()), amount);
		}
		else if (chemical instanceof MoleculeEnum) {
			itemStack = new ItemStack(ModItems.molecule, amount, ((MoleculeEnum) chemical).id());
		}
		return itemStack;
	}

	public static boolean canDrain(final World world, final Block block, final int x, final int y, final int z) {
		if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && world.getBlockState(new BlockPos(x, y, z)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(x, y, z))) == 0) {
			return true;
		}
		else if (block instanceof IFluidBlock) {
			return ((IFluidBlock) block).canDrain(world, new BlockPos(x, y, z));
		}

		return false;
	}

	public static MinechemChemicalType getChemical(final Block block) {
		MinechemChemicalType chemical = null;
		if (block instanceof IFluidBlock) {
			final Fluid fluid = ((IFluidBlock) block).getFluid();
			chemical = getChemical(fluid);
		}
		else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
			chemical = MoleculeEnum.water;
		}

		return chemical;
	}

	public static MinechemChemicalType getChemical(final Fluid fluid) {
		if (fluid instanceof FluidElement) {
			return ((FluidElement) fluid).element;
		}
		else if (fluid instanceof FluidMolecule) {
			return ((FluidMolecule) fluid).molecule;
		}
		else if (fluid == FluidRegistry.WATER) {
			return MoleculeEnum.water;
		}
		return null;
	}

	public static ElementEnum getElement(final Fluid fluid) {
		for (final Map.Entry<ElementEnum, FluidElement> entry : ModFluids.FLUID_ELEMENTS.entrySet()) {
			if (entry.getValue() == fluid) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static MoleculeEnum getMolecule(final Fluid fluid) {
		for (final Entry<MoleculeEnum, Fluid> entry : ModFluids.FLUID_MOLECULES.entrySet()) {
			if (entry.getValue() == fluid) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static Fluid getFluid(final TileEntity te) {
		if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).getTankProperties()[0].getContents().getFluid();
		}
		for (final EnumFacing facing : EnumFacing.VALUES) {
			if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)) {
				final Fluid fluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing).getTankProperties()[0].getContents().getFluid();
				if (fluid != null) {
					return fluid;
				}
			}
		}
		return null;
	}

	public static void incPlayerInventory(final ItemStack current, final int inc, final EntityPlayer player, final ItemStack give) {
		if (inc < 0) {
			current.splitStack(-inc);
		}
		else if (inc > 0) {
			if (current.getCount() + inc <= current.getMaxStackSize()) {
				current.grow(inc);
			}
			else {
				final int added = current.getMaxStackSize() - current.getCount();
				current.setCount(current.getMaxStackSize());
				final ItemStack extraStack = current.copy();
				extraStack.setCount(inc - added);
				if (!player.inventory.addItemStackToInventory(extraStack)) {
					player.dropItem(extraStack, false);
				}
			}
		}

		if (!player.inventory.addItemStackToInventory(give)) {
			player.dropItem(give, false);
		}
	}

	public static Set<ItemStack> findItemStacks(final IInventory inventory, final Item item, final int damage) {
		final Set<ItemStack> stacks = new HashSet<>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			final ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == item && stack.getItemDamage() == damage) {
				stacks.add(stack);
			}
		}

		return stacks;
	}

	public static void removeStackInInventory(final IInventory inventory, final ItemStack stack) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (stack == inventory.getStackInSlot(i)) { //don't change == to equals()
				inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				break;
			}
		}
	}

	public static String subscriptNumbers(String string) {
		string = string.replace('0', '\u2080');
		string = string.replace('1', '\u2081');
		string = string.replace('2', '\u2082');
		string = string.replace('3', '\u2083');
		string = string.replace('4', '\u2084');
		string = string.replace('5', '\u2085');
		string = string.replace('6', '\u2086');
		string = string.replace('7', '\u2087');
		string = string.replace('8', '\u2088');
		string = string.replace('9', '\u2089');
		return string;
	}

	public static void addDisabledStacks(final String[] stringInputs, final NonNullList<ItemStack> decomposerBlacklist, final ArrayList<String> ids) {
		for (final String string : stringInputs) {
			if (string == null || string.equals("")) {
				continue;
			}
			final String[] splitString = string.split(":");
			final ArrayList<String> wildcardMatch = new ArrayList<>();
			if (splitString.length < 2 || splitString.length > 3) {
				ModLogger.debug(string + " is an invalid blacklist input");
				continue;
			}
			if (splitString[0].equals("ore")) {
				String itemID = splitString[1];
				if (itemID.contains("*")) {
					itemID = itemID.replaceAll("\\*", ".*");
				}
				final Pattern itemPattern = Pattern.compile(itemID, Pattern.CASE_INSENSITIVE);
				for (final String item : OreDictionary.getOreNames()) {
					if (itemPattern.matcher(item).matches()) {
						wildcardMatch.add(item);
					}
				}
				if (wildcardMatch.isEmpty()) {
					ModLogger.debug(splitString[1] + " has no matches in the OreDictionary");
					continue;
				}
				for (final String key : wildcardMatch) {
					decomposerBlacklist.addAll(OreDictionary.getOres(key));
				}
			}
			else {
				int meta;
				try {
					meta = splitString.length == 3 ? Integer.valueOf(splitString[2]) : Short.MAX_VALUE;
				}
				catch (final NumberFormatException e) {
					if (splitString[2].equals("*")) {
						meta = Short.MAX_VALUE;
					}
					else {
						ModLogger.debug(splitString[2] + " is an invalid damage value - defaulting to all values");
						meta = Short.MAX_VALUE;
					}
				}
				String itemID = splitString[0] + ":" + splitString[1];
				if (itemID.contains("*")) {
					itemID = itemID.replaceAll("\\*", ".*");
				}
				final Pattern itemPattern = Pattern.compile(itemID, Pattern.CASE_INSENSITIVE);
				for (final String item : ids) {
					if (itemPattern.matcher(item).matches()) {
						wildcardMatch.add(item);
					}
				}
				if (wildcardMatch.isEmpty()) {
					ModLogger.debug(string + " has no matches in the ItemRegistry");
					continue;
				}
				for (final String key : wildcardMatch) {
					final Object disable = Item.REGISTRY.getObject(new ResourceLocation(key));
					if (disable instanceof Item) {
						decomposerBlacklist.add(new ItemStack((Item) disable, 1, meta));
					}
					else if (disable instanceof Block) {
						decomposerBlacklist.add(new ItemStack((Block) disable, 1, meta));
					}
				}
			}
		}
	}

	public static void populateBlacklists() {
		//ModConfig.decomposerBlacklist = new ArrayList<ItemStack>();
		//ModConfig.synthesisBlacklist = new ArrayList<ItemStack>();
		ModConfig.decomposerBlacklist.add(ModItems.emptyTube);
		final ArrayList<String> registeredItems = new ArrayList<>();
		for (final ResourceLocation key : Item.REGISTRY.getKeys()) {
			registeredItems.add(key.toString());
		}
		addDisabledStacks(ModConfig.DecomposerBlacklist, ModConfig.decomposerBlacklist, registeredItems);
		addDisabledStacks(ModConfig.SynthesisMachineBlacklist, ModConfig.synthesisBlacklist, registeredItems);
	}

	@SideOnly(Side.CLIENT)
	public static int getSplitStringHeight(final FontRenderer fontRenderer, final String string, final int width) {
		final List<String> stringRows = fontRenderer.listFormattedStringToWidth(string, width);
		return stringRows.size() * fontRenderer.FONT_HEIGHT;
	}

	public static float translateValue(final float value, final float leftMin, final float leftMax, final float rightMin, final float rightMax) {
		final float leftRange = leftMax - leftMin;
		final float rightRange = rightMax - rightMin;
		final float valueScaled = (value - leftMin) / leftRange;
		return rightMin + valueScaled * rightRange;
	}

	public static String getLocalString(final String key) {
		return getLocalString(key, false);
	}

	public static String getLocalString(final String key, final boolean capitalize) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			//LanguageMap languageMap = new LanguageMap();
			//String localString = languageMap.translateKey(key);
			final String localString = I18n.translateToLocal(key);
			return capitalize ? capitalizeFully(localString.replaceAll("molecule\\.", "")) : localString;
		}
		return key;
	}

	public static String capitalizeFully(final String input) {
		final String[] splitString = input.split(" ");
		String result = "";
		for (int i = 0; i < splitString.length; i++) {
			final char[] digit = splitString[i].toCharArray();
			if (digit.length < 1) {
				continue;
			}
			digit[0] = Character.toUpperCase(digit[0]);
			for (int j = 1; j < digit.length; j++) {
				digit[j] = Character.toLowerCase(digit[j]);
			}
			result += new String(digit) + (i < splitString.length - 1 ? " " : "");
		}
		return result;
	}

	public static NBTTagList writeItemStackListToTagList(final NonNullList<ItemStack> list) {
		final NBTTagList taglist = new NBTTagList();
		for (final ItemStack itemstack : list) {
			final NBTTagCompound itemstackCompound = new NBTTagCompound();
			itemstack.writeToNBT(itemstackCompound);
			taglist.appendTag(itemstackCompound);
		}
		return taglist;
	}

	public static NonNullList<ItemStack> readTagListToItemStackList(final NBTTagList taglist) {
		final NonNullList<ItemStack> itemlist = NonNullList.<ItemStack>withSize(taglist.tagCount(), ItemStack.EMPTY);
		for (int i = 0; i < taglist.tagCount(); i++) {
			final NBTTagCompound itemstackCompound = taglist.getCompoundTagAt(i);
			final ItemStack itemstack = new ItemStack(itemstackCompound);
			itemlist.set(i, itemstack);
		}
		return itemlist;
	}

	public static NonNullList<ItemStack> convertChemicalsIntoItemStacks(final List<PotionChemical> potionChemicals) {
		return convertChemicalsIntoItemStacks(potionChemicals.toArray(new PotionChemical[potionChemicals.size()]));
	}

	public static NonNullList<ItemStack> convertChemicalsIntoItemStacks(final PotionChemical[] potionChemicals) {
		if (potionChemicals != null && potionChemicals.length > 0) {
			final NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(potionChemicals.length, ItemStack.EMPTY);
			if (potionChemicals != null && potionChemicals.length > 0) {
				for (int i = 0; i < potionChemicals.length; i++) {
					final PotionChemical potionChemical = potionChemicals[i];
					if (potionChemical instanceof Element && ((Element) potionChemical).element != null) {
						stacks.set(i, new ItemStack(ModItems.element, potionChemical.amount, ((Element) potionChemical).element.atomicNumber()));
					}
					else if (potionChemical instanceof Molecule && ((Molecule) potionChemical).molecule != null) {
						stacks.set(i, new ItemStack(ModItems.molecule, potionChemical.amount, ((Molecule) potionChemical).molecule.id()));
					}
				}
			}
			return stacks;
		}
		return NonNullList.<ItemStack>create();
	}

	public static NonNullList<ItemStack> copyStackList(final NonNullList<ItemStack> stackList) {
		final NonNullList<ItemStack> newStackList = NonNullList.<ItemStack>create();
		for (final ItemStack stack : stackList) {
			newStackList.add(stack.copy());
		}
		return newStackList;
	}

	public static boolean isStackListEmpty(final NonNullList<ItemStack> stackList) {
		for (final ItemStack stack : stackList) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static ArrayList<PotionChemical> pushTogetherChemicals(final ArrayList<PotionChemical> oldList) {
		final ArrayList<PotionChemical> list = new ArrayList<>();
		for (final PotionChemical chemical : oldList) {
			list.add(chemical.copy());
		}
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i) == null) {
				continue;
			}
			// spot for move
			for (int j = 0; j < i; j++) {
				// empty spot
				if (list.get(j) == null) {
					list.set(j, list.get(i));
					list.set(j, null);
					break;
				} // same stack
				else if (list.get(j).sameAs(list.get(i))) {
					list.get(j).amount += list.get(i).amount;
					list.set(i, null);
					break;
				}
			}
		}
		list.removeAll(Collections.singleton(null));
		return list;
	}

	public static boolean itemStackMatchesChemical(final ItemStack itemstack, final PotionChemical potionChemical) {
		return itemStackMatchesChemical(itemstack, potionChemical, 1);
	}

	public static boolean itemStackMatchesChemical(final ItemStack itemstack, final PotionChemical potionChemical, final int factor) {
		if (potionChemical instanceof Element && itemstack.getItem() == ModItems.element) {
			final Element element = (Element) potionChemical;
			return itemstack.getItemDamage() == element.element.atomicNumber() && itemstack.getCount() >= element.amount * factor;
		}
		if (potionChemical instanceof Molecule && itemstack.getItem() == ModItems.molecule) {
			final Molecule molecule = (Molecule) potionChemical;
			return itemstack.getItemDamage() == molecule.molecule.id() && itemstack.getCount() >= molecule.amount * factor;
		}
		return false;
	}

	public static ItemStack chemicalToItemStack(final PotionChemical potionChemical, final int amount) {
		if (potionChemical instanceof Element) {
			return new ItemStack(ModItems.element, amount, ((Element) potionChemical).element.atomicNumber());
		}
		else if (potionChemical instanceof Molecule) {
			return new ItemStack(ModItems.molecule, amount, ((Molecule) potionChemical).molecule.id());
		}
		return ItemStack.EMPTY;
	}

	public static PotionChemical[] stackListToChemicalArray(final List<ItemStack> stackList) {
		final PotionChemical[] chemicalList = new PotionChemical[stackList.size()];
		for (int i = 0; i < stackList.size(); i++) {
			chemicalList[i] = itemStackToChemical(stackList.get(i));
		}
		return chemicalList;
	}

	public static PotionChemical itemStackToChemical(@Nonnull final ItemStack itemstack) {
		if (isStackAnElement(itemstack)) {
			if (itemstack.getItemDamage() == 0) {
				return null;
			}
			return new Element(getElement(itemstack), itemstack.getCount());
		}
		else if (isStackAMolecule(itemstack)) {
			return new Molecule(getMolecule(itemstack), itemstack.getCount());
		}
		return null;
	}

	public static ItemStack getBucketForChemical(final MinechemChemicalType type) {
		for (final Block block : ModFluids.getFluidBlocks()) {
			if (block instanceof BlockFluidMinechem && ((BlockFluidMinechem) block).getFluid() instanceof FluidMinechem) {
				final BlockFluidMinechem fluidBlock = (BlockFluidMinechem) block;
				final MinechemChemicalType blockType = ((FluidMinechem) fluidBlock.getFluid()).getChemical();
				if (blockType instanceof MoleculeEnum && ((MoleculeEnum) blockType).name().equals("water")) {
					return new ItemStack(Items.WATER_BUCKET);
				}
				else {
					if (type == blockType) {
						return FluidUtil.getFilledBucket(new FluidStack(fluidBlock.getFluid(), 1000));
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static ElementEnum getElement(final ItemStack stack) {
		return stack.getItem() == ModItems.element && stack.getItemDamage() != 0 ? ElementEnum.getByID(stack.getItemDamage()) : null;
	}

	public static MoleculeEnum getMolecule(final ItemStack itemstack) {
		final int itemDamage = itemstack.getItemDamage();
		MoleculeEnum mol = MoleculeEnum.getById(itemDamage);
		if (mol == null) {
			itemstack.setItemDamage(0);
			mol = MoleculeEnum.getById(0);
		}
		return mol;
	}

	public static ItemStack getBucketForFluid(final Fluid fluid) {
		return FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
	}

	public static int getElementIDFromBucket(@Nonnull final ItemStack bucket) {
		if (bucket.getItem() instanceof UniversalBucket) {
			final Fluid fluid = ((UniversalBucket) bucket.getItem()).getFluid(bucket).getFluid();
			if (fluid instanceof FluidElement) {
				final FluidElement fluidElement = (FluidElement) fluid;
				if (fluidElement.getChemical() instanceof ElementEnum) {
					return ((ElementEnum) fluidElement.getChemical()).atomicNumber();
				}
			}
		}
		return -1;
	}

	public static int getMoleculeIDFromBucket(@Nonnull final ItemStack bucket) {
		if (bucket.getItem() instanceof UniversalBucket) {
			final Fluid fluid = ((UniversalBucket) bucket.getItem()).getFluid(bucket).getFluid();
			if (fluid instanceof FluidMolecule) {
				final FluidMolecule fluidElement = (FluidMolecule) fluid;
				if (fluidElement.getChemical() instanceof MoleculeEnum) {
					return ((MoleculeEnum) fluidElement.getChemical()).id();
				}
			}
		}
		return -1;
	}

	public static MinechemChemicalType getChemicalTypeFromBucket(@Nonnull final ItemStack bucket) {
		if (bucket.getItem() instanceof UniversalBucket) {
			final Fluid fluid = ((UniversalBucket) bucket.getItem()).getFluid(bucket).getFluid();
			if (fluid instanceof FluidMinechem) {
				return ((FluidMinechem) fluid).getChemical();
			}
		}
		return null;
	}

	/*
	 * Opens passed in URL, MUST check
	 * FMLClientHandler.instance().getClient(),mc.gameSettings.chatLinksPrompt
	 * before using.
	 */
	public static void openURL(final String url) {
		try {
			final Class<?> oclass = Class.forName("java.awt.Desktop");
			final Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
			oclass.getMethod("browse", new Class[] {
					URI.class
			}).invoke(object, new Object[] {
					new URI(url)
			});
		}
		catch (final Throwable throwable) {
			ModLogger.debug("Couldn\'t open link: " + url);
		}
	}

	public static boolean stacksAreSameKind(final ItemStack is1, final ItemStack is2) {
		boolean damageMatters = false;
		if (MinechemUtil.isStackAnElement(is1) && MinechemUtil.isStackAnElement(is2)) {
			damageMatters = true;
		}
		final int dmg1 = is1.getItemDamage();
		final int dmg2 = is2.getItemDamage();
		if (damageMatters) {
			if (dmg1 != dmg2) {
				return false;
			}
		}
		final NBTTagCompound nbt1 = is1.serializeNBT();
		final NBTTagCompound nbt2 = is2.serializeNBT();
		boolean sameNBT = true;
		if (nbt1 != null && nbt2 != null) {
			sameNBT = nbt1.equals(nbt2);
		}
		return is1.getItem() == is2.getItem() && sameNBT;
	}

	public static boolean isStackAChemical(final ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemElement || itemstack.getItem() instanceof ItemMolecule;
	}

	public static boolean isStackAnElement(final ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemElement && !isStackAnEmptyTestTube(itemstack);
	}

	public static boolean isStackAMolecule(final ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemMolecule && !isStackAnEmptyTestTube(itemstack);
	}

	public static boolean isStackAnEmptyTestTube(final ItemStack itemstack) {
		return itemstack.toString().contains("minechem.itemTestTube");
	}

	public static String stringSieve(final String str) {
		return str.toLowerCase().trim() // remove trailing whitespace
				.replaceAll("\\s", "") // replace spaces
				.replaceAll("block", "").replaceAll("item", "");
	}

}
