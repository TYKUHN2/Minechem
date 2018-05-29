package minechem.client.gui;

import java.io.IOException;

import minechem.block.tile.TileDecomposer;
import minechem.client.gui.widget.tab.TabDecomposerState;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerDecomposer;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDecomposer extends GuiContainerTabbed {

	private TileDecomposer decomposer;
	private GuiFluidTank guiFluidTank;
	private GuiButton dumpButton;
	int guiWidth = 176;
	int guiHeight = 186;

	public GuiDecomposer(InventoryPlayer inventoryPlayer, TileDecomposer decomposer) {
		super(new ContainerDecomposer(inventoryPlayer, decomposer));
		xSize = guiWidth;
		ySize = guiHeight;
		this.decomposer = decomposer;
		addTab(new TabDecomposerState(this, decomposer));
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.decomposer")));
		//addTab(new GuiTabPatreon(this));
		guiFluidTank = new GuiFluidTank(decomposer.getTankCapacity(), 18, 16);

	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		dumpButton = new GuiButton(0, 162, 44, 12, 12, "x");
		buttonList.add(dumpButton);
	}

	@Override
	public void drawScreen(int mX, int mY, float pticks) {
		super.drawScreen(mX, mY, pticks);
		dumpButton.enabled = decomposer.tank.getFluidAmount() > 0;
		if (dumpButton.isMouseOver() && decomposer.tank.getFluidAmount() > 0) {
			drawHoveringText("Dump " + decomposer.tank.getFluid().getLocalizedName(), mX, mY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

		String info = MinechemUtil.getLocalString("gui.title.decomposer");
		int infoWidth = fontRenderer.getStringWidth(info);
		GlStateManager.enableBlend();
		fontRenderer.drawString(info, (guiWidth - infoWidth) / 2, 5, 0x000000);
		guiFluidTank.drawTooltip(mouseX, mouseY, decomposer.tank.getFluid());
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (dumpButton.isMouseOver() && decomposer.tank.getFluidAmount() > 0) {
			decomposer.dumpFluid();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.DECOMPOSER);
		int x = (width - guiWidth) / 2;
		int y = (height - guiHeight) / 2;
		drawTexturedModalRect(x, y, 0, 0, guiWidth, guiHeight);
		decomposer = (TileDecomposer) mc.world.getTileEntity(decomposer.getPos());
		guiFluidTank.draw(x, y, decomposer.tank.getFluid());

	}

	private boolean mouseInButton(int x, int y) {
		return x >= dumpButton.x && x < dumpButton.x + dumpButton.width && y >= dumpButton.y && y < dumpButton.y + dumpButton.height;
	}

}
