package minechem.client.gui;

import java.io.IOException;

import minechem.api.IMinechemBlueprint;
import minechem.block.tile.TileBlueprintProjector;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerBlueprintProjector;
import minechem.init.ModGlobals.ModResources;
import minechem.utils.BlueprintUtil;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiBlueprintProjector extends GuiContainerTabbed {

	TileBlueprintProjector projector;

	int[] lastClick;
	int[] lastDrag;
	float rotX = 0;
	float rotY = 0;

	public GuiBlueprintProjector(InventoryPlayer inventoryPlayer, TileBlueprintProjector projector) {
		super(new ContainerBlueprintProjector(inventoryPlayer, projector));
		this.projector = projector;
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.projector")));
		//addTab(new GuiTabPatreon(this));
		rotX = 25;
		rotY = -45;
	}

	public TileBlueprintProjector getProjector() {
		return projector;
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
			BlueprintUtil.renderStructureOnScreen(projector.getBlueprint(), x + 53, y + 5, var2, var3, rotX, rotY, projector.getBlueprint().getRenderScale());
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
		if ((clickX > 187 && clickX < 291 && mx >= 0 && mx < 1000) && (clickY >= 47 && clickY < 130 && my >= 0 && my < 1000)) {
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
