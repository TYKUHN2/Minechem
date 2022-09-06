package minechem.fluid.reaction;

import java.util.List;
import minechem.item.MinechemChemicalType;

public class ChemicalFluidReactionOutput
{

    public final List<MinechemChemicalType> outputs;
    public final float explosionLevel;

    /**
     * If explosionLevel==Float.NaN, then it will not explode.
     *
     * @param outputs Outputs of chemical reaction
     * @param explosionLevel Size of explosion of reaction
     */
    public ChemicalFluidReactionOutput(List<MinechemChemicalType> outputs, float explosionLevel)
    {
        this.outputs = outputs;
        this.explosionLevel = explosionLevel;
    }

}
