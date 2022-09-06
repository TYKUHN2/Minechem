package minechem.item.polytool.types;

import java.util.List;

import minechem.item.element.ElementEnum;
import minechem.item.polytool.PolytoolUpgradeType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;

public class PolytoolTypeChlorine extends PolytoolUpgradeType {
	@Override
	public void hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase player) {
		List<EntityLivingBase> targets = target.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(target.posX - power, target.posY - power, target.posZ - power, target.posX + power, target.posY + power, target.posZ + power));
        for (EntityLivingBase entity : targets) {
            entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 1));

        }
	}

	@Override
	public ElementEnum getElement() {

		return ElementEnum.Cl;
	}

	@Override
	public String getDescription() {
		return "Area of Effect poison";
	}

}
