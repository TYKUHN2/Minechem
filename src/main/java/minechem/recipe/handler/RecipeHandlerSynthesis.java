package minechem.recipe.handler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import minechem.api.recipe.ISynthesisRecipe;
import minechem.init.ModGlobals;
import minechem.init.ModRegistries;
import minechem.item.ItemElement;
import minechem.item.ItemMolecule;
import minechem.item.element.Element;
import minechem.item.molecule.Molecule;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeSynthesisShaped;
import minechem.recipe.RecipeSynthesisShaped.MinechemShapedPrimer;
import minechem.recipe.RecipeSynthesisShapeless;
import minechem.recipe.SingleItemStackBasedIngredient;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeHandlerSynthesis {

	private static final String PREFIX_SHAPED = "synthesis_shaped_";
	private static final String PREFIX_SHAPELESS = "synthesis_shapeless_";

	public static RecipeHandlerSynthesis instance = new RecipeHandlerSynthesis();

	public static boolean itemStacksMatchesShapelessRecipe(NonNullList<ItemStack> stacks, RecipeSynthesisShapeless recipe, int factor) {
		NonNullList<ItemStack> stacksCopy = MinechemUtil.copyStackList(stacks);
		NonNullList<ItemStack> stackList = NonNullList.<ItemStack>create();
		for (SingleItemStackBasedIngredient ingredient : recipe.getSingleIngredients()) {
			stackList.add(ingredient.getIngredientStack());
		}
		if (stacksCopy.size() == stackList.size()) {
			/*
			List<PotionChemical> potionChemicals = new ArrayList<>();
			for (ItemStack stack : stackList) {
				ElementEnum element = MinechemUtil.getElement(stack);
				MoleculeEnum molecule = MinechemUtil.getMolecule(stack);
				if (element != null) {
					potionChemicals.add(new Element(element, element.atomicNumber()));
				}
				else if (molecule != null) {
					potionChemicals.add(new Molecule(molecule, molecule.id()));
				}
			}
			*/
			//NonNullList<ItemStack> shapelessRecipe = MinechemUtil.convertChemicalsIntoItemStacks(potionChemicals);
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

	public static boolean itemStacksMatchesShapedRecipe(NonNullList<ItemStack> stacks, RecipeSynthesisShaped recipe, int factor) {
		PotionChemical[] chemicals = MinechemUtil.stackListToChemicalArray(stacks);
		NonNullList<ItemStack> recipeStacks = RecipeUtil.ingredientListToStackList(recipe.getIngredients());
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
	public static ISynthesisRecipe findMatchingRecipe(InventoryCrafting craftMatrix, World worldIn) {
		for (ISynthesisRecipe irecipe : ModRegistries.SYNTHESIS_RECIPES) {
			if (irecipe.matches(craftMatrix, worldIn)) {
				return irecipe;
			}
		}

		return null;
	}

	private static NonNullList<ItemStack> getListFromRecipe(ISynthesisRecipe recipe) {
		NonNullList<ItemStack> tmpList = NonNullList.create();
		NonNullList<SingleItemStackBasedIngredient> ingredients = recipe.getSingleIngredients();
		for (SingleItemStackBasedIngredient ingredient : ingredients) {
			tmpList.add(ingredient.getIngredientStack());
		}
		return tmpList;
	}

	public static ISynthesisRecipe getRecipeFromInput(NonNullList<ItemStack> stackList) {
		ISynthesisRecipe matchedRecipe = null;
		for (Map.Entry<ResourceLocation, ISynthesisRecipe> recipeEntry : ModRegistries.SYNTHESIS_RECIPES.getEntries()) {
			ISynthesisRecipe recipe = recipeEntry.getValue();
			NonNullList<ItemStack> recipeList = getListFromRecipe(recipe);
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

	/*
	private static <K extends IForgeRegistryEntry<K>> K register(K object) {
		return GameData.register_impl(object);
	}
	*/
	private static <K extends IForgeRegistryEntry<ISynthesisRecipe>> ISynthesisRecipe register(ISynthesisRecipe object) {
		return GameData.register_impl(object);
	}

	public static void addShapedRecipe(ResourceLocation registryName, int energyCost, ItemStack result, Object... recipe) {
		MinechemShapedPrimer primer = RecipeSynthesisShaped.parseShaped(recipe);
		ISynthesisRecipe newRecipe = new RecipeSynthesisShaped(primer.width, primer.height, energyCost, primer.input, result).setRegistryName(registryName);
		register(newRecipe);
	}

	public static void addShapedRecipe(String name, int energyCost, ItemStack result, Object... recipe) {
		String regDomain = name.split(":").length == 1 ? ModGlobals.ID : name.split(":")[1];
		String regPath = name.split(":").length == 1 ? name : name.split(":")[1];
		addShapedRecipe(new ResourceLocation(regDomain, PREFIX_SHAPED + "" + regPath), energyCost, result, recipe);
	}

	public static void addShapelessRecipe(String name, int energyCost, @Nonnull ItemStack output, PotionChemical... recipe) {
		NonNullList<ItemStack> lst = NonNullList.create();
		for (Object ingredient : recipe) {
			if (ingredient instanceof PotionChemical) {
				PotionChemical chemical = (PotionChemical) ingredient;
				lst.add(MinechemUtil.chemicalToItemStack(chemical, chemical.amount));//RecipeUtil.getIngredient(ingredient));
			}
		}
		NonNullList<SingleItemStackBasedIngredient> dumbList = SingleItemStackBasedIngredient.fromItemStacks(lst.toArray(new ItemStack[lst.size()]));
		ISynthesisRecipe newRecipe = new RecipeSynthesisShapeless(output, energyCost, dumbList).setRegistryName(new ResourceLocation(ModGlobals.ID, PREFIX_SHAPELESS + "" + name));
		register(newRecipe);
	}

	public static void removeShapedRecipe(ItemStack stack) {
		if (stack.getItem() instanceof ItemElement || stack.getItem() instanceof ItemMolecule) {
			removeShapedRecipe(MinechemUtil.itemStackToChemical(stack));
		}
	}

	private static void removeShapedRecipe(PotionChemical recipeOutput) {
		RecipeSynthesisShaped recipe = getShapedRecipe(recipeOutput);
		if (recipe != null) {
			RecipeUtil.removeRecipe(recipe.getRegistryName());
		}
	}

	public static ISynthesisRecipe getRecipeFromOutput(ItemStack stack) {
		ISynthesisRecipe recipe = null;
		recipe = getShapedRecipe(stack);
		if (recipe == null) {
			recipe = getShapelessRecipe(stack);
		}
		return recipe;
	}

	public static int getEnergyCost(ISynthesisRecipe recipe) {
		if (recipe instanceof RecipeSynthesisShaped) {
			return ((RecipeSynthesisShaped) recipe).getEnergyCost();
		}
		if (recipe instanceof RecipeSynthesisShapeless) {
			return ((RecipeSynthesisShapeless) recipe).getEnergyCost();
		}
		return 0;
	}

	public static boolean isShaped(ISynthesisRecipe recipe) {
		return recipe.isShaped();
	}

	public static boolean isShapeless(ISynthesisRecipe recipe) {
		return !recipe.isShaped();
	}

	public static PotionChemical[] getChemicalsFromRecipe(ISynthesisRecipe recipe) {
		PotionChemical[] returnArray = new PotionChemical[recipe.getIngredients().size()];
		NonNullList<ItemStack> stackList = RecipeUtil.getRecipeAsStackList(recipe);
		for (int i = 0; i < stackList.size(); i++) {
			ItemStack stack = stackList.get(i).copy();
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

	private static RecipeSynthesisShaped getShapedRecipe(PotionChemical recipeOutput) {
		int amount = 1;
		if (recipeOutput instanceof Element) {
			amount = ((Element) recipeOutput).amount;
		}
		else if (recipeOutput instanceof Molecule) {
			amount = ((Molecule) recipeOutput).amount;
		}
		return getShapedRecipe(MinechemUtil.chemicalToItemStack(recipeOutput, amount));
	}

	private static RecipeSynthesisShaped getShapedRecipe(ItemStack recipeOutput) {
		Set<Map.Entry<ResourceLocation, ISynthesisRecipe>> recipes = ModRegistries.SYNTHESIS_RECIPES.getEntries();
		for (Map.Entry<ResourceLocation, ISynthesisRecipe> recipeEntry : recipes) {
			ISynthesisRecipe recipe = recipeEntry.getValue();
			if (recipe instanceof RecipeSynthesisShaped) {
				ItemStack resultStack = recipe.getRecipeOutput();
				if (MinechemUtil.stacksAreSameKind(recipeOutput, resultStack)) {
					return (RecipeSynthesisShaped) recipe;
				}
			}
		}
		return null;
	}

	private static RecipeSynthesisShapeless getShapelessRecipe(ItemStack recipeOutput) {
		Set<Map.Entry<ResourceLocation, ISynthesisRecipe>> recipes = ModRegistries.SYNTHESIS_RECIPES.getEntries();
		for (Map.Entry<ResourceLocation, ISynthesisRecipe> recipeEntry : recipes) {
			ISynthesisRecipe recipe = recipeEntry.getValue();
			if (recipe instanceof RecipeSynthesisShapeless) {
				ItemStack resultStack = recipe.getRecipeOutput();
				if (MinechemUtil.stacksAreSameKind(recipeOutput, resultStack)) {
					return (RecipeSynthesisShapeless) recipe;
				}
			}
		}
		return null;
	}

	private static RecipeSynthesisShapeless getShapelessRecipe(PotionChemical recipeOutput) {
		int amount = 1;
		if (recipeOutput instanceof Element) {
			amount = ((Element) recipeOutput).amount;
		}
		else if (recipeOutput instanceof Molecule) {
			amount = ((Molecule) recipeOutput).amount;
		}
		return getShapelessRecipe(MinechemUtil.chemicalToItemStack(recipeOutput, amount));
	}

	public static void addShapelessOreDictRecipe(String item, int energyCost, PotionChemical... chemicals) {
		List<ItemStack> oreDictEntries = OreDictionary.getOres(item);
		int entry = 0;
		for (Iterator<ItemStack> itr = oreDictEntries.iterator(); itr.hasNext() && entry < 8; entry++) {
			PotionChemical[] val = new PotionChemical[9];
			for (int i = 0; i < chemicals.length; i++) {
				val[(i + entry) % 9] = chemicals[i];
			}
			ItemStack ore = itr.next();
			addShapelessRecipe(ore.getItem().getUnlocalizedName(), energyCost, new ItemStack(ore.getItem(), 1, ore.getItemDamage()), val);
		}
	}

	public static void addShapedOreDictRecipe(String item, int energyCost, PotionChemical... chemicals) {
		List<ItemStack> oreDictEntries = OreDictionary.getOres(item);
		//int entry = 0;
		//for (Iterator<ItemStack> itr = oreDictEntries.iterator(); itr.hasNext() && entry < 8; entry++) {
		if (!oreDictEntries.isEmpty()) {
			ItemStack ore = oreDictEntries.get(0);
			ResourceLocation regName = ore.getItem().getRegistryName();
			addShapedRecipe(regName.getResourceDomain() + "_" + regName.getResourcePath() + "_" + ore.getItemDamage(), energyCost, new ItemStack(ore.getItem(), 1, ore.getItemDamage()), createShapedObject(chemicals));
		}
	}

	private static Object[] createShapedObject(PotionChemical[] chemicals) {
		int currentSymbolIndex = 'a';
		String[] pattern = new String[3];
		List<Object> symbolDefs = new ArrayList<>();
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
		Object[] returnObject = new Object[pattern.length + symbolDefs.size()];
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

	public void accountStack(ItemStack stack) {
		this.accountStack(stack, -1);
	}

	public void accountStack(ItemStack stack, int forceCount) {
		if (!stack.isEmpty() && !stack.isItemDamaged() && !stack.isItemEnchanted() && !stack.hasDisplayName()) {
			int i = pack(stack);
			int j = forceCount == -1 ? stack.getCount() : forceCount;
			increment(i, j);
		}
	}

	public static int pack(ItemStack stack) {
		Item item = stack.getItem();
		int i = item.getHasSubtypes() ? stack.getMetadata() : 0;
		return Item.REGISTRY.getIDForObject(item) << 16 | i & 65535;
	}

	public boolean containsItem(int p_194120_1_) {
		return itemToCount.get(p_194120_1_) > 0;
	}

	public int tryTake(int p_194122_1_, int maximum) {
		int i = itemToCount.get(p_194122_1_);

		if (i >= maximum) {
			itemToCount.put(p_194122_1_, i - maximum);
			return p_194122_1_;
		}
		else {
			return 0;
		}
	}

	private void increment(int p_194117_1_, int amount) {
		itemToCount.put(p_194117_1_, itemToCount.get(p_194117_1_) + amount);
	}

	public boolean canCraft(ISynthesisRecipe recipe, @Nullable IntList p_194116_2_) {
		return this.canCraft(recipe, p_194116_2_, 1);
	}

	public boolean canCraft(ISynthesisRecipe recipe, @Nullable IntList p_194118_2_, int p_194118_3_) {
		return (new RecipeHandlerSynthesis.RecipePicker(recipe)).tryPick(p_194118_3_, p_194118_2_);
	}

	public int getBiggestCraftableStack(ISynthesisRecipe recipe, @Nullable IntList p_194114_2_) {
		return this.getBiggestCraftableStack(recipe, Integer.MAX_VALUE, p_194114_2_);
	}

	public int getBiggestCraftableStack(ISynthesisRecipe recipe, int p_194121_2_, @Nullable IntList p_194121_3_) {
		return (new RecipeHandlerSynthesis.RecipePicker(recipe)).tryPickAll(p_194121_2_, p_194121_3_);
	}

	public static ItemStack unpack(int p_194115_0_) {
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
		private IntList path = new IntArrayList();

		public RecipePicker(ISynthesisRecipe p_i47608_2_) {
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
				IntList intlist = ingredients.get(i).getValidItemStacksPacked();

				for (int j = 0; j < possessedIngredientStackCount; ++j) {
					if (intlist.contains(possessedIngredientStacks[j])) {
						data.set(getIndex(true, j, i));
					}
				}
			}
		}

		public boolean tryPick(int p_194092_1_, @Nullable IntList listIn) {
			if (p_194092_1_ <= 0) {
				return true;
			}
			else {
				int k;

				for (k = 0; dfs(p_194092_1_); ++k) {
					tryTake(possessedIngredientStacks[path.getInt(0)], p_194092_1_);
					int l = path.size() - 1;
					setSatisfied(path.getInt(l));

					for (int i1 = 0; i1 < l; ++i1) {
						toggleResidual((i1 & 1) == 0, path.get(i1).intValue(), path.get(i1 + 1).intValue());
					}

					path.clear();
					data.clear(0, ingredientCount + possessedIngredientStackCount);
				}

				boolean flag = k == ingredientCount;
				boolean flag1 = flag && listIn != null;

				if (flag1) {
					listIn.clear();
				}

				data.clear(0, ingredientCount + possessedIngredientStackCount + ingredientCount);
				int j1 = 0;
				List<SingleItemStackBasedIngredient> list = recipe.getSingleIngredients();

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
			IntCollection intcollection = new IntAVLTreeSet();

			for (SingleItemStackBasedIngredient ingredient : ingredients) {
				intcollection.addAll(ingredient.getValidItemStacksPacked());
			}

			IntIterator intiterator = intcollection.iterator();

			while (intiterator.hasNext()) {
				if (!containsItem(intiterator.nextInt())) {
					intiterator.remove();
				}
			}

			return intcollection.toIntArray();
		}

		private boolean dfs(int p_194098_1_) {
			int k = possessedIngredientStackCount;

			for (int l = 0; l < k; ++l) {
				if (itemToCount.get(possessedIngredientStacks[l]) >= p_194098_1_) {
					visit(false, l);

					while (!path.isEmpty()) {
						int i1 = path.size();
						boolean flag = (i1 & 1) == 1;
						int j1 = path.getInt(i1 - 1);

						if (!flag && !isSatisfied(j1)) {
							break;
						}

						int k1 = flag ? ingredientCount : k;

						for (int l1 = 0; l1 < k1; ++l1) {
							if (!hasVisited(flag, l1) && hasConnection(flag, j1, l1) && hasResidual(flag, j1, l1)) {
								visit(flag, l1);
								break;
							}
						}

						int i2 = path.size();

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

		private boolean isSatisfied(int p_194091_1_) {
			return data.get(getSatisfiedIndex(p_194091_1_));
		}

		private void setSatisfied(int p_194096_1_) {
			data.set(getSatisfiedIndex(p_194096_1_));
		}

		private int getSatisfiedIndex(int p_194094_1_) {
			return ingredientCount + possessedIngredientStackCount + p_194094_1_;
		}

		private boolean hasConnection(boolean p_194093_1_, int p_194093_2_, int p_194093_3_) {
			return data.get(getIndex(p_194093_1_, p_194093_2_, p_194093_3_));
		}

		private boolean hasResidual(boolean p_194100_1_, int p_194100_2_, int p_194100_3_) {
			return p_194100_1_ != data.get(1 + getIndex(p_194100_1_, p_194100_2_, p_194100_3_));
		}

		private void toggleResidual(boolean p_194089_1_, int p_194089_2_, int p_194089_3_) {
			data.flip(1 + getIndex(p_194089_1_, p_194089_2_, p_194089_3_));
		}

		private int getIndex(boolean p_194095_1_, int p_194095_2_, int p_194095_3_) {
			int k = p_194095_1_ ? p_194095_2_ * ingredientCount + p_194095_3_ : p_194095_3_ * ingredientCount + p_194095_2_;
			return ingredientCount + possessedIngredientStackCount + ingredientCount + 2 * k;
		}

		private void visit(boolean p_194088_1_, int p_194088_2_) {
			data.set(getVisitedIndex(p_194088_1_, p_194088_2_));
			path.add(p_194088_2_);
		}

		private boolean hasVisited(boolean p_194101_1_, int p_194101_2_) {
			return data.get(getVisitedIndex(p_194101_1_, p_194101_2_));
		}

		private int getVisitedIndex(boolean p_194099_1_, int p_194099_2_) {
			return (p_194099_1_ ? 0 : ingredientCount) + p_194099_2_;
		}

		public int tryPickAll(int p_194102_1_, @Nullable IntList list) {
			int k = 0;
			int l = Math.min(p_194102_1_, getMinIngredientCount()) + 1;

			while (true) {
				int i1 = (k + l) / 2;

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

			for (SingleItemStackBasedIngredient ingredient : ingredients) {
				int l = 0;
				int i1;

				for (IntListIterator intlistiterator = ingredient.getValidItemStacksPacked().iterator(); intlistiterator.hasNext(); l = Math.max(l, itemToCount.get(i1))) {
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
		public ISynthesisRecipe createMissing(ResourceLocation key, boolean isNetwork) {
			return isNetwork ? new DummyRecipe().setRegistryName(key) : null;
		}

		private static class DummyRecipe implements ISynthesisRecipe {

			private static ItemStack result = ItemStack.EMPTY;
			private ResourceLocation name;

			@Override
			public ISynthesisRecipe setRegistryName(ResourceLocation name) {
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
			public boolean matches(InventoryCrafting inv, World worldIn) {
				return false;
			}

			@Override
			public ItemStack getCraftingResult(InventoryCrafting inv) {
				return result;
			}

			@Override
			public boolean canFit(int width, int height) {
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
