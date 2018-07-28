package minechem.item;

import net.minecraft.client.resources.I18n;

public enum MatterState implements IDescriptiveName {
		LIQUID("liquid", false, 1000, 8), SOLID("solid", false, 1200, 1), GAS("gaseous", true, 400, 8);

	private final boolean isGas;
	private final int viscosity;
	private final String descriptiveName;
	private final int quanta;

	MatterState(String descriptiveName, boolean isGas, int viscosity, int quanta) {
		this.isGas = isGas;
		this.viscosity = viscosity;
		this.descriptiveName = descriptiveName;
		this.quanta = quanta;
	}

	public boolean isGas() {
		return isGas;
	}

	public int getViscosity() {
		return viscosity;
	}

	public int getQuanta() {
		return quanta;
	}

	public String stateName() {
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
