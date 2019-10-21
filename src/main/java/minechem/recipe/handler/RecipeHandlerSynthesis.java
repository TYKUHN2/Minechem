package minechem.recipe.handler;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.ints.*;
import minechem.api.recipe.ISynthesisRecipe;
import minechem.init.ModGlobals;
import minechem.init.ModRegistries;
import minechem.item.ItemElement;
import minechem.item.ItemMolecule;
import minechem.item.element.Element;
import minechem.item.molecule.Molecule;
import minechem.potion.PotionChemical;
import minechem.recipe.*;
import minechem.recipe.RecipeSynthesisShaped.MinechemShapedPrimer;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.*;

public class RecipeHandlerSynthesis {

	private static final String PREFIX_SHAPED = "synthesis_shaped_";
	private static final String PREFIX_SHAPELESS = "synthesis_shapeless_";

	public static RecipeHandlerSynthesis instance = new RecipeHandlerSynthesis();

	public static boolean itemStacksMatchesShapelessRecipe(final NonNullList<ItemStack> stacks, final RecipeSynthesisShapeless recipe, final int factor) {
		final NonNullList<ItemStack> stacksCopy = MinechemUtil.copyStackList(stacks);
		final NonNullList<ItemStack> stackList = NonNullList.<ItemStack>create();
		for (final SingleItemStackBasedIngredient ingredient : recipe.getSingleIngredients()) {
			stackList.add(ingredient.getIngredientStack());
		}
		if (stacksCopy.size() == stackList.size()) {
			for (int i = 0; i < stackList.size(); i++) {
				for (int j = 0; j < stacksCopy.size(); j++) {
					if (!stackList.get(i).isEmpty() && MinechemUtil.stacksAreSameKind(stacksCopy.get(j), stackList.get(i))/*ItemStack.areItemStacksEqual(stacksCopy.get(i), shapelessRecipe.get(i))*/ && stackList.get(i).getCount() == stacksCopy.get(j).getCount()) {
						stackList.set(i, ItemStack.EMPTY);
						break;
					}
				}
			}
			if (MinechemUtil.isStackListEmpty(stackList)) {
				return true;
			}
		}
		return false;
	}

	public static boolean itemStacksMatchesShapedRecipe(final NonNullList<ItemStack> stacks, final RecipeSynthesisShaped recipe, final int factor) {
		final PotionChemical[] chemicals = MinechemUtil.stackListToChemicalArray(stacks);
		final NonNullList<ItemStack> recipeStacks = RecipeUtil.ingredientListToStackList(recipe.getIngredients());
		for (int i = 0; i < chemicals.length; i++) {
			if (stacks.get(i).isEmpty() && chemicals[i] == null) {
				continue;
			}
			if (stacks.get(i).isEmpty() || chemicals[i] == null) {
				return false;
			}
			if (!MinechemUtil.itemStackMatchesChemical(recipeStacks.get(i), chemicals[i], factor) || recipeStacks.get(i).getCount() != chemicals[i].amount) {
				return false;
			}
		}
		return true;
	}

	@Nullable
	public static ISynthesisRecipe findMatchingRecipe(final InventoryCrafting craftMatrix, final World worldIn) {
		for (final ISynthesisRecipe irecipe : ModRegistries.SYNTHESIS_RECIPES) {
			if (irecipe.matches(craftMatrix, worldIn)) {
				return irecipe;
			}
		}

		return null;
	}

	private static NonNullList<ItemStack> getListFromRecipe(final ISynthesisRecipe recipe) {
		final NonNullList<ItemStack> tmpList = NonNullList.create();
		final NonNullList<SingleItemStackBasedIngredient> ingredients = recipe.getSingleIngredients();
		for (final SingleItemStackBasedIngredient ingredient : ingredients) {
			tmpList.add(ingredient.getIngredientStack());
		}
		return tmpList;
	}

