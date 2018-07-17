package minechem.proxy;

import minechem.Minechem;
import minechem.fluid.reaction.ChemicalFluidReactionHandler;
import minechem.init.ModBlocks;
import minechem.init.ModBlueprints;
import minechem.init.ModConfig;
import minechem.init.ModFluids;
import minechem.init.ModGlobals.MetaData;
import minechem.init.ModGuiHandler;
import minechem.init.ModItems;
import minechem.init.ModNetworking;
import minechem.init.ModWorldGen;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.item.polytool.PolytoolEventHandler;
import minechem.item.polytool.types.PolytoolTypeIron;
import minechem.potion.PharmacologyEffectRegistry;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ModConfig.init(event.getSuggestedConfigurationFile());
		ModNetworking.init();
		MetaData.preInit();
		ModItems.init();
		ModBlocks.registerBlocks();
		ElementEnum.init();
		MoleculeEnum.init();
		ModFluids.init();
		PharmacologyEffectRegistry.init();
		ModBlueprints.init();
	}

	public void init(FMLInitializationEvent event) {
		ModItems.registerToOreDictionary();
		MinecraftForge.EVENT_BUS.register(new PolytoolEventHandler());
		ModWorldGen.init();
		registerRenderers();
		ChemicalFluidReactionHandler.initReaction();
	}

	public void postInit(FMLPostInitializationEvent event) {
		MinechemUtil.populateBlacklists();
		NetworkRegistry.INSTANCE.registerGuiHandler(Minechem.INSTANCE, new ModGuiHandler());
		PolytoolTypeIron.getOres();
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		RecipeUtil.init();
		RecipeHandlerDecomposer.recursiveRecipes();
	}

	public void registerRenderers() {

	}

}
