package minechem.init;

import minechem.network.message.ChemistJournalActiveItemMessage;
import minechem.network.message.DecomposerDumpFluidMessage;
import minechem.network.message.FissionUpdateMessage;
import minechem.network.message.FusionUpdateMessage;
import minechem.network.message.GhostBlockMessage;
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
		INSTANCE.registerMessage(ChemistJournalActiveItemMessage.class, ChemistJournalActiveItemMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(FissionUpdateMessage.class, FissionUpdateMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(FusionUpdateMessage.class, FusionUpdateMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(DecomposerDumpFluidMessage.class, DecomposerDumpFluidMessage.class, id++, Side.SERVER);
	}

}
