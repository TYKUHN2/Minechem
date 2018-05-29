package minechem.client.gui;

import minechem.init.ModConfig;
import minechem.init.ModGlobals;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiConfiguration extends GuiConfig {

	public GuiConfiguration(GuiScreen guiScreen) {
		super(guiScreen, ModConfig.getConfigElements(), ModGlobals.ID, false, false, GuiConfiguration.getAbridgedConfigPath(ModConfig.config.toString()));
	}

}
