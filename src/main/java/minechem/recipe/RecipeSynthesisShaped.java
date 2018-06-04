package minechem.recipe;

import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;

import minechem.init.ModGlobals;
import minechem.potion.PotionChemical;
import minechem.utils.MinechemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;

/**
 * @author p455w0rd
 *
 */
public class RecipeSynthesisShaped extends ShapedRecipes {

	public static final String GROUP = ModGlobals.ID + ":synthesis_shaped";
	private int energyCost = 0;

	/**
	 * @param group
	 * @param width
	 * @param height
	 * @param energyCost
	 * @param ingredients
	 * @param result
	 */
	public RecipeSynthesisShaped(String group, int width, int height, int energyCost, NonNullList<Ingredient> ingredients, ItemStack result) {
		super(group, width, height, ingredients, result);
		this.energyCost = energyCost;
	}

	public RecipeSynthesisShaped(int width, int height, int energyCost, NonNullList<Ingredient> ingredients, ItemStack result) {
		this(GROUP, width, height, energyCost, ingredients, result);
	}

	public static ShapedPrimer parseShaped(Object... recipe) {
		ShapedPrimer ret = new ShapedPrimer();
		String shape = "";
		int idx = 0;

		if (recipe[idx] instanceof Boolean) {
			ret.mirrored = (Boolean) recipe[idx];
			if (recipe[idx + 1] instanceof Object[]) {
				recipe = (Object[]) recipe[idx + 1];
			}
			else {
				idx = 1;
			}
		}

		if (recipe[idx] instanceof String[]) {
			String[] parts = ((String[]) recipe[idx++]);

			for (String s : parts) {
				ret.width = s.length();
				shape += s;
			}

			ret.height = parts.length;
		}
		else {
			while (recipe[idx] instanceof String) {
				String s = (String) recipe[idx++];
				shape += s;
				ret.width = s.length();
				ret.height++;
			}
		}

		if (ret.width * ret.height != shape.length() || shape.length() == 0) {
			String err = "Invalid shaped recipe: ";
			for (Object tmp : recipe) {
				err += tmp + ", ";
			}
			throw new RuntimeException(err);
		}

		HashMap<Character, Ingredient> itemMap = Maps.newHashMap();
		itemMap.put(' ', Ingredient.EMPTY);

		for (; idx < recipe.length; idx += 2) {
			Character chr = (Character) recipe[idx];
			Object in = recipe[idx + 1];
			Ingredient ing = getIngredient(in);

			if (' ' == chr.charValue()) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			if (ing != null) {
				itemMap.put(chr, ing);
			}
			else {
				String err = "Invalid shaped ore recipe: ";
				for (Object tmp : recipe) {
					err += tmp + ", ";
				}
				throw new RuntimeException(err);
			}
		}

		ret.input = NonNullList.withSize(ret.width * ret.height, Ingredient.EMPTY);

		Set<Character> keys = Sets.newHashSet(itemMap.keySet());
		keys.remove(' ');

		int x = 0;
		for (char chr : shape.toCharArray()) {
			Ingredient ing = itemMap.get(chr);
			if (ing == null) {
				throw new IllegalArgumentException("Pattern references symbol '" + chr + "' but it's not defined in the key");
			}
			ret.input.set(x++, ing);
			keys.remove(chr);
		}

		if (!keys.isEmpty()) {
			throw new IllegalArgumentException("Key defines symbols that aren't used in pattern: " + keys);
		}

		return ret;
	}

	public static Ingredient getIngredient(Object obj) {
		if (obj instanceof PotionChemical) {
			PotionChemical chemical = (PotionChemical) obj;
			return CraftingHelper.getIngredient(MinechemUtil.chemicalToItemStack(chemical, chemical.amount));
		}
		return CraftingHelper.getIngredient(obj);
	}

	public int getEnergyCost() {
		return energyCost;
	}

}
