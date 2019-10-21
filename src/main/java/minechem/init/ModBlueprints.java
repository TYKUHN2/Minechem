package minechem.init;

import java.util.List;

import com.google.common.collect.Lists;

import minechem.api.IMinechemBlueprint;
import minechem.item.blueprint.BlueprintFission;
import minechem.item.blueprint.BlueprintFusion;
import net.minecraft.util.ResourceLocation;

/**
 * @author p455w0rd
 *
 */
public class ModBlueprints {

	private static List<IMinechemBlueprint> BLUEPRINT_LIST = Lists.newArrayList();

	public static BlueprintFission fission;
	public static BlueprintFusion fusion;

	public static void init() {
		BLUEPRINT_LIST.add(fission = (BlueprintFission) new BlueprintFission().setRegistryName(new ResourceLocation(ModGlobals.MODID, "fission")));
		BLUEPRINT_LIST.add(fusion = (BlueprintFusion) new BlueprintFusion().setRegistryName(new ResourceLocation(ModGlobals.MODID, "fusion")));
	}

	public static List<IMinechemBlueprint> getList() {
		return BLUEPRINT_LIST;
	}

	public static void registerBlueprints() {
		for (IMinechemBlueprint bp : getList()) {
			ModRegistries.MINECHEM_BLUEPRINTS.register(bp);
		}
	}

}
