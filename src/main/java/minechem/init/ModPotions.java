package minechem.init;

import java.util.ArrayList;
import java.util.List;

import minechem.api.IPotionEffectRenderer;
import minechem.potion.PotionAtropineHigh;
import minechem.potion.PotionMinechem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author p455w0rd
 *
 */
public class ModPotions {

	private static List<Potion> POTION_LIST = new ArrayList<>();

	public static final Potion ATROPINE_HIGH = new PotionAtropineHigh();

	public static void registerPotions(IForgeRegistry<Potion> registry) {
		for (Potion poition : getPotionList()) {
			registry.register(poition);
		}
	}

	public static List<Potion> getPotionList() {
		if (POTION_LIST.isEmpty()) {
			POTION_LIST.add(ATROPINE_HIGH);
		}
		return POTION_LIST;
	}

	public static List<PotionMinechem> getMinechemPotionsList() {
		List<PotionMinechem> potionList = new ArrayList<PotionMinechem>();
		for (Potion potion : getPotionList()) {
			if (potion instanceof PotionMinechem) {
				potionList.add((PotionMinechem) potion);
			}
		}
		return potionList;
	}

	public static void renderEffects() {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		if (player != null) {
			for (Potion potion : getPotionList()) {
				if (player.isPotionActive(potion)) {
					if (potion instanceof IPotionEffectRenderer) {
						((IPotionEffectRenderer) potion).render(5 * player.getActivePotionEffect(potion).getAmplifier() + 5);
					}
				}
			}
		}
	}

}
