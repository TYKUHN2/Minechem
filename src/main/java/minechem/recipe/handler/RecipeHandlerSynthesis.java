package minechem.recipe.handler;

import javax.annotation.Nonnull;

import minechem.init.ModGlobals;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeSynthesisShaped;
import minechem.recipe.RecipeSynthesisShapeless;
import minechem.utils.MapKey;
import minechem.utils.MinechemUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeHandlerSynthesis {

	public static RecipeHandlerSynthesis instance = new RecipeHandlerSynthesis();

	public RecipeHandlerSynthesis() {

	}

	public RecipeSynthesisShapeless getRecipeFromOutput(@Nonnull ItemStack output) {
		MapKey key = MapKey.getKey(output);
		if (key == null) {
			return null;
		}
		return RecipeSynthesisShapeless.recipes.get(key);
	}

	public RecipeSynthesisShapeless getRecipeFromInput(NonNullList<ItemStack> input) {
		for (RecipeSynthesisShapeless recipe : RecipeSynthesisShapeless.recipes.values()) {
			if (itemStacksMatchesRecipe(input, recipe)) {
				return recipe;
			}
		}
		return null;
	}

	public boolean itemStacksMatchesRecipe(NonNullList<ItemStack> stacks, RecipeSynthesisShapeless recipe) {
		return itemStacksMatchesRecipe(stacks, recipe, 1);
	}

	public boolean itemStacksMatchesRecipe(NonNullList<ItemStack> stacks, RecipeSynthesisShapeless recipe, int factor) {
		if (recipe.isShaped()) {
			return itemStacksMatchesShapedRecipe(stacks, recipe, factor);
		}
		else {
			return itemStacksMatchesShapelessRecipe(stacks, recipe, factor);
		}
	}

	private boolean itemStacksMatchesShapelessRecipe(NonNullList<ItemStack> stacks, RecipeSynthesisShapeless recipe, int factor) {
		NonNullList<ItemStack> stacksCopy = MinechemUtil.copyStackList(stacks);
		//ArrayList<ItemStack> stacksList = new ArrayList<ItemStack>();
		NonNullList<ItemStack> shapelessRecipe = MinechemUtil.convertChemicalsIntoItemStacks(recipe.getShapelessRecipe());
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
		/*
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
		*/
	}

	private static <K extends IForgeRegistryEntry<K>> K register(K object) {
		return GameData.register_impl(object);
	}

	public static void addShapedRecipe(String name, int energyCost, ItemStack result, Object... recipe) {
		ShapedPrimer primer = RecipeSynthesisShaped.parseShaped(recipe);
		register(new RecipeSynthesisShaped(primer.width, primer.height, energyCost, primer.input, result).setRegistryName(new ResourceLocation(ModGlobals.ID, name)));
	}

	private boolean itemStacksMatchesShapedRecipe(NonNullList<ItemStack> stacks, RecipeSynthesisShapeless recipe, int factor) {
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
	public static boolean takeFromCraftingInventory(RecipeSynthesisShapeless recipe, final IInventory inv) {
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			inv.setInventorySlotContents(slot, ItemStack.EMPTY);
		}
		return true;
	}
}
