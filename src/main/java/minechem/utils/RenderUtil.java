package minechem.utils;

import java.awt.Color;
import java.util.*;

import javax.vecmath.Vector4f;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import minechem.client.model.generated.*;
import minechem.item.element.ElementEnum;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
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
import net.minecraftforge.client.model.pipeline.*;
import net.minecraftforge.client.model.pipeline.LightUtil.ItemConsumer;
import net.minecraftforge.common.model.TRSRTransformation;

public final class RenderUtil {

	private static final EnumFacing[] HORIZONTALS = {
			EnumFacing.UP, EnumFacing.DOWN
	};
	private static final EnumFacing[] VERTICALS = {
			EnumFacing.WEST, EnumFacing.EAST
	};
	private static Map<ResourceLocation, IBakedModel> MODEL_CACHE = Maps.<ResourceLocation, IBakedModel>newHashMap();

	public static void drawTextureIn3D(final ResourceLocation texture, final int color) {
		final float scale = 1.0F;
		GlStateManager.translate(-scale / 2, -scale / 2, 0.0F);
		final IBakedModel model = getModel(texture);
		render(model, color);
	}

	public static IBakedModel getModel(final ResourceLocation loc) {
		if (!getModelCache().containsKey(loc)) {
			getModelCache().put(loc, new PerspectiveAwareBakedModel(ItemLayerModel.getQuadsForSprite(0, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString()), DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())), Transforms.DEFAULT_ITEM, ModelProperties.DEFAULT_ITEM));
		}
		return getModelCache().get(loc);
	}

	public static Map<ResourceLocation, IBakedModel> getModelCache() {
		return MODEL_CACHE;
	}

	public static void render(final IBakedModel model, final ItemStack stack) {
		render(model, -1, stack);
	}

	public static void render(final IBakedModel model, final int color) {
		render(model, color, ItemStack.EMPTY);
	}

	public static void render(final IBakedModel model) {
		render(model, 0x00);
	}

	public static void render(final IBakedModel model, final int color, final ItemStack stack) {
		renderQuads(model.getQuads((IBlockState) null, (EnumFacing) null, 0L), 0.0F);
	}

	public static void renderQuads(final List<BakedQuad> quads, final float alphaOverride) {
		final Tessellator tess = Tessellator.getInstance();
		final BufferBuilder buffer = tess.getBuffer();
		buffer.begin(0x07, DefaultVertexFormats.ITEM);

		final int alpha = (int) (alphaOverride * 255F) & 0xFF;
		for (final BakedQuad quad : quads) {
			int colour = -1;
			colour |= alpha << 24;
			boolean doParty = false;
			final Minecraft mc = Minecraft.getMinecraft();
			final World world = mc.world;
			final EntityPlayer player = mc.player;
			final BlockPos pos = player.getPosition();
			final int distance = 2;
			for (int x = -distance; x < distance; x++) {
				for (int y = -distance; y < distance; y++) {
					for (int z = -distance; z < distance; z++) {
						final BlockPos checkingPos = pos.add(x, y, z);
						if (world.getBlockState(checkingPos).getBlock() == Blocks.JUKEBOX) {
							final IBlockState state = world.getBlockState(checkingPos);
							if (state.getValue(BlockJukebox.HAS_RECORD).booleanValue()) {
								doParty = true;
								break;
							}
						}
					}
				}
			}
			if (doParty) {
				final Random rand = world.rand;
				final int red = rand.nextInt(255);
				final int green = rand.nextInt(255);
				final int blue = rand.nextInt(255);
				colour = (colour << 8) + red;
				colour = (colour << 8) + green;
				colour = (colour << 8) + blue;
			}
			LightUtil.renderQuadColor(buffer, quad, colour);
		}

		tess.draw();
	}

	public static void renderQuadsColored(final List<BakedQuad> quads, int color, final float alphaOverride) {
		final Tessellator tess = Tessellator.getInstance();
		final BufferBuilder buffer = tess.getBuffer();
		buffer.begin(0x07, DefaultVertexFormats.ITEM);

		final int alpha = (int) (alphaOverride * 255F) & 0xFF;
		for (final BakedQuad quad : quads) {
			color |= alpha << 24;
			boolean doParty = false;
			final Minecraft mc = Minecraft.getMinecraft();
			final World world = mc.world;
			final EntityPlayer player = mc.player;
			final BlockPos pos = player.getPosition();
			final int distance = 2;
			for (int x = -distance; x < distance; x++) {
				for (int y = -distance; y < distance; y++) {
					for (int z = -distance; z < distance; z++) {
						final BlockPos checkingPos = pos.add(x, y, z);
						if (world.getBlockState(checkingPos).getBlock() == Blocks.JUKEBOX) {
							final IBlockState state = world.getBlockState(checkingPos);
							if (state.getValue(BlockJukebox.HAS_RECORD).booleanValue()) {
								doParty = true;
								break;
							}
						}
					}
				}
			}
			if (doParty) {
				final Random rand = world.rand;
				final int red = rand.nextInt(255);
				final int green = rand.nextInt(255);
				final int blue = rand.nextInt(255);
				color = (color << 8) + red;
				color = (color << 8) + green;
				color = (color << 8) + blue;
			}
			LightUtil.renderQuadColor(buffer, quad, color);
		}

		tess.draw();
	}

	@SuppressWarnings("deprecation")
	public static void renderQuads(final BufferBuilder renderer, final List<BakedQuad> quads, final int color, final ItemStack stack) {
		final boolean flag = color == -1 && !stack.isEmpty();
		int i = 0;
		for (final int j = quads.size(); i < j; i++) {
			final BakedQuad bakedquad = quads.get(i);
			int k = color;
			if (!flag) {
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
				final float b = (float) (k & 0xFF) / 0xFF;
				final float g = (float) (k >>> 8 & 0xFF) / 0xFF;
				final float r = (float) (k >>> 16 & 0xFF) / 0xFF;
				final float a = (float) (k >>> 24 & 0xFF) / 0xFF;

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
	public static void setOpenGLColour(final int colour) {
		final Color color = new Color(colour);
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
	public static void setGreyscaleOpenGLColour(final float greyscale) {
		GlStateManager.color(greyscale, greyscale, greyscale, 1.0F);
	}

	public static void setColorForElement(final ElementEnum element) {
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

	public static int getColorForElement(final ElementEnum element) {
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

	public static void startScissor(final Minecraft minecraft, final int x, final int y, final int w, final int h) {
		final ScaledResolution scaledRes = new ScaledResolution(minecraft);
		final int scale = scaledRes.getScaleFactor();

		final int scissorWidth = w * scale;
		final int scissorHeight = h * scale;
		final int scissorX = x * scale;
		final int scissorY = minecraft.displayHeight - scissorHeight - y * scale;

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
	}

	public static void endScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public static ImmutableList<BakedQuad> getQuadsForSprites(final VertexFormat format, final Optional<TRSRTransformation> transform, final TextureAtlasSprite... sprites) {
		final int tint = 0;
		final ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
		float horizontalPixelPos = 0;
		for (final TextureAtlasSprite sprite : sprites) {
			final int uMax = sprite.getIconWidth();
			final int vMax = sprite.getIconHeight();

			final FaceData faceData = new FaceData(uMax, vMax);
			boolean translucent = false;

			for (int f = 0; f < sprite.getFrameCount(); f++) {
				final int[] pixels = sprite.getFrameTextureData(f)[0];
				boolean ptu;
				final boolean[] ptv = new boolean[uMax];
				Arrays.fill(ptv, true);
				for (int v = 0; v < vMax; v++) {
					ptu = true;
					for (int u = 0; u < uMax; u++) {
						final int alpha = getAlpha(pixels, uMax, vMax, u, v);
						final boolean t = alpha / 255f <= 0.1f;

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
			for (final EnumFacing facing : HORIZONTALS) {
				for (int v = 0; v < vMax; v++) {
					int uStart = 0, uEnd = uMax;
					boolean building = false;
					for (int u = 0; u < uMax; u++) {
						final boolean face = faceData.get(facing, u, v);
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
								final int off = facing == EnumFacing.DOWN ? 1 : 0;
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
						final int off = facing == EnumFacing.DOWN ? 1 : 0;
						builder.add(buildSideQuad(format, transform, facing, tint, sprite, uStart, v + off, uEnd - uStart, horizontalPixelPos));
					}
				}
			}

			// vertical quads
			for (final EnumFacing facing : VERTICALS) {
				for (int u = 0; u < uMax; u++) {
					int vStart = 0, vEnd = vMax;
					boolean building = false;
					for (int v = 0; v < vMax; v++) {
						final boolean face = faceData.get(facing, u, v);
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
								final int off = facing == EnumFacing.EAST ? 1 : 0;
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
						final int off = facing == EnumFacing.EAST ? 1 : 0;
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

	private static int getAlpha(final int[] pixels, final int uMax, final int vMax, final int u, final int v) {
		return pixels[u + (vMax - 1 - v) * uMax] >> 24 & 0xFF;
	}

	private static BakedQuad buildSideQuad(final VertexFormat format, final Optional<TRSRTransformation> transform, final EnumFacing side, final int tint, final TextureAtlasSprite sprite, final int u, final int v, final int size, final float horizontalOffset) {
		final float eps = 1e-2f;

		final int width = sprite.getIconWidth();
		final int height = sprite.getIconHeight();

		final float x0 = (float) u / width;
		final float y0 = (float) v / height;
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

		final float dx = side.getDirectionVec().getX() * eps / width;
		final float dy = side.getDirectionVec().getY() * eps / height;

		final float u0 = 16f * (x0 - dx);
		final float u1 = 16f * (x1 - dx);
		final float v0 = 16f * (1f - y0 - dy);
		final float v1 = 16f * (1f - y1 - dy);

		return buildQuad(format, transform, remap(side), sprite, tint, x0 + horizontalOffset, y0, z0, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0), x1 + horizontalOffset, y1, z0, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x1 + horizontalOffset, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x0 + horizontalOffset, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0));
	}

	private static EnumFacing remap(final EnumFacing side) {
		// getOpposite is related to the swapping of V direction
		return side.getAxis() == EnumFacing.Axis.Y ? side.getOpposite() : side;
	}

	private static BakedQuad buildQuad(final VertexFormat format, final Optional<TRSRTransformation> transform, final EnumFacing side, final TextureAtlasSprite sprite, final int tint, final float x0, final float y0, final float z0, final float u0, final float v0, final float x1, final float y1, final float z1, final float u1, final float v1, final float x2, final float y2, final float z2, final float u2, final float v2, final float x3, final float y3, final float z3, final float u3, final float v3) {
		final UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setQuadTint(tint);
		builder.setQuadOrientation(side);
		builder.setTexture(sprite);
		putVertex(builder, format, transform, side, x0, y0, z0, u0, v0);
		putVertex(builder, format, transform, side, x1, y1, z1, u1, v1);
		putVertex(builder, format, transform, side, x2, y2, z2, u2, v2);
		putVertex(builder, format, transform, side, x3, y3, z3, u3, v3);
		return builder.build();
	}

	private static void putVertex(final UnpackedBakedQuad.Builder builder, final VertexFormat format, final Optional<TRSRTransformation> transform, final EnumFacing side, final float x, final float y, final float z, final float u, final float v) {
		final Vector4f vec = new Vector4f();
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

	public static void drawCustomTooltip(final GuiScreen gui, final FontRenderer fr, final List<String> textList, final int x, final int y, final int subTipColor) {
		drawCustomTooltip(gui, fr, textList, x, y, subTipColor, false);
	}

	public static void drawCustomTooltip(final GuiScreen gui, final FontRenderer fr, List<String> textList, final int x, final int y, final int subTipColor, final boolean ignoremouse) {
		if (!textList.isEmpty()) {
			final Minecraft mc = Minecraft.getMinecraft();
			final ScaledResolution scaledresolution = new ScaledResolution(mc);
			final int sf = scaledresolution.getScaleFactor();
			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int max = 240;
			final int mx = Mouse.getEventX();
			boolean flip = false;
			if (!ignoremouse && (max + 24) * sf + mx > mc.displayWidth && (max = (mc.displayWidth - mx) / sf - 24) < 120) {
				max = 240;
				flip = true;
			}
			int widestLineWidth = 0;
			final Iterator<String> textLineEntry = textList.iterator();
			boolean b = false;
			while (textLineEntry.hasNext()) {
				final String textLine = textLineEntry.next();
				if (fr.getStringWidth(textLine) <= max) {
					continue;
				}
				b = true;
				break;
			}
			if (b) {
				final ArrayList<String> tl = new ArrayList<>();
				for (final String o : textList) {
					String textLine = "";
					final List<String> tl2 = fr.listFormattedStringToWidth(textLine, (textLine = o).startsWith("@@") ? max * 2 : max);
					for (final Object o2 : tl2) {
						String textLine2 = ((String) o2).trim();
						if (textLine.startsWith("@@")) {
							textLine2 = "@@" + textLine2;
						}
						tl.add(textLine2);
					}
				}
				textList = tl;
			}
			final Iterator<String> textLines = textList.iterator();
			int totalHeight = -2;
			while (textLines.hasNext()) {
				final String textLine = textLines.next();
				int lineWidth = fr.getStringWidth(textLine);
				if (textLine.startsWith("@@") && !fr.getUnicodeFlag()) {
					lineWidth /= 2;
				}
				if (lineWidth > widestLineWidth) {
					widestLineWidth = lineWidth;
				}
				totalHeight += textLine.startsWith("@@") && !fr.getUnicodeFlag() ? 7 : 10;
			}
			int sX = x + 12;
			int sY = y - 12;
			if (textList.size() > 1) {
				totalHeight += 2;
			}
			if (sY + totalHeight > scaledresolution.getScaledHeight()) {
				sY = scaledresolution.getScaledHeight() - totalHeight - 5;
			}
			if (flip) {
				sX = sX - (widestLineWidth + 24);
			}
			Minecraft.getMinecraft().getRenderItem().zLevel = 300.0f;
			final int var10 = -267386864;
			drawGradientRect(sX - 3, sY - 4, sX + widestLineWidth + 3, sY - 3, var10, var10);
			drawGradientRect(sX - 3, sY + totalHeight + 3, sX + widestLineWidth + 3, sY + totalHeight + 4, var10, var10);
			drawGradientRect(sX - 3, sY - 3, sX + widestLineWidth + 3, sY + totalHeight + 3, var10, var10);
			drawGradientRect(sX - 4, sY - 3, sX - 3, sY + totalHeight + 3, var10, var10);
			drawGradientRect(sX + widestLineWidth + 3, sY - 3, sX + widestLineWidth + 4, sY + totalHeight + 3, var10, var10);
			final int var11 = 1347420415;
			final int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
			drawGradientRect(sX - 3, sY - 3 + 1, sX - 3 + 1, sY + totalHeight + 3 - 1, var11, var12);
			drawGradientRect(sX + widestLineWidth + 2, sY - 3 + 1, sX + widestLineWidth + 3, sY + totalHeight + 3 - 1, var11, var12);
			drawGradientRect(sX - 3, sY - 3, sX + widestLineWidth + 3, sY - 3 + 1, var11, var11);
			drawGradientRect(sX - 3, sY + totalHeight + 2, sX + widestLineWidth + 3, sY + totalHeight + 3, var12, var12);
			for (int i = 0; i < textList.size(); ++i) {
				GL11.glPushMatrix();
				GL11.glTranslatef(sX, sY, 0.0f);
				String tl = textList.get(i);
				boolean shift = false;
				GL11.glPushMatrix();
				if (tl.startsWith("@@") && !fr.getUnicodeFlag()) {
					sY += 7;
					GL11.glScalef(0.5f, 0.5f, 1.0f);
					shift = true;
				}
				else {
					sY += 10;
				}
				tl = tl.replaceAll("@@", "");
				if (subTipColor != -99) {
					tl = i == 0 ? "\u00a7" + Integer.toHexString(subTipColor) + tl : "\u00a77" + tl;
				}
				GL11.glTranslated(0.0, 0.0, 301.0);
				fr.drawStringWithShadow(tl, 0.0f, shift ? 3.0f : 0.0f, -1);
				GL11.glPopMatrix();
				if (i == 0) {
					sY += 2;
				}
				GL11.glPopMatrix();
			}
			Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableRescaleNormal();
		}
	}

	public static void drawGradientRect(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
		final boolean blendon = GL11.glIsEnabled(3042);
		final float var7 = (par5 >> 24 & 255) / 255.0f;
		final float var8 = (par5 >> 16 & 255) / 255.0f;
		final float var9 = (par5 >> 8 & 255) / 255.0f;
		final float var10 = (par5 & 255) / 255.0f;
		final float var11 = (par6 >> 24 & 255) / 255.0f;
		final float var12 = (par6 >> 16 & 255) / 255.0f;
		final float var13 = (par6 >> 8 & 255) / 255.0f;
		final float var14 = (par6 & 255) / 255.0f;
		GL11.glDisable(3553);
		GL11.glEnable(3042);
		GL11.glDisable(3008);
		GL11.glBlendFunc(770, 771);
		GL11.glShadeModel(7425);
		final Tessellator var15 = Tessellator.getInstance();
		var15.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
		var15.getBuffer().pos(par3, par2, 300.0).color(var8, var9, var10, var7).endVertex();
		var15.getBuffer().pos(par1, par2, 300.0).color(var8, var9, var10, var7).endVertex();
		var15.getBuffer().pos(par1, par4, 300.0).color(var12, var13, var14, var11).endVertex();
		var15.getBuffer().pos(par3, par4, 300.0).color(var12, var13, var14, var11).endVertex();
		var15.draw();
		GL11.glShadeModel(7424);
		GlStateManager.blendFunc(770, 771);
		if (!blendon) {
			GL11.glDisable(3042);
		}
		GL11.glEnable(3008);
		GL11.glEnable(3553);
	}

	private static class FaceData {
		private final EnumMap<EnumFacing, BitSet> data = new EnumMap<>(EnumFacing.class);

		private final int vMax;

		FaceData(final int uMax, final int vMax) {
			this.vMax = vMax;

			data.put(EnumFacing.WEST, new BitSet(uMax * vMax));
			data.put(EnumFacing.EAST, new BitSet(uMax * vMax));
			data.put(EnumFacing.UP, new BitSet(uMax * vMax));
			data.put(EnumFacing.DOWN, new BitSet(uMax * vMax));
		}

		public void set(final EnumFacing facing, final int u, final int v) {
			data.get(facing).set(getIndex(u, v));
		}

		public boolean get(final EnumFacing facing, final int u, final int v) {
			return data.get(facing).get(getIndex(u, v));
		}

		private int getIndex(final int u, final int v) {
			return v * vMax + u;
		}
	}

}
