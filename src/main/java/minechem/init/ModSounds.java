package minechem.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author p455w0rd
 *
 */
public class ModSounds {

	public static final SoundEvent BLUEPRINT_PROJECTOR = setupSound("projector");

	private static SoundEvent setupSound(String soundName) {
		ResourceLocation registryName = new ResourceLocation(ModGlobals.MODID, soundName);
		return new SoundEvent(registryName).setRegistryName(registryName);
	}

	public static void registerSounds(IForgeRegistry<SoundEvent> registry) {
		registry.register(BLUEPRINT_PROJECTOR);
	}

}
