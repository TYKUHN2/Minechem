package minechem.item.blueprint;

import java.util.List;

import javax.annotation.Nullable;

import minechem.api.IMinechemBlueprint;
import minechem.init.ModCreativeTab;
import minechem.init.ModGlobals;
import minechem.item.ItemBase;
import minechem.utils.BlueprintUtil;
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

	public ItemBlueprint() {
		super();
		setUnlocalizedName("blueprint");
		setRegistryName(ModGlobals.MODID + ":blueprint");
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setHasSubtypes(true);
		ForgeRegistries.ITEMS.register(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		for (int i = 0; i < 2; i++) {
			ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IMinechemBlueprint blueprint = BlueprintUtil.getBlueprint(stack);
		if (blueprint != null) {
			String dimensions = String.format("Size: %d x %d x %d", blueprint.xSize(), blueprint.ySize(), blueprint.zSize());
			tooltip.add(dimensions);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + ".name";
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		String displayName = MinechemUtil.getLocalString(getUnlocalizedName() + ".name");
		if (!BlueprintUtil.isBlueprintBlank(itemstack)) {
			displayName = MinechemUtil.getLocalString(BlueprintUtil.getBlueprint(itemstack).getDescriptiveName()) + " " + displayName;
		}
		else {
			displayName = MinechemUtil.getLocalString("item.blueprint.blank.desc") + " " + displayName;
		}
		return displayName;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) {
			//list.add(new ItemStack(this));
			list.addAll(BlueprintUtil.getAllBlueprintsAsStacks());
		}
	}

}
