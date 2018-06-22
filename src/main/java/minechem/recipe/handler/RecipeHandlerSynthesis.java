package minechem.recipe.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import minechem.init.ModGlobals;
import minechem.item.ItemElement;
import minechem.item.ItemMolecule;
import minechem.item.element.Element;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeSynthesisOld;
import minechem.recipe.RecipeSynthesisShaped;
import minechem.recipe.RecipeSynthesisShapeless;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeHandlerSynthesis {

	private static final String PREFIX_SHAPED = "synthesis_shaped_";
	private static final String PREFIX_SHAPELESS = "synthesis_shapeless_";
	private static Map<RecipeSynthesisShapeless, List<ItemStack>> SHAPELESS_CACHE = new HashMap<>();
	private static Map<RecipeSynthesisShaped, List<ItemStack>> SHAPED_CACHE = new HashMap<>();

	public static RecipeHandlerSynthesis instance = new RecipeHandlerSynthesis();

	public static boolean itemStacksMatchesShapelessRecipe(NonNullList<ItemStack> stacks, RecipeSynthesisShapeless recipe, int factor) {
		NonNullList<ItemStack> stacksCopy = MinechemUtil.copyStackList(stacks);
		ItemStack[] stackArray = recipe.getIngredients().toArray(new ItemStack[recipe.getIngredients().size()]);
		List<PotionChemical> potionChemicals = new ArrayList<>();
		for (ItemStack stack : stackArray) {
			ElementEnum element = MinechemUtil.getElement(stack);
			MoleculeEnum molecule = MinechemUtil.getMolecule(stack);
			if (element != null) {
				potionChemicals.add(new Element(element, element.atomicNumber()));
			}
			else if (molecule != null) {
				potionChemicals.add(new Molecule(molecule, molecule.id()));
			}
		}
		NonNullList<ItemStack> shapelessRecipe = MinechemUtil.convertChemicalsIntoItemStacks(potionChemicals);
		for (int i = 0; i < shapelessRecipe.size(); i++) {
			if (ItemStack.areItemStacksEqual(stacksCopy.get(i), shapelessRecipe.get(i))) {
				shapelessRecipe.set(i, ItemStack.EMPTY);
				stacksCopy.set(i, ItemStack.EMPTY);
			}
		}
		if (MinechemUtil.isStackListEmpty(shapelessRecipe) && MinechemUtil.isStackListEmpty(stacksCopy)) {
			return true;
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

	public static IRecipe getRecipeFromInput(NonNullList<ItemStack> stackList) {
		IRecipe matchedRecipe = null;
		for (RecipeSynthesisShaped recipe : SHAPED_CACHE.keySet()) {
			if (SHAPED_CACHE.get(recipe).size() != stackList.size()) {
				continue;
			}
			ItemStack currentStack = ItemStack.EMPTY;
			for (int i = 0; i < stackList.size(); i++) {
				currentStack = stackList.get(i);
				if (!MinechemUtil.stacksAreSameKind(currentStack, SHAPED_CACHE.get(recipe).get(i)) || currentStack.getCount() != SHAPED_CACHE.get(recipe).get(i).getCount()) {
					matchedRecipe = null;
					continue;
				}
				matchedRecipe = recipe;
				break;
			}
		}
		if (matchedRecipe == null) {
			for (RecipeSynthesisShapeless recipe : SHAPELESS_CACHE.keySet()) {
				if (SHAPELESS_CACHE.get(recipe).size() != stackList.size()) {
					continue;
				}
				ItemStack currentStack = ItemStack.EMPTY;
				for (int i = 0; i < stackList.size(); i++) {
					currentStack = stackList.get(i);
					if (!MinechemUtil.stacksAreSameKind(currentStack, SHAPED_CACHE.get(recipe).get(i)) || currentStack.getCount() != SHAPED_CACHE.get(recipe).get(i).getCount()) {
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

	private static <K extends IForgeRegistryEntry<K>> K register(K object) {
		return GameData.register_impl(object);
	}

	public static void addShapedRecipe(String name, int energyCost, ItemStack result, Object... recipe) {
		ShapedPrimer primer = RecipeSynthesisShaped.parseShaped(recipe);
		IRecipe newRecipe = new RecipeSynthesisShaped(primer.width, primer.height, energyCost, primer.input, result).setRegistryName(new ResourceLocation(ModGlobals.ID, PREFIX_SHAPED + "" + name));
		register((RecipeSynthesisShaped) newRecipe);
		SHAPED_CACHE.put((RecipeSynthesisShaped) newRecipe, RecipeUtil.ingredientListToStackList(primer.input));
	}

	public static void addShapelessRecipe(String name, int energyCost, @Nonnull ItemStack output, Object... recipe) {
		NonNullList<ItemStack> lst = NonNullList.create();
		for (Object ingredient : recipe) {
			if (ingredient instanceof PotionChemical) {
				PotionChemical chemical = (PotionChemical) ingredient;
				lst.add(MinechemUtil.chemicalToItemStack(chemical, chemical.amount));//RecipeUtil.getIngredient(ingredient));
			}
		}
		NonNullList<Ingredient> dumbList = NonNullList.create();
		dumbList.add(Ingredient.fromStacks(lst.toArray(new ItemStack[lst.size()])));
		IRecipe newRecipe = new RecipeSynthesisShapeless(output, energyCost, dumbList).setRegistryName(new ResourceLocation(ModGlobals.ID, PREFIX_SHAPELESS + "" + name));
		register(newRecipe);
		SHAPELESS_CACHE.put((RecipeSynthesisShapeless) newRecipe, RecipeUtil.ingredientListToStackList(dumbList));
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

	public static IRecipe getRecipeFromOutput(ItemStack stack) {
		//PotionChemical chemical = MinechemUtil.itemStackToChemical(stack);
		IRecipe recipe = null;
		//if (chemical != null) {
		recipe = getShapedRecipe(stack);
		if (recipe == null) {
			recipe = getShapelessRecipe(stack);
		}
		//}
		return recipe;
	}

	public static int getEnergyCost(IRecipe recipe) {
		if (recipe instanceof RecipeSynthesisShaped) {
			return ((RecipeSynthesisShaped) recipe).getEnergyCost();
		}
		if (recipe instanceof RecipeSynthesisShapeless) {
			return ((RecipeSynthesisShapeless) recipe).getEnergyCost();
		}
		return 0;
	}

	public static boolean isShaped(IRecipe recipe) {
		return recipe instanceof RecipeSynthesisShaped;
	}

	public static boolean isShapeless(IRecipe recipe) {
		return recipe instanceof RecipeSynthesisShapeless;
	}

	public static PotionChemical[] getChemicalsFromRecipe(IRecipe recipe) {
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
		Set<Map.Entry<ResourceLocation, IRecipe>> recipes = ForgeRegistries.RECIPES.getEntries();
		for (Map.Entry<ResourceLocation, IRecipe> recipeEntry : recipes) {
			IRecipe recipe = recipeEntry.getValue();
			if (recipe instanceof RecipeSynthesisShaped) {
				ItemStack resultStack = recipe.getRecipeOutput();
				//PotionChemical resultChemical = MinechemUtil.itemStackToChemical(resultStack);
				//if (resultChemical == null) {

				//}
				if (MinechemUtil.stacksAreSameKind(recipeOutput, resultStack)) {
					return (RecipeSynthesisShaped) recipe;
				}
			}
		}
		return null;
	}

	private static RecipeSynthesisShapeless getShapelessRecipe(ItemStack recipeOutput) {
		Set<Map.Entry<ResourceLocation, IRecipe>> recipes = ForgeRegistries.RECIPES.getEntries();
		for (Map.Entry<ResourceLocation, IRecipe> recipeEntry : recipes) {
			IRecipe recipe = recipeEntry.getValue();
			if (recipe instanceof RecipeSynthesisShapeless) {
				ItemStack resultStack = recipe.getRecipeOutput();
				//PotionChemical resultChemical = MinechemUtil.itemStackToChemical(resultStack);
				//if (resultChemical == null) {

				//}
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

	public static void createAndAddRecipeSafely(String item, boolean shaped, int energyCost, PotionChemical... chemicals) {
		List<ItemStack> oreDictEntries = OreDictionary.getOres(item);
		int entry = 0;
		for (Iterator<ItemStack> itr = oreDictEntries.iterator(); itr.hasNext() && entry < 8; entry++) {
			PotionChemical[] val = new PotionChemical[9];
			for (int i = 0; i < chemicals.length; i++) {
				val[(i + entry) % 9] = chemicals[i];
			}
			ItemStack ore = itr.next();
			addShapelessRecipe(ore.getItem().getUnlocalizedName(), energyCost, new ItemStack(ore.getItem(), 1, ore.getItemDamage()), Lists.newArrayList(val));
		}
	}

	private int getIngredientSlotThatMatchesStack(NonNullList<ItemStack> ingredients, @Nonnull ItemStack itemstack, int factor) {
		for (int slot = 0; slot < ingredients.size(); slot++) {
			ItemStack ingredientStack = ingredients.get(slot);
			if (!ingredientStack.isEmpty() && MinechemUtil.stacksAreSameKind(itemstack, ingredientStack) && itemstack.getCount() == ingredientStack.getCount()) {
				return slot;
			}
		}
		return -1;
	}

	/**
	 * Clears the crafting inventory.
	 */
	public static boolean takeFromCraftingInventory(RecipeSynthesisOld recipe, final IInventory inv) {
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			inv.setInventorySlotContents(slot, ItemStack.EMPTY);
		}
		return true;
	}
}
