package minechem.client.render;

import minechem.block.tile.TileMicroscope;
import minechem.client.model.ModelMicroscope;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RenderMicroscope extends TileEntitySpecialRenderer<TileMicroscope> {// implements IItemRenderer {

	ModelMicroscope microscopeModel;

	public RenderMicroscope() {
		microscopeModel = new ModelMicroscope();
	}

	@Override
	public void render(final TileMicroscope tileEntity, final double x, final double y, final double z, final float var8, final int var9, final float alpha) {
		final World world = Minecraft.getMinecraft().world;
		final IBlockState state = world.getBlockState(tileEntity.getPos());
		final int facing = state.getBlock().getMetaFromState(state);
		int j = 0;

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
		bindTexture(ModResources.Model.MICROSCOPE);
		microscopeModel.render(0.0625F);
		GlStateManager.popMatrix();
	}

	public static class ItemRenderMicroscope extends TileEntityItemStackRenderer {

		ModelMicroscope model;

		public ItemRenderMicroscope() {
			model = new ModelMicroscope();
		}

		@Override
		public void renderByItem(final ItemStack stack, final float partialTicks) {
			//RenderHelper.enableStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5D, 1.5D, 0.5D);
			GlStateManager.rotate(180f, 0f, 0f, 1f);
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Model.MICROSCOPE);
			model.render(0.0625F);
			GlStateManager.translate(-0.5D, -1.5D, -0.5D);
			GlStateManager.popMatrix();
			//RenderHelper.disableStandardItemLighting();
		}

	}

}
