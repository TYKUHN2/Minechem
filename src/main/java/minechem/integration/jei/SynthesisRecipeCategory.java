package minechem.integration.jei;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.*;
import mezz.jei.config.Constants;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.Translator;
import minechem.init.ModBlocks;
import minechem.init.ModGlobals;
import minechem.integration.JEI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

/**
 * @author p455w0rd
 *
 */
public class SynthesisRecipeCategory implements IRecipeCategory<IRecipeWrapper> {

	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot1 = 1;

	public static final int width = 116;
	public static final int height = 54;

	private final IDrawable background;
	private final IDrawable icon;
	private final String localizedName;
	private final ICraftingGridHelper craftingGridHelper;

	public SynthesisRecipeCategory(final IGuiHelper guiHelper) {
		//Texture
		final ResourceLocation location = Constants.RECIPE_GUI_VANILLA;
		background = guiHelper.createDrawable(location, 0, 60, width, height);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.synthesis));
		localizedName = Translator.translateToLocal("gui.title.synthesis");
		craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
	}

	@Override
	public String getUid() {
		return JEI.CAT_SYNTH;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public String getModName() {
		return ModGlobals.NAME;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setRecipe(final IRecipeLayout recipeLayout, final IRecipeWrapper recipeWrapper, final IIngredients ingredients) {
		final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(craftOutputSlot, false, 94, 18);

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				final int index = craftInputSlot1 + x + y * 3;
				guiItemStacks.init(index, true, x * 18, y * 18);
			}
		}

		if (recipeWrapper instanceof ICustomCraftingRecipeWrapper) {
			final ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper) recipeWrapper;
			customWrapper.setRecipe(recipeLayout, ingredients);
			return;
		}

		final List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		final List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		if (recipeWrapper instanceof IShapedCraftingRecipeWrapper) {
			final IShapedCraftingRecipeWrapper wrapper = (IShapedCraftingRecipeWrapper) recipeWrapper;
			craftingGridHelper.setInputs(guiItemStacks, inputs, wrapper.getWidth(), wrapper.getHeight());
		}
		else {
			craftingGridHelper.setInputs(guiItemStacks, inputs);
			recipeLayout.setShapeless();
		}
		guiItemStacks.set(craftOutputSlot, outputs.get(0));

		if (recipeWrapper instanceof ICraftingRecipeWrapper) {
			final ICraftingRecipeWrapper craftingRecipeWrapper = (ICraftingRecipeWrapper) recipeWrapper;
			final ResourceLocation registryName = craftingRecipeWrapper.getRegistryName();
			if (registryName != null) {
				guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
					if (slotIndex == craftOutputSlot) {
						final String recipeModId = registryName.getResourceDomain();

						boolean modIdDifferent = false;
						final ResourceLocation itemRegistryName = ingredient.getItem().getRegistryName();
						if (itemRegistryName != null) {
							final String itemModId = itemRegistryName.getResourceDomain();
							modIdDifferent = !recipeModId.equals(itemModId);
						}

						if (modIdDifferent) {
							final String modName = ForgeModIdHelper.getInstance().getFormattedModNameForModId(recipeModId);
							if (modName != null) {
								tooltip.add(TextFormatting.GRAY + Translator.translateToLocalFormatted("jei.tooltip.recipe.by", modName));
							}
						}

						final boolean showAdvanced = Minecraft.getMinecraft().gameSettings.advancedItemTooltips || GuiScreen.isShiftKeyDown();
						if (showAdvanced) {
							tooltip.add(TextFormatting.DARK_GRAY + Translator.translateToLocalFormatted("jei.tooltip.recipe.id", registryName.toString()));
						}
					}
				});
			}
		}
	}

}