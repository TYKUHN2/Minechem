package minechem.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionHelper {
	private static Map<String, Potion> potionMap = new LinkedHashMap<String, Potion>();

	public static Potion getPotionByName(String name) {
		if (potionMap.isEmpty()) {
			registerPotions();
		}
		Potion tmpPotion = potionMap.get(name.toLowerCase());
		if (tmpPotion == null) {
			tmpPotion = potionMap.get("effect." + name.toLowerCase());
		}
		if (tmpPotion == null) {
			tmpPotion = Potion.REGISTRY.getObject(new ResourceLocation(name.toLowerCase()));
		}
		return tmpPotion;
	}

	public static String getPotionNameById(int id) {
		Potion potion = Potion.getPotionById(id);
		return potion == null ? "" : potion.getName();
	}

	private static void registerPotions() {
		for (Potion potion : Potion.REGISTRY) {
			if (potion != null) {
				potionMap.put(potion.getName().startsWith("effect.") ? potion.getName().substring(7).toLowerCase() : potion.getName().toLowerCase(), potion);
			}
		}
	}
}
