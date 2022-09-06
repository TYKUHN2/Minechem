package minechem.handler.oredict;

import java.util.Arrays;
import java.util.List;

import minechem.api.IOreDictionaryHandler;
import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.recipe.RecipeDecomposer;

public class OreDictionaryExtraUtilitiesHandler implements IOreDictionaryHandler {

	private static final int BURNT_QUARTZ = 0;
	private static final int ICE_STONE = 1;

	private final String[] blockTypes = new String[] {
			"burntquartz",
			"icestone"
	};

	private final List<String> blockList = Arrays.asList(blockTypes);

	@Override
	public boolean canHandle(String oreName) {
		return blockList.contains(oreName);
	}

	@Override
	public void handle(String oreName) {
		switch (blockList.indexOf(oreName)) {
		case BURNT_QUARTZ:
			RecipeDecomposer.addOreDictRecipe(oreName, new Molecule(MoleculeEnum.siliconDioxide, 4), new Molecule(MoleculeEnum.arsenicOxide, 1), new Molecule(MoleculeEnum.galliumOxide, 1));
		case ICE_STONE:
			RecipeDecomposer.addOreDictRecipe(oreName, new Molecule(MoleculeEnum.water, 4));
		}
	}

}
