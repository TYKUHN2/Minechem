package minechem.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import minechem.api.IMinechemBlueprint;
import minechem.api.client.gui.widget.IGuiMaterialListSlot;
import minechem.block.tile.TileBlueprintProjector;
import minechem.block.tile.TileMinechemEnergyBase;
import minechem.client.gui.widget.GuiImgButton;
import minechem.client.gui.widget.GuiMaterialListSlot;
import minechem.client.gui.widget.tab.TabBlueprintProjectorState;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerBlueprintProjector;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.BlueprintUtil;
import minechem.utils.MinechemUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class GuiBlueprintProjector extends GuiContainerTabbed {

	TileBlueprintProjector projector;
	GuiImgButton playButton;
	List<IGuiMaterialListSlot> materialList;
	int[] lastClick;
	int[] lastDrag;
	float rotX = 0;
	float rotY = 0;
	int mouseX, mouseY = 0;

	public GuiBlueprintProjector(InventoryPlayer inventoryPlayer, TileBlueprintProjector projector) {
		super(new ContainerBlueprintProjector(inventoryPlayer, projector));
		this.projector = projector;
		addTab(new TabBlueprintProjectorState(this, projector));
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.projector")));
		//addTab(new GuiTabPatreon(this));
		rotX = 25;
		rotY = -45;
		ySize = 204;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui() {
		super.initGui();
		getMaterialList();
		//playButton = new GuiImgButton(this, 0, guiLeft + 100, guiTop + 100, 0, 400, ModResources.Gui.PROJECTOR, 0, 400, 5, 9);
		buttonList.clear();
		//buttonList.add(playButton);
	}

	public TileBlueprintProjector getProjector() {
		return projector;
	}

	public float getZLevel() {
		return zLevel;
	}

	public void setZLevel(float level) {
		zLevel = level;
	}

	public RenderItem getItemRenderer() {
		return itemRender;
	}

	public float getItemRendererZLevel() {
		return getItemRenderer().zLevel;
	}

	public void setItemRenderZLevel(float level) {
		getItemRenderer().zLevel = level;
	}

	protected List<IGuiMaterialListSlot> getMaterialList() {
		if (getProjector().hasBlueprint()) {
			if (materialList == null || materialList.isEmpty()) {
				materialList = new ArrayList<IGuiMaterialListSlot>();
				IBlockState[] states = getProjector().getBlueprint().getMaterials().keySet().toArray(new IBlockState[getProjector().getBlueprint().getMaterials().keySet().size()]);
				int yOffset = 0;
				for (int i = 0; i < states.length; i++) {
					ItemStack stack = states[i].getBlock().getPickBlock(states[i], null, mc.world, new BlockPos(0, 0, 0), mc.player);
					if (!stack.isEmpty()) {
						Integer count = getProjector().getBlueprint().getMaterials().get(states[i]);
						materialList.add(new GuiMaterialListSlot(this, i, 7, guiTop + yOffset, Pair.of(stack, count), Lists.newArrayList(stack.getDisplayName())));
						yOffset += 10;
					}
				}
			}
		}
		else {
			materialList = new ArrayList<IGuiMaterialListSlot>();
		}
		return materialList;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String info = MinechemUtil.getLocalString("block.blueprint_projector.name");
		int infoWidth = fontRenderer.getStringWidth(info);
		fontRenderer.drawString(info, (xSize - infoWidth) / 2, 5, 0x000000);
		if (getMaterialList() != null && !getMaterialList().isEmpty()) {
			IMinechemBlueprint bp = getProjector().getBlueprint();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5, 0.5, 0.5);
			String bpName = bp.getDescriptiveName();
			if (bpName.length() > 15) {
				bpName = bpName.substring(0, 15) + "...";
			}
			mc.fontRenderer.drawString(bpName, 27, 112, 0x000000);
			mc.fontRenderer.drawString(MinechemUtil.getLocalString("gui.blueprintprojector.material_list"), 34, 124, 0x000000);
			GlStateManager.popMatrix();
			int xx = 0;
			int yy = 0;
			for (IGuiMaterialListSlot slot : getMaterialList()) {
				final int startX = (xx * (50) + 5) * 2;
				final int startY = yy * 10 + 18;
				slot.draw(startX, startY, x, y);
				if (slot.isMouseOver(mouseX, mouseY)) {
					renderTooltip(slot, mouseX, mouseY);
				}
				yy++;
			}
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

	public void renderTooltip(IGuiMaterialListSlot slot, int x, int y) {
		List<String> textLines = slot.getTooltip();
		if (mc.player.inventory.getItemStack().isEmpty() && !textLines.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int i = 0;

			for (String s : textLines) {
				int j = mc.fontRenderer.getStringWidth(s);

				if (j > i) {
					i = j;
				}
			}

			int l1 = x - xSize - 50;
			int i2 = y - ySize / 2;
			int k = 8;

			if (textLines.size() > 1) {
				k += 2 + (textLines.size() - 1) * 10;
			}

			if (l1 + i > xSize) {
				l1 -= 28 + i;
			}

			if (i2 + k + 6 > ySize) {
				i2 = ySize - k;
			}
			i += 13;

			//k += 20;
			//i2 += 50;
			l1 = x - guiLeft;
			i2 = y - guiTop - 20;
			setZLevel(300.0F);
			setItemRenderZLevel(300.0F);
			drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -0x9db8f6, -267386864);
			drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
			drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
			drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
			drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
			drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
			drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
			drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
			drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

			for (int k1 = 0; k1 < textLines.size(); ++k1) {
				String s1 = textLines.get(k1);
				mc.fontRenderer.drawStringWithShadow(s1, l1 + 10, i2, -1);

				if (k1 == 0) {
					i2 += 2;
				}

				i2 += 10;
			}

			setItemRenderZLevel(0.0F);
			setZLevel(0.0F);

			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableRescaleNormal();

			GlStateManager.scale(2.0F, 2.0F, 2.0F);
			itemRender.renderItemAndEffectIntoGUI(slot.get().getLeft(), l1 / 2 - 12, i2 / 2 - 12);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.PROJECTOR);

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0.0F);
		ItemStack blueprintStack = projector.getStackInSlot(0);
		if (!blueprintStack.isEmpty()) {
			drawBlueprintInfo(blueprintStack);
		}
		GlStateManager.popMatrix();
		if (projector.hasBlueprint()) {
			int stored = ((TileBlueprintProjector) mc.world.getTileEntity(getProjector().getPos())).getEnergyStored();
			int required = ((TileMinechemEnergyBase) mc.world.getTileEntity(getProjector().getPos())).getEnergyRequired();
			if (stored > required) {
				BlueprintUtil.renderStructureOnScreen(projector.getBlueprint(), x + 53, y + 5, var2, var3, rotX, rotY, projector.getBlueprint().getRenderScale());
			}
		}
	}

	public int guiLeft() {
		return guiLeft;
	}

	public int guiTop() {
		return guiTop;
	}

	public void renderToolTip(int x, int y) {
		if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null && hoveredSlot.getHasStack()) {
			renderToolTip(hoveredSlot.getStack(), x, y);
		}
	}

	private void drawBlueprintInfo(ItemStack blueprintStack) {
		IMinechemBlueprint blueprint = BlueprintUtil.getBlueprint(blueprintStack);
		if (blueprint == null) {
			return;
		}
		/*
		String name = blueprintStack.getDisplayName().replace("Blueprint", "");
		fontRenderer.drawStringWithShadow(name, 64, 12, 0xFFFFFF);
		WeakHashMap<Integer, Integer> blockCount = getBlockCount(blueprint);
		Map<Integer, BlueprintBlock> blockLookup = blueprint.getBlockLookup();
		int y = 23;
		Iterator<Entry<Integer, Integer>> it = blockCount.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> entry = it.next();
			BlueprintBlock block = blockLookup.get(entry.getKey());
			if (block != null) {
				ItemStack itemstack = new ItemStack(block.block, 1, block.metadata);
				String info = String.format("%dx%s", entry.getValue(), itemstack.getDisplayName());
				fontRenderer.drawString(info, 64, y, 0xDDDDDD);
				y += 10;
			}
		}
		*/

	}

	private void mouseDragged(int x, int y, int clickX, int clickY, int mx, int my, int lastX, int lastY, int button) {
		if ((clickX > guiLeft + 62 && clickX < guiLeft + 165 && mx >= 0 && mx < 1000) && (clickY >= guiTop + 8 && clickY < guiTop + 80 && my >= 0 && my < 1000)) {
			int dx = mx - lastX;
			int dy = my - lastY;
			rotY = rotY + (dx / 104f) * 80;
			rotX = rotX + (dy / 100f) * 80;
		}
	}

	@Override
	protected void mouseClickMove(int mx, int my, int button, long time) {
		if (lastClick != null) {
			if (lastDrag == null) {
				lastDrag = new int[] {
						mx - guiLeft, my - guiTop
				};
			}
			mouseDragged(guiLeft + 32, guiTop + 28, lastClick[0], lastClick[1], mx - guiLeft, my - guiTop, lastDrag[0], lastDrag[1], button);
			lastDrag = new int[] {
					mx - guiLeft, my - guiTop
			};
		}
	}

	@Override
	protected void mouseReleased(int mx, int my, int action) {
		super.mouseReleased(mx, my, action);
		lastClick = null;
		lastDrag = null;
	}

	@Override
	public void mouseClicked(int mx, int my, int button) throws IOException {
		super.mouseClicked(mx, my, button);
		lastClick = new int[] {
				mx, my
		};
	}
}
