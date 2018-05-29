package minechem.item.blueprint;

import java.util.List;

import javax.annotation.Nullable;

import minechem.init.ModCreativeTab;
import minechem.init.ModItems;
import minechem.item.ItemBase;
import minechem.utils.MinechemUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlueprint extends ItemBase {

	public static final String[] names = {
			"item.name.blueprintFusion",
			"item.name.blueprintFission"
	};

	public ItemBlueprint() {
		super();
		setUnlocalizedName("blueprint");
		setRegistryName("blueprint");
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setHasSubtypes(true);
		ForgeRegistries.ITEMS.register(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		for (int i = 0; i < 2; i++) {
			ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName(), "inventory"));
		}
	}

	public static ItemStack createItemStackFromBlueprint(MinechemBlueprint blueprint) {
		return new ItemStack(ModItems.blueprint, 1, blueprint.id);
	}

	public MinechemBlueprint getBlueprint(ItemStack itemstack) {
		int metadata = itemstack.getItemDamage();
		return MinechemBlueprint.blueprints.get(metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		MinechemBlueprint blueprint = getBlueprint(stack);
		if (blueprint != null) {
			String dimensions = String.format("%d x %d x %d", blueprint.xSize, blueprint.ySize, blueprint.zSize);
			tooltip.add(dimensions);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + "." + names[itemstack.getItemDamage()];
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		int metadata = itemstack.getItemDamage();
		return MinechemUtil.getLocalString(names[metadata], true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < names.length; i++) {
				list.add(new ItemStack(this, 1, i));
			}
		}
	}

}
