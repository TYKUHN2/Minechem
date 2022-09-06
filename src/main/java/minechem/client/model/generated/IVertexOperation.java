package minechem.client.model.generated;

import net.minecraftforge.common.model.IModelState;

/**
 * Represents an operation to be run for each vertex that operates on and modifies the current state
 */
public interface IVertexOperation {

	/**
	 * Load any required references and add dependencies to the pipeline based on the current model (can be null)
	 * Return false if this operation is redundant in the pipeline with the given model
	 */
	boolean load(IModelState state);

	/**
	 * Perform the operation on the current render state
	 */
	void operate(IModelState state);

	/**
	 * Get the unique id representing this type of operation. Duplicate operation IDs within the pipeline may have unexpected results.
	 * ID shoulld be obtained from CCRenderState.registerOperation() and stored in a static variable
	 */
	int operationID();
}