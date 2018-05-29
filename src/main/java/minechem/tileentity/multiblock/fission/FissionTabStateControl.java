package minechem.tileentity.multiblock.fission;

import minechem.client.gui.widget.tab.GuiTabState;
import minechem.init.ModConfig;
import net.minecraft.client.gui.Gui;

public class FissionTabStateControl extends GuiTabState {
	private int lastKnownEnergyCost = 0;

	public FissionTabStateControl(Gui gui, FissionTileEntity fission) {
		super(gui);
		tileEntity = fission;
		state = TabState.norecipe;
	}

	@Override
	public void update() {
		super.update();
		if (tileEntity.inventory.get(0).isEmpty()) {
			state = TabState.norecipe;
		}
		else {
			lastKnownEnergyCost = (tileEntity.inventory.get(0).getItemDamage() + 1) * ModConfig.fissionMultiplier;
			if (((FissionTileEntity) tileEntity).inputIsFissionable()) {
				if (tileEntity.getEnergyRequired() < tileEntity.getEnergyStored()) {
					state = TabState.powered;
				}
				else {
					state = TabState.unpowered;
				}
			}
			else {
				state = TabState.norecipe;
			}
		}
		overlayColor = state.color;
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
