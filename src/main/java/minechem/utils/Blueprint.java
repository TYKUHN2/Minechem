package minechem.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import minechem.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @author Shadows
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
public class Blueprint {

	public static IBlockState A = ModBlocks.reactor_core.getStateFromMeta(1);
	public static IBlockState B = ModBlocks.reactor_wall.getStateFromMeta(1);
	public static IBlockState C = ModBlocks.tungsten_plating.getStateFromMeta(2);
	public static IBlockState D = Blocks.AIR.getDefaultState();

	public static Blueprint FISSION_REACTOR = new Blueprint(new IBlockState[][][] {
		//@formatter:off
		new IBlockState[][] {
			new IBlockState[] { A, A, A, A, A }, // bottom layer
			new IBlockState[] { A, A, A, A, A },
			new IBlockState[] { A, A, A, A, A },
			new IBlockState[] { A, A, A, A, A },
			new IBlockState[] { A, A, A, A, A }
		},
		new IBlockState[][] {
			new IBlockState[] { A, A, A, A, A }, // 2nd layer
			new IBlockState[] { A, D, D, D, A },
			new IBlockState[] { A, D, D, D, A },
			new IBlockState[] { A, D, D, D, A },
			new IBlockState[] { A, A, A, A, A }
		},
		new IBlockState[][] {
			new IBlockState[] { A, A, A, A, A }, // 3rd layer
			new IBlockState[] { A, D, D, D, A },
			new IBlockState[] { A, D, C, D, A },
			new IBlockState[] { A, D, D, D, A },
			new IBlockState[] { A, A, A, A, A }
		},
		new IBlockState[][] {
			new IBlockState[] { A, A, A, A, A }, // 2nd layer
			new IBlockState[] { A, D, D, D, A },
			new IBlockState[] { A, D, D, D, A },
			new IBlockState[] { A, D, D, D, A },
			new IBlockState[] { A, A, A, A, A }
		},
		new IBlockState[][] {
			new IBlockState[] { A, A, A, A, A }, // bottom layer
			new IBlockState[] { A, A, A, A, A },
			new IBlockState[] { A, A, A, A, A },
			new IBlockState[] { A, A, A, A, A },
			new IBlockState[] { A, A, A, A, A }
		}
		//@formatter:on
	});

	private final IBlockState[][][] states;

	public Blueprint(final IBlockState[][][] statesToPlace) {
		states = statesToPlace;
	}

	public Blueprint(final Block[][][] blocks) {
		final IBlockState[][][] states = new IBlockState[blocks.length][][];
		for (int i = 0; i < blocks.length; i++) {
			states[i] = new IBlockState[blocks[i].length][];

			for (int j = 0; j < blocks[i].length; j++) {
				states[i][j] = new IBlockState[blocks[i][j].length];

				for (int k = 0; k < blocks[i][j].length; k++) {
					states[i][j][k] = blocks[i][j][k] == null ? null : blocks[i][j][k].getDefaultState();
				}
			}
		}
		this.states = states;
	}

	public IBlockState[][][] getReverseVerticalArray() {
		final IBlockState[][][] newStates = states;
		ArrayUtils.reverse(newStates);
		return newStates;
	}

	public void placeStateArray(final BlockPos pos, final World world) {
		placeStateArray(pos, world, EnumFacing.NORTH);
	}

	public void placeStateArray(final BlockPos pos, final World world, final EnumFacing facing) {
		int xOffset = 0;
		int zOffset = 0;

		for (int y = 0; y < states.length; y++) {
			for (int x = 0; x < states[y].length; x++) {
				for (int z = 0; z < states[y][x].length; z++) {
					if (states[y][x][z] != null) {
						switch (facing) {
						case NORTH:
							xOffset = 0;
							zOffset = -(states[y][x].length - 1);
							break;
						case SOUTH:
							xOffset = -(states[y][x].length - 1);
							zOffset = 0;
							break;
						case WEST:
							xOffset = -(states[y][x].length - 1);
							zOffset = -(states[y][x].length - 1);
							break;
						case EAST:
						default:
							break;
						}
						world.setBlockState(pos.add(x + xOffset, y, z + zOffset), states[y][x][z]);
					}
				}
			}
		}
	}

	public void visualiseStateArray(final BlockPos pos, final World world) {
		visualiseStateArray(pos, world, EnumFacing.NORTH, -1);
	}

	public void visualiseStateArray(final BlockPos pos, final World world, final EnumFacing facing) {
		visualiseStateArray(pos, world, facing, -1);
	}

	public int getTotalVerticalSlices() {
		return states[0][0].length;
	}

	public IBlockState[] getVerticalSliceFromBlueprint(final int layer) {
		final IBlockState[] vertStates = new IBlockState[states[0].length * states.length];
		if (layer < 0) {
			return vertStates;
		}
		for (int y = 0; y < states.length; y++) {
			for (int x = 0; x < states[y].length; x++) {
				for (int z = 0; z < states[y][x].length; z++) {
					if (states[y][x][z] != null) {
						if (z != -1 && z != layer) {
							continue;
						}
						vertStates[y + x * states.length] = states[y][x][z];
					}
				}
			}
		}
		return vertStates;
	}

