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

public class RecipeSynthesis {
	public static Map<MapKey, RecipeSynthesis> recipes = new HashMap<MapKey, RecipeSynthesis>();
	private ItemStack output = ItemStack.EMPTY;
	private PotionChemical[] shapedRecipe;
	private ArrayList<PotionChemical> unshapedRecipe = new ArrayList<PotionChemical>();
	private int energyCost;
	private boolean isShaped;

	public static RecipeSynthesis add(RecipeSynthesis recipe) {
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
			RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(ore.getItem(), 1, ore.getItemDamage()), true, energyCost, val));
		}
	}

	public static void remove(ItemStack itemStack) {
		ArrayList<RecipeSynthesis> recipes = RecipeSynthesis.search(itemStack);

		for (RecipeSynthesis recipe : recipes) {
			RecipeSynthesis.recipes.remove(MapKey.getKey(recipe.output));
		}
	}

	public static void removeRecipeSafely(String item) {
		for (ItemStack i : OreDictionary.getOres(item)) {
			RecipeSynthesis.remove(i);
		}
	}

	public static RecipeSynthesis remove(String string) {
		if (recipes.containsKey(string)) {
			return recipes.remove(string);
		}
		return null;
	}

	public static ArrayList<RecipeSynthesis> search(ItemStack itemStack) {
		ArrayList<RecipeSynthesis> results = new ArrayList<RecipeSynthesis>();

		for (RecipeSynthesis recipe : RecipeSynthesis.recipes.values()) {
			if (itemStack.isItemEqual(recipe.output)) {
				results.add(recipe);
			}
		}

		return results;

	}

	public RecipeSynthesis(ItemStack output, boolean isShaped, int energyCost, PotionChemical... recipe) {
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

	public RecipeSynthesis(ItemStack output, boolean shaped, int energyCost, ArrayList<PotionChemical> recipe) {
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

	public static RecipeSynthesis get(MapKey key) {
		return recipes.get(key);
	}

}
