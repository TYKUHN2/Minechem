package minechem.recipe;

import minechem.init.ModGlobals;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import minechem.utils.RecipeUtil;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/**
 * @author p455w0rd
 *
 */
public class RecipeSynthesisShapeless extends ShapelessRecipes {

	public static final String GROUP = ModGlobals.ID + ":synthesis_shapeless";
	private int energyCost = 0;
	private final boolean isSimple;
	private final NonNullList<ItemStack> recipeStacks;

	/**
	 * @param group
	 * @param output
	 * @param ingredients
	 */
	public RecipeSynthesisShapeless(ItemStack output, int energyCost, NonNullList<Ingredient> ingredients) {
		super(GROUP, output, ingredients);
		this.energyCost = energyCost;
		boolean simple = true;
		for (Ingredient i : ingredients) {
			simple &= i.isSimple();
		}
		recipeStacks = RecipeUtil.ingredientListToStackList(ingredients);
		isSimple = simple;
	}

	public int getEnergyCost() {
		return energyCost;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int ingredientCount = 0;
		RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
		NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);

		for (int i = 0; i < inv.getHeight(); ++i) {
			for (int j = 0; j < inv.getWidth(); ++j) {
				ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

				if (!itemstack.isEmpty()) {
					++ingredientCount;
					if (isSimple) {
						recipeItemHelper.accountStack(itemstack);
					}
					else {
						inputs.add(itemstack);
					}
				}
			}
		}

		if (ingredientCount != recipeItems.size()) {
			return false;
		}

		if (isSimple) {
			return recipeItemHelper.canCraft(this, null);
		}

		//return net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.recipeItems) != null;
		return RecipeHandlerSynthesis.itemStacksMatchesShapelessRecipe(inputs, this, 1);
	}

}
