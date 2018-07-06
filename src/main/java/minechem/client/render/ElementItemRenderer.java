package minechem.client.render;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import minechem.client.model.generated.ItemLayerWrapper;
import minechem.client.model.generated.ModelProperties;
import minechem.client.model.generated.PerspectiveAwareBakedModel;
import minechem.client.model.generated.Transforms;
import minechem.init.ModGlobals.Textures;
import minechem.item.ItemElement;
import minechem.item.MatterState;
import minechem.item.element.ElementEnum;
import minechem.utils.MinechemUtil;
import minechem.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * @author p455w0rd
 *
 */
public class ElementItemRenderer extends TileEntityItemStackRenderer {

	public static ItemLayerWrapper model;
	public static TransformType transformType;
	public static Map<Integer, PerspectiveAwareBakedModel[]> ELEMENT_MODEL_CACHE = new HashMap<>();

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {
		if (transformType == null) {
			transformType = TransformType.NONE;
		}

		if (stack.getItem() instanceof ItemElement && stack.getItemDamage() > 0) {
			ElementEnum element = MinechemUtil.getElement(stack);
			float duration = 1500;
			float t = Minecraft.getSystemTime() % duration;
			int frame = 0;
			boolean isSolid = true;
			switch (element.roomState()) {
			case LIQUID:
				isSolid = false;
				frame = (int) translateValue(t, 0, duration, 0, 7);
				break;
			case GAS:
				isSolid = false;
				frame = (int) translateValue(t, 0, duration, 0, 7);
				break;
			default:
			}
			model.setInternal(getModelForElement(MinechemUtil.getElement(stack), frame));
			if (model != null) {
				model.handlePerspective(transformType);
			}
			if (transformType == TransformType.GUI) {
				//RenderHelper.enableStandardItemLighting();
				//RenderHelper.disableStandardItemLighting();
			}
			if (transformType == TransformType.FIXED) {
				GlStateManager.translate(1.0, 0, 1.0);
				GlStateManager.rotate(180.0f, 0, 1.0f, 0);
			}
			Map<LayerType, List<BakedQuad>> quadsLayered = getQuadsForElement(element, frame);
			int color = RenderUtil.getColorForElement(element);
			if (!isSolid) {
				color = color & 0xCBFFFFFF;
			}
			if (transformType == TransformType.THIRD_PERSON_LEFT_HAND || transformType == TransformType.THIRD_PERSON_RIGHT_HAND) {
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				GlStateManager.translate(0.2F, 0.9F, 0.6F);
			}
			if (transformType == TransformType.FIRST_PERSON_RIGHT_HAND) {
				GlStateManager.translate(0.5F, 0.0F, -0.6F);
			}
			if (transformType == TransformType.FIRST_PERSON_LEFT_HAND) {
				GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(-0.2F, -0.08F, -0.6F);
			}
			if (transformType == TransformType.FIRST_PERSON_RIGHT_HAND) {
				GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.2F, -0.08F, -0.6F);
			}
			if (transformType == TransformType.FIXED || transformType == TransformType.GUI) {
				GlStateManager.translate(0.1F, 0.0F, 0.0F);
			}

			RenderUtil.renderQuadsColored(quadsLayered.get(LayerType.ELEMENT), color, 0.0F);
			RenderUtil.renderQuadsColored(quadsLayered.get(LayerType.TUBE), 0xFFFFFFFF, 0.0F);
			GlStateManager.translate(-0.1F, 0.0F, 0.0F);
			GlStateManager.pushMatrix();
			float scale = 0.70F;
			GlStateManager.scale(scale, scale, 1.0F);
			GlStateManager.translate(0.0F, scale - 0.3, 0.0F);
			RenderUtil.renderQuadsColored(quadsLayered.get(LayerType.SYMBOL), 0xFFFFFFFF, 0.0F);
			//Minecraft.getMinecraft().getRenderItem().renderItem(stack, model.getInternal());
			GlStateManager.scale(-scale * scale, -scale * scale, -scale * scale);
			GlStateManager.popMatrix();
			if (transformType == TransformType.GUI) {
				//RenderHelper.enableStandardItemLighting();
			}
			if (transformType == TransformType.FIXED || transformType == TransformType.GUI) {
				GlStateManager.translate(-0.1F, 0.0F, 0.0F);
			}
			if (transformType == TransformType.THIRD_PERSON_LEFT_HAND || transformType == TransformType.THIRD_PERSON_RIGHT_HAND) {
				GlStateManager.scale(-0.5F, -0.5F, -0.5F);
			}
			if (transformType == TransformType.FIRST_PERSON_LEFT_HAND) {
				GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.2F, 0.08F, 0.6F);
			}
			if (transformType == TransformType.FIRST_PERSON_RIGHT_HAND) {
				GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(-0.2F, 0.08F, 0.6F);
			}
		}
	}

	private static ResourceLocation[] getSpritesForElement(ElementEnum element) {
		switch (element.roomState()) {
		case SOLID:
		default:
			return new ResourceLocation[] {
					Textures.Sprite.SOLID_STATE
			};
		case LIQUID:
			return Textures.Sprite.LIQUID_STATES;
		case GAS:
			return Textures.Sprite.GAS_STATES;
		}

	}

	public static PerspectiveAwareBakedModel getModelForElement(ElementEnum element, int frame) {
		return getModelsForElement(element)[frame];
	}

	public static List<BakedQuad> getAllQuadsForElement(ElementEnum element, int frame) {
		List<BakedQuad> quadList = new LinkedList<>();
		Map<LayerType, List<BakedQuad>> quadListMap = getQuadsForElement(element, frame);
		quadList.addAll(quadListMap.get(LayerType.ELEMENT));
		quadList.addAll(quadListMap.get(LayerType.TUBE));
		quadList.addAll(quadListMap.get(LayerType.SYMBOL));
		return quadList;
	}

	public static Map<LayerType, List<BakedQuad>> getQuadsForElement(ElementEnum element, int frame) {
		Map<LayerType, List<BakedQuad>> elementQuadsMap = new HashMap<>();
		List<BakedQuad> elementQuads = new LinkedList<BakedQuad>();
		List<BakedQuad> tubeQuads = new LinkedList<BakedQuad>();
		List<BakedQuad> symbolQuads = new LinkedList<BakedQuad>();
		ResourceLocation[] sprites = getSpritesForElement(element);
		elementQuads.addAll(ItemLayerModel.getQuadsForSprite(0, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(sprites[frame].toString()), DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())));
		tubeQuads.addAll(ItemLayerModel.getQuadsForSprite(0, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Textures.Sprite.FILLED_TUBE.toString()), DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())));
		symbolQuads.addAll(TextLayer.getQuadsForString(element.name()));
		elementQuadsMap.put(LayerType.ELEMENT, elementQuads);
		elementQuadsMap.put(LayerType.TUBE, tubeQuads);
		elementQuadsMap.put(LayerType.SYMBOL, symbolQuads);
		return elementQuadsMap;
	}

	public static PerspectiveAwareBakedModel[] getModelsForElement(ElementEnum element) {
		if (!ELEMENT_MODEL_CACHE.containsKey(element.atomicNumber())) {
			PerspectiveAwareBakedModel[] newModels = new PerspectiveAwareBakedModel[1];
			List<BakedQuad> quads = getAllQuadsForElement(element, 0);
			boolean isSolid = true;
			if (element.roomState() != MatterState.SOLID) {
				isSolid = false;
			}
			if (isSolid) {
				PerspectiveAwareBakedModel newModel = new PerspectiveAwareBakedModel(quads, Transforms.DEFAULT_ITEM, ModelProperties.DEFAULT_ITEM);
				newModels[0] = newModel;
			}
			else {
				newModels = new PerspectiveAwareBakedModel[7];
				for (int i = 0; i < 7; i++) {
					quads = getAllQuadsForElement(element, i);
					PerspectiveAwareBakedModel newModel = new PerspectiveAwareBakedModel(quads, Transforms.DEFAULT_ITEM, ModelProperties.DEFAULT_ITEM);
					newModels[i] = newModel;
				}
			}
			ELEMENT_MODEL_CACHE.put(element.atomicNumber(), newModels);
		}
		return ELEMENT_MODEL_CACHE.get(element.atomicNumber());
	}

	public static float translateValue(float value, float leftMin, float leftMax, float rightMin, float rightMax) {
		float leftRange = leftMax - leftMin;
		float rightRange = rightMax - rightMin;
		float valueScaled = (value - leftMin) / leftRange;
		return rightMin + (valueScaled * rightRange);
	}

	public static enum LayerType {
			TUBE, ELEMENT, SYMBOL;
	}

}
