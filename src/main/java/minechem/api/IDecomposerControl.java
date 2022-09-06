package minechem.api;

import net.minecraft.item.ItemStack;

/**
 * To be implemented on any items that should return a non-standard amount of resources when decomposed
 *
 * @author hilburn
 */
public interface IDecomposerControl
{
    /**
     * @return Returns the Decomposition multiplier for {@code itemStack} or 0 to disable decomposition
     */
    double getDecomposerMultiplier(ItemStack itemStack);
}
