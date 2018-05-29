package minechem.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import minechem.client.gui.widget.tab.TabHelp;
import minechem.container.ContainerPolytool;
import minechem.init.ModGlobals.ModResources;
import minechem.init.ModItems;
import minechem.item.ItemPolytool;
import minechem.item.element.ElementEnum;
import minechem.item.element.ElementGuiHelper;
import minechem.item.polytool.PolytoolUpgradeType;
import minechem.utils.MinechemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiPolytool extends GuiContainerTabbed {
	private static final ItemStack polytoolItem = new ItemStack(ModItems.polytool);
	private static final Random rand = new Random();
	public List<ElementGuiHelper> elements = Lists.<ElementGuiHelper>newArrayList();
	long renders;
	ItemStack polytool;
	InventoryPlayer player;
	boolean shouldUpdate;

	public GuiPolytool(ContainerPolytool polytoolContainer) {
		super(polytoolContainer);
		xSize = 176;
		ySize = 218;
		polytool = polytoolContainer.player.getCurrentItem();
		player = polytoolContainer.player;
		shouldUpdate = true;
		ItemStack stack = polytoolContainer.player.getCurrentItem();
		if (stack.getItem() instanceof ItemPolytool) {
			ArrayList<PolytoolUpgradeType> upgrades = ItemPolytool.getUpgrades(stack);
			Iterator<PolytoolUpgradeType> iter = upgrades.iterator();
			while (iter.hasNext()) {
				PolytoolUpgradeType upgrade = iter.next();
				ElementEnum element = upgrade.getElement();
				for (int i = 0; i < upgrade.power; i++) {

					elements.add(new ElementGuiHelper(1 + rand.nextInt(2), rand.nextDouble() * Math.PI * 2, element));
				}

			}
		}
		addTab(new TabHelp(this, MinechemUtil.getLocalString("help.polytool")));

	}

	public void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str) {
		par2 += guiLeft;
		par3 += guiTop;

		// Copied from GuiContainer
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		zLevel = 3;
		itemRender.zLevel = 200.0F;
		FontRenderer font = null;
		if (!par1ItemStack.isEmpty()) {
			font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
		}
		if (font == null) {
			font = fontRenderer;
		}
		itemRender.renderItemAndEffectIntoGUI(par1ItemStack, par2, par3);
		itemRender.renderItemOverlayIntoGUI(font, par1ItemStack, par2, par3 - (8), par4Str);
		zLevel = 3;
		itemRender.zLevel = 0.0F;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(ModResources.Gui.POLYTOOL);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		renders++;

		Iterator<ElementGuiHelper> renderIter = elements.iterator();
		while (renderIter.hasNext()) {
			renderIter.next().draw(this, renders);
		}

		drawItemStack(polytoolItem, 80, 42, "");
		String localizedName;

		localizedName = MinechemUtil.getLocalString("gui.polytool.sword");
		if (localizedName.isEmpty() || localizedName.equals("gui.polytool.sword")) {
			localizedName = "Sword";
		}
		fontRenderer.drawString(localizedName + ": " + ItemPolytool.instance.getSwordStr(polytool), guiLeft + 10, guiTop + 80, 0x404040);

		localizedName = MinechemUtil.getLocalString("gui.polytool.ores");
		if (localizedName.isEmpty() || localizedName.equals("gui.polytool.ores")) {
			localizedName = "Ores";
		}
		fontRenderer.drawString(localizedName + ": " + ItemPolytool.instance.getPickaxeStr(polytool), guiLeft + 10, guiTop + 90, 0x404040);

		localizedName = MinechemUtil.getLocalString("gui.polytool.stone");
		if (localizedName.isEmpty() || localizedName.equals("gui.polytool.stone")) {
			localizedName = "Stone";
		}
		fontRenderer.drawString(localizedName + ": " + ItemPolytool.instance.getStoneStr(polytool), guiLeft + 10, guiTop + 100, 0x404040);

		localizedName = MinechemUtil.getLocalString("gui.polytool.axe");
		if (localizedName.isEmpty() || localizedName.equals("gui.polytool.axe")) {
			localizedName = "Axe";
		}
		fontRenderer.drawString(localizedName + ": " + ItemPolytool.instance.getAxeStr(polytool), guiLeft + 10, guiTop + 110, 0x404040);

		localizedName = MinechemUtil.getLocalString("gui.polytool.shovel");
		if (localizedName.isEmpty() || localizedName.equals("gui.polytool.shovel")) {
			localizedName = "Shovel";
		}
		fontRenderer.drawString(localizedName + ": " + ItemPolytool.instance.getShovelStr(polytool), guiLeft + 10, guiTop + 120, 0x404040);
	}

	public void addUpgrade(PolytoolUpgradeType upgrade) {
		for (int i = 0; i < upgrade.power; i++) {
			elements.add(new ElementGuiHelper(1 + rand.nextInt(2), rand.nextDouble() * Math.PI * 2, upgrade.getElement()));
			shouldUpdate = true;
		}
		polytool = player.getCurrentItem();
	}
}
