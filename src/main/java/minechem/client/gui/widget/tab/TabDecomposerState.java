package minechem.client.gui.widget.tab;

import minechem.block.tile.TileDecomposer;
import minechem.block.tile.TileDecomposer.State;
import minechem.init.ModConfig;
import net.minecraft.client.gui.Gui;

public class TabDecomposerState extends GuiTabState {
	public TabDecomposerState(Gui gui, TileDecomposer decomposer) {
		super(gui);
		tileEntity = decomposer;
	}

	@Override
	public void update() {
		super.update();
		TileDecomposer decomposer = (TileDecomposer) tileEntity;
		State state = decomposer.getState();
		if (!ModConfig.powerUseEnabled || decomposer.getEnergyStored() > ModConfig.costDecomposition) {
			this.state = TabState.powered;
		}
		else {
			this.state = TabState.unpowered;
		}
		if (state == State.jammed) {
			this.state = TabState.jammed;
		}

		overlayColor = this.state.color;
	}

}