	public static ISynthesisRecipe getRecipeFromInput(final NonNullList<ItemStack> stackList) {
		ISynthesisRecipe matchedRecipe = null;
		for (final Map.Entry<ResourceLocation, ISynthesisRecipe> recipeEntry : ModRegistries.SYNTHESIS_RECIPES.getEntries()) {
			final ISynthesisRecipe recipe = recipeEntry.getValue();
			final NonNullList<ItemStack> recipeList = getListFromRecipe(recipe);
			if (recipe.isShaped()) {
				if (recipeList.size() != stackList.size()) {
					continue;
				}
				ItemStack currentStack = ItemStack.EMPTY;
				for (int i = 0; i < stackList.size(); i++) {
					currentStack = stackList.get(i);
					if (!MinechemUtil.stacksAreSameKind(currentStack, recipeList.get(i)) || currentStack.getCount() != recipeList.get(i).getCount()) {
						matchedRecipe = null;
						continue;
					}
					matchedRecipe = recipe;
					break;
				}
			}
			else {
				if (recipeList.size() != stackList.size()) {
					continue;
				}
				ItemStack currentStack = ItemStack.EMPTY;
				for (int i = 0; i < stackList.size(); i++) {
					currentStack = stackList.get(i);
					if (!MinechemUtil.stacksAreSameKind(currentStack, recipeList.get(i)) || currentStack.getCount() != recipeList.get(i).getCount()) {
						matchedRecipe = null;
						continue;
					}
					matchedRecipe = recipe;
					break;
				}
			}
		}
		return matchedRecipe;
	}

	private static <K extends IForgeRegistryEntry<ISynthesisRecipe>> ISynthesisRecipe register(final ISynthesisRecipe object) {
		return GameData.register_impl(object);
	}

	public static void addShapedRecipe(final ResourceLocation registryName, final int energyCost, final ItemStack result, final Object... recipe) {
		final MinechemShapedPrimer primer = RecipeSynthesisShaped.parseShaped(recipe);
		final ISynthesisRecipe newRecipe = new RecipeSynthesisShaped(primer.width, primer.height, energyCost, primer.input, result).setRegistryName(registryName);
		register(newRecipe);
	}

	public static void addShapedRecipe(final String name, final int energyCost, final ItemStack result, final Object... recipe) {
		final String regDomain = name.split(":").length == 1 ? ModGlobals.MODID : name.split(":")[1];
		final String regPath = name.split(":").length == 1 ? name : name.split(":")[1];
		addShapedRecipe(new ResourceLocation(regDomain, PREFIX_SHAPED + "" + regPath), energyCost, result, recipe);
	}

	public static void addShapelessRecipe(final String name, final int energyCost, @Nonnull final ItemStack output, final PotionChemical... recipe) {
		final NonNullList<ItemStack> lst = NonNullList.create();
		for (final Object ingredient : recipe) {
			if (ingredient instanceof PotionChemical) {
				final PotionChemical chemical = (PotionChemical) ingredient;
				lst.add(MinechemUtil.chemicalToItemStack(chemical, chemical.amount));//RecipeUtil.getIngredient(ingredient));
			}
		}
		final NonNullList<SingleItemStackBasedIngredient> dumbList = SingleItemStackBasedIngredient.fromItemStacks(lst.toArray(new ItemStack[lst.size()]));
		final ISynthesisRecipe newRecipe = new RecipeSynthesisShapeless(output, energyCost, dumbList).setRegistryName(new ResourceLocation(ModGlobals.MODID, PREFIX_SHAPELESS + "" + name));
		register(newRecipe);
	}

	public static void removeShapedRecipe(final ItemStack stack) {
		if (stack.getItem() instanceof ItemElement || stack.getItem() instanceof ItemMolecule) {
			removeShapedRecipe(MinechemUtil.itemStackToChemical(stack));
		}
	}

	private static void removeShapedRecipe(final PotionChemical recipeOutput) {
		final RecipeSynthesisShaped recipe = getShapedRecipe(recipeOutput);
		if (recipe != null) {
			RecipeUtil.removeRecipe(recipe.getRegistryName());
		}
	}

	public static ISynthesisRecipe getRecipeFromOutput(final ItemStack stack) {
		ISynthesisRecipe recipe = null;
		recipe = getShapedRecipe(stack);
		if (recipe == null) {
			recipe = getShapelessRecipe(stack);
		}
		return recipe;
	}

	public static int getEnergyCost(final ISynthesisRecipe recipe) {
		if (recipe instanceof RecipeSynthesisShaped) {
			return ((RecipeSynthesisShaped) recipe).getEnergyCost();
		}
		if (recipe instanceof RecipeSynthesisShapeless) {
			return ((RecipeSynthesisShapeless) recipe).getEnergyCost();
		}
		return 0;
	}

	public static boolean isShaped(final ISynthesisRecipe recipe) {
		return recipe.isShaped();
	}

