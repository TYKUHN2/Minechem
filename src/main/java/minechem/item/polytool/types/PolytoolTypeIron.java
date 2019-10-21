package minechem.item.polytool.types;

import java.util.*;

import minechem.item.ItemPolytool;
import minechem.item.element.ElementEnum;
import minechem.item.polytool.PolytoolUpgradeType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class PolytoolTypeIron extends PolytoolUpgradeType {
	private static Map<String, Boolean> ores = new LinkedHashMap<>();

	public static void getOres() {
		for (final String ore : OreDictionary.getOreNames()) {
			if (ore.regionMatches(0, "ore", 0, 3)) {
				for (final ItemStack stack : OreDictionary.getOres(ore)) {
					ores.put(blockHash(stack), true);
				}
			}
		}
	}

	private static String blockHash(final ItemStack stack) {
		return blockHash(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
	}

	private static String blockHash(final Block block, final int meta) {
		return block.getUnlocalizedName() + "@" + meta;
	}

	@Override
	public void onBlockDestroyed(final ItemStack itemStack, final World world, final Block id, final int x1, final int y1, final int z1, final EntityLivingBase entityLiving) {
		if (!world.isRemote && entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entityLiving;
			final ArrayList<Vec3i> queue = new ArrayList<>(100);
			float carbon = 0;
			for (final Object upgrade : ItemPolytool.getUpgrades(itemStack)) {
				if (((PolytoolUpgradeType) upgrade).getElement() == ElementEnum.C) {
					carbon = ((PolytoolUpgradeType) upgrade).power;
				}
			}
			final IBlockState state = world.getBlockState(new BlockPos(x1, y1, z1));
			if (ores.containsKey(blockHash(id, state.getBlock().getMetaFromState(state)))) {
				int toMine = (int) power;
				queue.add(new Vec3i(x1, y1, z1));
				while (!queue.isEmpty()) {
					final Vec3i coord = queue.remove(0);
					final int x = coord.getX();
					final int y = coord.getY();
					final int z = coord.getZ();
					for (final EnumFacing dir : EnumFacing.values()) {
						if (world.getBlockState(new BlockPos(x + dir.getFrontOffsetX(), y + dir.getFrontOffsetY(), z + dir.getFrontOffsetZ())).getBlock() == id && world.getBlockState(new BlockPos(x + dir.getFrontOffsetX(), y + dir.getFrontOffsetY(), z + dir.getFrontOffsetZ())) == state) {

							breakExtraBlock(world, x + dir.getFrontOffsetX(), y + dir.getFrontOffsetY(), z + dir.getFrontOffsetZ(), player, id, state.getBlock().getMetaFromState(state), carbon);
							queue.add(new Vec3i(x + dir.getFrontOffsetX(), y + dir.getFrontOffsetY(), z + dir.getFrontOffsetZ()));
							toMine--;
							if (toMine <= 0) {
								return;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public ElementEnum getElement() {
		return ElementEnum.Fe;
	}

	@Override
	public String getDescription() {
		return "Mines deposits of ores";
	}

	@SuppressWarnings("deprecation")
	protected void breakExtraBlock(final World world, final int x, final int y, final int z, final EntityPlayer player, final Block block, final int meta, final float carbon) {
		if (player.capabilities.isCreativeMode) {
			block.onBlockHarvested(world, new BlockPos(x, y, z), block.getStateFromMeta(meta), player);
			world.setBlockToAir(new BlockPos(x, y, z));
			if (!world.isRemote) {
				((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, new BlockPos(x, y, z)));
			}
			return;
		}

		if (!world.isRemote) {
			final int bonus = block == Blocks.DIAMOND_ORE || block == Blocks.COAL_ORE ? (int) (world.rand.nextDouble() * Math.log(carbon)) + 1 : 1;
			block.onBlockHarvested(world, new BlockPos(x, y, z), block.getStateFromMeta(meta), player);

			if (block.removedByPlayer(block.getStateFromMeta(meta), world, new BlockPos(x, y, z), player, true)) {
				for (int i = 0; i < bonus; i++) {
					block.harvestBlock(world, player, new BlockPos(x, y, z), block.getStateFromMeta(meta), null, ItemStack.EMPTY);
				}
			}

			final EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
			mpPlayer.connection.sendPacket(new SPacketBlockChange(world, new BlockPos(x, y, z)));
		}
	}

}
