package minechem.client.model.generated;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.tuple.Pair;

import minechem.init.ModGlobals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * @author p455w0rd
 *
 */
public class CharacterSprite extends TextureAtlasSprite {

	private static final Map<Character, CharacterSprite> CHARCTER_SPRITE_CACHE = new HashMap<>();
	private static ResourceLocation FONT_TEXTURE;
	private final char character;

	private CharacterSprite(final char character) {
		super(ModGlobals.MODID + ":charsprite_" + String.valueOf(character) + "" + (Character.isUpperCase(character) ? "_upper" : ""));
		this.character = character;
		setIconWidth(16);
		setIconHeight(16);
	}

	public static final TextureAtlasSprite getSpriteForChar(final char character) {
		if (!CHARCTER_SPRITE_CACHE.containsKey(character)) {
			CHARCTER_SPRITE_CACHE.put(character, new CharacterSprite(character));
		}
		return CHARCTER_SPRITE_CACHE.get(character);
	}

	@Override
	public boolean hasCustomLoader(final IResourceManager manager, final ResourceLocation location) {
		return true;
	}

	@Override
	public boolean load(final IResourceManager manager, final ResourceLocation location, final Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		final int mp = Minecraft.getMinecraft().gameSettings.mipmapLevels + 1;
		final BufferedImage[] ingot_image = new BufferedImage[mp];
		IResource charmap = null;
		BufferedImage charmap_image = null;
		//PngSizeInfo sizeInfo = null;
		try {
			charmap = manager.getResource(getFontTexture());
			charmap_image = ImageIO.read(charmap.getInputStream());
			//sizeInfo = PngSizeInfo.makeFromResource(manager.getResource(getFontTexture()));
		}
		catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		if (charmap == null || charmap_image == null) {// || sizeInfo == null) {
			return false;
		}
		final int xPos = getCharPos(character).getLeft();
		final int yPos = getCharPos(character).getRight();

		final BufferedImage output_image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		//int[] new_data = new int[64];
		//charmap_image.getRGB(xPos, yPos, width, height, new_data, 0, width);
		/*
		for (int y = 0; y < height; y += width) {
		
			for (int i = 0; i < new_data.length; i++) {
				int col = new_data[i];
				if (getAlpha(col) != 0x00) {
					//int mult = getBlue(col);
					//                    new_data[i] = makeCol((getRed(avgCol) + mult) / 2, (getGreen(avgCol) + mult) / 2, (getBlue(avgCol) + mult) / 2);
					new_data[i] = 0xFFFFFFFF;
				}
			}
			// write the new image data to the output image buffer
			output_image.setRGB(0, y, width, width, new_data, 0, width);
		}
		*/
		// read pixels to new icon array
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				//new_data[i*j] = charmap_image.getRGB(i+xPos, j+yPos);
				final int color = i >= 8 || j >= 8 ? 0x00000000 : charmap_image.getRGB(i + xPos, j + yPos);
				output_image.setRGB(i, j, color);
			}
		}
		// replace the old texture
		ingot_image[0] = output_image;

		try {
			//loadSprite(PngSizeInfo.makeFromResource(manager.getResource(new ResourceLocation(ModGlobals.ID, getNugget().getName() + "_" + (getNugget().getNuggetRenderType() + 2) + ".png"))), Minecraft.getMinecraft().gameSettings.ambientOcclusion > 1.0F);
			//loadSprite(new PngSizeInfo(), true);
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();

			ImageIO.write(output_image, "PNG", stream);

			final byte[] bytes = stream.toByteArray();
			loadSpriteFrames(new IResource() {
				@Nonnull
				@Override
				public ResourceLocation getResourceLocation() {
					return location;
				}

				@Nonnull
				@Override
				public InputStream getInputStream() {
					return new ByteArrayInputStream(bytes);
				}

				@Override
				public boolean hasMetadata() {
					return false;
				}

				@Nullable
				@Override
				public <T extends IMetadataSection> T getMetadata(final String sectionName) {
					return null;
				}

				@Nonnull
				@Override
				public String getResourcePackName() {
					return ModGlobals.MODID;
				}

				@Override
				public void close() throws IOException {

				}
			}, mp);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static ResourceLocation getFontTexture() {
		if (FONT_TEXTURE == null) {
			FONT_TEXTURE = ObfuscationReflectionHelper.getPrivateValue(FontRenderer.class, Minecraft.getMinecraft().fontRenderer, "locationFontTexture");
		}
		return FONT_TEXTURE;
	}

	public Pair<Integer, Integer> getCharPos(final char ch) {
		final int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch);
		return Pair.of(i % 16 * 8, i / 16 * 8);
	}

	/*private static int getAlpha(int col) {
		return (col & 0xff000000) >> 24;
	}
	
	private static int getRed(int col) {
		return (col & 0x00ff0000) >> 16;
	}
	
	private static int getGreen(int col) {
		return (col & 0x0000ff00) >> 8;
	}
	
	private static int getBlue(int col) {
		return col & 0x000000ff;
	}
	
	private static int makeCol(int red, int green, int blue) {
		return makeCol(red, green, blue, 0xff);
	}
	
	private static int makeCol(int red, int green, int blue, int alpha) {
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}*/

}
