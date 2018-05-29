package minechem.client.render;

/**
 * @author p455w0rd
 *
 */
public interface ILayer {

	default void render() {
		render(0xFFFFFFFF);
	}

	void render(int colour);

}
