package minechem.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockGhost extends ItemBlock {

	public ItemBlockGhost(Block block) {
		super(block);
		setUnlocalizedName(block.getRegistryName().getResourcePath());
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damageValue) {
		return damageValue;
	}

}
