package minechem.item;

import minechem.init.ModCreativeTab;
import minechem.init.ModGlobals;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMicroscopeLens extends ItemBase {

	static final String[] descriptiveNames = {
			"item.name.concaveLens",
			"item.name.convexLens",
			"item.name.microscopeLens",
			"item.name.projectorLens"
	};

	public ItemMicroscopeLens() {
		super();
		setUnlocalizedName("optical_microscope_lens");
		setRegistryName(ModGlobals.ID + ":optical_microscope_lens");
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setHasSubtypes(true);
		ForgeRegistries.ITEMS.register(this);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		int metadata = itemStack.getItemDamage();
		return MinechemUtil.getLocalString(descriptiveNames[metadata], true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) {
			list.add(new ItemStack(this, 1, 0));
			list.add(new ItemStack(this, 1, 1));
			list.add(new ItemStack(this, 1, 2));
			list.add(new ItemStack(this, 1, 3));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(new ResourceLocation(ModGlobals.ID, "lens_concave"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(new ResourceLocation(ModGlobals.ID, "lens_convex"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 2, new ModelResourceLocation(new ResourceLocation(ModGlobals.ID, "lens_microscope"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 3, new ModelResourceLocation(new ResourceLocation(ModGlobals.ID, "lens_projector"), "inventory"));
	}

}
