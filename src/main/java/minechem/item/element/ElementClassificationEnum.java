package minechem.item.element;

import minechem.item.IDescriptiveName;
import net.minecraft.client.resources.I18n;

public enum ElementClassificationEnum implements IDescriptiveName {
		nonmetal("non_metal"), inertGas("inert_gas"), halogen("halogen"), alkaliMetal("alkali_metal"),
		alkalineEarthMetal("alkaline_earth_metal"), semimetallic("metalloid"), // Yes this is the proper name!
		otherMetal("other_metal"), transitionMetal("transition_metal"), lanthanide("lanthanide"), actinide("actinide");

	private final String descriptiveName;

	ElementClassificationEnum(String descriptiveName) {
		this.descriptiveName = descriptiveName;
	}

	public String className() {
		return descriptiveName;
	}

	@Override
	public String descriptiveName() {
		String localizedName = I18n.format("element.classification." + descriptiveName);
		if (!localizedName.isEmpty() || !localizedName.equals("element.classification." + descriptiveName)) {
			return localizedName;
		}
		return descriptiveName;
	}
}
