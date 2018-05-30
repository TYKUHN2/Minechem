package minechem.proxy;

import minechem.init.ModEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
	public static int RENDER_ID;

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void registerRenderers() {

	}

	public void registerTickHandlers() {
		ModEvents.init();
	}

	public EntityPlayer getPlayer(MessageContext context) {
		return context.getServerHandler().player;
	}

	public void setTEISR(Item item, Object renderer) {
	}

}
