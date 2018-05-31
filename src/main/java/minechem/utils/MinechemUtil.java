package minechem.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import minechem.fluid.FluidElement;
import minechem.fluid.FluidMolecule;
import minechem.fluid.MinechemFluid;
import minechem.fluid.MinechemFluidBlock;
import minechem.init.ModConfig;
import minechem.init.ModFluids;
import minechem.init.ModItems;
import minechem.init.ModLogger;
import minechem.item.ItemElement;
import minechem.item.ItemMolecule;
import minechem.item.MinechemChemicalType;
import minechem.item.element.Element;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;
import net.minecraftforge.oredict.OreDictionary;

public final class MinechemUtil {

	public static final Random random = new Random();

	private MinechemUtil() {
	}

	public static ItemStack addItemToInventory(IInventory inventory, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		for (int i = 0, l = inventory.getSizeInventory(); i < l; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) {
				int append = itemStack.getCount() > inventory.getInventoryStackLimit() ? inventory.getInventoryStackLimit() : itemStack.getCount();
				ItemStack newStack = itemStack.copy();
				newStack.setCount(append);
				inventory.setInventorySlotContents(i, newStack);
				itemStack.shrink(append);
			}
			else if (stack.getItem() == itemStack.getItem() && stack.getItemDamage() == itemStack.getItemDamage()) {
				int free = inventory.getInventoryStackLimit() - stack.getCount();
				int append = itemStack.getCount() > free ? free : itemStack.getCount();
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

	public static void throwItemStack(World world, ItemStack itemStack, double x, double y, double z) {
		if (!itemStack.isEmpty()) {
			float f = random.nextFloat() * 0.8F + 0.1F;
			float f1 = random.nextFloat() * 0.8F + 0.1F;
			float f2 = random.nextFloat() * 0.8F + 0.1F;

			EntityItem entityitem = new EntityItem(world, (float) x + f, (float) y + f1, (float) z + f2, itemStack);
			float f3 = 0.05F;
			entityitem.motionX = (float) random.nextGaussian() * f3;
			entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
			entityitem.motionZ = (float) random.nextGaussian() * f3;
			world.spawnEntity(entityitem);
		}
	}

	public static ItemStack createItemStack(MinechemChemicalType chemical, int amount) {
		ItemStack itemStack = ItemStack.EMPTY;
		if (chemical instanceof ElementEnum) {
			itemStack = ItemElement.createStackOf(ElementEnum.getByID(((ElementEnum) chemical).atomicNumber()), amount);
		}
		else if (chemical instanceof MoleculeEnum) {
			itemStack = new ItemStack(ModItems.molecule, amount, ((MoleculeEnum) chemical).id());
		}
		return itemStack;
	}

	public static boolean canDrain(World world, Block block, int x, int y, int z) {
		if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && world.getBlockState(new BlockPos(x, y, z)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(x, y, z))) == 0) {
			return true;
		}
		else if (block instanceof IFluidBlock) {
			return ((IFluidBlock) block).canDrain(world, new BlockPos(x, y, z));
		}

		return false;
	}

	public static MinechemChemicalType getChemical(Block block) {
		MinechemChemicalType chemical = null;
		if (block instanceof IFluidBlock) {
			Fluid fluid = ((IFluidBlock) block).getFluid();
			chemical = getChemical(fluid);
		}
		else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
			chemical = MoleculeEnum.water;
		}

