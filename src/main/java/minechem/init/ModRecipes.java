package minechem.init;

import java.util.ArrayList;
import java.util.Locale;

import com.google.common.collect.Lists;

import minechem.api.IOreDictionaryHandler;
import minechem.handler.oredict.OreDictionaryAppliedEnergisticsHandler;
import minechem.handler.oredict.OreDictionaryDefaultHandler;
import minechem.handler.oredict.OreDictionaryExtraUtilitiesHandler;
import minechem.init.ModIntegration.Mods;
import minechem.item.ItemElement;
import minechem.item.element.Element;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeCloneChemistJournal;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeDecomposerChance;
import minechem.recipe.RecipeDecomposerFluid;
import minechem.recipe.RecipeDecomposerFluidSelect;
import minechem.recipe.RecipeDecomposerSelect;
import minechem.recipe.RecipeDecomposerSuper;
import minechem.recipe.RecipePotionCoating;
import minechem.recipe.RecipePotionSpiking;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import minechem.utils.BlueprintUtil;
import net.minecraft.block.BlockOre;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class ModRecipes {

	private static final ModRecipes recipes = new ModRecipes();

	public static int BLOCK_MULTIPLIER = 8;
	public static int COST_INGOT = 300;
	public static int COST_BLOCK = 15;
	public static int COST_ITEM = 10;
	public static int COST_METALBLOCK = COST_INGOT * BLOCK_MULTIPLIER;
	public static int COST_PLANK = 20;
	public static int COST_LAPIS = 20;
	public static int COST_LAPISBLOCK = COST_LAPIS * BLOCK_MULTIPLIER;
	public static int COST_GRASS = 40;
	public static int COST_SMOOTH = 30;
	public static int COST_STAR = 6000;
	public static int COST_SUGAR = 30;
	public static int COST_GLOW = 70;
	public static int COST_GLOWBLOCK = COST_GLOW * 4;
	public static int COST_TEAR = 3000;
	public static int COST_OBSIDIAN = 100;
	public static int COST_PLANT = 200;
	public static int COST_FOOD = 250;
	public static int COST_WOOD = COST_PLANK * 4;
	public static int COST_GLASS = 300;
	public static int COST_PANE = COST_GLASS / 3;
	public static int COST_WOOL = 200;
	public static int COST_CARPET = COST_WOOL / 2;
	public static int COST_GEM = 1000;
	public static int COST_GEMBLOCK = COST_GEM * BLOCK_MULTIPLIER;

	Molecule siO = new Molecule(MoleculeEnum.siliconDioxide, 1);
	Molecule moleculeSiliconDioxide = molecule(MoleculeEnum.siliconDioxide, 4);
	Molecule moleculeCellulose = molecule(MoleculeEnum.cellulose, 1);
	Molecule moleculePolyvinylChloride = molecule(MoleculeEnum.polyvinylChloride);
	Molecule moleculeLazurite = molecule(MoleculeEnum.lazurite, 9);

	Element elementHydrogen = element(ElementEnum.H, 64);
	Element elementHelium = element(ElementEnum.He, 64);
	Element elementCarbon = element(ElementEnum.C, 64);

	ItemStack blockSandStone = new ItemStack(Blocks.SANDSTONE, 1, 0);
	ItemStack blockChiseledSandStone = new ItemStack(Blocks.SANDSTONE, 1, 1);
	ItemStack blockSmoothSandStone = new ItemStack(Blocks.SANDSTONE, 1, 2);
	ItemStack blockWool = new ItemStack(Blocks.WOOL, 1, 0);
	ItemStack blockOrangeWool = new ItemStack(Blocks.WOOL, 1, 1);
	ItemStack blockMagentaWool = new ItemStack(Blocks.WOOL, 1, 2);
	ItemStack blockLightBlueWool = new ItemStack(Blocks.WOOL, 1, 3);
	ItemStack blockYellowWool = new ItemStack(Blocks.WOOL, 1, 4);
	ItemStack blockLimeWool = new ItemStack(Blocks.WOOL, 1, 5);
	ItemStack blockPinkWool = new ItemStack(Blocks.WOOL, 1, 6);
	ItemStack blockGrayWool = new ItemStack(Blocks.WOOL, 1, 7);
	ItemStack blockLightGrayWool = new ItemStack(Blocks.WOOL, 1, 8);
	ItemStack blockCyanWool = new ItemStack(Blocks.WOOL, 1, 9);
	ItemStack blockPurpleWool = new ItemStack(Blocks.WOOL, 1, 10);
	ItemStack blockBlueWool = new ItemStack(Blocks.WOOL, 1, 11);
	ItemStack blockBrownWool = new ItemStack(Blocks.WOOL, 1, 12);
	ItemStack blockGreenWool = new ItemStack(Blocks.WOOL, 1, 13);
	ItemStack blockRedWool = new ItemStack(Blocks.WOOL, 1, 14);
	ItemStack blockBlackWool = new ItemStack(Blocks.WOOL, 1, 15);
	ItemStack blockSunFlower = new ItemStack(Blocks.DOUBLE_PLANT, 1, 0);
	ItemStack blockLilac = new ItemStack(Blocks.DOUBLE_PLANT, 1, 1);
	ItemStack blockTallGrass = new ItemStack(Blocks.DOUBLE_PLANT, 1, 2);
	ItemStack blockLargeFern = new ItemStack(Blocks.DOUBLE_PLANT, 1, 3);
	ItemStack blockRoseBush = new ItemStack(Blocks.DOUBLE_PLANT, 1, 4);
	ItemStack blockPeony = new ItemStack(Blocks.DOUBLE_PLANT, 1, 5);
	ItemStack blockCobweb = new ItemStack(Blocks.WEB);
	ItemStack blockLapis = new ItemStack(Blocks.LAPIS_BLOCK);
	ItemStack blockOreLapis = new ItemStack(Blocks.LAPIS_ORE);
	ItemStack blockGlassPane = new ItemStack(Blocks.GLASS_PANE);
	ItemStack blockWhiteStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 0);
	ItemStack blockOrangeStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 1);
	ItemStack blockMagentaStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 2);
	ItemStack blockLightBlueStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 3);
	ItemStack blockYellowStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 4);
	ItemStack blockLimeStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 5);
	ItemStack blockPinkStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 6);
	ItemStack blockGrayStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 7);
	ItemStack blockLightGrayStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 8);
	ItemStack blockCyanStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 9);
	ItemStack blockPurpleStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 10);
	ItemStack blockBlueStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 11);
	ItemStack blockBrownStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 12);
	ItemStack blockGreenStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 13);
	ItemStack blockRedStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 14);
	ItemStack blockBlackStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 15);
	ItemStack blockGlass = new ItemStack(Blocks.GLASS);
	ItemStack blockWhiteStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 0);
	ItemStack blockOrangeStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 1);
	ItemStack blockMagentaStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 2);
	ItemStack blockLightBlueStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 3);
	ItemStack blockYellowStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 4);
	ItemStack blockLimeStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 5);
	ItemStack blockPinkStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 6);
	ItemStack blockGrayStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 7);
	ItemStack blockLightGrayStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 8);
	ItemStack blockCyanStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 9);
	ItemStack blockPurpleStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 10);
	ItemStack blockBlueStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 11);
	ItemStack blockBrownStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 12);
	ItemStack blockGreenStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 13);
	ItemStack blockRedStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 14);
	ItemStack blockBlackStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 15);
	ItemStack itemDyePowderBlack = new ItemStack(Items.DYE, 1, 0);
	ItemStack itemDyePowderRed = new ItemStack(Items.DYE, 1, 1);
	ItemStack itemDyePowderGreen = new ItemStack(Items.DYE, 1, 2);
	ItemStack itemDyePowderBrown = new ItemStack(Items.DYE, 1, 3);
	ItemStack itemDyePowderBlue = new ItemStack(Items.DYE, 1, 4);
	ItemStack itemDyePowderPurple = new ItemStack(Items.DYE, 1, 5);
	ItemStack itemDyePowderCyan = new ItemStack(Items.DYE, 1, 6);
	ItemStack itemDyePowderLightGray = new ItemStack(Items.DYE, 1, 7);
	ItemStack itemDyePowderGray = new ItemStack(Items.DYE, 1, 8);
	ItemStack itemDyePowderPink = new ItemStack(Items.DYE, 1, 9);
	ItemStack itemDyePowderLime = new ItemStack(Items.DYE, 1, 10);
	ItemStack itemDyePowderYellow = new ItemStack(Items.DYE, 1, 11);
	ItemStack itemDyePowderLightBlue = new ItemStack(Items.DYE, 1, 12);
	ItemStack itemDyePowderMagenta = new ItemStack(Items.DYE, 1, 13);
	ItemStack itemDyePowderOrange = new ItemStack(Items.DYE, 1, 14);
	ItemStack itemDyePowderWhite = new ItemStack(Items.DYE, 1, 15);
	ItemStack blockOakLeaves = new ItemStack(Blocks.LEAVES, 1, 0);
	ItemStack blockSpruceLeaves = new ItemStack(Blocks.LEAVES, 1, 1);
	ItemStack blockBirchLeaves = new ItemStack(Blocks.LEAVES, 1, 2);
	ItemStack blockJungleLeaves = new ItemStack(Blocks.LEAVES, 1, 3);
	ItemStack blockAcaciaLeaves = new ItemStack(Blocks.LEAVES2, 1, 0);
	ItemStack blockDarkOakLeaves = new ItemStack(Blocks.LEAVES2, 1, 1);
	ItemStack blockOakLog = new ItemStack(Blocks.LOG, 1, 0);
	ItemStack blockSpruceLog = new ItemStack(Blocks.LOG, 1, 1);
	ItemStack blockBirchLog = new ItemStack(Blocks.LOG, 1, 2);
	ItemStack blockJungleLog = new ItemStack(Blocks.LOG, 1, 3);
	ItemStack blockAcaciaLog = new ItemStack(Blocks.LOG2, 1, 0);
	ItemStack blockDarkOakLog = new ItemStack(Blocks.LOG2, 1, 1);
	ItemStack coalOre = new ItemStack(Blocks.COAL_ORE);
	ItemStack ironOre = new ItemStack(Blocks.IRON_ORE);
	ItemStack goldOre = new ItemStack(Blocks.GOLD_ORE);
	ItemStack blockGravel = new ItemStack(Blocks.GRAVEL);
	ItemStack blockSand = new ItemStack(Blocks.SAND);
	ItemStack blockOakSapling = new ItemStack(Blocks.SAPLING, 1, 0);
	ItemStack blockSpruceSapling = new ItemStack(Blocks.SAPLING, 1, 1);
	ItemStack blockBirchSapling = new ItemStack(Blocks.SAPLING, 1, 2);
	ItemStack blockJungleSapling = new ItemStack(Blocks.SAPLING, 1, 3);
	ItemStack blockAcaciaSapling = new ItemStack(Blocks.SAPLING, 1, 4);
	ItemStack blockDarkOakSapling = new ItemStack(Blocks.SAPLING, 1, 5);
	ItemStack blockOakWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 0);
	ItemStack blockSpruceWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 1);
	ItemStack blockBirchWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 2);
	ItemStack blockJungleWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 3);
	ItemStack blockAcaciaWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 4);
	ItemStack blockDarkOakWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 5);
	ItemStack blockOakWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 0);
	ItemStack blockSpruceWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 1);
	ItemStack blockBirchWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 2);
	ItemStack blockJungleWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 3);
	ItemStack blockAcaciaWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 4);
	ItemStack blockDarkOakWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 5);
	ItemStack blockCobblestone = new ItemStack(Blocks.COBBLESTONE);
	ItemStack blockDirt = new ItemStack(Blocks.DIRT, 1, 0);
	ItemStack blockPodzol = new ItemStack(Blocks.DIRT, 1, 2);
	ItemStack blockGrass = new ItemStack(Blocks.GRASS, 1, 0);
	ItemStack blockStone = new ItemStack(Blocks.STONE);
	ItemStack carpetBlockWool = new ItemStack(Blocks.CARPET, 1, 0);
	ItemStack carpetBlockOrangeWool = new ItemStack(Blocks.CARPET, 1, 1);
	ItemStack carpetBlockMagentaWool = new ItemStack(Blocks.CARPET, 1, 2);
	ItemStack carpetBlockLightBlueWool = new ItemStack(Blocks.CARPET, 1, 3);
	ItemStack carpetBlockYellowWool = new ItemStack(Blocks.CARPET, 1, 4);
	ItemStack carpetBlockLimeWool = new ItemStack(Blocks.CARPET, 1, 5);
	ItemStack carpetBlockPinkWool = new ItemStack(Blocks.CARPET, 1, 6);
	ItemStack carpetBlockGrayWool = new ItemStack(Blocks.CARPET, 1, 7);
	ItemStack carpetBlockLightGrayWool = new ItemStack(Blocks.CARPET, 1, 8);
	ItemStack carpetBlockCyanWool = new ItemStack(Blocks.CARPET, 1, 9);
	ItemStack carpetBlockPurpleWool = new ItemStack(Blocks.CARPET, 1, 10);
	ItemStack carpetBlockBlueWool = new ItemStack(Blocks.CARPET, 1, 11);
	ItemStack carpetBlockBrownWool = new ItemStack(Blocks.CARPET, 1, 12);
	ItemStack carpetBlockGreenWool = new ItemStack(Blocks.CARPET, 1, 13);
	ItemStack carpetBlockRedWool = new ItemStack(Blocks.CARPET, 1, 14);
	ItemStack carpetBlockBlackWool = new ItemStack(Blocks.CARPET, 1, 15);
	ItemStack blockYellowFlower = new ItemStack(Blocks.YELLOW_FLOWER);
	ItemStack blockPoppyFlower = new ItemStack(Blocks.RED_FLOWER, 1, 0);
	ItemStack blockBlueOrchid = new ItemStack(Blocks.RED_FLOWER, 1, 1);
	ItemStack blockAllium = new ItemStack(Blocks.RED_FLOWER, 1, 2);
	ItemStack blockAsureBluet = new ItemStack(Blocks.RED_FLOWER, 1, 3);
	ItemStack blockRedTulip = new ItemStack(Blocks.RED_FLOWER, 1, 4);
	ItemStack blockOrangeTulip = new ItemStack(Blocks.RED_FLOWER, 1, 5);
	ItemStack blockWhiteTulip = new ItemStack(Blocks.RED_FLOWER, 1, 6);
	ItemStack blockPinkTulip = new ItemStack(Blocks.RED_FLOWER, 1, 7);
	ItemStack blockOxeyeDaisy = new ItemStack(Blocks.RED_FLOWER, 1, 8);
	ItemStack blockMushroomBrown = new ItemStack(Blocks.BROWN_MUSHROOM);
	ItemStack blockMushroomRed = new ItemStack(Blocks.RED_MUSHROOM);
	ItemStack blockTnt = new ItemStack(Blocks.TNT);
	ItemStack blockObsidian = new ItemStack(Blocks.OBSIDIAN);
	ItemStack blockOreDiamond = new ItemStack(Blocks.DIAMOND_ORE);
	ItemStack blockDiamond = new ItemStack(Blocks.DIAMOND_BLOCK);
	ItemStack blockPressurePlatePlanks = new ItemStack(Blocks.WOODEN_PRESSURE_PLATE);
	ItemStack blockOreRedstone = new ItemStack(Blocks.REDSTONE_ORE);
	ItemStack blockCactus = new ItemStack(Blocks.CACTUS);
	ItemStack blockPumpkin = new ItemStack(Blocks.PUMPKIN);
	ItemStack pumpkinSeed = new ItemStack(Items.PUMPKIN_SEEDS);
	ItemStack blockNetherrack = new ItemStack(Blocks.NETHERRACK);
	ItemStack itemNetherbrick = new ItemStack(Items.NETHERBRICK);
	ItemStack itemPotion = new ItemStack(Items.POTIONITEM, 1, 0);
	ItemStack blockIce = new ItemStack(Blocks.ICE);
	ItemStack blockSlowSand = new ItemStack(Blocks.SOUL_SAND);
	ItemStack blockGlowStone = new ItemStack(Blocks.GLOWSTONE);
	ItemStack blockMycelium = new ItemStack(Blocks.MYCELIUM);
	ItemStack blockWhiteStone = new ItemStack(Blocks.END_STONE);
	ItemStack blockOreEmerald = new ItemStack(Blocks.EMERALD_ORE);
	ItemStack blockEmerald = new ItemStack(Blocks.EMERALD_BLOCK);
	ItemStack itemAppleRed = new ItemStack(Items.APPLE);
	ItemStack itemArrow = new ItemStack(Items.ARROW);
	ItemStack itemCoal = new ItemStack(Items.COAL);
	ItemStack blockCoal = new ItemStack(Blocks.COAL_BLOCK);
	ItemStack itemChar = new ItemStack(Items.COAL, 1, 1);
	ItemStack itemDiamond = new ItemStack(Items.DIAMOND);
	ItemStack itemIngotIron = new ItemStack(Items.IRON_INGOT);
	ItemStack itemIngotGold = new ItemStack(Items.GOLD_INGOT);
	ItemStack itemStick = new ItemStack(Items.STICK);
	ItemStack itemString = new ItemStack(Items.STRING);
	ItemStack itemFeather = new ItemStack(Items.FEATHER);
	ItemStack itemGunpowder = new ItemStack(Items.GUNPOWDER);
	ItemStack itemBread = new ItemStack(Items.BREAD);
	ItemStack itemFlint = new ItemStack(Items.FLINT);
	ItemStack itemAppleGold = new ItemStack(Items.GOLDEN_APPLE, 1, 0);
	ItemStack itemDoorAcacia = new ItemStack(Items.ACACIA_DOOR);
	ItemStack itemDoorBirch = new ItemStack(Items.BIRCH_DOOR);
	ItemStack itemDoorDarkOak = new ItemStack(Items.DARK_OAK_DOOR);
	ItemStack itemDoorJungle = new ItemStack(Items.JUNGLE_DOOR);
	ItemStack itemDoorOak = new ItemStack(Items.OAK_DOOR);
	ItemStack itemDoorSpruce = new ItemStack(Items.SPRUCE_DOOR);
	ItemStack itemBucket = new ItemStack(Items.BUCKET);
	ItemStack itemBucketWater = new ItemStack(Items.WATER_BUCKET);
	ItemStack itemRedstoneDust = new ItemStack(Items.REDSTONE);
	ItemStack blockRedstone = new ItemStack(Blocks.REDSTONE_BLOCK);
	ItemStack itemSnowball = new ItemStack(Items.SNOWBALL);
	ItemStack itemLeather = new ItemStack(Items.LEATHER);
	ItemStack itemBrick = new ItemStack(Items.BRICK);
	ItemStack itemClayBall = new ItemStack(Items.CLAY_BALL);
	ItemStack itemReed = new ItemStack(Items.REEDS);
	ItemStack itemVine = new ItemStack(Blocks.VINE);
	ItemStack itemPaper = new ItemStack(Items.PAPER);
	ItemStack itemCompass = new ItemStack(Items.COMPASS);
	ItemStack itemMap = new ItemStack(Items.MAP);
	ItemStack itemBook = new ItemStack(Items.BOOK);
	ItemStack blockBook = new ItemStack(Blocks.BOOKSHELF);
	ItemStack itemSlimeBall = new ItemStack(Items.SLIME_BALL);
	ItemStack itemGlowstone = new ItemStack(Items.GLOWSTONE_DUST);
	ItemStack itemBone = new ItemStack(Items.BONE);
	ItemStack itemSugar = new ItemStack(Items.SUGAR);
	ItemStack itemMelon = new ItemStack(Items.MELON);
	ItemStack blockMelon = new ItemStack(Blocks.MELON_BLOCK);
	ItemStack itemChickenCooked = new ItemStack(Items.COOKED_CHICKEN);
	ItemStack itemRottenFlesh = new ItemStack(Items.ROTTEN_FLESH);
	ItemStack itemEnderPearl = new ItemStack(Items.ENDER_PEARL);
	ItemStack blockEnderDragonEgg = new ItemStack(Blocks.DRAGON_EGG);
	ItemStack itemBlazeRod = new ItemStack(Items.BLAZE_ROD);
	ItemStack itemBlazePowder = new ItemStack(Items.BLAZE_POWDER);
	ItemStack itemGhastTear = new ItemStack(Items.GHAST_TEAR);
	ItemStack itemNetherStalkSeeds = new ItemStack(Items.NETHER_WART);
	ItemStack itemSpiderEye = new ItemStack(Items.SPIDER_EYE);
	ItemStack itemFermentedSpiderEye = new ItemStack(Items.FERMENTED_SPIDER_EYE);
	ItemStack itemMagmaCream = new ItemStack(Items.MAGMA_CREAM);
	ItemStack itemSpeckledMelon = new ItemStack(Items.SPECKLED_MELON);
	ItemStack itemEmerald = new ItemStack(Items.EMERALD);
	ItemStack itemWheat = new ItemStack(Items.WHEAT);
	ItemStack itemCarrot = new ItemStack(Items.CARROT);
	ItemStack itemPotato = new ItemStack(Items.POTATO);
	ItemStack itemGoldenCarrot = new ItemStack(Items.GOLDEN_CARROT);
	ItemStack itemNetherStar = new ItemStack(Items.NETHER_STAR);
	ItemStack itemNetherQuartz = new ItemStack(Items.QUARTZ);
	ItemStack itemRecord13 = new ItemStack(Items.RECORD_13);
	ItemStack itemRecordCat = new ItemStack(Items.RECORD_CAT);
	ItemStack itemRecordFar = new ItemStack(Items.RECORD_FAR);
	ItemStack itemRecordMall = new ItemStack(Items.RECORD_MALL);
	ItemStack itemRecordMellohi = new ItemStack(Items.RECORD_MELLOHI);
	ItemStack itemRecordStal = new ItemStack(Items.RECORD_STAL);
	ItemStack itemRecordStrad = new ItemStack(Items.RECORD_STRAD);
	ItemStack itemRecordWard = new ItemStack(Items.RECORD_WARD);
	ItemStack itemRecordChirp = new ItemStack(Items.RECORD_CHIRP);
	ItemStack itemRecord11 = new ItemStack(Items.RECORD_11);
	ItemStack itemRecordWait = new ItemStack(Items.RECORD_WAIT);
	ItemStack itemRecordBlocks = new ItemStack(Items.RECORD_BLOCKS);
	ItemStack bars = new ItemStack(Blocks.IRON_BARS);
	//ItemStack blockGlass = new ItemStack(Blocks.GLASS);
	ItemStack blockThinGlass = new ItemStack(Blocks.GLASS_PANE);
	ItemStack blockIron = new ItemStack(Blocks.IRON_BLOCK);
	//ItemStack itemIngotIron = new ItemStack(Items.IRON_INGOT);
	ItemStack itemRedstone = new ItemStack(Items.REDSTONE);
	ItemStack minechemItemsAtomicManipulator = new ItemStack(ModItems.atomicManipulator);
	//ItemStack moleculePolyvinylChloride = new ItemStack(MinechemItemsRegistration.molecule, 1, MoleculeEnum.polyvinylChloride.id());

	/*
	 * Amount of fluid for given unit
	 */
	private static final int INGOT_AMOUNT = 144;
	private static final int BUCKET_AMOUNT = 1000;

	public static ModRecipes getInstance() {
		return recipes;
	}

	public static void registerRecipes() {
		getInstance().registerMinecraftRecipes();
		getInstance().registerDecomposerRecipes();
		getInstance().registerFluidRecipes();
		getInstance().registerOreDictRecipes();
	}

	//TODO convert to JSON
	public void registerMinecraftRecipes() {
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "concave_lens"), null, ModItems.concaveLens, new Object[] {
				"G G", "GGG", "G G", Character.valueOf('G'), blockGlass
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "convex_lens"), null, ModItems.convexLens, new Object[] {
				" G ", "GGG", " G ", Character.valueOf('G'), blockGlass
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "microscope_lens"), null, ModItems.microscopeLens, new Object[] {
				"A", "B", "A", Character.valueOf('A'), ModItems.convexLens, Character.valueOf('B'), ModItems.concaveLens
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "microscope"), null, new ItemStack(ModBlocks.microscope), new Object[] {
				" LI", " PI", "III", Character.valueOf('L'), ModItems.microscopeLens, Character.valueOf('P'), blockThinGlass, Character.valueOf('I'), itemIngotIron
		});

		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "atomic_manipulator"), null, new ItemStack(ModItems.atomicManipulator), new Object[] {
				"PPP", "PIP", "PPP", Character.valueOf('P'), new ItemStack(Blocks.PISTON), Character.valueOf('I'), blockIron
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "decomposer"), null, new ItemStack(ModBlocks.decomposer), new Object[] {
				"III", "IAI", "IRI", Character.valueOf('A'), minechemItemsAtomicManipulator, Character.valueOf('I'), itemIngotIron, Character.valueOf('R'), itemRedstone
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "synthesis"), null, new ItemStack(ModBlocks.synthesis), new Object[] {
				"IRI", "IAI", "IDI", Character.valueOf('A'), minechemItemsAtomicManipulator, Character.valueOf('I'), itemIngotIron, Character.valueOf('R'), itemRedstone, Character.valueOf('D'), new ItemStack(Items.DIAMOND)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "fusion_1"), null, new ItemStack(ModBlocks.reactor_wall, 16), new Object[] {
				"ILI", "ILI", "ILI", Character.valueOf('I'), itemIngotIron, Character.valueOf('L'), ItemElement.createStackOf(ElementEnum.Pb, 1)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "fusion_2"), null, new ItemStack(ModBlocks.tungsten_plating, 16), new Object[] {
				"IWI", "IBI", "IWI", Character.valueOf('I'), itemIngotIron, Character.valueOf('W'), ItemElement.createStackOf(ElementEnum.W, 1), Character.valueOf('B'), ItemElement.createStackOf(ElementEnum.Be, 1)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "fusion_3"), null, new ItemStack(ModBlocks.reactor_core), new Object[] {
				"III", "IBI", "III", Character.valueOf('I'), itemIngotIron, Character.valueOf('B'), ItemElement.createStackOf(ElementEnum.U, 1)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "projector_lens"), null, ModItems.projectorLens, new Object[] {
				"ABA", Character.valueOf('A'), ModItems.concaveLens, Character.valueOf('B'), ModItems.convexLens
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "blueprint_projector"), null, new ItemStack(ModBlocks.blueprintProjector), new Object[] {
				" I ", "GPL", " I ", Character.valueOf('I'), itemIngotIron, Character.valueOf('P'), blockThinGlass, Character.valueOf('L'), ModItems.projectorLens, Character.valueOf('G'), new ItemStack(Blocks.REDSTONE_LAMP)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "leaded_chest"), null, new ItemStack(ModBlocks.leadChest), new Object[] {
				"LLL", "LCL", "LLL", Character.valueOf('L'), new ItemStack(ModItems.element, 1, ElementEnum.Pb.atomicNumber()), Character.valueOf('C'), new ItemStack(Blocks.CHEST)
		});
		GameRegistry.addShapelessRecipe(new ResourceLocation(ModGlobals.ID, "journal"), null, new ItemStack(ModItems.journal), Ingredient.fromItem(Items.BOOK), Ingredient.fromItem(Item.getItemFromBlock(Blocks.GLASS)));
		// Fusion
		GameRegistry.addShapelessRecipe(new ResourceLocation(ModGlobals.ID, "blueprint_fusion"), null, BlueprintUtil.createStack(ModBlueprints.fusion), Ingredient.fromStacks(new ItemStack(Items.PAPER), new ItemStack(Blocks.DIAMOND_BLOCK)));
		// Fission
		GameRegistry.addShapelessRecipe(new ResourceLocation(ModGlobals.ID, "blueprint_fission"), null, BlueprintUtil.createStack(ModBlueprints.fission), Ingredient.fromStacks(new ItemStack(Items.PAPER), new ItemStack(Items.DIAMOND)));
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "empty_tube"), null, ModItems.emptyTube, new Object[] {
				"   ", "P P", " P ", Character.valueOf('P'), blockThinGlass
		});

	}

	public void registerFluidRecipes() {
		int threeQuarterFluidPerIngot = 180;
		RecipeDecomposer.add(new RecipeDecomposerFluid(new FluidStack(FluidRegistry.WATER, BUCKET_AMOUNT), new PotionChemical[] {
				element(ElementEnum.H, 2), element(ElementEnum.O)
		}));

		// Lava
		// This assumes lava is composed from cobblestone at a 4:1 ratio
		// as well as having slightly higher purity
		RecipeDecomposer.add(new RecipeDecomposerFluidSelect("lava", BUCKET_AMOUNT / 4, 0.2F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Si), element(ElementEnum.O)
				}), new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Fe), element(ElementEnum.O)
				}), new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Mg), element(ElementEnum.O)
				}), new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Ti), element(ElementEnum.O)
				}), new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Pb), element(ElementEnum.O)
				}), new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Na), element(ElementEnum.Cl)
				})
		}));

		// Mod fluids
		// Checks if the fluid exists
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("water", BUCKET_AMOUNT, new PotionChemical[] {
				element(ElementEnum.H, 2), element(ElementEnum.O)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("iron.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Fe, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("gold.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Au, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("copper.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Cu, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("tin.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Sn, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("aluminum.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Al, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("cobalt.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Co, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("ardite.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Fe, 2), element(ElementEnum.W, 2), element(ElementEnum.Si, 2)
		});

		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("bronze.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Cu, 12), element(ElementEnum.Sn, 4)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("aluminumbrass.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Cu, 12), element(ElementEnum.Al, 4)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("manyullyn.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Co, 8), element(ElementEnum.Fe, 1), element(ElementEnum.W, 1), element(ElementEnum.Si, 1)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("alumite.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Al, 8), element(ElementEnum.Fe, 3), molecule(MoleculeEnum.siliconDioxide, 2), molecule(MoleculeEnum.magnesiumOxide, 1)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("obsidian.molten", INGOT_AMOUNT, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 16), molecule(MoleculeEnum.magnesiumOxide, 8)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("steel.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Fe, 16), element(ElementEnum.C, 1)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("stone.seared", INGOT_AMOUNT, new PotionChemical[] {
				molecule(MoleculeEnum.siliconOxide, 12), molecule(MoleculeEnum.ironOxide, 4)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("glass.molten", INGOT_AMOUNT, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("emerald.molten", INGOT_AMOUNT, new PotionChemical[] {
				molecule(MoleculeEnum.beryl, 6), element(ElementEnum.Cr, 6), element(ElementEnum.V, 6)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("blood.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.O, 6), element(ElementEnum.Fe, 2), molecule(MoleculeEnum.ironOxide, 8)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("nickel.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Ni, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("lead.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Pb, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("silver.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Ag, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("platinum.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Pt, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("invar.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Fe, 10), element(ElementEnum.Ni, 6)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("electrum.molten", INGOT_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Ag, 8), element(ElementEnum.Au, 8)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("ender", threeQuarterFluidPerIngot, new PotionChemical[] {
				molecule(MoleculeEnum.calciumCarbonate), molecule(MoleculeEnum.calciumCarbonate), molecule(MoleculeEnum.calciumCarbonate), molecule(MoleculeEnum.calciumCarbonate), element(ElementEnum.Es), molecule(MoleculeEnum.calciumCarbonate), molecule(MoleculeEnum.calciumCarbonate), molecule(MoleculeEnum.calciumCarbonate), molecule(MoleculeEnum.calciumCarbonate)
		});

		if (ModConfig.decomposeChemicalFluids) {
			for (ElementEnum element : ModFluids.FLUID_ELEMENTS.keySet()) {
				RecipeDecomposerFluid.add(new RecipeDecomposerFluid(new FluidStack(ModFluids.FLUID_ELEMENTS.get(element), 125), new Element(element, 1)));
			}
			for (MoleculeEnum molecule : ModFluids.FLUID_MOLECULES.keySet()) {
				RecipeDecomposerFluid.add(new RecipeDecomposerFluid(new FluidStack(ModFluids.FLUID_MOLECULES.get(molecule), 125), molecule.componentsArray()));
			}
		}
		registerMFRFluidRecipes();
	}

	private void registerMFRFluidRecipes() {
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("mushroomsoup", BUCKET_AMOUNT, new PotionChemical[] {
				molecule(MoleculeEnum.water, 4), molecule(MoleculeEnum.pantherine, 2)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("chocolatemilk", BUCKET_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Ca, 4), molecule(MoleculeEnum.theobromine, 1)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("milk", BUCKET_AMOUNT, new PotionChemical[] {
				element(ElementEnum.Ca, 4), molecule(MoleculeEnum.oleicAcid, 1)
		});
	}

	public static void registerSynthesisRecipes() {
		getInstance().registerSynthesisMachineRecipes();
	}

	private void registerSynthesisMachineRecipes() {
		RecipeHandlerSynthesis.addShapedRecipe("smooth_stone", COST_SMOOTH, new ItemStack(Blocks.STONE, 16), "ab ", "cd ", 'a', element(ElementEnum.Si), 'b', element(ElementEnum.O, 2), 'c', element(ElementEnum.Al, 2), 'd', element(ElementEnum.O, 3));
		RecipeHandlerSynthesis.addShapedRecipe("grass_block", COST_GLASS, new ItemStack(Blocks.GRASS, 16), " a ", " bc", 'a', moleculeCellulose, 'b', element(ElementEnum.O, 2), 'c', element(ElementEnum.Si));
		RecipeHandlerSynthesis.addShapedRecipe("dirt", COST_BLOCK, new ItemStack(Blocks.DIRT, 1, 0), "a  ", "   ", "   ", 'a', molecule(MoleculeEnum.siliconDioxide));
		RecipeHandlerSynthesis.addShapedRecipe("podzol", COST_BLOCK, new ItemStack(Blocks.DIRT, 1, 2), "   ", "a  ", "   ", 'a', molecule(MoleculeEnum.siliconDioxide));
		RecipeHandlerSynthesis.addShapedRecipe("cobblestone", COST_SMOOTH, new ItemStack(Blocks.COBBLESTONE, 16), "ab ", 'a', element(ElementEnum.Si, 2), 'b', element(ElementEnum.O, 4));
		RecipeHandlerSynthesis.addShapedRecipe("oak_planks", COST_PLANK, blockOakWoodPlanks, "   ", "   ", " aa", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("spruce_planks", COST_PLANK, blockSpruceWoodPlanks, "   ", "   ", "aa ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("birch_planks", COST_PLANK, blockBirchWoodPlanks, "   ", "  a", "a  ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("jungle_planks", COST_PLANK, blockJungleWoodPlanks, "   ", " aa", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("acacia_planks", COST_PLANK, blockAcaciaWoodPlanks, "   ", "aa ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_planks", COST_PLANK, blockDarkOakWoodPlanks, "  a", "a  ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("oak_slab", COST_PLANK, blockOakWoodSlabs, "   ", "   ", "a a", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("spruce_slab", COST_PLANK, blockSpruceWoodSlabs, "   ", "  a", " a ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("birch_slab", COST_PLANK, blockBirchWoodSlabs, "   ", " a ", "a  ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("jungle_slab", COST_PLANK, blockJungleWoodSlabs, "   ", "a a", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("acacia_slab", COST_PLANK, blockAcaciaWoodSlabs, "  a", " a ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_slab", COST_PLANK, blockDarkOakWoodSlabs, " a ", "a  ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("oak_sapling", COST_PLANT, blockOakSapling, "   ", "   ", "  a", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("spruce_sapling", COST_PLANT, blockSpruceSapling, "   ", "   ", " a ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("birch_sapling", COST_PLANT, blockBirchSapling, "   ", "   ", "a  ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("jungle_sapling", COST_PLANT, blockJungleSapling, "   ", "  a", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("acacia_sapling", COST_PLANT, blockAcaciaSapling, "   ", " a ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_sapling", COST_PLANT, blockDarkOakSapling, "   ", "a  ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("block_sand", COST_BLOCK, blockSand, "aaa", 'a', moleculeSiliconDioxide);
		RecipeHandlerSynthesis.addShapedRecipe("block_gravel", COST_BLOCK, blockGravel, "   ", "   ", "  a", 'a', molecule(MoleculeEnum.siliconDioxide));
		RecipeHandlerSynthesis.addShapedRecipe("oak_log", COST_WOOD, blockOakLog, "aaa", " a ", "   ", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("spruce_log", COST_WOOD, blockSpruceLog, "   ", " a ", "aaa", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("birch_log", COST_WOOD, blockBirchLog, "a a", "   ", "a a", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("jungle_log", COST_WOOD, blockJungleLog, "a  ", "aa ", "a  ", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("acacia_log", COST_WOOD, blockAcaciaLog, "  a", " aa", "  a", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_log", COST_WOOD, blockDarkOakLog, " a ", "a a", " a ", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("oak_leaves", COST_BLOCK, blockOakLeaves, "aaa", " a ", "   ", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("spruce_leaves", COST_BLOCK, blockSpruceLeaves, "   ", " a ", "aaa", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("birch_leaves", COST_BLOCK, blockOakLeaves, "a a", "   ", "a a", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("jungle_leaves", COST_BLOCK, blockJungleLeaves, "a  ", "aa ", "a  ", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("acacia_leaves", COST_BLOCK, blockAcaciaLeaves, "  a", " aa", "  a", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_leaves", COST_BLOCK, blockDarkOakLeaves, " a ", "a a", " a ", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapelessRecipe("dye_black", COST_ITEM, itemDyePowderBlack, new PotionChemical[] {
				molecule(MoleculeEnum.blackPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_red", COST_ITEM, itemDyePowderRed, new PotionChemical[] {
				molecule(MoleculeEnum.redPigment)
		});

		RecipeHandlerSynthesis.addShapelessRecipe("dye_green", COST_ITEM, itemDyePowderGreen, new PotionChemical[] {
				molecule(MoleculeEnum.greenPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_brown", COST_ITEM, itemDyePowderBrown, new PotionChemical[] {
				molecule(MoleculeEnum.theobromine)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_blue", COST_ITEM, itemDyePowderBlue, new PotionChemical[] {
				molecule(MoleculeEnum.lazurite)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_purple", COST_ITEM, itemDyePowderPurple, new PotionChemical[] {
				molecule(MoleculeEnum.purplePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_cyan", COST_ITEM, itemDyePowderCyan, new PotionChemical[] {
				molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.whitePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_lightgray", COST_ITEM, itemDyePowderLightGray, new PotionChemical[] {
				molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_gray", COST_ITEM, itemDyePowderGray, new PotionChemical[] {
				molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment, 2)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_pink", COST_ITEM, itemDyePowderPink, new PotionChemical[] {
				molecule(MoleculeEnum.redPigment), molecule(MoleculeEnum.whitePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_lime", COST_ITEM, itemDyePowderLime, new PotionChemical[] {
				molecule(MoleculeEnum.limePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_yellow", COST_ITEM, itemDyePowderYellow, new PotionChemical[] {
				molecule(MoleculeEnum.yellowPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_lightblue", COST_ITEM, itemDyePowderLightBlue, new PotionChemical[] {
				molecule(MoleculeEnum.lightbluePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_magenta", COST_ITEM, itemDyePowderMagenta, new PotionChemical[] {
				molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.redPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_orange", COST_ITEM, itemDyePowderOrange, new PotionChemical[] {
				molecule(MoleculeEnum.orangePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("dye_white", COST_ITEM, itemDyePowderWhite, new PotionChemical[] {
				molecule(MoleculeEnum.whitePigment)
		});
		RecipeHandlerSynthesis.addShapedRecipe("block_glass", COST_GLASS, blockGlass, "a a", "   ", "a a", 'a', moleculeSiliconDioxide);
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_white", COST_GLASS, blockWhiteStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_orange", COST_GLASS, blockOrangeStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.orangePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_magenta", COST_GLASS, blockMagentaStainedGlass, "a a", "b c", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.lightbluePigment), 'c', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_lightblue", COST_GLASS, blockLightBlueStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_yellow", COST_GLASS, blockYellowStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.yellowPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_lime", COST_GLASS, blockLimeStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.limePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_pink", COST_GLASS, blockPinkStainedGlass, "a a", "b c", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_gray", COST_GLASS, blockGrayStainedGlass, "a a", "b c", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_lightgray", COST_GLASS, blockLightGrayStainedGlass, "a a", "bbc", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_cyan", COST_GLASS, blockCyanStainedGlass, "a a", "b c", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_purple", COST_GLASS, blockPurpleStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.purplePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_blue", COST_GLASS, blockBlueStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.lazurite));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_brown", COST_GLASS, blockBrownStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.tannicacid));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_green", COST_GLASS, blockGreenStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.greenPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_red", COST_GLASS, blockRedStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_black", COST_GLASS, blockBlackStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane", COST_PANE, blockGlassPane, "aaa", "   ", "aaa", 'a', siO);
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_white", COST_PANE, blockWhiteStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_orange", COST_PANE, blockOrangeStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.orangePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_magenta", COST_PANE, blockMagentaStainedGlassPane, "aaa", "b c", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.lightbluePigment), 'c', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_lightblue", COST_PANE, blockLightBlueStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_yellow", COST_PANE, blockYellowStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.yellowPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_lime", COST_PANE, blockLimeStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.limePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_pink", COST_PANE, blockPinkStainedGlassPane, "aaa", "b c", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_gray", COST_PANE, blockGrayStainedGlassPane, "aaa", "b c", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_lightgray", COST_PANE, blockLightGrayStainedGlassPane, "aaa", "bbc", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_cyan", COST_PANE, blockCyanStainedGlassPane, "aaa", "b c", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_purple", COST_PANE, blockPurpleStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.purplePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_blue", COST_PANE, blockBlueStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.lazurite));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_brown", COST_PANE, blockBrownStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.tannicacid));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_green", COST_PANE, blockGreenStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.greenPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_red", COST_PANE, blockRedStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_black", COST_PANE, blockBlackStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("lapis_block", COST_LAPISBLOCK, blockLapis, "a  ", "   ", "   ", 'a', moleculeLazurite);
		RecipeHandlerSynthesis.addShapedRecipe("sunflower", COST_PLANT, blockSunFlower, "a", "b", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2), 'b', molecule(MoleculeEnum.yellowPigment));
		RecipeHandlerSynthesis.addShapedRecipe("lilac", COST_PLANT, blockLilac, "abc", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2), 'b', molecule(MoleculeEnum.redPigment), 'c', new Molecule(MoleculeEnum.whitePigment, 2));
		RecipeHandlerSynthesis.addShapedRecipe("tall_grass", COST_PLANT, blockTallGrass, "a  ", "   ", "   ", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2));
		RecipeHandlerSynthesis.addShapedRecipe("large_fern", COST_PLANT, blockLargeFern, " a ", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2));
		RecipeHandlerSynthesis.addShapedRecipe("rose_bush", COST_PLANT, blockRoseBush, "ab ", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2), 'b', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("peony", COST_PLANT, blockPeony, "abc", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2), 'b', molecule(MoleculeEnum.redPigment), 'c', molecule(MoleculeEnum.whitePigment));
		RecipeHandlerSynthesis.addShapedRecipe("sandstone", COST_PLANT, blockSandStone, "   ", " a ", "   ", 'a', molecule(MoleculeEnum.siliconDioxide, 16));
		RecipeHandlerSynthesis.addShapedRecipe("chiseled_sandstone", COST_PLANT, blockChiseledSandStone, "   ", "   ", " a ", 'a', molecule(MoleculeEnum.siliconDioxide, 16));
		RecipeHandlerSynthesis.addShapedRecipe("smooth_sandstone", COST_PLANT, blockSmoothSandStone, " a ", "   ", "   ", 'a', molecule(MoleculeEnum.siliconDioxide, 16));
		RecipeHandlerSynthesis.addShapelessRecipe("wool", COST_WOOL, blockWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.whitePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_orange", COST_WOOL, blockOrangeWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.orangePigment)
		});

		RecipeHandlerSynthesis.addShapelessRecipe("wool_magenta", COST_WOOL, blockMagentaWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.redPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_lightblue", COST_WOOL, blockLightBlueWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.lightbluePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_yellow", COST_WOOL, blockYellowWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.yellowPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_lime", COST_WOOL, blockLimeWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.limePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_pink", COST_WOOL, blockPinkWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.redPigment), molecule(MoleculeEnum.whitePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_gray", COST_WOOL, blockGrayWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment, 2)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_lightgray", COST_WOOL, blockLightGrayWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_cyan", COST_WOOL, blockCyanWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.whitePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_purple", COST_WOOL, blockPurpleWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.purplePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_blue", COST_WOOL, blockBlueWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.lazurite)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_green", COST_WOOL, blockGreenWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.greenPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_red", COST_WOOL, blockRedWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.redPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("wool_black", COST_WOOL, blockBlackWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.blackPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet", COST_CARPET, carpetBlockWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.whitePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_orange", COST_CARPET, carpetBlockOrangeWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.orangePigment)
		});

		RecipeHandlerSynthesis.addShapelessRecipe("carpet_magenta", COST_CARPET, carpetBlockMagentaWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.redPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_lightblue", COST_CARPET, carpetBlockLightBlueWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.lightbluePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_yellow", COST_CARPET, carpetBlockYellowWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.yellowPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_lime", COST_CARPET, carpetBlockLimeWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.limePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_pink", COST_CARPET, carpetBlockPinkWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.redPigment), molecule(MoleculeEnum.whitePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_gray", COST_CARPET, carpetBlockGrayWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment, 2)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_lightgray", COST_CARPET, carpetBlockLightGrayWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_cyan", COST_CARPET, carpetBlockCyanWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.whitePigment)
		});
		//TODO
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_purple", COST_CARPET, carpetBlockPurpleWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.purplePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_blue", COST_CARPET, carpetBlockBlueWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.lazurite)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_green", COST_CARPET, carpetBlockGreenWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.greenPigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_red", COST_CARPET, carpetBlockRedWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.orangePigment)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("carpet_black", COST_CARPET, carpetBlockBlackWool, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.blackPigment)
		});

		RecipeHandlerSynthesis.addShapedRecipe("gold_block", COST_METALBLOCK, new ItemStack(Blocks.GOLD_BLOCK), "aaa", "aaa", "aaa", 'a', element(ElementEnum.Au, 16));
		RecipeHandlerSynthesis.addShapedRecipe("iron_block", COST_METALBLOCK, new ItemStack(Blocks.IRON_BLOCK), "aaa", "aaa", "aaa", 'a', element(ElementEnum.Fe, 16));
		RecipeHandlerSynthesis.addShapelessRecipe("tnt", COST_OBSIDIAN, blockTnt, new PotionChemical[] {
				molecule(MoleculeEnum.tnt)
		});
		RecipeHandlerSynthesis.addShapedRecipe("obsidian", COST_OBSIDIAN, new ItemStack(Blocks.GOLD_BLOCK), "aaa", "b a", "bbb", 'a', molecule(MoleculeEnum.siliconDioxide, 4), 'b', molecule(MoleculeEnum.magnesiumOxide, 2));
		RecipeHandlerSynthesis.addShapedRecipe("diamond_block", COST_GEMBLOCK, blockDiamond, "aaa", "aaa", "aaa", 'a', molecule(MoleculeEnum.fullrene, 3));
		RecipeHandlerSynthesis.addShapedRecipe("cactus_black", COST_PLANT, blockCactus, "a a", " b ", "a a", 'a', molecule(MoleculeEnum.water, 5), 'b', molecule(MoleculeEnum.mescaline));
		RecipeHandlerSynthesis.addShapelessRecipe("block_pumpkin", COST_PLANT, blockPumpkin, new PotionChemical[] {
				molecule(MoleculeEnum.cucurbitacin, 4)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("seed_pumpkin", COST_PLANT, pumpkinSeed, new PotionChemical[] {
				molecule(MoleculeEnum.cucurbitacin)
		});
		RecipeHandlerSynthesis.addShapedRecipe("nether_brick", COST_SMOOTH, itemNetherbrick, "aa ", "bc ", "dd ", 'a', element(ElementEnum.Si, 2), 'b', element(ElementEnum.Zn, 2), 'c', element(ElementEnum.W, 1), 'd', element(ElementEnum.Be, 2));
		RecipeHandlerSynthesis.addShapedRecipe("water_bottle", COST_ITEM, itemPotion, " a ", "aba", " a ", 'a', molecule(MoleculeEnum.siliconDioxide, 4), 'b', molecule(MoleculeEnum.water, 5));
		RecipeHandlerSynthesis.addShapedRecipe("glowstone_block", COST_GLOWBLOCK, blockGlowStone, "aa", "aa", 'a', element(ElementEnum.P));
		RecipeHandlerSynthesis.addShapelessRecipe("block_mycelium", COST_GRASS, new ItemStack(Blocks.MYCELIUM, 16), new PotionChemical[] {
				molecule(MoleculeEnum.fingolimod)
		});
		RecipeHandlerSynthesis.addShapedRecipe("emerald_block", COST_GEMBLOCK, blockEmerald, "aaa", "bcb", "aaa", 'a', element(ElementEnum.Cr, 3), 'b', element(ElementEnum.V, 9), 'c', molecule(MoleculeEnum.beryl, 18));
		RecipeHandlerSynthesis.addShapelessRecipe("apple", COST_FOOD, itemAppleRed, new PotionChemical[] {
				molecule(MoleculeEnum.malicAcid), molecule(MoleculeEnum.water, 2)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("charcoal", COST_ITEM, itemChar, new PotionChemical[] {
				element(ElementEnum.C, 4), element(ElementEnum.C, 4)
		});
		RecipeHandlerSynthesis.addShapedRecipe("diamond", COST_GEM, itemDiamond, "   ", " a ", "   ", 'a', molecule(MoleculeEnum.fullrene, 3));
		RecipeHandlerSynthesis.addShapedRecipe("polytool", COST_STAR, new ItemStack(ModItems.polytool), " a ", "a a", " a ", 'a', molecule(MoleculeEnum.fullrene, 64));
		RecipeHandlerSynthesis.addShapelessRecipe("ingot_iron", COST_INGOT, itemIngotIron, new PotionChemical[] {
				element(ElementEnum.Fe, 16)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("ingot_gold", COST_INGOT, itemIngotGold, new PotionChemical[] {
				element(ElementEnum.Au, 16)
		});
		RecipeHandlerSynthesis.addShapedRecipe("string", COST_ITEM, itemString, "abc", 'a', molecule(MoleculeEnum.serine), 'b', molecule(MoleculeEnum.glycine), 'c', molecule(MoleculeEnum.alinine));
		RecipeHandlerSynthesis.addShapedRecipe("feather", COST_ITEM, itemFeather, "aba", "aca", "ada", 'a', element(ElementEnum.N), 'b', molecule(MoleculeEnum.water, 2), 'c', molecule(MoleculeEnum.water, 1), 'd', molecule(MoleculeEnum.water, 5));
		RecipeHandlerSynthesis.addShapedRecipe("gun_powder", COST_ITEM, itemGunpowder, "ab ", "c  ", "   ", 'a', molecule(MoleculeEnum.potassiumNitrate), 'b', element(ElementEnum.C), 'c', element(ElementEnum.S, 2));
		RecipeHandlerSynthesis.addShapedRecipe("flint", COST_ITEM, itemFlint, " a ", "aaa", 'a', moleculeSiliconDioxide);
		RecipeHandlerSynthesis.addShapedRecipe("bucket", COST_FOOD, itemBucket, "a a", " a ", 'a', element(ElementEnum.Fe, 16));
		RecipeHandlerSynthesis.addShapedRecipe("water_bucket", COST_FOOD, itemBucketWater, "aba", " a ", 'a', element(ElementEnum.Fe, 16), 'b', molecule(MoleculeEnum.water, 16));
		RecipeHandlerSynthesis.addShapedRecipe("redstone_dust", COST_LAPIS, itemRedstoneDust, "  a", " b ", 'a', molecule(MoleculeEnum.iron3oxide), 'b', element(ElementEnum.Cu));
		RecipeHandlerSynthesis.addShapedRecipe("redstone_block", COST_LAPISBLOCK, blockRedstone, "  a", " b ", 'a', molecule(MoleculeEnum.iron3oxide, 9), 'b', element(ElementEnum.Cu, 9));
		RecipeHandlerSynthesis.addShapedRecipe("snowball", COST_FOOD, new ItemStack(Items.SNOWBALL, 5), "a a", " a ", "a a", 'a', molecule(MoleculeEnum.water));
		RecipeHandlerSynthesis.addShapedRecipe("leather", COST_ITEM, new ItemStack(Items.LEATHER, 5), "   ", " a ", "   ", 'a', molecule(MoleculeEnum.keratin));
		RecipeHandlerSynthesis.addShapedRecipe("brick", COST_ITEM, new ItemStack(Items.BRICK, 8), "aa ", "aa ", 'a', molecule(MoleculeEnum.kaolinite));
		RecipeHandlerSynthesis.addShapelessRecipe("item_clay", COST_ITEM, new ItemStack(Items.CLAY_BALL, 2), new PotionChemical[] {
				molecule(MoleculeEnum.kaolinite)
		});
		RecipeHandlerSynthesis.addShapedRecipe("vine", COST_GRASS, itemVine, "a a", "a a", "a a", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("paper", COST_ITEM, new ItemStack(Items.PAPER, 8), " a ", " a ", " a ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapelessRecipe("item_bone", COST_ITEM, itemBone, new PotionChemical[] {
				molecule(MoleculeEnum.hydroxylapatite)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("item_sugar", COST_SUGAR, itemSugar, new PotionChemical[] {
				molecule(MoleculeEnum.sucrose)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("item_melon", COST_FOOD, new ItemStack(Items.MELON), new PotionChemical[] {
				molecule(MoleculeEnum.cucurbitacin), molecule(MoleculeEnum.asparticAcid), molecule(MoleculeEnum.water, 1)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("block_melon", COST_PLANT, itemSugar, new PotionChemical[] {
				molecule(MoleculeEnum.cucurbitacin), molecule(MoleculeEnum.asparticAcid), molecule(MoleculeEnum.water, 16)
		});
		RecipeHandlerSynthesis.addShapedRecipe("cooked_chicken", COST_FOOD, itemChickenCooked, "abc", 'a', element(ElementEnum.K, 16), 'b', element(ElementEnum.Na, 16), 'c', element(ElementEnum.C, 16));
		RecipeHandlerSynthesis.addShapedRecipe("ender_pearl", COST_TEAR, itemEnderPearl, "aaa", "aba", "aaa", 'a', molecule(MoleculeEnum.calciumCarbonate), 'b', element(ElementEnum.Es));
		RecipeHandlerSynthesis.addShapedRecipe("dragon_egg", COST_BLOCK, blockEnderDragonEgg, "abc", " d ", 'a', molecule(MoleculeEnum.calciumCarbonate, 18), 'b', molecule(MoleculeEnum.hydroxylapatite, 8), 'c', element(ElementEnum.Pu, 22), 'd', element(ElementEnum.Fm, 12));
		RecipeHandlerSynthesis.addShapedRecipe("blaze_rod", COST_TEAR, itemBlazeRod, "a  ", "a  ", "a  ", 'a', element(ElementEnum.Pu, 2));
		RecipeHandlerSynthesis.addShapedRecipe("ghast_tear", COST_TEAR, itemGhastTear, "aab", " ab", " b ", 'a', element(ElementEnum.Yb), 'b', element(ElementEnum.No));
		RecipeHandlerSynthesis.addShapedRecipe("spider_eye", COST_ITEM, itemSpiderEye, "a  ", " b ", "  a", 'a', element(ElementEnum.C), 'b', molecule(MoleculeEnum.tetrodotoxin));
		RecipeDecomposer.add(new RecipeDecomposer(itemFermentedSpiderEye, new PotionChemical[] {
				element(ElementEnum.Po), molecule(MoleculeEnum.ethanol)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("magma_cream", COST_TEAR, itemMagmaCream, " a ", "bcb", " b ", 'a', element(ElementEnum.Pu), 'b', molecule(MoleculeEnum.pmma), 'c', element(ElementEnum.Hg));
		RecipeHandlerSynthesis.addShapedRecipe("emerald", 5000, itemEmerald, " a ", "bcb", " a ", 'a', element(ElementEnum.Cr), 'b', element(ElementEnum.V), 'c', molecule(MoleculeEnum.beryl, 2));
		RecipeHandlerSynthesis.addShapedRecipe("nether_star", COST_STAR, itemNetherStar, "aaa", "bca", "ddd", 'a', elementHelium, 'b', elementCarbon, 'c', element(ElementEnum.Cn, 16), 'd', elementHydrogen);
		RecipeHandlerSynthesis.addShapedRecipe("record_13", COST_GEM, itemRecord13, "aa ", "   ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_cat", COST_GEM, itemRecordCat, " aa", "   ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_far", COST_GEM, itemRecordFar, "  a", "a  ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_mall", COST_GEM, itemRecordMall, "   ", "aa ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_mellohi", COST_GEM, itemRecordMellohi, "   ", " aa", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_stal", COST_GEM, itemRecordStal, "   ", "  a", "a  ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_strad", COST_GEM, itemRecordStrad, "   ", "   ", "aa ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_ward", COST_GEM, itemRecordWard, "   ", "   ", " aa", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_chirp", COST_GEM, itemRecordChirp, "a  ", "   ", "  a", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_11", COST_GEM, itemRecord11, "a  ", " a ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_wait", COST_GEM, itemRecordWait, "a  ", "a  ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_blocks", COST_GEM, itemRecordBlocks, "a  ", " a ", "   ", 'a', moleculePolyvinylChloride);

		//OreDict
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotIron", COST_INGOT, element(ElementEnum.Fe, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotGold", COST_INGOT, element(ElementEnum.Au, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotCopper", COST_INGOT, element(ElementEnum.Cu, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotTin", COST_INGOT, element(ElementEnum.Sn, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotSilver", COST_INGOT, element(ElementEnum.Ag, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotLead", COST_INGOT, element(ElementEnum.Pb, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotPlatinum", COST_INGOT, element(ElementEnum.Pt, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotAluminium", COST_INGOT, element(ElementEnum.Al, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotMagnesium", COST_INGOT, element(ElementEnum.Mg, 16));
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotSteel", COST_INGOT, new PotionChemical[] {
				element(ElementEnum.Fe, 15), element(ElementEnum.C, 1)
		});
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotHSLA", COST_INGOT, new PotionChemical[] {
				element(ElementEnum.Fe, 15), element(ElementEnum.C, 1)
		});
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotBronze", COST_INGOT, new PotionChemical[] {
				element(ElementEnum.Cu, 12), element(ElementEnum.Sn, 4)
		});
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotElectrum", COST_INGOT, new PotionChemical[] {
				element(ElementEnum.Ag, 8), element(ElementEnum.Au, 8)
		});
		RecipeHandlerSynthesis.addShapelessOreDictRecipe("ingotInvar", COST_INGOT, new PotionChemical[] {
				element(ElementEnum.Fe, 10), element(ElementEnum.Ni, 6)
		});
		RecipeHandlerSynthesis.addShapelessRecipe("block_ironbars", COST_BLOCK, bars, new PotionChemical[] {
				element(ElementEnum.Fe, 3), element(ElementEnum.Fe, 3)
		});

		for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
			if (molecule != null) {
				ItemStack moleculeItemStack = new ItemStack(ModItems.molecule, 1, molecule.id());
				RecipeHandlerSynthesis.addShapelessRecipe("molecule_" + molecule.name().toLowerCase(Locale.US), COST_ITEM * molecule.components().size(), moleculeItemStack, molecule.components().toArray(new PotionChemical[molecule.components().size()]));
			}
		}

		//addUnusedSynthesisRecipes();
	}

	public void registerDecomposerRecipes() {
		/*
		RecipeDecomposer.add((new RecipeDecomposerChance(blockStone, 0.2F, new PotionChemical[] {
				element(ElementEnum.Si)
		})));
		*/
		// Smooth Stone
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockStone, 0.07F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Si), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Mg), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Ti), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Pb), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Zn), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Al), element(ElementEnum.O)))));

		// Grass Block
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockGrass, 0.07F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Si), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Mg), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Ti), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Pb), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Zn), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(moleculeCellulose))));

		// Dirt
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockDirt, 0.07F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Si), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Mg), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Ti), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Pb), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Zn), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Ga), element(ElementEnum.O)))));

		RecipeDecomposer.add(new RecipeDecomposerSelect(blockPodzol, 0.07F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Si), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Mg), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Ti), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Pb), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Zn), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Ga), element(ElementEnum.O)))));

		// Cobblestone

		RecipeDecomposer.add(new RecipeDecomposerSelect(blockCobblestone, 0.07F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Si), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Mg), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Ti), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Pb), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Na), element(ElementEnum.Cl)))));

		// Planks

		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakWoodPlanks, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceWoodPlanks, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchWoodPlanks, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleWoodPlanks, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaWoodPlanks, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakWoodPlanks, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));

		// Wooden Slabs

		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakWoodSlabs, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceWoodSlabs, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchWoodSlabs, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleWoodSlabs, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaWoodSlabs, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakWoodSlabs, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));

		// Saplings

		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakSapling, 0.25F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceSapling, 0.25F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchSapling, 0.25F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleSapling, 0.25F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaSapling, 0.25F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakSapling, 0.25F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose)
		}));

		// Sand

		RecipeDecomposer.add(new RecipeDecomposer(blockSand, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 16)
		}));

		// Gravel

		RecipeDecomposer.add(new RecipeDecomposerChance(blockGravel, 0.35F, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide)
		}));

		// Gold Ore

		RecipeDecomposer.add(new RecipeDecomposer(goldOre, new PotionChemical[] {
				element(ElementEnum.Au, 48)
		}));

		// Iron Ore

		RecipeDecomposer.add(new RecipeDecomposer(ironOre, new PotionChemical[] {
				element(ElementEnum.Fe, 48)
		}));

		// Coal Ore

		RecipeDecomposer.add(new RecipeDecomposer(coalOre, new PotionChemical[] {
				element(ElementEnum.C, 48)
		}));

		// Log

		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakLog, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceLog, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchLog, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleLog, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaLog, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakLog, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 8)
		}));

		// Leaves

		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakLeaves, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceLeaves, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchLeaves, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleLeaves, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaLeaves, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakLeaves, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 4)
		}));

		// Dyes

		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderBlack, new PotionChemical[] {
				molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderRed, new PotionChemical[] {
				molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderGreen, new PotionChemical[] {
				molecule(MoleculeEnum.greenPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDyePowderBrown, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.theobromine), molecule(MoleculeEnum.tannicacid)
		}));
		// Lapis
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderBlue, new PotionChemical[] {
				molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderPurple, new PotionChemical[] {
				molecule(MoleculeEnum.purplePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderCyan, new PotionChemical[] {
				molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderLightGray, new PotionChemical[] {
				molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderGray, new PotionChemical[] {
				molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderPink, new PotionChemical[] {
				molecule(MoleculeEnum.redPigment), molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderLime, new PotionChemical[] {
				molecule(MoleculeEnum.limePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderYellow, new PotionChemical[] {
				molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderLightBlue, new PotionChemical[] {
				molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderMagenta, new PotionChemical[] {
				molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderOrange, new PotionChemical[] {
				molecule(MoleculeEnum.orangePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderWhite, new PotionChemical[] {
				molecule(MoleculeEnum.whitePigment)
		}));

		// Glass

		RecipeDecomposer.add(new RecipeDecomposer(blockGlass, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 16)
		}));

		// Glass Panes

		RecipeDecomposer.add(new RecipeDecomposer(blockGlassPane, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 6)
		}));

		// Lapis Lazuli Ore

		RecipeDecomposer.add(new RecipeDecomposer(blockOreLapis, new PotionChemical[] {
				molecule(MoleculeEnum.lazurite, 6), molecule(MoleculeEnum.sodalite), molecule(MoleculeEnum.noselite), molecule(MoleculeEnum.calciumCarbonate), molecule(MoleculeEnum.pyrite)
		}));

		// Lapis Lazuli Block

		RecipeDecomposer.add(new RecipeDecomposer(blockLapis, new PotionChemical[] {
				molecule(MoleculeEnum.lazurite, 9)
		}));

		// Cobweb

		RecipeDecomposer.add(new RecipeDecomposer(blockCobweb, new PotionChemical[] {
				molecule(MoleculeEnum.fibroin)
		}));

		//double tall plants

		RecipeDecomposer.add(new RecipeDecomposerChance(blockSunFlower, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLilac, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockTallGrass, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLargeFern, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRoseBush, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPeony, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));

		// Sandstone

		RecipeDecomposer.add(new RecipeDecomposer(blockSandStone, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(blockChiseledSandStone, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(blockSmoothSandStone, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 16)
		}));

		// Wool

		RecipeDecomposer.add(new RecipeDecomposerChance(blockWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOrangeWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.orangePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockMagentaWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLightBlueWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockYellowWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLimeWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.limePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPinkWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.redPigment), molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockGrayWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLightGrayWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockCyanWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPurpleWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.purplePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBlueWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBrownWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.tannicacid)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockGreenWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.greenPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRedWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBlackWool, 0.6F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine, 2), molecule(MoleculeEnum.blackPigment)
		}));

		// Wool carpet

		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockOrangeWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.orangePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockMagentaWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockLightBlueWool, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockYellowWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockLimeWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.limePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockPinkWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.redPigment), molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockGrayWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockLightGrayWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockCyanWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockPurpleWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.purplePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockBlueWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockBrownWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.tannicacid)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockGreenWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.greenPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockRedWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockBlackWool, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.blackPigment)
		}));

		// Flowers

		RecipeDecomposer.add(new RecipeDecomposerChance(blockYellowFlower, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.yellowPigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPoppyFlower, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.redPigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBlueOrchid, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAllium, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.purplePigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAsureBluet, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.whitePigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRedTulip, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.redPigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOrangeTulip, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.orangePigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockWhiteTulip, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.whitePigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPinkTulip, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.whitePigment, 1), new Molecule(MoleculeEnum.redPigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOxeyeDaisy, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2), new Molecule(MoleculeEnum.whitePigment, 1)
		}));

		// Mushrooms

		if (ModConfig.recreationalChemicalEffects) {
			RecipeDecomposer.add(new RecipeDecomposer(blockMushroomBrown, new PotionChemical[] {
					molecule(MoleculeEnum.psilocybin), molecule(MoleculeEnum.water, 2)
			}));
		}
		RecipeDecomposer.add(new RecipeDecomposer(blockMushroomRed, new PotionChemical[] {
				molecule(MoleculeEnum.pantherine), molecule(MoleculeEnum.water, 2)
		}));

		// Block of Gold
		RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(Blocks.GOLD_BLOCK), new PotionChemical[] {
				element(ElementEnum.Au, 144)
		}));

		// Block of Iron
		RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(Blocks.IRON_BLOCK), new PotionChemical[] {
				element(ElementEnum.Fe, 144)
		}));

		// TNT

		RecipeDecomposer.add(new RecipeDecomposer(blockTnt, new PotionChemical[] {
				molecule(MoleculeEnum.tnt)
		}));

		// Obsidian

		RecipeDecomposer.add(new RecipeDecomposer(blockObsidian, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 16), molecule(MoleculeEnum.magnesiumOxide, 8)
		}));

		// Diamond Ore

		RecipeDecomposer.add(new RecipeDecomposer(blockOreDiamond, new PotionChemical[] {
				molecule(MoleculeEnum.fullrene, 6)
		}));

		// Block of Diamond

		RecipeDecomposer.add(new RecipeDecomposer(blockDiamond, new PotionChemical[] {
				molecule(MoleculeEnum.fullrene, 27)
		}));

		// Pressure Plate

		RecipeDecomposer.add(new RecipeDecomposerChance(blockPressurePlatePlanks, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 4)
		}));

		// Redstone Ore

		RecipeDecomposer.add(new RecipeDecomposerChance(blockOreRedstone, 0.8F, new PotionChemical[] {
				molecule(MoleculeEnum.iron3oxide, 9), element(ElementEnum.Cu, 9)
		}));

		// Cactus

		RecipeDecomposer.add(new RecipeDecomposer(blockCactus, new PotionChemical[] {
				molecule(MoleculeEnum.mescaline), molecule(MoleculeEnum.water, 20)
		}));

		// Pumpkin

		RecipeDecomposer.add(new RecipeDecomposer(blockPumpkin, new PotionChemical[] {
				molecule(MoleculeEnum.cucurbitacin, 4)
		}));

		// Pumpkin seed

		RecipeDecomposer.add(new RecipeDecomposer(pumpkinSeed, new PotionChemical[] {
				molecule(MoleculeEnum.cucurbitacin)
		}));

		// Netherrack

		RecipeDecomposer.add(new RecipeDecomposerSelect(blockNetherrack, 0.15F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 2), element(ElementEnum.O), element(ElementEnum.Fe)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 2), element(ElementEnum.Ni), element(ElementEnum.Tc)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 3), element(ElementEnum.Ti), element(ElementEnum.Fe)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si), element(ElementEnum.W, 4), element(ElementEnum.Cr, 2)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 10), element(ElementEnum.W), element(ElementEnum.Zn, 8), element(ElementEnum.Be, 4)))));

		// Nether Brick

		RecipeDecomposer.add(new RecipeDecomposerSelect(itemNetherbrick, 0.15F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 2), element(ElementEnum.C), element(ElementEnum.Fe)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 2), element(ElementEnum.Ni), element(ElementEnum.Tc)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 3), element(ElementEnum.Ti), element(ElementEnum.Fe)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si), element(ElementEnum.W, 4), element(ElementEnum.Cr, 2)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 10), element(ElementEnum.W), element(ElementEnum.Zn, 8), element(ElementEnum.Be, 4)))));

		// Water Bottle

		RecipeDecomposer.add(new RecipeDecomposer(itemPotion, new PotionChemical[] {
				molecule(MoleculeEnum.water, 5), molecule(MoleculeEnum.siliconDioxide, 16)
		}));

		//Ice Block

		RecipeDecomposer.add(new RecipeDecomposer(blockIce, new PotionChemical[] {
				molecule(MoleculeEnum.water, 8)
		}));

		// Soul Sand

		RecipeDecomposer.add(new RecipeDecomposerSelect(blockSlowSand, 0.2F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Pb, 2), element(ElementEnum.Be), element(ElementEnum.Si, 2), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Pb), element(ElementEnum.Si, 5), element(ElementEnum.O, 2)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 2), element(ElementEnum.O)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 6), element(ElementEnum.O, 2)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Es), element(ElementEnum.O, 2)))));

		// Glowstone

		RecipeDecomposer.add(new RecipeDecomposer(blockGlowStone, new PotionChemical[] {
				element(ElementEnum.P, 4)
		}));

		// Mycelium

		RecipeDecomposer.add(new RecipeDecomposerChance(blockMycelium, 0.09F, new PotionChemical[] {
				molecule(MoleculeEnum.fingolimod)
		}));

		// End Stone

		RecipeDecomposer.add(new RecipeDecomposerSelect(blockWhiteStone, 0.8F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 2), element(ElementEnum.O), element(ElementEnum.H, 4), element(ElementEnum.Li)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Es)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Pu)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fr)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Nd)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Nd)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Si, 2), element(ElementEnum.O, 4)), Lists.<PotionChemical>newArrayList(element(ElementEnum.H, 4)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Be, 8)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Li, 2)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Zr)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Rb)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Ga), element(ElementEnum.As)))));

		// Emerald Ore

		RecipeDecomposer.add(new RecipeDecomposer(blockOreEmerald, new PotionChemical[] {
				molecule(MoleculeEnum.beryl, 6), element(ElementEnum.Cr, 6), element(ElementEnum.V, 6)
		}));

		// Emerald Block

		RecipeDecomposer.add(new RecipeDecomposer(blockEmerald, new PotionChemical[] {
				molecule(MoleculeEnum.beryl, 18), element(ElementEnum.Cr, 18), element(ElementEnum.V, 18)
		}));

		// Section 2 - Items
		// Apple

		RecipeDecomposer.add(new RecipeDecomposer(itemAppleRed, new PotionChemical[] {
				molecule(MoleculeEnum.malicAcid)
		}));

		// Arrow

		RecipeDecomposer.add(new RecipeDecomposer(itemArrow, new PotionChemical[] {
				element(ElementEnum.Si), element(ElementEnum.O, 2), element(ElementEnum.N, 6)
		}));

		// Coal

		RecipeDecomposer.add(new RecipeDecomposerChance(itemCoal, 0.92F, new PotionChemical[] {
				element(ElementEnum.C, 8)
		}));

		// Coal Block

		RecipeDecomposer.add(new RecipeDecomposerChance(blockCoal, 0.82F, new PotionChemical[] {
				element(ElementEnum.C, 72)
		}));

		// Charcoal

		RecipeDecomposer.add(new RecipeDecomposerChance(itemChar, 0.82F, new PotionChemical[] {
				element(ElementEnum.C, 8)
		}));

		// Diamond

		RecipeDecomposer.add(new RecipeDecomposer(itemDiamond, new PotionChemical[] {
				molecule(MoleculeEnum.fullrene, 3)
		}));

		// Polytool

		// Iron Ingot

		RecipeDecomposer.add(new RecipeDecomposer(itemIngotIron, new PotionChemical[] {
				element(ElementEnum.Fe, 16)
		}));

		// Gold Ingot

		RecipeDecomposer.add(new RecipeDecomposer(itemIngotGold, new PotionChemical[] {
				element(ElementEnum.Au, 16)
		}));

		// Stick

		RecipeDecomposer.add(new RecipeDecomposerChance(itemStick, 0.3F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose)
		}));

		// String

		RecipeDecomposer.add(new RecipeDecomposerChance(itemString, 0.45F, new PotionChemical[] {
				molecule(MoleculeEnum.serine), molecule(MoleculeEnum.glycine), molecule(MoleculeEnum.alinine)
		}));

		// Feather

		RecipeDecomposer.add(new RecipeDecomposer(itemFeather, new PotionChemical[] {
				molecule(MoleculeEnum.water, 8), element(ElementEnum.N, 6)
		}));

		// Gunpowder

		RecipeDecomposer.add(new RecipeDecomposer(itemGunpowder, new PotionChemical[] {
				molecule(MoleculeEnum.potassiumNitrate), element(ElementEnum.S, 2), element(ElementEnum.C)
		}));

		// Bread

		RecipeDecomposer.add(new RecipeDecomposerChance(itemBread, 0.1F, new PotionChemical[] {
				molecule(MoleculeEnum.starch), molecule(MoleculeEnum.sucrose)
		}));

		// Flint

		RecipeDecomposer.add(new RecipeDecomposerChance(itemFlint, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide)
		}));

		// Golden Apple

		RecipeDecomposer.add(new RecipeDecomposer(itemAppleGold, new PotionChemical[] {
				molecule(MoleculeEnum.malicAcid), element(ElementEnum.Au, 64)
		}));

		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorAcacia, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorBirch, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorDarkOak, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorJungle, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorOak, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorSpruce, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 12)
		}));

		// Bucket

		RecipeDecomposer.add(new RecipeDecomposer(itemBucket, element(ElementEnum.Fe, 48)));

		// Water Bucket

		RecipeDecomposer.add(new RecipeDecomposer(itemBucketWater, new PotionChemical[] {
				molecule(MoleculeEnum.water, 16), element(ElementEnum.Fe, 48)
		}));

		// Redstone Dust

		RecipeDecomposer.add(new RecipeDecomposerChance(itemRedstoneDust, 0.42F, new PotionChemical[] {
				molecule(MoleculeEnum.iron3oxide), element(ElementEnum.Cu)
		}));

		// Redstone Block

		RecipeDecomposer.add(new RecipeDecomposerChance(blockRedstone, 0.42F, new PotionChemical[] {
				molecule(MoleculeEnum.iron3oxide, 9), element(ElementEnum.Cu, 9)
		}));

		// Snowball

		RecipeDecomposer.add(new RecipeDecomposer(itemSnowball, new PotionChemical[] {
				molecule(MoleculeEnum.water)
		}));

		// Leather

		RecipeDecomposer.add(new RecipeDecomposerChance(itemLeather, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.keratin)
		}));

		// Brick

		RecipeDecomposer.add(new RecipeDecomposerChance(itemBrick, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.kaolinite)
		}));

		// Clay

		RecipeDecomposer.add(new RecipeDecomposerChance(itemClayBall, 0.5F, new PotionChemical[] {
				molecule(MoleculeEnum.kaolinite)
		}));

		// Reed

		RecipeDecomposer.add(new RecipeDecomposerChance(itemReed, 0.65F, new PotionChemical[] {
				molecule(MoleculeEnum.sucrose), element(ElementEnum.H, 2), element(ElementEnum.O)
		}));

		// Vines

		RecipeDecomposer.add(new RecipeDecomposer(itemVine, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 6)
		}));

		// Paper

		RecipeDecomposer.add(new RecipeDecomposerChance(itemPaper, 0.35F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose)
		}));

		//Compass

		RecipeDecomposer.add(new RecipeDecomposerSelect(itemCompass, 1.0F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe, 64)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe, 64), molecule(MoleculeEnum.iron3oxide)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe, 64), molecule(MoleculeEnum.iron3oxide), element(ElementEnum.Cu)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Fe, 64), element(ElementEnum.Cu)))));

		// Map

		RecipeDecomposer.add(new RecipeDecomposerSuper(itemMap, NonNullList.from(ItemStack.EMPTY, itemPaper, itemPaper, itemPaper, itemPaper, itemPaper, itemPaper, itemPaper, itemPaper, itemCompass)));

		// Book

		RecipeDecomposer.add(new RecipeDecomposerSuper(itemBook, NonNullList.from(ItemStack.EMPTY, itemPaper, itemPaper, itemPaper, itemLeather)));

		// Bookshelf

		RecipeDecomposer.add(new RecipeDecomposerSuper(blockBook, NonNullList.from(ItemStack.EMPTY, blockBirchWoodPlanks, blockBirchWoodPlanks, blockBirchWoodPlanks, itemBook, itemBook, itemBook, blockBirchWoodPlanks, blockBirchWoodPlanks, blockBirchWoodPlanks)));

		// Slimeball

		RecipeDecomposer.add(new RecipeDecomposerSelect(itemCompass, 1.0F, Lists.<ArrayList<PotionChemical>>newArrayList(Lists.<PotionChemical>newArrayList(molecule(MoleculeEnum.pmma)), Lists.<PotionChemical>newArrayList(element(ElementEnum.Hg)), Lists.<PotionChemical>newArrayList(molecule(MoleculeEnum.water, 10)))));

		// Glowstone Dust

		RecipeDecomposer.add(new RecipeDecomposer(itemGlowstone, new PotionChemical[] {
				element(ElementEnum.P)
		}));

		// Bone

		RecipeDecomposer.add(new RecipeDecomposer(itemBone, new PotionChemical[] {
				molecule(MoleculeEnum.hydroxylapatite)
		}));

		// Sugar

		RecipeDecomposer.add(new RecipeDecomposerChance(itemSugar, 0.75F, new PotionChemical[] {
				molecule(MoleculeEnum.sucrose)
		}));

		// Melon Slice

		RecipeDecomposer.add(new RecipeDecomposer(itemMelon, new PotionChemical[] {
				molecule(MoleculeEnum.water, 1)
		}));

		// Melon

		RecipeDecomposer.add(new RecipeDecomposer(blockMelon, new PotionChemical[] {
				molecule(MoleculeEnum.cucurbitacin), molecule(MoleculeEnum.asparticAcid), molecule(MoleculeEnum.water, 16)
		}));

		// Cooked Chicken

		RecipeDecomposer.add(new RecipeDecomposer(itemChickenCooked, new PotionChemical[] {
				element(ElementEnum.K), element(ElementEnum.Na), element(ElementEnum.C, 2)
		}));

		// Rotten Flesh

		RecipeDecomposer.add(new RecipeDecomposerChance(itemRottenFlesh, 0.05F, new PotionChemical[] {
				new Molecule(MoleculeEnum.nodularin, 1)
		}));

		// Enderpearl

		RecipeDecomposer.add(new RecipeDecomposer(itemEnderPearl, new PotionChemical[] {
				element(ElementEnum.Es), molecule(MoleculeEnum.calciumCarbonate, 8)
		}));

		// EnderDragon Egg

		RecipeDecomposer.add(new RecipeDecomposer(blockEnderDragonEgg, new PotionChemical[] {
				molecule(MoleculeEnum.calciumCarbonate, 16), molecule(MoleculeEnum.hydroxylapatite, 6), element(ElementEnum.Pu, 18), element(ElementEnum.Fm, 8)
		}));

		// Blaze Rod

		RecipeDecomposer.add(new RecipeDecomposer(itemBlazeRod, new PotionChemical[] {
				element(ElementEnum.Pu, 6)
		}));

		// Blaze Powder

		RecipeDecomposer.add(new RecipeDecomposer(itemBlazePowder, new PotionChemical[] {
				element(ElementEnum.Pu)
		}));

		// Ghast Tear

		RecipeDecomposer.add(new RecipeDecomposer(itemGhastTear, new PotionChemical[] {
				element(ElementEnum.Yb, 4), element(ElementEnum.No, 4)
		}));

		if (ModConfig.recreationalChemicalEffects) {
			// Nether Wart

			RecipeDecomposer.add(new RecipeDecomposerChance(itemNetherStalkSeeds, 0.5F, new PotionChemical[] {
					molecule(MoleculeEnum.cocainehcl)
			}));
		}

		// Spider Eye

		RecipeDecomposer.add(new RecipeDecomposerChance(itemSpiderEye, 0.2F, new PotionChemical[] {
				molecule(MoleculeEnum.tetrodotoxin)
		}));

		// Fermented Spider Eye

		RecipeDecomposer.add(new RecipeDecomposer(itemFermentedSpiderEye, new PotionChemical[] {
				element(ElementEnum.Po), molecule(MoleculeEnum.ethanol)
		}));

		// Magma Cream

		RecipeDecomposer.add(new RecipeDecomposer(itemMagmaCream, new PotionChemical[] {
				element(ElementEnum.Hg), element(ElementEnum.Pu), molecule(MoleculeEnum.pmma, 3)
		}));

		// Glistering Melon

		RecipeDecomposer.add(new RecipeDecomposer(itemSpeckledMelon, new PotionChemical[] {
				molecule(MoleculeEnum.water, 4), molecule(MoleculeEnum.whitePigment), element(ElementEnum.Au, 1)
		}));

		// Emerald

		RecipeDecomposer.add(new RecipeDecomposer(itemEmerald, new PotionChemical[] {
				molecule(MoleculeEnum.beryl, 2), element(ElementEnum.Cr, 2), element(ElementEnum.V, 2)
		}));

		// Wheat

		RecipeDecomposer.add(new RecipeDecomposerChance(itemWheat, 0.3F, new PotionChemical[] {
				molecule(MoleculeEnum.cellulose, 2)
		}));

		// Carrot

		RecipeDecomposer.add(new RecipeDecomposer(itemCarrot, new PotionChemical[] {
				molecule(MoleculeEnum.retinol)
		}));

		// Potato

		RecipeDecomposer.add(new RecipeDecomposerChance(itemPotato, 0.4F, new PotionChemical[] {
				molecule(MoleculeEnum.water, 8), element(ElementEnum.K, 2), molecule(MoleculeEnum.cellulose)
		}));

		// Golden Carrot

		RecipeDecomposer.add(new RecipeDecomposer(itemGoldenCarrot, new PotionChemical[] {
				molecule(MoleculeEnum.retinol), element(ElementEnum.Au, 4)
		}));

		// Nether Star

		RecipeDecomposer.add(new RecipeDecomposer(itemNetherStar, new PotionChemical[] {
				elementHelium, elementHelium, elementHelium, elementCarbon, element(ElementEnum.Cn, 16), elementHelium, elementHydrogen, elementHydrogen, elementHydrogen
		}));

		// Nether Quartz

		RecipeDecomposer.add(new RecipeDecomposer(itemNetherQuartz, new PotionChemical[] {
				molecule(MoleculeEnum.siliconDioxide, 4), molecule(MoleculeEnum.galliumarsenide, 1)
		}));

		// Music Records

		RecipeDecomposer.add(new RecipeDecomposer(itemRecord13, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordCat, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordFar, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordMall, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordMellohi, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordStal, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordStrad, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordWard, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordChirp, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecord11, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordWait, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordBlocks, moleculePolyvinylChloride, moleculePolyvinylChloride));

		//Ironbars

		RecipeDecomposer.add(new RecipeDecomposer(bars, new PotionChemical[] {
				element(ElementEnum.Fe, 3), element(ElementEnum.Fe, 3)
		}));

		//Uranium Ore
		RecipeDecomposer.addOreDictRecipe("oreUranium", element(ElementEnum.U, 48));

		for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
			if (molecule != null) {
				ItemStack itemStack = new ItemStack(ModItems.molecule, 1, molecule.id());
				RecipeDecomposer.add(new RecipeDecomposer(itemStack, molecule.components()));
			}
		}
	}

	public void registerOreDictRecipes() {
		RecipeDecomposer.addOreDictRecipe("dustSalt", new Element(ElementEnum.Na), new Element(ElementEnum.Cl));
		RecipeDecomposer.addOreDictRecipe("logWood", new Molecule(MoleculeEnum.cellulose, 8));
		RecipeDecomposer.addOreDictRecipe("plankWood", new Molecule(MoleculeEnum.cellulose, 2));
		RecipeDecomposer.addOreDictRecipe("ingotIron", element(ElementEnum.Fe, 16));
		RecipeDecomposer.addOreDictRecipe("ingotGold", element(ElementEnum.Au, 16));
		RecipeDecomposer.addOreDictRecipe("ingotCopper", element(ElementEnum.Cu, 16));
		RecipeDecomposer.addOreDictRecipe("ingotTin", element(ElementEnum.Sn, 16));
		RecipeDecomposer.addOreDictRecipe("ingotSilver", element(ElementEnum.Ag, 16));
		RecipeDecomposer.addOreDictRecipe("ingotLead", element(ElementEnum.Pb, 16));
		RecipeDecomposer.addOreDictRecipe("ingotPlatinum", element(ElementEnum.Pt, 16));
		RecipeDecomposer.addOreDictRecipe("ingotAluminium", element(ElementEnum.Al, 16));
		RecipeDecomposer.addOreDictRecipe("ingotMagnesium", element(ElementEnum.Mg, 16));
		RecipeDecomposer.addOreDictRecipe("ingotSteel", new PotionChemical[] {
				element(ElementEnum.Fe, 15), element(ElementEnum.C, 1)
		});
		RecipeDecomposer.addOreDictRecipe("ingotHSLA", new PotionChemical[] {
				element(ElementEnum.Fe, 15), element(ElementEnum.C, 1)
		});
		RecipeDecomposer.addOreDictRecipe("ingotBronze", new PotionChemical[] {
				element(ElementEnum.Cu, 12), element(ElementEnum.Sn, 4)
		});
		RecipeDecomposer.addOreDictRecipe("ingotElectrum", new PotionChemical[] {
				element(ElementEnum.Ag, 8), element(ElementEnum.Au, 8)
		});
		RecipeDecomposer.addOreDictRecipe("ingotInvar", new PotionChemical[] {
				element(ElementEnum.Fe, 10), element(ElementEnum.Ni, 6)
		});

		for (String oreName : OreDictionary.getOreNames()) {
			if (oreName.contains("gemApatite")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						element(ElementEnum.Ca, 5), molecule(MoleculeEnum.phosphate, 4), element(ElementEnum.Cl)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						element(ElementEnum.Ca, 5), molecule(MoleculeEnum.phosphate, 4), element(ElementEnum.Cl)
				});
			}
			else if (oreName.contains("plateSilicon")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						element(ElementEnum.Si, 2)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						element(ElementEnum.Si, 2)
				});
			}
			else if (oreName.contains("xychoriumBlue")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.Cu, 1)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 300, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.Cu, 1)
				});
			}
			else if (oreName.contains("xychoriumRed")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.Fe, 1)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 300, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.Fe, 1)
				});
			}
			else if (oreName.contains("xychoriumGreen")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.V, 1)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 300, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.V, 1)
				});
			}
			else if (oreName.contains("xychoriumDark")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.Si, 1)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 300, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.Si, 1)
				});
			}
			else if (oreName.contains("xychoriumLight")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.Ti, 1)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 300, new PotionChemical[] {
						element(ElementEnum.Zr, 2), element(ElementEnum.Ti, 1)
				});
			}
			else if (oreName.contains("gemPeridot")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						molecule(MoleculeEnum.olivine)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						molecule(MoleculeEnum.olivine)
				});
			}
			else if (oreName.contains("cropMaloberry")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						molecule(MoleculeEnum.xylitol), molecule(MoleculeEnum.sucrose)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						molecule(MoleculeEnum.xylitol), molecule(MoleculeEnum.sucrose)
				});
			}
			else if (oreName.contains("cropDuskberry")) {
				if (ModConfig.recreationalChemicalEffects) {
					RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
							molecule(MoleculeEnum.psilocybin), element(ElementEnum.S, 2)
					});
					RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
							molecule(MoleculeEnum.psilocybin), element(ElementEnum.S, 2)
					});
				}
			}
			else if (oreName.contains("cropSkyberry")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						molecule(MoleculeEnum.theobromine), element(ElementEnum.S, 2)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						molecule(MoleculeEnum.theobromine), element(ElementEnum.S, 2)
				});
			}
			else if (oreName.contains("cropBlightberry")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						molecule(MoleculeEnum.asprin), element(ElementEnum.S, 2)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						molecule(MoleculeEnum.asprin), element(ElementEnum.S, 2)
				});
			}
			else if (oreName.contains("cropBlueberry")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						molecule(MoleculeEnum.blueorgodye), molecule(MoleculeEnum.sucrose, 2)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						molecule(MoleculeEnum.blueorgodye), molecule(MoleculeEnum.sucrose, 2)
				});
			}
			else if (oreName.contains("cropRaspberry")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						molecule(MoleculeEnum.redorgodye), molecule(MoleculeEnum.sucrose, 2)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						molecule(MoleculeEnum.redorgodye), molecule(MoleculeEnum.sucrose, 2)
				});
			}
			else if (oreName.contains("cropBlackberry")) {
				RecipeDecomposer.addOreDictRecipe(oreName, new PotionChemical[] {
						molecule(MoleculeEnum.purpleorgodye), molecule(MoleculeEnum.sucrose, 2)
				});
				RecipeHandlerSynthesis.addShapelessOreDictRecipe(oreName, 1000, new PotionChemical[] {
						molecule(MoleculeEnum.purpleorgodye), molecule(MoleculeEnum.sucrose, 2)
				});
			}
			else {
				for (IOreDictionaryHandler handler : getOreDictionaryHandlers()) {
					if (handler.canHandle(oreName)) {
						handler.handle(oreName);
						return;
					}
				}
			}
		}
	}

	//TODO should be able to remove these 2
	public boolean shouldCreateSynthesis(ItemStack item) {
		if (item.getItem() == Items.STICK) {
			return false;
		}
		if (item.getItem() instanceof ItemBlock) {
			return shouldCreateSynthesis((ItemBlock) item.getItem());
		}
		return true;
	}

	public boolean shouldCreateSynthesis(ItemBlock block) {
		// check if the block is an oreBlock
		return !(block.getBlock() instanceof BlockOre);
	}

	public static ArrayList<IOreDictionaryHandler> getOreDictionaryHandlers() {
		ArrayList<IOreDictionaryHandler> oreDictionaryHandlers = Lists.newArrayList();
		oreDictionaryHandlers.add(new OreDictionaryDefaultHandler());
		if (Mods.AE2.isLoaded()) {
			oreDictionaryHandlers.add(new OreDictionaryAppliedEnergisticsHandler());
		}
		if (Mods.EXU2.isLoaded()) {
			oreDictionaryHandlers.add(new OreDictionaryExtraUtilitiesHandler());
		}
		return oreDictionaryHandlers;
	}

	public static Element element(ElementEnum var1, int var2) {
		return new Element(var1, var2);
	}

	public static Element element(ElementEnum var1) {
		return new Element(var1, 1);
	}

	public static Molecule molecule(MoleculeEnum var1, int var2) {
		return new Molecule(var1, var2);
	}

	public static Molecule molecule(MoleculeEnum var1) {
		return new Molecule(var1, 1);
	}

	public static void registerCustomRecipes(IForgeRegistry<IRecipe> registry) {
		registry.register(new RecipePotionCoating());
		registry.register(new RecipePotionSpiking());
		registry.register(new RecipeCloneChemistJournal());
	}

	//TODO
	/*

	//Redstone Arsenal
	if (Loader.isModLoaded("RedstoneArsenal")) {
		Item alloy = Item.REGISTRY.getObject(new ResourceLocation("RedstoneArsenal", "material"));
		ItemStack blend = new ItemStack(alloy, 1, 0);
		ItemStack ingot = new ItemStack(alloy, 1, 32);
		ItemStack nugget = new ItemStack(alloy, 1, 64);
		ItemStack gem = new ItemStack(alloy, 1, 96);
		RecipeDecomposer.add(new RecipeDecomposer(blend, new PotionChemical[] {
				element(ElementEnum.Au, 8),
				element(ElementEnum.Ag, 8),
				molecule(MoleculeEnum.iron3oxide),
				element(ElementEnum.Cu)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(ingot, new PotionChemical[] {
				element(ElementEnum.Au, 8),
				element(ElementEnum.Ag, 8),
				molecule(MoleculeEnum.iron3oxide),
				element(ElementEnum.Cu)
		}));
		RecipeDecomposer.add(new RecipeDecomposerSelect(nugget, 0.11F, new RecipeDecomposer(new PotionChemical[] {
				element(ElementEnum.Au, 8),
				element(ElementEnum.Ag, 8),
				molecule(MoleculeEnum.iron3oxide),
				element(ElementEnum.Cu)
		})));
		RecipeDecomposer.add(new RecipeDecomposer(gem, new PotionChemical[] {
				molecule(MoleculeEnum.fullrene, 3),
				molecule(MoleculeEnum.iron3oxide),
				element(ElementEnum.Cu)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blend, COST_INGOT * 2, new PotionChemical[] {
				element(ElementEnum.Au, 8),
				element(ElementEnum.Ag, 8),
				molecule(MoleculeEnum.iron3oxide, 2),
				element(ElementEnum.Cu, 2)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(ingot, COST_INGOT * 2, new PotionChemical[] {
				element(ElementEnum.Au, 8),
				element(ElementEnum.Ag, 8),
				molecule(MoleculeEnum.iron3oxide, 2),
				element(ElementEnum.Cu, 2)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(gem, COST_INGOT * 2, new PotionChemical[] {
				molecule(MoleculeEnum.fullrene, 3),
				molecule(MoleculeEnum.iron3oxide, 2),
				element(ElementEnum.Cu, 2)
		}));
	}

	//EnderIO
	if (Loader.isModLoaded("EnderIO")) {
		Item alloy = Item.REGISTRY.getObject(new ResourceLocation("EnderIO", "itemAlloy"));
		ItemStack electricalSteel = new ItemStack(alloy, 1, 0);
		ItemStack energeticAlloy = new ItemStack(alloy, 1, 1);
		ItemStack vibrantAlloy = new ItemStack(alloy, 1, 2);
		ItemStack redstoneAlloy = new ItemStack(alloy, 1, 3);
		ItemStack conductiveIron = new ItemStack(alloy, 1, 4);
		ItemStack pulsatingIron = new ItemStack(alloy, 1, 5);
		ItemStack darkSteel = new ItemStack(alloy, 1, 6);
		ItemStack soularium = new ItemStack(alloy, 1, 7);
		RecipeDecomposer.add(new RecipeDecomposer(electricalSteel, new PotionChemical[] {
				element(ElementEnum.Fe, 8),
				element(ElementEnum.C, 4),
				element(ElementEnum.Si, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(energeticAlloy, new PotionChemical[] {
				element(ElementEnum.Au, 8),
				element(ElementEnum.P, 1),
				molecule(MoleculeEnum.iron3oxide),
				element(ElementEnum.Cu)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(vibrantAlloy, new PotionChemical[] {
				element(ElementEnum.Au, 8),
				element(ElementEnum.P, 1),
				molecule(MoleculeEnum.iron3oxide),
				element(ElementEnum.Cu),
				element(ElementEnum.Es),
				molecule(MoleculeEnum.calciumCarbonate, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(redstoneAlloy, new PotionChemical[] {
				element(ElementEnum.Si, 12),
				molecule(MoleculeEnum.iron3oxide),
				element(ElementEnum.Cu)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(conductiveIron, new PotionChemical[] {
				element(ElementEnum.Fe, 12),
				molecule(MoleculeEnum.iron3oxide),
				element(ElementEnum.Cu)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(pulsatingIron, new PotionChemical[] {
				element(ElementEnum.Fe, 12),
				element(ElementEnum.Es),
				molecule(MoleculeEnum.calciumCarbonate, 6)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(darkSteel, new PotionChemical[] {
				molecule(MoleculeEnum.magnesiumOxide, 4),
				molecule(MoleculeEnum.siliconOxide, 8),
				element(ElementEnum.Fe, 8),
				element(ElementEnum.C, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerSelect(soularium, 0.4F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Pb, 3),
						element(ElementEnum.Be, 1),
						element(ElementEnum.Si, 2),
						element(ElementEnum.O),
						element(ElementEnum.Au, 8)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Pb, 1),
						element(ElementEnum.Si, 5),
						element(ElementEnum.O, 2),
						element(ElementEnum.Au, 8)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Si, 2),
						element(ElementEnum.O),
						element(ElementEnum.Au, 8)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Si, 6),
						element(ElementEnum.O, 2),
						element(ElementEnum.Au, 8)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Es, 1),
						element(ElementEnum.O, 2),
						element(ElementEnum.Au, 8)
				})
		}));
	}

	// AE2
	if (Loader.isModLoaded("appliedenergistics2")) {
		MoleculeEnum certusQuartzMolecule = MoleculeEnum.aluminiumPhosphate;
		MoleculeEnum chargedCertusQuartzMolecule = MoleculeEnum.aluminiumHypophosphite;
		PotionChemical chargedCertusQuartzChemical = new Molecule(chargedCertusQuartzMolecule);

		PotionChemical[] chargedCertusQuartzDecompositionFormula = new PotionChemical[] {
				new Molecule(chargedCertusQuartzMolecule, 4)
		};

		PotionChemical[] quartzGlassDecompositionFormula = new PotionChemical[] {
				new Molecule(certusQuartzMolecule, 5),
				new Molecule(MoleculeEnum.siliconDioxide, 16)
		};

		Item item = Item.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "item.ItemMultiMaterial"));
		Block skyStone = Block.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "tile.BlockSkyStone"));
		Block quartzGlass = Block.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "tile.BlockQuartzGlass"));
		ItemStack charged = new ItemStack(item, 1, 1);
		ItemStack singularity = new ItemStack(item, 1, 47);
		ItemStack skystone = new ItemStack(skyStone);
		ItemStack quartzglass = new ItemStack(quartzGlass);

		RecipeDecomposer.add(new RecipeDecomposer(charged, chargedCertusQuartzDecompositionFormula));
		RecipeHandlerSynthesis.addShapedRecipe("charged_certus_quartz", 30000, charged, " a ", "a a", " a ", 'a', chargedCertusQuartzChemical);

		RecipeDecomposer.add(new RecipeDecomposer(singularity, new PotionChemical[] {
				element(ElementEnum.Fm, 148),
				element(ElementEnum.Md, 142),
				element(ElementEnum.No, 133),
				element(ElementEnum.Lr, 124),
				element(ElementEnum.Rf, 107),
				element(ElementEnum.Db, 104),
				element(ElementEnum.Sg, 93),
				element(ElementEnum.Bh, 81),
				element(ElementEnum.Hs, 67),
				element(ElementEnum.Mt, 54),
				element(ElementEnum.Ds, 52),
				element(ElementEnum.Rg, 37),
				element(ElementEnum.Cn, 33),
				element(ElementEnum.Uut, 22)
		}));

		RecipeDecomposer.add(new RecipeDecomposerSelect(skystone, 0.9F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Si),
						element(ElementEnum.He)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Fe),
						element(ElementEnum.Xe)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Mg),
						element(ElementEnum.Ar)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Ti),
						element(ElementEnum.He)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Pb),
						element(ElementEnum.Ne)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Zn),
						element(ElementEnum.He)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Al),
						element(ElementEnum.He)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Si),
						element(ElementEnum.Xe)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Fe),
						element(ElementEnum.Ar)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Mg),
						element(ElementEnum.Kr)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Ti),
						element(ElementEnum.Ne)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Pb),
						element(ElementEnum.Rn)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Zn),
						element(ElementEnum.Ne)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						element(ElementEnum.Al),
						element(ElementEnum.Ar)
				})
		}));

		RecipeDecomposer.add(new RecipeDecomposer(quartzglass, quartzGlassDecompositionFormula));
		RecipeHandlerSynthesis.addShapedRecipe("quartz_glass", 30000, quartzglass, "aba", "bab", "aba", 'a', certusQuartzMolecule, 'b', molecule(MoleculeEnum.siliconDioxide, 4));
	}

	//RailCraft
	if (Loader.isModLoaded("Railcraft")) {
		Block metalPost = Block.REGISTRY.getObject(new ResourceLocation("Railcraft", "tile.railcraft.post.metal"));
		Block metalPlatform = Block.REGISTRY.getObject(new ResourceLocation("Railcraft", "tile.railcraft.post.metal.platform"));
		Block post = Block.REGISTRY.getObject(new ResourceLocation("Railcraft", "tile.railcraft.post"));
		RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(metalPost), element(ElementEnum.Fe, 5)));
		RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(metalPlatform), element(ElementEnum.Fe, 5)));
		RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(post), new PotionChemical[] {}));
	}
	*/

	//TODO - consider whether or not this should be a thing..maybe a config?
	/*
	private void addUnusedSynthesisRecipes() {
		Iterator<RecipeDecomposer> decomposerRecipes = RecipeDecomposer.recipes.values().iterator();

		while (decomposerRecipes.hasNext()) {
			RecipeDecomposer nextDecomposerRecipe = decomposerRecipes.next();
			if (nextDecomposerRecipe.getInput().getItemDamage() != -1) {
				boolean check = false;
				Iterator<RecipeSynthesisOld> synthesisRecipes = RecipeSynthesisOld.recipes.values().iterator();

				while (true) {
					if (synthesisRecipes.hasNext()) {
						RecipeSynthesisOld nextSynthesisRecipe = synthesisRecipes.next();
						if (!MinechemUtil.stacksAreSameKind(nextSynthesisRecipe.getOutput(), nextDecomposerRecipe.getInput())) {
							continue;
						}

						check = true;
					}

					if (!check) {
						ArrayList<PotionChemical> rawDecomposerRecipe = nextDecomposerRecipe.getOutputRaw();
						if (rawDecomposerRecipe != null) {
							if (shouldCreateSynthesis(nextDecomposerRecipe.getInput())) {
								RecipeHandlerSynthesis.addShapelessRecipe(nextDecomposerRecipe.getInput().getUnlocalizedName(), 100, nextDecomposerRecipe.getInput(), rawDecomposerRecipe);
							}
						}
					}
					break;
				}
			}
		}
	}
	*/
}