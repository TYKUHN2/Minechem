package minechem.init;

import minechem.potion.PotionEnchantmentCoated;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author p455w0rd
 *
 */
public class ModEnchantments {

	public static void register(IForgeRegistry<Enchantment> registry) {
		PotionEnchantmentCoated.registerForge(registry);
	}

}
