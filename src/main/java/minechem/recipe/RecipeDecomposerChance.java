package minechem.recipe;

import java.util.ArrayList;
import java.util.Random;
import minechem.potion.PotionChemical;
import net.minecraft.item.ItemStack;

public class RecipeDecomposerChance extends RecipeDecomposer
{

    static Random random = new Random();
    float chance;

    public RecipeDecomposerChance(ItemStack input, float chance, PotionChemical... output)
    {
        super(input, output);
        this.chance = chance;
    }

    @Override
    public ArrayList<PotionChemical> getOutput()
    {
        if (random.nextFloat() < this.chance)
        {
            return super.getOutput();
        } else
        {
            return null;
        }
    }

    @Override
    public float getChance()
    {
        return chance;
    }

}
