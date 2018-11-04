package minechem.client.render;

import java.util.Map;

import com.google.common.collect.Maps;

import minechem.block.tile.TileDecomposer;
import minechem.block.tile.TileDecomposer.State;
import minechem.client.model.ModelDecomposer;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class RenderDecomposer extends TileEntitySpecialRenderer<TileDecomposer> {

	private static final Map<BlockPos, ModelDecomposer> MODELREGISTRY = Maps.newHashMap();

	ModelDecomposer model;

	public RenderDecomposer() {
		model = new ModelDecomposer();
	}

	@Override
	public void render(TileDecomposer tileEntity, double x, double y, double z, float var8, int var9, float alpha) {
		TileDecomposer decomposer = tileEntity;
		model = getModelForTile(decomposer.getPos());
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y + 1.5D, z + 0.5D);
		GlStateManager.rotate(180f, 0f, 0f, 1f);
		boolean isRunning = decomposer.isCooking() && decomposer.getState() != State.jammed;
		bindTexture(isRunning ? ModResources.Model.DECOMPOSER_ON : ModResources.Model.DECOMPOSER_OFF);
		if (isRunning) {
			model.updateWindillRotation();
		}
		model.render(0.0625F);
		GlStateManager.popMatrix();
	}

	public ModelDecomposer getModelForTile(BlockPos pos) {
		if (!MODELREGISTRY.containsKey(pos)) {
			MODELREGISTRY.put(pos, new ModelDecomposer());
		}
		return MODELREGISTRY.get(pos);
	}

	public static class ItemRenderDecomposer extends TileEntityItemStackRenderer {

		ModelDecomposer model;

		public ItemRenderDecomposer() {
			model = new ModelDecomposer();
		}

		@Override
		public void renderByItem(ItemStack stack, float partialTicks) {
			RenderHelper.enableStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5D, 1.5D, 0.5D);
			GlStateManager.rotate(180f, 0f, 0f, 1f);
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Model.DECOMPOSER_ON);
			model.updateWindillRotation();
			model.render(0.0625F);
			GlStateManager.translate(-0.5D, -1.5D, -0.5D);
			GlStateManager.popMatrix();
			RenderHelper.disableStandardItemLighting();
		}

	}
}
