package minechem.recipe;

import java.util.ArrayList;
import java.util.Random;
import minechem.potion.PotionChemical;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RecipeDecomposerFluidChance extends RecipeDecomposerFluid
{

    static Random random = new Random();
    float chance;

    public RecipeDecomposerFluidChance(String fluid, int amount, float chance, PotionChemical[] chemicals)
    {
        super(fluid, amount, chemicals);
        this.chance = chance;
    }

    public RecipeDecomposerFluidChance(FluidStack fluid, float chance, PotionChemical... chemicals)
    {
        super(fluid, chemicals);
        this.chance = chance;
    }

    public static void createAndAddFluidRecipeSafely(String fluid, int amount, float chance, PotionChemical... chemicals)
    {
        if (FluidRegistry.isFluidRegistered(fluid))
        {
            RecipeDecomposer.add(new RecipeDecomposerFluidChance(fluid, amount, chance, chemicals));
        }
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
