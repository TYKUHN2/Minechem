package minechem.init;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * @author p455w0rd
 *
 */
public class ModRendering {

	private static final ModelResourceLocation TEISR_LOC = new ModelResourceLocation(new ResourceLocation(ModGlobals.ID, "teisr"), "inventory");
	public static final ModelResourceLocation ITEM_ELEMENT_LOC = new ModelResourceLocation(new ResourceLocation(ModGlobals.ID, "tube_filled"), "inventory");
	public static final ModelResourceLocation ITEM_MOLECULE_LOC = new ModelResourceLocation(new ResourceLocation(ModGlobals.ID, "molecule"), "inventory");
	private static List<Pair<ModelResourceLocation, IBakedModel>> particleModels = new LinkedList<>();

	public static void setItemTEISR(final Item item, final TileEntityItemStackRenderer renderer) {
		setItemTEISR(item, renderer, 0);
	}

	public static void setItemTEISR(final Item item, final TileEntityItemStackRenderer renderer, final int meta) {
		setItemTEISR(item, renderer, meta, TEISR_LOC);
	}

	public static void setItemTEISR(final Item item, final TileEntityItemStackRenderer renderer, final int meta, final ModelResourceLocation location) {
		ModelLoader.setCustomModelResourceLocation(item, meta, location);
		item.setTileEntityItemStackRenderer(renderer);
	}

	public static void setBlockTEISR(final Block block, final TileEntityItemStackRenderer renderer) {
		setItemTEISR(Item.getItemFromBlock(block), renderer);
	}

	public static void registerParticle(final ModelResourceLocation location, final IBakedModel model) {
		particleModels.add(new ImmutablePair<>(location, model));
	}

	public static <T extends TileEntity> void setBlockRendering(final Block block, final TileEntitySpecialRenderer<? super T> blockRenderer, final Class<T> tile, final TileEntityItemStackRenderer itemRenderer, final ResourceLocation particleTexture) {
		setItemTEISR(Item.getItemFromBlock(block), itemRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(tile, blockRenderer);

		final ModelResourceLocation particleLoc = new ModelResourceLocation(block.getRegistryName(), "particle");
		registerParticle(particleLoc, new BuiltInModel(ItemCameraTransforms.DEFAULT, ItemOverrideList.NONE) {
			@Override
			public TextureAtlasSprite getParticleTexture() {
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(particleTexture.toString());
			}
		});
		ModelLoader.setCustomStateMapper(block, blockIn -> Maps.toMap(blockIn.getBlockState().getValidStates(), (Function<IBlockState, ModelResourceLocation>) input -> particleLoc));
	}

	public static List<Pair<ModelResourceLocation, IBakedModel>> getParticleModels() {
		return particleModels;
	}

}
