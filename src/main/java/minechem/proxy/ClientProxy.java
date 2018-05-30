package minechem.proxy;

import minechem.Minechem;
import minechem.block.tile.TileDecomposer;
import minechem.block.tile.TileLeadedChest;
import minechem.block.tile.TileSynthesis;
import minechem.client.render.ElementItemRenderer;
import minechem.client.render.RenderDecomposer;
import minechem.client.render.RenderLeadedChest;
import minechem.client.render.RenderSynthesis;
import minechem.init.ModBlocks;
import minechem.init.ModGuiHandler;
import minechem.init.ModItems;
import minechem.init.ModLogger;
import minechem.item.molecule.MoleculeEnum;
import minechem.utils.MinechemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// import minechem.render.FluidItemRenderingHandler;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		ModItems.element.setTileEntityItemStackRenderer(new ElementItemRenderer());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			MoleculeEnum molecule = MinechemUtil.getMolecule(stack);
			if (molecule == null) {
				return -1;
			}
			int color = 0xFFFFFFFF;
			if (tintIndex == 1) {
				color = molecule.getColor1();
			}
			if (tintIndex == 2) {
				color = molecule.getColor2();
			}
			return color;
		}, ModItems.molecule);
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

	@Override
	public EntityPlayer getPlayer(MessageContext context) {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public void setTEISR(Item item, Object renderer) {
		if (renderer != null && renderer instanceof TileEntityItemStackRenderer) {
			item.setTileEntityItemStackRenderer((TileEntityItemStackRenderer) renderer);
		}
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		ModItems.registerRenderers();
		ModBlocks.registerRenderers();
	}

}
