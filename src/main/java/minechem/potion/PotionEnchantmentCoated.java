package minechem.potion;

import java.util.HashMap;

import minechem.init.ModGlobals;
import minechem.item.molecule.MoleculeEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class PotionEnchantmentCoated extends Enchantment {

	MoleculeEnum molecule;
	public static final HashMap<MoleculeEnum, PotionEnchantmentCoated> POTION_COATED_REGISTRY = new HashMap<MoleculeEnum, PotionEnchantmentCoated>();

	protected PotionEnchantmentCoated(MoleculeEnum molecule, int id) {
		super(Rarity.COMMON, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.values());
		this.molecule = molecule;
	}

	public void applyEffect(EntityLivingBase entity) {
		PharmacologyEffectRegistry.applyEffect(molecule, entity);
	}

	/**
	 * Returns the minimum level that the enchantment can have.
	 */
	@Override
	public int getMinLevel() {
		return 1;
	}

	/**
	 * Returns the maximum level that the enchantment can have.
	 */
	@Override
	public int getMaxLevel() {
		return 10;
	}

	@Override
	public boolean canApply(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return false;
	}

	@Override
	public String getTranslatedName(int level) {
		String enchantedName = I18n.format("enchantment.level." + level);
		if (I18n.hasKey("minechem.enchantment.coated")) {
			return MinechemUtil.getLocalString(molecule.getUnlocalizedName()) + " " + I18n.format("minechem.enchantment.coated", enchantedName);
		}
		else {
			return MinechemUtil.getLocalString(molecule.getUnlocalizedName()) + " " + enchantedName + " Coated";
		}
	}

	/*
		public static void registerCoatings() {
			for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
				if (molecule != null && PharmacologyEffectRegistry.hasEffect(molecule)) {
					for (Enchantment ench : Enchantment.REGISTRY) {
						if (ench != null) {
							PotionEnchantmentCoated newEnch = new PotionEnchantmentCoated(molecule, Enchantment.getEnchantmentID(ench));
							newEnch.setName(molecule.getUnlocalizedName() + ".coated");
							PotionEnchantmentCoated.POTION_COATED_REGISTRY.put(molecule, newEnch);
							break;
						}
					}
				}
			}
		}
	*/
	public static void registerForge(IForgeRegistry<Enchantment> registry) {
		for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
			if (molecule != null && PharmacologyEffectRegistry.hasEffect(molecule)) {
				for (Enchantment ench : Enchantment.REGISTRY) {
					if (ench != null) {
						PotionEnchantmentCoated newEnch = new PotionEnchantmentCoated(molecule, Enchantment.getEnchantmentID(ench));
						newEnch.setRegistryName(new ResourceLocation(ModGlobals.ID, molecule.getUnlocalizedName() + ".coated"));
						newEnch.setName(molecule.getUnlocalizedName() + ".coated");
						registry.register(newEnch);
						PotionEnchantmentCoated.POTION_COATED_REGISTRY.put(molecule, newEnch);
						break;
					}
				}
			}
		}
	}

}
