package minechem.radiation;

import minechem.init.ModConfig;
import minechem.init.ModGlobals;

public enum RadiationEnum {
		stable(0, 0, "7"),
		hardlyRadioactive(ModGlobals.TICKS_PER_DAY, 1, "a"),
		slightlyRadioactive(ModGlobals.TICKS_PER_HOUR * 12, 2, "2"),
		radioactive(ModGlobals.TICKS_PER_HOUR * 6, 6, "e"),
		highlyRadioactive(ModGlobals.TICKS_PER_HOUR * 3, 8, "6"),
		extremelyRadioactive(ModGlobals.TICKS_PER_HOUR, 16, "4");

	private final long life;
	private final int damage;
	private final String colour;

	RadiationEnum(int life, int damage, String colour) {
		this.life = life;
		this.damage = damage;
		this.colour = ModGlobals.TEXT_MODIFIER + colour;
	}

	public long getLife() {
		return (long) (life * (ModConfig.halfLifeMultiplier / 100F));
	}

	public int getDamage() {
		return damage;
	}

	public String getColour() {
		return colour;
	}
}
