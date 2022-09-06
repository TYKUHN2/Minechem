package minechem.client.gui.renderitem;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import minechem.client.gui.GuiMicroscope;
import minechem.container.ContainerMicroscope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RenderItemMicroscope extends RenderItem {

	public ContainerMicroscope microscopeContainer;
	public GuiMicroscope microscopeGui;

	public RenderItemMicroscope() {
		super(Minecraft.getMinecraft().getTextureManager(), Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager(), Minecraft.getMinecraft().getItemColors());
	}

	public void setGui(GuiMicroscope gui) {
		microscopeGui = gui;
		microscopeContainer = (ContainerMicroscope) microscopeGui.inventorySlots;
	}

	private void setScissor(float x, float y, float w, float h) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution scaledRes = new ScaledResolution(mc);
		int scale = scaledRes.getScaleFactor();
		x *= scale;
		y *= scale;
		w *= scale;
		h *= scale;
		float guiScaledWidth = (microscopeGui.getGuiWidth() * scale);
		float guiScaledHeight = (microscopeGui.getGuiHeight() * scale);
		float guiLeft = ((float)(mc.displayWidth / 2) - guiScaledWidth / 2);
		float guiTop = ((float)(mc.displayHeight / 2) + guiScaledHeight / 2);
		int scissorX = Math.round((guiLeft + x));
		int scissorY = Math.round(((guiTop - h) - y));
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(scissorX, scissorY, (int) w, (int) h);
	}

	private void stopScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	@Override
	public void renderItemAndEffectIntoGUI(@Nullable EntityLivingBase entity, final ItemStack stack, int x, int y) {
		if (stack.isEmpty() || microscopeGui == null) {
			return;
		}
		//GlStateManager.scale(16.0F, 16.0F, 1.0F);
		//CCRenderItem.getOverridenRenderItem().renderItemAndEffectIntoGUI(entity, stack, x, y);
		//super.renderItemAndEffectIntoGUI(entity, stack, x, y);
	}

	@Override
	public void renderItemAndEffectIntoGUI(ItemStack itemStack, int x, int y) {
		if (itemStack.isEmpty()) {
			return;
		}
		//RenderHelper.enableStandardItemLighting();

		Slot slot = microscopeGui.inventorySlots.getSlot(0);
		if (slot.getStack() != null) {
			GL11.glPushMatrix();
			setScissor(microscopeGui.getEyePieceX(), microscopeGui.getEyePieceY(), 52, 52);
			int renderX = slot.xPos;
			int renderY = slot.yPos - 2;
			GL11.glTranslatef(renderX, renderY, 0.0F);
			GL11.glScalef(3.0F, 3.0F, 1.0F);
			GL11.glTranslatef(-renderX - 5.4F, -renderY - 4.5F, 540.0F);

			super.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, slot.getStack(), renderX, renderY);

			stopScissor();
			GL11.glPopMatrix();
		}

		if (itemStack == Minecraft.getMinecraft().player.inventory.getItemStack()) {
			GL11.glPushMatrix();
			setScissor(microscopeGui.getEyePieceX(), microscopeGui.getEyePieceY(), 52, 52);
			GL11.glTranslatef(x, y, 0.0F);
			GL11.glScalef(3.0F, 3.0F, 1.0F);
			GL11.glTranslatef(-x - 8.0F, -y - 8.0F, 540.0F);
			super.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, itemStack, x, y);
			stopScissor();
			GL11.glPopMatrix();
		}
	}
}
