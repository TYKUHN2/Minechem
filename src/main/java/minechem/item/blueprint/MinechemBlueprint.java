package minechem.item.blueprint;

import minechem.api.IMinechemBlueprint;
import minechem.utils.MinechemUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class MinechemBlueprint extends IForgeRegistryEntry.Impl<IMinechemBlueprint> implements IMinechemBlueprint {

	public static final String BLUEPRINT_NBT_KEY = "Blueprint";

	public static final IBlockState air = Blocks.AIR.getDefaultState();

	private int xSize;
	private int ySize;
	private int zSize;
	public String name;
	private final String langKey;

	public MinechemBlueprint(String langKey, int xSize, int ySize, int zSize) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		this.langKey = langKey;
	}

	@Override
	public String getDescriptiveName() {
		return MinechemUtil.getLocalString(langKey);
	}

	@Override
	public IBlockState[][] getHorizontalSlice(int y) {
		IBlockState[][][] structure = getStructure();
		IBlockState[][] slice = new IBlockState[xSize][zSize];
		for (int x = 0; x < xSize; x++) {
			for (int z = 0; z < zSize; z++) {
				slice[x][z] = structure[y][x][z];
			}
		}
		return slice;
	}

	@Override
	public IBlockState[][] getVerticalSlice(int x) {
		IBlockState[][][] structure = getStructure();
		IBlockState[][] slice = new IBlockState[ySize][zSize];
		for (int y = 0; y < ySize; y++) {
			for (int z = 0; z < zSize; z++) {
				slice[y][z] = structure[y][x][z];
			}
		}
		return slice;
	}

	@Override
	public int xSize() {
		return xSize;
	}

	@Override
	public int ySize() {
		return ySize;
	}

	@Override
	public int zSize() {
		return zSize;
	}

	@Override
	public int getTotalSize() {
		return (xSize * zSize) * ySize;
	}

	@Override
	public abstract IBlockState[][][] getStructure();

	@Override
	public abstract int getManagerPosX();

	@Override
	public abstract int getManagerPosY();

	@Override
	public abstract int getManagerPosZ();

	public static class MinechemBlueprintCallbacks implements IForgeRegistry.MissingFactory<IMinechemBlueprint> {

		public static final MinechemBlueprintCallbacks INSTANCE = new MinechemBlueprintCallbacks();

		@Override
		public IMinechemBlueprint createMissing(ResourceLocation key, boolean isNetwork) {
			return isNetwork ? new DummyBlueprint().setRegistryName(key) : null;
		}

		private static class DummyBlueprint implements IMinechemBlueprint {

			private ResourceLocation name;

			@Override
			public IMinechemBlueprint setRegistryName(ResourceLocation name) {
				this.name = name;
				return this;
			}

			@Override
			public ResourceLocation getRegistryName() {
				return name;
			}

			@Override
			public Class<IMinechemBlueprint> getRegistryType() {
				return IMinechemBlueprint.class;
			}

			@Override
			public IBlockState[][] getHorizontalSlice(int y) {
				return new IBlockState[0][0];
			}

			@Override
			public IBlockState[][] getVerticalSlice(int x) {
				return new IBlockState[0][0];
			}

			@Override
			public int getTotalSize() {
				return 0;
			}

			@Override
			public IBlockState[][][] getStructure() {
				return new IBlockState[0][0][0];
			}

			@Override
			public int getManagerPosX() {
				return 0;
			}

			@Override
			public int getManagerPosY() {
				return 0;
			}

			@Override
			public int getManagerPosZ() {
				return 0;
			}

			@Override
			public int xSize() {
				return 0;
			}

			@Override
			public int ySize() {
				return 0;
			}

			@Override
			public int zSize() {
				return 0;
			}

			@Override
			public String getDescriptiveName() {
				return "blueprint.dummy.desc";
			}

			@Override
			public int getRenderScale() {
				return 0;
			}
		}
	}

}
