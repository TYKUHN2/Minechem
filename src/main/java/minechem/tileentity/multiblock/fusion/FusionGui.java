package minechem.tileentity.multiblock.fusion;

import minechem.client.gui.GuiContainerTabbed;
import minechem.client.gui.widget.tab.GuiTabPatreon;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class FusionGui extends GuiContainerTabbed {
	private static float increaseRate = .2F;
	private static float decreaseRate = .4F;

	FusionTileEntity fusion;
	static int guiWidth = 176;
	static int guiHeight = 187;
	int storedEnergy;
	int maxEnergy;

	public FusionGui(InventoryPlayer inventoryPlayer, FusionTileEntity fusion) {
		super(new FusionContainer(inventoryPlayer, fusion));
		this.fusion = fusion;
		xSize = guiWidth;
		ySize = guiHeight;
		storedEnergy = this.fusion.getEnergyStored();
		maxEnergy = fusion.getMaxEnergyStored();
		addTab(new FusionTabStateControl(this, fusion));
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.fusion")));
		addTab(new GuiTabPatreon(this));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		String info = MinechemUtil.getLocalString("block.fusionReactor.name");
		int infoWidth = fontRenderer.getStringWidth(info);
		fontRenderer.drawString(info, (guiWidth - infoWidth) / 2, 5, 0x000000);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		updateEnergy();

		mc.renderEngine.bindTexture(ModResources.Gui.FUSION);
		int x = (width - guiWidth) / 2;
		int y = (height - guiHeight) / 2;
		// DRAW GUI
		drawTexturedModalRect(x, y, 0, 0, guiWidth, guiHeight);

		// DRAW ENERGY BAR OVERLAY
		drawEnergyBarOverlay(x, y);
	}

	public void drawEnergyBarOverlay(int x, int y) {
		// @TODO - calculate energybar width based on machine state and energy
		drawTexturedModalRect(x + 8, y + 38, 0, 187, fusion.getPowerRemainingScaled(160), 3);

	}

	private void updateEnergy() {
		storedEnergy = fusion.getEnergyStored();
	}

}
