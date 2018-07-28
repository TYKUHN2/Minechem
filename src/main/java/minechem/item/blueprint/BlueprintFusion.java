package minechem.item.blueprint;

import minechem.init.ModBlocks;
import net.minecraft.block.state.IBlockState;

public class BlueprintFusion extends MinechemBlueprint {

	private static IBlockState w = air;
	private static IBlockState C = ModBlocks.tungsten_plating.getDefaultState();
	private static IBlockState A = ModBlocks.reactor_wall.getDefaultState();
	private static IBlockState X = ModBlocks.reactor_core.getDefaultState();
	private static IBlockState[][][] structure = {
			{
					{
							w, w, w, w, A, A, A, A, A, w, w, w, w
					}, {
							w, w, A, A, A, A, A, A, A, A, A, w, w
					}, {
							w, A, A, A, C, C, C, C, C, A, A, A, w
					}, {
							w, A, A, C, C, C, C, C, C, C, A, A, w
					}, {
							A, A, C, C, C, C, C, C, C, C, C, A, A
					}, {
							A, A, C, C, C, C, C, C, C, C, C, A, A
					}, {
							A, A, C, C, C, C, C, C, C, C, C, A, A
					}, {
							A, A, C, C, C, C, C, C, C, C, C, A, A
					}, {
							A, A, C, C, C, C, C, C, C, C, C, A, A
					}, {
							w, A, A, C, C, C, C, C, C, C, A, A, w
					}, {
							w, A, A, A, C, C, C, C, C, A, A, A, w
					}, {
							w, w, A, A, A, A, A, A, A, A, A, w, w
					}, {
							w, w, w, w, A, A, A, A, A, w, w, w, w
					}
			}, {
					{
							w, w, w, w, A, A, A, A, A, w, w, w, w
					}, {
							w, w, A, A, C, C, C, C, C, A, A, w, w
					}, {
							w, A, C, C, w, w, w, w, w, C, C, A, w
					}, {
							w, A, C, w, w, w, w, w, w, w, C, A, w
					}, {
							A, C, w, w, w, w, w, w, w, w, w, C, A
					}, {
							A, C, w, w, w, w, C, w, w, w, w, C, A
					}, {
							A, C, w, w, w, C, w, C, w, w, w, C, A
					}, {
							A, C, w, w, w, w, C, w, w, w, w, C, A
					}, {
							A, C, w, w, w, w, w, w, w, w, w, C, A
					}, {
							w, A, C, w, w, w, w, w, w, w, C, A, w
					}, {
							w, A, C, C, w, w, w, w, w, C, C, A, w
					}, {
							w, w, A, A, C, C, C, C, C, A, A, w, w
					}, {
							w, w, w, w, A, A, A, A, A, w, w, w, w
					},
			}, {
					{
							w, w, w, w, A, A, A, A, A, w, w, w, w
					}, {
							w, w, A, A, C, C, C, C, C, A, A, w, w
					}, {
							w, A, C, C, w, w, w, w, w, C, C, A, w
					}, {
							w, A, C, w, w, w, w, w, w, w, C, A, w
					}, {
							A, C, w, w, w, w, w, w, w, w, w, C, A
					}, {
							A, C, w, w, w, w, C, w, w, w, w, C, A
					}, {
							A, C, w, w, w, C, X, C, w, w, w, C, A
					}, {
							A, C, w, w, w, w, C, w, w, w, w, C, A
					}, {
							A, C, w, w, w, w, w, w, w, w, w, C, A
					}, {
							w, A, C, w, w, w, w, w, w, w, C, A, w
					}, {
							w, A, C, C, w, w, w, w, w, C, C, A, w
					}, {
							w, w, A, A, C, C, C, C, C, A, A, w, w
					}, {
							w, w, w, w, A, A, A, A, A, w, w, w, w
					},
			}, {
					{
							w, w, w, w, A, A, A, A, A, w, w, w, w
					}, {
							w, w, A, A, C, C, C, C, C, A, A, w, w
					}, {
							w, A, C, C, w, w, w, w, w, C, C, A, w
					}, {
							w, A, C, w, w, w, w, w, w, w, C, A, w
					}, {
							A, C, w, w, w, w, w, w, w, w, w, C, A
					}, {
							A, C, w, w, w, w, C, w, w, w, w, C, A
					}, {
							A, C, w, w, w, C, w, C, w, w, w, C, A
					}, {
							A, C, w, w, w, w, C, w, w, w, w, C, A
					}, {
							A, C, w, w, w, w, w, w, w, w, w, C, A
					}, {
							w, A, C, w, w, w, w, w, w, w, C, A, w
					}, {
							w, A, C, C, w, w, w, w, w, C, C, A, w
					}, {
							w, w, A, A, C, C, C, C, C, A, A, w, w
					}, {
							w, w, w, w, A, A, A, A, A, w, w, w, w
					},
			}, {
					{
							w, w, w, w, w, w, w, w, w, w, w, w, w
					}, {
							w, w, w, w, w, w, w, w, w, w, w, w, w
					}, {
							w, w, w, w, C, C, C, C, C, w, w, w, w
					}, {
							w, w, w, C, C, C, C, C, C, C, w, w, w
					}, {
							w, w, C, C, C, C, C, C, C, C, C, w, w
					}, {
							w, w, C, C, C, C, A, C, C, C, C, w, w
					}, {
							w, w, C, C, C, A, A, A, C, C, C, w, w
					}, {
							w, w, C, C, C, C, A, C, C, C, C, w, w
					}, {
							w, w, C, C, C, C, C, C, C, C, C, w, w
					}, {
							w, w, w, C, C, C, C, C, C, C, w, w, w
					}, {
							w, w, w, w, C, C, C, C, C, w, w, w, w
					}, {
							w, w, w, w, w, w, w, w, w, w, w, w, w
					}, {
							w, w, w, w, w, w, w, w, w, w, w, w, w
					}
			},
	};

	public BlueprintFusion() {
		super("blueprint.fusion.desc", 13, 5, 13);
		name = "blueprint_fusion";
	}

	@Override
	public IBlockState[][][] getStructure() {
		return structure;
	}

	@Override
	public int getManagerPosX() {
		return 6;
	}

	@Override
	public int getManagerPosY() {
		return 2;
	}

	@Override
	public int getManagerPosZ() {
		return 6;
	}

	@Override
	public int getRenderScale() {
		return 6;
	}

	@Override
	public int getXOffset() {
		return 22;
	}

	@Override
	public int getYOffset() {
		return 60;
	}

}
