package minechem.recipe;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import minechem.api.IDecomposerControl;
import minechem.init.ModConfig;
import minechem.init.ModLogger;
import minechem.potion.PotionChemical;
import minechem.utils.MapKey;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RecipeDecomposerSuper extends RecipeDecomposer {
	//TODO seems to be overcounting the output chance of the stained glasspane recipe (0.4375 instead of 0.375) investigate when have time
	static Random random = new Random();
	public Map<MapKey, Double> recipes = new Hashtable<>();

	public RecipeDecomposerSuper(@Nonnull ItemStack input, NonNullList<ItemStack> components, int level) {
		this.input = input.copy();
		this.input.setCount(1);

		ModLogger.debug(input.toString());
		for (ItemStack component : components) {
			if (!component.isEmpty() && component.getItem() != null) {
				if (component.getItem() instanceof IDecomposerControl && ((IDecomposerControl) component.getItem()).getDecomposerMultiplier(component) == 0) {
					continue;
				}
				RecipeDecomposer decompRecipe = RecipeDecomposer.get(component);
				if (decompRecipe != null) {
					addDecompRecipe(decompRecipe, 1.0D / Math.max(input.getCount(), 1));
				}
				else if (!component.isItemEqual(input) || !(component.getItemDamage() == input.getItemDamage())) {
					//Recursively generate recipe
					RecipeUtil recipe = RecipeUtil.get(component);
					if (recipe != null && level < ModConfig.recursiveDepth) {
						RecipeDecomposerSuper newSuper;
						RecipeDecomposer.add(newSuper = new RecipeDecomposerSuper(recipe.output, recipe.inStacks, level + 1));
						addDecompRecipe(newSuper, 1.0D / recipe.getOutStackSize());
					}
				}
			}
		}
	}

	private void addDecompRecipe(RecipeDecomposer decompRecipe, double d) {
		MapKey key = MapKey.getKey(decompRecipe.input);
		if (key != null) {
			Double current = recipes.put(key, d);
			if (current != null) {
				recipes.put(key, d + current);
			}
		}
	}

	public RecipeDecomposerSuper(@Nonnull ItemStack input, NonNullList<ItemStack> components) {
		this(input, components, 0);
	}

	public RecipeDecomposerSuper(@Nonnull ItemStack input, NonNullList<ItemStack> components, ArrayList<PotionChemical> chemicals) {
		this(input, components, 0);
		addPotionChemical(chemicals);
	}

	private void addPotionChemical(ArrayList<PotionChemical> out) {
		if (out != null) {
			for (PotionChemical add : out) {
				super.addChemicals(add);
			}
		}
	}

	@Override
	public ArrayList<PotionChemical> getOutput() {
		ArrayList<PotionChemical> result = super.getOutput();
		for (MapKey currentKey : recipes.keySet()) {
			RecipeDecomposer current = RecipeDecomposer.get(currentKey);
			if (current != null) {
				Double i = recipes.get(currentKey);
				ModLogger.debug("getOutput :" + currentKey + " chance: " + i);
				while (i >= 1) {
					ArrayList<PotionChemical> partialResult = current.getOutput();
					if (partialResult != null && partialResult.size() > 0) {
						result.addAll(partialResult);
					}
					i--;
				}
				if (random.nextDouble() < i) {
					ArrayList<PotionChemical> partialResult = current.getOutput();
					if (partialResult != null) {
						result.addAll(partialResult);
					}
				}
			}
		}
		return result;
	}

	@Override
	public ArrayList<PotionChemical> getOutputRaw() {
		ArrayList<PotionChemical> result = super.getOutputRaw();
		for (MapKey currentKey : recipes.keySet()) {
			RecipeDecomposer current = RecipeDecomposer.get(currentKey);
			ModLogger.debug("getOutputRaw: " + currentKey);
			if (current != null) {
				for (int i = 0; i < recipes.get(currentKey); i++) {
					ArrayList<PotionChemical> partialResult = current.getOutputRaw();
					partialResult = MinechemUtil.pushTogetherChemicals(partialResult);
					result.addAll(partialResult);
				}
			}
		}
		return MinechemUtil.pushTogetherChemicals(result);
	}

	public ArrayList<PotionChemical> getGuaranteedOutput() {
		return super.getOutput();
	}

	@Override
	public boolean isNull() {
		return super.getOutput() == null || recipes == null || !hasOutput();
	}

	@Override
	public ArrayList<PotionChemical> getPartialOutputRaw(int f) {
		return super.getPartialOutputRaw(f);
	}

	@Override
	public boolean hasOutput() {
		if (!output.isEmpty()) {
			return true;
		}
		if (recipes.isEmpty()) {
			return false;
		}
		for (MapKey key : recipes.keySet()) {
			RecipeDecomposer recipe = RecipeDecomposer.get(key);
			if (recipe != null && !recipe.isNull()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean outputContains(PotionChemical potionChemical) {
		boolean contains;
		contains = super.outputContains(potionChemical);
		if (!contains) {
			for (MapKey key : recipes.keySet()) {
				RecipeDecomposer dr = RecipeDecomposer.get(key);
				ModLogger.debug("outputContains: " + key);
				if (dr == null) {
					continue;
				}
				contains = dr.outputContains(potionChemical);
				if (contains) {
					break;
				}
			}
		}
		return contains;
	}

	@Override
	public float getChance() {
		float chances = 1;
		for (Map.Entry<MapKey, Double> entry : recipes.entrySet()) {
			RecipeDecomposer dr = RecipeDecomposer.get(entry.getKey());
			if (dr != null) {
				chances *= dr.getChance() / entry.getValue();
			}
		}
		return chances;
	}
}
