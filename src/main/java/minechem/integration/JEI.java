package minechem.integration;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import minechem.api.recipe.ISynthesisRecipe;
import minechem.init.ModBlocks;
import minechem.init.ModGlobals;
import minechem.init.ModNetworking;
import minechem.init.ModRegistries;
import minechem.integration.jei.PacketSynthesisRecipeTransfer;
import minechem.integration.jei.ShapedSynthesisWrapper;
import minechem.integration.jei.ShapelessSynthesisWrapper;
import minechem.integration.jei.SynthesisRecipeCategory;
import minechem.integration.jei.SynthesisTransferHandler;
import minechem.integration.jei.TabMover;
import minechem.recipe.RecipeSynthesisShaped;
import minechem.recipe.RecipeSynthesisShapeless;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author p455w0rd
 *
 */
@JEIPlugin
public class JEI implements IModPlugin {

	public static final String CAT_SYNTH = ModGlobals.ID + ".synthesis";

	public static void registerPackets() {
		ModNetworking.INSTANCE.registerMessage(PacketSynthesisRecipeTransfer.class, PacketSynthesisRecipeTransfer.class, ModNetworking.nextID(), Side.SERVER);
	}

	@Override
	public void register(IModRegistry registry) {
		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();

		blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.ghostBlock));
		registry.addAdvancedGuiHandlers(new TabMover());
		registry.addRecipes(ModRegistries.SYNTHESIS_RECIPES.getValuesCollection(), CAT_SYNTH);
		registry.handleRecipes(RecipeSynthesisShapeless.class, recipe -> new ShapelessSynthesisWrapper<ISynthesisRecipe>(registry.getJeiHelpers(), recipe), CAT_SYNTH);
		registry.handleRecipes(RecipeSynthesisShaped.class, recipe -> new ShapedSynthesisWrapper(registry.getJeiHelpers(), recipe), CAT_SYNTH);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.synthesis), CAT_SYNTH);
		recipeTransferRegistry.addRecipeTransferHandler(new SynthesisTransferHandler(jeiHelpers.recipeTransferHandlerHelper()), CAT_SYNTH);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registry.addRecipeCategories(new SynthesisRecipeCategory(guiHelper));
	}

}
