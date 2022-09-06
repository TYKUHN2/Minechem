package minechem.client.render;

import java.util.List;

import minechem.block.multiblock.tile.TileGhostBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author p455w0rd
 *
 */
@SideOnly(Side.CLIENT)
public class RenderGhostBlock extends TileEntitySpecialRenderer<TileGhostBlock> {

	@Override
	public void render(TileGhostBlock te, double x, double y, double z, float scale, int i, float alpha) {
		Minecraft mc = Minecraft.getMinecraft();
		ItemModelMesher mesher = mc.getRenderItem().getItemModelMesher();
		ItemStack stack = te.getBlockAsItemStack();
		if (stack.isEmpty()) {
			return;
		}
		IBakedModel bakedmodel = mesher.getItemModel(stack);
		bakedmodel = bakedmodel.getOverrides().handleItemState(bakedmodel, stack, null, null);
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

		float pbx = OpenGlHelper.lastBrightnessX;
		float pby = OpenGlHelper.lastBrightnessY;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.65F);
		//GlStateManager.disableLighting();
		//GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.pushMatrix();
		bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, TransformType.NONE, false);
		GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		if (bakedmodel.isBuiltInRenderer()) {
			GlStateManager.enableRescaleNormal();
			stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
		}
		else {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
			for (EnumFacing enumfacing : EnumFacing.values()) {
				renderQuads(bufferbuilder, bakedmodel.getQuads(null, enumfacing, 0L), -1, stack);
			}
			renderQuads(bufferbuilder, bakedmodel.getQuads(null, null, 0L), -1, stack);
			tessellator.draw();

			if (stack.hasEffect()) {
				//renderEffect(bakedmodel);
			}
		}

		removeStandartTranslationFromTESRMatrix();
		GlStateManager.translate(-(x + 0.5D), -(y + 0.5D), -(z + 0.5D));
		GlStateManager.cullFace(GlStateManager.CullFace.BACK);
		GlStateManager.popMatrix();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, pbx, pby);
		GlStateManager.disableRescaleNormal();
		//GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}

	private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
		boolean flag = color == -1 && !stack.isEmpty();
		int i = 0;

		for (int j = quads.size(); i < j; ++i) {
			BakedQuad bakedquad = quads.get(i);
			int k = color;

			if (flag && bakedquad.hasTintIndex()) {
				k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());

				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}

				k = k | -16777216;
			}

			LightUtil.renderQuadColor(renderer, bakedquad, k);
		}
	}

	public static void removeStandartTranslationFromTESRMatrix() {
		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
		if (rView == null) {
			rView = Minecraft.getMinecraft().player;
		}
		Entity entity = rView;
		double tx = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
		double ty = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
		double tz = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
		GlStateManager.translate(-tx, -ty, -tz);
	}

}