	public static boolean isShapeless(final ISynthesisRecipe recipe) {
		return !recipe.isShaped();
	}

	public static PotionChemical[] getChemicalsFromRecipe(final ISynthesisRecipe recipe) {
		final PotionChemical[] returnArray = new PotionChemical[recipe.getIngredients().size()];
		final NonNullList<ItemStack> stackList = RecipeUtil.getRecipeAsStackList(recipe);
		for (int i = 0; i < stackList.size(); i++) {
			final ItemStack stack = stackList.get(i).copy();
			if (MinechemUtil.isStackAnElement(stack)) {
				returnArray[i] = new Element(MinechemUtil.getElement(stack));
			}
			else if (MinechemUtil.isStackAMolecule(stack)) {
				returnArray[i] = new Molecule(MinechemUtil.getMolecule(stack));
			}
			else {
				returnArray[i] = null;
			}
		}
		return returnArray;
	}

	private static RecipeSynthesisShaped getShapedRecipe(final PotionChemical recipeOutput) {
		int amount = 1;
		if (recipeOutput instanceof Element) {
			amount = ((Element) recipeOutput).amount;
		}
		else if (recipeOutput instanceof Molecule) {
			amount = ((Molecule) recipeOutput).amount;
		}
		return getShapedRecipe(MinechemUtil.chemicalToItemStack(recipeOutput, amount));
	}

	private static RecipeSynthesisShaped getShapedRecipe(final ItemStack recipeOutput) {
		final Set<Map.Entry<ResourceLocation, ISynthesisRecipe>> recipes = ModRegistries.SYNTHESIS_RECIPES.getEntries();
		for (final Map.Entry<ResourceLocation, ISynthesisRecipe> recipeEntry : recipes) {
			final ISynthesisRecipe recipe = recipeEntry.getValue();
			if (recipe instanceof RecipeSynthesisShaped) {
				final ItemStack resultStack = recipe.getRecipeOutput();
				if (MinechemUtil.stacksAreSameKind(recipeOutput, resultStack)) {
					return (RecipeSynthesisShaped) recipe;
				}
			}
		}
		return null;
	}

	private static RecipeSynthesisShapeless getShapelessRecipe(final ItemStack recipeOutput) {
		final Set<Map.Entry<ResourceLocation, ISynthesisRecipe>> recipes = ModRegistries.SYNTHESIS_RECIPES.getEntries();
		for (final Map.Entry<ResourceLocation, ISynthesisRecipe> recipeEntry : recipes) {
			final ISynthesisRecipe recipe = recipeEntry.getValue();
			if (recipe instanceof RecipeSynthesisShapeless) {
				final ItemStack resultStack = recipe.getRecipeOutput();
				if (MinechemUtil.stacksAreSameKind(recipeOutput, resultStack)) {
					return (RecipeSynthesisShapeless) recipe;
				}
			}
		}
		return null;
	}

	/*private static RecipeSynthesisShapeless getShapelessRecipe(final PotionChemical recipeOutput) {
		int amount = 1;
		if (recipeOutput instanceof Element) {
			amount = ((Element) recipeOutput).amount;
		}
		else if (recipeOutput instanceof Molecule) {
			amount = ((Molecule) recipeOutput).amount;
		}
		return getShapelessRecipe(MinechemUtil.chemicalToItemStack(recipeOutput, amount));
	}*/

	public static void addShapelessOreDictRecipe(final String item, final int energyCost, final PotionChemical... chemicals) {
		final List<ItemStack> oreDictEntries = OreDictionary.getOres(item);
		int entry = 0;
		for (final Iterator<ItemStack> itr = oreDictEntries.iterator(); itr.hasNext() && entry < 8; entry++) {
			final PotionChemical[] val = new PotionChemical[9];
			for (int i = 0; i < chemicals.length; i++) {
				val[(i + entry) % 9] = chemicals[i];
			}
			final ItemStack ore = itr.next();
			addShapelessRecipe(ore.getItem().getUnlocalizedName(), energyCost, new ItemStack(ore.getItem(), 1, ore.getItemDamage()), val);
		}
	}

