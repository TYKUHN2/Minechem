package minechem.client.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import minechem.block.tile.TileBlueprintProjector;
import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerBlueprintProjector;
import minechem.init.ModGlobals.ModResources;
import minechem.init.ModItems;
import minechem.item.blueprint.BlueprintBlock;
import minechem.item.blueprint.MinechemBlueprint;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiBlueprintProjector extends GuiContainerTabbed {
	TileBlueprintProjector projector;

	public GuiBlueprintProjector(InventoryPlayer inventoryPlayer, TileBlueprintProjector projector) {
		super(new ContainerBlueprintProjector(inventoryPlayer, projector));
		this.projector = projector;
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.projector")));
		//addTab(new GuiTabPatreon(this));
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
	}

	private HashMap<Integer, Integer> getBlockCount(MinechemBlueprint blueprint) {
		HashMap<Integer, Integer> blockCount = new HashMap<Integer, Integer>();

		for (int x = 0; x < blueprint.xSize; x++) {
			for (int y = 0; y < blueprint.ySize; y++) {
				for (int z = 0; z < blueprint.zSize; z++) {
					int structureID = blueprint.getStructure()[y][x][z];
					int count = 0;
					if (blockCount.get(structureID) != null) {
						count = blockCount.get(structureID);
					}
					count++;
					blockCount.put(structureID, count);
				}
			}
		}

		return blockCount;
	}

	private void drawBlueprintInfo(ItemStack blueprintStack) {
		MinechemBlueprint blueprint = ModItems.blueprint.getBlueprint(blueprintStack);
		if (blueprint == null) {
			return;
		}
		String name = blueprintStack.getDisplayName().replace("Blueprint", "");
		fontRenderer.drawStringWithShadow(name, 64, 12, 0xFFFFFF);
		HashMap<Integer, Integer> blockCount = getBlockCount(blueprint);
		HashMap<Integer, BlueprintBlock> blockLookup = blueprint.getBlockLookup();
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
	}
}
