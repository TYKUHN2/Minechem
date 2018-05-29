package minechem.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import minechem.block.tile.TileLeadedChest;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLeadedChest extends TileEntitySpecialRenderer<TileLeadedChest> {

	private final ModelChest model;

	public RenderLeadedChest() {
		model = new ModelChest();
	}

	public void renderTileEntityLeadedChestAt(TileLeadedChest leadedChest, double xCoord, double yCoord, double zCoord, float partialTick) {

	}

	@Override
	public void render(TileLeadedChest tileentity, double xCoord, double yCoord, double zCoord, float partialTick, int ii, float alpha) {
		if (tileentity == null) {
			return;
		}
		/*
		int facing = 0;

		if (leadedChest.hasWorld()) {
			Block var10 = leadedChest.getBlockType();
			facing = leadedChest.getBlockMetadata();

			if (var10 != null && facing == 0) {
				facing = leadedChest.getBlockMetadata();
			}
		}
		else {
			facing = 0;
		}

		GlStateManager.pushMatrix();
		//GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float) xCoord, (float) yCoord + 1.0F, (float) zCoord + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		short var11 = 0;

		if (facing == 2) {
			var11 = 180;
		}

		if (facing == 3) {
			var11 = 0;
		}

		if (facing == 4) {
			var11 = 90;
		}

		if (facing == 5) {
			var11 = -90;
		}

		GlStateManager.rotate(var11, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		*/
		World world = Minecraft.getMinecraft().world;
		IBlockState state = world.getBlockState(tileentity.getPos());
		int facing = state.getBlock().getMetaFromState(state);

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		if (ii >= 0) {
			bindTexture(DESTROY_STAGES[ii]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		}
		if (ii < 0) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.translate((float) xCoord, (float) yCoord + 1.0F, (float) zCoord + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		int j = 0;

		if (facing == 2) {
			j = 180;
		}

		if (facing == 3) {
			j = 0;
		}

		if (facing == 4) {
			j = 90;
		}

		if (facing == 5) {
			j = -90;
		}
		GlStateManager.rotate(j, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		float f = tileentity.prevLidAngle + (tileentity.lidAngle - tileentity.prevLidAngle) * partialTick;
		f = 1.0F - f;
		f = 1.0F - f * f * f;
		model.chestLid.rotateAngleX = -(f * ((float) Math.PI / 2F));
		bindTexture(ModResources.Model.LEADED_CHEST);
		model.renderAll();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (ii >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
		//GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		//GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		//GlStateManager.popMatrix();
		//GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
		public void renderItem(ItemStack item, TransformType transforms) {
			RenderHelper.enableStandardItemLighting();
			TileEntityRendererDispatcher.instance.render(new TileLeadedChest(), 0.0D, 0.0D, 0.0D, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	
		@Override
		public IModelState getTransforms() {
			return TransformUtils.DEFAULT_BLOCK;
		}
		*/

	public static class ItemRenderLeadedChest extends TileEntityItemStackRenderer {

		@Override
		public void renderByItem(ItemStack stack, float partialTicks) {
			RenderHelper.enableStandardItemLighting();
			TileEntityRendererDispatcher.instance.render(new TileLeadedChest(), 0.0D, 0.0D, 0.0D, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}

	}
}
