package minechem.recipe;

import java.util.ArrayList;
import minechem.potion.PotionChemical;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RecipeDecomposerFluidSelect extends RecipeDecomposerFluidChance
{

    ArrayList<RecipeDecomposer> possibleRecipes = new ArrayList<RecipeDecomposer>();

    public RecipeDecomposerFluidSelect(FluidStack fluid, float chance, RecipeDecomposer[] recipes)
    {
        super(fluid, chance);
        for (RecipeDecomposer rec : recipes)
        {
            possibleRecipes.add(rec);
        }
    }

    public RecipeDecomposerFluidSelect(String fluid, int amount, float chance, RecipeDecomposer... recipes)
    {
        super(FluidRegistry.getFluidStack(fluid, amount), chance);
        for (RecipeDecomposer rec : recipes)
        {
            possibleRecipes.add(rec);
        }
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
