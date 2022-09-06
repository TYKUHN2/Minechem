package minechem.init;

import net.minecraftforge.fml.common.Loader;

/**
 * @author p455w0rd
 *
 */
public class ModIntegration {
	public enum Mods {
			TOP("theoneprobe", "The One Probe"), WAILA("waila", "WAILA"), JEI("jei", "JEI"),
			TE("thermalexpansion", "Thermal Expansion"), TF("thermalfoundation", "Thermal Foundation"),
			RA("redstonearsenal", "Redstone Arsenal"), AE2("appliedenergistics2", "Applied Energistics 2"),
			EXU2("extrautils2", "Extra Utilities 2");

		private final String modid, name;

		Mods(String modidIn, String nameIn) {
			modid = modidIn;
			name = nameIn;
		}

		public String getId() {
			return modid;
		}

		public String getName() {
			return name;
		}

		public boolean isLoaded() {
			return Loader.isModLoaded(getId());
		}
	}

}