package minechem.client.gui.widget.tab;

import minechem.client.gui.GuiContainerTabbed;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.MinechemUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

/**
 *
 * @author jakimfett
 */
public class GuiTabPatreon extends GuiTab {
	String tabText[];
	int tabTextHeight[];
	String link;
	int stringWidth;
	String linkText;
	private int linkX;
	private int linkY;

	public GuiTabPatreon(GuiContainerTabbed gui) {
		super(gui);
		tabText = new String[2];
		tabTextHeight = new int[2];

		maxWidth = 120;
		stringWidth = maxWidth - 10;

		tabText[0] = MinechemUtil.getLocalString("tab.patreon.firstline");
		tabTextHeight[0] = MinechemUtil.getSplitStringHeight(fontRenderer, tabText[0], stringWidth);
		tabText[1] = MinechemUtil.getLocalString("tab.patreon.secondline");
		tabTextHeight[1] = MinechemUtil.getSplitStringHeight(fontRenderer, tabText[1], stringWidth);

		link = "http://jakimfett.com/patreon";
		linkText = MinechemUtil.getLocalString("tab.patreon.linktext");

		maxHeight = tabTextHeight[0] + tabTextHeight[1] + 35;
		overlayColor = 0xFFFFFF;
	}

	public String getLinkURL() {
		return link;
	}

	@Override
	public void draw(int x, int y) {
		drawBackground(x, y);
		if (!isFullyOpened()) {
			drawIcon(x + 2, y + 3);
			return;
		}

		fontRenderer.drawSplitString(tabText[0], x + 6, y + 10, stringWidth, 0x000000);
		fontRenderer.drawSplitString(tabText[1], x + 6, y + 15 + tabTextHeight[0], stringWidth, 0x000000);

		linkX = x + 6;
		linkY = y + 20 + tabTextHeight[0] + tabTextHeight[1];
		fontRenderer.drawSplitString(TextFormatting.UNDERLINE + "" + linkText, linkX, linkY, stringWidth, 0x0066CC);
	}

	@Override
	public String getTooltip() {
		if (!isOpen()) {
			String localizedTooltip = MinechemUtil.getLocalString("tab.tooltip.patreon");
			if (localizedTooltip.equals("tab.tooltip.patreon") || localizedTooltip.isEmpty()) {
				return "Support Minechem";
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
		return ModResources.Icon.PATREON;
	}

	public boolean isLinkAtOffsetPosition(int mouseX, int mouseY) {

		if (mouseX >= linkX) {
			if (mouseX <= linkX + fontRenderer.getStringWidth(linkText)) {
				if (mouseY >= linkY) {
					if (mouseY <= linkY + MinechemUtil.getSplitStringHeight(fontRenderer, linkText, fontRenderer.getStringWidth(linkText))) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
