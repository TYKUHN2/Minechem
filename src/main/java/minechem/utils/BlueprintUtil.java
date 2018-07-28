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
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author p455w0rd
 *
 */
public class BlueprintUtil {

	public static IMinechemBlueprint getBlueprint(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt.hasKey(MinechemBlueprint.BLUEPRINT_NBT_KEY, Constants.NBT.TAG_STRING)) {
				ResourceLocation regName = new ResourceLocation(nbt.getString(MinechemBlueprint.BLUEPRINT_NBT_KEY));
				return ModRegistries.MINECHEM_BLUEPRINTS.getValue(regName);
			}
		}
		return null;
	}

	public static ItemStack createStack(IMinechemBlueprint blueprint) {
		ItemStack blueprintStack = new ItemStack(ModItems.blueprint);
		if (!blueprintStack.hasTagCompound()) {
			blueprintStack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = blueprintStack.getTagCompound();
		nbt.setString(MinechemBlueprint.BLUEPRINT_NBT_KEY, "");
		if (blueprint == null) {
			nbt.removeTag(MinechemBlueprint.BLUEPRINT_NBT_KEY);
		}
		else {
			nbt.setString(MinechemBlueprint.BLUEPRINT_NBT_KEY, blueprint.getRegistryName().toString());
		}
		return blueprintStack;
	}

	public static boolean isBlueprintBlank(ItemStack blueprint) {
		if (blueprint.hasTagCompound()) {
			NBTTagCompound nbt = blueprint.getTagCompound();
			if (nbt.hasKey(MinechemBlueprint.BLUEPRINT_NBT_KEY, Constants.NBT.TAG_STRING)) {
				String regName = nbt.getString(MinechemBlueprint.BLUEPRINT_NBT_KEY);
				return regName.isEmpty();
			}
		}
		return true;
	}

	public static List<IMinechemBlueprint> getAllBlueprints() {
		return ModRegistries.MINECHEM_BLUEPRINTS.getValues();
	}

	public static NonNullList<ItemStack> getAllBlueprintsAsStacks() {
		NonNullList<ItemStack> stackList = NonNullList.<ItemStack>create();
		for (IMinechemBlueprint blueprint : getAllBlueprints()) {
			stackList.add(createStack(blueprint));
		}
		return stackList;
	}

	public static Pair<IMinechemBlueprint, EnumFacing> getBlueprintFromStructure(World world, BlockPos pos) {
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
		List<IMinechemBlueprint> bpList = ModRegistries.MINECHEM_BLUEPRINTS.getValues();
		for (IMinechemBlueprint bp : bpList) {
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), facing);

				position.moveForwards(Math.floor(bp.xSize() / 2));
				position.moveLeft(Math.floor(bp.zSize() / 2));
				position.moveDown(Math.floor(bp.ySize() / 2));
				LocalPosition.Pos3 worldPos = position.getLocalPos(pos);
				if (isStructureComplete(bp, facing, world, pos)) {
					return Pair.of(bp, facing);
				}
			}
		}
		return null;
	}

	public static boolean isStructureComplete(World world, BlockPos pos) {
		return getBlueprintFromStructure(world, pos) != null;
	}

	public static boolean isStructureComplete(IMinechemBlueprint blueprint, EnumFacing structureFacing, World world, BlockPos pos) {
		if (blueprint == null || structureFacing == null || world == null || pos == null) {
			return false;
		}
		LocalPosition position = new LocalPosition(pos.getX(), pos.getY(), pos.getZ(), structureFacing);
		//LocalPosition worldPos = new LocalPosition(blueprint.getManagerPosX(), blueprint.getManagerPosY(), blueprint.getManagerPosZ(), structureFacing);
		position.moveForwards(Math.floor(blueprint.zSize() / 2));
		position.moveRight(Math.floor(blueprint.xSize() / 2));
		position.moveDown(Math.floor(blueprint.ySize() / 2));
		IBlockState[][][] resultStructure = blueprint.getStructure();
		for (int x = 0; x < blueprint.xSize(); ++x) {
			for (int y = 0; y < blueprint.ySize(); ++y) {
				for (int z = 0; z < blueprint.zSize(); ++z) {
					if (x == blueprint.getManagerPosX() && y == blueprint.getManagerPosY() && z == blueprint.getManagerPosZ()) {
						continue;
					}
					LocalPosition.Pos3 worldPos = position.getLocalPos(new BlockPos(x, y, z));
					BlockPos wPos = new BlockPos(pos.getX() - blueprint.getManagerPosX() + x, pos.getY() - blueprint.getManagerPosY() + y, pos.getZ() - blueprint.getManagerPosZ() + z);
					IBlockState bpState = resultStructure[y][x][z];
					IBlockState worldState = world.getBlockState(wPos);

					Block bpBlock = bpState.getBlock();
					Block worldBlock = worldState.getBlock();

					int bpMeta = bpBlock.getMetaFromState(bpState);
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
	public static void renderStructureOnScreen(IMinechemBlueprint blueprint, int x, int y, int mx, int my, float rotX, float rotY, int scale) {
		boolean openBuffer = false;
		int stackDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);

		try {

			int structureLength = blueprint.zSize();
			int structureWidth = blueprint.xSize();
			int structureHeight = blueprint.ySize();
			double squirt = Math.sqrt(structureHeight * structureHeight + structureWidth * structureWidth + structureLength * structureLength);
			float transX = x + 40 + blueprint.getXOffset();
			float transY = y + 0 + blueprint.getYOffset();

			int yOffTotal = (int) (transY - y + scale * squirt / 2);

			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			RenderHelper.disableStandardItemLighting();
			//			GL11.glEnable(GL11.GL_DEPTH_TEST);
			//			GL11.glDepthFunc(GL11.GL_ALWAYS);
			//			GL11.glDisable(GL11.GL_CULL_FACE);
			int i = 0;
			ItemStack highlighted = ItemStack.EMPTY;

			final BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();

			float f = (float) Math.sqrt(structureHeight * structureHeight + structureWidth * structureWidth + structureLength * structureLength);

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
			int idx = 0;

			for (int h = 0; h < structureHeight; h++) {
				for (int l = 0; l < structureLength; l++) {
					for (int w = 0; w < structureWidth; w++) {
						BlockPos pos = new BlockPos(l, h, w);
						if (blockAccess != null && !blockAccess.isAirBlock(pos)) {
							GlStateManager.translate(l, h, w);
							//boolean b = multiblock.overwriteBlockRender(renderInfo.data[h][l][w], idx++);
							GlStateManager.translate(-l, -h, -w);
							//if (!b) {
							IBlockState state = blockAccess.getBlockState(pos);
							Tessellator tessellator = Tessellator.getInstance();
							BufferBuilder buffer = tessellator.getBuffer();
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
		catch (Exception e) {
			e.printStackTrace();
		}
		if (openBuffer) {
			try {
				Tessellator.getInstance().draw();
			}
			catch (Exception e) {
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

		public MultiblockBlockAccess(MultiblockRenderInfo data) {
			this.data = data;
			final int[] index = {
					0
			};//Nasty workaround, but IDEA suggested it =P

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
		public TileEntity getTileEntity(BlockPos pos) {
			return null;
		}

		@Override
		public int getCombinedLight(BlockPos pos, int lightValue) {
			// full brightness always
			return 15 << 20 | 15 << 4;
		}

		@Override
		public IBlockState getBlockState(BlockPos pos) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			if (y >= 0 && y < structure.length) {
				if (x >= 0 && x < structure[y].length) {
					if (z >= 0 && z < structure[y][x].length) {
						int index = y * (data.structureLength * data.structureWidth) + x * data.structureWidth + z;
						if (index <= data.getLimiter()) {
							return structure[y][x][z];
						}
					}
				}
			}
			return Blocks.AIR.getDefaultState();
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			return getBlockState(pos).getBlock() == Blocks.AIR;
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			World world = Minecraft.getMinecraft().world;
			if (world != null) {
				return world.getBiome(pos);
			}
			else {
				return Biomes.BIRCH_FOREST;
			}
		}

		@Override
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return 0;
		}

		@Override
		public WorldType getWorldType() {

			World world = Minecraft.getMinecraft().world;
			if (world != null) {
				return world.getWorldType();
			}
			else {
				return WorldType.DEFAULT;
			}
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
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
		private int maxBlockIndex;

		public MultiblockRenderInfo(IMinechemBlueprint multiblock) {
			this.multiblock = multiblock;
			init(multiblock.getStructure());
			maxBlockIndex = blockIndex = structureHeight * structureLength * structureWidth;
		}

		public void init(IBlockState[][][] structure) {
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
					for (IBlockState ss : structure[h][l]) {
						if (ss != null) {
							perLvl++;
						}
					}
				}
				countPerLevel[h] = perLvl;
				blockCount += perLvl;
			}
		}

		public void setShowLayer(int layer) {
			showLayer = layer;
			if (layer < 0) {
				reset();
			}
			else {
				blockIndex = (layer + 1) * (structureLength * structureWidth) - 1;
			}
		}

		public void reset() {
			blockIndex = maxBlockIndex;
		}

		public void step() {
			int start = blockIndex;
			do {
				if (++blockIndex >= maxBlockIndex) {
					blockIndex = 0;
				}
			}
			while (isEmpty(blockIndex) && blockIndex != start);
		}

		private boolean isEmpty(int index) {
			int y = index / (structureLength * structureWidth);
			int r = index % (structureLength * structureWidth);
			int x = r / structureWidth;
			int z = r % structureWidth;

			IBlockState stack = data[y][x][z];
			return stack == null;
		}

		public int getLimiter() {
			return blockIndex;
		}
	}

	public final ItemStack[][][] convertToStacks(IMinechemBlueprint blueprint) {
		IBlockState[][][] schematicArray = blueprint.getStructure();
		ItemStack[][][] stackArray = new ItemStack[][][] {};
		for (int xIndex = 0; xIndex < schematicArray.length; xIndex++) {
			for (int yIndex = 0; yIndex < schematicArray[xIndex].length; yIndex++) {
				for (int zIndex = 0; zIndex < schematicArray[xIndex][yIndex].length; zIndex++) {
					if (stackArray[xIndex][yIndex][zIndex] == null) {
						IBlockState currentState = schematicArray[xIndex][yIndex][zIndex];
						int meta = currentState.getBlock().getMetaFromState(currentState);
						stackArray[xIndex][yIndex][zIndex] = new ItemStack(currentState.getBlock(), 1, meta);
					}
				}
			}
		}
		return stackArray;
	}

	@SideOnly(Side.CLIENT)
	public static void renderBlueprintInWorld(TileBlueprintProjector te) {
		if (te.getWorld() != null && te.hasBlueprint()) {
			// create the fake world
			WorldProxy proxy = new WorldProxy(te.getWorld(), 15);
			BlockPos tilePos = te.getPos();
			int xCoord = tilePos.getX();
			int yCoord = tilePos.getY();
			int zCoord = tilePos.getZ();

			// Get the direction the projector is pointing
			EnumFacing dir = EnumFacing.HORIZONTALS[te.getBlockMetadata() - 2];
			IBlockState[][][] schematicArray = te.getBlueprint().getStructure();
			for (int xIndex = 0; xIndex < schematicArray.length; xIndex++) {
				for (int yIndex = 0; yIndex < schematicArray[xIndex].length; yIndex++) {
					for (int zIndex = 0; zIndex < schematicArray[xIndex][yIndex].length; zIndex++) {

						int xRender;
						int yRender = yCoord + yIndex;
						int zRender;

						if (dir.getFrontOffsetX() > 0) {
							xRender = xCoord + (xIndex * -1) + dir.getFrontOffsetX() + schematicArray.length;
							zRender = zCoord + zIndex - schematicArray[xIndex][yIndex].length / 2;

						}
						else if (dir.getFrontOffsetX() < 0) {
							xRender = xCoord + xIndex + dir.getFrontOffsetX() - schematicArray.length;
							zRender = zCoord + (zIndex * -1) + schematicArray[xIndex][yIndex].length / 2;

						}
						else if (dir.getFrontOffsetZ() > 0) {
							xRender = xCoord + (zIndex * -1) + schematicArray[xIndex][yIndex].length / 2;
							zRender = zCoord + (xIndex * -1) + dir.getFrontOffsetZ() + schematicArray.length;

						}
						else // if (dir.offsetZ < 0)
						{
							xRender = xCoord + zIndex - schematicArray[xIndex][yIndex].length / 2;
							zRender = zCoord + xIndex + dir.getFrontOffsetZ() - schematicArray.length;
						}

						proxy.setCoordinates(xRender, yRender, zRender);
						IBlockState currentState = schematicArray[xIndex][yIndex][zIndex];
						proxy.setBlockMetadata(currentState.getBlock().getMetaFromState(currentState));

						final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
						BlockPos blockPos = te.getPos();
						IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(currentState);
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder worldRenderer = tessellator.getBuffer();
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

		public WorldProxy(IBlockAccess world, int brightness) {
			this.world = world;
			setBlockMetadata(0);
			this.brightness = brightness;
		}

		public void setCoordinates(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public final void setBlockMetadata(int meta) {
			this.meta = meta;
		}

		@Override
		public TileEntity getTileEntity(BlockPos pos) {
			return null;
		}

		@Override
		public int getCombinedLight(BlockPos pos, int lightValue) {
			return 0;
		}

		@Override
		public IBlockState getBlockState(BlockPos pos) {
			return null;
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			return false;
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return null;
		}

		@Override
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return 0;
		}

		@Override
		public WorldType getWorldType() {
			return null;
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
			return true;
		}

	}

}
