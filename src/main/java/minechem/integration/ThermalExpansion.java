package minechem.integration;

import static minechem.init.ModRecipes.COST_WOOL;
import static minechem.init.ModRecipes.element;
import static minechem.init.ModRecipes.molecule;

import minechem.init.ModIntegration.Mods;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeDecomposerChance;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author p455w0rd
 *
 */
public class ThermalExpansion {

	public static void addRecipes() {

		Block rockwool = Block.REGISTRY.getObject(new ResourceLocation(Mods.TE.getId(), "rockwool"));
		ItemStack blockRockWool = new ItemStack(rockwool, 1, 0);
		ItemStack blockRockOrangeWool = new ItemStack(rockwool, 1, 1);
		ItemStack blockRockMagentaWool = new ItemStack(rockwool, 1, 2);
		ItemStack blockRockLightBlueWool = new ItemStack(rockwool, 1, 3);
		ItemStack blockRockYellowWool = new ItemStack(rockwool, 1, 4);
		ItemStack blockRockLimeWool = new ItemStack(rockwool, 1, 5);
		ItemStack blockRockPinkWool = new ItemStack(rockwool, 1, 6);
		ItemStack blockRockGrayWool = new ItemStack(rockwool, 1, 7);
		ItemStack blockRockLightGrayWool = new ItemStack(rockwool, 1, 8);
		ItemStack blockRockCyanWool = new ItemStack(rockwool, 1, 9);
		ItemStack blockRockPurpleWool = new ItemStack(rockwool, 1, 10);
		ItemStack blockRockBlueWool = new ItemStack(rockwool, 1, 11);
		ItemStack blockRockBrownWool = new ItemStack(rockwool, 1, 12);
		ItemStack blockRockGreenWool = new ItemStack(rockwool, 1, 13);
		ItemStack blockRockRedWool = new ItemStack(rockwool, 1, 14);
		ItemStack blockRockBlackWool = new ItemStack(rockwool, 1, 15);

		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockOrangeWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.orangePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockMagentaWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.lightbluePigment),
				molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockLightBlueWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockYellowWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockLimeWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.limePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockPinkWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.redPigment),
				molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockGrayWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.whitePigment),
				molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockLightGrayWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.whitePigment),
				molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockCyanWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.lightbluePigment),
				molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockPurpleWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.purplePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockBlueWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockBrownWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.tannicacid)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockGreenWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.greenPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockRedWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRockBlackWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.asbestos, 2),
				molecule(MoleculeEnum.blackPigment)
		}));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool", COST_WOOL, blockRockWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.whitePigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_orange", COST_WOOL, blockRockOrangeWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.orangePigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_magenta", COST_WOOL, blockRockMagentaWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_lightblue", COST_WOOL, blockRockLightBlueWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_yellow", COST_WOOL, blockRockYellowWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.yellowPigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_lime", COST_WOOL, blockRockLimeWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.limePigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_pink", COST_WOOL, blockRockPinkWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.redPigment), molecule(MoleculeEnum.whitePigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_gray", COST_WOOL, blockRockGrayWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment, 2));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_lightgray", COST_WOOL, blockRockLightGrayWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.whitePigment, 2), molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_cyan", COST_WOOL, blockRockCyanWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.whitePigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_purple", COST_WOOL, blockRockPurpleWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.purplePigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_blue", COST_WOOL, blockRockBlueWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.lazurite));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_green", COST_WOOL, blockRockGreenWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.greenPigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_red", COST_WOOL, blockRockRedWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_black", COST_WOOL, blockRockBlackWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.blackPigment));

		Block glass = Block.REGISTRY.getObject(new ResourceLocation("ThermalExpansion", "Glass"));
		Block frame = Block.REGISTRY.getObject(new ResourceLocation("ThermalExpansion", "Frame"));
		Block light = Block.REGISTRY.getObject(new ResourceLocation("ThermalExpansion", "Light"));
		ItemStack glassStack = new ItemStack(glass);
		ItemStack lightFrameStack = new ItemStack(frame, 1, 9);
		ItemStack lightStack = new ItemStack(light);

		RecipeDecomposer.add(new RecipeDecomposer(glassStack, new PotionChemical[] {
				molecule(MoleculeEnum.magnesiumOxide, 8),
				molecule(MoleculeEnum.siliconOxide, 16),
				element(ElementEnum.Pb, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(lightFrameStack, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 4),
				molecule(MoleculeEnum.galliumarsenide, 1),
				molecule(MoleculeEnum.magnesiumOxide, 16),
				molecule(MoleculeEnum.siliconOxide, 32),
				element(ElementEnum.Pb, 16),
				element(ElementEnum.Cu, 16)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(lightStack, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 4),
				molecule(MoleculeEnum.galliumarsenide, 1),
				molecule(MoleculeEnum.magnesiumOxide, 16),
				molecule(MoleculeEnum.siliconOxide, 32),
				element(ElementEnum.Pb, 16),
				element(ElementEnum.Cu, 16),
				element(ElementEnum.P, 4)
		}));

	}

}
