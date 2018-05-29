package minechem.client.gui.widget.tab;

import minechem.block.tile.TileMinechemEnergyBase;
import minechem.init.ModConfig;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GuiTabState extends GuiTab {

	protected enum TabState {
			jammed(MinechemUtil.getLocalString("tab.tooltip.jammed"), 0xAA0000, ModResources.Icon.JAMMED),
			noBottles(MinechemUtil.getLocalString("tab.tooltip.nobottles"), 0xAA0000, ModResources.Icon.NO_BOTTLES),
			powered(MinechemUtil.getLocalString("tab.tooltip.powered"), 0x00CC00, ModResources.Icon.ENERGY),
			unpowered(MinechemUtil.getLocalString("tab.tooltip.unpowered"), 0xAA0000, ModResources.Icon.NO_ENERGY),
			norecipe(MinechemUtil.getLocalString("tab.tooltip.norecipe"), 0xAA0000, ModResources.Icon.NO_RECIPE),
			noingredients(MinechemUtil.getLocalString("tab.tooltip.notenoughingredients"), 0xAA0000, ModResources.Icon.NO_RECIPE);

		public String tooltip;
		public int color;

		@SideOnly(Side.CLIENT)
		public ResourceLocation resource;

		private TabState(String tooltip, int color, ResourceLocation resource) {
			this.tooltip = tooltip;
			this.color = color;
			this.resource = resource;
		}
	}

	int headerColour = 0xe1c92f;
	int subheaderColour = 0xaaafb8;
	int textColour = 0x000000;

	protected TabState state;
	protected TileMinechemEnergyBase tileEntity;

	public GuiTabState(Gui gui) {
		super(gui);
		state = TabState.unpowered;
		int h = 0;
		if (ModConfig.powerUseEnabled) {
			h = 75;
		}
		minWidth = 16 + 7;
		minHeight = 16 + 8;
		maxHeight = minHeight + h;
		maxWidth = minWidth + 80;
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

			fontRenderer.drawStringWithShadow(MinechemUtil.getLocalString("tab.title.activationEnergy"), x + 5, y + 60, subheaderColour);
			fontRenderer.drawString(String.valueOf(tileEntity.getEnergyRequired()) + " FE", x + 5, y + 70, textColour);
		}
	}

	@Override
	public String getTooltip() {
		if (!isFullyOpened()) {
			return state.tooltip;
		}
		return null;
	}

	@Override
	public ResourceLocation getIcon() {
		return state.resource;
	}
}
