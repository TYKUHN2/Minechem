package minechem.potion;

import java.util.ArrayList;

import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import net.minecraft.client.resources.I18n;

public enum PotionMineralEnum {
		quartz("Quartz", new Molecule(MoleculeEnum.siliconDioxide)),
		berlinite("Berlinite", new Molecule(MoleculeEnum.aluminiumPhosphate, 4));

	// Descriptive name, in en_US. Should not be used; instead, use a
	// localized string from a .properties file.
	private final String descriptiveName;
	// Localization key.
	private final String localizationKey;

	private final ArrayList<PotionChemical> components;

	/**
	 * Returns the localized name of this mineral, or an en_US-based placeholder if no localization was found.
	 *
	 * @return Localized name of this mineral.
	 */
	public String getName() {
		String localizedName = I18n.format(localizationKey);
		if (localizedName.isEmpty()) {
			return localizationKey;
		}
		return localizedName;
	}

	public PotionChemical[] getComposition() {
		return components.toArray(new PotionChemical[components.size()]);
	}

	PotionMineralEnum(String descriptiveName, PotionChemical... chemicals) {
		this.descriptiveName = descriptiveName;
		localizationKey = "mineral." + name();
		components = new ArrayList<PotionChemical>();
		for (PotionChemical potionChemical : chemicals) {
			components.add(potionChemical);
		}
	}
}