	public boolean isStructureBuilt(final BlockPos pos, final World world, final EnumFacing facing) {
		final int total = getTotalVerticalSlices();
		for (int i = 0; i < total; i++) {
			if (!isVerticalSliceBuilt(pos, world, facing, i)) {
				return false;
			}
		}
		return true;
	}

	public int getXSize() {
		return states[0].length;
	}

	public int getZSize() {
		return states[0][0].length;
	}

	public boolean isVerticalSliceBuilt(final BlockPos pos, final World world, final EnumFacing facing, final int layer) {
		if (layer < 0) {
			return true;
		}
		final IBlockState[] requiredSlice = getVerticalSliceFromBlueprint(layer);
		int xOffset = 0;
		int zOffset = 0;

		final IBlockState[][][] statesToUse = facing.getOpposite() == EnumFacing.NORTH || facing.getOpposite() == EnumFacing.EAST ? states : getReverseVerticalArray();

		for (int y = 0; y < statesToUse.length; y++) {
			for (int x = 0; x < statesToUse[y].length; x++) {
				for (int z = 0; z < statesToUse[y][x].length; z++) {
					if (statesToUse[y][x][z] != null) {
						final int comparingLayer = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? z : x;
						if (comparingLayer != layer) {
							continue;
						}
						BlockPos newPos = pos.add(x + xOffset, y, z + zOffset);
						switch (facing.getOpposite()) {
						case NORTH:
							xOffset = 0;
							zOffset = -(statesToUse[y][x].length - 1);
							newPos = pos.add(x + xOffset - statesToUse[y][x].length / 2, y, z + zOffset - 2);
							break;
						case SOUTH:
							xOffset = -(statesToUse[y][x].length - 1);
							zOffset = 0;
							newPos = pos.add(-(x + xOffset + statesToUse[y][x].length / 2), y, -(z + 2) + statesToUse[y][x].length * 2 - 2);
							break;
						case WEST:
							xOffset = -(statesToUse[y][x].length - 1);
							zOffset = -(statesToUse[y][x].length - 1);
							newPos = pos.add(-(x + 2 + statesToUse[y][x].length / 2) - 2, y, -(z + zOffset + statesToUse[y][x].length / 2));
							break;
						case EAST:
							newPos = pos.add(x + xOffset + statesToUse[y][x].length / 2, y, z + zOffset - 2);
						default:
							break;
						}
						if (requiredSlice[z] == null) {
							continue;
						}
						final IBlockState current = world.getBlockState(newPos);
						final IBlockState required = requiredSlice[statesToUse[y][x].length - 1 - x + y * statesToUse.length];
						if (current != required) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public void visualiseStateArray(final BlockPos pos, final World world, final EnumFacing facing, final int layer) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder vertexbuffer = tessellator.getBuffer();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			int xOffset = 0;
			int zOffset = 0;
			final IBlockState[][][] statesToUse = facing.getOpposite() == EnumFacing.NORTH || facing.getOpposite() == EnumFacing.EAST ? states : getReverseVerticalArray();
			for (int y = 0; y < statesToUse.length; y++) {
				for (int x = 0; x < statesToUse[y].length; x++) {
					for (int z = 0; z < statesToUse[y][x].length; z++) {
						if (statesToUse[y][x][z] != null) {
							final int comparingLayer = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? z : x;
							if (layer != -1 && comparingLayer != layer) {
								continue;
							}

							BlockPos newPos = pos.add(x + xOffset, y, z + zOffset);
							switch (facing.getOpposite()) {
							case NORTH:
								zOffset = -(statesToUse[y][x].length - 1);
								newPos = pos.add(x - statesToUse[y][x].length / 2, y, z + zOffset - 2);
								break;
							case SOUTH:
								xOffset = -(statesToUse[y][x].length - 1);
								newPos = pos.add(-(x + xOffset + statesToUse[y][x].length / 2), y, -(z + 2) + statesToUse[y][x].length * 2 - 2);
								break;
							case WEST:
								xOffset = -(statesToUse[y][x].length - 1);
								zOffset = -(states[y][x].length - 1);
								newPos = pos.add(-(x + 2 + statesToUse[y][x].length / 2) - 2, y, -(z + zOffset + statesToUse[y][x].length / 2));
								break;
							case EAST:
								newPos = pos.add(x + xOffset + statesToUse[y][x].length / 2, y, z - 2);
							default:
								break;
							}

							if (world.getBlockState(newPos).getBlock() != Blocks.AIR) {
								continue;
							}
							Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(statesToUse[y][x][z], newPos, world, vertexbuffer);
						}
					}
				}
			}
			GlStateManager.pushMatrix();
			removeStandartTranslationFromTESRMatrix();
			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.disableBlend();
		}
	}

	public static void removeStandartTranslationFromTESRMatrix() {
		final float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
		if (rView == null) {
			rView = Minecraft.getMinecraft().player;
		}
		final Entity entity = rView;
		final double tx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		final double ty = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		final double tz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
		GlStateManager.translate(-tx, -ty, -tz);
	}

}