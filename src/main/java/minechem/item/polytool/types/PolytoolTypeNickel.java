package minechem.item.polytool.types;

import java.util.List;

import minechem.item.element.ElementEnum;
import minechem.item.polytool.PolytoolUpgradeType;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class PolytoolTypeNickel extends PolytoolUpgradeType {

	public PolytoolTypeNickel() {
		super();
	}

	@Override
	public void onBlockDestroyed(ItemStack itemStack, World world, Block block, int x, int y, int z, EntityLivingBase player) {
		List<EntityItem> items = player.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(player.posX - power, player.posY - power, player.posZ - power, player.posX + power, player.posY + power, player.posZ + power));

        for (EntityItem entity : items) {
            entity.motionX = -1 * (entity.posX - player.posX);

            entity.motionY = -1 * (entity.posY - player.posY);

            entity.motionZ = -1 * (entity.posZ - player.posZ);
        }
	}

	@Override
	public ElementEnum getElement() {
		return ElementEnum.Ni;
	}

	@Override
	public String getDescription() {
		return "Sucks up nearby items when another block is mined";
	}

}
