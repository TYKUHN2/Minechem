package minechem.init;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import minechem.api.IMinechemBlueprint;
import minechem.api.recipe.ISynthesisRecipe;
import minechem.block.fluid.BlockFluidMinechem;
import minechem.block.tile.TileBlueprintProjector;
import minechem.client.gui.GuiBlueprintProjector;
import minechem.client.model.generated.*;
import minechem.client.model.generated.ModelProperties.PerspectiveProperties;
import minechem.client.render.ElementItemRenderer;
import minechem.event.RadiationDecayEvent;
import minechem.fluid.FluidElement;
import minechem.fluid.FluidMolecule;
import minechem.fluid.reaction.ChemicalFluidReactionHandler;
import minechem.handler.HandlerElementDecay;
import minechem.init.ModGlobals.Textures;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PharmacologyEffectRegistry;
import minechem.potion.PotionEnchantmentCoated;
import minechem.utils.*;
import minechem.utils.BlueprintUtil.MultiblockBlockAccess;
import minechem.utils.BlueprintUtil.MultiblockRenderInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author p455w0rd
 *
 */
@EventBusSubscriber(modid = ModGlobals.MODID)
public class ModEvents {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTextureStitch(final TextureStitchEvent.Pre event) {
		final TextureMap map = event.getMap();
		map.registerSprite(Textures.Sprite.MICROSCOPE);
		map.registerSprite(Textures.Sprite.SYNTHESIZER);
		map.registerSprite(Textures.Sprite.DECOMPOSER);
		map.registerSprite(Textures.Sprite.BLUEPRINT_PROJECTOR);
		map.registerSprite(Textures.Sprite.LEADED_CHEST);
		map.registerSprite(Textures.Sprite.FILLED_TUBE);
		for (final ResourceLocation element : Textures.Sprite.LIQUID_STATES) {
			map.registerSprite(element);
		}
		for (final ResourceLocation element : Textures.Sprite.GAS_STATES) {
			map.registerSprite(element);
		}
		map.registerSprite(Textures.Sprite.SOLID_STATE);
		map.registerSprite(Textures.Sprite.MOLECULE_TUBE);
		map.registerSprite(Textures.Sprite.MOLECULE_PASS_1);
		map.registerSprite(Textures.Sprite.MOLECULE_PASS_2);
		map.registerSprite(Textures.Sprite.FLUID_STILL);
		map.registerSprite(Textures.Sprite.FLUID_FLOW);
		// @formatter:off
		final char[] charList = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		// @formatter:on
		for (final char chr : charList) {
			map.setTextureEntry(CharacterSprite.getSpriteForChar(chr));
		}
		for (final char chr : charList) {
			map.setTextureEntry(CharacterSprite.getSpriteForChar(Character.toUpperCase(chr)));
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelBake(final ModelBakeEvent event) {
		for (final Pair<ModelResourceLocation, IBakedModel> pair : ModRendering.getParticleModels()) {
			event.getModelRegistry().putObject(pair.getKey(), pair.getValue());
		}
		ElementItemRenderer.model = new ItemLayerWrapper(new PerspectiveAwareBakedModel(ImmutableList.of(), PerspectiveProperties.DEFAULT_ITEM));
		event.getModelRegistry().putObject(ModRendering.ITEM_ELEMENT_LOC, ElementItemRenderer.model);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelRegister(final ModelRegistryEvent event) {
		for (final ElementEnum element : ElementEnum.elements.values()) {
			ModRendering.setItemTEISR(ModItems.element, new ElementItemRenderer(), element.atomicNumber(), ModRendering.ITEM_ELEMENT_LOC);
		}
	}

	@SubscribeEvent
	public static void tick(final TickEvent.PlayerTickEvent event) {
		if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
			final EntityPlayer player = event.player;
			HandlerElementDecay.getInstance().update(player);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onGuiForegroundDraw(final GuiContainerEvent.DrawForeground event) {

	}

	@SubscribeEvent
	public static void checkForPoison(final LivingEntityUseItemEvent.Finish event) {
		if (event.getItem() != null && event.getItem().getTagCompound() != null && ModConfig.FoodSpiking) {
			final NBTTagCompound stackTag = event.getItem().getTagCompound();
			final boolean isPoisoned = stackTag.getBoolean("minechem.isPoisoned");
			final int[] effectTypes = stackTag.getIntArray("minechem.effectTypes");
			if (isPoisoned) {
				for (final int effectType : effectTypes) {
					final MoleculeEnum molecule = MoleculeEnum.getById(effectType);
					PharmacologyEffectRegistry.applyEffect(molecule, event.getEntityLiving());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLootTableLoadEvent(final LootTableLoadEvent event) {
		//@formatter:off
		if (event.getName() != null && event.getTable() != null && (
				event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) ||
				event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) ||
				event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE) ||
				event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE) ||
				event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE) ||
				event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST) ||
				event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)
				)) {
			ModLogger.debug("Adding blueprints to dungeon loot...");
			final LootTable table = event.getTable();
			final LootCondition[] emptyCondition = new LootCondition[0];
			final LootPool pool = table.getPool("main");
			if (pool != null) {
				final LootFunction fissionNBTFunc = new SetNBT(emptyCondition,BlueprintUtil.createStack(ModBlueprints.fission).getTagCompound());
				final LootFunction fusionNBTFunc = new SetNBT(emptyCondition,BlueprintUtil.createStack(ModBlueprints.fusion).getTagCompound());
				pool.addEntry(new LootEntryItem(ModItems.blueprint, 5, 0, new LootFunction[]{fissionNBTFunc}, emptyCondition, ModItems.blueprint.getRegistryName().toString()+"_fission"));
				pool.addEntry(new LootEntryItem(ModItems.blueprint, 5, 0, new LootFunction[]{fusionNBTFunc}, emptyCondition, ModItems.blueprint.getRegistryName().toString()+"_fusion"));
			}
		}
		//@formatter:on
	}

