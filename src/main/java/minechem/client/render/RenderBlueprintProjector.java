package minechem.client.render;

import minechem.block.tile.TileBlueprintProjector;
import minechem.client.model.ModelBlueprintProjector;
import minechem.init.ModConfig;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class RenderBlueprintProjector extends TileEntitySpecialRenderer<TileBlueprintProjector> {// implements IItemRenderer {

	ModelBlueprintProjector model;

	public RenderBlueprintProjector() {
		model = new ModelBlueprintProjector();
	}

	@Override
	public void render(TileBlueprintProjector tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		World world = Minecraft.getMinecraft().world;
		IBlockState state = world.getBlockState(tileEntity.getPos());
		int facing = state.getBlock().getMetaFromState(state);
		int j = 0;

		if (facing == 2) {
			j = 0;
		}

		if (facing == 3) {
			j = 180;
		}

		if (facing == 4) {
			j = -90;
		}

		if (facing == 5) {
			j = 90;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y + 1.5D, z + 0.5D);
		GlStateManager.rotate(180f, 0f, 0f, 1f);
		GlStateManager.rotate(j * 45.0F, 0.0F, 1.0F, 0.0F);
		if (tileEntity.hasBlueprint() && (!ModConfig.powerUseEnabled || (ModConfig.powerUseEnabled && tileEntity.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() > 0))) {
			bindTexture(ModResources.Model.PROJECTOR_ON);
		}
		else {

			bindTexture(ModResources.Model.PROJECTOR_OFF);
		}
		model.render(0.0625F);
		GlStateManager.popMatrix();
		/*
				if (tileEntity.hasBlueprint() && !tileEntity.isStructureComplete()) {

					Blueprint.FISSION_REACTOR.visualiseStateArray(tileEntity.getPos(), world, EnumFacing.values()[facing], tileEntity.getProjectionLayer());
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				}
				*/
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
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Model.PROJECTOR_ON);
			model.render(0.0625F);
			//GlStateManager.disableBlend();
			GlStateManager.translate(-0.5D, -1.5D, -0.5D);
			GlStateManager.popMatrix();
		}

		@Override
		public IModelState getTransforms() {
			return TransformUtils.DEFAULT_BLOCK;
		}
		*/

	public static class ItemRenderBlueprintProjector extends TileEntityItemStackRenderer {

		ModelBlueprintProjector model;

		public ItemRenderBlueprintProjector() {
			model = new ModelBlueprintProjector();
		}

		@Override
		public void renderByItem(ItemStack stack, float partialTicks) {
			RenderHelper.enableStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5D, 1.5D, 0.5D);
			GlStateManager.rotate(180f, 0f, 0f, 1f);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			//GlStateManager.rotate((facing * 90.0F), 0.0F, 1.0F, 0.0F);
			//GlStateManager.enableBlend();
			//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Model.PROJECTOR_ON);
			//GlStateManager.disableDepth();
			model.render(0.0625F);
			//GlStateManager.disableBlend();
			GlStateManager.translate(-0.5D, -1.5D, -0.5D);
			GlStateManager.popMatrix();
		}
	}

}
