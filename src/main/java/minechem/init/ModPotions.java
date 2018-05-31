package minechem.init;

import minechem.potion.PotionMinechem;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author p455w0rd
 *
 */
public class ModPotions {

	public static Potion atropineHigh = new PotionMinechem(true, 0x00FF6E).setPotionName("delirium").setRegistryName(PotionMinechem.getRegName("delerium"));

	public static void registerPotions(IForgeRegistry<Potion> registry) {
		registry.register(atropineHigh);
	}

}
