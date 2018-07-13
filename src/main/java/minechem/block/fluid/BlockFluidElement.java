package minechem.block.fluid;

import minechem.fluid.FluidElement;
import minechem.fluid.FluidMinechem;
import minechem.init.ModConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFluidElement extends BlockFluidMinechem {

	public BlockFluidElement(FluidMinechem fluid, Material material) {
		super(fluid, material);
	}

	public BlockFluidElement(FluidMinechem fluid) {
		super(fluid, materialFluidBlock);
		setUnlocalizedName(fluidName);
		//this.setRegistryName(fluidName);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityLivingBase && ModConfig.fluidEffects) {
			int power = ((FluidElement) getFluid()).element.radioactivity().ordinal();
			if (power > 0) {
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.POISON, 10, power - 1));
			}
		}
	}

}
