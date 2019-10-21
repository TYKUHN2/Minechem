package minechem.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import minechem.api.IMinechemBlueprint;
import minechem.block.tile.TileBlueprintProjector;
import minechem.init.ModItems;
import minechem.init.ModRegistries;
import minechem.item.blueprint.MinechemBlueprint;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
public class BlueprintUtil {

	public static IMinechemBlueprint getBlueprint(final ItemStack stack) {
		if (stack.hasTagCompound()) {
			final NBTTagCompound nbt = stack.getTagCompound();
			if (nbt.hasKey(MinechemBlueprint.BLUEPRINT_NBT_KEY, Constants.NBT.TAG_STRING)) {
				final ResourceLocation regName = new ResourceLocation(nbt.getString(MinechemBlueprint.BLUEPRINT_NBT_KEY));
				return ModRegistries.MINECHEM_BLUEPRINTS.getValue(regName);
			}
		}
		return null;
	}

	public static ItemStack createStack(final IMinechemBlueprint blueprint) {
		final ItemStack blueprintStack = new ItemStack(ModItems.blueprint);
		if (!blueprintStack.hasTagCompound()) {
			blueprintStack.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound nbt = blueprintStack.getTagCompound();
		nbt.setString(MinechemBlueprint.BLUEPRINT_NBT_KEY, "");
		if (blueprint == null) {
			nbt.removeTag(MinechemBlueprint.BLUEPRINT_NBT_KEY);
		}
		else {
			nbt.setString(MinechemBlueprint.BLUEPRINT_NBT_KEY, blueprint.getRegistryName().toString());
		}
		return blueprintStack;
	}

	public static boolean isBlueprintBlank(final ItemStack blueprint) {
		if (blueprint.hasTagCompound()) {
			final NBTTagCompound nbt = blueprint.getTagCompound();
			if (nbt.hasKey(MinechemBlueprint.BLUEPRINT_NBT_KEY, Constants.NBT.TAG_STRING)) {
				final String regName = nbt.getString(MinechemBlueprint.BLUEPRINT_NBT_KEY);
				return regName.isEmpty();
			}
		}
		return true;
	}

	public static List<IMinechemBlueprint> getAllBlueprints() {
		return ModRegistries.MINECHEM_BLUEPRINTS.getValues();
	}

	public static NonNullList<ItemStack> getAllBlueprintsAsStacks() {
		final NonNullList<ItemStack> stackList = NonNullList.<ItemStack>create();
		for (final IMinechemBlueprint blueprint : getAllBlueprints()) {
			stackList.add(createStack(blueprint));
		}
		return stackList;
	}

	public static Pair<IMinechemBlueprint, EnumFacing> getBlueprintFromStructure(final World world, final BlockPos pos) {
		/*
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileReactorCore) {
			TileReactorCore reactorTile = (TileReactorCore) te;
			if (reactorTile.getBlueprint() != null && reactorTile.getStructureFacing() != null) {
				if (te instanceof TileFissionCore) {
					TileFissionCore fissionTile = (TileFissionCore) te;
					if (fissionTile.getBlueprint() == null) {
						fissionTile.setBlueprint(reactorTile.getBlueprint());
					}
					if (fissionTile.getStructureFacing() == null) {
						fissionTile.setStructureFacing(reactorTile.getStructureFacing());
					}
					if (isStructureComplete(fissionTile.getBlueprint(), fissionTile.getStructureFacing(), world, pos)) {
						return Pair.of(fissionTile.getBlueprint(), fissionTile.getStructureFacing());
					}
				}
				if (te instanceof TileFusionCore) {
					TileFusionCore fusionTile = (TileFusionCore) te;
					if (fusionTile.getBlueprint() == null) {
						fusionTile.setBlueprint(reactorTile.getBlueprint());
					}
					if (fusionTile.getStructureFacing() == null) {
						fusionTile.setStructureFacing(reactorTile.getStructureFacing());
					}
					if (isStructureComplete(fusionTile.getBlueprint(), fusionTile.getStructureFacing(), world, pos)) {
						return Pair.of(fusionTile.getBlueprint(), fusionTile.getStructureFacing());
					}
				}
			}
		}
		*/
		final List<IMinechemBlueprint> bpList = ModRegistries.MINECHEM_BLUEPRINTS.getValues();
		for (final IMinechemBlueprint bp : bpList) {
			for (final EnumFacing facing : EnumFacing.HORIZONTALS) {
				final LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), facing);

				position.moveForward(Math.floor(bp.xSize() / 2));
				position.moveLeft(Math.floor(bp.zSize() / 2));
				position.moveDown(Math.floor(bp.ySize() / 2));
				//LocalPosition.Pos3 worldPos = position.getLocalPos(pos);
				if (isStructureComplete(bp, facing, world, pos)) {
					return Pair.of(bp, facing);
				}
			}
		}
		return null;
	}

	public static boolean isStructureComplete(final World world, final BlockPos pos) {
		return getBlueprintFromStructure(world, pos) != null;
	}

	public static boolean isStructureComplete(final IMinechemBlueprint blueprint, final EnumFacing structureFacing, final World world, final BlockPos pos) {
		if (blueprint == null || structureFacing == null || world == null || pos == null) {
			return false;
		}
		final LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), structureFacing);
		//LocalPosition worldPos = new LocalPosition(blueprint.getManagerPosX(), blueprint.getManagerPosY(), blueprint.getManagerPosZ(), structureFacing);
		position.moveForward(Math.floor(blueprint.zSize() / 2));
		position.moveRight(Math.floor(blueprint.xSize() / 2));
		position.moveDown(Math.floor(blueprint.ySize() / 2));
		final IBlockState[][][] resultStructure = blueprint.getStructure();
		for (int x = 0; x < blueprint.xSize(); ++x) {
			for (int y = 0; y < blueprint.ySize(); ++y) {
				for (int z = 0; z < blueprint.zSize(); ++z) {
					if (x == blueprint.getManagerPosX() && y == blueprint.getManagerPosY() && z == blueprint.getManagerPosZ()) {
						continue;
					}
					final LocalPosition.Pos3 worldPos = position.getLocalPos(new BlockPos(x, y, z));
					final BlockPos wPos = new BlockPos(pos.getX() - blueprint.getManagerPosX() + x, pos.getY() - blueprint.getManagerPosY() + y, pos.getZ() - blueprint.getManagerPosZ() + z);
					final IBlockState bpState = resultStructure[y][x][z];
					IBlockState worldState = world.getBlockState(wPos);

					final Block bpBlock = bpState.getBlock();
					Block worldBlock = worldState.getBlock();

					final int bpMeta = bpBlock.getMetaFromState(bpState);
					int worldMeta = worldBlock.getMetaFromState(worldState);
					if (bpBlock != worldBlock || bpMeta != worldMeta) {
						worldState = world.getBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
						worldBlock = worldState.getBlock();
						worldMeta = worldBlock.getMetaFromState(worldState);
						if (bpBlock != worldBlock || bpMeta != worldMeta) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public static int structureRenderTicks = 0;
	public static MultiblockBlockAccess blockAccess;
	public static int currentLayer = 0;

	@SideOnly(Side.CLIENT)
	public static void renderStructureOnScreen(final IMinechemBlueprint blueprint, final int x, final int y, final int mx, final int my, final float rotX, final float rotY, final int scale) {
		boolean openBuffer = false;
		final int stackDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);

		try {

			final int structureLength = blueprint.zSize();
			final int structureWidth = blueprint.xSize();
			final int structureHeight = blueprint.ySize();
			//double squirt = Math.sqrt(structureHeight * structureHeight + structureWidth * structureWidth + structureLength * structureLength);
			final float transX = x + 40 + blueprint.getXOffset();
			final float transY = y + 0 + blueprint.getYOffset();

			//int yOffTotal = (int) (transY - y + scale * squirt / 2);

			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			RenderHelper.disableStandardItemLighting();
			//			GL11.glEnable(GL11.GL_DEPTH_TEST);
			//			GL11.glDepthFunc(GL11.GL_ALWAYS);
			//			GL11.glDisable(GL11.GL_CULL_FACE);
			//int i = 0;
			//ItemStack highlighted = ItemStack.EMPTY;

			final BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();

			//float f = (float) Math.sqrt(structureHeight * structureHeight + structureWidth * structureWidth + structureLength * structureLength);

			GlStateManager.translate(transX, transY, Math.max(structureHeight, Math.max(structureWidth, structureLength)));
			GlStateManager.scale(scale, -scale, 1);
			GlStateManager.rotate(rotX, 1, 0, 0);
			GlStateManager.rotate(90 + rotY, 0, 1, 0);

			GlStateManager.translate(structureLength / -2f, structureHeight / -2f, structureWidth / -2f);

			GlStateManager.disableLighting();

			if (Minecraft.isAmbientOcclusionEnabled()) {
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
			}
			else {
				GlStateManager.shadeModel(GL11.GL_FLAT);
			}

			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			//int idx = 0;

			for (int h = 0; h < structureHeight; h++) {
				for (int l = 0; l < structureLength; l++) {
					for (int w = 0; w < structureWidth; w++) {
						final BlockPos pos = new BlockPos(l, h, w);
						if (blockAccess != null && !blockAccess.isAirBlock(pos)) {
							GlStateManager.translate(l, h, w);
							//boolean b = multiblock.overwriteBlockRender(renderInfo.data[h][l][w], idx++);
							GlStateManager.translate(-l, -h, -w);
							//if (!b) {
							final IBlockState state = blockAccess.getBlockState(pos);
							final Tessellator tessellator = Tessellator.getInstance();
							final BufferBuilder buffer = tessellator.getBuffer();
							buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
							openBuffer = true;
							blockRender.renderBlock(state, pos, blockAccess, buffer);
							tessellator.draw();
							openBuffer = false;
							//}
						}
					}
				}
			}

			GlStateManager.popMatrix();

			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();

			GlStateManager.enableBlend();
			RenderHelper.disableStandardItemLighting();

		}
		catch (final Exception e) {
			e.printStackTrace();
		}
		if (openBuffer) {
			try {
				Tessellator.getInstance().draw();
			}
			catch (final Exception e) {
			}
		}
		int newStackDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
		while (newStackDepth > stackDepth) {
			GlStateManager.popMatrix();
			newStackDepth--;
		}
	}

	public static class MultiblockBlockAccess implements IBlockAccess {

		public final MultiblockRenderInfo data;
		private final IBlockState[][][] structure;

		public MultiblockBlockAccess(final MultiblockRenderInfo data) {
			this.data = data;
			//final int[] index = {
			//		0
			//};//Nasty workaround, but IDEA suggested it =P

			structure = Arrays.stream(data.multiblock.getStructure()).map(layer -> {
				return Arrays.stream(layer).map(row -> {
					return Arrays.stream(row).map(blockstate -> {
						return blockstate;
					}).collect(Collectors.toList()).toArray(new IBlockState[0]);
				}).collect(Collectors.toList()).toArray(new IBlockState[0][]);
			}).collect(Collectors.toList()).toArray(new IBlockState[0][][]);

		}

		@Nullable
		@Override
		public TileEntity getTileEntity(final BlockPos pos) {
			return null;
		}

		@Override
		public int getCombinedLight(final BlockPos pos, final int lightValue) {
			// full brightness always
			return 15 << 20 | 15 << 4;
		}

		@Override
		public IBlockState getBlockState(final BlockPos pos) {
			final int x = pos.getX();
			final int y = pos.getY();
			final int z = pos.getZ();

			if (y >= 0 && y < structure.length) {
				if (x >= 0 && x < structure[y].length) {
					if (z >= 0 && z < structure[y][x].length) {
						final int index = y * data.structureLength * data.structureWidth + x * data.structureWidth + z;
						if (index <= data.getLimiter()) {
							return structure[y][x][z];
						}
					}
				}
			}
			return Blocks.AIR.getDefaultState();
		}

		@Override
		public boolean isAirBlock(final BlockPos pos) {
			return getBlockState(pos).getBlock() == Blocks.AIR;
		}

		@Override
		public Biome getBiome(final BlockPos pos) {
			final World world = Minecraft.getMinecraft().world;
			if (world != null) {
				return world.getBiome(pos);
			}
			else {
				return Biomes.BIRCH_FOREST;
			}
		}

		@Override
		public int getStrongPower(final BlockPos pos, final EnumFacing direction) {
			return 0;
		}

		@Override
		public WorldType getWorldType() {

			final World world = Minecraft.getMinecraft().world;
			if (world != null) {
				return world.getWorldType();
			}
			else {
				return WorldType.DEFAULT;
			}
		}

		@Override
		public boolean isSideSolid(final BlockPos pos, final EnumFacing side, final boolean _default) {
			return false;
		}
	}

	//Stolen back from boni's StructureInfo
	public static class MultiblockRenderInfo {

		public IMinechemBlueprint multiblock;
		public IBlockState[][][] data;
		public int blockCount = 0;
		public int[] countPerLevel;
		public int structureHeight = 0;
		public int structureLength = 0;
		public int structureWidth = 0;
		public int showLayer = -1;

		private int blockIndex = -1;
		private final int maxBlockIndex;

		public MultiblockRenderInfo(final IMinechemBlueprint multiblock) {
			this.multiblock = multiblock;
			init(multiblock.getStructure());
			maxBlockIndex = blockIndex = structureHeight * structureLength * structureWidth;
		}

		public void init(final IBlockState[][][] structure) {
			data = structure;
			structureHeight = structure.length;
			structureWidth = 0;
			structureLength = 0;

			countPerLevel = new int[structureHeight];
			blockCount = 0;
			for (int h = 0; h < structure.length; h++) {
				if (structure[h].length > structureLength) {
					structureLength = structure[h].length;
				}
				int perLvl = 0;
				for (int l = 0; l < structure[h].length; l++) {
					if (structure[h][l].length > structureWidth) {
						structureWidth = structure[h][l].length;
					}
					for (final IBlockState ss : structure[h][l]) {
						if (ss != null) {
							perLvl++;
						}
					}
				}
				countPerLevel[h] = perLvl;
				blockCount += perLvl;
			}
		}

		public void setShowLayer(final int layer) {
			showLayer = layer;
			if (layer < 0) {
				reset();
			}
			else {
				blockIndex = (layer + 1) * structureLength * structureWidth - 1;
			}
		}

		public void reset() {
			blockIndex = maxBlockIndex;
		}

		public void step() {
			final int start = blockIndex;
			do {
				if (++blockIndex >= maxBlockIndex) {
					blockIndex = 0;
				}
			}
			while (isEmpty(blockIndex) && blockIndex != start);
		}

		private boolean isEmpty(final int index) {
			final int y = index / (structureLength * structureWidth);
			final int r = index % (structureLength * structureWidth);
			final int x = r / structureWidth;
			final int z = r % structureWidth;

			final IBlockState stack = data[y][x][z];
			return stack == null;
		}

		public int getLimiter() {
			return blockIndex;
		}
	}

	public final ItemStack[][][] convertToStacks(final IMinechemBlueprint blueprint) {
		final IBlockState[][][] schematicArray = blueprint.getStructure();
		final ItemStack[][][] stackArray = new ItemStack[][][] {};
		for (int xIndex = 0; xIndex < schematicArray.length; xIndex++) {
			for (int yIndex = 0; yIndex < schematicArray[xIndex].length; yIndex++) {
				for (int zIndex = 0; zIndex < schematicArray[xIndex][yIndex].length; zIndex++) {
					if (stackArray[xIndex][yIndex][zIndex] == null) {
						final IBlockState currentState = schematicArray[xIndex][yIndex][zIndex];
						final int meta = currentState.getBlock().getMetaFromState(currentState);
						stackArray[xIndex][yIndex][zIndex] = new ItemStack(currentState.getBlock(), 1, meta);
					}
				}
			}
		}
		return stackArray;
	}

	@SideOnly(Side.CLIENT)
	public static void renderBlueprintInWorld(final TileBlueprintProjector te) {
		if (te.getWorld() != null && te.hasBlueprint()) {
			// create the fake world
			final WorldProxy proxy = new WorldProxy(te.getWorld(), 15);
			final BlockPos tilePos = te.getPos();
			final int xCoord = tilePos.getX();
			final int yCoord = tilePos.getY();
			final int zCoord = tilePos.getZ();

			// Get the direction the projector is pointing
			final EnumFacing dir = EnumFacing.HORIZONTALS[te.getBlockMetadata() - 2];
			final IBlockState[][][] schematicArray = te.getBlueprint().getStructure();
			for (int xIndex = 0; xIndex < schematicArray.length; xIndex++) {
				for (int yIndex = 0; yIndex < schematicArray[xIndex].length; yIndex++) {
					for (int zIndex = 0; zIndex < schematicArray[xIndex][yIndex].length; zIndex++) {

						int xRender;
						final int yRender = yCoord + yIndex;
						int zRender;

						if (dir.getFrontOffsetX() > 0) {
							xRender = xCoord + xIndex * -1 + dir.getFrontOffsetX() + schematicArray.length;
							zRender = zCoord + zIndex - schematicArray[xIndex][yIndex].length / 2;

						}
						else if (dir.getFrontOffsetX() < 0) {
							xRender = xCoord + xIndex + dir.getFrontOffsetX() - schematicArray.length;
							zRender = zCoord + zIndex * -1 + schematicArray[xIndex][yIndex].length / 2;

						}
						else if (dir.getFrontOffsetZ() > 0) {
							xRender = xCoord + zIndex * -1 + schematicArray[xIndex][yIndex].length / 2;
							zRender = zCoord + xIndex * -1 + dir.getFrontOffsetZ() + schematicArray.length;

						}
						else // if (dir.offsetZ < 0)
						{
							xRender = xCoord + zIndex - schematicArray[xIndex][yIndex].length / 2;
							zRender = zCoord + xIndex + dir.getFrontOffsetZ() - schematicArray.length;
						}

						proxy.setCoordinates(xRender, yRender, zRender);
						final IBlockState currentState = schematicArray[xIndex][yIndex][zIndex];
						proxy.setBlockMetadata(currentState.getBlock().getMetaFromState(currentState));

						final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
						final BlockPos blockPos = te.getPos();
						final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(currentState);
						final Tessellator tessellator = Tessellator.getInstance();
						final BufferBuilder worldRenderer = tessellator.getBuffer();
						Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						GlStateManager.pushMatrix();
						GlStateManager.translate(xCoord + .5, yCoord + .5, zCoord + .5);
						RenderHelper.disableStandardItemLighting();
						GlStateManager.blendFunc(770, 771);
						GlStateManager.enableBlend();
						GlStateManager.disableCull();
						if (Minecraft.isAmbientOcclusionEnabled()) {
							GL11.glShadeModel(GL11.GL_SMOOTH);

						}
						else {
							GL11.glShadeModel(GL11.GL_FLAT);

						}

						worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
						worldRenderer.setTranslation(-.5 - blockPos.getX(), -.5 - blockPos.getY(), -.5 - blockPos.getZ());
						worldRenderer.color(255, 255, 255, 255);
						blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), model, currentState, blockPos, worldRenderer, true);
						worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
						tessellator.draw();
						RenderHelper.enableStandardItemLighting();
						GlStateManager.popMatrix();

					}
				}
			}
		}

	}

	public static class WorldProxy implements IBlockAccess {

		IBlockAccess world;
		int meta;
		int brightness;
		int x, y, z;

		public WorldProxy(final IBlockAccess world, final int brightness) {
			this.world = world;
			setBlockMetadata(0);
			this.brightness = brightness;
		}

		public void setCoordinates(final int x, final int y, final int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public final void setBlockMetadata(final int meta) {
			this.meta = meta;
		}

		@Override
		public TileEntity getTileEntity(final BlockPos pos) {
			return null;
		}

		@Override
		public int getCombinedLight(final BlockPos pos, final int lightValue) {
			return 0;
		}

		@Override
		public IBlockState getBlockState(final BlockPos pos) {
			return null;
		}

		@Override
		public boolean isAirBlock(final BlockPos pos) {
			return false;
		}

		@Override
		public Biome getBiome(final BlockPos pos) {
			return null;
		}

		@Override
		public int getStrongPower(final BlockPos pos, final EnumFacing direction) {
			return 0;
		}

		@Override
		public WorldType getWorldType() {
			return null;
		}

		@Override
		public boolean isSideSolid(final BlockPos pos, final EnumFacing side, final boolean _default) {
			return true;
		}

	}

}
