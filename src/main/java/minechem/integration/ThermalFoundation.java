package minechem.integration;

import static minechem.init.ModRecipes.COST_INGOT;
import static minechem.init.ModRecipes.element;
import static minechem.init.ModRecipes.molecule;

import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author p455w0rd
 *
 */
public class ThermalFoundation {

	public static void addRecipes() {
		Item bucket = Item.REGISTRY.getObject(new ResourceLocation("thermalfoundation", "bucket"));
		Item material = Item.REGISTRY.getObject(new ResourceLocation("thermalfoundation", "material"));

		ItemStack redstoneBucket = new ItemStack(bucket, 1, 0);
		ItemStack glowstoneBucket = new ItemStack(bucket, 1, 1);
		ItemStack enderBucket = new ItemStack(bucket, 1, 2);
		ItemStack signalumBlend = new ItemStack(material, 1, 42);
		ItemStack lumiumBlend = new ItemStack(material, 1, 43);
		ItemStack enderiumBlend = new ItemStack(material, 1, 44);
		ItemStack signalumIngot = new ItemStack(material, 1, 74);
		ItemStack lumiumIngot = new ItemStack(material, 1, 75);
		ItemStack enderiumIngot = new ItemStack(material, 1, 76);

		RecipeDecomposer.add(new RecipeDecomposer(redstoneBucket, new PotionChemical[] {
				element(ElementEnum.Cu, 4),
				element(ElementEnum.Fe, 48),
				molecule(MoleculeEnum.iron3oxide, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(enderBucket, new PotionChemical[] {
				element(ElementEnum.Fe, 48),
				element(ElementEnum.Es, 4),
				molecule(MoleculeEnum.calciumCarbonate, 32)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(glowstoneBucket, new PotionChemical[] {
				element(ElementEnum.Fe, 48),
				element(ElementEnum.P, 4)
		}));

		RecipeDecomposer.add(new RecipeDecomposer(signalumBlend, new PotionChemical[] {
				element(ElementEnum.Cu, 12),
				element(ElementEnum.Ag, 4),
				molecule(MoleculeEnum.iron3oxide)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(signalumIngot, new PotionChemical[] {
				element(ElementEnum.Cu, 12),
				element(ElementEnum.Ag, 4),
				molecule(MoleculeEnum.iron3oxide)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(lumiumBlend, new PotionChemical[] {
				element(ElementEnum.Sn, 12),
				element(ElementEnum.Ag, 4),
				element(ElementEnum.P)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(lumiumIngot, new PotionChemical[] {
				element(ElementEnum.Sn, 12),
				element(ElementEnum.Ag, 4),
				element(ElementEnum.P)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(enderiumBlend, new PotionChemical[] {
				element(ElementEnum.Sn, 8),
				element(ElementEnum.Ag, 4),
				element(ElementEnum.Pt, 4),
				element(ElementEnum.Es),
				molecule(MoleculeEnum.calciumCarbonate, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(enderiumIngot, new PotionChemical[] {
				element(ElementEnum.Sn, 8),
				element(ElementEnum.Ag, 4),
				element(ElementEnum.Pt, 4),
				element(ElementEnum.Es),
				molecule(MoleculeEnum.calciumCarbonate, 8)
		}));
		RecipeHandlerSynthesis.addShapelessRecipe("signalum_blend", COST_INGOT, signalumBlend, element(ElementEnum.Cu, 12), element(ElementEnum.Ag, 4), molecule(MoleculeEnum.iron3oxide));
		RecipeHandlerSynthesis.addShapelessRecipe("signalum_ingot", COST_INGOT, signalumIngot, element(ElementEnum.Cu, 12), element(ElementEnum.Ag, 4), molecule(MoleculeEnum.iron3oxide));
		RecipeHandlerSynthesis.addShapelessRecipe("lumium_blend", COST_INGOT, lumiumBlend, element(ElementEnum.Sn, 12), element(ElementEnum.Ag, 4), element(ElementEnum.P));
		RecipeHandlerSynthesis.addShapelessRecipe("lumium_ingot", COST_INGOT, lumiumIngot, element(ElementEnum.Sn, 12), element(ElementEnum.Ag, 4), element(ElementEnum.P));
		RecipeHandlerSynthesis.addShapelessRecipe("enderium_blend", COST_INGOT, enderiumBlend, element(ElementEnum.Sn, 8), element(ElementEnum.Ag, 4), element(ElementEnum.Pt, 4), element(ElementEnum.Es));
		RecipeHandlerSynthesis.addShapelessRecipe("enderium_ingot", COST_INGOT, enderiumIngot, element(ElementEnum.Sn, 8), element(ElementEnum.Ag, 4), element(ElementEnum.Pt, 4), element(ElementEnum.Es), molecule(MoleculeEnum.calciumCarbonate, 8), molecule(MoleculeEnum.iron3oxide), element(ElementEnum.Pu), element(ElementEnum.C, 8), element(ElementEnum.S, 16));
	}

}
