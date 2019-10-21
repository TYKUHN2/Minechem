package minechem.client.model.generated;

import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.Optional;

import javax.vecmath.Vector4f;

import com.google.common.collect.ImmutableList;

import minechem.init.ModGlobals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * @author p455w0rd
 *
 */
public class FontModelUtil {

	private static final EnumFacing[] HORIZONTALS = {
			EnumFacing.UP,
			EnumFacing.DOWN
	};
	private static final EnumFacing[] VERTICALS = {
			EnumFacing.WEST,
			EnumFacing.EAST
	};
	private static final ResourceLocation FONT_TEXTURE = new ResourceLocation(ModGlobals.MODID, "textures/models/font.png");;

	public static ResourceLocation getFontTexture() {
		return FONT_TEXTURE;
	}

	public static ImmutableList<BakedQuad> getQuadsForChar(char ch, int tint, VertexFormat format, Optional<TRSRTransformation> transform) {
		return getQuadsForChar(ch, tint, getFontTexture(), format, transform);
	}

	public static ImmutableList<BakedQuad> getQuadsForChar(char ch, int tint, ResourceLocation sprite, VertexFormat format, Optional<TRSRTransformation> transform) {
		return getQuadsForChar(ch, tint, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(sprite.toString()), format, transform);
	}

	public static ImmutableList<BakedQuad> getQuadsForChar(char ch, int tint, TextureAtlasSprite sprite, VertexFormat format, Optional<TRSRTransformation> transform) {

		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
		int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch);
		int uMax = i % 16 * 8;
		int vMax = i / 16 * 8;

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
							builder.add(buildSideQuad(format, transform, facing, tint, sprite, uStart, v + off, u - uStart));
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
					builder.add(buildSideQuad(format, transform, facing, tint, sprite, uStart, v + off, uEnd - uStart));
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
							builder.add(buildSideQuad(format, transform, facing, tint, sprite, u + off, vStart, v - vStart));
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
					builder.add(buildSideQuad(format, transform, facing, tint, sprite, u + off, vStart, vEnd - vStart));
				}
			}
		}

		// front
		builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint, 0, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(), 0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(), 1, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(), 1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()));
		// back
		builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint, 0, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(), 1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(), 0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()));

		return builder.build();

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

	private static int getAlpha(int[] pixels, int uMax, int vMax, int u, int v) {
		return pixels[u + (vMax - 1 - v) * uMax] >> 24 & 0xFF;
	}

	private static BakedQuad buildSideQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int u, int v, int size) {
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

		return buildQuad(format, transform, remap(side), sprite, tint, x0, y0, z0, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0), x1, y1, z0, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x1, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x0, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0));
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

}
