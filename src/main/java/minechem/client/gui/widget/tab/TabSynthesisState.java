package minechem.client.gui.widget.tab;

import minechem.block.tile.TileSynthesis;
import net.minecraft.client.gui.Gui;

public class TabSynthesisState extends GuiTabState {
	private int lastKnownEnergyCost;

	public TabSynthesisState(Gui gui, TileSynthesis synthesis) {
		super(gui);
		tileEntity = synthesis;
		state = TabState.norecipe;
	}

	@Override
	public void update() {
		super.update();
		/*
		TileSynthesis synthesis = (TileSynthesis) tileEntity;
		RecipeSynthesis recipe = synthesis.getCurrentRecipe();
		if (recipe == null) {
			state = TabState.norecipe;
		}
		else {
			if (ModConfig.powerUseEnabled) {
				lastKnownEnergyCost = recipe.energyCost();
				if (synthesis.hasEnoughPowerForCurrentRecipe()) {
					if (!synthesis.canTakeOutputStack(false)) {
						state = TabState.noingredients;
					}
					else {
						state = TabState.powered;
					}
				}
				else {
					state = TabState.unpowered;
				}
			}
			else {
				if (!synthesis.canTakeOutputStack(false)) {
					state = TabState.noingredients;
				}
				else {
					state = TabState.powered;
				}
			}
		}
		*/
		overlayColor = state.color;
	}

	@Override
	public String getTooltip() {
		if (!isFullyOpened()) {
			if (lastKnownEnergyCost > 0) {
				return "Energy Needed: " + lastKnownEnergyCost;
			}
			return state.tooltip;
		}
		return null;
	}

}
