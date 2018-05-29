package minechem.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import minechem.init.ModGlobals.ModResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluidTank extends Gui {
	private int capacity;
	private int posX, posY;

	public GuiFluidTank(int capacity, int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
		this.capacity = capacity;
	}

	public void draw(int x, int y, FluidStack fluidStack) {
		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Gui.TANK);
		drawTexturedModalRect(x + posX, y + posY, 0, 0, 18, 39);

		if (fluidStack != null && fluidStack.amount > 0) {
			int emptyHeight = 37 - ((int) ((38) * ((float) fluidStack.amount / capacity)));
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill().toString());
			drawRect(x + posX + 2, y + posY + 1 + emptyHeight, x + posX + 16, y + posY + 1 + 37, 0xFF000000);
			//GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			int color = fluidStack.getFluid().getColor(fluidStack) << 8;
			GlStateManager.color((color >>> 24) / 255F, (color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, 1.0f);
			for (int i = 0; i < 3; i++) {
				drawTexturedModalRect(x + posX + 2, (y + posY - 10) + (16 * i), sprite, 15, 16);
			}
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Gui.TANK);
			drawTexturedModalRect(x + posX + 2, y + posY + 1, 1, 1, 15, emptyHeight);
			drawTexturedModalRect(x + posX, y + posY, 0, 0, 18, 2);
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Gui.DECOMPOSER);
			drawTexturedModalRect(x + posX + 2, y + 5, 4, 4, 15, 11);
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Gui.TANK);
		drawTexturedModalRect(x + posX + 1, y + posY + 1, 19, 1, 16, 37);

		GlStateManager.enableLighting();
	}

	public void drawTooltip(int x, int y, FluidStack fluidStack) {
		if (!mouseInTank(x, y)) {
			return;
		}

		List<String> description = new ArrayList<String>();

		if (fluidStack == null || fluidStack.amount == 0 || fluidStack.getFluid() == null) {
			description.add("Empty");
		}
		else {
			if (fluidStack.amount > 0 && fluidStack.getFluid() != null) {
				String amountToText = fluidStack.amount + "mB";

				description.add(fluidStack.getLocalizedName());
				description.add(amountToText);
			}
		}
		drawHoveringText(description, x, y, Minecraft.getMinecraft().fontRenderer);
	}

	private boolean mouseInTank(int x, int y) {
		return x >= posX && x < posX + 16 && y >= posY && y < posY + 37;
	}

	@SuppressWarnings("rawtypes")
	protected void drawHoveringText(List list, int x, int y, FontRenderer fontrenderer) {
		if (!list.isEmpty()) {
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int k = 0;
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				int l = fontrenderer.getStringWidth(s);

				if (l > k) {
					k = l;
				}
			}

			int i1 = x + 12;
			int j1 = y - 12;
			int k1 = 8;

			if (list.size() > 1) {
				k1 += 2 + (list.size() - 1) * 10;
			}

			zLevel = 300.0F;
			int l1 = -267386864;
			drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
			drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
			drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
			drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
			drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
			int i2 = 1347420415;
			int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
			drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
			drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
			drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
			drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

			for (int k2 = 0; k2 < list.size(); ++k2) {
				String s1 = (String) list.get(k2);
				fontrenderer.drawStringWithShadow(s1, i1, j1, -1);

				if (k2 == 0) {
					j1 += 2;
				}

				j1 += 10;
			}

			zLevel = 0.0F;
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}
}
