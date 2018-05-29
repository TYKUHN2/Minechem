package minechem.item.polytool.types;

import java.util.Iterator;
import java.util.List;

import minechem.item.element.ElementEnum;
import minechem.item.polytool.PolytoolUpgradeType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;

public class PolytoolTypeMagnesium extends PolytoolUpgradeType {
	@Override
	public void hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase player) {
		List<EntityLivingBase> targets = target.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(target.posX - power, target.posY - power, target.posZ - power, target.posX + power, target.posY + power, target.posZ + power));
		Iterator<EntityLivingBase> iter = targets.iterator();
		while (iter.hasNext()) {
			EntityLivingBase entity = iter.next();
			entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200, 1));
		}
	}

	@Override
	public ElementEnum getElement() {
		return ElementEnum.Mg;
	}

	@Override
	public String getDescription() {
		return "Blinds nearby entities with flashbangs";
	}

}
