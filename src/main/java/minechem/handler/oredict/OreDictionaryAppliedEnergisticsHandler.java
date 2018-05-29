package minechem.handler.oredict;

import minechem.api.IOreDictionaryHandler;
import minechem.init.ModLogger;
import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeSynthesis;

public class OreDictionaryAppliedEnergisticsHandler implements IOreDictionaryHandler {

	private MoleculeEnum certusQuartzMolecule = MoleculeEnum.aluminiumPhosphate;

	private PotionChemical certusQuartzChemical = new Molecule(certusQuartzMolecule);

	private MoleculeEnum chargedCertusQuartzMolecule = MoleculeEnum.aluminiumHypophosphite;

	private PotionChemical chargedCertusQuartzChemical = new Molecule(chargedCertusQuartzMolecule);

	private PotionChemical[] certusQuartzDecompositionFormula = new PotionChemical[] {
			new Molecule(certusQuartzMolecule, 4)
	};

	private PotionChemical[] certusQuartzCrystalSynthesisFormula = new PotionChemical[] {
			null,
			certusQuartzChemical,
			null,
			certusQuartzChemical,
			null,
			certusQuartzChemical,
			null,
			certusQuartzChemical,
			null
	};

	private PotionChemical[] certusQuartzDustSynthesisFormula = new PotionChemical[] {
			null,
			certusQuartzChemical,
			null,
			certusQuartzChemical,
			certusQuartzChemical,
			certusQuartzChemical,
			null,
			null,
			null
	};

	private PotionChemical[] chargedCertusQuartzDecompositionFormula = new PotionChemical[] {
			new Molecule(chargedCertusQuartzMolecule, 4)
	};

	private PotionChemical[] chargedCertusQuartzCrystalSynthesisFormula = new PotionChemical[] {
			null,
			chargedCertusQuartzChemical,
			null,
			chargedCertusQuartzChemical,
			null,
			chargedCertusQuartzChemical,
			null,
			chargedCertusQuartzChemical,
			null
	};

	private PotionChemical[] fluixCertusQuartzDecompositionFormula = new PotionChemical[] {
			new Molecule(certusQuartzMolecule, 2),
			new Molecule(chargedCertusQuartzMolecule)
	};

	private PotionChemical[] fluixQuartzCrystalSynthesisFormula = new PotionChemical[] {
			null,
			new Molecule(MoleculeEnum.galliumarsenide, 1),
			null,
			certusQuartzChemical,
			null,
			chargedCertusQuartzChemical,
			null,
			chargedCertusQuartzChemical,
			null
	};

	private PotionChemical[] fluixQuartzDustDecompositionFormula = new PotionChemical[] {
			new Molecule(MoleculeEnum.galliumarsenide, 1),
			new Molecule(certusQuartzMolecule, 1),
			new Molecule(chargedCertusQuartzMolecule, 2)
	};

	private PotionChemical[] fluixQuartzDustSynthesisFormula = new PotionChemical[] {
			null,
			null,
			null,
			certusQuartzChemical,
			new Molecule(MoleculeEnum.galliumarsenide, 1),
			chargedCertusQuartzChemical,
			null,
			chargedCertusQuartzChemical,
			null
	};

	private PotionChemical[] fluixQuartzPearlDecompositionFormula = new PotionChemical[] {
			new Molecule(MoleculeEnum.galliumarsenide, 2),
			certusQuartzChemical,
			new Molecule(chargedCertusQuartzMolecule, 2)
	};

	private PotionChemical[] fluixQuartzPearlSynthesisFormula = new PotionChemical[] {
			null,
			new Molecule(MoleculeEnum.galliumarsenide, 1),
			null,
			certusQuartzChemical,
			new Molecule(MoleculeEnum.galliumarsenide, 1),
			chargedCertusQuartzChemical,
			null,
			chargedCertusQuartzChemical,
			null
	};

	@Override
	public boolean canHandle(String oreName) {
		return oreName.endsWith("CertusQuartz") || oreName.endsWith("Fluix");
	}

	@Override
	public void handle(String oreName) {
		if (oreName.equals("dustCertusQuartz")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, certusQuartzDecompositionFormula);
			RecipeSynthesis.createAndAddRecipeSafely(oreName, true, 30000, certusQuartzDustSynthesisFormula);
		}
		else if (oreName.equals("crystalCertusQuartz")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, certusQuartzDecompositionFormula);
			RecipeSynthesis.createAndAddRecipeSafely(oreName, true, 30000, certusQuartzCrystalSynthesisFormula);
		}
		else if (oreName.equals("crystalChargedCertusQuartz")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, chargedCertusQuartzDecompositionFormula);
			RecipeSynthesis.createAndAddRecipeSafely(oreName, true, 30000, chargedCertusQuartzCrystalSynthesisFormula);
		}
		else if (oreName.equals("crystalFluix")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, fluixCertusQuartzDecompositionFormula);
			RecipeSynthesis.createAndAddRecipeSafely(oreName, true, 30000, fluixQuartzCrystalSynthesisFormula);
		}
		else if (oreName.equals("dustFluix")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, fluixQuartzDustDecompositionFormula);
			RecipeSynthesis.createAndAddRecipeSafely(oreName, true, 30000, fluixQuartzDustSynthesisFormula);
		}
		else if (oreName.equals("pearlFluix")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, fluixQuartzPearlDecompositionFormula);
			RecipeSynthesis.createAndAddRecipeSafely(oreName, true, 30000, fluixQuartzPearlSynthesisFormula);
		}
		else {
			ModLogger.debug("Unknown type of AE2 item : " + oreName);
		}
	}
}
