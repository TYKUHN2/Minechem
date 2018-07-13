package minechem.init;

import java.util.List;

import com.google.common.collect.Lists;

import minechem.api.ICustomRenderer;
import minechem.block.BlockBlueprintProjector;
import minechem.block.BlockDecomposer;
import minechem.block.BlockLeadedChest;
import minechem.block.BlockMicroscope;
import minechem.block.BlockSynthesis;
import minechem.block.BlockUraniumOre;
import minechem.block.tile.TileRadioactiveFluid;
import minechem.block.tile.TileBlueprintProjector;
import minechem.block.tile.TileDecomposer;
import minechem.block.tile.TileEntityProxy;
import minechem.block.tile.TileLeadedChest;
import minechem.block.tile.TileMicroscope;
import minechem.block.tile.TileSynthesis;
import minechem.tileentity.multiblock.fission.FissionTileEntity;
import minechem.tileentity.multiblock.fusion.FusionBlock;
import minechem.tileentity.multiblock.fusion.FusionTileEntity;
import minechem.tileentity.multiblock.ghostblock.GhostBlock;
import minechem.tileentity.multiblock.ghostblock.GhostBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.item.ItemStack;
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
	public static Block FUSION;
	public static Block printer;
	public static Block leadChest;

	public static Block uranium;
	public static Material materialGhost = new MaterialTransparent(MapColor.AIR);

	public static void registerBlocks() {

		// Decomposer
		BLOCK_LIST.add(decomposer = new BlockDecomposer());
		GameRegistry.registerTileEntity(TileDecomposer.class, decomposer.getUnlocalizedName());

		// Microscope.
		BLOCK_LIST.add(microscope = new BlockMicroscope());
		GameRegistry.registerTileEntity(TileMicroscope.class, microscope.getUnlocalizedName());

		// Chemical Synthesis Machine.
		BLOCK_LIST.add(synthesis = new BlockSynthesis());
		GameRegistry.registerTileEntity(TileSynthesis.class, synthesis.getUnlocalizedName());

		// Fusion Reactor.
		BLOCK_LIST.add(FUSION = new FusionBlock());
		GameRegistry.registerTileEntity(FusionTileEntity.class, FUSION.getUnlocalizedName());

		// Ghost Block.
		ghostBlock = new GhostBlock();
		GameRegistry.registerTileEntity(GhostBlockTileEntity.class, ghostBlock.getUnlocalizedName());

		// Blueprint Projector.
		BLOCK_LIST.add(blueprintProjector = new BlockBlueprintProjector());
		GameRegistry.registerTileEntity(TileBlueprintProjector.class, blueprintProjector.getUnlocalizedName());

		// Uranium Ore (World Gen).
		BLOCK_LIST.add(uranium = new BlockUraniumOre());
		OreDictionary.registerOre("oreUranium", new ItemStack(uranium));

		// Leaded Chest (for storing radioactive isotopes).
		BLOCK_LIST.add(leadChest = new BlockLeadedChest());
		GameRegistry.registerTileEntity(TileLeadedChest.class, leadChest.getUnlocalizedName());

		// Fission Reactor.
		GameRegistry.registerTileEntity(FissionTileEntity.class, "fissionReactor");

		// Tile Entity Proxy.
		GameRegistry.registerTileEntity(TileEntityProxy.class, "minchem.tileEntityProxy");

		GameRegistry.registerTileEntity(TileRadioactiveFluid.class, "minechem.tileEntityRadiationFluid");

	}

	@SideOnly(Side.CLIENT)
	public static void registerRenderers() {
		for (Block block : BLOCK_LIST) {
			if (block instanceof ICustomRenderer) {
				((ICustomRenderer) block).registerRenderer();
			}
		}
	}

}
