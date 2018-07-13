package minechem.handler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import minechem.api.INoDecay;
import minechem.api.IRadiationShield;
import minechem.api.RadiationInfo;
import minechem.event.RadiationDecayEvent;
import minechem.fluid.FluidMinechem;
import minechem.init.ModFluids;
import minechem.init.ModItems;
import minechem.item.ItemElement;
import minechem.item.MinechemChemicalType;
import minechem.item.element.ElementEnum;
import minechem.radiation.RadiationEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.UniversalBucket;

public class HandlerElementDecay {

	private static HandlerElementDecay instance = new HandlerElementDecay();

	public static HandlerElementDecay getInstance() {
		return instance == null ? new HandlerElementDecay() : instance;
	}

	public void update(EntityPlayer player) {
		Container openContainer = player.openContainer;
		if (openContainer != null) {
			if (openContainer instanceof INoDecay) {
				updateContainerNoDecay(player, openContainer, player.inventory);
			}
			else {
				updateContainer(player, openContainer, player.inventory);
			}
		}
		else {
			updateContainer(player, player.inventoryContainer, player.inventory);
		}
	}

	private void updateContainerNoDecay(EntityPlayer player, Container openContainer, IInventory inventory) {
		INoDecay container = (INoDecay) openContainer;
		List<ItemStack> itemstacks = container.getStorageInventory();
		if (itemstacks != null) {
			for (ItemStack itemstack : itemstacks) {
				if (!itemstack.isEmpty() && (itemstack.getItem() == ModItems.molecule || itemstack.getItem() == ModItems.element/* || itemstack.getItem() instanceof MinechemBucketItem*/) && RadiationInfo.getRadioactivity(itemstack) != RadiationEnum.stable) {
					RadiationInfo radiationInfo = ItemElement.getRadiationInfo(itemstack, player.world);
					radiationInfo.decayStarted += player.world.getTotalWorldTime() - radiationInfo.lastDecayUpdate;
					radiationInfo.lastDecayUpdate = player.world.getTotalWorldTime();
					RadiationInfo.setRadiationInfo(radiationInfo, itemstack);
				}
			}
		}
		List<ItemStack> playerStacks = container.getPlayerInventory();
		if (playerStacks != null) {
			updateRadiationOnItems(player.world, player, openContainer, inventory, playerStacks);
		}
	}

	private void updateContainer(EntityPlayer player, Container container, IInventory inventory) {
		List<ItemStack> itemstacks = container.getInventory();
		updateRadiationOnItems(player.world, player, container, inventory, itemstacks);
	}

	private void updateRadiationOnItems(World world, EntityPlayer player, Container container, IInventory inventory, List<ItemStack> itemstacks) {
		updateRadiationOnItems(world, inventory, itemstacks, player, container, player.posX, player.posY, player.posZ);
	}

	private void updateRadiationOnItems(World world, IInventory inventory, List<ItemStack> itemstacks, EntityPlayer player, Container container, double posX, double posY, double posZ) {
		for (int i = 0; i < itemstacks.size(); i++) {
			ItemStack itemstack = itemstacks.get(i);
			if (itemstack != null) {
				RadiationEnum radiation = null;
				Item item = itemstack.getItem();
				if (item == ModItems.element) {
					radiation = RadiationInfo.getRadioactivity(itemstack);
				}
				else if (item == ModItems.molecule) {
					radiation = MinechemUtil.getMolecule(itemstack).radioactivity();
				}
				/*
				else if (item instanceof MinechemBucketItem) {
					radiation = ((MinechemBucketItem) item).chemical.radioactivity();
				}
				*/

				if (radiation != null && radiation != RadiationEnum.stable) {
					Long time = world.getTotalWorldTime() - ItemElement.getRadiationInfo(itemstack, world).decayStarted;
					ItemStack before = itemstack.copy();
					int damage = updateRadiation(world, itemstack, inventory, posX, posY, posZ);
					ItemStack after = itemstack.copy();
					if (damage > 0) {
						IInventory decayInventory = (container == null) ? inventory : container.getSlot(i).inventory;
						MinecraftForge.EVENT_BUS.post(new RadiationDecayEvent(decayInventory, damage, time, before, after, player));
						if (container != null && player != null) {
							applyRadiationDamage(player, container, damage);
						}
					}
				}
			}
		}
	}

