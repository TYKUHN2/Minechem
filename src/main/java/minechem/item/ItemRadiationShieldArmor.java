package minechem.item;

import java.util.List;

import javax.annotation.Nullable;

import minechem.api.IRadiationShield;
import minechem.init.ModCreativeTab;
import minechem.init.ModGlobals.Textures;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRadiationShieldArmor extends ItemArmor implements IRadiationShield {

	private final float radiationShieldFactor;
	//private String textureFile;

	public ItemRadiationShieldArmor(final int id, final int part, final float radiationShieldFactor, final String texture) {
		super(ArmorMaterial.CHAIN, 2, EntityEquipmentSlot.values()[part]);
		this.radiationShieldFactor = radiationShieldFactor;
		setUnlocalizedName("itemArmorRadiationShield");
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		//textureFile = texture;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack itemstack, @Nullable final World world, final List<String> list, final ITooltipFlag flagIn) {
		final int percentile = (int) (radiationShieldFactor * 100);
		final String info = String.format("%d%% Radiation Shielding", percentile);
		list.add(info);
	}

	@Override
	public float getRadiationReductionFactor(final int baseDamage, final ItemStack itemstack, final EntityPlayer player) {
		itemstack.damageItem(baseDamage / 4, player);
		return radiationShieldFactor;
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final EntityEquipmentSlot slot, final String type) {
		return Textures.Model.HAZMAT;
	}

}
