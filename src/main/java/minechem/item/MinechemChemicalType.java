package minechem.item;

import minechem.radiation.RadiationEnum;

public abstract class MinechemChemicalType
{

    private final MatterState roomState;
    private final RadiationEnum radioactivity;

    public MinechemChemicalType(MatterState roomState, RadiationEnum radioactivity)
    {
        this.roomState = roomState;
        this.radioactivity = radioactivity;
    }

    public RadiationEnum radioactivity()
    {
        return radioactivity;
    }

    public MatterState roomState()
    {
        return roomState;
    }

    abstract public String getUnlocalizedName();
}
