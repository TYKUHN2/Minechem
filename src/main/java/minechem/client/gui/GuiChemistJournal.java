package minechem.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import minechem.api.IVerticalScrollContainer;
import minechem.container.ChemistJournalContainer;
import minechem.init.ModGlobals;
import minechem.init.ModGlobals.ModResources;
import minechem.init.ModItems;
import minechem.init.ModNetworking;
import minechem.network.message.ChemistJournalActiveItemMessage;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeDecomposerChance;
import minechem.recipe.RecipeDecomposerSelect;
import minechem.recipe.RecipeSynthesisShaped;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import minechem.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

public class GuiChemistJournal extends GuiContainerTabbed implements IVerticalScrollContainer {

	private static final int SYNTHESIS_X = 197;
	private static final int SYNTHESIS_Y = 121;
	private static final int DECOMPOSER_X = 197;
	private static final int DECOMPOSER_Y = 41;

	private static final int GUI_WIDTH = 302;
	private static final int GUI_HEIGHT = 191;

	GuiVerticalScrollBar scrollBar;
	GuiTextField searchBar;
	List<GuiFakeSlot> itemListSlots = new ArrayList<GuiFakeSlot>();
	int listHeight;
	GuiFakeSlot[] synthesisSlots = new GuiFakeSlot[9];
	GuiFakeSlot[] decomposerSlots = new GuiFakeSlot[9];
	EntityPlayer player;
	private int slideShowTimer = 0;
	private int currentSlide = 0;
	public ItemStack currentItemStack;
	IRecipe currentSynthesisRecipe;
	RecipeDecomposer currentDecomposerRecipe;
	ItemStack journalStack;
	List<ItemStack> itemList;

	public GuiChemistJournal(EntityPlayer entityPlayer) {
		super(new ChemistJournalContainer(entityPlayer.inventory));
		player = entityPlayer;
		journalStack = entityPlayer.inventory.getCurrentItem();
		currentItemStack = ModItems.journal.getActiveStack(journalStack);
		if (!currentItemStack.isEmpty()) {
			showRecipesForStack(currentItemStack);
		}
		xSize = GUI_WIDTH;
		ySize = GUI_HEIGHT;
		scrollBar = new GuiVerticalScrollBar(this, 128, 14, 157, xSize, ySize);
		searchBar = new GuiTextField(100, 12, 20, 14);

		itemList = ModItems.journal.getItemList(journalStack);
		populateItemList();
		//addTab(new TabTable(this));
	}

