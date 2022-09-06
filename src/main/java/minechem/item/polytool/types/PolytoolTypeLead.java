package minechem.item.polytool.types;

import java.util.List;

import minechem.item.element.ElementEnum;
import minechem.item.polytool.PolytoolUpgradeType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

public class PolytoolTypeLead extends PolytoolUpgradeType {
	@Override
	public void hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase player) {
		if (!target.world.isRemote) {
			List<EntityLivingBase> targets = target.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(target.posX - power * 3, target.posY - power * 3, target.posZ - power * 3, target.posX + power * 3, target.posY + power * 3, target.posZ + power * 3));
            for (EntityLivingBase entity : targets) {
                if (entity != player) {
                    entity.motionY = -50;
                }
            }
		}
	}

	@Override
	public ElementEnum getElement() {
		return ElementEnum.Pb;
	}

	@Override
	public String getDescription() {
		return "Sends nearby entities flying to the ground";
	}

}
