package minechem;

import minechem.init.ModGlobals;
import minechem.proxy.CommonProxy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, acceptedMinecraftVersions = "[1.12.2]", certificateFingerprint = "@FINGERPRINT@", dependencies = ModGlobals.DEPENDENCIES)
public class Minechem {

	@Mod.Instance(value = ModGlobals.MODID)
	public static Minechem INSTANCE;

	@Mod.Metadata(ModGlobals.MODID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "minechem.proxy.ClientProxy", serverSide = "minechem.proxy.CommonProxy")
	public static CommonProxy PROXY;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
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