	public void populateItemList() {
		int i = 0;
		int j = 0;
		itemListSlots.clear();
		if (itemList != null) {
			for (ItemStack itemstack : itemList) {
				if (!searchBar.getText().equals("") && !itemstack.isEmpty() && !(itemstack.getDisplayName().toLowerCase().contains(searchBar.getText()))) {
					continue;
				}
				int xPos = (i * 18) + 18;
				int yPos = (j * 18) + 28;
				GuiFakeSlot slot = new GuiFakeSlot(this, player);
				slot.setXPos(xPos);
				slot.setYPos(yPos);
				slot.setItemStack(itemstack);
				itemListSlots.add(slot);
				if (++i == 6) {
					i = 0;
					j++;
				}
			}
		}
		listHeight = j * 18;
		if (itemListSlots.size() == 1) {
			showRecipesForStack(itemListSlots.get(0).getItemStack());
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		GuiFakeSlot clickedSlot = null;
		for (GuiFakeSlot slot : itemListSlots) {
			if (slot.getMouseIsOver()) {
				clickedSlot = slot;
				break;
			}
		}
		if (clickedSlot != null) {
			onSlotClick(clickedSlot);
		}
		//System.out.println("mouseX: " + mouseX + " mouseY: " + mouseY + " Button: " + mouseButton);
		if (mouseButton == 1 && mouseX >= 20 && mouseY >= 14 && mouseX <= 119 && mouseY <= 25) {
			searchBar.setText("");
			searchBar.cursorPos = 0;
			populateItemList();
		}
	}

	@Override
	protected void keyTyped(char character, int keyCode) {
		// only leave GUI when ESC is pressed
		if (keyCode == 1) {
			mc.player.closeScreen();
		}
		searchBar.keyTyped(character, keyCode);
		populateItemList();
	}

	public void onSlotClick(GuiFakeSlot slot) {
		ItemStack itemstack = slot.getItemStack();
		showRecipesForStack(itemstack);
	}

	public void showRecipesForStack(ItemStack itemstack) {
		currentItemStack = itemstack;
		ModItems.journal.setActiveStack(itemstack, journalStack);
		ChemistJournalActiveItemMessage message = new ChemistJournalActiveItemMessage(itemstack, player);
		ModNetworking.INSTANCE.sendToServer(message);

		IRecipe synthesisRecipe = RecipeHandlerSynthesis.getRecipeFromOutput(itemstack);
		RecipeDecomposer decomposerRecipe = RecipeHandlerDecomposer.instance.getRecipe(itemstack);
		synthesisSlots = new GuiFakeSlot[9];
		decomposerSlots = new GuiFakeSlot[9];
		currentSynthesisRecipe = null;
		currentDecomposerRecipe = null;
		if (synthesisRecipe != null) {
			showSynthesisRecipe(synthesisRecipe);
			currentSynthesisRecipe = synthesisRecipe;
		}
		if (decomposerRecipe != null) {
			showDecomposerRecipe(decomposerRecipe);
			currentDecomposerRecipe = decomposerRecipe;
		}
	}

	public void showSynthesisRecipe(IRecipe recipe) {
		NonNullList<ItemStack> ingredients = RecipeUtil.getRecipeAsStackList(recipe);
		showIngredients(ingredients, synthesisSlots, SYNTHESIS_X, SYNTHESIS_Y);
	}

	public void showDecomposerRecipe(RecipeDecomposer recipe) {
		if (recipe instanceof RecipeDecomposerSelect) {
			slideShowTimer = 0;
			currentSlide = 0;
			return;
		}

		NonNullList<ItemStack> ingredients = MinechemUtil.convertChemicalsIntoItemStacks(recipe.getOutputRaw());
		//ItemStack[] ingredientArray = ingredients.toArray(new ItemStack[9]);
		showIngredients(ingredients, decomposerSlots, DECOMPOSER_X, DECOMPOSER_Y);
	}

	private void showDecomposerRecipeSelect(RecipeDecomposerSelect recipe) {
		List<RecipeDecomposer> recipes = recipe.getAllPossibleRecipes();
		if (slideShowTimer >= ModGlobals.TICKS_PER_SECOND * 8) {
			slideShowTimer = 0;
			currentSlide++;
			if (currentSlide >= recipes.size()) {
				currentSlide = 0;
			}
		}
		if (slideShowTimer == 0) {
			ArrayList<PotionChemical> potionChemicals = recipes.get(currentSlide).getOutputRaw();
			NonNullList<ItemStack> ingredients = MinechemUtil.convertChemicalsIntoItemStacks(potionChemicals);
			//ItemStack[] ingredientArray = ingredients.toArray(new ItemStack[9]);
			showIngredients(ingredients, decomposerSlots, DECOMPOSER_X, DECOMPOSER_Y);
		}
		slideShowTimer++;
	}

	private void showIngredients(NonNullList<ItemStack> ingredients, GuiFakeSlot[] slotArray, int xOffset, int yOffset) {
		int pos = 0;
		int i = 0;
		int j = 0;
		int x, y;
		for (ItemStack ingredient : ingredients) {
			if (pos >= 9) {
				break;
			}
			slotArray[pos] = null;
			if (!ingredient.isEmpty()) {
				x = (i * 18) + xOffset;
				y = (j * 18) + yOffset;
				GuiFakeSlot fakeSlot = new GuiFakeSlot(this, player);
				fakeSlot.setItemStack(ingredient);
				fakeSlot.setXPos(x);
				fakeSlot.setYPos(y);
				slotArray[pos] = fakeSlot;
			}
			pos++;
			if (++i == 3) {
				i = 0;
				j++;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.pushMatrix();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.JOURNAL);
		drawTexturedModalRect(0, 0, 0, 0, xSize / 2, ySize / 2);
		GlStateManager.popMatrix();

		if (!currentItemStack.isEmpty()) {
			drawRecipeGrid();
			drawRecipeGrid();
			drawText();
			drawRecipeSlots(x, y);
		}
		else {
			drawHelp();
		}

		scrollBar.draw();
		searchBar.draw();
		drawSlots(x, y);
		drawSlotTooltips();

		// Draw page corner overlay.
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.JOURNAL);
		drawTexturedModalRect(8 / 2, 164 / 2, 161 / 2, 192 / 2, 20 / 2, 20 / 2);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();

		if (currentDecomposerRecipe instanceof RecipeDecomposerSelect) {
			showDecomposerRecipeSelect((RecipeDecomposerSelect) currentDecomposerRecipe);
		}
	}

	private void drawRecipeGrid() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.pushMatrix();

		GlStateManager.scale(2.0F, 2.0F, 1.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.JOURNAL);

		drawTexturedModalRect(197 / 2, 41 / 2, 51 / 2, 192 / 2, 54 / 2, 54 / 2);
		if (currentSynthesisRecipe != null && currentSynthesisRecipe instanceof RecipeSynthesisShaped) {
			drawTexturedModalRect(197 / 2, 121 / 2, 104 / 2, 192 / 2, 54 / 2, 54 / 2);
		}
		else {
			drawTexturedModalRect(197 / 2, 121 / 2, 51 / 2, 192 / 2, 54 / 2, 54 / 2);
		}
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
	}

