package minechem.block;

import java.util.ArrayList;

import minechem.Minechem;
import minechem.block.tile.TileDecomposer;
import minechem.client.render.RenderDecomposer;
import minechem.client.render.RenderDecomposer.ItemRenderDecomposer;
import minechem.init.ModCreativeTab;
import minechem.init.ModGlobals;
import minechem.init.ModGlobals.Textures;
import minechem.init.ModRendering;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDecomposer extends BlockSimpleContainer {

	public BlockDecomposer() {
		super(Material.IRON);
		setRegistryName(ModGlobals.ID + ":chemical_decomposer");
		setUnlocalizedName("chemical_decomposer");
		setCreativeTab(ModCreativeTab.CREATIVE_TAB_ITEMS);
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderer() {
		//ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderDecomposer());
		//ModelRegistryHelper.setParticleTexture(this, Textures.Sprite.MICROSCOPE);
		//ClientRegistry.bindTileEntitySpecialRenderer(TileDecomposer.class, new RenderDecomposer());
		//ModelLoader.setCustomStateMapper(this, blockIn -> Collections.emptyMap());
		ModRendering.setBlockRendering(this, new RenderDecomposer(), TileDecomposer.class, new ItemRenderDecomposer(), Textures.Sprite.DECOMPOSER);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
		if (!world.isRemote) {
			/*
			DecomposerUpdateMessage message = new DecomposerUpdateMessage((TileDecomposer) tileEntity);
			if (player instanceof EntityPlayerMP) {
				MessageHandler.INSTANCE.sendTo(message, (EntityPlayerMP) player);
			}
			else {
				MessageHandler.INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), ModConfig.UpdateRadius));
			}
			}
			*/
			player.openGui(Minechem.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileDecomposer();
	}

	@Override
	public void addStacksDroppedOnBlockBreak(TileEntity tileEntity, ArrayList<ItemStack> itemStacks) {
		if (tileEntity instanceof TileDecomposer) {
			TileDecomposer decomposer = (TileDecomposer) tileEntity;
			for (int slot = 0; slot < decomposer.getSizeInventory(); slot++) {
				ItemStack itemstack = decomposer.getStackInSlot(slot);
				if (!itemstack.isEmpty()) {
					itemStacks.add(itemstack);
				}
			}
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_) {
		return false;
	}

}
