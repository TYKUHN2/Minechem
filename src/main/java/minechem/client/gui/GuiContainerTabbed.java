package minechem.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import minechem.client.gui.widget.tab.GuiTab;
import minechem.client.gui.widget.tab.GuiTabPatreon;
import minechem.utils.MinechemUtil;
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

	private static Class<?> openedTab;

	private String clickedURI;

	protected enum SlotColor {
			BLUE, RED, YELLOW, ORANGE, GREEN, PURPLE
	}

	protected enum SlotType {
			SINGLE, OUTPUT, DOUBLEOUTPUT
	}

	protected enum SlotRender {
			TOP, BOTTOM, FULL
	}

	protected static int SCALE_ENERGY = 42;
	protected static int SCALE_LIQUID = 60;
	protected static int SCALE_PROGRESS = 24;
	protected static int SCALE_SPEED = 16;

	protected ArrayList<GuiTab> tabListLeft = new ArrayList<>();
	protected ArrayList<GuiTab> tabListRight = new ArrayList<>();

	public int mouseX = 0;
	public int mouseY = 0;

	public static boolean drawBorders;

	public void drawTexture(int x, int y, ResourceLocation resource) {
		int w = 16;
		int h = 16;
		mc.getTextureManager().bindTexture(resource);
		GlStateManager.color(1F, 1F, 1F, 1F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + h, 2).tex(0D, 1D).endVertex();
		buffer.pos(x + w, y + h, 2).tex(1D, 1D).endVertex();
		buffer.pos(x + w, y, 2).tex(1D, 0D).endVertex();
		buffer.pos(x, y, 2).tex(0D, 0D).endVertex();
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
			}
		}
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

	public void addTab(GuiTab guiTab) {

		if (guiTab.leftSide) {
			tabListLeft.add(guiTab);
		}
		else {
			tabListRight.add(guiTab);
		}
		if (getOpenedTab() != null && guiTab.getClass().equals(getOpenedTab())) {
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

	public static Class<?> getOpenedTab() {
		return openedTab;
	}

	public static void setOpenedTab(Class<?> tabClass) {
		openedTab = tabClass;
	}

}
