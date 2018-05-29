package minechem.item;

import minechem.init.ModCreativeTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAtomicManipulator extends ItemBase {

	public ItemAtomicManipulator() {
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setUnlocalizedName("atomic_manipulator");
		setRegistryName("atomic_manipulator");
		setHasSubtypes(true);
		ForgeRegistries.ITEMS.register(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
			subItems.add(new ItemStack(this, 1, 0));
		}
	}

}
