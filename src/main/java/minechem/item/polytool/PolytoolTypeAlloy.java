package minechem.item.polytool;

import java.util.Iterator;

import minechem.item.element.ElementAlloyEnum;
import minechem.item.element.ElementEnum;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class PolytoolTypeAlloy extends PolytoolUpgradeType {

	private ElementAlloyEnum alloy;

	public PolytoolTypeAlloy(ElementAlloyEnum alloy) {
		this.alloy = alloy;
	}

	public PolytoolTypeAlloy(ElementAlloyEnum alloy, float power) {
		this.power = power;
		this.alloy = alloy;
	}

	public float getStrOre() {
		return alloy.pickaxe * power;
	}

	public float getStrStone() {
		return alloy.stone * power;
	}

	public float getStrAxe() {
		return alloy.axe * power;
	}

	public float getStrSword() {
		return alloy.sword * power;
	}

	public float getStrShovel() {
		return alloy.shovel * power;
	}

	@Override
	public float getStrVsBlock(ItemStack itemStack, Block block, int meta) {
		// There must be a better way to do this
		if (isToolEffective(new ItemStack(Items.DIAMOND_PICKAXE), block, meta)) {
			for (int id : OreDictionary.getOreIDs(new ItemStack(block, 1, meta))) {
				if (OreDictionary.getOreName(id).contains("stone")) {
					return getStrStone();
				}
			}
			if (block == Blocks.STONE || block == Blocks.COBBLESTONE) {
				return getStrStone();
			}
			return getStrOre();
		}
		else if (isToolEffective(new ItemStack(Items.DIAMOND_SHOVEL), block, meta)) {
			return getStrShovel();
		}
		else if (isToolEffective(new ItemStack(Items.DIAMOND_SWORD), block, meta)) {
			return getStrSword();
		}
		else if (isToolEffective(new ItemStack(Items.DIAMOND_AXE), block, meta)) {
			return getStrAxe();
		}
		return 0;
	}

	public boolean isToolEffective(ItemStack itemStack, Block block, int meta) {
		IBlockState state = block.getStateFromMeta(meta);
		Iterator<String> var4 = itemStack.getItem().getToolClasses(itemStack).iterator();

		String type;
		do {
			if (!var4.hasNext()) {
				return false;
			}

			type = var4.next();
		}
		while (!state.getBlock().isToolEffective(type, state));

		return true;
	}

	@Override
	public float getDamageModifier() {
		return getStrSword();
	}

	@Override
	public ElementEnum getElement() {
		return alloy.element;
	}

	@Override
	public String getDescription() {

		String result = "";

		result += "Ore: " + getStrOre() + " ";
		result += "Stone: " + getStrStone() + " ";
		result += "Sword: " + getStrSword() + " ";
		result += "Axe: " + getStrAxe() + " ";
		result += "Shovel: " + getStrShovel() + " ";

		return result;
	}
}
