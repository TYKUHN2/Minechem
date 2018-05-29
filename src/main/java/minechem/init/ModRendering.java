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
import net.minecraft.client.renderer.block.model.BuiltInModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
	private static List<Pair<ModelResourceLocation, IBakedModel>> particleModels = new LinkedList<Pair<ModelResourceLocation, IBakedModel>>();

	public static void setItemTEISR(Item item, TileEntityItemStackRenderer renderer) {
		//ModelLoader.setCustomModelResourceLocation(item, 0, TEISR_LOC);
		//item.setTileEntityItemStackRenderer(renderer);
		setItemTEISR(item, renderer, 0);
	}

	public static void setItemTEISR(Item item, TileEntityItemStackRenderer renderer, int meta) {
		//ModelLoader.setCustomModelResourceLocation(item, meta, TEISR_LOC);
		//item.setTileEntityItemStackRenderer(renderer);
		setItemTEISR(item, renderer, meta, TEISR_LOC);
	}

	public static void setItemTEISR(Item item, TileEntityItemStackRenderer renderer, int meta, ModelResourceLocation location) {
		ModelLoader.setCustomModelResourceLocation(item, meta, location);
		item.setTileEntityItemStackRenderer(renderer);
	}

	public static void setBlockTEISR(Block block, TileEntityItemStackRenderer renderer) {
		setItemTEISR(Item.getItemFromBlock(block), renderer);
	}

	public static void registerParticle(ModelResourceLocation location, IBakedModel model) {
		particleModels.add(new ImmutablePair<ModelResourceLocation, IBakedModel>(location, model));
	}

	public static <T extends TileEntity> void setBlockRendering(Block block, TileEntitySpecialRenderer<? super T> blockRenderer, Class<T> tile, TileEntityItemStackRenderer itemRenderer, ResourceLocation particleTexture) {
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
