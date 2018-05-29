package minechem.recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import minechem.api.IDecomposerControl;
import minechem.init.ModConfig;
import minechem.potion.PotionChemical;
import minechem.utils.MapKey;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeDecomposer {
	public static Map<MapKey, RecipeDecomposer> recipes = new LinkedHashMap<MapKey, RecipeDecomposer>();

	private static final Random rand = new Random();
	ItemStack input = ItemStack.EMPTY;
	public Map<MapKey, PotionChemical> output = new LinkedHashMap<MapKey, PotionChemical>();

	//TODO:Add blacklist support for fluids
	public static RecipeDecomposer add(RecipeDecomposer recipe) {
		if (!recipe.input.isEmpty() && recipe.input.getItem() != null) {
			if (isBlacklisted(recipe.input) || (recipe.input.getItem() instanceof IDecomposerControl && ((IDecomposerControl) recipe.input.getItem()).getDecomposerMultiplier(recipe.input) == 0)) {
				return null;
			}
			if (recipes.get(MapKey.getKey(recipe.input)) == null) {
				recipes.put(MapKey.getKey(recipe.input), recipe);
			}
		}
		else if (recipe instanceof RecipeDecomposerFluid && ((RecipeDecomposerFluid) recipe).inputFluid != null) {
			if (recipes.get(MapKey.getKey(((RecipeDecomposerFluid) recipe).inputFluid)) == null) {
				recipes.put(MapKey.getKey(((RecipeDecomposerFluid) recipe).inputFluid), recipe);
			}
		}
		return recipe;
	}

	public static RecipeDecomposer remove(String string) {
		if (recipes.containsKey(string)) {
			return recipes.remove(string);
		}
		return null;
	}

	public static RecipeDecomposer remove(ItemStack itemStack) {
		MapKey key = MapKey.getKey(itemStack);
		if (key != null && recipes.containsKey(key)) {
			return recipes.remove(key);
		}
		return null;
	}

	public static RecipeDecomposer remove(MapKey key) {
		return recipes.remove(key);
	}

	public static RecipeDecomposer get(ItemStack itemStack) {
		if (itemStack.isEmpty() || itemStack.getItem() == null) {
			return null;
		}
		return get(MapKey.getKey(itemStack));
	}

	public static RecipeDecomposer get(FluidStack fluidStack) {
		if (fluidStack == null || fluidStack.getFluid() == null) {
			return null;
		}
		return get(MapKey.getKey(fluidStack));
	}

	public static RecipeDecomposer get(MapKey key) {
		return recipes.get(key);
	}

	public static void removeRecipeSafely(String item) {
		for (ItemStack i : OreDictionary.getOres(item)) {
			RecipeDecomposer.remove(i);
		}
	}

	public static void createAndAddRecipeSafely(String item, PotionChemical... chemicals) {
		if (chemicals.length > 0) {
			for (ItemStack i : OreDictionary.getOres(item)) {
				RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(i.getItem(), 1, i.getItemDamage()), chemicals));
			}
		}
	}

	public RecipeDecomposer(ItemStack input, PotionChemical... chemicals) {
		this(chemicals);
		this.input = input;
	}

	public RecipeDecomposer(ItemStack input, List<PotionChemical> chemicals) {
		this(chemicals.toArray(new PotionChemical[chemicals.size()]));
		this.input = input;
	}

	public RecipeDecomposer(PotionChemical... chemicals) {
		addChemicals(chemicals);
	}

	public void addChemicals(PotionChemical... chemicals) {
		for (PotionChemical potionChemical : chemicals) {
			PotionChemical current = output.get(new MapKey(potionChemical));
			if (current != null) {
				current.amount += potionChemical.amount;
				continue;
			}
			output.put(new MapKey(potionChemical), potionChemical.copy());
		}
	}

	public ItemStack getInput() {
		return input;
	}

	public MapKey getKey() {
		return new MapKey(getInput());
	}

	public ArrayList<PotionChemical> getOutput() {
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		result.addAll(output.values());
		return result;
	}

	public ArrayList<PotionChemical> getOutputRaw() {
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		result.addAll(output.values());
		return result;
	}

	public ArrayList<PotionChemical> getPartialOutputRaw(int f) {
		ArrayList<PotionChemical> raw = getOutput();
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		if (raw != null) {
			for (PotionChemical chem : raw) {
				try {
					if (chem != null) {
						PotionChemical reduced = chem.copy();
						if (reduced != null) {
							reduced.amount = (int) Math.floor(chem.amount / f);
							if (reduced.amount == 0 && rand.nextFloat() > (chem.amount / f)) {
								reduced.amount = 1;
							}
							result.add(reduced);
						}
					}

				}
				catch (Exception e) {
					// something has gone wrong
					// but we do not know quite why
					// debug code goes here
				}

			}
		}

		return result;
	}

	public boolean isNull() {
		return output == null;
	}

	public boolean hasOutput() {
		return !output.values().isEmpty();
	}

	public boolean outputContains(PotionChemical potionChemical) {
		boolean contains = false;
		for (PotionChemical output : this.output.values()) {
			contains = potionChemical.sameAs(output);
			if (contains) {
				break;
			}
		}
		return contains;
	}

	public static boolean isBlacklisted(ItemStack itemStack) {
		for (ItemStack stack : ModConfig.decomposerBlacklist) {
			if (stack.getItem() == itemStack.getItem() && (stack.getItemDamage() == Short.MAX_VALUE || stack.getItemDamage() == itemStack.getItemDamage())) {
				return true;
			}
		}
		return false;
	}

	public float getChance() {
		return 1.0F;
	}
}