	public static void addShapedOreDictRecipe(final String item, final int energyCost, final PotionChemical... chemicals) {
		final List<ItemStack> oreDictEntries = OreDictionary.getOres(item);
		//int entry = 0;
		//for (Iterator<ItemStack> itr = oreDictEntries.iterator(); itr.hasNext() && entry < 8; entry++) {
		if (!oreDictEntries.isEmpty()) {
			final ItemStack ore = oreDictEntries.get(0);
			final ResourceLocation regName = ore.getItem().getRegistryName();
			addShapedRecipe(regName.getResourceDomain() + "_" + regName.getResourcePath() + "_" + ore.getItemDamage(), energyCost, new ItemStack(ore.getItem(), 1, ore.getItemDamage()), createShapedObject(chemicals));
		}
	}

	private static Object[] createShapedObject(final PotionChemical[] chemicals) {
		int currentSymbolIndex = 'a';
		final String[] pattern = new String[3];
		final List<Object> symbolDefs = new ArrayList<>();
		// String pattern
		for (int i = 0; i < 3; i++) {
			String rowStr = "";
			for (int j = 0; j < 3; j++) {
				if (chemicals[j + i * 3] == null) {
					rowStr += " ";
				}
				else {
					rowStr += (char) currentSymbolIndex;
					symbolDefs.add((char) currentSymbolIndex);
					symbolDefs.add(chemicals[j + i * 3]);
					currentSymbolIndex++;
				}
			}
			pattern[i] = rowStr;
		}
		final Object[] returnObject = new Object[pattern.length + symbolDefs.size()];
		for (int i = 0; i < 3; i++) {
			returnObject[i] = pattern[i];
		}
		for (int i = 3; i < symbolDefs.size() + 3; i++) {
			returnObject[i] = symbolDefs.get(i - 3);
		}
		return returnObject;
	}

	// helper stuff

	public final Int2IntMap itemToCount = new Int2IntOpenHashMap();

	public void accountStack(final ItemStack stack) {
		this.accountStack(stack, -1);
	}

	public void accountStack(final ItemStack stack, final int forceCount) {
		if (!stack.isEmpty() && !stack.isItemDamaged() && !stack.isItemEnchanted() && !stack.hasDisplayName()) {
			final int i = pack(stack);
			final int j = forceCount == -1 ? stack.getCount() : forceCount;
			increment(i, j);
		}
	}

	public static int pack(final ItemStack stack) {
		final Item item = stack.getItem();
		final int i = item.getHasSubtypes() ? stack.getMetadata() : 0;
		return Item.REGISTRY.getIDForObject(item) << 16 | i & 65535;
	}

	public boolean containsItem(final int p_194120_1_) {
		return itemToCount.get(p_194120_1_) > 0;
	}

	public int tryTake(final int p_194122_1_, final int maximum) {
		final int i = itemToCount.get(p_194122_1_);

		if (i >= maximum) {
			itemToCount.put(p_194122_1_, i - maximum);
			return p_194122_1_;
		}
		else {
			return 0;
		}
	}

	private void increment(final int p_194117_1_, final int amount) {
		itemToCount.put(p_194117_1_, itemToCount.get(p_194117_1_) + amount);
	}

	public boolean canCraft(final ISynthesisRecipe recipe, @Nullable final IntList p_194116_2_) {
		return this.canCraft(recipe, p_194116_2_, 1);
	}

	public boolean canCraft(final ISynthesisRecipe recipe, @Nullable final IntList p_194118_2_, final int p_194118_3_) {
		return new RecipeHandlerSynthesis.RecipePicker(recipe).tryPick(p_194118_3_, p_194118_2_);
	}

	public int getBiggestCraftableStack(final ISynthesisRecipe recipe, @Nullable final IntList p_194114_2_) {
		return this.getBiggestCraftableStack(recipe, Integer.MAX_VALUE, p_194114_2_);
	}

	public int getBiggestCraftableStack(final ISynthesisRecipe recipe, final int p_194121_2_, @Nullable final IntList p_194121_3_) {
		return new RecipeHandlerSynthesis.RecipePicker(recipe).tryPickAll(p_194121_2_, p_194121_3_);
	}

	public static ItemStack unpack(final int p_194115_0_) {
		return p_194115_0_ == 0 ? ItemStack.EMPTY : new ItemStack(Item.getItemById(p_194115_0_ >> 16 & 65535), 1, p_194115_0_ & 65535);
	}

	public void clear() {
		itemToCount.clear();
	}

	class RecipePicker {
		private final ISynthesisRecipe recipe;
		private final List<SingleItemStackBasedIngredient> ingredients = Lists.<SingleItemStackBasedIngredient>newArrayList();
		private final int ingredientCount;
		private final int[] possessedIngredientStacks;
		private final int possessedIngredientStackCount;
		private final BitSet data;
		private final IntList path = new IntArrayList();

