package minechem.client.gui.widget.tab;

import minechem.client.gui.GuiTableOfElements;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class TabChemistJournal extends GuiTab {
	public TabChemistJournal(Gui gui) {
		super(gui);

		currentX = GuiTableOfElements.GUI_WIDTH - 411;
		currentY = GuiTableOfElements.GUI_HEIGHT - 411;
		overlayColor = 0x2F7DAA;
	}

	@Override
	public void draw(int x, int y) {
		drawBackground(x, y);
		if (!isFullyOpened()) {
			drawIcon(x + 2, y + 3);
		}

	}

	@Override
	public String getTooltip() {

		return "Journal";
	}

	@Override
	public ResourceLocation getIcon() {
		return ModResources.Icon.HELP;
	}

}
