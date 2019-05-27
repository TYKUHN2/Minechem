package minechem;

import minechem.init.ModEvents;
import minechem.init.ModGlobals;
import minechem.proxy.CommonProxy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = ModGlobals.ID, name = ModGlobals.NAME, version = ModGlobals.VERSION, useMetadata = false, acceptedMinecraftVersions = "[1.12.2]", certificateFingerprint = "@FINGERPRINT@", dependencies = ModGlobals.DEPENDENCIES)
public class Minechem {

	@Mod.Instance(value = ModGlobals.ID)
	public static Minechem INSTANCE;

	@Mod.Metadata(ModGlobals.ID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "minechem.proxy.ClientProxy", serverSide = "minechem.proxy.CommonProxy")
	public static CommonProxy PROXY;

	static {
		FluidRegistry.enableUniversalBucket();
		ModEvents.init();
	}

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		//INSTANCE = this;
		PROXY.preInit(event);
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		PROXY.init(event);
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		PROXY.postInit(event);
	}

	@Mod.EventHandler
	public void loadComplete(final FMLLoadCompleteEvent event) {
		PROXY.loadComplete(event);
	}

}
