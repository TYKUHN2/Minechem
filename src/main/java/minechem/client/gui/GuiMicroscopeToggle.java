package minechem.client.gui;

import minechem.init.ModGlobals.ModResources;

public class GuiMicroscopeToggle extends GuiTogglable {

	public GuiMicroscopeToggle(GuiContainerTabbed container) {
		super();
		this.container = container;
		width = 13;
		height = 13;
		numStates = 2;
		texture = ModResources.Gui.MICROSCOPE;
		ToggleButton buttonSynthesis = new ToggleButton();
		buttonSynthesis.u = 176;
		buttonSynthesis.v = 124;
		buttonSynthesis.tooltip = "gui.title.synthesis";

		ToggleButton buttonDecomposer = new ToggleButton();
		buttonDecomposer.u = 189;
		buttonDecomposer.v = 124;
		buttonDecomposer.tooltip = "gui.title.decomposer";
		buttons.put(0, buttonSynthesis);
		buttons.put(1, buttonDecomposer);
	}
}
