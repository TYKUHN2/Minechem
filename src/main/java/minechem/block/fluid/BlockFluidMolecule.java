package minechem.block.fluid;

import minechem.fluid.FluidMinechem;
import minechem.fluid.FluidMolecule;
import minechem.init.ModConfig;
import minechem.potion.PharmacologyEffectRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFluidMolecule extends BlockFluidMinechem {

	public BlockFluidMolecule(FluidMinechem fluid, Material material) {
		super(fluid, material);
	}

	public BlockFluidMolecule(FluidMinechem fluid) {
		super(fluid, materialFluidBlock);
		//this.setRegistryName(fluidName);
		setUnlocalizedName(fluidName);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityLivingBase && ModConfig.fluidEffects && getFluid() instanceof FluidMolecule) {
			PharmacologyEffectRegistry.applyEffect(((FluidMolecule) getFluid()).molecule, (EntityLivingBase) entity);

			int power = ((FluidMolecule) getFluid()).molecule.radioactivity().ordinal();
			if (power > 0) {
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.POISON, 10, power - 1));
			}
		}
	}
}
