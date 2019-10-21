package minechem.client.gui.widget;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import minechem.api.client.gui.widget.IGuiMaterialListSlot;
import minechem.client.gui.GuiBlueprintProjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

/**
 * @author p455w0rd
 *
 */
public class GuiMaterialListSlot implements IGuiMaterialListSlot {

	final ItemStack stack;
	int count, index, x, y;
	final List<String> tooltip;
	final GuiBlueprintProjector gui;
	final int width = 49;
	final int height = 10;

	public GuiMaterialListSlot(final GuiBlueprintProjector gui, final int index, final int x, final int y, final Pair<ItemStack, Integer> listItem, final List<String> tooltip) {
		this.gui = gui;
		this.index = index;
		this.x = x;
		this.y = y;
		stack = listItem.getLeft();
		count = listItem.getRight();
		this.tooltip = tooltip;
	}

	@Override
	public boolean isMouseOver(final int mouseX, final int mouseY) {
		if (getGui().getProjector().hasBlueprint()) {
			final int yOffset = getGui().guiTop() + getY() + 50;
			return mouseX >= getGui().guiLeft() + getX() + 1 && mouseX < getGui().guiLeft() + getX() + width() && mouseY >= yOffset + getIndex() && mouseY < yOffset + height();
		}
		return false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public GuiBlueprintProjector getGui() {
		return gui;
	}

	@Override
	public Pair<ItemStack, Integer> get() {
		return Pair.of(stack, count);
	}

	@Override
	public List<String> getTooltip() {
		return tooltip;
	}

	@Override
	public void draw(final int x, final int y, final int mouseX, final int mouseY) {
		this.x = x;
		this.y = y;
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.6, 0.6, 0.6);
		drawItem(x + 8, y * 2 + 77 - getIndex(), get().getLeft());

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.scale(0.5, 0.5, 0.5);
		Minecraft.getMinecraft().fontRenderer.drawString("x " + get().getRight(), x + 34, y * 2 + 107, 0x000000);
		GlStateManager.popMatrix();
	}

	protected void drawItem(final int x, final int y, final ItemStack is) {
		getGui().setZLevel(100.0F);
		getGui().setItemRenderZLevel(100.0F);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableDepth();
		getGui().getItemRenderer().renderItemAndEffectIntoGUI(is, x, y);
		GlStateManager.disableDepth();
		getGui().setItemRenderZLevel(0.0F);
		getGui().setZLevel(0.0F);
	}

}
