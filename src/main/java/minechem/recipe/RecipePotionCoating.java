package minechem.recipe;

import minechem.init.ModConfig;
import minechem.init.ModGlobals;
import minechem.item.ItemMolecule;
import minechem.potion.PharmacologyEffectRegistry;
import minechem.potion.PotionEnchantmentCoated;
import minechem.utils.MinechemUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class RecipePotionCoating implements IRecipe {

	ItemStack output = ItemStack.EMPTY;

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

	public boolean doesItemContainEnchantment(ItemStack stack, Enchantment enchantment) {
		NBTTagList enchNBTList = stack.getEnchantmentTagList();
		int enchID = Enchantment.getEnchantmentID(enchantment);
		if (enchNBTList.hasNoTags()) {
			return false;
		}
		else {
			for (int i = 0; i < enchNBTList.tagCount(); i++) {
				NBTTagCompound tag = enchNBTList.getCompoundTagAt(i);
				if (!tag.hasNoTags() && tag.hasKey("id", Constants.NBT.TAG_SHORT)) {
					if (tag.getShort("id") == enchID) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private int getCurrentEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
		NBTTagList enchNBTList = stack.getEnchantmentTagList();
		int enchID = Enchantment.getEnchantmentID(enchantment);
		if (!enchNBTList.hasNoTags()) {
			for (int i = 0; i < enchNBTList.tagCount(); i++) {
				NBTTagCompound tag = enchNBTList.getCompoundTagAt(i);
				if (!tag.hasNoTags() && tag.hasKey("id", Constants.NBT.TAG_SHORT)) {
					if (tag.getShort("id") == enchID) {
						return tag.getShort("lvl");
					}
				}
			}
		}
		return -1;
	}

	/**
	 * @return true on success
	 */
	private boolean incEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
		int maxLevel = enchantment.getMaxLevel();
		int currentLevel = getCurrentEnchantmentLevel(stack, enchantment);
		if (currentLevel < maxLevel) {
			NBTTagList enchNBTList = stack.getEnchantmentTagList();
			int enchID = Enchantment.getEnchantmentID(enchantment);
			if (!enchNBTList.hasNoTags()) {
				for (int i = 0; i < enchNBTList.tagCount(); i++) {
					NBTTagCompound tag = enchNBTList.getCompoundTagAt(i);
					if (!tag.hasNoTags() && tag.hasKey("id", Constants.NBT.TAG_SHORT)) {
						if (tag.getShort("id") == enchID) {
							tag.setShort("lvl", (short) (tag.getShort("lvl") + 1));
							return true;
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
				ItemStack result = s.copy();
				for (int j = 0; j < inv.getSizeInventory(); j++) {
					ItemStack s2 = inv.getStackInSlot(j);
					if (!s2.isEmpty() && s2.getItem() instanceof ItemMolecule && PharmacologyEffectRegistry.hasEffect(MinechemUtil.getMolecule(s2))) {
						PotionEnchantmentCoated ench = PotionEnchantmentCoated.POTION_COATED_REGISTRY.get(MinechemUtil.getMolecule(s2));
						if (doesItemContainEnchantment(s, ench)) {
							if (incEnchantmentLevel(result, ench)) {
								return result;
							}
						}
						else {
							result.addEnchantment(PotionEnchantmentCoated.POTION_COATED_REGISTRY.get(MinechemUtil.getMolecule(s2)), 1);
							output = result.copy();
							return result;
						}
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventoryCrafting) {
		return NonNullList.withSize(9, ItemStack.EMPTY);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 1 && height >= 1;
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation name) {
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(ModGlobals.ID, "potion_coating");
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		return IRecipe.class;
	}

}
