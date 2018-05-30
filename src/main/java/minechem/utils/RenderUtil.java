package minechem.utils;

import java.awt.Color;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import minechem.client.model.generated.ModelProperties;
import minechem.client.model.generated.PerspectiveAwareBakedModel;
import minechem.client.model.generated.Transforms;
import minechem.item.element.ElementEnum;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.LightUtil.ItemConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.common.model.TRSRTransformation;

public final class RenderUtil {

	private static final EnumFacing[] HORIZONTALS = {
			EnumFacing.UP,
			EnumFacing.DOWN
	};
	private static final EnumFacing[] VERTICALS = {
			EnumFacing.WEST,
			EnumFacing.EAST
	};
	private static Map<ResourceLocation, IBakedModel> MODEL_CACHE = Maps.<ResourceLocation, IBakedModel>newHashMap();

	public static void drawTextureIn3D(ResourceLocation texture, int color) {
		float scale = 1.0F;
		GlStateManager.translate(-scale / 2, -scale / 2, 0.0F);
		IBakedModel model = getModel(texture);
		render(model, color);
	}

	public static IBakedModel getModel(ResourceLocation loc) {
		if (!getModelCache().containsKey(loc)) {
			getModelCache().put(loc, new PerspectiveAwareBakedModel(ItemLayerModel.getQuadsForSprite(0, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString()), DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())), Transforms.DEFAULT_ITEM, ModelProperties.DEFAULT_ITEM));
		}
		return getModelCache().get(loc);
	}

	public static Map<ResourceLocation, IBakedModel> getModelCache() {
		return MODEL_CACHE;
	}

	public static void render(IBakedModel model, ItemStack stack) {
		render(model, -1, stack);
	}

	public static void render(IBakedModel model, int color) {
		render(model, color, ItemStack.EMPTY);
	}

	public static void render(IBakedModel model) {
		render(model, 0x00);
	}

	public static void render(IBakedModel model, int color, ItemStack stack) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
		for (EnumFacing enumfacing : EnumFacing.values()) {
			renderQuads(vertexbuffer, model.getQuads((IBlockState) null, enumfacing, 0L), color, stack);
		}
		renderQuads(vertexbuffer, model.getQuads((IBlockState) null, (EnumFacing) null, 0L), color, stack);
		tessellator.draw();
	}

	public static void renderQuads(List<BakedQuad> quads, float alphaOverride) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();
		buffer.begin(0x07, DefaultVertexFormats.ITEM);

		int alpha = (int) (alphaOverride * 255F) & 0xFF;
		for (BakedQuad quad : quads) {
			int colour = -1;
			colour |= (alpha << 24);
			boolean doParty = false;
			Minecraft mc = Minecraft.getMinecraft();
			World world = mc.world;
			EntityPlayer player = mc.player;
			BlockPos pos = player.getPosition();
			int distance = 2;
			for (int x = -distance; x < distance; x++) {
				for (int y = -distance; y < distance; y++) {
					for (int z = -distance; z < distance; z++) {
						BlockPos checkingPos = pos.add(x, y, z);
						if (world.getBlockState(checkingPos).getBlock() == Blocks.JUKEBOX) {
							IBlockState state = world.getBlockState(checkingPos);
							if (state.getValue(BlockJukebox.HAS_RECORD).booleanValue()) {
								doParty = true;
								break;
							}
						}
					}
				}
			}
			if (doParty) {
				Random rand = world.rand;
				int red = rand.nextInt(255);
				int green = rand.nextInt(255);
				int blue = rand.nextInt(255);
				colour = (colour << 8) + red;
				colour = (colour << 8) + green;
				colour = (colour << 8) + blue;
			}
			LightUtil.renderQuadColor(buffer, quad, colour);
		}

		tess.draw();
	}

	public static void renderQuadsColored(List<BakedQuad> quads, int color, float alphaOverride) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();
		buffer.begin(0x07, DefaultVertexFormats.ITEM);

		int alpha = (int) (alphaOverride * 255F) & 0xFF;
		for (BakedQuad quad : quads) {
			color |= (alpha << 24);
			boolean doParty = false;
			Minecraft mc = Minecraft.getMinecraft();
			World world = mc.world;
			EntityPlayer player = mc.player;
			BlockPos pos = player.getPosition();
			int distance = 2;
			for (int x = -distance; x < distance; x++) {
				for (int y = -distance; y < distance; y++) {
					for (int z = -distance; z < distance; z++) {
						BlockPos checkingPos = pos.add(x, y, z);
						if (world.getBlockState(checkingPos).getBlock() == Blocks.JUKEBOX) {
							IBlockState state = world.getBlockState(checkingPos);
							if (state.getValue(BlockJukebox.HAS_RECORD).booleanValue()) {
								doParty = true;
								break;
							}
						}
					}
				}
			}
			if (doParty) {
				Random rand = world.rand;
				int red = rand.nextInt(255);
				int green = rand.nextInt(255);
				int blue = rand.nextInt(255);
				color = (color << 8) + red;
				color = (color << 8) + green;
				color = (color << 8) + blue;
			}
			LightUtil.renderQuadColor(buffer, quad, color);
		}

		tess.draw();
	}

	public static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
		boolean flag = (color == -1) && (!stack.isEmpty());
		int i = 0;
		for (int j = quads.size(); i < j; i++) {
			BakedQuad bakedquad = quads.get(i);
			int k = color;
			if ((flag)) {
				k = color == -1 ? 0xFFFFFFFF : color;
				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}
				k |= 0xFF000000;
			}
			if (bakedquad.getFormat().equals(renderer.getVertexFormat())) {
				renderer.addVertexData(bakedquad.getVertexData());
				ForgeHooksClient.putQuadColor(renderer, bakedquad, k);
			}
			else {
				ItemConsumer cons;
				if (renderer == Tessellator.getInstance().getBuffer()) {
					cons = LightUtil.getItemConsumer();
				}
				else {
					cons = new ItemConsumer(new VertexBufferConsumer(renderer));
				}
				float b = (float) (k & 0xFF) / 0xFF;
				float g = (float) ((k >>> 8) & 0xFF) / 0xFF;
				float r = (float) ((k >>> 16) & 0xFF) / 0xFF;
				float a = (float) ((k >>> 24) & 0xFF) / 0xFF;

				cons.setAuxColor(r, g, b, a);
				bakedquad.pipe(cons);
			}
		}
	}

	/**
	 * Executes GlStateManager.color() for given int colour
	 *
	 * @param colour in int form
	 */
	public static void setOpenGLColour(int colour) {
		Color color = new Color(colour);
		GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
	}

	/**
	 * Executes GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
	 */
	public static void resetOpenGLColour() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Executes GlStateManager.color(greyscale, greyscale, greyscale, 1.0F)
	 *
	 * @param greyscale the greyscale in float form where 0.0F is black and 1.0F is white
	 */
	public static void setGreyscaleOpenGLColour(float greyscale) {
		GlStateManager.color(greyscale, greyscale, greyscale, 1.0F);
	}

	public static void setColorForElement(ElementEnum element) {
		switch (element.classification()) {
		case actinide:
			GlStateManager.color(1.0F, 0.0F, 0.0F);
			break;
		case alkaliMetal:
			GlStateManager.color(0.0F, 1.0F, 0.0F);
			break;
		case alkalineEarthMetal:
			GlStateManager.color(0.0F, 0.0F, 1.0F);
			break;
		case halogen:
			GlStateManager.color(1.0F, 1.0F, 0.0F);
			break;
		case inertGas:
			GlStateManager.color(0.0F, 1.0F, 1.0F);
			break;
		case lanthanide:
			GlStateManager.color(1.0F, 0.0F, 1.0F);
			break;
		case nonmetal:
			GlStateManager.color(1.0F, 0.5F, 0.0F);
			break;
		case otherMetal:
			GlStateManager.color(0.5F, 1.0F, 0.0F);
			break;
		case semimetallic:
			GlStateManager.color(0.0F, 1.0F, 0.5F);
			break;
		case transitionMetal:
			GlStateManager.color(0.0F, 0.5F, 1.0F);
			break;
		default:
			break;
		}
	}

	public static int getColorForElement(ElementEnum element) {
		switch (element.classification()) {
		case actinide:
			return 0xFFFF0000;
		case alkaliMetal:
			return 0xFF00FF00;
		case alkalineEarthMetal:
			return 0xFF0000FF;
		case halogen:
			return 0xFFFFFF00;
		case inertGas:
			return 0xFF00FFFF;
		case lanthanide:
			return 0xFFFF00FF;
		case nonmetal:
			return 0xFFFF7700;
		case otherMetal:
			return 0xFF77FF00;
		case semimetallic:
			return 0xFF00FF77;
		case transitionMetal:
			return 0xFF0077FF;
		default:
			return 0xFFFFFFFF;
		}
	}

	public static void startScissor(Minecraft minecraft, int x, int y, int w, int h) {
		ScaledResolution scaledRes = new ScaledResolution(minecraft);
		int scale = scaledRes.getScaleFactor();

		int scissorWidth = w * scale;
		int scissorHeight = h * scale;
		int scissorX = x * scale;
		int scissorY = minecraft.displayHeight - scissorHeight - (y * scale);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
	}

	public static void endScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public static ImmutableList<BakedQuad> getQuadsForSprites(VertexFormat format, Optional<TRSRTransformation> transform, TextureAtlasSprite... sprites) {
		int tint = 0;
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
		float horizontalPixelPos = 0;
		for (TextureAtlasSprite sprite : sprites) {
			int uMax = sprite.getIconWidth();
			int vMax = sprite.getIconHeight();

			FaceData faceData = new FaceData(uMax, vMax);
			boolean translucent = false;

			for (int f = 0; f < sprite.getFrameCount(); f++) {
				int[] pixels = sprite.getFrameTextureData(f)[0];
				boolean ptu;
				boolean[] ptv = new boolean[uMax];
				Arrays.fill(ptv, true);
				for (int v = 0; v < vMax; v++) {
					ptu = true;
					for (int u = 0; u < uMax; u++) {
						int alpha = getAlpha(pixels, uMax, vMax, u, v);
						boolean t = alpha / 255f <= 0.1f;

						if (!t && alpha < 255) {
							translucent = true;
						}

						if (ptu && !t) // left - transparent, right - opaque
						{
							faceData.set(EnumFacing.WEST, u, v);
						}
						if (!ptu && t) // left - opaque, right - transparent
						{
							faceData.set(EnumFacing.EAST, u - 1, v);
						}
						if (ptv[u] && !t) // up - transparent, down - opaque
						{
							faceData.set(EnumFacing.UP, u, v);
						}
						if (!ptv[u] && t) // up - opaque, down - transparent
						{
							faceData.set(EnumFacing.DOWN, u, v - 1);
						}

						ptu = t;
						ptv[u] = t;
					}
					if (!ptu) // last - opaque
					{
						faceData.set(EnumFacing.EAST, uMax - 1, v);
					}
				}
				// last line
				for (int u = 0; u < uMax; u++) {
					if (!ptv[u]) {
						faceData.set(EnumFacing.DOWN, u, vMax - 1);
					}
				}
			}

			// horizontal quads
			for (EnumFacing facing : HORIZONTALS) {
				for (int v = 0; v < vMax; v++) {
					int uStart = 0, uEnd = uMax;
					boolean building = false;
					for (int u = 0; u < uMax; u++) {
						boolean face = faceData.get(facing, u, v);
						if (!translucent) {
							if (face) {
								if (!building) {
									building = true;
									uStart = u;
								}
								uEnd = u + 1;
							}
						}
						else {
							if (building && !face) // finish current quad
							{
								// make quad [uStart, u]
								int off = facing == EnumFacing.DOWN ? 1 : 0;
								builder.add(buildSideQuad(format, transform, facing, tint, sprite, uStart, v + off, u - uStart, horizontalPixelPos));
								building = false;
							}
							else if (!building && face) // start new quad
							{
								building = true;
								uStart = u;
							}
						}
					}
					if (building) // build remaining quad
					{
						// make quad [uStart, uEnd]
						int off = facing == EnumFacing.DOWN ? 1 : 0;
						builder.add(buildSideQuad(format, transform, facing, tint, sprite, uStart, v + off, uEnd - uStart, horizontalPixelPos));
					}
				}
			}

			// vertical quads
			for (EnumFacing facing : VERTICALS) {
				for (int u = 0; u < uMax; u++) {
					int vStart = 0, vEnd = vMax;
					boolean building = false;
					for (int v = 0; v < vMax; v++) {
						boolean face = faceData.get(facing, u, v);
						if (!translucent) {
							if (face) {
								if (!building) {
									building = true;
									vStart = v;
								}
								vEnd = v + 1;
							}
						}
						else {
							if (building && !face) // finish current quad
							{
								// make quad [vStart, v]
								int off = facing == EnumFacing.EAST ? 1 : 0;
								builder.add(buildSideQuad(format, transform, facing, tint, sprite, u + off, vStart, v - vStart, horizontalPixelPos));
								building = false;
							}
							else if (!building && face) // start new quad
							{
								building = true;
								vStart = v;
							}
						}
					}
					if (building) // build remaining quad
					{
						// make quad [vStart, vEnd]
						int off = facing == EnumFacing.EAST ? 1 : 0;
						builder.add(buildSideQuad(format, transform, facing, tint, sprite, u + off, vStart, vEnd - vStart, horizontalPixelPos));
					}
				}
			}

			// front
			builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint, 0 + horizontalPixelPos, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(), 0 + horizontalPixelPos, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(), 1 + horizontalPixelPos, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(), 1 + horizontalPixelPos, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()));
			// back
			builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint, 0 + horizontalPixelPos, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(), 1 + horizontalPixelPos, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(), 1 + horizontalPixelPos, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(), 0 + horizontalPixelPos, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()));
			horizontalPixelPos += 0.37F;
		}
		return builder.build();
	}

	private static int getAlpha(int[] pixels, int uMax, int vMax, int u, int v) {
		return pixels[u + (vMax - 1 - v) * uMax] >> 24 & 0xFF;
	}

	private static BakedQuad buildSideQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int u, int v, int size, float horizontalOffset) {
		final float eps = 1e-2f;

		int width = sprite.getIconWidth();
		int height = sprite.getIconHeight();

		float x0 = (float) u / width;
		float y0 = (float) v / height;
		float x1 = x0, y1 = y0;
		float z0 = 7.5f / 16f, z1 = 8.5f / 16f;

		switch (side) {
		case WEST:
			z0 = 8.5f / 16f;
			z1 = 7.5f / 16f;
		case EAST:
			y1 = (float) (v + size) / height;
			break;
		case DOWN:
			z0 = 8.5f / 16f;
			z1 = 7.5f / 16f;
		case UP:
			x1 = (float) (u + size) / width;
			break;
		default:
			throw new IllegalArgumentException("can't handle z-oriented side");
		}

		float dx = side.getDirectionVec().getX() * eps / width;
		float dy = side.getDirectionVec().getY() * eps / height;

		float u0 = 16f * (x0 - dx);
		float u1 = 16f * (x1 - dx);
		float v0 = 16f * (1f - y0 - dy);
		float v1 = 16f * (1f - y1 - dy);

		return buildQuad(format, transform, remap(side), sprite, tint, x0 + horizontalOffset, y0, z0, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0), x1 + horizontalOffset, y1, z0, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x1 + horizontalOffset, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x0 + horizontalOffset, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0));
	}

	private static EnumFacing remap(EnumFacing side) {
		// getOpposite is related to the swapping of V direction
		return side.getAxis() == EnumFacing.Axis.Y ? side.getOpposite() : side;
	}

	private static BakedQuad buildQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, TextureAtlasSprite sprite, int tint, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3) {
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setQuadTint(tint);
		builder.setQuadOrientation(side);
		builder.setTexture(sprite);
		putVertex(builder, format, transform, side, x0, y0, z0, u0, v0);
		putVertex(builder, format, transform, side, x1, y1, z1, u1, v1);
		putVertex(builder, format, transform, side, x2, y2, z2, u2, v2);
		putVertex(builder, format, transform, side, x3, y3, z3, u3, v3);
		return builder.build();
	}

	private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, float x, float y, float z, float u, float v) {
		Vector4f vec = new Vector4f();
		for (int e = 0; e < format.getElementCount(); e++) {
			switch (format.getElement(e).getUsage()) {
			case POSITION:
				if (transform.isPresent()) {
					vec.x = x;
					vec.y = y;
					vec.z = z;
					vec.w = 1;
					transform.get().getMatrix().transform(vec);
					builder.put(e, vec.x, vec.y, vec.z, vec.w);
				}
				else {
					builder.put(e, x, y, z, 1);
				}
				break;
			case COLOR:
				builder.put(e, 1f, 1f, 1f, 1f);
				break;
			case UV:
				if (format.getElement(e).getIndex() == 0) {
					builder.put(e, u, v, 0f, 1f);
					break;
				}
			case NORMAL:
				builder.put(e, side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ(), 0f);
				break;
			default:
				builder.put(e);
				break;
			}
		}
	}

	private static class FaceData {
		private final EnumMap<EnumFacing, BitSet> data = new EnumMap<>(EnumFacing.class);

		private final int vMax;

		FaceData(int uMax, int vMax) {
			this.vMax = vMax;

			data.put(EnumFacing.WEST, new BitSet(uMax * vMax));
			data.put(EnumFacing.EAST, new BitSet(uMax * vMax));
			data.put(EnumFacing.UP, new BitSet(uMax * vMax));
			data.put(EnumFacing.DOWN, new BitSet(uMax * vMax));
		}

		public void set(EnumFacing facing, int u, int v) {
			data.get(facing).set(getIndex(u, v));
		}

		public boolean get(EnumFacing facing, int u, int v) {
			return data.get(facing).get(getIndex(u, v));
		}

		private int getIndex(int u, int v) {
			return v * vMax + u;
		}
	}

}
