package minechem.api;

import java.util.WeakHashMap;

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

	@SuppressWarnings("deprecation")
	default IBlockState getBlockstateFromStack(final int index, final ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
			return ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getItemDamage());
		}
		return null;
	}

	default WeakHashMap<IBlockState, Integer> getMaterials() {
		final WeakHashMap<IBlockState, Integer> tmpMap = new WeakHashMap<>();
		for (int x = 0; x < xSize(); x++) {
			for (int y = 0; y < ySize(); y++) {
				for (int z = 0; z < zSize(); z++) {
					final IBlockState tmpState = getStructure()[y][x][z];
					if (tmpMap.isEmpty() || !tmpMap.containsKey(tmpState)) {
						tmpMap.put(tmpState, 1);
					}
					else {
						final int currCount = tmpMap.get(tmpState);
						tmpMap.put(tmpState, currCount + 1);
					}
				}
			}
		}
		return tmpMap;
	}

	int getXOffset();

	int getYOffset();

}
