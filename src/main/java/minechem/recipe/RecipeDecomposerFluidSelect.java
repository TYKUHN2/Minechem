package minechem.recipe;

import java.util.ArrayList;
import java.util.Arrays;

import minechem.potion.PotionChemical;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RecipeDecomposerFluidSelect extends RecipeDecomposerFluidChance
{

    ArrayList<RecipeDecomposer> possibleRecipes = new ArrayList<>();

    public RecipeDecomposerFluidSelect(FluidStack fluid, float chance, RecipeDecomposer[] recipes)
    {
        super(fluid, chance);
        possibleRecipes.addAll(Arrays.asList(recipes));
    }

    public RecipeDecomposerFluidSelect(String fluid, int amount, float chance, RecipeDecomposer... recipes)
    {
        super(FluidRegistry.getFluidStack(fluid, amount), chance);
        possibleRecipes.addAll(Arrays.asList(recipes));
    }

    @Override
    public ArrayList<PotionChemical> getOutput()
    {
        if (random.nextFloat() < this.chance)
        {
            RecipeDecomposer selectedRecipe = possibleRecipes.get(random.nextInt(possibleRecipes.size()));
            return selectedRecipe.getOutput();
        }
        return null;
    }

    @Override
    public ArrayList<PotionChemical> getOutputRaw()
    {
        return possibleRecipes.get(0).getOutputRaw();
    }

    public ArrayList<RecipeDecomposer> getAllPossibleRecipes()
    {
        return this.possibleRecipes;
    }

}
