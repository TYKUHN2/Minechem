package minechem.client.gui;

import org.lwjgl.input.Mouse;

import minechem.api.IVerticalScrollContainer;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiVerticalScrollBar extends Gui {

	Minecraft mc;
	IVerticalScrollContainer container;
	int mouseX;
	int mouseY;
	// Width, height of the scrollbar slider.
	int width = 12;
	int height = 15;
	// Current X, Y position of the scrollbar slider.
	int xpos;
	int ypos;
	int startingYPos;
	// Max Y Displacement of the scrollbar slider.
	int maxDisplacement;
	float scaleFactor;
	float scrollValue = 0.0F;
	boolean isDragging = false;
	int activeU = 232;
	int activeV = 0;
	int unactiveU = 244;
	int unactiveV = 0;

	public GuiVerticalScrollBar(final IVerticalScrollContainer container, final int x, final int y, final int maxDisplacement, final int parentWidth, final int parentHeight) {
		this.container = container;
		xpos = x;
		ypos = y;
		startingYPos = y;
		this.maxDisplacement = maxDisplacement - height;
		scaleFactor = 1.0F / this.maxDisplacement;
		mc = FMLClientHandler.instance().getClient();
	}

	public void handleMouseInput() {
		final int screenWidth = container.getScreenWidth();
		final int screenHeight = container.getScreenHeight();
		final int parentWidth = container.getGuiWidth();
		final int parentHeight = container.getGuiHeight();
		final int i = Mouse.getEventX() * screenWidth / mc.displayWidth;
		final int j = screenHeight - Mouse.getEventY() * screenHeight / mc.displayHeight - 1;
		mouseX = i - (screenWidth - parentWidth) / 2;
		mouseY = j - (screenHeight - parentHeight) / 2;
		final int eventButton = Mouse.getEventButton();
		if (Mouse.getEventButtonState()) {
			onMouseClick();
		}
		else if (eventButton == -1) {
			onMouseMoved(Mouse.getDX(), Mouse.getDY());
		}
		else if (eventButton == 1 || eventButton == 0) {
			onMouseRelease();
		}
		final int wheelValue = Mouse.getEventDWheel();
		if (wheelValue != 0) {
			if (wheelValue > 0) {
				onMouseScroll(wheelValue, true);
			}
			else if (wheelValue < 0) {
				onMouseScroll(wheelValue, false);
			}
		}

	}

	public boolean pointIntersects(final int x, final int y) {
		return x >= xpos && x <= xpos + width && y >= ypos && y <= ypos + height;
	}

	/**
	 * Returns true iff the given GUI-relative point is within the scrollbar.
	 *
	 * @param x X coordinate relative to the parent container.
	 * @param y Y coordinate relative to the parent container.
	 * @return True iff the coordinates are within this scrollbar.
	 */
	public boolean pointInScrollBar(final int x, final int y) {
		return x >= xpos && x <= xpos + width && y >= startingYPos && y <= startingYPos + maxDisplacement + height;
	}

	public void setYPos(final int y) {
		ypos = y;
		if (ypos < startingYPos) {
			ypos = startingYPos;
		}
		if (ypos > startingYPos + maxDisplacement) {
			ypos = startingYPos + maxDisplacement;
		}
		scrollValue = (ypos - startingYPos) * scaleFactor;
	}

	private void onMouseClick() {
		if (container.isScrollBarActive()) {
			// Clicking on the slider starts dragging it.
			if (pointIntersects(mouseX, mouseY)) {
				isDragging = true;
			}
			else if (pointInScrollBar(mouseX, mouseY)) {
				// Move the slider one slider-height up or down.
				final int scrollAmount = height;
				if (mouseY < ypos) {
					// Up.
					setYPos(ypos - scrollAmount);
				}
				else if (mouseY > ypos + height) {
					// Down.
					setYPos(ypos + scrollAmount);
				}
			}
		}
	}

	private void onMouseRelease() {
		isDragging = false;
	}

	private void onMouseMoved(final int dx, final int dy) {
		if (container.isScrollBarActive() && isDragging) {
			setYPos(mouseY);
		}
	}

	private void onMouseScroll(final int value, final boolean up) {
		if (!container.isScrollBarActive()) {
			return;
		}
		if (up) {
			setYPos(ypos - container.getScrollAmount());
		}
		else {
			setYPos(ypos + container.getScrollAmount());
		}
	}

	public float getScrollValue() {
		return scrollValue;
	}

	public void draw() {
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(ModResources.SPRITES);
		if (container.isScrollBarActive()) {
			drawTexturedModalRect(xpos, ypos, activeU, activeV, width, height);
		}
		else {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
			drawTexturedModalRect(xpos, ypos, unactiveU, unactiveV, width, height);
		}
	}
}
