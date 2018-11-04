package minechem.utils;

import minechem.init.ModItems;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.radiation.RadiationEnum;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.UniversalBucket;

public class RadiationUtil implements Cloneable {

	public long decayStarted;
	public long lastDecayUpdate;
	public int radiationDamage;
	public int dimensionID;
	public ItemStack itemstack;
	public RadiationEnum radioactivity;

	public RadiationUtil(ItemStack itemstack, long decayStarted, long lastDecayUpdate, int dimensionID, RadiationEnum radioactivity) {
		this.itemstack = itemstack;
		this.decayStarted = decayStarted;
		this.dimensionID = dimensionID;
		this.lastDecayUpdate = lastDecayUpdate;
		this.radioactivity = radioactivity;
	}

	public RadiationUtil(ItemStack itemstack, RadiationEnum radioactivity) {
		this.itemstack = itemstack;
		this.radioactivity = radioactivity;
		decayStarted = 0;
		dimensionID = 0;
		lastDecayUpdate = 0;
	}

	public boolean isRadioactive() {
		return radioactivity != RadiationEnum.stable;
	}

	@Override
	public RadiationUtil clone() {
		return new RadiationUtil(itemstack.copy(), decayStarted, lastDecayUpdate, dimensionID, radioactivity);
	}

	public static RadiationEnum getRadioactivity(ItemStack itemstack) {
		Item item = itemstack.getItem();
		if (item == ModItems.element) {
			int id = itemstack.getItemDamage();
			ElementEnum element = ElementEnum.getByID(id);
			return id != 0 && element != null ? element.radioactivity() : RadiationEnum.stable;
		}
		else if (item == ModItems.molecule) {
			int id = itemstack.getItemDamage();
			if (id >= MoleculeEnum.molecules.size() || MoleculeEnum.molecules.get(id) == null) {
				return RadiationEnum.stable;
			}
			return MoleculeEnum.molecules.get(id).radioactivity();
		}
		else if (item instanceof UniversalBucket) {
			return MinechemUtil.getChemicalTypeFromBucket(itemstack).radioactivity();
		}
		return RadiationEnum.stable;
	}

	public static void setRadiationInfo(RadiationUtil radiationInfo, ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setLong("lastUpdate", radiationInfo.lastDecayUpdate);
		nbt.setLong("decayStart", radiationInfo.decayStarted);
		nbt.setInteger("dimensionID", radiationInfo.dimensionID);
		stack.setTagCompound(nbt);
	}

	public static ItemStack getStackWithoutRadiation(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt.hasKey("lastUpdate", Constants.NBT.TAG_LONG)) {
				nbt.removeTag("lastUpdate");
			}
			if (nbt.hasKey("decayStart", Constants.NBT.TAG_LONG)) {
				nbt.removeTag("decayStart");
			}
			if (nbt.hasKey("dimensionID", Constants.NBT.TAG_INT)) {
				nbt.removeTag("dimensionID");
			}
			ItemStack returnStack = stack.copy();
			if (nbt.hasNoTags()) {
				returnStack.setTagCompound(null);
			}
			else {
				returnStack.setTagCompound(nbt);
			}
			return returnStack;
		}
		return stack;
	}

}
