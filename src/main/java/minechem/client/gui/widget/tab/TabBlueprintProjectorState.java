package minechem.client.gui.widget.tab;

import minechem.block.tile.TileBlueprintProjector;
import minechem.init.ModConfig;
import minechem.utils.MinechemUtil;
import net.minecraft.client.gui.Gui;

/**
 * @author p455w0rd
 *
 */
public class TabBlueprintProjectorState extends GuiTabState {

	public TabBlueprintProjectorState(Gui gui, TileBlueprintProjector projector) {
		super(gui);
		tileEntity = projector;
	}

	@Override
	public void update() {
		super.update();
		TileBlueprintProjector projector = (TileBlueprintProjector) tileEntity;
		if (!ModConfig.powerUseEnabled || projector.getEnergyStored() > ModConfig.costDecomposition) {
			state = TabState.powered;
		}
		else {
			state = TabState.unpowered;
		}
		overlayColor = state.color;
	}

	@Override
	public void draw(int x, int y) {
		drawBackground(x, y);
		drawIcon(x + 1, y + 3);
		if (!isFullyOpened()) {
			return;
		}

		// State
		//fontRenderer.drawStringWithShadow(MinechemUtil.getLocalString(state.tooltip), x + 22, y + 10, headerColour);
		fontRenderer.drawSplitString(MinechemUtil.getLocalString(state.tooltip), x + 22, y + 3, maxWidth, headerColour);

		// Amount of power stored.
		if (ModConfig.powerUseEnabled) {
			fontRenderer.drawStringWithShadow(MinechemUtil.getLocalString("tab.title.stored") + ":", x + 5, y + 30, subheaderColour);
			String print = tileEntity.getEnergyStored() + " FE (" + String.valueOf(tileEntity.getPowerRemainingScaled(100D)) + "%)";
			fontRenderer.drawString(print, x + 5, y + 40, textColour);

			String energyMsg = MinechemUtil.getLocalString("tab.title.activation_energy_projector");
			int yPos = y + 60;
			String[] lines = energyMsg.split("\n");
			for (String str : lines) {
				fontRenderer.drawStringWithShadow(str, x + 5, yPos, subheaderColour);
				yPos += 10;
			}
			//fontRenderer.drawStringWithShadow(MinechemUtil.getLocalString("tab.title.activation_energy_projector"), x + 5, y + 60, subheaderColour);
			fontRenderer.drawString(String.valueOf(tileEntity.getEnergyRequired()) + " FE", x + 5, yPos, textColour);
		}
	}

}
