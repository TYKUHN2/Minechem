package minechem.client.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import minechem.api.IMinechemBlueprint;
import minechem.api.client.gui.widget.IGuiMaterialListSlot;
import minechem.api.client.gui.widget.IGuiMaterialListView;
import minechem.client.gui.GuiBlueprintProjector;
import minechem.utils.MinechemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

/**
 * @author p455w0rd
 *
 */
public class GuiMaterialListView implements IGuiMaterialListView {

	final List<IGuiMaterialListSlot> materialList;
	final GuiBlueprintProjector gui;
	final int x, y, width, height, listItemHeight;

	public GuiMaterialListView(GuiBlueprintProjector gui, int x, int y, int width, int height, int listItemHeight, IGuiMaterialListSlot... guiMaterialListSlots) {
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.listItemHeight = listItemHeight;
		materialList = new ArrayList<>();
		Arrays.stream(guiMaterialListSlots).forEachOrdered(guiMaterialListSlot -> materialList.add(guiMaterialListSlot));
	}

	@Override
	public GuiBlueprintProjector getGui() {
		return gui;
	}

	@Override
	public List<IGuiMaterialListSlot> getMaterialList() {
		return materialList;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getEntryHeight() {
		return listItemHeight;
	}

	@Override
	public void draw() {
		int listYOffset = getY() + 5;
		if (getGui().getProjector().hasBlueprint()) {
			IMinechemBlueprint bp = getGui().getProjector().getBlueprint();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5, 0.5, 0.5);
			String bpName = bp.getDescriptiveName();
			if (bpName.length() > 14) {
				bpName = bpName.substring(0, 15) + "...";
			}
			getGui().mc.fontRenderer.drawString(bpName, getX() + 11, getY() * 2, 0x000000);
			getGui().mc.fontRenderer.drawString(MinechemUtil.getLocalString("gui.blueprintprojector.material_list"), getX() + 16, (5 + getY()) * 2, 0x000000);
			GlStateManager.popMatrix();
			for (IGuiMaterialListSlot materialItem : getMaterialList()) {
				materialItem.draw(getX(), listYOffset);
				listYOffset += 10;
			}
		}
	}

	public static class GuiMaterialListSlot implements IGuiMaterialListSlot {

		final ItemStack stack;
		final int count, index;
		final List<String> tooltip;
		final GuiBlueprintProjector gui;

		public GuiMaterialListSlot(GuiBlueprintProjector gui, int index, Pair<ItemStack, Integer> listItem, List<String> tooltip) {
			this.gui = gui;
			this.index = index;
			stack = listItem.getLeft();
			count = listItem.getRight();
			this.tooltip = tooltip;
		}

		@Override
		public GuiBlueprintProjector getGui() {
			return gui;
		}

		@Override
		public Pair<ItemStack, Integer> get() {
			return Pair.of(stack, count);
		}

		@Override
		public List<String> getTooltip() {
			return tooltip;
		}

		@Override
		public void draw(int x, int y) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6, 0.6, 0.6);
			int round = y / 10;
			drawItem(x, (y - round) * 2, get().getLeft());

			GlStateManager.popMatrix();

			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5, 0.5, 0.5);
			Minecraft.getMinecraft().fontRenderer.drawString("x " + get().getRight(), x + 25, (10 + y) * 2, 0x000000);
			GlStateManager.popMatrix();
		}

		protected void drawItem(final int x, final int y, final ItemStack is) {
			getGui().setZLevel(100.0F);
			getGui().setItemRenderZLevel(100.0F);
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableDepth();
			getGui().getItemRenderer().renderItemAndEffectIntoGUI(is, x, y);
			GlStateManager.disableDepth();
			getGui().setItemRenderZLevel(0.0F);
			getGui().setZLevel(0.0F);
		}

		@Override
		public int getIndex() {
			return index;
		}

	}

}
