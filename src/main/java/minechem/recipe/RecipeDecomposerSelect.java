package minechem.recipe;

import java.util.ArrayList;
import minechem.potion.PotionChemical;
import net.minecraft.item.ItemStack;

public class RecipeDecomposerSelect extends RecipeDecomposerChance
{

    ArrayList<RecipeDecomposer> possibleRecipes = new ArrayList<RecipeDecomposer>();

    public RecipeDecomposerSelect(ItemStack input, float chance, RecipeDecomposer... possibleRecipes)
    {
        super(input, chance);
        for (RecipeDecomposer recipe : possibleRecipes)
        {
            this.possibleRecipes.add(recipe);
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

    public RecipeDecomposer getRecipeRaw()
    {
        return possibleRecipes.get(0);
    }

    public ArrayList<RecipeDecomposer> getAllPossibleRecipes()
    {
        return this.possibleRecipes;
    }

    @Override
    public boolean isNull()
    {
        return (super.isNull() && this.possibleRecipes == null);
    }

    @Override
    public boolean hasOutput()
    {
        return !this.possibleRecipes.isEmpty();
    }

    @Override
    public boolean outputContains(PotionChemical potionChemical)
    {
        boolean contains = false;
        for (RecipeDecomposer dr : possibleRecipes)
        {
            contains = dr.outputContains(potionChemical);
            if (contains)
            {
                break;
            }
        }
        return contains;
    }
}
