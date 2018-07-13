package minechem.init;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import minechem.api.ICustomRenderer;
import minechem.block.fluid.BlockFluidElement;
import minechem.block.fluid.BlockFluidMolecule;
import minechem.fluid.FluidElement;
import minechem.fluid.FluidMolecule;
import minechem.item.MatterState;
import minechem.item.MinechemChemicalType;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * @author p455w0rd
 *
 */
public class ModFluids {

	public static Map<MoleculeEnum, Fluid> FLUID_MOLECULES = new IdentityHashMap<MoleculeEnum, Fluid>();
	public static Map<ElementEnum, FluidElement> FLUID_ELEMENTS = new IdentityHashMap<ElementEnum, FluidElement>();

	public static Map<Fluid, Block> FLUID_MOLECULE_BLOCKS = new IdentityHashMap<Fluid, Block>();
	public static Map<FluidElement, Block> FLUID_ELEMENT_BLOCKS = new IdentityHashMap<FluidElement, Block>();

	public static void init() {
		for (ElementEnum element : ElementEnum.elements.values()) {
			if (element != null && (element.roomState() == MatterState.LIQUID || element.roomState() == MatterState.GAS)) {
				FluidElement fluid = (FluidElement) createFluid(element);
				FLUID_ELEMENTS.put(element, fluid);
				FLUID_ELEMENT_BLOCKS.put(fluid, new BlockFluidElement(fluid));
			}
		}
		for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
			if (molecule != null && (molecule.roomState() == MatterState.LIQUID || molecule.roomState() == MatterState.GAS)) {
				if (molecule == MoleculeEnum.water) {
					Fluid fluid = FluidRegistry.WATER;
					FLUID_MOLECULES.put(molecule, fluid);
					BlockStaticLiquid fluidBlock = Blocks.WATER;
					FLUID_MOLECULE_BLOCKS.put(fluid, fluidBlock);
				}
				else {
					FluidMolecule fluid = (FluidMolecule) createFluid(molecule);
					FLUID_MOLECULES.put(molecule, fluid);
					BlockFluidMolecule fluidBlock = new BlockFluidMolecule(fluid);
					FLUID_MOLECULE_BLOCKS.put(fluid, fluidBlock);
				}
			}
		}
	}

	public static void initModels() {
		for (Block block : FLUID_ELEMENT_BLOCKS.values()) {
			if (block instanceof BlockFluidElement) {
				((ICustomRenderer) block).registerRenderer();
			}
		}
		for (Block block : FLUID_MOLECULE_BLOCKS.values()) {
			if (block instanceof BlockFluidMolecule) {
				((ICustomRenderer) block).registerRenderer();
			}
		}
	}

	private static Fluid createFluid(MinechemChemicalType type) {
		Fluid fluid;
		if (type instanceof ElementEnum) {
			fluid = new FluidElement((ElementEnum) type);
		}
		else {
			fluid = new FluidMolecule((MoleculeEnum) type);
		}
		boolean useOwnFluid = FluidRegistry.registerFluid(fluid);
		if (!useOwnFluid) {
			fluid = FluidRegistry.getFluid(fluid.getName());
		}
		return fluid;
	}

	public static List<Block> getFluidBlocks() {
		if (FLUID_ELEMENT_BLOCKS.size() <= 0 || FLUID_MOLECULE_BLOCKS.size() <= 0) {
			init();
		}
		List<Block> blockList = Lists.newArrayList();
		for (Block block : FLUID_ELEMENT_BLOCKS.values()) {
			blockList.add(block);
		}
		for (Block block : FLUID_MOLECULE_BLOCKS.values()) {
			blockList.add(block);
		}
		return blockList;
	}

}
