package minechem.client.render;

import minechem.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class IconLayer implements ILayer {

	private final ResourceLocation iIcon;
	private final boolean colour;
	private final boolean isSolid;

	public IconLayer(ResourceLocation iIcon) {
		this(iIcon, false, true);
	}

	public IconLayer(ResourceLocation iIcon, boolean colour) {
		this(iIcon, colour, true);
	}

	public IconLayer(ResourceLocation iIcon, boolean colour, boolean isSolid) {
		this.iIcon = iIcon;
		this.colour = colour;
		this.isSolid = isSolid;
	}

	@Override
	public void render(int colour) {
		RenderUtil.resetOpenGLColour();
		if (this.colour) {
			if (!isSolid) {
				//colour = colour & 0xCBCBCBCB;
			}
			RenderUtil.setOpenGLColour(colour);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.5F, 0.0F);
		RenderUtil.drawTextureIn3D(iIcon, this.colour ? colour : 0xFFFFFFFF);
		GlStateManager.translate(-0.5F, -0.5F, 0.0F);
		GlStateManager.popMatrix();
	}

}