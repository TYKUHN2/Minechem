package minechem.init;

import java.util.List;

import com.google.common.collect.Lists;

import minechem.api.ICustomRenderer;
import minechem.block.*;
import minechem.block.multiblock.*;
import minechem.block.multiblock.tile.*;
import minechem.block.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ModBlocks {

	public static List<Block> BLOCK_LIST = Lists.newArrayList();

	public static Block decomposer;
	public static Block microscope;
	public static Block synthesis;
	public static Block ghostBlock;
	public static Block blueprintProjector;
	public static Block reactor_core;
	public static Block reactor_wall;
	public static Block tungsten_plating;
	public static Block printer;
	public static Block leadChest;

	public static Block uranium;
	public static Material materialGhost = new MaterialTransparent(MapColor.AIR);

	public static void registerBlocks() {

		// Decomposer
		BLOCK_LIST.add(decomposer = new BlockDecomposer());
		GameRegistry.registerTileEntity(TileDecomposer.class, new ResourceLocation(ModGlobals.MODID, decomposer.getUnlocalizedName()));

		// Microscope.
		BLOCK_LIST.add(microscope = new BlockMicroscope());
		GameRegistry.registerTileEntity(TileMicroscope.class, new ResourceLocation(ModGlobals.MODID, microscope.getUnlocalizedName()));

		// Chemical Synthesis Machine.
		BLOCK_LIST.add(synthesis = new BlockSynthesis());
		GameRegistry.registerTileEntity(TileSynthesis.class, new ResourceLocation(ModGlobals.MODID, synthesis.getUnlocalizedName()));

		// Fusion Reactor.
		BLOCK_LIST.add(reactor_core = new BlockReactorCore());
		GameRegistry.registerTileEntity(TileReactorCore.class, new ResourceLocation(ModGlobals.MODID, reactor_core.getUnlocalizedName()));
		GameRegistry.registerTileEntity(TileFusionCore.class, new ResourceLocation(ModGlobals.MODID, reactor_core.getUnlocalizedName() + "_fusion"));
		GameRegistry.registerTileEntity(TileFissionCore.class, new ResourceLocation(ModGlobals.MODID, reactor_core.getUnlocalizedName() + "_fission"));

		BLOCK_LIST.add(reactor_wall = new BlockReactorWall());
		BLOCK_LIST.add(tungsten_plating = new BlockTungsten());

		// Ghost Block.
		ghostBlock = new BlockGhost();
		GameRegistry.registerTileEntity(TileGhostBlock.class, new ResourceLocation(ModGlobals.MODID, ghostBlock.getUnlocalizedName()));

		// Blueprint Projector.
		BLOCK_LIST.add(blueprintProjector = new BlockBlueprintProjector());
		GameRegistry.registerTileEntity(TileBlueprintProjector.class, new ResourceLocation(ModGlobals.MODID, blueprintProjector.getUnlocalizedName()));

		// Uranium Ore (World Gen).
		BLOCK_LIST.add(uranium = new BlockUraniumOre());
		OreDictionary.registerOre("oreUranium", new ItemStack(uranium));

		// Leaded Chest (for storing radioactive isotopes).
		BLOCK_LIST.add(leadChest = new BlockLeadedChest());
		GameRegistry.registerTileEntity(TileLeadedChest.class, leadChest.getRegistryName());

		// Tile Entity Proxy.
		GameRegistry.registerTileEntity(TileEntityProxy.class, new ResourceLocation(ModGlobals.MODID, "tileEntityProxy"));

		GameRegistry.registerTileEntity(TileRadioactiveFluid.class, new ResourceLocation(ModGlobals.MODID, "tileEntityRadiationFluid"));

	}

	@SideOnly(Side.CLIENT)
	public static void registerRenderers() {
		for (final Block block : BLOCK_LIST) {
			if (block instanceof ICustomRenderer) {
				((ICustomRenderer) block).registerRenderer();
			}
		}
	}

}
