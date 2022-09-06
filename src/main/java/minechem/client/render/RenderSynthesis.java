package minechem.client.render;

import org.lwjgl.opengl.GL11;

import minechem.block.tile.TileSynthesis;
import minechem.client.model.ModelSynthesis;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RenderSynthesis extends TileEntitySpecialRenderer<TileSynthesis> {// implements IItemRenderer {

	ModelSynthesis model;

	public RenderSynthesis() {
		model = new ModelSynthesis();
	}

	@Override
	public void render(final TileSynthesis tileEntity, final double x, final double y, final double z, final float scale, final int i, final float alpha) {
		final World world = Minecraft.getMinecraft().world;
		final IBlockState state = world.getBlockState(tileEntity.getPos());
		final int facing = state.getBlock().getMetaFromState(state);
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

		// Animate the machine if it has power and something to work on.
		//RecipeSynthesis currentRecipe = synthesis.getCurrentRecipe();

		//if (currentRecipe != null && !synthesis.canAffordRecipe(currentRecipe) && synthesis.hasEnoughPowerForCurrentRecipe()) {
		//	model.updateArm();
		//}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y + 1.5D, z + 0.5D);
		GlStateManager.rotate(180f, 0f, 0f, 1f);
		GlStateManager.rotate(j * 45.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		bindTexture(ModResources.Model.SYNTHESIS);
		model.render(0.0625F);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public static class ItemRenderSynthesis extends TileEntityItemStackRenderer {

		ModelSynthesis model;

		public ItemRenderSynthesis() {
			model = new ModelSynthesis();
		}

		@Override
		public void renderByItem(final ItemStack stack, final float partialTicks) {
			//RenderHelper.enableStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5D, 1.5D, 0.5D);
			GlStateManager.rotate(180f, 0f, 0f, 1f);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(ModResources.Model.SYNTHESIS);
			model.updateArm();
			model.render(0.0625F);
			GlStateManager.translate(-0.5D, -1.5D, -0.5D);
			GlStateManager.popMatrix();
		}
	}

}
