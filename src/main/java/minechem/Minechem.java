package minechem;

import minechem.fluid.FluidChemicalDispenser;
import minechem.fluid.reaction.ChemicalFluidReactionHandler;
import minechem.init.ModBlocks;
import minechem.init.ModConfig;
import minechem.init.ModFluids;
import minechem.init.ModGlobals;
import minechem.init.ModGlobals.MetaData;
import minechem.init.ModItems;
import minechem.init.ModLogger;
import minechem.init.ModNetworking;
import minechem.init.ModRecipes;
import minechem.init.ModWorldGen;
import minechem.item.blueprint.MinechemBlueprint;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.item.polytool.PolytoolEventHandler;
import minechem.item.polytool.types.PolytoolTypeIron;
import minechem.potion.PharmacologyEffectRegistry;
import minechem.potion.PotionCoatingRecipe;
import minechem.potion.PotionCoatingSubscribe;
import minechem.potion.PotionEnchantmentCoated;
import minechem.proxy.CommonProxy;
import minechem.recipe.RecipePotionSpiking;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ModGlobals.ID, name = ModGlobals.NAME, version = ModGlobals.VERSION, useMetadata = false, acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:forge@[14.23.2.2638,);")
public class Minechem {
	public static boolean isCoFHAAPILoaded;

	// Instancing
	@Mod.Instance(value = ModGlobals.ID)
	public static Minechem INSTANCE;

	// Public extra data about our mod that Forge uses in the mods listing page for more information.
	@Mod.Metadata(ModGlobals.ID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "minechem.proxy.ClientProxy", serverSide = "minechem.proxy.CommonProxy")
	public static CommonProxy PROXY;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// Register instance.
		INSTANCE = this;

		try {
			Class.forName("cofh.api.energy.IEnergyHandler");
			isCoFHAAPILoaded = true;
		}
		catch (Exception e) {
			isCoFHAAPILoaded = false;
		}

		// Load configuration.
		ModLogger.debug("Loading configuration...");
		ModConfig.init(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ModConfig());

		ModLogger.debug("Registering Packets...");
		ModNetworking.init();

		ModLogger.debug("Setting up ModMetaData");
		MetaData.init(metadata);

		// Register items and blocks.
		ModLogger.debug("Registering Items...");
		ModItems.init();

		ModLogger.debug("Registering Blocks...");
		ModBlocks.registerBlocks();

		ModLogger.debug("Registering Elements & Molecules...");
		ElementEnum.init();
		MoleculeEnum.init();

		ModLogger.debug("Registering Fluids...");
		ModFluids.init();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			ModFluids.initModels();
		}

		ModLogger.debug("Registering Blueprints...");
		MinechemBlueprint.registerBlueprints();

		//GameRegistry.registerFuelHandler(new MinechemFuelHandler());
		PROXY.registerTickHandlers();
		//PROXY.preInit(event);
		//ResourceUtils.registerReloadListener(manager -> RenderUtil.getModelCache().clear());
		//FMLInterModComms.sendMessage("OpenBlocks", "donateUrl", "http://jakimfett.com/patreon/");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		ModLogger.debug("Registering OreDict Compatability...");
		ModItems.registerToOreDictionary();

		ModLogger.debug("Registering Chemical Effects...");
		MinecraftForge.EVENT_BUS.register(new PotionCoatingSubscribe());

		ModLogger.debug("Registering Polytool Event Handler...");
		MinecraftForge.EVENT_BUS.register(new PolytoolEventHandler());

		ModLogger.debug("Matching Pharmacology Effects to Chemicals...");
		//CraftingManager.REGISTRY.register(0,new ResourceLocation(ModGlobals.ID, "potion_coating"), new PotionCoatingRecipe());
		ForgeRegistries.RECIPES.register(new PotionCoatingRecipe().setRegistryName(new ResourceLocation(ModGlobals.ID, "potion_coating")));

		ModLogger.debug("Registering FoodSpiking Recipes...");
		//CraftingManager.getInstance().getRecipeList().add(new PotionSpikingRecipe());
		ForgeRegistries.RECIPES.register(new RecipePotionSpiking().setRegistryName(new ResourceLocation(ModGlobals.ID, "potion_spiking")));

		ModLogger.debug("Registering Ore Generation...");
		GameRegistry.registerWorldGenerator(new ModWorldGen(), 0);

		ModLogger.debug("Registering Fluid Containers...");
		//ModItems.registerFluidContainers();

		ModLogger.debug("Register Tick Events for chemical effects tracking...");
		PROXY.registerTickHandlers();

		ModLogger.debug("Registering ClientProxy Rendering Hooks...");
		PROXY.registerRenderers();

		ModLogger.debug("Registering Fluid Reactions...");
		FluidChemicalDispenser.init();
		ChemicalFluidReactionHandler.initReaction();

		//TODO
		/*
		if (Loader.isModLoaded("MineTweaker3"))
		{
		    LogHelper.debug("Loading MineTweaker Classes...");
		    MineTweakerAPI.registerClass(Chemicals.class);
		    MineTweakerAPI.registerClass(Decomposer.class);
		    MineTweakerAPI.registerClass(Synthesiser.class);
		    MineTweakerAPI.registerClass(Fuels.class);
		}
		*/
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinechemUtil.populateBlacklists();

		ModLogger.debug("Registering Recipes...");
		ModRecipes.getInstance().RegisterRecipes();
		ModRecipes.getInstance().registerFluidRecipes();
		//MinechemBucketHandler.getInstance().registerBucketRecipes();

		ModLogger.debug("Adding effects to molecules...");
		PharmacologyEffectRegistry.init();

		ModLogger.debug("Activating Chemical Effect Layering (Coatings)...");
		PotionEnchantmentCoated.registerCoatings();

		ModLogger.debug("Registering Mod Ores for PolyTool...");
		PolytoolTypeIron.getOres();

		ModLogger.debug("Overriding bucket dispenser...");
		//MinechemBucketReceiver.init();

		ModLogger.info("Minechem has loaded");
	}

	@Mod.EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent event) {
		ModLogger.debug("Registering Mod Recipes...");
		ModRecipes.getInstance().RegisterModRecipes();

		Long start = System.currentTimeMillis();
		ModLogger.info("Registering other Mod Recipes...");
		ModRecipes.getInstance().registerOreDictOres();
		RecipeUtil.init();
		RecipeHandlerDecomposer.recursiveRecipes();
		ModLogger.info((System.currentTimeMillis() - start) + "ms spent registering Recipes");
	}
}
