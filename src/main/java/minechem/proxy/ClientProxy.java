package minechem.proxy;

import minechem.block.multiblock.tile.TileGhostBlock;
import minechem.block.tile.TileDecomposer;
import minechem.block.tile.TileLeadedChest;
import minechem.block.tile.TileSynthesis;
import minechem.client.render.RenderDecomposer;
import minechem.client.render.RenderGhostBlock;
import minechem.client.render.RenderLeadedChest;
import minechem.client.render.RenderSynthesis;
import minechem.init.ModBlocks;
import minechem.init.ModFluids;
import minechem.init.ModItems;
import minechem.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(ClientProxy.class);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ModItems.molecule.getColorHandler(), ModItems.molecule);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@Override
	public void loadComplete(FMLLoadCompleteEvent event) {
		super.loadComplete(event);
	}

	@Override
	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileDecomposer.class, new RenderDecomposer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSynthesis.class, new RenderSynthesis());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLeadedChest.class, new RenderLeadedChest());
		ClientRegistry.bindTileEntitySpecialRenderer(TileGhostBlock.class, new RenderGhostBlock());
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		ModItems.registerRenderers();
		ModBlocks.registerRenderers();
		ModFluids.initModels();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onPreRender(RenderGameOverlayEvent.Pre e) {
		ModPotions.renderEffects();
	}

}
