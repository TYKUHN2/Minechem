package minechem.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import minechem.init.ModConfig;
import minechem.potion.PotionChemical;
import minechem.utils.MapKey;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeSynthesisShapeless {
	public static Map<MapKey, RecipeSynthesisShapeless> recipes = new HashMap<MapKey, RecipeSynthesisShapeless>();
	private ItemStack output = ItemStack.EMPTY;
	private PotionChemical[] shapedRecipe;
	private ArrayList<PotionChemical> unshapedRecipe = new ArrayList<PotionChemical>();
	private int energyCost;
	private boolean isShaped;
	public int width = 3;
	public int height = 3;

	public static RecipeSynthesisShapeless add(RecipeSynthesisShapeless recipe) {
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

	public static void createAndAddRecipeSafely(String item, boolean shaped, int energyCost, PotionChemical... chemicals) {
		List<ItemStack> oreDictEntries = OreDictionary.getOres(item);
		int entry = 0;
		for (Iterator<ItemStack> itr = oreDictEntries.iterator(); itr.hasNext() && entry < 8; entry++) {
			PotionChemical[] val = new PotionChemical[9];
			for (int i = 0; i < chemicals.length; i++) {
				val[(i + entry) % 9] = chemicals[i];
			}
			ItemStack ore = itr.next();
			RecipeSynthesisShapeless.add(new RecipeSynthesisShapeless(new ItemStack(ore.getItem(), 1, ore.getItemDamage()), true, energyCost, val));
		}
	}

	public static void remove(ItemStack itemStack) {
		ArrayList<RecipeSynthesisShapeless> recipes = RecipeSynthesisShapeless.search(itemStack);

		for (RecipeSynthesisShapeless recipe : recipes) {
			RecipeSynthesisShapeless.recipes.remove(MapKey.getKey(recipe.output));
		}
	}

	public static void removeRecipeSafely(String item) {
		for (ItemStack i : OreDictionary.getOres(item)) {
			RecipeSynthesisShapeless.remove(i);
		}
	}

	public static RecipeSynthesisShapeless remove(String string) {
		if (recipes.containsKey(string)) {
			return recipes.remove(string);
		}
		return null;
	}

	public static ArrayList<RecipeSynthesisShapeless> search(ItemStack itemStack) {
		ArrayList<RecipeSynthesisShapeless> results = new ArrayList<RecipeSynthesisShapeless>();

		for (RecipeSynthesisShapeless recipe : RecipeSynthesisShapeless.recipes.values()) {
			if (itemStack.isItemEqual(recipe.output)) {
				results.add(recipe);
			}
		}

		return results;

	}

	public RecipeSynthesisShapeless(ItemStack output, boolean isShaped, int energyCost, PotionChemical... recipe) {
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

	public RecipeSynthesisShapeless(ItemStack output, boolean isShaped, int energyCost, int width, int height, PotionChemical... recipe) {
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

	public RecipeSynthesisShapeless(ItemStack output, boolean shaped, int energyCost, ArrayList<PotionChemical> recipe) {
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

	public static RecipeSynthesisShapeless get(MapKey key) {
		return recipes.get(key);
	}

}
