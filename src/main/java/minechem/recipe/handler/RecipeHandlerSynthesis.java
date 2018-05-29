package minechem.recipe.handler;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nonnull;

import minechem.potion.PotionChemical;
import minechem.recipe.RecipeSynthesis;
import minechem.utils.MapKey;
import minechem.utils.MinechemUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RecipeHandlerSynthesis {

	public static RecipeHandlerSynthesis instance = new RecipeHandlerSynthesis();

	public RecipeHandlerSynthesis() {

	}

	public RecipeSynthesis getRecipeFromOutput(@Nonnull ItemStack output) {
		MapKey key = MapKey.getKey(output);
		if (key == null) {
			return null;
		}
		return RecipeSynthesis.recipes.get(key);
	}

	public RecipeSynthesis getRecipeFromInput(NonNullList<ItemStack> input) {
		for (RecipeSynthesis recipe : RecipeSynthesis.recipes.values()) {
			if (itemStacksMatchesRecipe(input, recipe)) {
				return recipe;
			}
		}
		return null;
	}

	public boolean itemStacksMatchesRecipe(NonNullList<ItemStack> stacks, RecipeSynthesis recipe) {
		return itemStacksMatchesRecipe(stacks, recipe, 1);
	}

	public boolean itemStacksMatchesRecipe(NonNullList<ItemStack> stacks, RecipeSynthesis recipe, int factor) {
		if (recipe.isShaped()) {
			return itemStacksMatchesShapedRecipe(stacks, recipe, factor);
		}
		else {
			return itemStacksMatchesShapelessRecipe(stacks, recipe, factor);
		}
	}

	private boolean itemStacksMatchesShapelessRecipe(NonNullList<ItemStack> stacks, RecipeSynthesis recipe, int factor) {
		ArrayList<ItemStack> stacksList = new ArrayList<ItemStack>();
		NonNullList<ItemStack> shapelessRecipe = MinechemUtil.convertChemicalsIntoItemStacks(new ArrayList<PotionChemical>(Arrays.asList(recipe.getShapelessRecipe())));
		for (ItemStack itemstack : stacks) {
			if (!itemstack.isEmpty()) {
				stacksList.add(itemstack.copy());
			}
		}
		for (ItemStack itemstack : stacksList) {
			int ingredientSlot = getIngredientSlotThatMatchesStack(shapelessRecipe, itemstack, 1);
			if (ingredientSlot != -1) {
				shapelessRecipe.set(ingredientSlot, ItemStack.EMPTY);
			}
			else {
				return false;
			}
		}
		boolean isRecipeListEmpty = true;
		for (ItemStack stack : shapelessRecipe) {
			if (!stack.isEmpty()) {
				isRecipeListEmpty = false;
			}
		}
		return isRecipeListEmpty;
	}

	private boolean itemStacksMatchesShapedRecipe(NonNullList<ItemStack> stacks, RecipeSynthesis recipe, int factor) {
		PotionChemical[] chemicals = recipe.getShapedRecipe();
		for (int i = 0; i < chemicals.length; i++) {
			if (stacks.get(i).isEmpty() && chemicals[i] == null) {
				continue;
			}
			if (stacks.get(i).isEmpty() || chemicals[i] == null) {
				return false;
			}
			if (!MinechemUtil.itemStackMatchesChemical(stacks.get(i), chemicals[i], factor) || stacks.get(i).getCount() != chemicals[i].amount) {
				return false;
			}
		}
		return true;
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
	public static boolean takeFromCraftingInventory(RecipeSynthesis recipe, final IInventory inv) {
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			inv.setInventorySlotContents(slot, ItemStack.EMPTY);
		}
		return true;
	}
}
