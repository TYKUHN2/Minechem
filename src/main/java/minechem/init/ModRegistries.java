package minechem.init;

import minechem.api.recipe.ISynthesisRecipe;
import minechem.recipe.handler.RecipeHandlerSynthesis.SynthesisRecipeCallbacks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * @author p455w0rd
 *
 */
public class ModRegistries {

	static {
		init();
	}

	public static final IForgeRegistry<ISynthesisRecipe> SYNTHESIS_RECIPES = GameRegistry.findRegistry(ISynthesisRecipe.class);

	public static void init() {
		new RegistryBuilder<ISynthesisRecipe>().setName(new ResourceLocation(ModGlobals.ID, "synthesis_recipes")).setType(ISynthesisRecipe.class).setMaxID(Integer.MAX_VALUE >> 5).disableSaving().allowModification().addCallback(SynthesisRecipeCallbacks.INSTANCE).create();
	}

}
