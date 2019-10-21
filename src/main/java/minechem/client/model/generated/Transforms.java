package minechem.client.model.generated;

import java.util.*;
import java.util.Map.Entry;

import javax.vecmath.Vector3f;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;

import minechem.client.model.generated.ITransformFactory.IStandardTransformFactory;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ForgeBlockStateV1.TRSRDeserializer;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.ModContainer;

/**
 * Created by covers1624 on 5/16/2016.
 * This is mostly just extracted from the ForgeBlockStateV1.
 * Credits to Rain Warrior.
 */
public class Transforms {

	private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(TRSRTransformation.class, TRSRDeserializer.INSTANCE).create();

	private static Map<ResourceLocation, ITransformFactory> transformFactories = new HashMap<>();

	public static final IModelState DEFAULT_BLOCK;
	public static final IModelState DEFAULT_ITEM;
	public static final IModelState DEFAULT_TOOL;
	public static final IModelState DEFAULT_BOW;
	public static final IModelState DEFAULT_HANDHELD_ROD;

	static {
		Map<TransformType, TRSRTransformation> map;
		TRSRTransformation thirdPerson;
		TRSRTransformation firstPerson;

		//@formatter:off
        map = new HashMap<>();
        thirdPerson =                                   create(0F,2.5F, 0F,75F, 45F, 0F,0.375F );
        map.put(TransformType.GUI,                      create(0F,  0F, 0F,30F,225F, 0F,0.625F));
        map.put(TransformType.GROUND,                   create(0F,  3F, 0F, 0F,  0F, 0F, 0.25F));
        map.put(TransformType.FIXED,                    create(0F,  0F, 0F, 0F,  0F, 0F,  0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  thirdPerson);
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   flipLeft(thirdPerson));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(0F, 0F, 0F, 0F, 45F, 0F, 0.4F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(0F, 0F, 0F, 0F, 225F, 0F, 0.4F));
        DEFAULT_BLOCK = part -> null;

        map = new HashMap<>();
        thirdPerson =                                    create(   0F,  3F,   1F, 0F,  0F, 0F, 0.55F);
        firstPerson =                                    create(1.13F,3.2F,1.13F, 0F,-90F,25F, 0.68F);
        map.put(TransformType.GROUND,                    create(   0F,  2F,   0F, 0F,  0F, 0F, 0.5F));
        map.put(TransformType.HEAD,                      create(   0F, 13F,   7F, 0F,180F, 0F,   1F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,   thirdPerson);
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,    flipLeft(thirdPerson));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,   firstPerson);
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,    flipLeft(firstPerson));
        DEFAULT_ITEM = part -> null;

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(   0F,  2F,   0F, 0F,  0F, 0F, 0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(   0F,  4F, 0.5F, 0F,-90F, 55,0.85F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(   0F,  4F, 0.5F, 0F, 90F,-55,0.85F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(1.13F,3.2F,1.13F, 0F,-90F, 25,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(1.13F,3.2F,1.13F, 0F, 90F,-25,0.68F));
        DEFAULT_TOOL = part -> null;

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(   0F,  2F,   0F,  0F,   0F,  0F, 0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(  -1F, -2F, 2.5F,-80F, 260F,-40F, 0.9F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(  -1F, -2F, 2.5F,-80F,-280F, 40F, 0.9F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(1.13F,3.2F,1.13F,  0F, -90F, 25F,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(1.13F,3.2F,1.13F,  0F,  90F,-25F,0.68F));
        DEFAULT_BOW = part -> null;

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(0F, 2F,   0F, 0F,  0F,  0F, 0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(0F,  4F,2.5F, 0F, 90F, 55F,0.85F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(0F,  4F,2.5F, 0F,-90F,-55F,0.85F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(0F,1.6F,0.8F, 0F, 90F, 25F,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(0F,1.6F,0.8F, 0F,-90F,-25F,0.68F));
        DEFAULT_HANDHELD_ROD = part -> null;
        //@formatter:on
		registerDefaultFactories();
	}

	/**
	 * Creates a new TRSRTransformation.
	 *
	 * @param tx The x transform.
	 * @param ty The y transform.
	 * @param tz The z transform.
	 * @param rx The x Axis rotation.
	 * @param ry The y Axis rotation.
	 * @param rz The z Axis rotation.
	 * @param s  The scale.
	 * @return The new TRSRTransformation.
	 */
	public static TRSRTransformation create(final float tx, final float ty, final float tz, final float rx, final float ry, final float rz, final float s) {
		return create(new Vector3f(tx / 16, ty / 16, tz / 16), new Vector3f(rx, ry, rz), new Vector3f(s, s, s));
	}

	/**
	 * Creates a new TRSRTransformation.
	 *
	 * @param transform The transform.
	 * @param rotation  The rotation.
	 * @param scale     The scale.
	 * @return The new TRSRTransformation.
	 */
	public static TRSRTransformation create(final Vector3 transform, final Vector3 rotation, final Vector3 scale) {
		return create(transform.vector3f(), rotation.vector3f(), scale.vector3f());
	}

	/**
	 * Creates a new TRSRTransformation.
	 *
	 * @param transform The transform.
	 * @param rotation  The rotation.
	 * @param scale     The scale.
	 * @return The new TRSRTransformation.
	 */
	public static TRSRTransformation create(final Vector3f transform, final Vector3f rotation, final Vector3f scale) {
		return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(transform, TRSRTransformation.quatFromXYZDegrees(rotation), scale, null));
	}

	/**
	 * Flips the transform for the left hand.
	 *
	 * @param transform The right hand transform.
	 * @return The new left hand transform.
	 */
	public static TRSRTransformation flipLeft(final TRSRTransformation transform) {
		return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
	}

	/**
	 * Called from CCBlockStateLoader to load the transform factories for a specific mod container.
	 *
	 * @param mod        The mod.
	 * @param transforms The JsonObject holding the transform factory data.
	 */
	public static void loadTransformFactory(final ModContainer mod, final JsonObject transforms) {
		for (final Entry<String, JsonElement> entry : transforms.entrySet()) {
			if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
				final String key = entry.getKey();
				final String value = entry.getValue().getAsString();
				try {
					final Class<?> clazz = Class.forName(value);
					if (ITransformFactory.class.isAssignableFrom(clazz)) {
						registerTransformFactory(new ResourceLocation(mod.getModId(), key), (ITransformFactory) clazz.newInstance());
					}
					else {
						throw new JsonSyntaxException("Class '" + value + "' is not an instance of ITransformFactory");
					}
				}
				catch (final ClassNotFoundException e) {
					throw new JsonSyntaxException("Could not find class: '" + value + "'!", e);
				}
				catch (InstantiationException | IllegalAccessException e) {
					throw new JsonSyntaxException("Could not instantiate '" + value + "'!", e);
				}
			}
			else {
				throw new JsonParseException("transforms: Entry is expected to be a string and is not.. " + entry.getValue());
			}
		}
	}

	/**
	 * Registers a transformation factory.
	 *
	 * @param type    The factory identifier.
	 * @param factory The Factory instance.
	 */
	public static void registerTransformFactory(final ResourceLocation type, final ITransformFactory factory) {
		if (transformFactories.containsKey(type)) {
			//CCLLog.big(Level.WARN, "Overriding already registered transform factory for type '%s', this may cause issues...", type);
		}
		transformFactories.put(type, factory);
	}

	/**
	 * Retrieves a registered transformation factory.
	 *
	 * @param type The factory identifier.
	 * @return The factory.
	 */
	public static ITransformFactory getTransformFactory(final ResourceLocation type) {
		if (!transformFactories.containsKey(type)) {
			throw new IllegalArgumentException(String.format("Unable to get TransformFactory for unregistered type{%s}!", type));
		}
		return transformFactories.get(type);
	}

	public static void registerDefaultFactories() {
		registerTransformFactory(new ResourceLocation("minecraft:default"), new IStandardTransformFactory() {

			@Override
			public TRSRTransformation getTransform(final TransformType type, final JsonObject object) {
				final Vector3 rot = parseVec3(object, "rotation", Vector3.zero.copy());
				final Vector3 trans = parseVec3(object, "translation", Vector3.zero.copy());
				trans.multiply(1D / 16D);
				trans.x = clip(trans.x, -5.0D, 5.0D);
				trans.y = clip(trans.y, -5.0D, 5.0D);
				trans.z = clip(trans.z, -5.0D, 5.0D);
				final Vector3 scale = parseVec3(object, "scale", Vector3.one);
				scale.x = clip(scale.x, -4.0D, 4.0D);
				scale.y = clip(scale.y, -4.0D, 4.0D);
				scale.z = clip(scale.z, -4.0D, 4.0D);
				return create(trans, rot, scale);
			}

			private Vector3 parseVec3(final JsonObject object, final String key, final Vector3 defaultValue) {
				if (object.has(key)) {
					final JsonArray array = JsonUtils.getJsonArray(object, key);
					if (array.size() == 3) {
						final float[] floats = new float[3];
						for (int i = 0; i < 3; i++) {
							floats[i] = JsonUtils.getFloat(array.get(i), key + "[ " + i + " ]");
						}
						return new Vector3(floats);
					}
					throw new JsonParseException("Expected 3 " + key + " values, found: " + array.size());
				}
				return defaultValue;
			}
		});
	}

	public static double clip(double value, final double min, final double max) {
		if (value > max) {
			value = max;
		}
		if (value < min) {
			value = min;
		}
		return value;
	}

	/**
	 * Reimplementation from ForgeBlockStateV1's variant Deserializer.
	 *
	 * @param json The Json that contains either ModelRotation x,y,TRSRTransforms or CCL defaults.
	 * @return A IModelState.
	 */
	@SuppressWarnings("deprecation")
	public static Optional<IModelState> parseFromJson(final JsonObject json) {
		Optional<IModelState> ret = Optional.empty();
		if (json.has("x") || json.has("y")) {
			final int x = JsonUtils.getInt(json, "x", 0);
			final int y = JsonUtils.getInt(json, "y", 0);
			final ModelRotation rot = ModelRotation.getModelRotation(x, y);
			ret = Optional.of(new TRSRTransformation(rot));
			if (!ret.isPresent()) {
				throw new JsonParseException("Invalid BlockModelRotation x: " + x + " y: " + y);
			}
		}
		if (json.has("transform")) {
			final JsonElement transformElement = json.get("transform");
			if (transformElement.isJsonPrimitive() && transformElement.getAsJsonPrimitive().isString()) {
				final String transform = transformElement.getAsString();
				switch (transform) {
				case "identity":
					ret = Optional.of(TRSRTransformation.identity());
					break;
				case "ccl:default-block":
					ret = Optional.of(DEFAULT_BLOCK);
					break;
				case "ccl:default-item":
					ret = Optional.of(DEFAULT_ITEM);
					break;
				case "ccl:default-tool":
					ret = Optional.of(DEFAULT_TOOL);
					break;
				case "ccl:default-bow":
					ret = Optional.of(DEFAULT_BOW);
					break;
				case "ccl:default-handheld-rod":
					ret = Optional.of(DEFAULT_HANDHELD_ROD);
					break;
				}
			}
			else if (!transformElement.isJsonObject()) {
				try {
					final TRSRTransformation base = GSON.fromJson(transformElement, TRSRTransformation.class);
					ret = Optional.of(TRSRTransformation.blockCenterToCorner(base));
				}
				catch (final JsonParseException e) {
					throw new JsonParseException("transform: expected a string, object or valid base transformation, got: " + transformElement);
				}
			}
			else {
				final JsonObject transform = transformElement.getAsJsonObject();
				if (transform.has("type")) {
					final JsonElement typeElement = transform.get("type");
					if (typeElement.isJsonPrimitive() && typeElement.getAsJsonPrimitive().isString()) {
						final ResourceLocation type = new ResourceLocation(typeElement.getAsString());
						try {
							final ITransformFactory factory = getTransformFactory(type);
							ret = Optional.of(factory.getModelState(transform));
						}
						catch (final IllegalArgumentException e) {
							throw new JsonParseException("Unregistered type!" + type, e);
						}
					}
					else {
						throw new JsonParseException("type: expected as string but was not a string. got: " + typeElement);
					}
				}
				else {
					//TODO, move this to a factory.
					final EnumMap<TransformType, TRSRTransformation> transforms = Maps.newEnumMap(TransformType.class);
					if (transform.has("thirdperson")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("thirdperson"), TRSRTransformation.class);
						transform.remove("thirdperson");
						transforms.put(TransformType.THIRD_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("thirdperson_righthand")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("thirdperson_righthand"), TRSRTransformation.class);
						transform.remove("thirdperson_righthand");
						transforms.put(TransformType.THIRD_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("thirdperson_lefthand")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("thirdperson_lefthand"), TRSRTransformation.class);
						transform.remove("thirdperson_lefthand");
						transforms.put(TransformType.THIRD_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("firstperson")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("firstperson"), TRSRTransformation.class);
						transform.remove("firstperson");
						transforms.put(TransformType.FIRST_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("firstperson_righthand")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("firstperson_righthand"), TRSRTransformation.class);
						transform.remove("firstperson_righthand");
						transforms.put(TransformType.FIRST_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("firstperson_lefthand")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("firstperson_lefthand"), TRSRTransformation.class);
						transform.remove("firstperson_lefthand");
						transforms.put(TransformType.FIRST_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("head")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("head"), TRSRTransformation.class);
						transform.remove("head");
						transforms.put(TransformType.HEAD, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("gui")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("gui"), TRSRTransformation.class);
						transform.remove("gui");
						transforms.put(TransformType.GUI, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("ground")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("ground"), TRSRTransformation.class);
						transform.remove("ground");
						transforms.put(TransformType.GROUND, TRSRTransformation.blockCenterToCorner(t));
					}
					if (transform.has("fixed")) {
						final TRSRTransformation t = GSON.fromJson(transform.get("fixed"), TRSRTransformation.class);
						transform.remove("fixed");
						transforms.put(TransformType.FIXED, TRSRTransformation.blockCenterToCorner(t));
					}
					int k = transform.entrySet().size();
					if (transform.has("matrix")) {
						k--;
					}
					if (transform.has("translation")) {
						k--;
					}
					if (transform.has("rotation")) {
						k--;
					}
					if (transform.has("scale")) {
						k--;
					}
					if (transform.has("post-rotation")) {
						k--;
					}
					if (k > 0) {
						throw new JsonParseException("transform: allowed keys: 'thirdperson', 'firstperson', 'gui', 'head', 'matrix', 'translation', 'rotation', 'scale', 'post-rotation'");
					}
					TRSRTransformation base = TRSRTransformation.identity();
					if (!transform.entrySet().isEmpty()) {
						base = GSON.fromJson(transform, TRSRTransformation.class);
						base = TRSRTransformation.blockCenterToCorner(base);
					}
					IModelState state;
					if (transforms.isEmpty()) {
						state = base;
					}
					else {
						state = part -> {
							final ImmutableMap<TransformType, TRSRTransformation> map = Maps.immutableEnumMap(transforms);
							if (!part.isPresent() || !(part.get() instanceof TransformType) || !map.containsKey(part.get())) {
								TRSRTransformation base2 = TRSRTransformation.identity();
								if (!transform.entrySet().isEmpty()) {
									base2 = GSON.fromJson(transform, TRSRTransformation.class);
									base2 = TRSRTransformation.blockCenterToCorner(base2);
								}
								return Optional.of(base2);
							}
							return Optional.ofNullable(map.get(part.get()));
						};
					}
					ret = Optional.of(state);
				}
			}
		}
		return ret;
	}

	public static TRSRTransformation fromMatrix4(final Matrix4 matrix4) {
		return new TRSRTransformation(matrix4.toMatrix4f());
	}
}