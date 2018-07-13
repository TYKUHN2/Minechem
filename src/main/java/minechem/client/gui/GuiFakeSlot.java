package minechem.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GuiFakeSlot extends Gui {

	private Minecraft mc;
	private GuiContainerTabbed parentContainer;
	private EntityPlayer player;
	private int xPos, yPos;
	private int xOffset = 0;
	private int yOffset = 0;
	private int width = 16;
	private int height = 16;
	private ItemStack itemstack;
	private RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

	public GuiFakeSlot(GuiContainerTabbed parentContainer, EntityPlayer player) {
		this.parentContainer = parentContainer;
		this.player = player;
		mc = Minecraft.getMinecraft();
	}

	private int mouseX() {
		return parentContainer.mouseX;
	}

	private int mouseY() {
		return parentContainer.mouseY;
	}

	public void setItemStack(ItemStack itemstack) {
		this.itemstack = itemstack;
	}

	public ItemStack getItemStack() {
		return itemstack;
	}

	public void setXPos(int x) {
		xPos = x;
	}

	public void setYPos(int y) {
		yPos = y;
	}

	public void setXOffset(int x) {
		xOffset = x;
	}

	public void setYOffset(int y) {
		yOffset = y;
	}

	public int getXPos() {
		return xPos + xOffset;
	}

	public int getYPos() {
		return yPos + yOffset;
	}

	public boolean getMouseIsOver() {
		int mx = mouseX();
		int my = mouseY();
		int x = getXPos();
		int y = getYPos();
		return mx >= x && mx <= x + width && my >= y && my <= y + height;
	}

	public void draw() {
		zLevel = 0.0F;
		GlStateManager.pushMatrix();
		GlStateManager.translate(getXPos(), yPos, 0);

		if (getMouseIsOver()) {
			drawBackgroundHighlight();
		}
		if (itemstack != null) {
			drawItemStack(itemstack);
		}

		GlStateManager.popMatrix();
	}

	public void drawTooltip(int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		if (itemstack != null && getMouseIsOver()) {
			drawItemStackTooltip(itemstack);
		}
		GlStateManager.translate(-x, -y, 0);
		GlStateManager.popMatrix();
	}

	private void drawItemStack(ItemStack itemstack) {
		GlStateManager.disableLighting();
		RenderHelper.enableGUIStandardItemLighting();
		zLevel = 100.0F;
		renderItem.zLevel = 100.0F;
		renderItem.renderItemAndEffectIntoGUI(itemstack, 0, 0);
		renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, itemstack, 0, 0, null);
		zLevel = 0.0F;
		renderItem.zLevel = 0.0F;
	}

	private void drawItemStackTooltip(ItemStack itemstack) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		List<String> lines = itemstack.getTooltip(player, mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);
		int x = 0;
		int y = 0;
		int lineSpacing = 10;
		int maxLineWidth = 0;
		for (String line : lines) {
			int lineWidth = mc.fontRenderer.getStringWidth(line);
			if (lineWidth > maxLineWidth) {
				maxLineWidth = lineWidth;
			}
		}

		int bkX = x - 3;
		int bkY = y - 3;
		int tooltipWidth = maxLineWidth + 4;
		int tooltipHeight = (lines.size() * lineSpacing) + 4;
		int backgroundColor;
		backgroundColor = 0xAA000088;
		drawGradientRect(bkX - 1, bkY - 1, bkX + tooltipWidth + 1, bkY + tooltipHeight + 1, backgroundColor, backgroundColor);
		backgroundColor = 0xCC000000;
		drawGradientRect(bkX, bkY, bkX + tooltipWidth, bkY + tooltipHeight, backgroundColor, backgroundColor);

		for (int i = 0; i < lines.size(); i++) {
			int tx = x;
			int ty = y + (i * 10);
			String tooltip = lines.get(i);
			if (i == 0) {
				tooltip = itemstack.getRarity().rarityColor + tooltip;
			}
			mc.fontRenderer.drawStringWithShadow(tooltip, tx, ty, 0xFFFFFFFF);
		}
		GlStateManager.enableDepth();
	}

	private void drawBackgroundHighlight() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int color4 = 0x44000000;
		drawGradientRect(0, 0, width, height, color4, color4);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
	}

}
