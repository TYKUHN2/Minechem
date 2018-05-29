package minechem.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * @author p455w0rd
 *
 */
public class ModSounds {

	public static final SoundEvent BLUEPRINT_PROJECTOR = setupSound("projector");

	private static SoundEvent setupSound(String soundName) {
		ResourceLocation registryName = new ResourceLocation(ModGlobals.ID, soundName);
		SoundEvent se = new SoundEvent(registryName).setRegistryName(registryName);
		return se;
	}

}
