package minechem.recipe;

import minechem.init.ModGlobals;
import minechem.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeCloneChemistJournal implements IRecipe {

	/*
	static {
		RecipeSorter.register("minechem:cloneJournal", ChemistJournalRecipeCloning.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}
	*/
	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		ItemStack itemstack1 = crafting.getStackInSlot(0);
		ItemStack itemstack2 = crafting.getStackInSlot(1);
		return (!itemstack1.isEmpty() && itemstack1.getItem() == ModItems.journal) && (!itemstack2.isEmpty() && itemstack2.getItem() == Items.BOOK);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack journal = crafting.getStackInSlot(0);
		ItemStack newJournal = journal.copy();
		newJournal.setCount(2);
		return newJournal;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
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
		return new ResourceLocation(ModGlobals.ID, "clone_journal");
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		return IRecipe.class;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 4;
	}

}