		public RecipePicker(final ISynthesisRecipe p_i47608_2_) {
			recipe = p_i47608_2_;
			ingredients.addAll(p_i47608_2_.getSingleIngredients());
			ingredients.removeIf((p_194103_0_) -> {
				return p_194103_0_ == SingleItemStackBasedIngredient.EMPTY;
			});
			ingredientCount = ingredients.size();
			possessedIngredientStacks = getUniqueAvailIngredientItems();
			possessedIngredientStackCount = possessedIngredientStacks.length;
			data = new BitSet(ingredientCount + possessedIngredientStackCount + ingredientCount + ingredientCount * possessedIngredientStackCount);

			for (int i = 0; i < ingredients.size(); ++i) {
				final IntList intlist = ingredients.get(i).getValidItemStacksPacked();

				for (int j = 0; j < possessedIngredientStackCount; ++j) {
					if (intlist.contains(possessedIngredientStacks[j])) {
						data.set(getIndex(true, j, i));
					}
				}
			}
		}

		public boolean tryPick(final int p_194092_1_, @Nullable final IntList listIn) {
			if (p_194092_1_ <= 0) {
				return true;
			}
			else {
				int k;

				for (k = 0; dfs(p_194092_1_); ++k) {
					tryTake(possessedIngredientStacks[path.getInt(0)], p_194092_1_);
					final int l = path.size() - 1;
					setSatisfied(path.getInt(l));

					for (int i1 = 0; i1 < l; ++i1) {
						toggleResidual((i1 & 1) == 0, path.get(i1).intValue(), path.get(i1 + 1).intValue());
					}

					path.clear();
					data.clear(0, ingredientCount + possessedIngredientStackCount);
				}

				final boolean flag = k == ingredientCount;
				final boolean flag1 = flag && listIn != null;

				if (flag1) {
					listIn.clear();
				}

				data.clear(0, ingredientCount + possessedIngredientStackCount + ingredientCount);
				int j1 = 0;
				final List<SingleItemStackBasedIngredient> list = recipe.getSingleIngredients();

				for (int k1 = 0; k1 < list.size(); ++k1) {
					if (flag1 && list.get(k1) == SingleItemStackBasedIngredient.EMPTY) {
						listIn.add(0);
					}
					else {
						for (int l1 = 0; l1 < possessedIngredientStackCount; ++l1) {
							if (hasResidual(false, j1, l1)) {
								toggleResidual(true, l1, j1);
								increment(possessedIngredientStacks[l1], p_194092_1_);

								if (flag1) {
									listIn.add(possessedIngredientStacks[l1]);
								}
							}
						}

						++j1;
					}
				}

				return flag;
			}
		}

		private int[] getUniqueAvailIngredientItems() {
			final IntCollection intcollection = new IntAVLTreeSet();

			for (final SingleItemStackBasedIngredient ingredient : ingredients) {
				intcollection.addAll(ingredient.getValidItemStacksPacked());
			}

			final IntIterator intiterator = intcollection.iterator();

			while (intiterator.hasNext()) {
				if (!containsItem(intiterator.nextInt())) {
					intiterator.remove();
				}
			}

			return intcollection.toIntArray();
		}

		private boolean dfs(final int p_194098_1_) {
			final int k = possessedIngredientStackCount;

			for (int l = 0; l < k; ++l) {
				if (itemToCount.get(possessedIngredientStacks[l]) >= p_194098_1_) {
					visit(false, l);

					while (!path.isEmpty()) {
						final int i1 = path.size();
						final boolean flag = (i1 & 1) == 1;
						final int j1 = path.getInt(i1 - 1);

						if (!flag && !isSatisfied(j1)) {
							break;
						}

						final int k1 = flag ? ingredientCount : k;

						for (int l1 = 0; l1 < k1; ++l1) {
							if (!hasVisited(flag, l1) && hasConnection(flag, j1, l1) && hasResidual(flag, j1, l1)) {
								visit(flag, l1);
								break;
							}
						}

						final int i2 = path.size();

						if (i2 == i1) {
							path.removeInt(i2 - 1);
						}
					}

					if (!path.isEmpty()) {
						return true;
					}
				}
			}

			return false;
		}

		private boolean isSatisfied(final int p_194091_1_) {
			return data.get(getSatisfiedIndex(p_194091_1_));
		}

