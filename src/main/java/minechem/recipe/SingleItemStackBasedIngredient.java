package minechem.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

/**
 * @author p455w0rd
 *
 */
//TODO convert to using this along with custom I[Shaped|Shapeless]SynthesisRecipe
public class SingleItemStackBasedIngredient extends Ingredient {

	public static final SingleItemStackBasedIngredient EMPTY = new SingleItemStackBasedIngredient(ItemStack.EMPTY);

	private final ItemStack ingredientStack;

	protected SingleItemStackBasedIngredient(ItemStack ingredientStack) {
		super(ingredientStack);
		this.ingredientStack = ingredientStack;
	}

	public static NonNullList<SingleItemStackBasedIngredient> fromItemStacks(ItemStack... stacks) {
		NonNullList<SingleItemStackBasedIngredient> list = NonNullList.create();
		if (stacks.length > 0) {
			for (ItemStack stack : stacks) {
				if (!stack.isEmpty()) {
					list.add(new SingleItemStackBasedIngredient(stack));
				}
			}
		}

		return list;
	}

	public static SingleItemStackBasedIngredient fromStack(ItemStack stack) {
		if (stack.isEmpty()) {
			return EMPTY;
		}
		return new SingleItemStackBasedIngredient(stack);
	}

	public ItemStack getIngredientStack() {
		return ingredientStack;
	}

	public Ingredient asIngredient() {
		return this;
	}

}
