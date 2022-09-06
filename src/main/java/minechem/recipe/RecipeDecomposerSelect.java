package minechem.recipe;

import java.util.ArrayList;

import minechem.potion.PotionChemical;
import net.minecraft.item.ItemStack;

public class RecipeDecomposerSelect extends RecipeDecomposerChance {

	ArrayList<RecipeDecomposer> possibleRecipes = new ArrayList<>();
	ArrayList<ArrayList<PotionChemical>> possibleOutputs = new ArrayList<>();

	public RecipeDecomposerSelect(ItemStack input, float chance, ArrayList<ArrayList<PotionChemical>> possibleOutputs) {
		super(input, chance);
		this.possibleOutputs.addAll(possibleOutputs);
	}

	@Override
	public PotionChemical[] getOutputAsArray() {
		ArrayList<PotionChemical> out = getOutput();
		return out.toArray(new PotionChemical[0]);
	}

	@Override
	public ArrayList<PotionChemical> getOutput() {
		return possibleOutputs.get(random.nextInt(possibleOutputs.size()));
	}

	@Override
	public ArrayList<PotionChemical> getOutputRaw() {
		return possibleOutputs.get(0);
	}

	public RecipeDecomposer getRecipeRaw() {
		return this;
	}

	public ArrayList<ArrayList<PotionChemical>> getAllPossibleOutputs() {
		return possibleOutputs;
	}

	@Override
	public boolean isNull() {
		return (super.isNull() && possibleOutputs == null);
	}

	@Override
	public boolean hasOutput() {
		return !possibleOutputs.isEmpty();
	}

	@Override
	public boolean outputContains(PotionChemical potionChemical) {
		for (ArrayList<PotionChemical> po : possibleOutputs) {
			if (po.contains(potionChemical)) {
				return true;
			}
		}
		return false;
	}
}
