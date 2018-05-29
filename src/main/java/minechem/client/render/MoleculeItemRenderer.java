package minechem.client.render;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import minechem.init.ModGlobals.Textures;
import minechem.item.ItemMolecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class MoleculeItemRenderer extends TileEntityItemStackRenderer {

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {
		if (stack.getItem() instanceof ItemMolecule) {
			MoleculeEnum molecule = MinechemUtil.getMolecule(stack);
			List<ILayer> layers = new LinkedList<ILayer>();
			int color1 = new Color(molecule.red, molecule.green, molecule.blue).getRGB();
			int color2 = new Color(molecule.red2, molecule.green2, molecule.blue2).getRGB();
			layers.add(new IconLayer(Textures.Sprite.MOLECULE_TUBE));
			layers.add(new IconLayer(Textures.Sprite.MOLECULE_PASS_1, true, false));
			layers.add(new IconLayer(Textures.Sprite.MOLECULE_PASS_2, true));
			GlStateManager.pushMatrix();
			//RenderHelper.disableStandardItemLighting();
			GlStateManager.enableRescaleNormal();
			//GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(1.0, 0, 1.0);
			GlStateManager.rotate(180.0f, 0, 1.0f, 0);
			layers.get(0).render();
			layers.get(2).render(color2);
			layers.get(1).render(color1);
			layers.get(0).render();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			//RenderHelper.enableStandardItemLighting();
			//GlStateManager.disableLighting();
			//GlStateManager.enableLighting();
			//GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
