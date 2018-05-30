package minechem.client.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.lwjgl.opengl.GL11;

import minechem.client.model.generated.CharacterSprite;
import minechem.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.common.model.TRSRTransformation;

public class TextLayer implements ILayer {

	private static final Map<String, List<BakedQuad>> STRING_QUAD_CACHE = new HashMap<>();
	//private static final Map<String, IBakedModel> BAKED_MODEL_CACHE = new HashMap<>();
	private final String text;

	public TextLayer(String text) {
		this.text = text;
	}

	@Override
	public void render(int colour) {
		//PerspectiveAwareBakedModel model = (PerspectiveAwareBakedModel) getModelForString(text);
		//model.handlePerspective(PerspectiveAwareBakedModel.cameraTransformType);
		float scale = 0.30F;
		//GlStateManager.enableAlpha();
		//GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
		//if (Minecraft.isAmbientOcclusionEnabled()) {
		//GL11.glShadeModel(GL11.GL_SMOOTH);
		//}
		//else {
		GL11.glShadeModel(GL11.GL_FLAT);
		//}

		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, 1.0F);
		GlStateManager.translate(0.0F, (scale / scale) + (scale / scale) + 0.250F, 0.0F);

		RenderUtil.renderQuads(getQuadsForString(text), 1.0F);
		//RenderUtil.render(model);

		//RenderUtil.render(model, 0xFFFFFFFF);
		GlStateManager.scale(-scale * scale, -scale * scale, -scale * scale);
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
		//GlStateManager.disableAlpha();
		//GlStateManager.disableBlend();
		//GlStateManager.disableLighting();
	}

	public static List<BakedQuad> getQuadsForString(String str) {
		//if (!STRING_QUAD_CACHE.containsKey(str)) {
		char[] chars = str.toCharArray();
		TextureAtlasSprite[] sprites = new TextureAtlasSprite[chars.length];
		for (int i = 0; i < chars.length; i++) {
			sprites[i] = CharacterSprite.getSpriteForChar(chars[i]);
		}
		STRING_QUAD_CACHE.put(str, RenderUtil.getQuadsForSprites(DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity()), sprites));
		//}
		return STRING_QUAD_CACHE.get(str);
	}
	/*
		private static IBakedModel getModelForString(String str) {
			for (String currentString : BAKED_MODEL_CACHE.keySet()) {
				if (currentString.equals(str)) {
					IBakedModel model = BAKED_MODEL_CACHE.get(currentString);
					if (model != null) {
						return model;
					}
				}
			}
			IBakedModel newModel = new PerspectiveAwareBakedModel(getQuadsForString(str), PerspectiveProperties.DEFAULT_ITEM) {
				@Override
				public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
					PerspectiveAwareBakedModel.cameraTransformType = cameraTransformType;
					if (properties instanceof PerspectiveProperties) {
						return PerspectiveMapWrapper.handlePerspective(this, ((PerspectiveProperties) properties).getModelState(), cameraTransformType);
					}
					return ForgeHooksClient.handlePerspective(this, cameraTransformType);
				}
			};
			BAKED_MODEL_CACHE.put(str, newModel);
			return newModel;
		}
	*/
}