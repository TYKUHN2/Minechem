package minechem.init;

import minechem.block.tile.TileBlueprintProjector;
import minechem.block.tile.TileDecomposer;
import minechem.block.tile.TileEntityProxy;
import minechem.block.tile.TileLeadedChest;
import minechem.block.tile.TileMicroscope;
import minechem.block.tile.TileSynthesis;
import minechem.client.gui.GuiBlueprintProjector;
import minechem.client.gui.GuiChemistJournal;
import minechem.client.gui.GuiDecomposer;
import minechem.client.gui.GuiLeadedChest;
import minechem.client.gui.GuiMicroscope;
import minechem.client.gui.GuiPolytool;
import minechem.client.gui.GuiSynthesis;
import minechem.client.gui.GuiTableOfElements;
import minechem.container.ChemistJournalContainer;
import minechem.container.ContainerBlueprintProjector;
import minechem.container.ContainerDecomposer;
import minechem.container.ContainerLeadedChest;
import minechem.container.ContainerMicroscope;
import minechem.container.ContainerPolytool;
import minechem.container.ContainerSynthesis;
import minechem.tileentity.multiblock.fission.FissionContainer;
import minechem.tileentity.multiblock.fission.FissionGui;
import minechem.tileentity.multiblock.fission.FissionTileEntity;
import minechem.tileentity.multiblock.fusion.FusionContainer;
import minechem.tileentity.multiblock.fusion.FusionGui;
import minechem.tileentity.multiblock.fusion.FusionTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModGuiHandler implements IGuiHandler {

	public static final int GUI_ID_TILEENTITY = 0;
	public static final int GUI_ID_JOURNAL = 1;
	public static final int GUI_TABLE = 2;

	public static final int GUI_ID_POLYTOOL = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUI_ID_JOURNAL) {
			return getServerGuiElementForJournal(player, world);
		}
		if (ID == GUI_ID_POLYTOOL) {
			return getServerGuiElementForPolytool(player, world);
		}
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof TileDecomposer) {
			return new ContainerDecomposer(player.inventory, (TileDecomposer) tileEntity);
		}
		if (tileEntity instanceof TileLeadedChest) {
			return new ContainerLeadedChest(player.inventory, (TileLeadedChest) tileEntity);
		}
		if (tileEntity instanceof TileMicroscope) {
			return new ContainerMicroscope(player.inventory, (TileMicroscope) tileEntity);
		}
		if (tileEntity instanceof TileSynthesis) {
			return new ContainerSynthesis(player.inventory, (TileSynthesis) tileEntity);
		}
		if (tileEntity instanceof FusionTileEntity) {
			return new FusionContainer(player.inventory, (FusionTileEntity) tileEntity);
		}
		if (tileEntity instanceof FissionTileEntity) {
			return new FissionContainer(player.inventory, (FissionTileEntity) tileEntity);
		}

		if (tileEntity instanceof TileEntityProxy) {
			return getServerGuiElementFromProxy((TileEntityProxy) tileEntity, player);
		}

		if (tileEntity instanceof TileBlueprintProjector) {
			return new ContainerBlueprintProjector(player.inventory, (TileBlueprintProjector) tileEntity);
		}
		return null;
	}

	private Object getServerGuiElementForPolytool(EntityPlayer player, World world) {

		return new ContainerPolytool(player);
	}

	public Object getServerGuiElementFromProxy(TileEntityProxy proxy, EntityPlayer player) {
		TileEntity tileEntity = proxy.getManager();
		if (tileEntity instanceof FusionTileEntity) {
			return new FusionContainer(player.inventory, (FusionTileEntity) tileEntity);
		}

		if (tileEntity instanceof FissionTileEntity) {
			return new FissionContainer(player.inventory, (FissionTileEntity) tileEntity);
		}
		return null;
	}

	public Object getServerGuiElementForJournal(EntityPlayer entityPlayer, World world) {
		return new ChemistJournalContainer(entityPlayer.inventory);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUI_ID_JOURNAL) {
			return getClientGuiElementForJournal(player, world);
		}

		if (ID == GUI_TABLE) {
			return getClientGuiForJournal(player, world);
		}

		if (ID == GUI_ID_POLYTOOL) {
			return getClientGuiForPolytool(player, world);
		}

		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		if (tileEntity instanceof TileDecomposer) {
			return new GuiDecomposer(player.inventory, (TileDecomposer) tileEntity);
		}
		if (tileEntity instanceof TileLeadedChest) {
			return new GuiLeadedChest(player.inventory, (TileLeadedChest) tileEntity);
		}
		if (tileEntity instanceof TileMicroscope) {
			return new GuiMicroscope(player.inventory, (TileMicroscope) tileEntity);
		}
		if (tileEntity instanceof TileSynthesis) {
			return new GuiSynthesis(player.inventory, (TileSynthesis) tileEntity);
		}
		if (tileEntity instanceof FusionTileEntity) {
			return new FusionGui(player.inventory, (FusionTileEntity) tileEntity);
		}
		if (tileEntity instanceof TileEntityProxy) {
			return getClientGuiElementFromProxy((TileEntityProxy) tileEntity, player);
		}
		if (tileEntity instanceof TileBlueprintProjector) {
			return new GuiBlueprintProjector(player.inventory, (TileBlueprintProjector) tileEntity);
		}
		if (tileEntity instanceof FissionTileEntity) {
			return new FissionGui(player.inventory, (FissionTileEntity) tileEntity);
		}
		return null;
	}

	private GuiPolytool getClientGuiForPolytool(EntityPlayer player, World world) {

		return new GuiPolytool(new ContainerPolytool(player));
	}

	public Object getClientGuiElementFromProxy(TileEntityProxy proxy, EntityPlayer player) {
		TileEntity tileEntity = proxy.getManager();
		if (tileEntity instanceof FusionTileEntity) {
			return new FusionGui(player.inventory, (FusionTileEntity) tileEntity);
		}

		if (tileEntity instanceof FissionTileEntity) {
			return new FissionGui(player.inventory, (FissionTileEntity) tileEntity);
		}
		return null;
	}

	public Object getClientGuiElementForJournal(EntityPlayer player, World world) {
		return new GuiChemistJournal(player);
	}

	public Object getClientGuiForJournal(EntityPlayer player, World world) {
		return new GuiTableOfElements(player);
	}

}
