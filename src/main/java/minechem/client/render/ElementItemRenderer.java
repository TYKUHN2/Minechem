package minechem.client.render;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import net.minecraft.client.renderer.RenderHelper;
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

	public static PerspectiveAwareBakedModel model;
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
			switch (element.roomState()) {
			case LIQUID:
				frame = (int) translateValue(t, 0, duration, 0, 7);
				break;
			case GAS:
				frame = (int) translateValue(t, 0, duration, 0, 7);
				break;
			default:
			}
			model = getModelForElement(MinechemUtil.getElement(stack), frame);
			if (model != null) {
				model.handlePerspective(transformType);
			}
			if (transformType == TransformType.GUI) {
				RenderHelper.disableStandardItemLighting();
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			//GlStateManager.pushMatrix();
			RenderUtil.render(getModelForElement(element, frame));//getModelForElement(element, frame));
			//GlStateManager.popMatrix();
			/*
			ElementEnum chemicalBase = MinechemUtil.getElement(stack);
			List<ILayer> layers = new LinkedList<ILayer>();
			float duration = 1500;
			float t = Minecraft.getSystemTime() % duration;
			int frame;

			switch (chemicalBase.roomState()) {
			case SOLID:
				layers.add(new IconLayer(Textures.Sprite.SOLID_STATE, true, stack));
				break;
			case LIQUID:
				frame = (int) translateValue(t, 0, duration, 0, 7);
				layers.add(new IconLayer(Textures.Sprite.LIQUID_STATES[frame], true, stack, false));
				break;
			case GAS:
				frame = (int) translateValue(t, 0, duration, 0, 7);
				layers.add(new IconLayer(Textures.Sprite.GAS_STATES[frame], true, stack, false));
				break;

			case PLASMA:
			layers.add(new IconLayer(item.tube, false));
			frame = (int) MathHelper.translateValue(t, 0, duration, 0, item.plasma.length);
			layers.add(new IconLayer(item.plasma[frame], true));
			break;

			}

			layers.add(new IconLayer(Textures.Sprite.FILLED_TUBE, true, stack));
			layers.add(new TextLayer(chemicalBase.name()));
			//GlStateManager.pushMatrix();
			GlStateManager.translate(1.0, 0, 1.0);
			GlStateManager.rotate(180.0f, 0, 1.0f, 0);
			//RenderHelper.disableStandardItemLighting();
			layers.get(1).render(0xFFFFFFFF);
			//GlStateManager.enableLighting();
			layers.get(0).render(RenderUtil.getColorForElement(chemicalBase));
			layers.get(1).render(0xFFFFFFFF);
			layers.get(2).render();
			//RenderHelper.enableStandardItemLighting();
			GlStateManager.translate(-1.0, 0, -1.0);
			//GlStateManager.popMatrix();
			if (transformType == TransformType.GUI) {
				RenderHelper.enableStandardItemLighting();
				*/
		}

		//}
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

	public static PerspectiveAwareBakedModel[] getModelsForElement(ElementEnum element) {
		if (!ELEMENT_MODEL_CACHE.containsKey(element.atomicNumber())) {
			PerspectiveAwareBakedModel[] newModels = new PerspectiveAwareBakedModel[1];
			ResourceLocation[] sprites = getSpritesForElement(element);
			boolean isSolid = true;
			if (element.roomState() != MatterState.SOLID) {
				isSolid = false;
			}
			if (isSolid) {
				List<BakedQuad> quads = new LinkedList<BakedQuad>();
				quads.addAll(ItemLayerModel.getQuadsForSprite(0, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(sprites[0].toString()), DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())));
				quads.addAll(ItemLayerModel.getQuadsForSprite(0, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Textures.Sprite.FILLED_TUBE.toString()), DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())));
				quads.addAll(TextLayer.getQuadsForString(element.name()));
				PerspectiveAwareBakedModel newModel = new PerspectiveAwareBakedModel(quads, Transforms.DEFAULT_ITEM, ModelProperties.DEFAULT_ITEM);
				newModels[0] = newModel;
			}
			else {
				newModels = new PerspectiveAwareBakedModel[7];
				for (int i = 0; i < 7; i++) {
					List<BakedQuad> quads = new LinkedList<BakedQuad>();
					quads.addAll(ItemLayerModel.getQuadsForSprite(0, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(sprites[i].toString()), DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())));
					quads.addAll(ItemLayerModel.getQuadsForSprite(0, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Textures.Sprite.FILLED_TUBE.toString()), DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())));
					quads.addAll(TextLayer.getQuadsForString(element.name()));
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

}
