package minechem.fluid.reaction;

import java.util.*;

import com.google.common.collect.Lists;

import minechem.block.fluid.BlockFluidMinechem;
import minechem.init.ModFluids;
import minechem.item.MinechemChemicalType;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;

public class ChemicalFluidReactionHandler {

	public static final Map<ChemicalFluidReactionRule, ChemicalFluidReactionOutput> reactionRules = new HashMap<>();
	public static final int FLUIDS_GENERATE_SPACE = 3;

	private static final ChemicalFluidReactionHandler INSTANCE = new ChemicalFluidReactionHandler();

	public static final ChemicalFluidReactionHandler getInstance() {
		return INSTANCE;
	}

	public void checkEntityItem(final World world, final EntityItem entityItem) {
		final ItemStack itemStack = entityItem.getItem();

		if (itemStack.getCount() <= 0) {
			return;
		}

		final Item item = itemStack.getItem();
		MinechemChemicalType chemicalA = null;
		if (item instanceof UniversalBucket) {
			final FluidStack fluidStack = ((UniversalBucket) item).getFluid(itemStack);
			if (fluidStack != null) {
				chemicalA = MinechemUtil.getChemical(fluidStack.getFluid());
			}
		}

		if (chemicalA != null && (world.isMaterialInBB(entityItem.getEntityBoundingBox(), Material.WATER) || world.isMaterialInBB(entityItem.getEntityBoundingBox(), BlockFluidMinechem.materialFluidBlock))) {
			final int x = MathHelper.floor(entityItem.posX);
			final int y = MathHelper.floor(entityItem.posY);
			final int z = MathHelper.floor(entityItem.posZ);
			final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
			final MinechemChemicalType chemicalB = MinechemUtil.getChemical(block);

			if (chemicalB != null) {
				final ChemicalFluidReactionRule rule = new ChemicalFluidReactionRule(chemicalA, chemicalB);
				if (reactionRules.containsKey(rule)) {
					chemicalReaction(world, entityItem, x, y, z, rule, !MinechemUtil.canDrain(world, block, x, y, z));
					itemStack.shrink(1);
					entityItem.setItem(itemStack);
					if (itemStack.getCount() <= 0) {
						world.removeEntity(entityItem);
					}
					MinechemUtil.throwItemStack(world, new ItemStack(Items.BUCKET), x, y, z);
				}
			}

		}
	}

