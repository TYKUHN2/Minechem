package minechem.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.recipes.BrokenCraftingRecipeException;
import mezz.jei.util.ErrorUtil;
import minechem.api.recipe.ISynthesisRecipe;
import minechem.recipe.RecipeSynthesisShapeless;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author p455w0rd
 *
 */
public class ShapelessSynthesisWrapper<T extends ISynthesisRecipe> implements ICraftingRecipeWrapper {

	//private final IJeiHelpers jeiHelpers;
	protected final T recipe;

	public ShapelessSynthesisWrapper(final IJeiHelpers jeiHelpers, final T recipe) {
		//this.jeiHelpers = jeiHelpers;
		this.recipe = recipe;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void getIngredients(final IIngredients ingredients) {
		final ItemStack recipeOutput = recipe.getRecipeOutput();
		//IStackHelper stackHelper = jeiHelpers.getStackHelper();

		try {
			final List<List<ItemStack>> inputLists = new ArrayList<>();
			if (recipe instanceof RecipeSynthesisShapeless) {
				final RecipeSynthesisShapeless synthRecipe = (RecipeSynthesisShapeless) recipe;
				for (int i = 0; i < synthRecipe.getSingleIngredients().size(); i++) {
					final List<ItemStack> tmpList = new ArrayList<>();
					for (int j = 0; j < synthRecipe.getSingleIngredients().get(i).getMatchingStacks().length; j++) {
						tmpList.add(synthRecipe.getSingleIngredients().get(i).getMatchingStacks()[j]);
					}
					inputLists.add(tmpList);
				}
			}
			else {
				for (int i = 0; i < recipe.getIngredients().size(); i++) {
					final List<ItemStack> tmpList = new ArrayList<>();
					for (int j = 0; j < recipe.getIngredients().get(i).getMatchingStacks().length; j++) {
						tmpList.add(recipe.getIngredients().get(i).getMatchingStacks()[j]);
					}
					inputLists.add(tmpList);
				}
			}
			// = Lists.newArrayList(.getMatchingStacks());//stackHelper.expandRecipeItemStackInputs(recipe.getIngredients());
			ingredients.setInputLists(ItemStack.class, inputLists);
			ingredients.setOutput(ItemStack.class, recipeOutput);
		}
		catch (final RuntimeException e) {
			final String info = ErrorUtil.getInfoFromBrokenCraftingRecipe(recipe, recipe.getIngredients(), recipeOutput);
			throw new BrokenCraftingRecipeException(info, e);
		}
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return recipe.getRegistryName();
	}
}