package minechem.client.gui.widget.tab;

import minechem.client.gui.GuiContainerTabbed;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.util.ResourceLocation;

public class TabHelp extends GuiTab {
	String helpString;
	int stringWidth;

	public TabHelp(GuiContainerTabbed gui, String helpString) {
		super(gui);
		this.helpString = helpString;
		maxWidth = 120;
		stringWidth = maxWidth - 10;
		maxHeight = MinechemUtil.getSplitStringHeight(fontRenderer, helpString, stringWidth) + 20;
		overlayColor = 0x88BBBB;
	}

	@Override
	public void draw(int x, int y) {
		drawBackground(x, y);
		if (!isFullyOpened()) {
			drawIcon(x + 2, y + 3);
			return;
		}
		fontRenderer.drawSplitString(helpString, x + 6, y + 10, stringWidth, 0x000000);
	}

	@Override
	public String getTooltip() {
		if (!isOpen()) {
			String localizedTooltip = MinechemUtil.getLocalString("tab.tooltip.help");
			if (localizedTooltip.equals("tab.tooltip.help") || localizedTooltip.isEmpty()) {
				return "Help";
			}
			else {
				return localizedTooltip;
			}
		}
		else {
			return null;
		}
	}

	@Override
	public ResourceLocation getIcon() {
		return ModResources.Icon.HELP;
	}

}