	private void drawText() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		String itemname = String.format("%sl%s", ModGlobals.TEXT_MODIFIER, currentItemStack.getDisplayName());
		if (itemname.length() > 18) {
			itemname = itemname.substring(0, 18).trim() + "...";
		}
		fontRenderer.drawString(itemname, 175, 10, 0x0000FF);
		fontRenderer.drawString(MinechemUtil.getLocalString("gui.journal.decomposer"), 175, 20, 0x884400);

		float chance = 100;
		if (currentDecomposerRecipe != null && currentDecomposerRecipe instanceof RecipeDecomposerChance) {
			chance = currentDecomposerRecipe.getChance();
			chance *= 100.0F;
		}
		if (currentDecomposerRecipe != null) {
			fontRenderer.drawString(String.format("%.1f%% " + MinechemUtil.getLocalString("gui.journal.chance"), chance), 175, 30, 0x555555);
		}

		fontRenderer.drawString(MinechemUtil.getLocalString("gui.journal.synthesis"), 175, 100, 0x884400);
		if (currentSynthesisRecipe != null) {
			int energyCost = RecipeHandlerSynthesis.getEnergyCost(currentSynthesisRecipe);
			fontRenderer.drawString(String.format("%d " + MinechemUtil.getLocalString("tab.tooltip.energy"), energyCost), 175, 110, 0x555555);
		}
	}

	private void drawHelp() {
		fontRenderer.drawString(MinechemUtil.getLocalString("item.chemist_journal.name"), 180, 18, 0xFF000000);
		String help = MinechemUtil.getLocalString("help.journal");
		GlStateManager.pushMatrix();
		float scale = 0.5F;
		GlStateManager.scale(scale, scale, 1);
		fontRenderer.drawSplitString(help, 345, 70, 200, 0xAA000000);
		GlStateManager.popMatrix();
	}

	private void drawSlots(int x, int y) {
		GlStateManager.pushMatrix();
		RenderUtil.startScissor(mc, x + 9, y + 26, 140, 156);
		int ypos = (int) -((listHeight - 130) * scrollBar.getScrollValue());
		GlStateManager.translate(0, ypos, 0);
		for (GuiFakeSlot slot : itemListSlots) {
			slot.setYOffset(ypos);
			slot.draw();
		}
		RenderUtil.endScissor();
		GlStateManager.popMatrix();
	}

	private void drawSlotTooltips() {
		for (GuiFakeSlot slot : itemListSlots) {
			slot.drawTooltip(mouseX + 10, mouseY);
		}
	}

	private void drawRecipeSlots(int x, int y) {
		for (GuiFakeSlot slot : synthesisSlots) {
			if (slot != null) {
				slot.draw();
			}
		}
		for (GuiFakeSlot slot : decomposerSlots) {
			if (slot != null) {
				slot.draw();
			}
		}
		for (GuiFakeSlot slot : synthesisSlots) {
			if (slot != null) {
				slot.drawTooltip(mouseX + 10, mouseY);
			}
		}
		for (GuiFakeSlot slot : decomposerSlots) {
			if (slot != null) {
				slot.drawTooltip(mouseX + 10, mouseY);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getEventX() * width / mc.displayWidth;
		int j = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		mouseX = i - (width - xSize) / 2;
		mouseY = j - (height - ySize) / 2;
		if (isScrollBarActive()) {
			scrollBar.handleMouseInput();
		}

	}

	@Override
	public boolean isScrollBarActive() {
		return true;
	}

	@Override
	public int getScreenWidth() {
		return width;
	}

	@Override
	public int getScreenHeight() {
		return height;
	}

	@Override
	public int getGuiWidth() {
		return xSize;
	}

	@Override
	public int getGuiHeight() {
		return ySize;
	}

	@Override
	public int getScrollAmount() {
		return 5;
	}

}