	public static void initReaction() {
		// TODO Add more reaction rules -yushijinhun
		List<MinechemChemicalType> list;

		//H2O+Li==LiOH+H
		list = new ArrayList<>();
		list.add(ElementEnum.H);
		list.add(MoleculeEnum.lithiumHydroxide);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.water, ElementEnum.Li), new ChemicalFluidReactionOutput(list, 0.1f));

		//H2O+Na==NaOH+H
		list = new ArrayList<>();
		list.add(ElementEnum.H);
		list.add(MoleculeEnum.sodiumHydroxide);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.water, ElementEnum.Na), new ChemicalFluidReactionOutput(list, 0.15f));

		//H2O+K==KOH+H
		list = new ArrayList<>();
		list.add(ElementEnum.H);
		list.add(MoleculeEnum.potassiumHydroxide);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.water, ElementEnum.K), new ChemicalFluidReactionOutput(list, 0.2f));

		//H2O+Li==RbOH+H
		list = new ArrayList<>();
		list.add(ElementEnum.H);
		list.add(MoleculeEnum.rubidiumHydroxide);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.water, ElementEnum.Rb), new ChemicalFluidReactionOutput(list, 0.25f));

		//H2O+Cs==CsOH+H
		list = new ArrayList<>();
		list.add(ElementEnum.H);
		list.add(MoleculeEnum.cesiumHydroxide);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.water, ElementEnum.Cs), new ChemicalFluidReactionOutput(list, 0.3f));

		//H2O+Fr==FrOH+H
		list = new ArrayList<>();
		list.add(ElementEnum.H);
		list.add(MoleculeEnum.franciumHydroxide);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.water, ElementEnum.Fr), new ChemicalFluidReactionOutput(list, 0.4f));

		//H2SO4+Cu==CuSO4+2H
		list = new ArrayList<>();
		list.add(ElementEnum.H);
		list.add(ElementEnum.H);
		list.add(MoleculeEnum.lightbluePigment);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.sulfuricAcid, ElementEnum.Cu), new ChemicalFluidReactionOutput(list, 0.1f));

		//H2SO4+S==2SO2+2H
		list = new ArrayList<>();
		list.add(ElementEnum.H);
		list.add(ElementEnum.H);
		list.add(MoleculeEnum.sulfurDioxide);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.sulfuricAcid, ElementEnum.S), new ChemicalFluidReactionOutput(list, 0.1f));

		//H2SO4+H2S==S+SO2+2H2O
		list = new ArrayList<>();
		list.add(ElementEnum.S);
		list.add(MoleculeEnum.sulfurDioxide);
		list.add(MoleculeEnum.water);
		list.add(MoleculeEnum.water);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.sulfuricAcid, MoleculeEnum.hydrogenSulfide), new ChemicalFluidReactionOutput(list, 0.1f));

		//HCl+NaOH==H2O+NaCl
		list = new ArrayList<>();
		list.add(MoleculeEnum.salt);
		list.add(MoleculeEnum.water);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.hcl, MoleculeEnum.sodiumHydroxide), new ChemicalFluidReactionOutput(list, 0.1f));

		//H+Cl==HCl
		list = new ArrayList<>();
		list.add(MoleculeEnum.hcl);
		reactionRules.put(new ChemicalFluidReactionRule(ElementEnum.H, ElementEnum.Cl), new ChemicalFluidReactionOutput(list, 0.1f));

		//NaCl+H2SO4==NaHSO4+HCl
		list = new ArrayList<>();
		list.add(MoleculeEnum.sodiumBisulfate);
		list.add(MoleculeEnum.hcl);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.salt, MoleculeEnum.sulfuricAcid), new ChemicalFluidReactionOutput(list, 0.1f));

		//NaHSO4+NaCl==Na2SO4+2HCl
		list = new ArrayList<>();
		list.add(MoleculeEnum.sodiumSulfate);
		list.add(MoleculeEnum.hcl);
		list.add(MoleculeEnum.hcl);
		reactionRules.put(new ChemicalFluidReactionRule(MoleculeEnum.salt, MoleculeEnum.sodiumBisulfate), new ChemicalFluidReactionOutput(list, 0.1f));
	}

	@SuppressWarnings("deprecation")
	private static void chemicalReaction(final World world, final Entity entity, final int x, final int y, final int z, final ChemicalFluidReactionRule rule, final boolean popFlowingFluid) {
		final ChemicalFluidReactionOutput output = reactionRules.get(rule);
		if (output == null) {
			return;
		}

		if (!Float.isNaN(output.explosionLevel)) {
			world.createExplosion(null, x, y, z, output.explosionLevel, true);
		}

		final int halfSpace = FLUIDS_GENERATE_SPACE / 2;
		final List<List<Vec3i>> availableSpaces = Lists.newArrayListWithExpectedSize(FLUIDS_GENERATE_SPACE);
		for (int i = 0; i < availableSpaces.size(); i++) {
			availableSpaces.set(i, findAvailableSpacesAtCrossSection(world, x, y - halfSpace + i, z, 1));
		}

		final Iterator<MinechemChemicalType> it = output.outputs.iterator();
		while (it.hasNext()) {
			final MinechemChemicalType chemical = it.next();
			final boolean hasFlowingStatus = chemical.roomState().getQuanta() > 2;

			Vec3i coords = null;
			if (!(!hasFlowingStatus && popFlowingFluid)) {
				final boolean isGas = chemical.roomState().isGas();
				if (isGas) {
					for (int i = availableSpaces.size() - 1; i > -1; i--) {
						if (!availableSpaces.get(i).isEmpty()) {
							coords = availableSpaces.get(i).remove(availableSpaces.get(i).size() - 1);
							break;
						}
					}
				}
				else {
					for (int i = 0; i < availableSpaces.size(); i++) {
						if (!availableSpaces.get(i).isEmpty()) {
							coords = availableSpaces.get(i).remove(availableSpaces.get(i).size() - 1);
							break;
						}
					}
				}
			}
			if (coords == null) {
				if (!popFlowingFluid) {
					final ItemStack itemStack = MinechemUtil.createItemStack(chemical, 8);
					MinechemUtil.throwItemStack(world, itemStack, x, y, z);
				}
			}
			else if (!(popFlowingFluid && !hasFlowingStatus)) {
				final int px = coords.getX();
				final int py = coords.getY();
				final int pz = coords.getZ();

				world.destroyBlock(new BlockPos(px, py, pz), true);
				world.setBlockToAir(new BlockPos(px, py, pz));

				Block fluidBlock = null;
				if (chemical instanceof ElementEnum) {
					fluidBlock = ModFluids.FLUID_ELEMENT_BLOCKS.get(ModFluids.FLUID_ELEMENTS.get(chemical));
				}
				else if (chemical instanceof MoleculeEnum) {
					fluidBlock = ModFluids.FLUID_MOLECULE_BLOCKS.get(ModFluids.FLUID_MOLECULES.get(chemical));
				}

				if (fluidBlock != null) {
					world.setBlockState(new BlockPos(px, py, pz), fluidBlock.getStateFromMeta(popFlowingFluid ? 1 : 0), 3);
				}
			}
		}
	}

	public static boolean checkToReact(final Block source, final Block destination, final World world, final int destinationX, final int destinationY, final int destinationZ, final int sourceX, final int sourceY, final int sourceZ) {
		final MinechemChemicalType chemicalA = MinechemUtil.getChemical(source);
		final MinechemChemicalType chemicalB = MinechemUtil.getChemical(destination);
		if (chemicalA != null && chemicalB != null) {
			final ChemicalFluidReactionRule rule = new ChemicalFluidReactionRule(chemicalA, chemicalB);

			if (reactionRules.containsKey(rule)) {
				final boolean flag = !(MinechemUtil.canDrain(world, source, sourceX, sourceY, sourceZ) && MinechemUtil.canDrain(world, destination, destinationX, destinationY, destinationZ));
				world.setBlockToAir(new BlockPos(sourceX, sourceY, sourceZ));
				world.setBlockToAir(new BlockPos(destinationX, destinationY, destinationZ));
				chemicalReaction(world, null, destinationX, destinationY, destinationZ, rule, flag);
				return true;
			}
		}

		return false;
	}

	public static List<Vec3i> findAvailableSpacesAtCrossSection(final World world, final int centerX, final int centerY, final int centerZ, final int size) {
		final List<Vec3i> spaces = new ArrayList<>();
		for (int xOffset = -size; xOffset <= size; xOffset++) {
			for (int zOffset = -size; zOffset <= size; zOffset++) {
				final int x = centerX + xOffset;
				final int z = centerZ + zOffset;

				if (world.isAirBlock(new BlockPos(x, centerY, z)) || !world.getBlockState(new BlockPos(x, centerY, z)).getMaterial().isSolid()) {
					spaces.add(new Vec3i(x, centerY, z));
				}
			}
		}

		return spaces;
	}
}
