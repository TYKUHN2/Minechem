package minechem.recipe;

import java.util.ArrayList;

import minechem.potion.PotionChemical;
import net.minecraft.item.ItemStack;

public class RecipeDecomposerSelect extends RecipeDecomposerChance {

	ArrayList<RecipeDecomposer> possibleRecipes = new ArrayList<RecipeDecomposer>();
	ArrayList<ArrayList<PotionChemical>> possibleOutputs = new ArrayList<ArrayList<PotionChemical>>();

	public RecipeDecomposerSelect(ItemStack input, float chance, ArrayList<ArrayList<PotionChemical>> possibleOutputs) {
		super(input, chance);
		for (ArrayList<PotionChemical> outputs : possibleOutputs) {
			this.possibleOutputs.add(outputs);
		}
	}

	@Override
	public PotionChemical[] getOutputAsArray() {
		ArrayList<PotionChemical> out = getOutput();
		return out.toArray(new PotionChemical[out.size()]);
	}

	@Override
	public ArrayList<PotionChemical> getOutput() {
		ArrayList<PotionChemical> selectedOutput = possibleOutputs.get(random.nextInt(possibleOutputs.size()));
		return selectedOutput;
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
