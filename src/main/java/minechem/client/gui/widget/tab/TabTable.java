package minechem.client.gui.widget.tab;

import minechem.client.gui.GuiContainerTabbed;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class TabTable extends GuiTab {

	public TabTable(final Gui gui) {
		super(gui);
		maxWidth = 545 / 2 + 10;
		maxHeight = 272 / 2 + 10;
		overlayColor = 0x2F7DAA;
	}

	@Override
	public void draw(final int x, final int y) {
		drawBackground(x, y);
		if (!isFullyOpened()) {
			drawIcon(x + 2, y + 3);
		}
		else {
			if (myGui instanceof GuiContainerTabbed) {
				//GuiContainerTabbed tabGui = ((GuiContainerTabbed) myGui);
				//tabGui.drawTexture(x, y, ModResources.Tab.TABLE_HEX);
				final int w = 545 / 2;
				final int h = 272 / 2;
				Minecraft.getMinecraft().getTextureManager().bindTexture(ModResources.Tab.TABLE_HEX);
				GlStateManager.color(1F, 1F, 1F, 1F);

				final Tessellator tessellator = Tessellator.getInstance();
				final BufferBuilder buffer = tessellator.getBuffer();

				buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				buffer.pos(x + 4, y + h + 4, 2).tex(0D, 1D).endVertex();
				buffer.pos(x + 4 + w, y + h + 4, 2).tex(1D, 1D).endVertex();
				buffer.pos(x + 4 + w, y + 0 + 4, 2).tex(1D, 0D).endVertex();
				buffer.pos(x + 4 + 0, y + 0 + 4, 2).tex(0D, 0D).endVertex();
				tessellator.draw();
			}
		}
	}

	@Override
	public String getTooltip() {

		return "Table Of Elements";
	}

	@Override
	public ResourceLocation getIcon() {
		return ModResources.Icon.HELP;
	}

}
