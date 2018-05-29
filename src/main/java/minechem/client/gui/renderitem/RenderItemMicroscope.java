package minechem.client.gui.renderitem;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import minechem.client.gui.GuiMicroscope;
import minechem.container.ContainerMicroscope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RenderItemMicroscope extends RenderItem {

	public ContainerMicroscope microscopeContainer;
	public InventoryPlayer inventoryPlayer;
	public GuiMicroscope microscopeGui;

	public RenderItemMicroscope() {
		super(Minecraft.getMinecraft().getTextureManager(), Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager(), Minecraft.getMinecraft().getItemColors());
	}

	public void setGui(GuiMicroscope gui) {
		microscopeGui = gui;
		microscopeContainer = (ContainerMicroscope) microscopeGui.inventorySlots;
		inventoryPlayer = microscopeGui.inventoryPlayer;
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
		float guiLeft = ((mc.displayWidth / 2) - guiScaledWidth / 2);
		float guiTop = ((mc.displayHeight / 2) + guiScaledHeight / 2);
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
		renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, itemStack, x, y);
		//RenderHelper.enableGUIStandardItemLighting();
		//RenderHelper.enableStandardItemLighting();
		Slot slot = microscopeContainer.getSlot(0);
		boolean shouldEnlarge = (itemStack == inventoryPlayer.getItemStack() && microscopeGui.isMouseInMicroscope()) || !slot.getStack().isEmpty() && slot.getStack() == itemStack;
		boolean isHeld = itemStack == inventoryPlayer.getItemStack();
		//if (shouldEnlarge) {
		/*
		GlStateManager.pushMatrix();
		//GlStateManager.disableDepth();
		setScissor(microscopeGui.getEyePieceX(), microscopeGui.getEyePieceY(), 52, 52);
		int renderX = microscopeGui.getGuiLeft() + slot.xPos;
		int renderY = microscopeGui.getGuiTop() + slot.yPos;
		//GlStateManager.translate(renderX, renderY, 0.0F);
		//GlStateManager.scale(3.0F, 3.0F, 1.0F);
		//GlStateManager.translate(-renderX - 5.3F, -renderY - 5.3F, 2.0F);
		//super.renderItemAndEffectIntoGUI(slot.getStack(), renderX, renderY);
		IBakedModel model = getItemModelWithOverrides(itemStack, (World) null, Minecraft.getMinecraft().player);//getItemModelMesher().getItemModel(itemStack);
		//getItemModelWithOverrides(itemStack, (World) null, Minecraft.getMinecraft().player)
		//renderItemModelIntoGUI(slot.getStack(), renderX, renderY, model);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.translate(renderX, renderY, 100.0F + zLevel);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		//GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F * 3.0F, 16.0F * 3.0F, 16.0F);

		if (model.isGui3d()) {
			GlStateManager.enableLighting();
		}
		else {
			GlStateManager.disableLighting();
		}

		model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);
		this.renderItem(itemStack, model);
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		stopScissor();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		*/

		/*
		IBakedModel bakedmodel = getItemModelWithOverrides(itemStack, (World) null, (EntityLivingBase) null);
		int renderX = microscopeGui.getGuiLeft() + slot.xPos;
		int renderY = microscopeGui.getGuiTop() + slot.yPos;
		GlStateManager.pushMatrix();
		setScissor(microscopeGui.getEyePieceX(), microscopeGui.getEyePieceY(), 52, 52);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(isHeld ? x : renderX, isHeld ? y : renderY, 100.0F + zLevel);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		//GlStateManager.scale(16.0F, 16.0F, 16.0F);
		GlStateManager.scale(16.0F * 3.0F, 16.0F * 3.0F, 16.0F);
		if (bakedmodel.isGui3d()) {
			GlStateManager.enableLighting();
		}
		else {
			GlStateManager.disableLighting();
		}
		bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
		this.renderItem(itemStack, bakedmodel);
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		stopScissor();
		GlStateManager.popMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		*/
		//setScissor(microscopeGui.getEyePieceX(), microscopeGui.getEyePieceY(), 52, 52);
		//super.renderItemAndEffectIntoGUI(itemStack, x, y);
		//stopScissor();
		//}
		//else {
		//super.renderItemAndEffectIntoGUI(itemStack, x, y);
		//}
		/*
				if (itemStack == inventoryPlayer.getItemStack() && microscopeGui.isMouseInMicroscope()) {
					GlStateManager.pushMatrix();
					setScissor(microscopeGui.getEyePieceX(), microscopeGui.getEyePieceY(), 52, 52);
					int renderX = x - 9;
					int renderY = y - 5;
					//GlStateManager.translate(renderX, renderY, 0.0F);
					//GlStateManager.scale(3.0F, 3.0F, 1.0F);
					//GlStateManager.translate(-renderX - 5.3F, -renderY - 5.3F, 540F);
					//super.renderItemAndEffectIntoGUI(itemStack, renderX, renderY);
					//renderItemModelIntoGUI(itemStack, renderX, renderY, getItemModelWithOverrides(itemStack, (World) null, Minecraft.getMinecraft().player));
					//GlStateManager.scale(-3.0F, -3.0F, -1.0F);
					IBakedModel model = getItemModelWithOverrides(itemStack, (World) null, Minecraft.getMinecraft().player);//getItemModelMesher().getItemModel(itemStack);
					//getItemModelWithOverrides(itemStack, (World) null, Minecraft.getMinecraft().player)
					//renderItemModelIntoGUI(slot.getStack(), renderX, renderY, model);
					Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
					GlStateManager.enableRescaleNormal();
					GlStateManager.enableAlpha();
					GlStateManager.alphaFunc(516, 0.1F);
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
					GlStateManager.translate(renderX, renderY, 100.0F + zLevel);
					GlStateManager.translate(8.0F, 8.0F, 0.0F);
					//GlStateManager.scale(1.0F, -1.0F, 1.0F);
					GlStateManager.scale(16.0F * 3.0F, 16.0F * 3.0F, 16.0F);
		
					if (model.isGui3d()) {
						GlStateManager.enableLighting();
					}
					else {
						GlStateManager.disableLighting();
					}
		
					model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);
					this.renderItem(itemStack, model);
					GlStateManager.disableAlpha();
					GlStateManager.disableRescaleNormal();
					GlStateManager.disableLighting();
					stopScissor();
					GlStateManager.enableDepth();
					GlStateManager.popMatrix();
					GlStateManager.disableBlend();
					Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		
				}
				*/
	}
}
