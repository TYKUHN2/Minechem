package minechem.fluid;

import minechem.init.ModGlobals;
import minechem.item.MatterState;
import minechem.item.MinechemChemicalType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

abstract public class FluidMinechem extends Fluid {

	private static final String texturePrefix = ModGlobals.MODID + ":blocks/";
	private int quanta;

	public FluidMinechem(String fluidName, boolean hasFlowIcon, MatterState roomstatus) {
		super(fluidName, new ResourceLocation(texturePrefix + "fluid_still"), new ResourceLocation(texturePrefix + "fluid" + (hasFlowIcon ? "_flow" : "_still")));
		setGaseous(roomstatus.isGas());
		setViscosity(roomstatus.getViscosity());
		setDensity(roomstatus.isGas() ? -10 : 10);
		setQuanta(roomstatus.getQuanta());
	}

	public void setQuanta(int quanta) {
		this.quanta = quanta;
	}

	public int getQuanta() {
		return quanta;
	}

	abstract public ItemStack getOutputStack();

	abstract public MinechemChemicalType getChemical();

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName() + ".name";
	}
}
