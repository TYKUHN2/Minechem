package minechem.potion;

import minechem.api.IPotionEffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author p455w0rd
 *
 */
public class PotionAtropineHigh extends PotionMinechem implements IPotionEffectRenderer {

	public PotionAtropineHigh() {
		super(true, 0x00FF6E, "delerium");
	}

	@Override
	public void render(Integer level) {
		ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
		int width = scale.getScaledWidth();
		int height = scale.getScaledHeight();
		//Gui gui = new Gui();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		int color = (int) (220.0F * level - 150) << 24 | 1052704;
		Gui.drawRect(0, 0, width, height, color);
		GlStateManager.enableAlpha();
		GlStateManager.enableDepth();
	}

}