	private void applyRadiationDamage(EntityPlayer player, Container container, int damage) {
		List<Float> reductions = new ArrayList<Float>();
		if (container instanceof IRadiationShield) {
			float reduction = ((IRadiationShield) container).getRadiationReductionFactor(damage, null, player);
			reductions.add(reduction);
		}
		for (ItemStack armour : player.inventory.armorInventory) {
			if (armour != null && armour.getItem() instanceof IRadiationShield) {
				float reduction = ((IRadiationShield) armour.getItem()).getRadiationReductionFactor(damage, armour, player);
				reductions.add(reduction);
			}
		}
		float totalReductionFactor = 1;
		for (float reduction : reductions) {
			totalReductionFactor -= reduction;
		}
		if (totalReductionFactor < 0) {
			totalReductionFactor = 0;
		}
		damage = Math.round(damage * totalReductionFactor);
		player.attackEntityFrom(DamageSource.GENERIC, damage);
	}

	private int updateRadiation(World world, ItemStack element, IInventory inventory, double x, double y, double z) {
		RadiationInfo radiationInfo = ItemElement.getRadiationInfo(element, world);
		int dimensionID = world.provider.getDimension();
		if (dimensionID != radiationInfo.dimensionID && radiationInfo.isRadioactive()) {
			radiationInfo.dimensionID = dimensionID;
			RadiationInfo.setRadiationInfo(radiationInfo, element);
			return 0;
		}
		else {
			long currentTime = world.getTotalWorldTime();
			return decayElement(element, radiationInfo, currentTime, world, inventory, x, y, z);
		}
	}

	private int decayElement(@Nonnull ItemStack element, RadiationInfo radiationInfo, long currentTime, World world, IInventory inventory, double x, double y, double z) {
		if (element.isEmpty() || element.getCount() == 0) {
			radiationInfo.decayStarted += currentTime - radiationInfo.lastDecayUpdate;
			radiationInfo.lastDecayUpdate = currentTime;
			RadiationInfo.setRadiationInfo(radiationInfo, element);
			return 0;
		}
		radiationInfo.lastDecayUpdate = currentTime;
		long lifeTime = currentTime - radiationInfo.decayStarted - radiationInfo.radioactivity.getLife();
		if (lifeTime > 0) {
			int damage = radiationInfo.radioactivity.getDamage() * element.getCount();
			Item item = element.getItem();
			if (item == ModItems.element) {
				radiationInfo = ItemElement.decay(element, world);
			}
			else if (item == ModItems.molecule) {
				radiationInfo = HandlerMoleculeDecay.getInstance().handleRadiationMolecule(world, element, inventory, x, y, z);
			}
			else if (item instanceof UniversalBucket) {
				Fluid fluid = ((UniversalBucket) item).getFluid(element).getFluid();
				if (fluid instanceof FluidMinechem) {
					MinechemChemicalType type = MinechemUtil.getChemicalTypeFromBucket(element);
					if (type instanceof ElementEnum) {
						//element.setItem(MinechemBucketHandler.getInstance().buckets.get(ModFluids.FLUID_ELEMENT_BLOCKS.get(ModFluids.FLUID_ELEMENTS.get(ElementEnum.getByID(((ElementEnum) type).atomicNumber())))));
						element = MinechemUtil.getBucketForFluid(ModFluids.FLUID_ELEMENTS.get(ElementEnum.getByID(((ElementEnum) type).atomicNumber())));
						radiationInfo = ItemElement.initiateRadioactivity(element, world);
					}
					else {
						radiationInfo = HandlerMoleculeDecay.getInstance().handleRadiationMoleculeBucket(world, element, inventory, x, y, z);
					}
				}
			}
			/*
			else if (item instanceof MinechemBucketItem) {
				MinechemChemicalType type = ((MinechemBucketItem) item).chemical;
				if (type instanceof ElementEnum) {
					element.setItem(MinechemBucketHandler.getInstance().buckets.get(ModFluids.FLUID_ELEMENT_BLOCKS.get(ModFluids.FLUID_ELEMENTS.get(ElementEnum.getByID(((ElementEnum) type).atomicNumber())))));
					radiationInfo = ElementItem.initiateRadioactivity(element, world);
				}
				else {
					radiationInfo = RadiationMoleculeHandler.getInstance().handleRadiationMoleculeBucket(world, element, inventory, x, y, z);
				}
			}
			*/
			RadiationInfo.setRadiationInfo(radiationInfo, element);
			return damage;
		}
		RadiationInfo.setRadiationInfo(radiationInfo, element);
		return 0;
	}

}
