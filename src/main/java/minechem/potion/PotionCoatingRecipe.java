package minechem.potion;

import minechem.init.ModConfig;
import minechem.item.ItemMolecule;
import minechem.utils.MinechemUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

@SuppressWarnings("deprecation")
public class PotionCoatingRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	static {
		RecipeSorter.register("minechem:coatingRecipe", PotionCoatingRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		if (ModConfig.SwordEffects) {
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack s = inv.getStackInSlot(i);
				if (!s.isEmpty() && s.getItem() instanceof ItemSword) {
					for (int j = 0; j < inv.getSizeInventory(); j++) {
						ItemStack s2 = inv.getStackInSlot(j);
						if (!s2.isEmpty() && s2.getItem() instanceof ItemMolecule) {
							if (PharmacologyEffectRegistry.hasEffect(MinechemUtil.getMolecule(s2))) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack s = inv.getStackInSlot(i);
			if (!s.isEmpty() && s.getItem() instanceof ItemSword) {
				for (int j = 0; j < inv.getSizeInventory(); j++) {
					ItemStack s2 = inv.getStackInSlot(j);
					if (!s2.isEmpty() && s2.getItem() instanceof ItemMolecule && PharmacologyEffectRegistry.hasEffect(MinechemUtil.getMolecule(s2))) {
						NBTTagList l = s2.getEnchantmentTagList();
						int level = 0;
						if (l != null && !l.hasNoTags()) {
							for (int k = 0; k < l.tagCount(); k++) {
								NBTTagCompound tag = l.getCompoundTagAt(k);
								Enchantment ench = PotionEnchantmentCoated.POTION_COATED_REGISTRY.get(MinechemUtil.getMolecule(s2));
								if (tag.getShort("id") == Enchantment.getEnchantmentID(ench)) {
									level = tag.getShort("lvl");
									ItemStack result = s.copy();
									result.getEnchantmentTagList().getCompoundTagAt(k).setInteger("lvl", level + 1);
								}
							}
						}
						ItemStack result = s.copy();
						result.addEnchantment(PotionEnchantmentCoated.POTION_COATED_REGISTRY.get(MinechemUtil.getMolecule(s2)), 1);
						return result;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventoryCrafting) {
		return NonNullList.create();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 1 && height >= 1;
	}

}
