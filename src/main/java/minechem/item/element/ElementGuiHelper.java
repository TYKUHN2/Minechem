package minechem.item.element;

import minechem.client.gui.GuiPolytool;
import minechem.item.ItemElement;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class ElementGuiHelper {
	int dist;
	double radians;
	ElementEnum element;
	ItemStack stack;

	public ElementGuiHelper(int notch, double radians, ElementEnum element) {
		dist = notch * 20;
		this.radians = radians;
		this.element = element;
		stack = ItemElement.createStackOf(element, 1);
	}

	public void draw(GuiPolytool gui, long ticks) {
		GlStateManager.pushMatrix();
		// Calculate displacement
		radians += .01;
		double rad = radians;
		// if(ticks>120){
		gui.drawItemStack(stack, (int) (80 + Math.sin(rad) * dist), (int) (42 + Math.cos(rad) * dist), "");
		/* }else{ radians+=.04; int originX=88; int originY=50; int targetX=(int) (originX+(Math.sin(radians)*dist)); int targetY=(int) (originY+(Math.cos(radians)*dist));
		 *
		 * int progressX=(int) (originX+((ticks/120)*(targetX-originX))); int progressY=(int) (originY+((ticks/120)*(targetY-originY))); gui.drawItemStack((ItemStack) ItemElement.createStackOf(element,1), progressX, progressX, ""); } */
		GlStateManager.popMatrix();
	}

}
