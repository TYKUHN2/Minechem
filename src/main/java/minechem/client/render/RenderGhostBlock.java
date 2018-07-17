package minechem.client.render;

import minechem.tileentity.multiblock.ghostblock.GhostBlockTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author p455w0rd
 *
 */
@SideOnly(Side.CLIENT)
public class RenderGhostBlock extends TileEntitySpecialRenderer<GhostBlockTileEntity> {

	@Override
	public void render(GhostBlockTileEntity te, double x, double y, double z, float scale, int i, float alpha) {
		World world = Minecraft.getMinecraft().world;
		IBlockState state = te.getRenderedBlockState();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		//GlStateManager.enableAlpha();
		//GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_DST_COLOR);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		//GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (!vertexbuffer.isDrawing) {
			//vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		}
		//Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(state, te.getPos(), te.getWorld(), vertexbuffer);
		GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
		//bindTexture(new ResourceLocation(ModGlobals.ID, "textures/blocks/ghost_fusion_wall.png"));
		GlStateManager.pushMatrix();

		ItemStack stack = te.getBlockAsItemStack();
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);

		removeStandartTranslationFromTESRMatrix();
		if (vertexbuffer.isDrawing) {
			//tessellator.draw();
		}
		GlStateManager.popMatrix();
		GlStateManager.translate(-(x + 0.5D), -(y + 0.5D), -(z + 0.5D));
		GlStateManager.disableBlend();
	}

	public static void removeStandartTranslationFromTESRMatrix() {
		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
		if (rView == null) {
			rView = Minecraft.getMinecraft().player;
		}
		Entity entity = rView;
		double tx = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
		double ty = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
		double tz = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
		GlStateManager.translate(-tx, -ty, -tz);
	}

}
