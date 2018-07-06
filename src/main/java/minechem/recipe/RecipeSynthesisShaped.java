package minechem.recipe;

import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;

import minechem.api.recipe.ISynthesisRecipe;
import minechem.init.ModGlobals;
import minechem.potion.PotionChemical;
import minechem.utils.MinechemUtil;
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
public class RecipeSynthesisShaped extends IForgeRegistryEntry.Impl<ISynthesisRecipe> implements ISynthesisRecipe {

	public static final String GROUP = ModGlobals.ID + ":synthesis_shaped";
	private int energyCost = 0;
	public final NonNullList<SingleItemStackBasedIngredient> recipeItems;
	/** Kept for compat purposes */
	public final NonNullList<Ingredient> vanillaIngredients = NonNullList.<Ingredient>create();
	private final ItemStack recipeOutput;
	private final int recipeWidth;
	private final int recipeHeight;

	public RecipeSynthesisShaped(int width, int height, int energyCost, NonNullList<SingleItemStackBasedIngredient> ingredients, ItemStack result) {
		recipeItems = ingredients;
		for (SingleItemStackBasedIngredient ingredient : ingredients) {
			vanillaIngredients.add(ingredient);
		}
		recipeOutput = result;
		this.energyCost = energyCost;
		recipeWidth = width;
		recipeHeight = height;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	@Override
	public NonNullList<SingleItemStackBasedIngredient> getSingleIngredients() {
		return recipeItems;
	}

	public static MinechemShapedPrimer parseShaped(Object... recipe) {
		MinechemShapedPrimer ret = new MinechemShapedPrimer();
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

		HashMap<Character, SingleItemStackBasedIngredient> itemMap = Maps.newHashMap();
		itemMap.put(' ', SingleItemStackBasedIngredient.EMPTY);

		for (; idx < recipe.length; idx += 2) {
			Character chr = (Character) recipe[idx];
			Object in = recipe[idx + 1];
			SingleItemStackBasedIngredient ing = getIngredient(in);

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

		ret.input = NonNullList.withSize(ret.width * ret.height, SingleItemStackBasedIngredient.EMPTY);

		Set<Character> keys = Sets.newHashSet(itemMap.keySet());
		keys.remove(' ');

		int x = 0;
		for (char chr : shape.toCharArray()) {
			SingleItemStackBasedIngredient ing = itemMap.get(chr);
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

	public static SingleItemStackBasedIngredient getIngredient(Object obj) {
		if (obj instanceof PotionChemical) {
			PotionChemical chemical = (PotionChemical) obj;
			return SingleItemStackBasedIngredient.fromStack(MinechemUtil.chemicalToItemStack(chemical, chemical.amount));
		}
		return SingleItemStackBasedIngredient.EMPTY;
	}

	@Override
	public String getGroup() {
		return GROUP;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		for (int i = 0; i <= inv.getWidth() - 3; ++i) {
			for (int j = 0; j <= inv.getHeight() - 3; ++j) {
				if (checkMatch(inv, i, j, true)) {
					return true;
				}

				if (checkMatch(inv, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean isShaped() {
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return getRecipeOutput().copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width == 3 && height == 3;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

	@Override
	public int getEnergyCost() {
		return energyCost;
	}

	private boolean checkMatch(InventoryCrafting p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
		for (int i = 0; i < p_77573_1_.getWidth(); ++i) {
			for (int j = 0; j < p_77573_1_.getHeight(); ++j) {
				int k = i - p_77573_2_;
				int l = j - p_77573_3_;
				SingleItemStackBasedIngredient ingredient = SingleItemStackBasedIngredient.EMPTY;

				if (k >= 0 && l >= 0 && k < recipeWidth && l < recipeHeight) {
					if (p_77573_4_) {
						ingredient = recipeItems.get(recipeWidth - k - 1 + l * recipeWidth);
					}
					else {
						ingredient = recipeItems.get(k + l * recipeWidth);
					}
				}

				if (!ingredient.apply(p_77573_1_.getStackInRowAndColumn(i, j))) {
					return false;
				}
			}
		}

		return true;
	}

	public static class MinechemShapedPrimer {
		public int height, width;
		public boolean mirrored = true;
		public NonNullList<SingleItemStackBasedIngredient> input;
	}

}
