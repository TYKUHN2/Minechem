package minechem.item.polytool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import minechem.item.ItemPolytool;
import minechem.item.element.ElementEnum;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.utils.MinechemUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PolytoolEventHandler {

	private static final Random random = new Random();

	public void addDrops(LivingDropsEvent event, ItemStack dropStack) {
		EntityItem entityitem = new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, dropStack);
		entityitem.setPickupDelay(10);
		event.getDrops().add(entityitem);
	}

	@SubscribeEvent
	public void onDrop(LivingDropsEvent event) {

		// Large page of the beheading code based off TiC code
		// Thanks to mDiyo
		if ("player".equals(event.getSource().damageType)) {

			EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
			ItemStack stack = player.getActiveItemStack();
			if (!stack.isEmpty() && stack.getItem() instanceof ItemPolytool) {
				float powerSilicon = ItemPolytool.getPowerOfType(stack, ElementEnum.Si);
				if (powerSilicon > 0) {
					int amount = (int) Math.ceil(random.nextDouble() * powerSilicon);
					Iterator<EntityItem> iter = event.getDrops().iterator();
					if (random.nextInt(16) < 1 + powerSilicon) {
						ArrayList<EntityItem> trueResult = new ArrayList<EntityItem>();
						while (iter.hasNext()) {
							EntityItem entityItem = iter.next();
							ItemStack item = entityItem.getItem();
							while (item.getCount() > 0) {
								// Always avoid chances
								RecipeDecomposer recipe = RecipeHandlerDecomposer.instance.getRecipe(item);

								if (recipe != null) {
									NonNullList<ItemStack> items = MinechemUtil.convertChemicalsIntoItemStacks(recipe.getOutput());
									for (ItemStack itemStack : items) {
										trueResult.add(new EntityItem(entityItem.world, entityItem.posX, entityItem.posY, entityItem.posZ, itemStack));
									}
								}
								else {
									trueResult.add(entityItem);
									break;
								}
								item.shrink(1);
							}

						}
						event.getDrops().clear();
						event.getDrops().addAll(trueResult);
					}
				}
			}
			EntityLivingBase enemy = event.getEntityLiving();
			if (enemy instanceof EntitySkeleton || enemy instanceof EntityZombie || enemy instanceof EntityPlayer || enemy instanceof EntityWitherSkeleton || enemy instanceof EntityCreeper || enemy instanceof EntityDragon) {

				if (!stack.isEmpty() && stack.getItem() instanceof ItemPolytool) {
					// Nitrogen preservation
					if (enemy instanceof EntityZombie) {

						float power = ItemPolytool.getPowerOfType(stack, ElementEnum.N);
						if (power > 0) {
							int amount = (int) Math.ceil(random.nextDouble() * power);
							addDrops(event, new ItemStack(Items.COOKED_BEEF, amount, 0));
							Iterator<EntityItem> iter = event.getDrops().iterator();
							while (iter.hasNext()) {
								EntityItem entityItem = iter.next();
								if (entityItem.getItem().getItem() == Items.ROTTEN_FLESH) {
									iter.remove();
								}
							}
						}
					}
					// Calcium bonus
					if (enemy instanceof EntitySkeleton) {
						float power = ItemPolytool.getPowerOfType(stack, ElementEnum.Ca);
						if (power > 0) {
							int amount = (int) Math.ceil(random.nextDouble() * power);
							Iterator<EntityItem> iter = event.getDrops().iterator();
							while (iter.hasNext()) {
								EntityItem entityItem = iter.next();
								if (entityItem.getItem().getItem() == Items.BONE) {
									entityItem.getItem().grow(amount);
								}
							}
						}
					}
					// Beryllium beheading
					float beheading = ItemPolytool.getPowerOfType(stack, ElementEnum.Be);
					while (beheading > 5) {
						if (beheading > 0 && random.nextInt(5) < beheading * 10) {
							if (event.getEntityLiving() instanceof EntitySkeleton) {
								addDrops(event, new ItemStack(Items.SKULL, 1, 0));
							}
							else if (event.getEntityLiving() instanceof EntityWitherSkeleton) {
								addDrops(event, new ItemStack(Items.SKULL, 1, 1));
							}
							else if (event.getEntityLiving() instanceof EntityZombie) {
								addDrops(event, new ItemStack(Items.SKULL, 1, 2));
							}
							else if (event.getEntityLiving() instanceof EntityPlayer) {
								ItemStack dropStack = new ItemStack(Items.SKULL, 1, 3);
								NBTTagCompound nametag = new NBTTagCompound();
								nametag.setString("SkullOwner", player.getDisplayName().getFormattedText());
								addDrops(event, dropStack);
							}
							else if (event.getEntityLiving() instanceof EntityCreeper) {
								addDrops(event, new ItemStack(Items.SKULL, 1, 4));
							}
							else if (event.getEntityLiving() instanceof EntityDragon) {
								addDrops(event, new ItemStack(Items.SKULL, 1, 5));
							}
						}

						// More head drops if level>5
						beheading--;
					}
				}
			}
		}
	}
}
