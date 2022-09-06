package minechem.handler.oredict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import minechem.api.IOreDictionaryHandler;
import minechem.init.ModLogger;
import minechem.init.ModRecipes;
import minechem.oredictionary.OreDictionaryBaseOreEnum;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.handler.RecipeHandlerSynthesis;

public class OreDictionaryDefaultHandler implements IOreDictionaryHandler {

	private enum EnumOrePrefix {
			block, oreNether, ore, ingot, nugget, dustDirty, dustSmall, dust, plate, gem, crystal
	}

	private final String[] supportedOres;

	private final Map<OreDictionaryBaseOreEnum, ArrayList<EnumOrePrefix>> seenOres = new HashMap<>();

	public OreDictionaryDefaultHandler() {
		ArrayList<String> ores = new ArrayList<>();
		for (OreDictionaryBaseOreEnum ore : OreDictionaryBaseOreEnum.values()) {
			ores.add(ore.name());
		}
		supportedOres = ores.toArray(new String[0]);
	}

	public String[] parseOreName(String oreName) {
		for (EnumOrePrefix prefix : EnumOrePrefix.values()) {
			if (oreName.startsWith(prefix.name())) {
				String remainder = oreName.substring(prefix.name().length()).toLowerCase();
				if (Arrays.asList(supportedOres).contains(remainder)) {
					return new String[] {
							prefix.name(), remainder
					};
				}
			}
		}

		return null;
	}

	@Override
	public boolean canHandle(String oreName) {
		return parseOreName(oreName) != null;
	}

	@Override
	public void handle(String oreName) {
		ModLogger.debug(OreDictionaryDefaultHandler.class.getSimpleName() + " registered : " + oreName);

		String[] tokens = parseOreName(oreName);
		EnumOrePrefix prefix = EnumOrePrefix.valueOf(tokens[0]);
		OreDictionaryBaseOreEnum ore = OreDictionaryBaseOreEnum.valueOf(tokens[1]);

		switch (prefix) {
		case oreNether:
			RecipeDecomposer.addOreDictRecipe(oreName, scaleFloor(ore.getComposition(), 6d));
			break;
		case ore:
			RecipeDecomposer.addOreDictRecipe(oreName, scaleFloor(ore.getComposition(), 3d));
			break;
		case ingot:
			RecipeDecomposer.addOreDictRecipe(oreName, ore.getComposition());
			if (!haveSeen(ore, EnumOrePrefix.dust) && !haveSeen(ore, EnumOrePrefix.dustSmall)) {
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, ModRecipes.COST_INGOT, ore.getComposition());
			}
			break;

		case nugget:
			RecipeDecomposer.addOreDictRecipe(oreName, scaleFloor(ore.getComposition(), 1d / 9d));
			break;
		case dust:
			RecipeDecomposer.addOreDictRecipe(oreName, ore.getComposition());
			//unregisterIngot(ore);
			RecipeHandlerSynthesis.addShapedOreDictRecipe(oreName, ModRecipes.COST_INGOT / 2, startAtRow(2, ore.getComposition()));
			break;
		case dustDirty:
			RecipeDecomposer.addOreDictRecipe(oreName, scaleFloor(ore.getComposition(), 0.75d));
			break;
		case plate:
			RecipeDecomposer.addOreDictRecipe(oreName, ore.getComposition());
			break;
		case dustSmall:
			RecipeDecomposer.addOreDictRecipe(oreName, scaleFloor(ore.getComposition(), 0.25d));
			//unregisterIngot(ore);
			RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, ModRecipes.COST_INGOT / 4, scaleCeil(ore.getComposition(), 0.25d));
			break;
		case crystal:
		case gem:
			RecipeDecomposer.addOreDictRecipe(oreName, ore.getComposition());
			RecipeHandlerSynthesis.addShapedOreDictRecipe(oreName, ModRecipes.COST_GEM, startAtRow(2, ore.getComposition()));
			break;
		default:
			ModLogger.debug(OreDictionaryDefaultHandler.class.getSimpleName() + " : Invalid ore dictionary type.");
			break;
		}

		seen(ore, prefix);
	}

	private PotionChemical[] startAtRow(int row, PotionChemical[] composition) {
		PotionChemical[] array = new PotionChemical[9];
		int j = 0;
		for (int i = (row * 3) - 1; i < array.length; i++) {
			array[i] = composition[j];
			if (++j >= composition.length) {
				break;
			}
		}
		return array;
	}

	/*
		private void unregisterIngot(OreDictionaryBaseOreEnum ore) {
			if (registeredIngots.containsKey(ore)) {
				RecipeSynthesisOld.remove(registeredIngots.get(ore));
				registeredIngots.remove(ore);
			}
		}
	*/
	private PotionChemical[] scaleCeil(PotionChemical[] composition, double factor) {
		ArrayList<PotionChemical> newComposition = new ArrayList<>();

		for (PotionChemical chem : composition) {
			PotionChemical newChem = chem.copy();
			newChem.amount = (int) Math.ceil(chem.amount * factor);
			newComposition.add(newChem);
		}

		return newComposition.toArray(new PotionChemical[0]);
	}

	private PotionChemical[] scaleFloor(PotionChemical[] composition, double factor) {
		ArrayList<PotionChemical> newComposition = new ArrayList<>();

		for (PotionChemical chem : composition) {
			PotionChemical newChem = chem.copy();
			newChem.amount = (int) Math.floor(chem.amount * factor);
			if (newChem.amount > 0) {
				newComposition.add(newChem);
			}
		}

		return newComposition.toArray(new PotionChemical[0]);
	}

	private boolean haveSeen(OreDictionaryBaseOreEnum ore, EnumOrePrefix prefix) {
		return seenOres.containsKey(ore) && seenOres.get(ore).contains(prefix);
	}

	private void seen(OreDictionaryBaseOreEnum ore, EnumOrePrefix prefix) {
		if (!seenOres.containsKey(ore)) {
			seenOres.put(ore, new ArrayList<>());
		}
		if (!seenOres.get(ore).contains(prefix)) {
			seenOres.get(ore).add(prefix);
		}
	}

}
