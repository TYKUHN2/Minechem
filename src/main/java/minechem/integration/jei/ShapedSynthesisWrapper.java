package minechem.integration.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import minechem.recipe.RecipeSynthesisShaped;

/**
 * @author p455w0rd
 *
 */
public class ShapedSynthesisWrapper extends ShapelessSynthesisWrapper<RecipeSynthesisShaped> implements IShapedCraftingRecipeWrapper {

	public ShapedSynthesisWrapper(IJeiHelpers jeiHelpers, RecipeSynthesisShaped recipe) {
		super(jeiHelpers, recipe);
	}

	@Override
	public int getWidth() {
		return 3;
	}

	@Override
	public int getHeight() {
		return 3;
	}
}