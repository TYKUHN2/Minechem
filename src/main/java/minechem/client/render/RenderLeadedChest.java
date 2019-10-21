package minechem.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import minechem.block.tile.TileLeadedChest;
import minechem.init.ModGlobals.ModResources;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.*;
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

	public void renderTileEntityLeadedChestAt(final TileLeadedChest leadedChest, final double xCoord, final double yCoord, final double zCoord, final float partialTick) {

	}

	@Override
	public void render(final TileLeadedChest tileentity, final double xCoord, final double yCoord, final double zCoord, final float partialTick, final int ii, final float alpha) {
		if (tileentity == null) {
			return;
		}
		final World world = Minecraft.getMinecraft().world;
		final IBlockState state = world.getBlockState(tileentity.getPos());
		final int facing = state.getBlock().getMetaFromState(state);

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		//RenderHelper.enableGUIStandardItemLighting();
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

	}

	public static class ItemRenderLeadedChest extends TileEntityItemStackRenderer {

		@Override
		public void renderByItem(final ItemStack stack, final float partialTicks) {
			//RenderHelper.enableStandardItemLighting();
			TileEntityRendererDispatcher.instance.render(new TileLeadedChest(), 0.0D, 0.0D, 0.0D, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}

	}
}
