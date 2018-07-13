package minechem.fluid;

import java.awt.Color;

import minechem.init.ModItems;
import minechem.item.MinechemChemicalType;
import minechem.item.molecule.MoleculeEnum;
import net.minecraft.item.ItemStack;

public class FluidMolecule extends FluidMinechem {

	public final MoleculeEnum molecule;
	private final int color;

	public FluidMolecule(MoleculeEnum molecule) {
		super("molecule." + molecule.name(), true, molecule.roomState());
		this.molecule = molecule;
		color = computeColor();
		setColor(color);
	}

	@Override
	public ItemStack getOutputStack() {
		return new ItemStack(ModItems.molecule, 1, molecule.id());
	}

	@Override
	public MinechemChemicalType getChemical() {
		return molecule;
	}

	@Override
	public int getColor() {
		return color | 0xFF000000;
	}

	private int computeColor() {
		int red = (int) (molecule.red * 255);
		int green = (int) (molecule.green * 255);
		int blue = (int) (molecule.blue * 255);
		return new Color(red, green, blue).getRGB();
	}
}
