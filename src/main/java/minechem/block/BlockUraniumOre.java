package minechem.block;

import minechem.api.ICustomRenderer;
import minechem.init.ModCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockUraniumOre extends Block implements ICustomRenderer {

	public BlockUraniumOre() {
		super(Material.IRON);
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		setUnlocalizedName("uranium_ore");
		setRegistryName("uranium_ore");
		setHardness(4F);
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

}
