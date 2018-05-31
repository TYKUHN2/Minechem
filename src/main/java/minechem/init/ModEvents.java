package minechem.init;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import minechem.api.IOreDictionaryHandler;
import minechem.client.model.generated.CharacterSprite;
import minechem.client.model.generated.ItemLayerWrapper;
import minechem.client.model.generated.ModelProperties.PerspectiveProperties;
import minechem.client.model.generated.PerspectiveAwareBakedModel;
import minechem.client.render.EffectsRenderer;
import minechem.client.render.ElementItemRenderer;
import minechem.event.RadiationDecayEvent;
import minechem.fluid.FluidElement;
import minechem.fluid.FluidMolecule;
import minechem.fluid.MinechemFluidBlock;
import minechem.fluid.reaction.ChemicalFluidReactionHandler;
import minechem.handler.RadiationHandler;
import minechem.init.ModGlobals.Textures;
import minechem.item.ItemElement;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PharmacologyEffectRegistry;
import minechem.potion.PotionEnchantmentCoated;
import minechem.utils.MinechemUtil;
import minechem.utils.TimeHelper;
import net.minecraft.block.material.Material;
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
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author p455w0rd
 *
 */
public class ModEvents {

	private static boolean tablesLoaded = false;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ModEvents());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		TextureMap map = event.getMap();
		map.registerSprite(Textures.Sprite.MICROSCOPE);
		map.registerSprite(Textures.Sprite.SYNTHESIZER);
		map.registerSprite(Textures.Sprite.DECOMPOSER);
		map.registerSprite(Textures.Sprite.BLUEPRINT_PROJECTOR);
		map.registerSprite(Textures.Sprite.LEADED_CHEST);
		map.registerSprite(Textures.Sprite.FILLED_TUBE);
		for (ResourceLocation element : Textures.Sprite.LIQUID_STATES) {
			map.registerSprite(element);
		}
		for (ResourceLocation element : Textures.Sprite.GAS_STATES) {
			map.registerSprite(element);
		}
		map.registerSprite(Textures.Sprite.SOLID_STATE);
		map.registerSprite(Textures.Sprite.MOLECULE_TUBE);
		map.registerSprite(Textures.Sprite.MOLECULE_PASS_1);
		map.registerSprite(Textures.Sprite.MOLECULE_PASS_2);
		map.registerSprite(Textures.Sprite.FLUID_STILL);
		map.registerSprite(Textures.Sprite.FLUID_FLOW);
		// @formatter:off
		char[] charList = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		// @formatter:on
		for (char chr : charList) {
			map.setTextureEntry(CharacterSprite.getSpriteForChar(chr));
			//map.setTextureEntry(CharacterSprite.getSpriteForChar(Character.toUpperCase(chr)));
		}
		for (char chr : charList) {
			//map.setTextureEntry(CharacterSprite.getSpriteForChar(chr));
			map.setTextureEntry(CharacterSprite.getSpriteForChar(Character.toUpperCase(chr)));
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onModelBake(ModelBakeEvent event) {
		for (Pair<ModelResourceLocation, IBakedModel> pair : ModRendering.getParticleModels()) {
			event.getModelRegistry().putObject(pair.getKey(), pair.getValue());
		}
		ElementItemRenderer.model = new ItemLayerWrapper(new PerspectiveAwareBakedModel(ImmutableList.of(), PerspectiveProperties.DEFAULT_ITEM));
		event.getModelRegistry().putObject(ModRendering.ITEM_ELEMENT_LOC, ElementItemRenderer.model);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onModelRegister(ModelRegistryEvent event) {
		for (ElementEnum element : ElementEnum.elements.values()) {
			ModRendering.setItemTEISR(ModItems.element, new ElementItemRenderer(), element.atomicNumber(), ModRendering.ITEM_ELEMENT_LOC);
		}
	}

	@SubscribeEvent
	public void tick(TickEvent.PlayerTickEvent event) {
		if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
			EntityPlayer player = event.player;
			RadiationHandler.getInstance().update(player);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiForegroundDraw(GuiContainerEvent.DrawForeground event) {
		List<Slot> slots = event.getGuiContainer().inventorySlots.inventorySlots;
		for (Slot slot : slots) {
			if (slot.getHasStack() && slot.getStack().getItem() instanceof ItemElement && slot.getStack().getItemDamage() > 0) {
				/*
				int x = slot.xPos;
				int y = slot.yPos;
				float scale = 0.5F;
				ItemStack stack = slot.getStack();
				ElementEnum chemicalBase = MinechemUtil.getElement(stack);
				FontRenderer font = Minecraft.getMinecraft().fontRenderer;
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				RenderHelper.disableStandardItemLighting();
				RenderUtil.resetOpenGLColour();
				GlStateManager.pushMatrix();
				//GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
				//GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				//GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(scale, scale, scale);
				//GlStateManager.translate(0.0F, 0.0F, -16.0F);
				//boolean oldBidiFlag = font.getBidiFlag();
				//font.setBidiFlag(true);
				font.drawString(chemicalBase.name(), x, y - 28, 0x000000);
				//Tessellator tess = Tessellator.getInstance();
				//BufferBuilder buffer = tess.getBuffer();
				//buffer.begin(0x07, DefaultVertexFormats.ITEM);
				//SimpleModelFontRenderer fr = new SimpleModelFontRenderer(Minecraft.getMinecraft().gameSettings, font, Minecraft.getMinecraft().getTextureManager(), false, m, format);
				//tess.draw();
				GlStateManager.translate(0.0F, 0.0F, 15.999F);
				//GlStateManager.translate(1.0F, 1.0F, -15.999F);
				font.drawString(chemicalBase.name(), x, y - 28, 0xEEEEEE);
				//font.setBidiFlag(oldBidiFlag);
				//GlStateManager.translate(-1.0F, -1.0F, 15.999F);
				GlStateManager.scale(scale + scale, scale + scale, scale + scale);
				//RenderHelper.enableStandardItemLighting();
				GlStateManager.popMatrix();
				*/
			}
		}
	}

	@SubscribeEvent
	public void checkForPoison(LivingEntityUseItemEvent.Finish event) {
		if (event.getItem() != null && event.getItem().getTagCompound() != null && ModConfig.FoodSpiking) {
			NBTTagCompound stackTag = event.getItem().getTagCompound();
			boolean isPoisoned = stackTag.getBoolean("minechem.isPoisoned");
			int[] effectTypes = stackTag.getIntArray("minechem.effectTypes");
			if (isPoisoned) {
				for (int effectType : effectTypes) {
					MoleculeEnum molecule = MoleculeEnum.getById(effectType);
					PharmacologyEffectRegistry.applyEffect(molecule, event.getEntityLiving());
				}
			}
		}
	}

	@SubscribeEvent
	public void onLootTableLoadEvent(LootTableLoadEvent event) {
		if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) && !tablesLoaded) {
			ModLogger.debug("Adding blueprints to dungeon loot...");
			LootTable table = event.getTable();
			table.getPool("main").addEntry(new LootEntryItem(ModItems.blueprint, 1, 0, new LootFunction[0], new LootCondition[0], "blueprint1"));
			table.getPool("main").addEntry(new LootEntryItem(ModItems.blueprint, 1, 1, new LootFunction[0], new LootCondition[0], "blueprint2"));
			tablesLoaded = true;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPreRender(RenderGameOverlayEvent.Pre e) {
		EffectsRenderer.renderEffects();
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		if (ModConfig.reactionItemMeetFluid) {
			World world = event.world;
			List<Object> entities = new ArrayList<Object>(world.loadedEntityList);
			for (Object entity : entities) {
				if (entity instanceof EntityItem) {
					ChemicalFluidReactionHandler.getInstance().checkEntityItem(world, (EntityItem) entity);
				}
			}
		}
	}

	@SubscribeEvent
	public void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
		ModSounds.registerSounds(event.getRegistry());
	}

	@SubscribeEvent
	public void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
		ModRecipes.registerCustomRecipes(event.getRegistry());
	}

	@SubscribeEvent
	public void onEnchantmentRegister(RegistryEvent.Register<Enchantment> event) {
		ModEnchantments.register(event.getRegistry());
	}

	@SubscribeEvent
	public void onPotionRegister(RegistryEvent.Register<Potion> event) {
		ModPotions.registerPotions(event.getRegistry());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogOverlay(EntityViewRenderEvent.RenderFogEvent event) {
		//if (event.getState().getMaterial() == ModMaterial.FLUID) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;
		if (player.isInsideOfMaterial(Material.WATER) && !player.isCreative() && !player.isSpectator()) {
			double d0 = player.posY + player.getEyeHeight();
			BlockPos blockpos = new BlockPos(player.posX, d0, player.posZ);
			if (!(world.getBlockState(blockpos).getBlock() instanceof MinechemFluidBlock)) {
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
	public void onFogColor(EntityViewRenderEvent.FogColors event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;
		if (player.isInsideOfMaterial(Material.WATER) && !player.isCreative() && !player.isSpectator()) {
			double d0 = player.posY + player.getEyeHeight();
			BlockPos blockpos = new BlockPos(player.posX, d0, player.posZ);
			if (!(world.getBlockState(blockpos).getBlock() instanceof MinechemFluidBlock)) {
				return;
			}
			Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
			MinechemFluidBlock fluidBlock = (MinechemFluidBlock) world.getBlockState(blockpos).getBlock();
			if (fluidBlock.getFluid() instanceof FluidElement) {
				color = new Color(((FluidElement) fluidBlock.getFluid()).getColor());
			}
			else if (fluidBlock.getFluid() instanceof FluidMolecule) {
				color = new Color(((FluidMolecule) fluidBlock.getFluid()).getColor());
			}
			float red = color.getRed() / 255f;
			float green = color.getGreen() / 255f;
			float blue = color.getBlue() / 255f;
			event.setRed(red);
			event.setGreen(green);
			event.setBlue(blue);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderOverlayEvent(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.AIR) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			World world = Minecraft.getMinecraft().world;
			if (player.isInsideOfMaterial(Material.WATER) && !player.isCreative() && !player.isSpectator()) {
				double d0 = player.posY + player.getEyeHeight();
				BlockPos blockpos = new BlockPos(player.posX, d0, player.posZ);
				if (!(world.getBlockState(blockpos).getBlock() instanceof MinechemFluidBlock)) {
					return;
				}
				Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
				MinechemFluidBlock fluidBlock = (MinechemFluidBlock) world.getBlockState(blockpos).getBlock();
				if (fluidBlock.getFluid() instanceof FluidElement) {
					color = new Color(((FluidElement) fluidBlock.getFluid()).getColor());
				}
				else if (fluidBlock.getFluid() instanceof FluidMolecule) {
					color = new Color(((FluidMolecule) fluidBlock.getFluid()).getColor());
				}
				Gui gui = Minecraft.getMinecraft().ingameGUI;
				if (gui == null) {
					return;
				}
				ScaledResolution scaledRes = event.getResolution();
				int i1 = scaledRes.getScaledWidth() / 2 + 91;
				int j1 = scaledRes.getScaledHeight() - 39;
				int k2 = j1 - 10;
				int i6 = Minecraft.getMinecraft().player.getAir();
				int k6 = MathHelper.ceil((i6 - 2) * 10.0D / 300.0D);
				int i7 = MathHelper.ceil(i6 * 10.0D / 300.0D) - k6;

				for (int k7 = 0; k7 < k6 + i7; ++k7) {

					float red = color.getRed() / 255f;
					float green = color.getGreen() / 255f;
					float blue = color.getBlue() / 255f;
					GlStateManager.color(red, green, blue, 1.0F);
					Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(ModGlobals.ID, "textures/gui/icons.png"));
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
	public void onOreEvent(OreDictionary.OreRegisterEvent event) {
		String oreName = event.getName();
		if (ModRecipes.getOreDictionaryHandlers() != null) {
			for (IOreDictionaryHandler handler : ModRecipes.getOreDictionaryHandlers()) {
				if (handler.canHandle(oreName)) {
					handler.handle(oreName);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public void onDecayEvent(RadiationDecayEvent event) {
		if (event.getPlayer() != null) {
			String nameBeforeDecay = event.getLongName(event.getBefore());
			String nameAfterDecay = event.getLongName(event.getAfter());
			String time = TimeHelper.getTimeFromTicks(event.getTime());
			String message = String.format("Radiation Warning: Element %s decayed into %s after %s.", nameBeforeDecay, nameAfterDecay, time);
			event.getPlayer().sendMessage(new TextComponentString(message));
		}
	}

	@SubscribeEvent
	public void getFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
		ItemStack stack = event.getItemStack();
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
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(ModGlobals.ID)) {
			ModConfig.loadConfig();
		}
	}

	@SubscribeEvent
	public void onAttack(LivingAttackEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
			ItemStack weapon = entity.getActiveItemStack();
			if (weapon.isEmpty()) {
				return;
			}
			NBTTagList list = weapon.getEnchantmentTagList();
			if (list == null) {
				return;
			}
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound enchantmentTag = list.getCompoundTagAt(i);
				Enchantment enchant = Enchantment.getEnchantmentByID(enchantmentTag.getShort("id"));
				if (enchant instanceof PotionEnchantmentCoated) {
					((PotionEnchantmentCoated) enchant).applyEffect(event.getEntityLiving());
				}
			}
		}
	}

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {

	}

}
