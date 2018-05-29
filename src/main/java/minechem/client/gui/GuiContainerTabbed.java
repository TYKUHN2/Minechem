package minechem.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import minechem.client.gui.widget.tab.GuiTab;
import minechem.client.gui.widget.tab.GuiTabPatreon;
import minechem.utils.MinechemUtil;
import minechem.utils.SessionVars;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public abstract class GuiContainerTabbed extends GuiContainerBase implements GuiYesNoCallback {

	private String clickedURI;

	protected static enum SlotColor {
			BLUE, RED, YELLOW, ORANGE, GREEN, PURPLE
	}

	protected static enum SlotType {
			SINGLE, OUTPUT, DOUBLEOUTPUT
	}

	protected static enum SlotRender {
			TOP, BOTTOM, FULL
	}

	protected static int SCALE_ENERGY = 42;
	protected static int SCALE_LIQUID = 60;
	protected static int SCALE_PROGRESS = 24;
	protected static int SCALE_SPEED = 16;

	protected ArrayList<GuiTab> tabListLeft = new ArrayList<GuiTab>();
	protected ArrayList<GuiTab> tabListRight = new ArrayList<GuiTab>();

	public int mouseX = 0;
	public int mouseY = 0;

	public static boolean drawBorders;

	private final int _zLevel = 3;

	public void drawTexture(int x, int y, ResourceLocation resource) {
		int w = 16;
		int h = 16;
		mc.getTextureManager().bindTexture(resource);
		GlStateManager.color(1F, 1F, 1F, 1F);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x + 0, y + h, _zLevel - 1).tex(0D, 1D).endVertex();
		buffer.pos(x + w, y + h, _zLevel - 1).tex(1D, 1D).endVertex();
		buffer.pos(x + w, y + 0, _zLevel - 1).tex(1D, 0D).endVertex();
		buffer.pos(x + 0, y + 0, _zLevel - 1).tex(0D, 0D).endVertex();
		tessellator.draw();

	}

	public GuiContainerTabbed(Container container) {
		this(container, null);
	}

	public GuiContainerTabbed(Container container, RenderItem itemRenderer) {
		super(container, itemRenderer);
	}

	public List<GuiTab> getLeftGuiTabs() {
		return tabListLeft;
	}

	public List<GuiTab> getRightGuiTabs() {
		return tabListRight;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();

		drawTabs();

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
	}

	@Override
	public void drawScreen(int mX, int mY, float par3) {
		super.drawScreen(mX, mY, par3);
		GuiTab guiTab = getTabAtPosition(mouseX, mouseY);
		if (guiTab != null) {
			String tooltip = guiTab.getTooltip();
			if (tooltip != null) {
				drawHoveringText(tooltip, mX, mY);
				//net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Arrays.asList(tooltip), mX, mY, width, height, -1, mc.fontRenderer);
			}
		}

	}

	/* DRAW FUNCTIONS */
	protected void drawColoredSlot(int x, int y, SlotColor color, SlotType type, SlotRender render) {

		if (drawBorders) {
			drawColoredSlotWithBorder(x, y, color, type, render);
		}
		else {
			drawColoredSlotNoBorder(x, y, color, type, render);
		}
	}

	protected void drawColoredSlotNoBorder(int x, int y, SlotColor color, SlotType type, SlotRender render) {

		int sizeX = 0;
		int sizeY = 0;
		int offsetX = color.ordinal() / 3 * 128;
		int offsetY = color.ordinal() % 3 * 32;

		switch (type) {
		case SINGLE:
			sizeX = 16;
			sizeY = 16;
			offsetX += 8;
			offsetY += 8;
			break;
		case OUTPUT:
			sizeX = 24;
			sizeY = 24;
			offsetX += 36;
			offsetY += 4;
			break;
		case DOUBLEOUTPUT:
			sizeX = 42;
			sizeY = 24;
			offsetX += 75;
			offsetY += 4;
			break;
		}

		switch (render) {
		case TOP:
			sizeY /= 2;
			break;
		case BOTTOM:
			sizeY /= 2;
			y += sizeY;
			offsetY += sizeY;
			break;
		case FULL:
			break;
		}

		drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

	protected void drawColoredSlotWithBorder(int x, int y, SlotColor color, SlotType type, SlotRender render) {

		int sizeX = 32;
		int sizeY = 32;
		int offsetX = color.ordinal() / 3 * 128;
		int offsetY = color.ordinal() % 3 * 32;

		offsetX += type.ordinal() * 32;

		if (type.ordinal() == 2) {
			sizeX = 64;
		}

		switch (type) {
		case SINGLE:
			x -= 8;
			y -= 8;
			break;
		case OUTPUT:
			x -= 4;
			y -= 4;
			break;
		case DOUBLEOUTPUT:
			x -= 11;
			y -= 4;
			break;
		}

		switch (render) {
		case TOP:
			sizeY /= 2;
			break;
		case BOTTOM:
			sizeY /= 2;
			y += sizeY;
			offsetY += sizeY;
			break;
		case FULL:
			break;
		}

		drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

	protected void drawColoredLiquidSlot(int x, int y, SlotColor color) {

		if (drawBorders) {
			drawColoredLiquidSlotWithBorder(x, y, color);
		}
		else {
			drawColoredLiquidSlotNoBorder(x, y, color);
		}
	}

	protected void drawColoredLiquidSlotNoBorder(int x, int y, SlotColor color) {

		int sizeX = 16;
		int sizeY = 60;
		int offsetX = color.ordinal() * 32;
		int offsetY = 96;

		drawTexturedModalRect(x, y, offsetX + 8, offsetY + 2, sizeX, sizeY);
	}

	protected void drawColoredLiquidSlotWithBorder(int x, int y, SlotColor color) {

		int sizeX = 32;
		int sizeY = 64;
		int offsetX = color.ordinal() * 32;
		int offsetY = 96;

		drawTexturedModalRect(x - 8, y - 2, offsetX, offsetY, sizeX, sizeY);
	}

	/* UTILITY FUNCTIONS */
	protected int getCenteredOffset(String string) {

		return this.getCenteredOffset(string, xSize);
	}

	protected int getCenteredOffset(String string, int xWidth) {
		//FontRenderer fontRenderer = RenderManager.instance.getFontRenderer();

		return (xWidth - fontRenderer.getStringWidth(string)) / 2;
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);

		GuiTab guiTab = getTabAtPosition(mouseX, mouseY);

		if (guiTab != null) {

			if (guiTab.leftSide) {
				for (GuiTab other : tabListLeft) {
					if (other != guiTab && other.isOpen()) {
						other.toggleOpen();
					}
				}
			}
			else {
				for (GuiTab other : tabListRight) {
					if (other != guiTab && other.isOpen()) {
						other.toggleOpen();
					}
				}
			}
			if (guiTab instanceof GuiTabPatreon) {
				GuiTabPatreon patreonTab = (GuiTabPatreon) guiTab;

				if (patreonTab.isFullyOpened()) {
					if (patreonTab.isLinkAtOffsetPosition(x - guiLeft, y - guiTop)) {
						clickedURI = patreonTab.getLinkURL();
						if (mc.gameSettings.chatLinksPrompt) {
							mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickedURI, 0, false));
						}
						else {
							MinechemUtil.openURL(patreonTab.getLinkURL());
						}
						return;
					}
				}
			}
			guiTab.toggleOpen();
		}
	}

	@Override
	public void confirmClicked(boolean confirm, int id) {
		if (id == 0) {
			if (confirm) {
				MinechemUtil.openURL(clickedURI);
			}

			clickedURI = null;
			mc.displayGuiScreen(this);
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getEventX() * width / mc.displayWidth;
		int j = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		mouseX = i - (width - xSize) / 2;
		mouseY = j - (height - ySize) / 2;
	}

	/* TAB MANAGEMENT */
	public void addTab(GuiTab guiTab) {

		if (guiTab.leftSide) {
			tabListLeft.add(guiTab);
		}
		else {
			tabListRight.add(guiTab);
		}
		if (SessionVars.getOpenedTab() != null && guiTab.getClass().equals(SessionVars.getOpenedTab())) {
			guiTab.setFullyOpen();
		}
	}

	protected void drawTabs() {

		int yPos = 4;

		for (GuiTab guiTab : tabListLeft) {

			guiTab.update();
			if (!guiTab.isVisible()) {
				continue;
			}
			guiTab.drawTab(0, yPos);
			yPos += guiTab.getHeight();
		}

		yPos = 4;
		for (GuiTab guiTab : tabListRight) {

			guiTab.update();
			if (!guiTab.isVisible()) {
				continue;
			}

			guiTab.drawTab(xSize, yPos);
			yPos += guiTab.getHeight();
		}
	}

	protected GuiTab getTabAtPosition(int mX, int mY) {

		int xShift = 0;
		int yShift = 4;

		for (GuiTab guiTab : tabListLeft) {
			if (!guiTab.isVisible()) {
				continue;
			}

			guiTab.currentX = xShift;
			guiTab.currentY = yShift;
			if (guiTab.intersectsWith(mX, mY, xShift, yShift)) {
				return guiTab;
			}
			yShift += guiTab.getHeight();
		}

		xShift = xSize;
		yShift = 4;

		for (GuiTab guiTab : tabListRight) {
			if (!guiTab.isVisible()) {
				continue;
			}

			guiTab.currentX = xShift;
			guiTab.currentY = yShift;
			if (guiTab.intersectsWith(mX, mY, xShift, yShift)) {
				return guiTab;
			}
			yShift += guiTab.getHeight();
		}

		return null;
	}

	public int getMouseX() {
		return (Mouse.getX() * width / mc.displayWidth);
	}

	public int getMouseY() {
		return height - (Mouse.getY() * height / mc.displayHeight - 1);
	}

}
