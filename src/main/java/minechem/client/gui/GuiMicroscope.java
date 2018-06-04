package minechem.client.gui;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import minechem.block.tile.TileMicroscope;
import minechem.client.gui.renderitem.RenderItemMicroscope;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerMicroscope;
import minechem.init.ModGlobals;
import minechem.init.ModGlobals.ModResources;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeDecomposerChance;
import minechem.recipe.RecipeDecomposerSelect;
import minechem.recipe.RecipeSynthesisShapeless;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import minechem.utils.MinechemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class GuiMicroscope extends GuiContainerTabbed {

	int guiWidth = 176;
	int guiHeight = 217;
	int eyepieceX = 25;
	int eyepieceY = 26;
	int inputSlotX = 44;
	int inputSlotY = 45;
	int slideShowTimer = 0;
	int currentSlide = 0;
	public InventoryPlayer inventoryPlayer;
	protected TileMicroscope microscope;
	GuiMicroscopeToggle recipeSwitch;
	private boolean isShapedRecipe;

	public GuiMicroscope(InventoryPlayer inventoryPlayer, TileMicroscope microscope) {
		super(new ContainerMicroscope(inventoryPlayer, microscope), new RenderItemMicroscope());
		((RenderItemMicroscope) renderItem).setGui(this);
		this.inventoryPlayer = inventoryPlayer;
		this.microscope = microscope;
		xSize = guiWidth;
		ySize = guiHeight;
		//itemRender = renderItem;
		recipeSwitch = new GuiMicroscopeToggle(this);
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.microscope")));
		//addTab(new GuiTabPatreon(this));
	}

	public int getGuiWidth() {
		return guiWidth;
	}

	public int getGuiHeight() {
		return guiHeight;
	}

	public int getEyePieceX() {
		return eyepieceX;
	}

	public int getEyePieceY() {
		return eyepieceY;
	}

	public boolean isMouseInMicroscope() {
		mouseX = getMouseX();
		mouseY = getMouseY();
		int x = (width - guiWidth) / 2;
		int y = (height - guiHeight) / 2;
		x += eyepieceX;
		y += eyepieceY;
		int h = 54;
		int w = 54;
		return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
	}

	private void drawMicroscopeOverlay() {
		zLevel = 600F;
		drawTexturedModalRect(eyepieceX, eyepieceY, 176, 0, 54, 54);
	}

	private void drawUnshapedOverlay() {
		zLevel = 0;
		drawTexturedModalRect(97, 26, 176, 70, 54, 54);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String info = MinechemUtil.getLocalString("gui.title.microscope");
		int infoWidth = fontRenderer.getStringWidth(info);
		GlStateManager.enableBlend();
		fontRenderer.drawString(info, (guiWidth - infoWidth) / 2, 5, 0x000000);
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Gui.MICROSCOPE);
		int x = (width - guiWidth) / 2;
		int y = (height - guiHeight) / 2;
		zLevel = 0;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0.0F);
		drawTexturedModalRect(0, 0, 0, 0, guiWidth, guiHeight);
		drawMicroscopeOverlay();
		if (!isShapedRecipe) {
			drawUnshapedOverlay();
		}

		GlStateManager.popMatrix();

		recipeSwitch.setPos(x + 153, y + 26);
		recipeSwitch.draw(mc.renderEngine);

		ItemStack itemstack = microscope.getStackInSlot(0);
		clearRecipeMatrix();
		if (!itemstack.isEmpty()) {
			if (recipeSwitch.getState() == 0) {
				drawSynthesisRecipe(itemstack, x, y);
			}
			else {
				isShapedRecipe = false;
				drawDecomposerRecipe(itemstack, x, y);
			}
		}
	}

	private void clearRecipeMatrix() {
		for (int slot = 2; slot < 2 + 9; slot++) {
			inventorySlots.putStackInSlot(slot, ItemStack.EMPTY);
		}
	}

	private void drawSynthesisRecipe(ItemStack inputstack, int x, int y) {
		RecipeSynthesisShapeless recipe = RecipeHandlerSynthesis.instance.getRecipeFromOutput(inputstack);
		if (recipe != null) {
			drawSynthesisRecipeMatrix(recipe, x, y);
			drawSynthesisRecipeCost(recipe, x, y);
		}
	}

	private void drawSynthesisRecipeMatrix(RecipeSynthesisShapeless recipe, int x, int y) {
		isShapedRecipe = recipe.isShaped();
		NonNullList<ItemStack> shapedRecipe = MinechemUtil.convertChemicalArrayIntoItemStackArray(isShapedRecipe ? recipe.getShapedRecipe() : recipe.getShapelessRecipe());
		int slot = 2;
		for (ItemStack itemstack : shapedRecipe) {
			inventorySlots.putStackInSlot(slot, itemstack);
			slot++;
			if (slot >= 11) {
				break;

			}
		}
	}

	private void drawSynthesisRecipeCost(RecipeSynthesisShapeless recipe, int x, int y) {
		if (!recipeSwitch.isMoverOver()) {
			String cost = String.format("%d Energy", recipe.energyCost());
			fontRenderer.drawString(cost, x + 100, y + 85, 0x000000);
		}
	}

	private void drawDecomposerRecipe(@Nonnull ItemStack inputstack, int x, int y) {
		RecipeDecomposer recipe = RecipeHandlerDecomposer.instance.getRecipe(inputstack);
		if (recipe != null) {
			NonNullList<ItemStack> output = MinechemUtil.convertChemicalsIntoItemStacks(recipe.getOutputRaw());
			if (recipe instanceof RecipeDecomposerSelect) {
				drawDecomposerRecipeSelectMatrix(((RecipeDecomposerSelect) recipe).getAllPossibleRecipes(), x, y);
			}
			else {
				drawDecomposerRecipeMatrix(output, x, y);
			}
			drawDecomposerChance(recipe, x, y);
		}
	}

	private void drawDecomposerRecipeMatrix(NonNullList<ItemStack> output, int x, int y) {
		int slot = 2;
		for (ItemStack itemstack : output) {
			inventorySlots.putStackInSlot(slot, itemstack);
			slot++;
			if (slot >= 11) {
				break;
			}
		}
	}

	private void drawDecomposerRecipeSelectMatrix(ArrayList<RecipeDecomposer> recipes, int x, int y) {
		if (slideShowTimer == ModGlobals.TICKS_PER_SECOND * 8) {
			slideShowTimer = 0;
			currentSlide++;
		}

		if (currentSlide == recipes.size()) {
			currentSlide = 0;
		}

		slideShowTimer++;
		RecipeDecomposer recipe = recipes.get(currentSlide);
		NonNullList<ItemStack> output = MinechemUtil.convertChemicalsIntoItemStacks(recipe.getOutputRaw());
		drawDecomposerRecipeMatrix(output, x, y);
	}

	private void drawDecomposerChance(RecipeDecomposer recipe, int x, int y) {
		if (!recipeSwitch.isMoverOver() && recipe instanceof RecipeDecomposerChance) {
			RecipeDecomposerChance recipeChance = (RecipeDecomposerChance) recipe;
			int chance = (int) (recipeChance.getChance() * 100);
			String info = String.format("%d%%", chance);
			fontRenderer.drawString(info, x + 108, y + 85, 0x000000);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		recipeSwitch.mouseClicked(x, y, mouseButton);
	}

	@Override
	protected void drawStack(ItemStack stack, int x, int y, String altText) {
		super.drawStack(stack, x, y, altText);
		Slot slot = inventorySlots.inventorySlots.get(0);
		if (slot.getStack() == stack || isMouseInMicroscope()) {
			//GlStateManager.scale(16.0F * 3.0F, 16.0F * 3.0F, 16.0F);
		}
		//super.drawItemStack(stack, x, y, altText);
		/*
				zLevel = 200.0F;
				itemRender.zLevel = 200.0F;
				FontRenderer font = null;
				if (stack != null) {
					font = stack.getItem().getFontRenderer(stack);
				}
				if (font == null) {
					font = mc.fontRenderer;
				}
				boolean shouldRenderHeldZoomed = false;
				Slot slot = inventorySlots.inventorySlots.get(0);
				//shouldRenderHeldZoomed = isPointInRegion(slot.xPos, slot.yPos, 16, 16, x, x);
				//if (stack == inventoryPlayer.getItemStack() && stack == returningStack && isMouseInMicroscope() && slot.getStack() == stack) {
				//renderItem.renderItemAndEffectIntoGUI(stack, x, y);
				//GlStateManager.enableDepth();
				//}
				//else {
				//itemRender.renderItemAndEffectIntoGUI(stack, x, y);
				//}
				//GlStateManager.enableDepth();
				//itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack.isEmpty() ? 0 : 8), altText);
				zLevel = 0.0F;
				itemRender.zLevel = 0.0F;
		*/
	}
}
