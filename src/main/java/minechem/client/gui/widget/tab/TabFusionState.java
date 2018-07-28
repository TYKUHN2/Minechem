package minechem.client.gui.widget.tab;

import minechem.block.multiblock.tile.TileFusionCore;
import minechem.block.multiblock.tile.TileReactorCore;
import minechem.block.tile.TileMinechemEnergyBase;
import minechem.client.gui.GuiFusion;
import minechem.init.ModConfig;
import minechem.utils.MinechemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.IInventory;

public class TabFusionState extends GuiTabState {
	private int lastKnownEnergyCost = 0;

	public TabFusionState(Gui gui) {
		super(gui);
		tileEntity = ((GuiFusion) gui).fusion;
		state = TabState.norecipe;
	}

	@Override
	public void update() {
		super.update();
		if (tileEntity instanceof TileFusionCore) {
			TileFusionCore fusionTile = (TileFusionCore) tileEntity;
			if (fusionTile instanceof IInventory) {
				IInventory tileInv = fusionTile;
				if (!tileInv.getStackInSlot(0).isEmpty() && !tileInv.getStackInSlot(1).isEmpty()) {
					if (fusionTile.inputsCanBeFused()) {
						lastKnownEnergyCost = ((TileMinechemEnergyBase) fusionTile).getEnergyRequired();
						if (lastKnownEnergyCost <= ((TileMinechemEnergyBase) fusionTile).getEnergyStored()) {
							if (fusionTile.canOutput()) {
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
			}
			overlayColor = state.color;
		}
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
			String print = ((GuiFusion) myGui).storedEnergy + " FE (" + String.valueOf(((TileReactorCore) Minecraft.getMinecraft().world.getTileEntity(tileEntity.getPos())).getPowerRemainingScaled(100D)) + "%)";
			fontRenderer.drawString(print, x + 5, y + 40, textColour);

			fontRenderer.drawStringWithShadow(MinechemUtil.getLocalString("tab.title.activation_energy"), x + 5, y + 60, subheaderColour);
			fontRenderer.drawString(String.valueOf(tileEntity.getEnergyRequired()) + " FE", x + 5, y + 70, textColour);
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
