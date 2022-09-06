package minechem.client.gui.widget.tab;

import minechem.block.multiblock.tile.TileFissionCore;
import minechem.block.multiblock.tile.TileFusionCore;
import minechem.init.ModConfig;
import net.minecraft.client.gui.Gui;

public class TabFissionState extends GuiTabState {
	private int lastKnownEnergyCost = 0;

	public TabFissionState(Gui gui, TileFissionCore fission) {
		super(gui);
		tileEntity = fission;
		state = TabState.norecipe;
	}

	@Override
	public void update() {
		super.update();
		if (tileEntity instanceof TileFusionCore) {
			TileFusionCore fissionTile = (TileFusionCore) tileEntity;
			if (fissionTile.getStackInSlot(0).isEmpty()) {
				state = TabState.norecipe;
			}
			else {
				lastKnownEnergyCost = (fissionTile.getStackInSlot(0).getItemDamage() + 1) * ModConfig.fissionMultiplier;
				if (((TileFissionCore) tileEntity).inputIsFissionable()) {
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
