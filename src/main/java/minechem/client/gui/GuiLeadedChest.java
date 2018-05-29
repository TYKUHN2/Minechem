package minechem.client.gui;

import minechem.block.tile.TileLeadedChest;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerLeadedChest;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiLeadedChest extends GuiContainerTabbed {
	int guiWidth = 176;
	int guiHeight = 217;
	TileLeadedChest leadedchest;

	public GuiLeadedChest(InventoryPlayer inventoryPlayer, TileLeadedChest leadedChest) {
		super(new ContainerLeadedChest(inventoryPlayer, leadedChest));
		leadedchest = leadedChest;
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.leadChest")));
		//addTab(new GuiTabPatreon(this));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String info = MinechemUtil.getLocalString("gui.title.leadedchest");
		int infoWidth = fontRenderer.getStringWidth(info);
		GlStateManager.enableBlend();
		fontRenderer.drawString(info, (guiWidth - infoWidth) / 2, 5, 0x000000);
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.LEADED_CHEST);

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0.0F);
		GlStateManager.popMatrix();
	}

}
