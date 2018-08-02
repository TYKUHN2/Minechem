package minechem.init;

import minechem.init.ModIntegration.Mods;
import minechem.integration.JEI;
import minechem.network.message.FissionUpdateMessage;
import minechem.network.message.FusionUpdateMessage;
import minechem.network.message.GhostBlockMessage;
import minechem.network.message.MessageActiveItem;
import minechem.network.message.MessageDecomposerDumpFluid;
import minechem.network.message.MessageFakeSlotScroll;
import minechem.network.message.MessageSetMouseStack;
import minechem.network.message.MessageSyncSynthesisMachine;
import minechem.network.message.PolytoolUpdateMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetworking {
	public static SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ModGlobals.ID);
	public static int id = 0;

	public static void init() {
		//INSTANCE.registerMessage(SynthesisUpdateMessage.class, SynthesisUpdateMessage.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(PolytoolUpdateMessage.class, PolytoolUpdateMessage.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(GhostBlockMessage.class, GhostBlockMessage.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(MessageActiveItem.class, MessageActiveItem.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(FissionUpdateMessage.class, FissionUpdateMessage.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(FusionUpdateMessage.class, FusionUpdateMessage.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(MessageDecomposerDumpFluid.class, MessageDecomposerDumpFluid.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(MessageFakeSlotScroll.class, MessageFakeSlotScroll.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(MessageSetMouseStack.class, MessageSetMouseStack.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncSynthesisMachine.class, MessageSyncSynthesisMachine.class, nextID(), Side.SERVER);
		if (Mods.JEI.isLoaded()) {
			JEI.registerPackets();
		}
	}

	public static int nextID() {
		return id++;
	}

}
