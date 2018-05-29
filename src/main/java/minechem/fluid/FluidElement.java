package minechem.fluid;

import java.awt.Color;

import minechem.init.ModItems;
import minechem.item.MinechemChemicalType;
import minechem.item.element.ElementEnum;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidElement extends MinechemFluid {

	public final ElementEnum element;
	private final int color;

	public FluidElement(ElementEnum element) {
		super("element." + element.name(), true, element.roomState());
		this.element = element;
		color = computColor();
		setColor(color);
	}

	@Override
	public ItemStack getOutputStack() {
		return new ItemStack(ModItems.element, 1, element.atomicNumber());
	}

	@Override
	public MinechemChemicalType getChemical() {
		return element;
	}

	@Override
	public int getColor(FluidStack stack) {
		return stack.getFluid().getColor();
	}

	@Override
	public int getColor() {
		return color;
	}

	private int computColor() {
		float red = 0.0F;
		float blue = 0.0F;
		float green = 0.0F;
		switch (element.classification()) {
		case actinide:
			red = 1.0F;
			break;
		case alkaliMetal:
			green = 1.0F;
			break;
		case alkalineEarthMetal:
			blue = 1.0F;
			break;
		case halogen:
			red = 1.0F;
			green = 1.0F;
			break;
		case inertGas:
			green = 1.0F;
			blue = 1.0F;
			break;
		case lanthanide:
			red = 1.0F;
			blue = 1.0F;
			break;
		case nonmetal:
			red = 1.0F;
			green = 0.5F;
			break;
		case otherMetal:
			red = 0.5F;
			green = 1.0F;
			break;
		case semimetallic:
			green = 1.0F;
			blue = 0.5F;
			break;
		case transitionMetal:
			green = 0.5F;
			blue = 1.0F;
			break;
		default:
			red = 1.0F;
			blue = 1.0F;
			green = 1.0F;
			break;
		}

		return new Color(red, green, blue, 1.0F).getRGB();
	}
}
