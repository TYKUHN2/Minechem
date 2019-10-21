package minechem.init;

import minechem.fluid.FluidElement;
import minechem.item.element.ElementEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCreativeTab extends CreativeTabs {
	public static CreativeTabs CREATIVE_TAB_ITEMS = new ModCreativeTab(ModGlobals.NAME, 0);
	public static CreativeTabs CREATIVE_TAB_ELEMENTS = new ModCreativeTab(ModGlobals.NAME + ".Elements", 1);
	public static CreativeTabs CREATIVE_TAB_BUCKETS = new ModCreativeTab(ModGlobals.NAME + ".buckets", 2);

	private final int tabIcon;

	public ModCreativeTab(final String tabName, final int i) {
		super(tabName);
		tabIcon = i;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(final NonNullList<ItemStack> stackList) {
		if (tabIcon == 2) {
			for (final FluidElement fluid : ModFluids.FLUID_ELEMENTS.values()) {
				stackList.add(FluidUtil.getFilledBucket(new FluidStack(fluid, 1000)));
			}
			for (final Fluid fluid : ModFluids.FLUID_MOLECULES.values()) {
				stackList.add(FluidUtil.getFilledBucket(new FluidStack(fluid, 1000)));
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
			return FluidUtil.getFilledBucket(new FluidStack(ModFluids.FLUID_ELEMENTS.get(ElementEnum.Ne), 1000));
		default:
			return new ItemStack(Items.FERMENTED_SPIDER_EYE);
		}
	}

	@Override
	public ItemStack getTabIconItem() {
		return null;
	}
}
