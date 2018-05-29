package minechem.tileentity.multiblock.fission;

import minechem.client.gui.GuiContainerTabbed;
import minechem.client.gui.widget.tab.GuiTabPatreon;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class FissionGui extends GuiContainerTabbed {
	int guiWidth = 176;
	int guiHeight = 166;

	public FissionGui(Container par1Container) {
		super(par1Container);
	}

	public FissionGui(InventoryPlayer inventoryPlayer, FissionTileEntity fission) {
		super(new FissionContainer(inventoryPlayer, fission));
		addTab(new FissionTabStateControl(this, fission));
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.fission")));
		addTab(new GuiTabPatreon(this));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		String info = MinechemUtil.getLocalString("block.fissionReactor.name");
		int infoWidth = fontRenderer.getStringWidth(info);
		fontRenderer.drawString(info, (guiWidth - infoWidth) / 2, 5, 0x000000);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.FISSION);
		int x = (width - guiWidth) / 2;
		int y = (height - guiHeight) / 2;
		// DRAW GUI
		drawTexturedModalRect(x, y, 0, 0, guiWidth, guiHeight);

	}

}
