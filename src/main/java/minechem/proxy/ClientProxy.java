package minechem.proxy;

import minechem.Minechem;
import minechem.block.tile.TileDecomposer;
import minechem.block.tile.TileLeadedChest;
import minechem.block.tile.TileSynthesis;
import minechem.client.render.RenderDecomposer;
import minechem.client.render.RenderLeadedChest;
import minechem.client.render.RenderSynthesis;
import minechem.init.ModBlocks;
import minechem.init.ModFluids;
import minechem.init.ModGuiHandler;
import minechem.init.ModItems;
import minechem.init.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void init(FMLInitializationEvent event) {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ModItems.molecule.getColorHandler(), ModItems.molecule);
	}

	@Override
	public void registerRenderers() {
		ModLogger.debug("Registering GUI and Container handlers...");
		NetworkRegistry.INSTANCE.registerGuiHandler(Minechem.INSTANCE, new ModGuiHandler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileDecomposer.class, new RenderDecomposer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSynthesis.class, new RenderSynthesis());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLeadedChest.class, new RenderLeadedChest());
	}

	@Override
	public void registerTickHandlers() {
		super.registerTickHandlers();
		MinecraftForge.EVENT_BUS.register(ClientProxy.class);
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		ModItems.registerRenderers();
		ModBlocks.registerRenderers();
		ModFluids.initModels();
	}

}
