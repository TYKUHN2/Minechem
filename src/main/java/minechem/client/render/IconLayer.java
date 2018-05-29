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
		//GlStateManager.scale(1F, 1F, 1F);
		RenderUtil.resetOpenGLColour();
		if (this.colour) {
			if (!isSolid) {
				//colour = colour & 0xCBCBCBCB;
			}
			RenderUtil.setOpenGLColour(colour);
		}
		GlStateManager.pushMatrix();
		//GlStateManager.scale(0.5F, 0.5F, 0.5F);
		//RenderHelper.enableGUIStandardItemLighting();
		//RenderHelper.disableStandardItemLighting();
		//GlStateManager.disableLighting();
		//GlStateManager.disableLight(0);
		//GlStateManager.disableLight(1);
		//GlStateManager.disableColorMaterial();
		GlStateManager.translate(0.5F, 0.5F, 0.0F);
		//if (!stack.isOnItemFrame()) {
		//	EntityPlayer player = Minecraft.getMinecraft().player;
		//	if (player.getHeldItemMainhand().getItem() != ModItems.element && player.getHeldItemOffhand().getItem() != ModItems.element) {
		//GlStateManager.rotate(180F, 0, 180F, 0);
		//	}
		//}
		RenderUtil.drawTextureIn3D(iIcon, this.colour ? colour : 0xFFFFFFFF);
		GlStateManager.translate(-0.5F, -0.5F, 0.0F);
		//RenderUtil.resetOpenGLColour();
		//RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}
}