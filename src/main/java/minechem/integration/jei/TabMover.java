package minechem.integration.jei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import minechem.client.gui.GuiContainerTabbed;
import minechem.client.gui.widget.tab.GuiTab;

/**
 * @author p455w0rd
 *
 */
public class TabMover implements IAdvancedGuiHandler<GuiContainerTabbed> {

	@Override
	public Class<GuiContainerTabbed> getGuiContainerClass() {
		return GuiContainerTabbed.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiContainerTabbed guiContainer) {
		List<Rectangle> tabBoxes = new ArrayList<>();
		for (GuiTab tab : guiContainer.getLeftGuiTabs()) {
			tabBoxes.add(tab.getBounds());
		}
		for (GuiTab tab : guiContainer.getRightGuiTabs()) {
			tabBoxes.add(tab.getBounds());
		}
		return tabBoxes;
	}

	@Nullable
	@Override
	public Object getIngredientUnderMouse(GuiContainerTabbed guiContainer, int mouseX, int mouseY) {

		return null;
	}

}