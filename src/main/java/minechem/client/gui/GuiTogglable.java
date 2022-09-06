package minechem.client.gui;

import java.util.HashMap;

import minechem.utils.MinechemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

public abstract class GuiTogglable {

	protected int numStates;
	protected int state = 0;
	protected int zLevel = 100;
	protected int width;
	protected int height;
	protected int x;
	protected int y;
	protected int mouseX;
	protected int mouseY;
	protected Minecraft mc;
	protected GuiContainerTabbed container;
	protected ResourceLocation texture;
	protected HashMap<Integer, ToggleButton> buttons = new HashMap<>();

	public GuiTogglable() {
		mc = FMLClientHandler.instance().getClient();
	}

	public void draw(TextureManager renderEngine) {
		renderEngine.bindTexture(texture);
		ToggleButton button = buttons.get(state);
		drawTexturedModalRect(x, y, button.u, button.v, width, height);
		String tooltip = MinechemUtil.getLocalString(button.tooltip);
		int cx = (container.width - container.getXSize()) / 2;
		int cy = (container.height - container.getYSize()) / 2;
		int tooltipWidth = mc.fontRenderer.getStringWidth(tooltip);

		if (isMoverOver()) {
			GlStateManager.disableDepth();
			container.drawHoveringText(tooltip, cx + 77 - (tooltipWidth / 2), cy + 100);
			GlStateManager.enableDepth();
		}
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void mouseClicked(int x, int y, int mouseButton) {
		if (isMoverOver()) {
			onClicked();
		}
	}

	public boolean isMoverOver() {
		mouseX = container.getMouseX();
		mouseY = container.getMouseY();
		return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
	}

	public int getState() {
		return state;
	}

	private void onClicked() {
		state++;
		if (state == numStates) {
			state = 0;
		}
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
	 */
	public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);
		vb.pos(par1, par2 + par6, zLevel).tex((par3) * var7, (par4 + par6) * var8).endVertex();
		vb.pos(par1 + par5, par2 + par6, zLevel).tex((par3 + par5) * var7, (par4 + par6) * var8).endVertex();
		vb.pos(par1 + par5, par2, zLevel).tex((par3 + par5) * var7, (par4) * var8).endVertex();
		vb.pos(par1, par2, zLevel).tex((par3) * var7, (par4) * var8).endVertex();
		tessellator.draw();
	}

	public static class ToggleButton {
		public int u;
		public int v;
		public String tooltip;
	}

}
