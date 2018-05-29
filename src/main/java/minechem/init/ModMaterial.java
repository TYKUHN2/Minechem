package minechem.init;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;

/**
 * @author p455w0rd
 *
 */
public class ModMaterial {

	public static final Material FLUID = new MaterialMinechemFluid();

	public static class MaterialMinechemFluid extends MaterialLiquid {

		public MaterialMinechemFluid() {
			super(MapColor.WATER);
			setNoPushMobility();
		}

	}

}
