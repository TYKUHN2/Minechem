package minechem.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import minechem.init.ModConfig;
import minechem.potion.PotionChemical;
import minechem.utils.MapKey;
import net.minecraft.item.ItemStack;

public class RecipeSynthesisOld {
	public static Map<MapKey, RecipeSynthesisOld> recipes = new HashMap<MapKey, RecipeSynthesisOld>();
	private ItemStack output = ItemStack.EMPTY;
	private PotionChemical[] shapedRecipe;
	private ArrayList<PotionChemical> unshapedRecipe = new ArrayList<PotionChemical>();
	private int energyCost;
	private boolean isShaped;
	public int width = 3;
	public int height = 3;

	public static RecipeSynthesisOld add(RecipeSynthesisOld recipe) {
		if (!recipe.getOutput().isEmpty() && recipe.getOutput().getItem() != null) {
			if (isBlacklisted(recipe.getOutput())) {
				return null;
			}
			MapKey key = MapKey.getKey(recipe.output);
			if (key != null && recipes.get(key) == null) {
				recipes.put(key, recipe);
			}
		}

		return recipe;
	}

	public RecipeSynthesisOld(ItemStack output, boolean isShaped, int energyCost, PotionChemical... recipe) {
		this.output = output;
		this.isShaped = isShaped;
		this.energyCost = energyCost;
		if (isShaped) {
			shapedRecipe = recipe;
		}
		unshapedRecipe = new ArrayList<PotionChemical>();
		for (PotionChemical chemical : recipe) {
			if (chemical != null) {
				unshapedRecipe.add(chemical);
			}
		}
	}

	public RecipeSynthesisOld(ItemStack output, boolean isShaped, int energyCost, int width, int height, PotionChemical... recipe) {
		this.output = output;
		this.isShaped = isShaped;
		this.energyCost = energyCost;
		if (isShaped) {
			shapedRecipe = recipe;
		}
		unshapedRecipe = new ArrayList<PotionChemical>();
		for (PotionChemical chemical : recipe) {
			if (chemical != null) {
				unshapedRecipe.add(chemical);
			}
		}
		this.width = width;
		this.height = height;
	}

	public RecipeSynthesisOld(ItemStack output, boolean shaped, int energyCost, ArrayList<PotionChemical> recipe) {
		this.output = output;
		isShaped = shaped;
		this.energyCost = energyCost;
		shapedRecipe = recipe.toArray(new PotionChemical[recipe.size()]);
		unshapedRecipe = recipe;
	}

	public ItemStack getOutput() {
		return output;
	}

	public boolean isShaped() {
		return isShaped;
	}

	public int energyCost() {
		return energyCost * ModConfig.synthesisMultiplier;
	}

	public PotionChemical[] getShapedRecipe() {
		return shapedRecipe;
	}

	public PotionChemical[] getShapelessRecipe() {
		return unshapedRecipe.toArray(new PotionChemical[unshapedRecipe.size()]);
	}

	public int getIngredientCount() {
		int var1 = 0;

		PotionChemical var3;
		for (Iterator<PotionChemical> var2 = unshapedRecipe.iterator(); var2.hasNext(); var1 += var3.amount) {
			var3 = var2.next();
		}

		return var1;
	}

	public static boolean isBlacklisted(ItemStack itemStack) {
		for (ItemStack stack : ModConfig.synthesisBlacklist) {
			if (stack.getItem() == itemStack.getItem() && (stack.getItemDamage() == Short.MAX_VALUE || stack.getItemDamage() == itemStack.getItemDamage())) {
				return true;
			}
		}
		return false;
	}

	public static RecipeSynthesisOld get(MapKey key) {
		return recipes.get(key);
	}

}
