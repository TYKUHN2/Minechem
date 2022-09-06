package minechem.recipe;

import minechem.api.recipe.ISynthesisRecipe;
import minechem.init.ModGlobals;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author p455w0rd
 *
 */
public class RecipeSynthesisShapeless extends IForgeRegistryEntry.Impl<ISynthesisRecipe> implements ISynthesisRecipe {

	public static final String GROUP = ModGlobals.MODID + ":synthesis_shapeless";
	private int energyCost;
	private final boolean isSimple;
	private final ItemStack recipeOutput;
	public final NonNullList<Ingredient> vanillaIngredients = NonNullList.create();
	public final NonNullList<SingleItemStackBasedIngredient> recipeItems;

	public RecipeSynthesisShapeless(ItemStack output, int energyCost, NonNullList<SingleItemStackBasedIngredient> ingredients) {
		this.energyCost = energyCost;
		recipeItems = ingredients;
		recipeOutput = output;
		vanillaIngredients.addAll(ingredients);
		boolean simple = true;
		for (SingleItemStackBasedIngredient i : ingredients) {
			simple &= i.isSimple();
		}
		isSimple = simple;
	}

	@Override
	public int getEnergyCost() {
		return energyCost;
	}

	@Override
	public NonNullList<SingleItemStackBasedIngredient> getSingleIngredients() {
		return recipeItems;
	}

	@Override
	public String getGroup() {
		return GROUP;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int ingredientCount = 0;
		//RecipeHandlerSynthesis recipeItemHelper = new RecipeHandlerSynthesis();
		NonNullList<ItemStack> inputs = NonNullList.create();

		for (int i = 0; i < inv.getHeight(); ++i) {
			for (int j = 0; j < inv.getWidth(); ++j) {
				ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

				if (!itemstack.isEmpty()) {
					++ingredientCount;
					if (isSimple) {
						//recipeItemHelper.accountStack(itemstack);
					}
					//else {
					inputs.add(itemstack);
					//}
				}
			}
		}

		if (ingredientCount != recipeItems.size()) {
			return false;
		}

		if (isSimple) {
			//return recipeItemHelper.canCraft(this, null);
		}

		return RecipeHandlerSynthesis.itemStacksMatchesShapelessRecipe(inputs, this, 1);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return getRecipeOutput().copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return (width >= 1 && width <= 3) && (height >= 1 && height <= 3);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

}
