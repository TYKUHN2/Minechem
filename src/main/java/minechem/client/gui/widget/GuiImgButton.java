package minechem.client.gui.widget;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author p455w0rd
 *
 */
public class GuiImgButton extends GuiButton {

	GuiScreen screen;
	ResourceLocation loc;
	int lx;
	int ly;
	int ww;
	int hh;
	public boolean active = true;

	public GuiImgButton(GuiScreen screen, int buttonId, int x, int y, int width, int height, ResourceLocation loc, int lx, int ly, int ww, int hh) {
		super(buttonId, x, y, width, height, "");
		this.screen = screen;
		this.loc = loc;
		this.lx = lx;
		this.ly = ly;
		this.ww = ww;
		this.hh = hh;
	}

	@Override
	public void drawButton(Minecraft mc, int xx, int yy, float pt) {
		if (visible) {
			FontRenderer fontrenderer = mc.fontRenderer;
			hovered = xx >= x - width / 2 && yy >= y - height / 2 && xx < x - width / 2 + width && yy < y - height / 2 + height;
			int k = getHoverState(hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			Color c = new Color(0xFFFFFF);
			float cc = 0.9f;
			float ac = 1.0f;
			if (k == 2) {
				ac = 1.0f;
				cc = 1.0f;
			}
			if (!active) {
				cc = 0.5f;
				ac = 0.9f;
			}
			GlStateManager.color(cc * (c.getRed() / 255.0f), cc * (c.getGreen() / 255.0f), cc * (c.getBlue() / 255.0f), ac);
			mc.getTextureManager().bindTexture(loc);
			this.drawTexturedModalRect(x - ww / 2, y - hh / 2, lx, ly, ww, hh);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			if (displayString != null) {
				int j = 16777215;
				if (!enabled) {
					j = 10526880;
				}
				else if (hovered) {
					j = 16777120;
				}
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, 0.0);
				GL11.glScaled(0.5, 0.5, 0.0);
				drawCenteredString(fontrenderer, new TextComponentTranslation(displayString, new Object[0]).getFormattedText(), 0, -4, j);
				GL11.glTranslated(-x, -y, 0.0);
				GL11.glPopMatrix();

			}
			mouseDragged(mc, xx, yy);
		}
	}

	@Override
	public void drawButtonForegroundLayer(int xx, int yy) {
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return active && enabled && visible && mouseX >= x - width / 2 && mouseY >= y - height / 2 && mouseX < x - width / 2 + width && mouseY < y - height / 2 + height;
	}
}