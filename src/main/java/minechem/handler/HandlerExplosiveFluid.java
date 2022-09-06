package minechem.handler;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import minechem.init.ModLogger;
import minechem.item.MinechemChemicalType;
import minechem.item.molecule.MoleculeEnum;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class HandlerExplosiveFluid
{

    private static HandlerExplosiveFluid instance;

    public static HandlerExplosiveFluid getInstance()
    {
        if (instance == null)
        {
            instance = new HandlerExplosiveFluid();
        }
        return instance;
    }

    private final Map<MinechemChemicalType, Float> explosiveFluids = new LinkedHashMap<>();
    private final Set<Block> fireSource = new LinkedHashSet<>();

    public HandlerExplosiveFluid()
    {
        init();
    }

    public boolean existingFireSource(Block block)
    {
        return fireSource.contains(block);
    }

    public void addFireSource(Block block)
    {
        fireSource.add(block);
        ModLogger.debug("Added fire source block:" + block);
    }

    public void removeFireSource(Block block)
    {
        fireSource.remove(block);
        ModLogger.debug("Removed fire source block:" + block);
    }

    public void addExplosiveFluid(MinechemChemicalType type, float level)
    {
        explosiveFluids.put(type, level);
        ModLogger.debug("Added explosive fluid:" + type);
    }

    public void removeExplosiveFluid(MinechemChemicalType type)
    {
        explosiveFluids.remove(type);
        ModLogger.debug("Removed explosive fluid:" + type);
    }

    public float getExplosiveFluid(MinechemChemicalType type)
    {
        Float level = explosiveFluids.get(type);
        if (level == null)
        {
            return Float.NaN;
        } else
        {
            return level;
        }
    }

    private void init()
    {
        addFireSource(Blocks.FIRE);
        addFireSource(Blocks.LAVA);
        addFireSource(Blocks.FLOWING_LAVA);

        addExplosiveFluid(MoleculeEnum.tnt, 2f);
    }
}
