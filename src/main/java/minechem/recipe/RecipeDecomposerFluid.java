package minechem.recipe;

import java.util.ArrayList;

import minechem.potion.PotionChemical;
import minechem.utils.MapKey;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RecipeDecomposerFluid extends RecipeDecomposer {

	public FluidStack inputFluid;

	public RecipeDecomposerFluid(FluidStack fluid, PotionChemical... chemicals) {
		super(chemicals);
		inputFluid = fluid;
	}

	public RecipeDecomposerFluid(String fluid, int amount, PotionChemical[] chemicals) {

		this(new FluidStack(FluidRegistry.getFluid(fluid), amount), chemicals);
	}

	public static void createAndAddFluidRecipeSafely(String fluid, int amount, PotionChemical... chemicals) {
		if (FluidRegistry.isFluidRegistered(fluid)) {
			RecipeDecomposer.add(new RecipeDecomposerFluid(fluid, amount, chemicals));
		}
	}

	@Override
	public ItemStack getInput() {
		return inputFluid.getFluid() == null ? ItemStack.EMPTY : new ItemStack(inputFluid.getFluid().getBlock());
	}

	@Override
	public ArrayList<PotionChemical> getOutput() {
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		result.addAll(output.values());
		return result;
	}

	@Override
	public ArrayList<PotionChemical> getOutputRaw() {
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		result.addAll(output.values());
		return result;
	}

	@Override
	public MapKey getKey() {
		return new MapKey(inputFluid);
	}
}
