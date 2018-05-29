package minechem.init;

import minechem.fluid.FluidElement;
import minechem.item.element.ElementEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCreativeTab extends CreativeTabs {
	public static CreativeTabs CREATIVE_TAB_ITEMS = new ModCreativeTab(ModGlobals.NAME, 0);
	public static CreativeTabs CREATIVE_TAB_ELEMENTS = new ModCreativeTab(ModGlobals.NAME + ".Elements", 1);
	public static CreativeTabs CREATIVE_TAB_BUCKETS = new ModCreativeTab(ModGlobals.NAME + ".buckets", 2);

	private int tabIcon;

	public ModCreativeTab(String tabName, int i) {
		super(tabName);
		tabIcon = i;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(NonNullList<ItemStack> stackList) {
		if (tabIcon == 2) {
			for (FluidElement fluid : ModFluids.FLUID_ELEMENTS.values()) {
				stackList.add(UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid));
			}
			for (Fluid fluid : ModFluids.FLUID_MOLECULES.values()) {
				stackList.add(UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid));
			}
		}
		else {
			super.displayAllRelevantItems(stackList);
		}
	}

	@Override
	public ItemStack getIconItemStack() {
		switch (tabIcon) {
		case 0:
			return new ItemStack(ModBlocks.microscope);
		case 1:
			return new ItemStack(ModItems.element, 1, ElementEnum.U.atomicNumber());
		case 2:
			return UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, ModFluids.FLUID_ELEMENTS.get(ElementEnum.Ne));
		default:
			return new ItemStack(Items.FERMENTED_SPIDER_EYE);
		}
	}

	@Override
	public ItemStack getTabIconItem() {
		return null;
	}
}
