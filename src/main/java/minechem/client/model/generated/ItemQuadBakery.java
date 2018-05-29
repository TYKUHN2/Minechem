package minechem.client.model.generated;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * Created by covers1624 on 13/02/2017.
 */
public class ItemQuadBakery {

	public static List<BakedQuad> bakeItem(List<TextureAtlasSprite> sprites) {
		return bakeItem(sprites, DefaultVertexFormats.ITEM, Transforms.DEFAULT_ITEM);
	}

	public static List<BakedQuad> bakeItem(List<TextureAtlasSprite> sprites, IModelState state) {
		return bakeItem(sprites, DefaultVertexFormats.ITEM, state);
	}

	public static List<BakedQuad> bakeItem(List<TextureAtlasSprite> sprites, VertexFormat format) {
		return bakeItem(sprites, format, Transforms.DEFAULT_ITEM);
	}

	public static List<BakedQuad> bakeItem(List<TextureAtlasSprite> sprites, VertexFormat format, IModelState state) {
		return bakeItem(format, state, sprites.toArray(new TextureAtlasSprite[0]));
	}

	public static List<BakedQuad> bakeItem(TextureAtlasSprite... sprites) {
		return bakeItem(Transforms.DEFAULT_ITEM, sprites);
	}

	public static List<BakedQuad> bakeItem(IModelState state, TextureAtlasSprite... sprites) {
		return bakeItem(DefaultVertexFormats.ITEM, state, sprites);
	}

	public static List<BakedQuad> bakeItem(VertexFormat format, TextureAtlasSprite... sprites) {
		return bakeItem(format, Transforms.DEFAULT_ITEM, sprites);
	}

	public static List<BakedQuad> bakeItem(VertexFormat format, IModelState state, TextureAtlasSprite... sprites) {

		checkArgument(sprites, "Sprites must not be Null or empty!");

		List<BakedQuad> quads = new LinkedList<>();
		Optional<TRSRTransformation> transform = state.apply(Optional.empty());
		for (int i = 0; i < sprites.length; i++) {
			TextureAtlasSprite sprite = sprites[i];
			quads.addAll(ItemLayerModel.getQuadsForSprite(i, sprite, format, transform));
		}
		return quads;
	}

	public <T> boolean isNullOrContainsNull(T[] input) {
		if (input != null) {
			for (T t : input) {
				if (t == null) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Provides a predicate callback for checking a condition.
	 *
	 * @param argument  The thing we are checking.
	 * @param log       What the reason for the predicate failing is.
	 * @param predicate The predicate callback.
	 * @param <E>       The thing.
	 */
	public static <E> void checkArgument(E argument, String log) {
		try {
			Preconditions.checkNotNull(argument);
		}
		catch (NullPointerException e) {
			throw new RuntimeException("Argument check failed! Reason: " + log);
		}
	}

}