package minechem.proxy;

import minechem.init.ModBlocks;
import minechem.init.ModConfig;
import minechem.init.ModEvents;
import minechem.init.ModFluids;
import minechem.init.ModGlobals.MetaData;
import minechem.init.ModItems;
import minechem.init.ModNetworking;
import minechem.item.blueprint.MinechemBlueprint;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PharmacologyEffectRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
		MinechemBlueprint.registerBlueprints();
	}

	public void init(FMLInitializationEvent event) {

	}

	public void registerRenderers() {

	}

	public void registerTickHandlers() {
		ModEvents.init();
	}

}
