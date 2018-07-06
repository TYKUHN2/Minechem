package minechem.init;

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
		//INSTANCE.registerMessage(SynthesisUpdateMessage.class, SynthesisUpdateMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(PolytoolUpdateMessage.class, PolytoolUpdateMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(GhostBlockMessage.class, GhostBlockMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(MessageActiveItem.class, MessageActiveItem.class, id++, Side.SERVER);
		INSTANCE.registerMessage(FissionUpdateMessage.class, FissionUpdateMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(FusionUpdateMessage.class, FusionUpdateMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(MessageDecomposerDumpFluid.class, MessageDecomposerDumpFluid.class, id++, Side.SERVER);
		INSTANCE.registerMessage(MessageFakeSlotScroll.class, MessageFakeSlotScroll.class, id++, Side.SERVER);
		INSTANCE.registerMessage(MessageSetMouseStack.class, MessageSetMouseStack.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncSynthesisMachine.class, MessageSyncSynthesisMachine.class, id++, Side.SERVER);
	}

}
