package minechem.api;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Please keep in mind that this a CLIENT SIDE
 * ONLY class. It is included on both sides
 * for maintainability.
 *
 * @author p455w0rd
 *
 */
public interface IPotionEffectRenderer {

	@SideOnly(Side.CLIENT)
	public void render(@Nullable Integer level);

}
