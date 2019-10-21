package minechem.client.gui;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import minechem.api.recipe.ISynthesisRecipe;
import minechem.block.tile.TileMicroscope;
import minechem.client.gui.renderitem.RenderItemMicroscope;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerMicroscope;
import minechem.init.ModGlobals;
import minechem.init.ModGlobals.ModResources;
import minechem.potion.PotionChemical;
import minechem.recipe.*;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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

	public GuiMicroscope(final InventoryPlayer inventoryPlayer, final TileMicroscope microscope) {
		super(new ContainerMicroscope(inventoryPlayer, microscope), new RenderItemMicroscope());
		((RenderItemMicroscope) renderItem).setGui(this);
		this.inventoryPlayer = inventoryPlayer;
		this.microscope = microscope;
		xSize = guiWidth;
		ySize = guiHeight;
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
		final int h = 54;
		final int w = 54;
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
	protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
		final String info = MinechemUtil.getLocalString("gui.title.microscope");
		final int infoWidth = fontRenderer.getStringWidth(info);
		GlStateManager.enableBlend();
		fontRenderer.drawString(info, (guiWidth - infoWidth) / 2, 5, 0x000000);
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Gui.MICROSCOPE);
		final int x = (width - guiWidth) / 2;
		final int y = (height - guiHeight) / 2;
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

		final ItemStack itemstack = microscope.getStackInSlot(0);
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

	@Override
	protected void drawSlotCustom(final Slot slotIn) {
		final int x = slotIn.xPos;
		final int y = slotIn.yPos;
		final ItemStack stack = slotIn.getStack();
		if (slotIn.slotNumber == 0) {
			if (!stack.isEmpty()) {
				zLevel = 200.0F;
				renderItem.zLevel = 200.0F;
				GlStateManager.enableDepth();
				RenderHelper.enableGUIStandardItemLighting();
				renderItem.renderItemAndEffectIntoGUI(stack, x, y);
				//GlStateManager.enableDepth();
				zLevel = 0.0F;
				renderItem.zLevel = 0.0F;
				return;
			}
		}
		super.drawSlotCustom(slotIn);
	}

	private void clearRecipeMatrix() {
		for (int slot = 2; slot < 2 + 9; slot++) {
			inventorySlots.putStackInSlot(slot, ItemStack.EMPTY);
		}
	}

	private void drawSynthesisRecipe(final ItemStack inputstack, final int x, final int y) {
		final ISynthesisRecipe recipe = RecipeHandlerSynthesis.getRecipeFromOutput(inputstack);
		if (recipe != null) {
			drawSynthesisRecipeMatrix(recipe, x, y);
			drawSynthesisRecipeCost(recipe, x, y);
			drawRecipeType(recipe, x, y);
			isShapedRecipe = recipe.isShaped();
		}

	}

	private void drawSynthesisRecipeMatrix(final ISynthesisRecipe recipe, final int x, final int y) {
		final NonNullList<ItemStack> shapedRecipe = RecipeUtil.getRecipeAsStackList(recipe);
		int slot = 2;
		for (final ItemStack itemstack : shapedRecipe) {
			inventorySlots.putStackInSlot(slot, itemstack);
			slot++;
			if (slot >= 11) {
				break;
			}
		}
	}

	private void drawRecipeType(final ISynthesisRecipe recipe, final int x, final int y) {
		if (!recipeSwitch.isMoverOver()) {
			final String type = RecipeHandlerSynthesis.isShaped(recipe) ? "Shaped" : "Shapeless";
			final String cost = String.format("%s", type);
			fontRenderer.drawString(cost, x + 100, y + 95, 0x000000);
		}
	}

	private void drawSynthesisRecipeCost(final ISynthesisRecipe recipe, final int x, final int y) {
		if (!recipeSwitch.isMoverOver()) {
			final String cost = String.format("%d Energy", RecipeHandlerSynthesis.getEnergyCost(recipe));
			fontRenderer.drawString(cost, x + 100, y + 85, 0x000000);
		}
	}

	private void drawDecomposerRecipe(@Nonnull final ItemStack inputstack, final int x, final int y) {
		final RecipeDecomposer recipe = RecipeHandlerDecomposer.instance.getRecipe(inputstack);
		if (recipe != null) {
			final NonNullList<ItemStack> output = MinechemUtil.convertChemicalsIntoItemStacks(recipe.getOutputRaw());
			if (recipe instanceof RecipeDecomposerSelect) {
				drawDecomposerRecipeSelectMatrix(((RecipeDecomposerSelect) recipe).getAllPossibleOutputs(), x, y);
			}
			else {
				drawDecomposerRecipeMatrix(output, x, y);
			}
			drawDecomposerChance(recipe, x, y);
		}
	}

	private void drawDecomposerRecipeMatrix(final NonNullList<ItemStack> output, final int x, final int y) {
		int slot = 2;
		for (final ItemStack itemstack : output) {
			inventorySlots.putStackInSlot(slot, itemstack);
			slot++;
			if (slot >= 11) {
				break;
			}
		}
	}

	private void drawDecomposerRecipeSelectMatrix(final ArrayList<ArrayList<PotionChemical>> outputs, final int x, final int y) {
		if (slideShowTimer == ModGlobals.TICKS_PER_SECOND * 8) {
			slideShowTimer = 0;
			currentSlide++;
		}

		if (currentSlide == outputs.size()) {
			currentSlide = 0;
		}

		slideShowTimer++;
		final ArrayList<PotionChemical> output = outputs.get(currentSlide);
		final NonNullList<ItemStack> outputStack = MinechemUtil.convertChemicalsIntoItemStacks(output);
		drawDecomposerRecipeMatrix(outputStack, x, y);
	}

	private void drawDecomposerChance(final RecipeDecomposer recipe, final int x, final int y) {
		if (!recipeSwitch.isMoverOver() && recipe instanceof RecipeDecomposerChance) {
			final RecipeDecomposerChance recipeChance = (RecipeDecomposerChance) recipe;
			final int chance = (int) (recipeChance.getChance() * 100);
			final String info = String.format("%d%%", chance);
			fontRenderer.drawString(info, x + 108, y + 85, 0x000000);
		}
	}

	@Override
	protected void mouseClicked(final int x, final int y, final int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		recipeSwitch.mouseClicked(x, y, mouseButton);
	}

	@Override
	protected void drawStack(final ItemStack stack, final int x, final int y, final String altText) {
		//Slot slot = inventorySlots.inventorySlots.get(0);
		zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		FontRenderer font = null;
		if (!stack.isEmpty()) {
			font = stack.getItem().getFontRenderer(stack);
		}
		if (font == null) {
			font = mc.fontRenderer;
		}
		boolean shouldRenderHeldZoomed = false;
		shouldRenderHeldZoomed = isMouseInMicroscope();//sPointInRegion(slot.xPos - 20, slot.yPos - 20, 52, 52, mouseX, mouseY);
		if (shouldRenderHeldZoomed) {
			renderItem.renderItemAndEffectIntoGUI(stack, x, y);
			GlStateManager.enableDepth();
		}
		else {
			itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		}
		itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack.isEmpty() ? 0 : 8), altText);
		zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}

}
