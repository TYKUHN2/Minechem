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

		// When the decomposer is powered we will change the texture to reflect this.
		/*
		if (decomposer.state != State.active) {
			bindTexture(ModResources.Model.DECOMPOSER_ON);
		}
		else if (decomposer.state == State.active) {
			// Makes the machine spin and look active while it is actually decomposing items in the input slot.
			bindTexture(ModResources.Model.DECOMPOSER_ON);
			model.updateWindillRotation();
		}
		else {
			// If we somehow enter another weird state just turn off.
			bindTexture(ModResources.Model.DECOMPOSER_OFF);
		}
		*/
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
	/*
		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			return new ArrayList<BakedQuad>();
		}
	
		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}
	
		@Override
		public boolean isGui3d() {
			return false;
		}
	
		@Override
		public boolean isBuiltInRenderer() {
			return true;
		}
	
		@Override
		public TextureAtlasSprite getParticleTexture() {
			return null;
		}
	
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}
	
		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}
	
		@Override
		public void renderItem(ItemStack stack, TransformType transformType) {
			RenderHelper.enableStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5D, 1.5D, 0.5D);
			GlStateManager.rotate(180f, 0f, 0f, 1f);
			//GlStateManager.rotate((facing * 90.0F), 0.0F, 1.0F, 0.0F);
			//GlStateManager.enableBlend();
			//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Model.DECOMPOSER_ON);
			model.updateWindillRotation();
			
			boolean isInMicroscope = false;
			if (Minecraft.getMinecraft().currentScreen instanceof GuiMicroscope) {
				GuiMicroscope gui = ((GuiMicroscope) Minecraft.getMinecraft().currentScreen);
				if (gui.isMouseInMicroscope() && stack == Minecraft.getMinecraft().player.inventory.getItemStack()) {
					isInMicroscope = true;
				}
			}
			if (isInMicroscope) {
				GuiMicroscope gui = ((GuiMicroscope) Minecraft.getMinecraft().currentScreen);
				Minecraft mc = Minecraft.getMinecraft();
				ScaledResolution scaledRes = new ScaledResolution(mc);
				int scale = scaledRes.getScaleFactor();
				int x = gui.getEyePieceX();
				int y = gui.getEyePieceY();
				int w = 52;
				int h = 52;
				x *= scale;
				y *= scale;
				w *= scale;
				h *= scale;
				float guiScaledWidth = (gui.getGuiWidth() * scale);
				float guiScaledHeight = (gui.getGuiHeight() * scale);
				float guiLeft = ((mc.displayWidth / 2) - guiScaledWidth / 2);
				float guiTop = ((mc.displayHeight / 2) + guiScaledHeight / 2);
				int scissorX = Math.round((guiLeft + x));
				int scissorY = Math.round(((guiTop - h) - y));
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				GL11.glScissor(scissorX, scissorY, w, h);
	
				GlStateManager.scale(3.0F, 3.0F, 3.0F);
			}
			
			model.render(0.0625F);
			GlStateManager.translate(-0.5D, -1.5D, -0.5D);
			GlStateManager.popMatrix();
		}
	
		@Override
		public IModelState getTransforms() {
			return TransformUtils.DEFAULT_BLOCK;
		}
	*/

	public static class ItemRenderDecomposer extends TileEntityItemStackRenderer {

		ModelDecomposer model;

		public ItemRenderDecomposer() {
			model = new ModelDecomposer();
		}

		@Override
		public void renderByItem(ItemStack stack, float partialTicks) {
			/*
			RenderHelper.enableStandardItemLighting();
			TileEntityRendererDispatcher.instance.render(new TileLeadedChest(), 0.0D, 0.0D, 0.0D, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			*/
			RenderHelper.enableStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5D, 1.5D, 0.5D);
			GlStateManager.rotate(180f, 0f, 0f, 1f);
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Model.DECOMPOSER_ON);
			model.updateWindillRotation();
			model.render(0.0625F);
			GlStateManager.translate(-0.5D, -1.5D, -0.5D);
			GlStateManager.popMatrix();
		}

	}
}
