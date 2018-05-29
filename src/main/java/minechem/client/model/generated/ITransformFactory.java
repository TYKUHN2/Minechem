package minechem.client.model.generated;

import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIXED;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GROUND;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GUI;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.HEAD;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonObject;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * Created by covers1624 on 10/07/2017.
 */
public interface ITransformFactory {

	/**
	 * Allows for custom deserialization of transforms through CCL's BlockState pipeline.
	 *
	 * @param object The JSONObject containing your data.
	 * @return The composed IModelState.
	 */
	IModelState getModelState(JsonObject object);

	/**
	 * One �ber method for de-serializing.
	 */
	public interface IStandardTransformFactory extends ITransformFactory {

		@Override
		default IModelState getModelState(JsonObject object) {

			Map<TransformType, TRSRTransformation> map = new HashMap<>();

			TRSRTransformation thirdRh = load(this, object, THIRD_PERSON_RIGHT_HAND, "thirdperson_righthand");
			TRSRTransformation thirdLh = load(this, object, THIRD_PERSON_LEFT_HAND, "thirdperson_lefthand");
			if (thirdLh == TRSRTransformation.identity()) {
				thirdLh = thirdRh;
			}
			map.put(THIRD_PERSON_RIGHT_HAND, thirdRh);
			map.put(THIRD_PERSON_LEFT_HAND, thirdLh);

			TRSRTransformation firstRh = load(this, object, FIRST_PERSON_RIGHT_HAND, "firstperson_righthand");
			TRSRTransformation firstLh = load(this, object, FIRST_PERSON_LEFT_HAND, "firstperson_lefthand");
			if (firstLh == TRSRTransformation.identity()) {
				firstLh = firstRh;
			}
			map.put(FIRST_PERSON_RIGHT_HAND, firstRh);
			map.put(FIRST_PERSON_LEFT_HAND, firstLh);

			TRSRTransformation head = load(this, object, HEAD, "firstperson_lefthand");
			TRSRTransformation gui = load(this, object, GUI, "firstperson_lefthand");
			TRSRTransformation ground = load(this, object, GROUND, "firstperson_lefthand");
			TRSRTransformation fixed = load(this, object, FIXED, "firstperson_lefthand");

			map.put(HEAD, head);
			map.put(GUI, gui);
			map.put(GROUND, ground);
			map.put(FIXED, fixed);
			return part -> Optional.ofNullable(map.get(part.get()));
		}

		/**
		 * Called to deserialize the transform for a specific type.
		 *
		 * @param type   The type.
		 * @param object The json object.
		 * @return The TRSRTransformation.
		 */
		TRSRTransformation getTransform(TransformType type, JsonObject object);

		static TRSRTransformation load(IStandardTransformFactory me, JsonObject object, TransformType type, String name) {
			if (object.has(name)) {
				return me.getTransform(type, object.getAsJsonObject(name));
			}
			return TRSRTransformation.identity();
		}
	}
}