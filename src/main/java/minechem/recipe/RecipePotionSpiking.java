package minechem.recipe;

import minechem.init.ModConfig;
import minechem.init.ModGlobals;
import minechem.item.ItemMolecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PharmacologyEffectRegistry;
import minechem.utils.MinechemUtil;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipePotionSpiking implements IRecipe {

	ItemStack result = ItemStack.EMPTY;

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		if (ModConfig.FoodSpiking) {
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack s = inv.getStackInSlot(i);
				if (!s.isEmpty() && s.getItem() instanceof ItemFood) {
					for (int j = 0; j < inv.getSizeInventory(); j++) {
						ItemStack s2 = inv.getStackInSlot(j);
						if (!s2.isEmpty() && s2.getItem() instanceof ItemMolecule) {
							if (PharmacologyEffectRegistry.hasEffect(MoleculeEnum.getById(s2.getItemDamage()))) {
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
			ItemStack foodItem = inv.getStackInSlot(i);
			if (!foodItem.isEmpty() && foodItem.getItem() instanceof ItemFood) {
				for (int j = 0; j < inv.getSizeInventory(); j++) {
					ItemStack moleculeStack = inv.getStackInSlot(j);
					if (!moleculeStack.isEmpty() && moleculeStack.getItem() instanceof ItemMolecule && PharmacologyEffectRegistry.hasEffect(MoleculeEnum.getById(moleculeStack.getItemDamage()))) {
						ItemStack result = foodItem.copy();
						result.setCount(1);
						if (result.getTagCompound() == null) // empty NBT
						{
							NBTTagCompound tagCompound = new NBTTagCompound();
							tagCompound.setBoolean("minechem.isPoisoned", true);
							tagCompound.setIntArray("minechem.effectTypes", new int[] {
									MinechemUtil.getMolecule(moleculeStack).id()
							});
							result.setTagCompound(tagCompound);
						}
						else if (result.getTagCompound().hasKey("minechem.isPoisoned")) // has been poisoned before
						{
							int[] arrayOld = result.getTagCompound().getIntArray("minechem.effectTypes");
							int[] arrayNew = new int[arrayOld.length + 1];
							System.arraycopy(arrayOld, 0, arrayNew, 0, arrayOld.length);
							arrayNew[arrayOld.length] = MinechemUtil.getMolecule(moleculeStack).id();
							result.getTagCompound().setIntArray("minechem.effectTypes", arrayNew);
						}
						else // has NBT but no poison
						{
							result.getTagCompound().setBoolean("minechem.isPoisoned", true);
							result.getTagCompound().setIntArray("minechem.effectTypes", new int[] {
									MinechemUtil.getMolecule(moleculeStack).id()
							});
						}
						this.result = result.copy();
						return result;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventoryCrafting) {
		return NonNullList.<ItemStack>create();
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation name) {
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(ModGlobals.MODID, "potion_spiking");
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		return IRecipe.class;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width + height >= 2;
	}

}
