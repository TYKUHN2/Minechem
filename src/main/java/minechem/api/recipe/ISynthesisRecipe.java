package minechem.api.recipe;

import minechem.recipe.SingleItemStackBasedIngredient;
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
public interface ISynthesisRecipe extends IForgeRegistryEntry<ISynthesisRecipe> {

	default public boolean isShaped() {
		return false;
	}

	default public NonNullList<SingleItemStackBasedIngredient> getSingleIngredients() {
		return NonNullList.<SingleItemStackBasedIngredient>create();
	}

	/**
	* Used to check if a recipe matches current crafting inventory
	*/
	boolean matches(InventoryCrafting inv, World worldIn);

	/**
	 * Returns an Item that is the result of this recipe
	 */
	ItemStack getCraftingResult(InventoryCrafting inv);

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	boolean canFit(int width, int height);

	ItemStack getRecipeOutput();

	default NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return net.minecraftforge.common.ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	default NonNullList<Ingredient> getIngredients() {
		return NonNullList.<Ingredient>create();
	}

	default boolean isDynamic() {
		return false;
	}

	default String getGroup() {
		return "";
	}

	public int getEnergyCost();

}
