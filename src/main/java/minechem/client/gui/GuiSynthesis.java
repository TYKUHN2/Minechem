package minechem.client.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import minechem.block.tile.TileSynthesis;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.client.gui.widget.tab.TabSynthesisState;
import minechem.container.ContainerSynthesis;
import minechem.init.ModGlobals.ModResources;
import minechem.init.ModNetworking;
import minechem.inventory.slot.SlotFake;
import minechem.network.message.MessageFakeSlotScroll;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class GuiSynthesis extends GuiContainerTabbed {

	int guiWidth = 176;
	int guiHeight = 242;

	public GuiSynthesis(InventoryPlayer inventoryPlayer, TileSynthesis synthesis) {
		super(new ContainerSynthesis(inventoryPlayer, synthesis));
		xSize = guiWidth;
		ySize = guiHeight;
		addTab(new TabSynthesisState(this, synthesis));
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.synthesis")));
		//addTab(new GuiTabPatreon(this));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String title = MinechemUtil.getLocalString("gui.title.synthesis");
		String storageHeader = MinechemUtil.getLocalString("gui.synthesis.input_storage");
		String invHeader = MinechemUtil.getLocalString("gui.title.inventory");
		String outputHeader = MinechemUtil.getLocalString("gui.synthesis.output");
		String invMakes = MinechemUtil.getLocalString("gui.synthesis.creates") + ":";
		int titleWidth = fontRenderer.getStringWidth(title);
		int makesWidth = fontRenderer.getStringWidth(invMakes);
		fontRenderer.drawString(title, (guiWidth - titleWidth) / 2, 5, 0x000000);
		fontRenderer.drawString(invMakes, (guiWidth - makesWidth) / 2, 73, 0x000000);
		fontRenderer.drawString(storageHeader, 8, 103, 0x000000);
		fontRenderer.drawString(invHeader, 8, 150, 0x000000);
		fontRenderer.drawString(outputHeader, 125, 25, 0x000000);
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(ModResources.Gui.SYNTHESIS);
		int x = (width - guiWidth) / 2;
		int y = (height - guiHeight) / 2;
		drawTexturedModalRect(x, y, 0, 0, guiWidth, guiHeight);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		final int i = Mouse.getEventDWheel();
		if (i != 0 && isShiftKeyDown() && isOverSynthesisMatrixSlot()) {
			final int x = Mouse.getEventX() * width / mc.displayWidth;
			final int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
			mouseWheelEvent(x, y, i / Math.abs(i));
		}

	}

	private boolean isOverSynthesisMatrixSlot() {
		Slot slot = getSlotUnderMouse();
		return slot != null && slot instanceof SlotFake;
	}

	private void mouseWheelEvent(final int x, final int y, final int wheel) {
		final Slot slot = getSlot(x, y);
		final ScrollDirection direction = wheel > 0 ? ScrollDirection.DOWN : ScrollDirection.UP;
		final int times = Math.abs(wheel);
		for (int h = 0; h < times; h++) {
			final MessageFakeSlotScroll p = new MessageFakeSlotScroll(direction, slot.slotNumber);
			ModNetworking.INSTANCE.sendToServer(p);
		}
	}

	public static enum ScrollDirection {
			UP(-1), IDLE(0), DOWN(1);

		private final int id;

		ScrollDirection(int id) {
			this.id = id;
		}

		public int getValue() {
			return id;
		}
	}

	private Slot getSlot(int x, int y) {
		for (int i = 0; i < inventorySlots.inventorySlots.size(); ++i) {
			Slot slot = inventorySlots.inventorySlots.get(i);

			if (isMouseOverSlot(slot, x, y) && slot.isEnabled()) {
				return slot;
			}
		}

		return null;
	}

	private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
		return isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
	}

}
