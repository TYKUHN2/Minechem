package minechem.tileentity.multiblock.fusion;

import minechem.client.gui.widget.tab.GuiTabState;
import net.minecraft.client.gui.Gui;

public class FusionTabStateControl extends GuiTabState {
	private int lastKnownEnergyCost = 0;

	public FusionTabStateControl(Gui gui, FusionTileEntity fusion) {
		super(gui);
		tileEntity = fusion;
		state = TabState.norecipe;
	}

	@Override
	public void update() {
		super.update();
		if (tileEntity instanceof FusionTileEntity) {
			if (!tileEntity.inventory.get(0).isEmpty() && !tileEntity.inventory.get(1).isEmpty()) {
				if (((FusionTileEntity) tileEntity).inputsCanBeFused()) {
					lastKnownEnergyCost = tileEntity.getEnergyRequired();
					if (lastKnownEnergyCost <= tileEntity.getEnergyStored()) {
						if (((FusionTileEntity) tileEntity).canOutput()) {
							state = TabState.powered;
						}
						else {
							state = TabState.jammed;
						}
					}
					else {
						state = TabState.unpowered;
					}
				}
				else {
					state = TabState.norecipe;
				}
			}
			else {
				state = TabState.norecipe;
			}
			overlayColor = state.color;
		}
	}

	@Override
	public String getTooltip() {
		if (!isFullyOpened()) {
			if (state == TabState.unpowered && lastKnownEnergyCost > 0) {
				return "Energy Needed: " + lastKnownEnergyCost;
			}
			return state.tooltip;
		}
		return null;
	}

}
