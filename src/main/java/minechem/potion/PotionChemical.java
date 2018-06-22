package minechem.potion;

import minechem.item.element.Element;
import minechem.item.molecule.Molecule;

public class PotionChemical implements Cloneable {

	public int amount;

	public PotionChemical(int amount) {
		this.amount = amount;
	}

	public PotionChemical copy() {
		return new PotionChemical(amount);
	}

	public boolean sameAs(PotionChemical potionChemical) {
		return false;
	}

	@Override
	public PotionChemical clone() {
		return copy();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PotionChemical) {
			PotionChemical chemical = (PotionChemical) obj;
			if (chemical instanceof Element && this instanceof Element) {
				Element element = (Element) chemical;
				return element.sameAs(this);
			}
			if (chemical instanceof Molecule && this instanceof Molecule) {
				Molecule molecule = (Molecule) chemical;
				return molecule.sameAs(this);
			}
		}
		return false;
	}
}
