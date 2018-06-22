package minechem.potion;

import java.util.ArrayList;

import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import net.minecraft.client.resources.I18n;

public enum PotionMineralEnum {

		quartz(new Molecule(MoleculeEnum.siliconDioxide)),
		berlinite(new Molecule(MoleculeEnum.aluminiumPhosphate, 4));

	private final ArrayList<PotionChemical> components;

	public PotionChemical[] getComposition() {
		return components.toArray(new PotionChemical[components.size()]);
	}

	public String getName() {
		String name = I18n.format("mineral." + name().toLowerCase());
		if (name.length() == 8) {
			name = name().substring(0, 1).toUpperCase() + name().substring(1);
		}
		return name();
	}

	PotionMineralEnum(PotionChemical... chemicals) {
		components = new ArrayList<PotionChemical>();
		for (PotionChemical potionChemical : chemicals) {
			components.add(potionChemical);
		}
	}
}
