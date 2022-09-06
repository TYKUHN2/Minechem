package minechem.handler;

import java.util.*;

import javax.annotation.Nonnull;

import minechem.init.ModFluids;
import minechem.init.ModItems;
import minechem.item.ItemElement;
import minechem.item.ItemMolecule;
import minechem.item.element.Element;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import minechem.radiation.RadiationEnum;
import minechem.utils.MinechemUtil;
import minechem.utils.RadiationUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HandlerMoleculeDecay {

	private static HandlerMoleculeDecay instance = null;

	public static HandlerMoleculeDecay getInstance() {
		if (instance == null) {
			instance = new HandlerMoleculeDecay();
		}

		return instance;
	}

	private final Map<MoleculeEnum, PotionChemical[]> decayedMoleculesCache;

	private HandlerMoleculeDecay() {
		decayedMoleculesCache = new WeakHashMap<>();
		initDecayedMoleculesPre();
	}

	public RadiationUtil handleRadiationMoleculeBucket(World world, @Nonnull ItemStack itemStack, IInventory inventory, double x, double y, double z) {
		PotionChemical[] decayedChemicals = getDecayedMolecule((MoleculeEnum) MinechemUtil.getChemicalTypeFromBucket(itemStack));
		for (PotionChemical decayedChemical : decayedChemicals) {
			decayedChemical.amount *= 8 * itemStack.getCount();
		}
		ItemStack[] items = toItemStacks(decayedChemicals);
		for (int i = 1; i < items.length; i++) {
			ItemStack stack = MinechemUtil.addItemToInventory(inventory, items[i]);
			if (!stack.isEmpty()) {
				MinechemUtil.throwItemStack(world, itemStack, x, y, z);
			}
		}

		Item item = items[0].getItem();
		if (item instanceof ItemMolecule) {
			//itemStack.setItem(MinechemBucketHandler.getInstance().buckets.get(ModFluids.FLUID_MOLECULE_BLOCKS.get(ModFluids.FLUID_MOLECULES.get(MoleculeItem.getMolecule(items[0])))));
			itemStack = MinechemUtil.getBucketForFluid(ModFluids.FLUID_MOLECULES.get(MinechemUtil.getMolecule(items[0])));
		}
		else if (item instanceof ItemElement) {
			//itemStack.setItem(MinechemBucketHandler.getInstance().buckets.get(ModFluids.FLUID_ELEMENT_BLOCKS.get(ModFluids.FLUID_ELEMENTS.get(ElementItem.getElement(items[0])))));
			itemStack = MinechemUtil.getBucketForFluid(ModFluids.FLUID_ELEMENTS.get(MinechemUtil.getElement(items[0])));
		}
		itemStack.setCount((items[0].getCount() / 8));
		itemStack.setTagCompound(items[0].getTagCompound());

		return ItemElement.initiateRadioactivity(itemStack, world);
	}

	public RadiationUtil handleRadiationMolecule(World world, ItemStack itemStack, IInventory inventory, double x, double y, double z) {
		PotionChemical[] decayedChemicals = getDecayedMolecule(MinechemUtil.getMolecule(itemStack));
		for (PotionChemical decayedChemical : decayedChemicals) {
			decayedChemical.amount *= itemStack.getCount();
		}
		ItemStack[] items = toItemStacks(decayedChemicals);
		for (int i = 1; i < items.length; i++) {
			ItemStack stack = MinechemUtil.addItemToInventory(inventory, items[i]);
			if (!stack.isEmpty()) {
				MinechemUtil.throwItemStack(world, itemStack, x, y, z);
			}
		}
		itemStack = new ItemStack(items[0].getItem(), items[0].getCount(), items[0].getItemDamage());
		itemStack.setTagCompound(items[0].getTagCompound());

		return ItemElement.initiateRadioactivity(itemStack, world);
	}

	private void initDecayedMoleculesPre() {

	}

	private PotionChemical[] getDecayedMolecule(MoleculeEnum molecule) {
		PotionChemical[] chemicals = decayedMoleculesCache.get(molecule);
		if (chemicals == null) {
			Set<PotionChemical> potionChemicalsSet = computDecayMolecule(molecule);
			chemicals = potionChemicalsSet.toArray(new PotionChemical[0]);
			decayedMoleculesCache.put(molecule, chemicals);
		}
		return copyOf(chemicals);
	}

	private ItemStack[] toItemStacks(PotionChemical[] chemicalsArray) {
		List<ItemStack> itemStacks = new ArrayList<>();
		List<PotionChemical> chemicals = new ArrayList<>(Arrays.asList(chemicalsArray));

		while (!chemicals.isEmpty()) {
			int index = chemicals.size() - 1;
			PotionChemical chemical = chemicals.remove(index);

			Item thisType;
			int thisDamage;
			if (chemical instanceof Element) {
				thisType = ModItems.element;
				thisDamage = ((Element) chemical).element.atomicNumber();
			}
			else if (chemical instanceof Molecule) {
				thisType = ModItems.molecule;
				thisDamage = ((Molecule) chemical).molecule.id();
			}
			else {
				continue;
			}

			for (ItemStack itemStack : itemStacks) {
				if (chemical.amount <= 0) {
					break;
				}

				if (itemStack.getItem() == thisType && itemStack.getItemDamage() == thisDamage) {
					int freeSpace = 64 - itemStack.getCount();
					int append = Math.min(freeSpace, chemical.amount);
					chemical.amount -= append;
					itemStack.grow(append);
				}
			}

			if (chemical.amount > 0) {
				itemStacks.add(new ItemStack(thisType, chemical.amount, thisDamage));
			}
		}

		return itemStacks.toArray(new ItemStack[0]);
	}

	private Set<PotionChemical> computDecayMolecule(MoleculeEnum molecule) {
		List<PotionChemical> chemicals = molecule.components();
		Set<PotionChemical> outChemicals = new HashSet<>();

		if (molecule.radioactivity() == RadiationEnum.stable) {
			outChemicals.add(new Molecule(molecule));
			return outChemicals;
		}

		for (PotionChemical chemical1 : chemicals) {
			PotionChemical chemical = chemical1.copy();

			if (chemical instanceof Element) {
				Element element = (Element) chemical;
				if (element.element.radioactivity() != RadiationEnum.stable) {
					element.element = ElementEnum.getByID(element.element.atomicNumber() - 1);
				}
			}
			else if (chemical instanceof Molecule) {
				Molecule molecule2 = (Molecule) chemical;
				if (molecule2.molecule.radioactivity() != RadiationEnum.stable) {
					PotionChemical[] chemicals2 = getDecayedMolecule(molecule2.molecule);
					Collections.addAll(outChemicals, chemicals2);
					chemical = null;
				}
			}

			if (chemical != null) {
				outChemicals.add(chemical);
			}
		}

		return outChemicals;
	}

	private PotionChemical[] copyOf(PotionChemical[] a) {
		PotionChemical[] b = new PotionChemical[a.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = a[i].clone();
		}
		return b;
	}
}
