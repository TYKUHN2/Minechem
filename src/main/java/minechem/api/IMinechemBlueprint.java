package minechem.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author p455w0rd
 *
 */
public interface IMinechemBlueprint extends IForgeRegistryEntry<IMinechemBlueprint> {

	// width
	int xSize();

	// height
	int ySize();

	// depth
	int zSize();

	int getRenderScale();

	IBlockState[][] getHorizontalSlice(int y);

	IBlockState[][] getVerticalSlice(int x);

	int getTotalSize();

	IBlockState[][][] getStructure();

	int getManagerPosX();

	int getManagerPosY();

	int getManagerPosZ();

	String getDescriptiveName();

	default IBlockState getBlockstateFromStack(int index, ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
			return ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getItemDamage());
		}
		return null;
	}

}
