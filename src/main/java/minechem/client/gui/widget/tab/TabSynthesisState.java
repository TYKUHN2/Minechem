package minechem.client.gui.widget.tab;

import minechem.api.recipe.ISynthesisRecipe;
import minechem.block.tile.TileSynthesis;
import minechem.init.ModConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;

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

		TileSynthesis synthesis = (TileSynthesis) tileEntity;
		ISynthesisRecipe recipe = synthesis.getCurrentRecipe();
		if (recipe == null) {
			state = TabState.norecipe;
		}
		else {
			ItemStack outputStack = synthesis.getStackInSlot(TileSynthesis.SLOT_ID_OUTPUT_MATRIX);
			if (ModConfig.powerUseEnabled) {
				lastKnownEnergyCost = recipe.getEnergyCost();
				if (synthesis.hasEnoughPower()) {
					if (outputStack.isEmpty()) {
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
				if (outputStack.isEmpty()) {
					state = TabState.noingredients;
				}
				else {
					state = TabState.powered;
				}
			}
		}

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