		private void setSatisfied(final int p_194096_1_) {
			data.set(getSatisfiedIndex(p_194096_1_));
		}

		private int getSatisfiedIndex(final int p_194094_1_) {
			return ingredientCount + possessedIngredientStackCount + p_194094_1_;
		}

		private boolean hasConnection(final boolean p_194093_1_, final int p_194093_2_, final int p_194093_3_) {
			return data.get(getIndex(p_194093_1_, p_194093_2_, p_194093_3_));
		}

		private boolean hasResidual(final boolean p_194100_1_, final int p_194100_2_, final int p_194100_3_) {
			return p_194100_1_ != data.get(1 + getIndex(p_194100_1_, p_194100_2_, p_194100_3_));
		}

		private void toggleResidual(final boolean p_194089_1_, final int p_194089_2_, final int p_194089_3_) {
			data.flip(1 + getIndex(p_194089_1_, p_194089_2_, p_194089_3_));
		}

		private int getIndex(final boolean p_194095_1_, final int p_194095_2_, final int p_194095_3_) {
			final int k = p_194095_1_ ? p_194095_2_ * ingredientCount + p_194095_3_ : p_194095_3_ * ingredientCount + p_194095_2_;
			return ingredientCount + possessedIngredientStackCount + ingredientCount + 2 * k;
		}

		private void visit(final boolean p_194088_1_, final int p_194088_2_) {
			data.set(getVisitedIndex(p_194088_1_, p_194088_2_));
			path.add(p_194088_2_);
		}

		private boolean hasVisited(final boolean p_194101_1_, final int p_194101_2_) {
			return data.get(getVisitedIndex(p_194101_1_, p_194101_2_));
		}

		private int getVisitedIndex(final boolean p_194099_1_, final int p_194099_2_) {
			return (p_194099_1_ ? 0 : ingredientCount) + p_194099_2_;
		}

		public int tryPickAll(final int p_194102_1_, @Nullable final IntList list) {
			int k = 0;
			int l = Math.min(p_194102_1_, getMinIngredientCount()) + 1;

			while (true) {
				final int i1 = (k + l) / 2;

				if (tryPick(i1, (IntList) null)) {
					if (l - k <= 1) {
						if (i1 > 0) {
							tryPick(i1, list);
						}

						return i1;
					}

					k = i1;
				}
				else {
					l = i1;
				}
			}
		}

		private int getMinIngredientCount() {
			int k = Integer.MAX_VALUE;

			for (final SingleItemStackBasedIngredient ingredient : ingredients) {
				int l = 0;
				int i1;

				for (final IntListIterator intlistiterator = ingredient.getValidItemStacksPacked().iterator(); intlistiterator.hasNext(); l = Math.max(l, itemToCount.get(i1))) {
					i1 = intlistiterator.next().intValue();
				}

				if (k > 0) {
					k = Math.min(k, l);
				}
			}

			return k;
		}
	}

	public static class SynthesisRecipeCallbacks implements IForgeRegistry.MissingFactory<ISynthesisRecipe> {

		public static final SynthesisRecipeCallbacks INSTANCE = new SynthesisRecipeCallbacks();

		@Override
		public ISynthesisRecipe createMissing(final ResourceLocation key, final boolean isNetwork) {
			return isNetwork ? new DummyRecipe().setRegistryName(key) : null;
		}

		private static class DummyRecipe implements ISynthesisRecipe {

			private static ItemStack result = ItemStack.EMPTY;
			private ResourceLocation name;

			@Override
			public ISynthesisRecipe setRegistryName(final ResourceLocation name) {
				this.name = name;
				return this;
			}

			@Override
			public ResourceLocation getRegistryName() {
				return name;
			}

			@Override
			public Class<ISynthesisRecipe> getRegistryType() {
				return ISynthesisRecipe.class;
			}

			@Override
			public boolean matches(final InventoryCrafting inv, final World worldIn) {
				return false;
			}

			@Override
			public ItemStack getCraftingResult(final InventoryCrafting inv) {
				return result;
			}

			@Override
			public boolean canFit(final int width, final int height) {
				return false;
			}

			@Override
			public ItemStack getRecipeOutput() {
				return result;
			}

			@Override
			public boolean isDynamic() {
				return true;
			}

			@Override
			public int getEnergyCost() {
				return 0;
			}
		}
	}

}
