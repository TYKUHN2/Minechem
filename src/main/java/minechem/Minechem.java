package minechem;

import minechem.fluid.FluidChemicalDispenser;
import minechem.fluid.reaction.ChemicalFluidReactionHandler;
import minechem.init.ModGlobals;
import minechem.init.ModItems;
import minechem.init.ModRecipes;
import minechem.init.ModWorldGen;
import minechem.item.polytool.PolytoolEventHandler;
import minechem.item.polytool.types.PolytoolTypeIron;
import minechem.proxy.CommonProxy;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModGlobals.ID, name = ModGlobals.NAME, version = ModGlobals.VERSION, useMetadata = false, acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:forge@[14.23.2.2638,);")
public class Minechem {

	@Mod.Instance(value = ModGlobals.ID)
	public static Minechem INSTANCE;

	@Mod.Metadata(ModGlobals.ID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "minechem.proxy.ClientProxy", serverSide = "minechem.proxy.CommonProxy")
	public static CommonProxy PROXY;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		INSTANCE = this;
		PROXY.preInit(event);
		PROXY.registerTickHandlers();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		ModItems.registerToOreDictionary();
		MinecraftForge.EVENT_BUS.register(new PolytoolEventHandler());
		ModWorldGen.init();
		PROXY.registerRenderers();
		FluidChemicalDispenser.init();
		ChemicalFluidReactionHandler.initReaction();
		PROXY.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinechemUtil.populateBlacklists();
		ModRecipes.getInstance().RegisterRecipes();
		ModRecipes.getInstance().registerFluidRecipes();
		PolytoolTypeIron.getOres();
	}

	@Mod.EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent event) {
		ModRecipes.getInstance().RegisterModRecipes();
		ModRecipes.getInstance().registerOreDictOres();
		RecipeUtil.init();
		RecipeHandlerDecomposer.recursiveRecipes();
	}

}