	@SubscribeEvent
	public static void tick(final TickEvent.WorldTickEvent event) {
		if (ModConfig.reactionItemMeetFluid) {
			final World world = event.world;
			final List<Object> entities = new ArrayList<>(world.loadedEntityList);
			for (final Object entity : entities) {
				if (entity instanceof EntityItem) {
					ChemicalFluidReactionHandler.getInstance().checkEntityItem(world, (EntityItem) entity);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onSoundRegister(final RegistryEvent.Register<SoundEvent> event) {
		ModSounds.registerSounds(event.getRegistry());
	}

	@SubscribeEvent
	public static void onIRecipeRegister(final RegistryEvent.Register<IRecipe> event) {
		ModRecipes.registerCustomRecipes(event.getRegistry());
		ModRecipes.registerRecipes();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onISynthesisRecipeRegister(final RegistryEvent.Register<ISynthesisRecipe> event) {
		ModRecipes.registerSynthesisRecipes();
	}

	@SubscribeEvent
	public static void onEnchantmentRegister(final RegistryEvent.Register<Enchantment> event) {
		ModEnchantments.register(event.getRegistry());
	}

	@SubscribeEvent
	public static void onPotionRegister(final RegistryEvent.Register<Potion> event) {
		ModPotions.registerPotions(event.getRegistry());
	}

	@SubscribeEvent
	public static void onBlueprintRegister(final RegistryEvent.Register<IMinechemBlueprint> event) {
		ModBlueprints.registerBlueprints();
	}

	@SubscribeEvent
	public static void onRegistryRegister(final RegistryEvent.NewRegistry event) {
		new ModRegistries();
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void afterWorldRender(final RenderWorldLastEvent event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientTick(final TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiBlueprintProjector) {
			if (((GuiBlueprintProjector) Minecraft.getMinecraft().currentScreen).getProjector() != null) {
				final TileBlueprintProjector projector = ((GuiBlueprintProjector) Minecraft.getMinecraft().currentScreen).getProjector();
				if (projector.hasBlueprint()) {
					if (BlueprintUtil.blockAccess == null || !BlueprintUtil.blockAccess.data.multiblock.getDescriptiveName().equals(projector.getBlueprint().getDescriptiveName())) {
						BlueprintUtil.blockAccess = new MultiblockBlockAccess(new MultiblockRenderInfo(projector.getBlueprint()));
					}
					final int totalLayers = BlueprintUtil.blockAccess.data.structureHeight;
					if (BlueprintUtil.structureRenderTicks >= 20) {
						BlueprintUtil.structureRenderTicks = 0;
						++BlueprintUtil.currentLayer;
						if (BlueprintUtil.currentLayer > totalLayers) {
							BlueprintUtil.currentLayer = 0;
						}
						BlueprintUtil.blockAccess.data.setShowLayer(BlueprintUtil.currentLayer);
					}
					else {
						BlueprintUtil.structureRenderTicks++;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogOverlay(final EntityViewRenderEvent.RenderFogEvent event) {
		//if (event.getState().getMaterial() == ModMaterial.FLUID) {
		final EntityPlayer player = Minecraft.getMinecraft().player;
		final World world = Minecraft.getMinecraft().world;
		if (player.isInsideOfMaterial(Material.WATER) && !player.isCreative() && !player.isSpectator()) {
			final double d0 = player.posY + player.getEyeHeight();
			final BlockPos blockpos = new BlockPos(player.posX, d0, player.posZ);
			if (!(world.getBlockState(blockpos).getBlock() instanceof BlockFluidMinechem)) {
				return;
			}
			GlStateManager.setFog(GlStateManager.FogMode.EXP);
			GlStateManager.setFogStart(event.getFarPlaneDistance() * 0.05F);
			GlStateManager.setFogEnd(Math.min(event.getFarPlaneDistance(), 192.0F) * 0.5F);
			event.setResult(Result.DENY);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogColor(final EntityViewRenderEvent.FogColors event) {
		final EntityPlayer player = Minecraft.getMinecraft().player;
		final World world = Minecraft.getMinecraft().world;
		if (player.isInsideOfMaterial(Material.WATER) && !player.isCreative() && !player.isSpectator()) {
			final double d0 = player.posY + player.getEyeHeight();
			final BlockPos blockpos = new BlockPos(player.posX, d0, player.posZ);
			if (!(world.getBlockState(blockpos).getBlock() instanceof BlockFluidMinechem)) {
				return;
			}
			Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
			final BlockFluidMinechem fluidBlock = (BlockFluidMinechem) world.getBlockState(blockpos).getBlock();
			if (fluidBlock.getFluid() instanceof FluidElement) {
				color = new Color(((FluidElement) fluidBlock.getFluid()).getColor());
			}
			else if (fluidBlock.getFluid() instanceof FluidMolecule) {
				color = new Color(((FluidMolecule) fluidBlock.getFluid()).getColor());
			}
			final float red = color.getRed() / 255f;
			final float green = color.getGreen() / 255f;
			final float blue = color.getBlue() / 255f;
			event.setRed(red);
			event.setGreen(green);
			event.setBlue(blue);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderOverlayEvent(final RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.AIR) {
			final EntityPlayer player = Minecraft.getMinecraft().player;
			final World world = Minecraft.getMinecraft().world;
			if (player.isInsideOfMaterial(Material.WATER) && !player.isCreative() && !player.isSpectator()) {
				final double d0 = player.posY + player.getEyeHeight();
				final BlockPos blockpos = new BlockPos(player.posX, d0, player.posZ);
				if (!(world.getBlockState(blockpos).getBlock() instanceof BlockFluidMinechem)) {
					return;
				}
				Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
				final BlockFluidMinechem fluidBlock = (BlockFluidMinechem) world.getBlockState(blockpos).getBlock();
				if (fluidBlock.getFluid() instanceof FluidElement) {
					color = new Color(((FluidElement) fluidBlock.getFluid()).getColor());
				}
				else if (fluidBlock.getFluid() instanceof FluidMolecule) {
					color = new Color(((FluidMolecule) fluidBlock.getFluid()).getColor());
				}
				final Gui gui = Minecraft.getMinecraft().ingameGUI;
				if (gui == null) {
					return;
				}
				final ScaledResolution scaledRes = event.getResolution();
				final int i1 = scaledRes.getScaledWidth() / 2 + 91;
				final int j1 = scaledRes.getScaledHeight() - 39;
				final int k2 = j1 - 10;
				final int i6 = Minecraft.getMinecraft().player.getAir();
				final int k6 = MathHelper.ceil((i6 - 2) * 10.0D / 300.0D);
				final int i7 = MathHelper.ceil(i6 * 10.0D / 300.0D) - k6;

				for (int k7 = 0; k7 < k6 + i7; ++k7) {

					final float red = color.getRed() / 255f;
					final float green = color.getGreen() / 255f;
					final float blue = color.getBlue() / 255f;
					GlStateManager.color(red, green, blue, 1.0F);
					Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/icons.png"));
					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();
					GlStateManager.enableDepth();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					if (k7 < k6) {
						gui.drawTexturedModalRect(i1 - k7 * 8 - 9, k2, 16, 18, 9, 9);
					}
					else {
						gui.drawTexturedModalRect(i1 - k7 * 8 - 9, k2, 25, 18, 9, 9);
					}
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					if (k7 < k6) {
						gui.drawTexturedModalRect(i1 - k7 * 8 - 9, k2, 16, 18 + 9, 9, 9);
					}
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onDecayEvent(final RadiationDecayEvent event) {
		if (event.getPlayer() != null) {
			final String nameBeforeDecay = event.getLongName(event.getBefore());
			final String nameAfterDecay = event.getLongName(event.getAfter());
			final String time = TickTimeUtil.getTimeFromTicks(event.getTime());
			final String message = String.format("Radiation Warning: Element %s decayed into %s after %s.", nameBeforeDecay, nameAfterDecay, time);
			event.getPlayer().sendMessage(new TextComponentString(message));
		}
	}

	@SubscribeEvent
	public static void getFuelBurnTime(final FurnaceFuelBurnTimeEvent event) {
		final ItemStack stack = event.getItemStack();
		int burnTime = -1;
		if (!stack.isEmpty()) {
			if (stack.getItem() == ModItems.element && MinechemUtil.getElement(stack) != null) {
				burnTime = MinechemUtil.getElement(stack).getFuelBurnTime();
			}
			else if (stack.getItem() == ModItems.molecule) {
				burnTime = MinechemUtil.getMolecule(stack).getFuelBurnTime();
			}
		}
		if (burnTime > 0) {
			event.setBurnTime(burnTime);
		}
	}

	@SubscribeEvent
	public static void onConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(ModGlobals.MODID)) {
			ModConfig.loadConfig();
		}
	}

	@SubscribeEvent
	public static void onAttack(final LivingAttackEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			final EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
			ItemStack weapon = ItemStack.EMPTY;
			final Iterable<ItemStack> weapons = entity.getHeldEquipment();
			for (final ItemStack stack : weapons) {
				if (stack.getItem() instanceof ItemSword) {
					weapon = stack.copy();
				}
			}
			if (weapon.isEmpty()) {
				return;
			}
			final NBTTagList list = weapon.getEnchantmentTagList();
			if (list == null) {
				return;
			}
			for (int i = 0; i < list.tagCount(); i++) {
				final NBTTagCompound enchantmentTag = list.getCompoundTagAt(i);
				final Enchantment enchant = Enchantment.getEnchantmentByID(enchantmentTag.getShort("id"));
				if (enchant instanceof PotionEnchantmentCoated) {
					((PotionEnchantmentCoated) enchant).applyEffect(event.getEntityLiving());
				}
			}
		}
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public static void onBlockBreak(final BlockEvent.BreakEvent event) {
		if (event.getState().getBlock() == ModBlocks.blueprintProjector) {
			event.setCanceled(true);
			final EntityPlayer player = event.getPlayer();
			final World world = event.getWorld();
			final BlockPos pos = event.getPos();
			final IBlockState iblockstate = world.getBlockState(pos);
			//Block block = iblockstate.getBlock();
			final TileEntity tileentity = world.getTileEntity(pos);
			ItemStack droppedStack = ItemStack.EMPTY;
			if (tileentity != null && tileentity instanceof TileBlueprintProjector) {
				final TileBlueprintProjector projector = (TileBlueprintProjector) tileentity;
				final IMinechemBlueprint blueprint = projector.getBlueprint();
				if (blueprint != null) {
					droppedStack = BlueprintUtil.createStack(blueprint);
					projector.setBlueprint(null);
					projector.destroyProjection();
				}
				//projector.markDirty();
				//world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
			}

			final ItemStack stack = player.getHeldItemMainhand();
			if (!stack.isEmpty() && stack.getItem().onBlockStartBreak(stack, pos, player)) {
				return;
			}

			//Item item = block.getItemDropped(iblockstate, world.rand, 0);

			//if (item == Items.AIR) {
			//	return;
			//}

			//itemstack.setStackDisplayName(((IWorldNameable)te).getName());
			if (!droppedStack.isEmpty()) {
				Block.spawnAsEntity(world, pos, droppedStack);
			}

			world.playEvent(player, 2001, pos, Block.getStateId(iblockstate));
			boolean flag1 = false;

			if (player.isCreative()) {
				flag1 = iblockstate.getBlock().removedByPlayer(iblockstate, world, pos, player, false);
				if (flag1) {
					iblockstate.getBlock().onBlockDestroyedByPlayer(world, pos, iblockstate);
				}
				((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));
			}
			else {
				final ItemStack itemstack1 = player.getHeldItemMainhand();
				final ItemStack itemstack2 = itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy();
				final boolean flag = iblockstate.getBlock().canHarvestBlock(world, pos, player);

				if (!itemstack1.isEmpty()) {
					itemstack1.onBlockDestroyed(world, iblockstate, pos, player);
					if (itemstack1.isEmpty()) {
						net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack2, EnumHand.MAIN_HAND);
					}
				}

				flag1 = iblockstate.getBlock().removedByPlayer(iblockstate, world, pos, player, false);//this.removeBlock(pos, flag);
				if (flag1) {
					iblockstate.getBlock().onBlockDestroyedByPlayer(world, pos, iblockstate);
				}
				if (flag1 && flag) {
					iblockstate.getBlock().harvestBlock(world, player, pos, iblockstate, tileentity, itemstack2);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTooltip(final ItemTooltipEvent event) {

	}

}
