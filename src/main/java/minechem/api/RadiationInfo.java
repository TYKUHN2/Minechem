package minechem.api;

import minechem.init.ModItems;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.radiation.RadiationEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.UniversalBucket;

public class RadiationInfo implements Cloneable {

	public long decayStarted;
	public long lastDecayUpdate;
	public int radiationDamage;
	public int dimensionID;
	public ItemStack itemstack;
	public RadiationEnum radioactivity;

	public RadiationInfo(ItemStack itemstack, long decayStarted, long lastDecayUpdate, int dimensionID, RadiationEnum radioactivity) {
		this.itemstack = itemstack;
		this.decayStarted = decayStarted;
		this.dimensionID = dimensionID;
		this.lastDecayUpdate = lastDecayUpdate;
		this.radioactivity = radioactivity;
	}

	public RadiationInfo(ItemStack itemstack, RadiationEnum radioactivity) {
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
	public RadiationInfo clone() {
		return new RadiationInfo(itemstack.copy(), decayStarted, lastDecayUpdate, dimensionID, radioactivity);
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

	public static void setRadiationInfo(RadiationInfo radiationInfo, ItemStack itemStack) {
		NBTTagCompound stackTag = itemStack.getTagCompound();
		if (stackTag == null) {
			stackTag = new NBTTagCompound();
		}
		stackTag.setLong("lastUpdate", radiationInfo.lastDecayUpdate);
		stackTag.setLong("decayStart", radiationInfo.decayStarted);
		stackTag.setInteger("dimensionID", radiationInfo.dimensionID);
		itemStack.setTagCompound(stackTag);
	}

}
