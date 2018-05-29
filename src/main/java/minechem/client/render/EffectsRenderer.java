package minechem.client.render;

import minechem.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EffectsRenderer {

	public static void renderEffects() {
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.player;
		if (player != null && player.isPotionActive(ModPotions.atropineHigh)) {
			PotionEffect DHigh = player.getActivePotionEffect(ModPotions.atropineHigh);
			int Multiplier = DHigh.getAmplifier();
			renderDelirium(5 * Multiplier + 5);
		}
	}

	/* // this is all unused code for a WIP gas system private void renderOverlays(float parialTickTime) { Minecraft mc = FMLClientHandler.instance().getClient(); if (mc.renderViewEntity != null && mc.gameSettings.thirdPersonView == 0 &&
	 * !mc.renderViewEntity.isPlayerSleeping() && mc.player.isInsideOfMaterial(MinechemBlocks.materialGas)) { renderWarpedTextureOverlay(mc, new ResourceLocation(ModMinechem.ID,"/misc/water.png")); } }
	 *
	 *
	 * // Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound before being called. Used for the water // overlay. Args: parialTickTime
	 *
	 * private void renderWarpedTextureOverlay(Minecraft mc, ResourceLocation texture) { int overlayTexture = mc.renderEngine.func_110581_b(texture).func_110552_b(); double tile = 4.0F; double yaw = -mc.player.rotationYaw / 64.0F; double pitch =
	 * mc.player.rotationPitch / 64.0F; double left = 0; double top = 0; double right = mc.displayWidth; double bot = mc.displayHeight; double z = -1; Tessellator ts = Tessellator.instance;
	 *
	 * GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F); GlStateManager.disableAlpha(); GlStateManager.enableBlend(); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); GL11.glBindTexture(GL11.GL_TEXTURE_2D, overlayTexture); GlStateManager.pushMatrix();
	 *
	 * ts.startDrawingQuads(); ts.addVertexWithUV(left, bot, z, tile + yaw, tile + pitch); ts.addVertexWithUV(right, bot, z, yaw, tile + pitch); ts.addVertexWithUV(right, top, z, yaw, pitch); ts.addVertexWithUV(left, top, z, tile + yaw, pitch); ts.draw();
	 *
	 * GlStateManager.popMatrix(); GlStateManager.disableBlend(); GlStateManager.enableAlpha(); } */
	public static void renderDelirium(int markiplier) {
		ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
		int width = scale.getScaledWidth();
		int height = scale.getScaledHeight();
		//Gui gui = new Gui();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		int color = (int) (220.0F * markiplier - 150) << 24 | 1052704;
		Gui.drawRect(0, 0, width, height, color);
		GlStateManager.enableAlpha();
		GlStateManager.enableDepth();

	}
}