		return chemical;
	}

	public static MinechemChemicalType getChemical(Fluid fluid) {
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

	public static ElementEnum getElement(Fluid fluid) {
		for (Map.Entry<ElementEnum, FluidElement> entry : ModFluids.FLUID_ELEMENTS.entrySet()) {
			if (entry.getValue() == fluid) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static MoleculeEnum getMolecule(Fluid fluid) {
		for (Entry<MoleculeEnum, Fluid> entry : ModFluids.FLUID_MOLECULES.entrySet()) {
			if (entry.getValue() == fluid) {
				return entry.getKey();
			}
		}
		return null;
	}

	// TODO EH LOL PTET CA MARCHE PAS
	public static Fluid getFluid(IFluidTank te) {
		////FluidTankInfo[] tanks = null;
		for (int i = 0; i < 6; i++) {
			FluidTankInfo tank = te.getInfo();
			if (tank != null) {
				//for (FluidTankInfo tank : tanks) {
				if (tank != null && tank.fluid != null) {
					return tank.fluid.getFluid();
				}
				//}
			}
		}
		return null;
	}

	public static void scanForMoreStacks(ItemStack current, EntityPlayer player) {
		int getMore = 8 - current.getCount();
		InventoryPlayer inventory = player.inventory;
		int maxSlot = player.inventory.getSizeInventory() - 4;
		int slot = 0;
		do {
			if (slot != inventory.currentItem) {
				ItemStack slotStack = inventory.getStackInSlot(slot);
				if (!slotStack.isEmpty() && slotStack.isItemEqual(current)) {
					ItemStack addStack = inventory.decrStackSize(slot, getMore);
					current.grow(addStack.getCount());
					getMore -= addStack.getCount();
				}
			}
			slot++;
		}
		while (getMore > 0 && slot < maxSlot);
	}

	public static void incPlayerInventory(ItemStack current, int inc, EntityPlayer player, ItemStack give) {
		if (inc < 0) {
			current.splitStack(-inc);
		}
		else if (inc > 0) {
			if (current.getCount() + inc <= current.getMaxStackSize()) {
				current.grow(inc);
			}
			else {
				int added = current.getMaxStackSize() - current.getCount();
				current.setCount(current.getMaxStackSize());
				ItemStack extraStack = current.copy();
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

	public static Set<ItemStack> findItemStacks(IInventory inventory, Item item, int damage) {
		Set<ItemStack> stacks = new HashSet<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == item && stack.getItemDamage() == damage) {
				stacks.add(stack);
			}
		}

		return stacks;
	}

	public static void removeStackInInventory(IInventory inventory, ItemStack stack) {
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

	public static void addDisabledStacks(String[] stringInputs, ArrayList<ItemStack> decomposerBlacklist, ArrayList<String> ids) {
		for (String string : stringInputs) {
			if (string == null || string.equals("")) {
				continue;
			}
			String[] splitString = string.split(":");
			ArrayList<String> wildcardMatch = new ArrayList<String>();
			if (splitString.length < 2 || splitString.length > 3) {
				ModLogger.debug(string + " is an invalid blacklist input");
				continue;
			}
			if (splitString[0].equals("ore")) {
				String itemID = splitString[1];
				if (itemID.contains("*")) {
					itemID = itemID.replaceAll("\\*", ".*");
				}
				Pattern itemPattern = Pattern.compile(itemID, Pattern.CASE_INSENSITIVE);
				for (String item : OreDictionary.getOreNames()) {
					if (itemPattern.matcher(item).matches()) {
						wildcardMatch.add(item);
					}
				}
				if (wildcardMatch.isEmpty()) {
					ModLogger.debug(splitString[1] + " has no matches in the OreDictionary");
					continue;
				}
				for (String key : wildcardMatch) {
					decomposerBlacklist.addAll(OreDictionary.getOres(key));
				}
			}
			else {
				int meta;
				try {
					meta = splitString.length == 3 ? Integer.valueOf(splitString[2]) : Short.MAX_VALUE;
				}
				catch (NumberFormatException e) {
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
				Pattern itemPattern = Pattern.compile(itemID, Pattern.CASE_INSENSITIVE);
				for (String item : ids) {
					if (itemPattern.matcher(item).matches()) {
						wildcardMatch.add(item);
					}
				}
				if (wildcardMatch.isEmpty()) {
					ModLogger.debug(string + " has no matches in the ItemRegistry");
					continue;
				}
				for (String key : wildcardMatch) {
					Object disable = Item.REGISTRY.getObject(new ResourceLocation(key));
					if (disable instanceof Item) {
						decomposerBlacklist.add(new ItemStack(((Item) disable), 1, meta));
					}
					else if (disable instanceof Block) {
						decomposerBlacklist.add(new ItemStack(((Block) disable), 1, meta));
					}
				}
			}
		}
	}

	public static void populateBlacklists() {
		ModConfig.decomposerBlacklist = new ArrayList<ItemStack>();
		ModConfig.synthesisBlacklist = new ArrayList<ItemStack>();
		ModConfig.decomposerBlacklist.add(ModItems.emptyTube);
		ArrayList<String> registeredItems = new ArrayList<String>();
		for (ResourceLocation key : Item.REGISTRY.getKeys()) {
			registeredItems.add(key.toString());
		}
		addDisabledStacks(ModConfig.DecomposerBlacklist, ModConfig.decomposerBlacklist, registeredItems);
		addDisabledStacks(ModConfig.SynthesisMachineBlacklist, ModConfig.synthesisBlacklist, registeredItems);
	}

	@SideOnly(Side.CLIENT)
	public static int getSplitStringHeight(FontRenderer fontRenderer, String string, int width) {
		List<String> stringRows = fontRenderer.listFormattedStringToWidth(string, width);
		return stringRows.size() * fontRenderer.FONT_HEIGHT;
	}

	public static float translateValue(float value, float leftMin, float leftMax, float rightMin, float rightMax) {
		float leftRange = leftMax - leftMin;
		float rightRange = rightMax - rightMin;
		float valueScaled = (value - leftMin) / leftRange;
		return rightMin + (valueScaled * rightRange);
	}

	public static String getLocalString(String key) {
		return getLocalString(key, false);
	}

	public static String getLocalString(String key, boolean capitalize) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			//LanguageMap languageMap = new LanguageMap();
			//String localString = languageMap.translateKey(key);
			String localString = I18n.translateToLocal(key);
			return capitalize ? capitalizeFully(localString.replaceAll("molecule\\.", "")) : localString;
		}
		return key;
	}

	public static String capitalizeFully(String input) {
		String[] splitString = input.split(" ");
		String result = "";
		for (int i = 0; i < splitString.length; i++) {
			char[] digit = splitString[i].toCharArray();
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

	public static NBTTagList writeItemStackListToTagList(NonNullList<ItemStack> list) {
		NBTTagList taglist = new NBTTagList();
		for (ItemStack itemstack : list) {
			NBTTagCompound itemstackCompound = new NBTTagCompound();
			itemstack.writeToNBT(itemstackCompound);
			taglist.appendTag(itemstackCompound);
		}
		return taglist;
	}

	public static NonNullList<ItemStack> readTagListToItemStackList(NBTTagList taglist) {
		NonNullList<ItemStack> itemlist = NonNullList.<ItemStack>withSize(taglist.tagCount(), ItemStack.EMPTY);
		for (int i = 0; i < taglist.tagCount(); i++) {
			NBTTagCompound itemstackCompound = taglist.getCompoundTagAt(i);
			ItemStack itemstack = new ItemStack(itemstackCompound);
			itemlist.set(i, itemstack);
		}
		return itemlist;
	}

	public static NonNullList<ItemStack> convertChemicalsIntoItemStacks(ArrayList<PotionChemical> potionChemicals) {
		if (potionChemicals != null) {
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(potionChemicals.size(), ItemStack.EMPTY);
			if (potionChemicals != null && potionChemicals.size() > 0) {
				for (int i = 0; i < potionChemicals.size(); i++) {
					PotionChemical potionChemical = potionChemicals.get(i);
					//for (PotionChemical potionChemical : potionChemicals) {
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

	public static NonNullList<ItemStack> pushTogetherStacks(NonNullList<ItemStack> stacks) {
		// i slot to move
		for (int i = stacks.size() - 1; i >= 0; i--) {
			if (stacks.get(i).isEmpty()) {
				continue;
			}
			// spot for move
			for (int j = 0; j < i; j++) {
				// empty spot
				if (stacks.get(j).isEmpty()) {
					stacks.set(j, stacks.get(i));
					stacks.set(j, ItemStack.EMPTY);
					break;
				} // same stack
				else if (stacks.get(j).isItemEqual(stacks.get(i))) {
					stacks.get(j).grow(stacks.get(i).getCount());
					stacks.set(i, ItemStack.EMPTY);
					break;
				}
			}
		}
		stacks.removeAll(Collections.singleton(ItemStack.EMPTY));
		return stacks;
	}

	public static NonNullList<ItemStack> convertChemicalArrayIntoItemStackArray(PotionChemical[] chemicals) {
		if (chemicals == null) {
			return NonNullList.<ItemStack>create();
		}

		NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(chemicals.length, ItemStack.EMPTY);
		for (int i = 0; i < chemicals.length; i++) {
			PotionChemical potionChemical = chemicals[i];
			if (potionChemical instanceof Element) {
				stacks.set(i, new ItemStack(ModItems.element, potionChemical.amount, ((Element) potionChemical).element.atomicNumber()));
			}
			else if (potionChemical instanceof Molecule) {
				stacks.set(i, new ItemStack(ModItems.molecule, potionChemical.amount, ((Molecule) potionChemical).molecule.id()));
			}
		}
		return stacks;
	}

	public static ArrayList<PotionChemical> pushTogetherChemicals(ArrayList<PotionChemical> oldList) {
		ArrayList<PotionChemical> list = new ArrayList<PotionChemical>();
		for (PotionChemical chemical : oldList) {
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

	public static boolean itemStackMatchesChemical(ItemStack itemstack, PotionChemical potionChemical) {
		return itemStackMatchesChemical(itemstack, potionChemical, 1);
	}

	public static boolean itemStackMatchesChemical(ItemStack itemstack, PotionChemical potionChemical, int factor) {
		if (potionChemical instanceof Element && itemstack.getItem() == ModItems.element) {
			Element element = (Element) potionChemical;
			return (itemstack.getItemDamage() == element.element.atomicNumber()) && (itemstack.getCount() >= element.amount * factor);
		}
		if (potionChemical instanceof Molecule && itemstack.getItem() == ModItems.molecule) {
			Molecule molecule = (Molecule) potionChemical;
			return (itemstack.getItemDamage() == molecule.molecule.id()) && (itemstack.getCount() >= molecule.amount * factor);
		}
		return false;
	}

	public static EnumFacing getDirectionFromFacing(int facing) {
		switch (facing) {
		case 0:
			return EnumFacing.SOUTH;
		case 1:
			return EnumFacing.WEST;
		case 2:
			return EnumFacing.NORTH;
		case 3:
			return EnumFacing.EAST;
		default:
			return null;
		}
	}

	/**
	 * Ensures that the given inventory is the full inventory, i.e. takes double chests into account.
	 *
	 * @param inv
	 * @return Modified inventory if double chest, unmodified otherwise. Credit to Buildcraft.
	 */
	public static IInventory getInventory(ILockableContainer inv) {
		if (inv instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest) inv;
			Position pos = new Position(chest.getPos().getX(), chest.getPos().getY(), chest.getPos().getZ());
			TileEntity tile;
			ILockableContainer chest2 = null;
			tile = getTile(chest.getWorld(), pos, EnumFacing.WEST);
			if (tile instanceof TileEntityChest) {
				chest2 = (ILockableContainer) tile;
			}
			tile = getTile(chest.getWorld(), pos, EnumFacing.EAST);
			if (tile instanceof TileEntityChest) {
				chest2 = (ILockableContainer) tile;
			}
			tile = getTile(chest.getWorld(), pos, EnumFacing.NORTH);
			if (tile instanceof TileEntityChest) {
				chest2 = (ILockableContainer) tile;
			}
			tile = getTile(chest.getWorld(), pos, EnumFacing.SOUTH);
			if (tile instanceof TileEntityChest) {
				chest2 = (ILockableContainer) tile;
			}
			if (chest2 != null) {
				return new InventoryLargeChest("", inv, chest2);
			}
		}
		return inv;
	}

	public static TileEntity getTile(World world, Position pos, EnumFacing dir) {
		Position tmp = new Position(pos);
		tmp.orientation = dir;
		tmp.moveForwards(1.0);

		return world.getTileEntity(new BlockPos((int) tmp.x, (int) tmp.y, (int) tmp.z));
	}

	@SideOnly(Side.SERVER)
	public static WorldServer getDimension(int dimensionID) {
		WorldServer[] worlds = FMLServerHandler.instance().getServer().worlds;
		for (WorldServer world : worlds) {
			if (world.provider.getDimension() == dimensionID) {
				return world;
			}
		}
		return null;
	}

	public static String getChemicalName(PotionChemical potionChemical) {
		if (potionChemical instanceof Element) {
			return getLocalString(((Element) potionChemical).element.name(), true);
		}
		else {
			return getLocalString(((Molecule) potionChemical).molecule.name(), true);
		}
	}

	public static ItemStack chemicalToItemStack(PotionChemical potionChemical, int amount) {
		if (potionChemical instanceof Element) {
			return new ItemStack(ModItems.element, amount, ((Element) potionChemical).element.atomicNumber());
		}
		else if (potionChemical instanceof Molecule) {
			return new ItemStack(ModItems.molecule, amount, ((Molecule) potionChemical).molecule.id());
		}
		return ItemStack.EMPTY;
	}

	public static PotionChemical itemStackToChemical(@Nonnull ItemStack itemstack) {
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

	public static int getNumberOfDigits(int n) {
		return (int) (Math.log10(n) + 1);
	}

	public static ItemStack getBucketForChemical(MinechemChemicalType type) {
		for (Block block : ModFluids.getFluidBlocks()) {
			if (block instanceof MinechemFluidBlock && ((MinechemFluidBlock) block).getFluid() instanceof MinechemFluid) {
				MinechemFluidBlock fluidBlock = (MinechemFluidBlock) block;
				MinechemChemicalType blockType = ((MinechemFluid) fluidBlock.getFluid()).getChemical();
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

	public static ElementEnum getElement(ItemStack stack) {
		return (stack.getItem() == ModItems.element && stack.getItemDamage() != 0) ? ElementEnum.getByID(stack.getItemDamage()) : null;
	}

	public static MoleculeEnum getMolecule(ItemStack itemstack) {
		int itemDamage = itemstack.getItemDamage();
		MoleculeEnum mol = MoleculeEnum.getById(itemDamage);
		if (mol == null) {
			itemstack.setItemDamage(0);
			mol = MoleculeEnum.getById(0);
		}
		return mol;
	}

	public static ItemStack getBucketForFluid(Fluid fluid) {
		return FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
	}

	public static int getElementIDFromBucket(@Nonnull ItemStack bucket) {
		if (bucket.getItem() instanceof UniversalBucket) {
			Fluid fluid = ((UniversalBucket) bucket.getItem()).getFluid(bucket).getFluid();
			if (fluid instanceof FluidElement) {
				FluidElement fluidElement = (FluidElement) fluid;
				if (fluidElement.getChemical() instanceof ElementEnum) {
					return ((ElementEnum) fluidElement.getChemical()).atomicNumber();
				}
			}
		}
		return -1;
	}

	public static int getMoleculeIDFromBucket(@Nonnull ItemStack bucket) {
		if (bucket.getItem() instanceof UniversalBucket) {
			Fluid fluid = ((UniversalBucket) bucket.getItem()).getFluid(bucket).getFluid();
			if (fluid instanceof FluidMolecule) {
				FluidMolecule fluidElement = (FluidMolecule) fluid;
				if (fluidElement.getChemical() instanceof MoleculeEnum) {
					return ((MoleculeEnum) fluidElement.getChemical()).id();
				}
			}
		}
		return -1;
	}

	public static MinechemChemicalType getChemicalTypeFromBucket(@Nonnull ItemStack bucket) {
		if (bucket.getItem() instanceof UniversalBucket) {
			Fluid fluid = ((UniversalBucket) bucket.getItem()).getFluid(bucket).getFluid();
			if (fluid instanceof MinechemFluid) {
				return ((MinechemFluid) fluid).getChemical();
			}
		}
		return null;
	}

	/*
	 * Opens passed in URL, MUST check
	 * FMLClientHandler.instance().getClient(),mc.gameSettings.chatLinksPrompt
	 * before using.
	 */
	public static void openURL(String url) {
		try {
			Class<?> oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
			oclass.getMethod("browse", new Class[] {
					URI.class
			}).invoke(object, new Object[] {
					new URI(url)
			});
		}
		catch (Throwable throwable) {
			ModLogger.debug("Couldn\'t open link: " + url);
		}
	}

	public static boolean stacksAreSameKind(ItemStack is1, ItemStack is2) {
		int dmg1 = is1.getItemDamage();
		int dmg2 = is2.getItemDamage();
		NBTTagCompound nbt1 = is1.getTagCompound();
		NBTTagCompound nbt2 = is2.getTagCompound();
		boolean sameNBT = true;
		if (nbt1 != null && nbt2 != null) {
			sameNBT = nbt1.equals(nbt2);
		}
		return is1.getItem() == is2.getItem() && sameNBT;
	}

	public static boolean isStackAChemical(ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemElement || itemstack.getItem() instanceof ItemMolecule;
	}

	public static boolean isStackAnElement(ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemElement && !isStackAnEmptyTestTube(itemstack);
	}

	public static boolean isStackAMolecule(ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemMolecule && !isStackAnEmptyTestTube(itemstack);
	}

	public static boolean isStackAnEmptyTestTube(ItemStack itemstack) {
		return itemstack.toString().contains("minechem.itemTestTube");
	}

	public static String stringSieve(String str) {
		return str.toLowerCase().trim() // remove trailing whitespace
				.replaceAll("\\s", "") // replace spaces
				.replaceAll("block", "").replaceAll("item", "");
	}

}
