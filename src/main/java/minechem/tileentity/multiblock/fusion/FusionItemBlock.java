package minechem.tileentity.multiblock.fusion;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class FusionItemBlock extends ItemBlock {

	private static final String[] names = {
			"fusion_wall", "fission_wall", "tungsten_plating", "fusion_core"
	};

	public FusionItemBlock(Block block) {
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName("fusion_wall");
	}

	@Override
	public int getMetadata(int damageValue) {
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "minechem." + names[itemstack.getItemDamage()];
	}
}
