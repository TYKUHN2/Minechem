package minechem.client.gui;

import java.io.IOException;

import minechem.Minechem;
import minechem.client.gui.widget.tab.TabChemistJournal;
import minechem.container.ChemistJournalContainer;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class GuiTableOfElements extends GuiContainerTabbed {

	public static final int GUI_WIDTH = 876;
	public static final int GUI_HEIGHT = 600;

	public GuiTableOfElements(EntityPlayer entityPlayer) {
		super(new ChemistJournalContainer(entityPlayer.inventory));
		addTab(new TabChemistJournal(this));
		xSize = GUI_WIDTH;
		ySize = GUI_HEIGHT;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		//int x = xSize;
		//int y = ySize;
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.pushMatrix();
		GlStateManager.scale(2.1F, 1.5F, 2.0F);

		mc.renderEngine.bindTexture(ModResources.Tab.TABLE_HEX);
		drawTexturedModalRect(0, 0, 0, 0, xSize / 2, ySize / 2);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();

	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (x == GUI_WIDTH - 411) {
			if (y == GUI_HEIGHT - 411) {
				mc.player.openGui(Minechem.INSTANCE, 2, mc.world, x, y, 0);
			}

		}
	}

}
