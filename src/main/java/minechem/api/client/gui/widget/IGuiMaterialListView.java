package minechem.api.client.gui.widget;

import java.util.List;

import minechem.client.gui.GuiBlueprintProjector;

/**
 * @author p455w0rd
 *
 */
public interface IGuiMaterialListView {

	List<IGuiMaterialListSlot> getMaterialList();

	GuiBlueprintProjector getGui();

	int getX();

	int getY();

	int getWidth();

	int getHeight();

	int getEntryHeight();

	void draw();

}
