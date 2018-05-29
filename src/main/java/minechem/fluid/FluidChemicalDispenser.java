package minechem.fluid;

import java.util.Set;

import minechem.api.RadiationInfo;
import minechem.block.tile.RadiationFluidTileEntity;
import minechem.init.ModFluids;
import minechem.init.ModItems;
import minechem.item.ItemElement;
import minechem.item.ItemMolecule;
import minechem.item.MinechemChemicalType;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidChemicalDispenser implements IBehaviorDispenseItem {

	public static void init() {
		FluidChemicalDispenser dispenser = new FluidChemicalDispenser();
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.element, dispenser);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.molecule, dispenser);
	}

	@Override
	public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack) {
		IPosition position = BlockDispenser.getDispensePosition(blockSource);
		World world = blockSource.getWorld();
		BlockPos pos = new BlockPos(position.getX(), position.getY(), position.getZ());
		TileEntity inventoryTile = blockSource.getBlockTileEntity();

		if (itemStack.getItem() instanceof ItemElement && itemStack.getItemDamage() != 0) {
			Block frontBlock = world.getBlockState(pos).getBlock();
			MinechemChemicalType chemical = MinechemUtil.getChemical(frontBlock);

			if (chemical != null && MinechemUtil.canDrain(world, frontBlock, pos.getX(), pos.getY(), pos.getZ())) {
				ItemStack stack = MinechemUtil.createItemStack(chemical, 8);

				if (!stack.isEmpty()) {
					if (itemStack.getCount() >= 8) {
						itemStack.shrink(8);
					}
					else {
						if (inventoryTile instanceof IInventory) {
							int needs = 8 - itemStack.getCount();
							IInventory inventory = (IInventory) inventoryTile;
							Set<ItemStack> otherTubes = MinechemUtil.findItemStacks(inventory, ModItems.element, 0);
							int free = 0;
							otherTubes.remove(itemStack);
							for (ItemStack emptyStack : otherTubes) {
								free += emptyStack.getCount();
							}
							if (free < needs) {
								return itemStack;
							}
							itemStack.setCount(0);

							for (ItemStack emptyStack : otherTubes) {
								if (emptyStack.getCount() >= needs) {
									emptyStack.shrink(needs);
									needs = 0;
								}
								else {
									needs -= emptyStack.getCount();
									emptyStack.setCount(0);
								}

								if (emptyStack.getCount() <= 0) {
									MinechemUtil.removeStackInInventory(inventory, emptyStack);
								}

								if (needs == 0) {
									break;
								}
							}
						}
					}

					TileEntity tile = world.getTileEntity(pos);
					if (tile instanceof RadiationFluidTileEntity && ((RadiationFluidTileEntity) tile).info != null) {
						RadiationInfo.setRadiationInfo(((RadiationFluidTileEntity) tile).info, stack);
					}
					world.setBlockToAir(pos);

					if (inventoryTile instanceof IInventory) {
						stack = MinechemUtil.addItemToInventory((IInventory) inventoryTile, stack);
					}
					MinechemUtil.throwItemStack(world, stack, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}
		else {
			IInventory inventory;
			Block block;
			if (inventoryTile instanceof IInventory) {
				inventory = (IInventory) inventoryTile;
			}
			else {
				return itemStack;
			}
			if (itemStack.getItem() instanceof ItemElement) {
				ElementEnum element = MinechemUtil.getElement(itemStack);
				block = ModFluids.FLUID_ELEMENT_BLOCKS.get(ModFluids.FLUID_ELEMENTS.get(element));
			}
			else if (itemStack.getItem() instanceof ItemMolecule) {
				MoleculeEnum molecule = MoleculeEnum.getById(itemStack.getItemDamage());
				block = ModFluids.FLUID_MOLECULE_BLOCKS.get(ModFluids.FLUID_MOLECULES.get(molecule));
			}
			else {
				return itemStack;
			}

			if (!world.isAirBlock(pos) && !world.getBlockState(pos).getMaterial().isSolid()) {
				world.destroyBlock(pos, true);
				world.setBlockToAir(pos);
			}

			if (world.isAirBlock(pos)) {
				RadiationInfo radioactivity = ItemElement.getRadiationInfo(itemStack, world);
				long worldtime = world.getTotalWorldTime();
				long leftTime = radioactivity.radioactivity.getLife() - (worldtime - radioactivity.decayStarted);

				if (itemStack.getCount() >= 8) {
					itemStack.shrink(8);
				}
				else {
					int needs = 8 - itemStack.getCount();
					itemStack.setCount(0);
					Set<ItemStack> otherItemsStacks = MinechemUtil.findItemStacks(inventory, itemStack.getItem(), itemStack.getItemDamage());
					otherItemsStacks.remove(itemStack);
					int free = 0;
					for (ItemStack stack : otherItemsStacks) {
						free += stack.getCount();
					}
					if (free < needs) {
						return itemStack;
					}

					for (ItemStack stack : otherItemsStacks) {
						RadiationInfo anotherRadiation = ItemElement.getRadiationInfo(stack, world);
						long anotherLeft = anotherRadiation.radioactivity.getLife() - (worldtime - anotherRadiation.decayStarted);
						if (anotherLeft < leftTime) {
							radioactivity = anotherRadiation;
							leftTime = anotherLeft;
						}

						if (stack.getCount() >= needs) {
							stack.shrink(needs);
							needs = 0;
						}
						else {
							needs -= stack.getCount();
							stack.setCount(0);
						}

						if (stack.getCount() <= 0) {
							MinechemUtil.removeStackInInventory(inventory, stack);
						}

						if (needs == 0) {
							break;
						}
					}
				}
				ItemStack empties = MinechemUtil.addItemToInventory(inventory, new ItemStack(ModItems.element, 8, 0));
				MinechemUtil.throwItemStack(world, empties, pos.getX(), pos.getY(), pos.getZ());

				world.setBlockState(pos, block.getStateFromMeta(0), 3);
				TileEntity tile = world.getTileEntity(pos);
				if (radioactivity.isRadioactive() && tile instanceof RadiationFluidTileEntity) {
					((RadiationFluidTileEntity) tile).info = radioactivity;
				}
			}
			return itemStack;

			//			Block block = null;
			//			RadiationEnum radioactivity = null;
			//			if (itemStack.getItem() instanceof ElementItem)
			//			{
			//				ElementEnum element = ElementItem.getElement(itemStack);
			//				block = FluidHelper.elementsBlocks.get(FluidHelper.elements.get(element));
			//				radioactivity = element.radioactivity();
			//			} else if (itemStack.getItem() instanceof MoleculeItem)
			//			{
			//				MoleculeEnum molecule = MoleculeEnum.getById(itemStack.getItemDamage());
			//				block = FluidHelper.moleculeBlocks.get(FluidHelper.molecules.get(molecule));
			//				radioactivity = molecule.radioactivity();
			//			}
			//
			//			if (!world.isAirBlock(x, y, z) && !world.getBlock(x, y, z).getMaterial().isSolid())
			//			{
			//				world.func_147480_a(x, y, z, true);
			//				world.setBlockToAir(x, y, z);
			//			}
			//
			//			if (world.isAirBlock(x, y, z) && block != null)
			//			{
			//				world.setBlock(x, y, z, block, 0, 3);
			//				--itemStack.stackSize;
			//				TileEntity tile = world.getTileEntity(x, y, z);
			//				if (radioactivity != RadiationEnum.stable && tile instanceof RadiationFluidTileEntity)
			//				{
			//					((RadiationFluidTileEntity) tile).info = ElementItem.getRadiationInfo(itemStack, world);
			//				}
			//				ItemStack elementStack = new ItemStack(MinechemItemsRegistration.element, 1, ElementEnum.heaviestMass);
			//				TileEntity inventoryTile = blockSource.getBlockTileEntity();
			//				if (inventoryTile instanceof IInventory)
			//				{
			//					elementStack = MinechemUtil.addItemToInventory((IInventory) inventoryTile, elementStack);
			//				}
			//				MinechemUtil.throwItemStack(world, elementStack, x, y, z);
			//			}
		}

		return itemStack;
	}
}
