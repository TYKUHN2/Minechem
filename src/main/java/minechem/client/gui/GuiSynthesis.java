package minechem.client.gui;

import minechem.block.tile.TileSynthesis;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.client.gui.widget.tab.TabSynthesisState;
import minechem.container.ContainerSynthesis;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSynthesis extends GuiContainerTabbed {
	int guiWidth = 176;
	int guiHeight = 205;

	public GuiSynthesis(InventoryPlayer inventoryPlayer, TileSynthesis synthesis) {
		super(new ContainerSynthesis(inventoryPlayer, synthesis));
		xSize = guiWidth;
		ySize = guiHeight;
		addTab(new TabSynthesisState(this, synthesis));
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.synthesis")));
		//addTab(new GuiTabPatreon(this));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String info = MinechemUtil.getLocalString("gui.title.synthesis");
		int infoWidth = fontRenderer.getStringWidth(info);
		fontRenderer.drawString(info, (guiWidth - infoWidth) / 2, 5, 0x000000);
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.SYNTHESIS);
		int x = (width - guiWidth) / 2;
		int y = (height - guiHeight) / 2;
		drawTexturedModalRect(x, y, 0, 0, guiWidth, guiHeight);
	}

}
