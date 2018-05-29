package minechem.init;

import java.util.List;

import com.google.common.collect.Lists;

import minechem.api.ICustomRenderer;
import minechem.item.ItemAtomicManipulator;
import minechem.item.ItemChemistJournal;
import minechem.item.ItemElement;
import minechem.item.ItemMicroscopeLens;
import minechem.item.ItemMolecule;
import minechem.item.ItemPolytool;
import minechem.item.blueprint.ItemBlueprint;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ModItems {

	private static List<Item> ITEM_LIST = Lists.newArrayList();

	public static ItemElement element;
	public static ItemMolecule molecule;
	public static ItemMicroscopeLens lens;
	public static ItemAtomicManipulator atomicManipulator;
	public static ItemBlueprint blueprint;
	public static ItemChemistJournal journal;
	public static ItemStack convexLens;
	public static ItemStack concaveLens;
	public static ItemStack projectorLens;
	public static ItemStack microscopeLens;
	public static ItemStack minechempills;
	public static Item polytool;
	public static ItemStack emptyTube;

	public static void init() {
		ITEM_LIST.add(element = new ItemElement());
		ITEM_LIST.add(molecule = new ItemMolecule());
		ITEM_LIST.add(lens = new ItemMicroscopeLens());
		concaveLens = new ItemStack(lens, 1, 0);
		convexLens = new ItemStack(lens, 1, 1);
		microscopeLens = new ItemStack(lens, 1, 2);
		projectorLens = new ItemStack(lens, 1, 3);

		ITEM_LIST.add(atomicManipulator = new ItemAtomicManipulator());
		ITEM_LIST.add(blueprint = new ItemBlueprint());
		ITEM_LIST.add(journal = new ItemChemistJournal());
		ITEM_LIST.add(polytool = new ItemPolytool());

		emptyTube = new ItemStack(ModItems.element, 1, 0);
	}

	@SideOnly(Side.CLIENT)
	public static void registerRenderers() {
		for (Item item : ITEM_LIST) {
			if (item instanceof ICustomRenderer) {
				((ICustomRenderer) item).registerRenderer();
			}
		}
	}

	public static void registerFluidContainers() {
		for (ElementEnum element : ElementEnum.elements.values()) {
			if (element != null) {
				ItemStack tube = new ItemStack(ModItems.element, 1, element.atomicNumber());

				//FluidContainerRegistry.registerFluidContainer(new FluidStack(FluidHelper.elements.get(element), 125), tube, emptyTube);
			}
		}

		for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
			if (molecule != null) {
				ItemStack tube = new ItemStack(ModItems.molecule, 1, molecule.id());
				FluidStack fluidStack = new FluidStack(FluidRegistry.WATER, 125);
				if (!molecule.name().equals("water")) {
					fluidStack = new FluidStack(ModFluids.FLUID_MOLECULES.get(molecule), 125);
				}
				//FluidContainerRegistry.registerFluidContainer(fluidStack, tube, emptyTube);
			}
		}
	}

	public static void registerToOreDictionary() {
		for (ElementEnum element : ElementEnum.elements.values()) {
			OreDictionary.registerOre("element_" + element.name(), new ItemStack(ModItems.element, 1, element.atomicNumber()));
		}
		for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
			OreDictionary.registerOre("molecule_" + molecule.name(), new ItemStack(ModItems.molecule, 1, molecule.id()));
		}
		OreDictionary.registerOre("dustSaltpeter", new ItemStack(ModItems.molecule, 1, MoleculeEnum.potassiumNitrate.id()));
		OreDictionary.registerOre("dustSalt", new ItemStack(ModItems.molecule, 1, MoleculeEnum.salt.id()));
		OreDictionary.registerOre("quicksilver", new ItemStack(ModItems.element, 1, ElementEnum.Hg.atomicNumber()));
	}

}
