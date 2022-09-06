package minechem.recipe.handler;

import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeDecomposerFluid;
import minechem.recipe.RecipeDecomposerSuper;
import minechem.utils.MapKey;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class RecipeHandlerDecomposer {
	public static RecipeHandlerDecomposer instance = new RecipeHandlerDecomposer();

	private RecipeHandlerDecomposer() {

	}

	public static void recursiveRecipes() {
		for (MapKey key : RecipeUtil.recipes.keySet()) {
			if (!RecipeDecomposer.recipes.containsKey(key)) {
				RecipeUtil recipe = RecipeUtil.get(key);
				RecipeDecomposer.add(new RecipeDecomposerSuper(recipe.output, recipe.inStacks));
			}
		}
		//Culls null recipes (used for recursion but breaks stuff if left in)
		RecipeDecomposer.recipes.entrySet().removeIf(entry -> entry.getValue().isNull());
	}

	public static void resetRecursiveRecipes() {
		for (MapKey key : RecipeDecomposer.recipes.keySet()) {
			if (RecipeDecomposer.get(key) instanceof RecipeDecomposerSuper) {
				RecipeDecomposer.remove(key);
			}
		}
		RecipeUtil.init();
		recursiveRecipes();
	}

	public RecipeDecomposer getRecipe(ItemStack input) {
		return RecipeDecomposer.get(input);
	}

	public RecipeDecomposer getRecipe(FluidStack fluidStack) {
		return RecipeDecomposer.get(fluidStack);
	}

	public NonNullList<ItemStack> getRecipeOutputForInput(ItemStack input) {
		RecipeDecomposer recipe = getRecipe(input);
		if (recipe != null) {
			return MinechemUtil.convertChemicalsIntoItemStacks(recipe.getOutput());
		}
		return null;
	}

	public NonNullList<ItemStack> getRecipeOutputForFluidInput(FluidStack input) {
		RecipeDecomposerFluid fluidRecipe = (RecipeDecomposerFluid) RecipeDecomposer.get(input);
		if (fluidRecipe != null) {
			return MinechemUtil.convertChemicalsIntoItemStacks(fluidRecipe.getOutput());
		}
		return null;
	}

}
