package minechem.init;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModWorldGen implements IWorldGenerator {

	public static void init() {
		if (ModConfig.generateOre && ModConfig.worldGenDimWhitelist.length > 0) {
			GameRegistry.registerWorldGenerator(new ModWorldGen(), 0);
		}
	}

	@Override
	public void generate(final Random random, final int chunkX, final int chunkZ, final World world, final IChunkGenerator chunkGenerator, final IChunkProvider chunkProvider) {
		if (ModConfig.generateOre) {
			for (final int dimID : ModConfig.worldGenDimWhitelist) {
				if (world.provider.getDimension() == dimID) {
					for (int k = 0; k <= ModConfig.UraniumOreDensity; k++) {
						final int firstBlockXCoord = 16 * chunkX + random.nextInt(16);
						final int firstBlockYCoord = random.nextInt(50);
						final int firstBlockZCoord = 16 * chunkZ + random.nextInt(16);
						final int oreCount = random.nextInt(ModConfig.UraniumOreClusterSize + 10);
						new WorldGenMinable(ModBlocks.uranium.getBlockState().getBaseState(), oreCount).generate(world, random, new BlockPos(firstBlockXCoord, firstBlockYCoord, firstBlockZCoord));
					}
				}
			}
		}
	}

}
