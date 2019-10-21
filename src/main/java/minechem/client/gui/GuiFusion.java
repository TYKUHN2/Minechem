package minechem.client.gui;

import minechem.block.multiblock.tile.TileFusionCore;
import minechem.block.multiblock.tile.TileReactorCore;
import minechem.client.gui.widget.tab.TabFusionState;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerFusion;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiFusion extends GuiContainerTabbed {
	//private static float increaseRate = .2F;
	//private static float decreaseRate = .4F;

	public TileFusionCore fusion;
	static int guiWidth = 176;
	static int guiHeight = 187;
	public int storedEnergy;
	int maxEnergy;

	public GuiFusion(final InventoryPlayer inventoryPlayer, final TileFusionCore fusion) {
		super(new ContainerFusion(inventoryPlayer, fusion));
		this.fusion = fusion;
		xSize = guiWidth;
		ySize = guiHeight;
		storedEnergy = this.fusion.getEnergyStored();
		maxEnergy = fusion.getMaxEnergyStored();
		addTab(new TabFusionState(this));
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.fusion")));
		//addTab(new GuiTabPatreon(this));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {

		final String info = MinechemUtil.getLocalString("gui.title.fusion_chamber");
		final int infoWidth = fontRenderer.getStringWidth(info);
		fontRenderer.drawString(info, (guiWidth - infoWidth) / 2, 6, 0x000000);
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		updateEnergy();

		mc.renderEngine.bindTexture(ModResources.Gui.FUSION);
		final int x = (width - guiWidth) / 2;
		final int y = (height - guiHeight) / 2;
		// DRAW GUI
		drawTexturedModalRect(x, y, 0, 0, guiWidth, guiHeight);

		// DRAW ENERGY BAR OVERLAY
		drawEnergyBarOverlay(x, y);
	}

	public void drawEnergyBarOverlay(final int x, final int y) {
		//int stored = ((TileReactorCore) mc.world.getTileEntity(fusion.getPos())).getEnergyStored();
		//int max = ((TileReactorCore) mc.world.getTileEntity(fusion.getPos())).getMaxEnergyStored();
		final int power2 = ((TileReactorCore) mc.world.getTileEntity(fusion.getPos())).getPowerRemainingScaled(160);
		//int power = stored * (160 / max);
		// @TODO - calculate energybar width based on machine state and energy
		drawTexturedModalRect(x + 8, y + 20, 0, 187, power2, 3);

	}

	private void updateEnergy() {
		storedEnergy = ((TileReactorCore) mc.world.getTileEntity(fusion.getPos())).getEnergyStored();
	}

}
