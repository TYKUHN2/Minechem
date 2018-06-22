package minechem.init;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import com.google.common.collect.Lists;

import minechem.api.IOreDictionaryHandler;
import minechem.handler.oredict.OreDictionaryAppliedEnergisticsHandler;
import minechem.handler.oredict.OreDictionaryDefaultHandler;
import minechem.item.ItemElement;
import minechem.item.blueprint.ItemBlueprint;
import minechem.item.blueprint.MinechemBlueprint;
import minechem.item.element.Element;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.Molecule;
import minechem.item.molecule.MoleculeEnum;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeCloneChemistJournal;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeDecomposerChance;
import minechem.recipe.RecipeDecomposerFluid;
import minechem.recipe.RecipeDecomposerFluidSelect;
import minechem.recipe.RecipeDecomposerSelect;
import minechem.recipe.RecipeDecomposerSuper;
import minechem.recipe.RecipePotionCoating;
import minechem.recipe.RecipePotionSpiking;
import minechem.recipe.RecipeSynthesisOld;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import minechem.utils.MinechemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class ModRecipes {

	private static final ModRecipes recipes = new ModRecipes();
	//public ArrayList decomposition = new ArrayList();
	//public ArrayList synthesis = new ArrayList();

	/*
	 * Energy costs for synthesizing (without multiplier)
	 */
	public static int BLOCK_MULTIPLIER = 8;
	public static int COST_INGOT = 300;
	public static int COST_BLOCK = 15;
	public static int COST_ITEM = 10;
	public static int COST_METALBLOCK = COST_INGOT * BLOCK_MULTIPLIER;
	public static int COST_PLANK = 20;
	public static int COST_LAPIS = 20;
	public static int COST_LAPISBLOCK = COST_LAPIS * BLOCK_MULTIPLIER;
	public static int COST_GRASS = 40;
	public static int COST_SMOOTH = 30;
	public static int COST_STAR = 6000;
	public static int COST_SUGAR = 30;
	public static int COST_GLOW = 70;
	public static int COST_GLOWBLOCK = COST_GLOW * 4;
	public static int COST_TEAR = 3000;
	public static int COST_OBSIDIAN = 100;
	public static int COST_PLANT = 200;
	public static int COST_FOOD = 250;
	public static int COST_WOOD = COST_PLANK * 4;
	public static int COST_GLASS = 300;
	public static int COST_PANE = COST_GLASS / 3;
	public static int COST_WOOL = 200;
	public static int COST_CARPET = COST_WOOL / 2;
	public static int COST_GEM = 1000;
	public static int COST_GEMBLOCK = COST_GEM * BLOCK_MULTIPLIER;

	/*
	 * Amount of fluid for given unit
	 */
	private static final int INGOT_AMOUNT = 144;
	private static final int BUCKET_AMOUNT = 1000;

	public static ModRecipes getInstance() {
		return recipes;
	}

	public void registerFluidRecipes() {
		// Quick and dirty fix for the Thermal Expansion Resonant Ender 6x bug
		int threeQuarterFluidPerIngot = 180;
		RecipeDecomposer.add(new RecipeDecomposerFluid(new FluidStack(FluidRegistry.WATER, BUCKET_AMOUNT), new PotionChemical[] {
				this.element(ElementEnum.H, 2),
				this.element(ElementEnum.O)
		}));

		// Lava
		// This assumes lava is composed from cobblestone at a 4:1 ratio
		//   as well as having slightly higher purity
		RecipeDecomposer.add(new RecipeDecomposerFluidSelect("lava", BUCKET_AMOUNT / 4, 0.2F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Mg),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ti),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pb),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Na),
						this.element(ElementEnum.Cl)
				})
		}));

		// Mod fluids
		// Checks if the fluid exists
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("water", BUCKET_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.H, 2),
				this.element(ElementEnum.O)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("iron.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("gold.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Au, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("copper.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Cu, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("tin.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Sn, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("aluminum.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Al, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("cobalt.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Co, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("ardite.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 2),
				this.element(ElementEnum.W, 2),
				this.element(ElementEnum.Si, 2)
		});

		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("bronze.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Cu, 12),
				this.element(ElementEnum.Sn, 4)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("aluminumbrass.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Cu, 12),
				this.element(ElementEnum.Al, 4)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("manyullyn.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Co, 8),
				this.element(ElementEnum.Fe, 1),
				this.element(ElementEnum.W, 1),
				this.element(ElementEnum.Si, 1)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("alumite.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Al, 8),
				this.element(ElementEnum.Fe, 3),
				this.molecule(MoleculeEnum.siliconDioxide, 2),
				this.molecule(MoleculeEnum.magnesiumOxide, 1)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("obsidian.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16),
				this.molecule(MoleculeEnum.magnesiumOxide, 8)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("steel.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 14),
				this.element(ElementEnum.C, 2)
		}); // This ratio should be tested
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("stone.seared", INGOT_AMOUNT, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconOxide, 12),
				this.molecule(MoleculeEnum.ironOxide, 4)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("glass.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("emerald.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.molecule(MoleculeEnum.beryl, 6),
				this.element(ElementEnum.Cr, 6),
				this.element(ElementEnum.V, 6)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("blood.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.O, 6),
				this.element(ElementEnum.Fe, 2),
				this.molecule(MoleculeEnum.ironOxide, 8)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("nickel.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Ni, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("lead.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Pb, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("silver.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Ag, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("platinum.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Pt, 16)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("invar.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 10),
				this.element(ElementEnum.Ni, 6)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("electrum.molten", INGOT_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Ag, 8),
				this.element(ElementEnum.Au, 8)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("ender", threeQuarterFluidPerIngot, new PotionChemical[] {
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.element(ElementEnum.Es),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate)
		});
		registerMFRFluidRecipes();

		if (ModConfig.decomposeChemicalFluids) {
			for (ElementEnum element : ModFluids.FLUID_ELEMENTS.keySet()) {
				RecipeDecomposerFluid.add(new RecipeDecomposerFluid(new FluidStack(ModFluids.FLUID_ELEMENTS.get(element), 125), new Element(element, 1)));
			}
			for (MoleculeEnum molecule : ModFluids.FLUID_MOLECULES.keySet()) {
				RecipeDecomposerFluid.add(new RecipeDecomposerFluid(new FluidStack(ModFluids.FLUID_MOLECULES.get(molecule), 125), molecule.componentsArray()));
			}
		}
	}

	private void registerMFRFluidRecipes() {
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("mushroomsoup", BUCKET_AMOUNT, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 4),
				this.molecule(MoleculeEnum.pantherine, 2)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("chocolatemilk", BUCKET_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Ca, 4),
				this.molecule(MoleculeEnum.theobromine, 1)
		});
		RecipeDecomposerFluid.createAndAddFluidRecipeSafely("milk", BUCKET_AMOUNT, new PotionChemical[] {
				this.element(ElementEnum.Ca, 4),
				this.molecule(MoleculeEnum.oleicAcid, 1)
		});
		// If someone figures out compositions for the other fluids, add them here.
	}

	public void registerVanillaChemicalRecipes() {

		// Molecules
		Molecule moleculeSiliconDioxide = this.molecule(MoleculeEnum.siliconDioxide, 4);
		Molecule moleculeCellulose = this.molecule(MoleculeEnum.cellulose, 1);
		Molecule moleculePolyvinylChloride = this.molecule(MoleculeEnum.polyvinylChloride);
		Molecule moleculeLazurite = this.molecule(MoleculeEnum.lazurite, 9);

		// Elements
		Element elementHydrogen = this.element(ElementEnum.H, 64);
		Element elementHelium = this.element(ElementEnum.He, 64);
		Element elementCarbon = this.element(ElementEnum.C, 64);

		// Section 1 - Blocks
		// Stone
		ItemStack blockStone = new ItemStack(Blocks.STONE);
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockStone, 0.2F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Mg),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ti),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pb),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Zn),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Al),
						this.element(ElementEnum.O)
				})
		}));
		RecipeHandlerSynthesis.addShapedRecipe("smooth_stone", COST_SMOOTH, new ItemStack(Blocks.STONE, 16), "ab ", "cd ", 'a', element(ElementEnum.Si), 'b', element(ElementEnum.O, 2), 'c', element(ElementEnum.Al, 2), 'd', element(ElementEnum.O, 3));

		// Grass Block
		ItemStack blockGrass = new ItemStack(Blocks.GRASS, 1, 0);
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockGrass, 0.07F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Mg),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ti),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pb),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Zn),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ga),
						this.element(ElementEnum.As)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						moleculeCellulose
				})
		}));
		RecipeHandlerSynthesis.addShapedRecipe("grass_block", COST_GLASS, new ItemStack(Blocks.GRASS, 16), " a ", " bc", 'a', moleculeCellulose, 'b', element(ElementEnum.O, 2), 'c', element(ElementEnum.Si));
		/*
		RecipeSynthesisShapeless.add(new RecipeSynthesisShapeless(new ItemStack(Blocks.GRASS, 16), true, COST_GRASS, new PotionChemical[] {
				null,
				moleculeCellulose,
				null,
				null,
				this.element(ElementEnum.O, 2),
				this.element(ElementEnum.Si)
		}));
		*/
		// Dirt
		ItemStack blockDirt = new ItemStack(Blocks.DIRT, 1, 0);
		ItemStack blockPodzol = new ItemStack(Blocks.DIRT, 1, 2);

		RecipeDecomposer.add(new RecipeDecomposerSelect(blockDirt, 0.07F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Mg),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ti),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pb),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Zn),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ga),
						this.element(ElementEnum.As)
				})
		}));
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockPodzol, 0.07F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Mg),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ti),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pb),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Zn),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ga),
						this.element(ElementEnum.As)
				})
		}));
		RecipeHandlerSynthesis.addShapedRecipe("dirt", COST_BLOCK, new ItemStack(Blocks.DIRT, 1, 0), "a  ", "   ", "   ", 'a', molecule(MoleculeEnum.siliconDioxide));
		RecipeHandlerSynthesis.addShapedRecipe("podzol", COST_BLOCK, new ItemStack(Blocks.DIRT, 1, 2), "   ", "a  ", "   ", 'a', molecule(MoleculeEnum.siliconDioxide));

		/*
		RecipeSynthesisShapeless.add(new RecipeSynthesisShapeless(new ItemStack(Blocks.DIRT, 1, 0), true, COST_BLOCK, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesisShapeless.add(new RecipeSynthesisShapeless(new ItemStack(Blocks.DIRT, 1, 2), true, COST_BLOCK, new PotionChemical[] {
				null,
				null,
				null,
				this.molecule(MoleculeEnum.siliconDioxide),
				null,
				null,
				null,
				null,
				null
		}));
		*/
		// Cobblestone
		ItemStack blockCobblestone = new ItemStack(Blocks.COBBLESTONE);
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockCobblestone, 0.1F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Mg),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ti),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pb),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Na),
						this.element(ElementEnum.Cl)
				})
		}));
		RecipeHandlerSynthesis.addShapedRecipe("cobblestone", COST_SMOOTH, new ItemStack(Blocks.COBBLESTONE, 16), "ab ", 'a', element(ElementEnum.Si, 2), 'b', element(ElementEnum.O, 4));

		// Planks
		ItemStack blockOakWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 0);
		ItemStack blockSpruceWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 1);
		ItemStack blockBirchWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 2);
		ItemStack blockJungleWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 3);
		ItemStack blockAcaciaWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 4);
		ItemStack blockDarkOakWoodPlanks = new ItemStack(Blocks.PLANKS, 1, 5);

		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakWoodPlanks, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceWoodPlanks, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchWoodPlanks, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleWoodPlanks, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaWoodPlanks, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakWoodPlanks, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("oak_planks", COST_PLANK, blockOakWoodPlanks, "   ", "   ", " aa", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("spruce_planks", COST_PLANK, blockSpruceWoodPlanks, "   ", "   ", "aa ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("birch_planks", COST_PLANK, blockBirchWoodPlanks, "   ", "  a", "a  ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("jungle_planks", COST_PLANK, blockJungleWoodPlanks, "   ", " aa", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("acacia_planks", COST_PLANK, blockAcaciaWoodPlanks, "   ", "aa ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_planks", COST_PLANK, blockDarkOakWoodPlanks, "  a", "a  ", "   ", 'a', molecule(MoleculeEnum.cellulose));

		// Wooden Slabs
		ItemStack blockOakWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 0);
		ItemStack blockSpruceWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 1);
		ItemStack blockBirchWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 2);
		ItemStack blockJungleWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 3);
		ItemStack blockAcaciaWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 4);
		ItemStack blockDarkOakWoodSlabs = new ItemStack(Blocks.WOODEN_SLAB, 1, 5);

		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakWoodSlabs, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceWoodSlabs, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchWoodSlabs, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleWoodSlabs, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaWoodSlabs, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakWoodSlabs, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("oak_slab", COST_PLANK, blockOakWoodSlabs, "   ", "   ", "a a", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("spruce_slab", COST_PLANK, blockSpruceWoodSlabs, "   ", "  a", " a ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("birch_slab", COST_PLANK, blockBirchWoodSlabs, "   ", " a ", "a  ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("jungle_slab", COST_PLANK, blockJungleWoodSlabs, "   ", "a a", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("acacia_slab", COST_PLANK, blockAcaciaWoodSlabs, "  a", " a ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_slab", COST_PLANK, blockDarkOakWoodSlabs, " a ", "a  ", "   ", 'a', molecule(MoleculeEnum.cellulose));

		// Saplings
		ItemStack blockOakSapling = new ItemStack(Blocks.SAPLING, 1, 0);
		ItemStack blockSpruceSapling = new ItemStack(Blocks.SAPLING, 1, 1);
		ItemStack blockBirchSapling = new ItemStack(Blocks.SAPLING, 1, 2);
		ItemStack blockJungleSapling = new ItemStack(Blocks.SAPLING, 1, 3);
		ItemStack blockAcaciaSapling = new ItemStack(Blocks.SAPLING, 1, 4);
		ItemStack blockDarkOakSapling = new ItemStack(Blocks.SAPLING, 1, 5);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakSapling, 0.25F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceSapling, 0.25F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchSapling, 0.25F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleSapling, 0.25F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaSapling, 0.25F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakSapling, 0.25F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("oak_sapling", COST_PLANT, blockOakSapling, "   ", "   ", "  a", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("spruce_sapling", COST_PLANT, blockSpruceSapling, "   ", "   ", " a ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("birch_sapling", COST_PLANT, blockBirchSapling, "   ", "   ", "a  ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("jungle_sapling", COST_PLANT, blockJungleSapling, "   ", "  a", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("acacia_sapling", COST_PLANT, blockAcaciaSapling, "   ", " a ", "   ", 'a', molecule(MoleculeEnum.cellulose));
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_sapling", COST_PLANT, blockDarkOakSapling, "   ", "a  ", "   ", 'a', molecule(MoleculeEnum.cellulose));

		// Sand
		ItemStack blockSand = new ItemStack(Blocks.SAND);
		RecipeDecomposer.add(new RecipeDecomposer(blockSand, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("block_sand", COST_BLOCK, blockSand, "aaa", 'a', moleculeSiliconDioxide);

		// Gravel
		ItemStack blockGravel = new ItemStack(Blocks.GRAVEL);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockGravel, 0.35F, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("block_gravel", COST_BLOCK, blockGravel, "   ", "   ", "  a", 'a', molecule(MoleculeEnum.siliconDioxide));

		// Gold Ore
		ItemStack goldOre = new ItemStack(Blocks.GOLD_ORE);
		RecipeDecomposer.add(new RecipeDecomposer(goldOre, new PotionChemical[] {
				this.element(ElementEnum.Au, 48)
		}));

		// Iron Ore
		ItemStack ironOre = new ItemStack(Blocks.IRON_ORE);
		RecipeDecomposer.add(new RecipeDecomposer(ironOre, new PotionChemical[] {
				this.element(ElementEnum.Fe, 48)
		}));

		// Coal Ore
		ItemStack coalOre = new ItemStack(Blocks.COAL_ORE);
		RecipeDecomposer.add(new RecipeDecomposer(coalOre, new PotionChemical[] {
				this.element(ElementEnum.C, 48)
		}));

		// Log
		ItemStack blockOakLog = new ItemStack(Blocks.LOG, 1, 0);
		ItemStack blockSpruceLog = new ItemStack(Blocks.LOG, 1, 1);
		ItemStack blockBirchLog = new ItemStack(Blocks.LOG, 1, 2);
		ItemStack blockJungleLog = new ItemStack(Blocks.LOG, 1, 3);
		ItemStack blockAcaciaLog = new ItemStack(Blocks.LOG2, 1, 0);
		ItemStack blockDarkOakLog = new ItemStack(Blocks.LOG2, 1, 1);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakLog, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceLog, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchLog, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleLog, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaLog, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakLog, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 8)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("oak_log", COST_WOOD, blockOakLog, "aaa", " a ", "   ", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("spruce_log", COST_WOOD, blockSpruceLog, "   ", " a ", "aaa", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("birch_log", COST_WOOD, blockBirchLog, "a a", "   ", "a a", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("jungle_log", COST_WOOD, blockJungleLog, "a  ", "aa ", "a  ", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("acacia_log", COST_WOOD, blockAcaciaLog, "  a", " aa", "  a", 'a', molecule(MoleculeEnum.cellulose, 2));
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_log", COST_WOOD, blockDarkOakLog, " a ", "a a", " a ", 'a', molecule(MoleculeEnum.cellulose, 2));

		// Leaves
		ItemStack blockOakLeaves = new ItemStack(Blocks.LEAVES, 1, 0);
		ItemStack blockSpruceLeaves = new ItemStack(Blocks.LEAVES, 1, 1);
		ItemStack blockBirchLeaves = new ItemStack(Blocks.LEAVES, 1, 2);
		ItemStack blockJungleLeaves = new ItemStack(Blocks.LEAVES, 1, 3);
		ItemStack blockAcaciaLeaves = new ItemStack(Blocks.LEAVES2, 1, 0);
		ItemStack blockDarkOakLeaves = new ItemStack(Blocks.LEAVES2, 1, 1);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOakLeaves, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSpruceLeaves, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBirchLeaves, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockJungleLeaves, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAcaciaLeaves, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockDarkOakLeaves, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 4)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("oak_leaves", COST_BLOCK, blockOakLeaves, "aaa", " a ", "   ", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("spruce_leaves", COST_BLOCK, blockSpruceLeaves, "   ", " a ", "aaa", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("birch_leaves", COST_BLOCK, blockOakLeaves, "a a", "   ", "a a", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("jungle_leaves", COST_BLOCK, blockJungleLeaves, "a  ", "aa ", "a  ", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("acacia_leaves", COST_BLOCK, blockAcaciaLeaves, "  a", " aa", "  a", 'a', moleculeCellulose);
		RecipeHandlerSynthesis.addShapedRecipe("dark_oak_leaves", COST_BLOCK, blockDarkOakLeaves, " a ", "a a", " a ", 'a', moleculeCellulose);

		// Dyes
		ItemStack itemDyePowderBlack = new ItemStack(Items.DYE, 1, 0);
		ItemStack itemDyePowderRed = new ItemStack(Items.DYE, 1, 1);
		ItemStack itemDyePowderGreen = new ItemStack(Items.DYE, 1, 2);
		ItemStack itemDyePowderBrown = new ItemStack(Items.DYE, 1, 3);
		ItemStack itemDyePowderBlue = new ItemStack(Items.DYE, 1, 4);
		ItemStack itemDyePowderPurple = new ItemStack(Items.DYE, 1, 5);
		ItemStack itemDyePowderCyan = new ItemStack(Items.DYE, 1, 6);
		ItemStack itemDyePowderLightGray = new ItemStack(Items.DYE, 1, 7);
		ItemStack itemDyePowderGray = new ItemStack(Items.DYE, 1, 8);
		ItemStack itemDyePowderPink = new ItemStack(Items.DYE, 1, 9);
		ItemStack itemDyePowderLime = new ItemStack(Items.DYE, 1, 10);
		ItemStack itemDyePowderYellow = new ItemStack(Items.DYE, 1, 11);
		ItemStack itemDyePowderLightBlue = new ItemStack(Items.DYE, 1, 12);
		ItemStack itemDyePowderMagenta = new ItemStack(Items.DYE, 1, 13);
		ItemStack itemDyePowderOrange = new ItemStack(Items.DYE, 1, 14);
		ItemStack itemDyePowderWhite = new ItemStack(Items.DYE, 1, 15);
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderBlack, new PotionChemical[] {
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderRed, new PotionChemical[] {
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderGreen, new PotionChemical[] {
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDyePowderBrown, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.theobromine),
				this.molecule(MoleculeEnum.tannicacid)
		}));
		// Lapis
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderBlue, new PotionChemical[] {
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderPurple, new PotionChemical[] {
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderCyan, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderLightGray, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderGray, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderPink, new PotionChemical[] {
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderLime, new PotionChemical[] {
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderYellow, new PotionChemical[] {
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderLightBlue, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderMagenta, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderOrange, new PotionChemical[] {
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(itemDyePowderWhite, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderBlack, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderRed, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderGreen, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderBrown, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.theobromine)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderBlue, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderPurple, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderCyan, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderLightGray, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderGray, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderPink, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderLime, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderYellow, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderLightBlue, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderMagenta, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderOrange, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemDyePowderWhite, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment)
		}));

		// Glass
		ItemStack blockGlass = new ItemStack(Blocks.GLASS);
		ItemStack blockWhiteStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 0);
		ItemStack blockOrangeStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 1);
		ItemStack blockMagentaStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 2);
		ItemStack blockLightBlueStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 3);
		ItemStack blockYellowStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 4);
		ItemStack blockLimeStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 5);
		ItemStack blockPinkStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 6);
		ItemStack blockGrayStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 7);
		ItemStack blockLightGrayStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 8);
		ItemStack blockCyanStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 9);
		ItemStack blockPurpleStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 10);
		ItemStack blockBlueStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 11);
		ItemStack blockBrownStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 12);
		ItemStack blockGreenStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 13);
		ItemStack blockRedStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 14);
		ItemStack blockBlackStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 15);
		RecipeDecomposer.add(new RecipeDecomposer(blockGlass, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		}));

		RecipeHandlerSynthesis.addShapedRecipe("block_glass", COST_GLASS, blockGlass, "a a", "   ", "a a", 'a', moleculeSiliconDioxide);
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_white", COST_GLASS, blockWhiteStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_orange", COST_GLASS, blockOrangeStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.orangePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_magenta", COST_GLASS, blockMagentaStainedGlass, "a a", "b c", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.lightbluePigment), 'c', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_lightblue", COST_GLASS, blockLightBlueStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_yellow", COST_GLASS, blockYellowStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.yellowPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_lime", COST_GLASS, blockLimeStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.limePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_pink", COST_GLASS, blockPinkStainedGlass, "a a", "b c", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_gray", COST_GLASS, blockGrayStainedGlass, "a a", "b c", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_lightgray", COST_GLASS, blockLightGrayStainedGlass, "a a", "bbc", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_cyan", COST_GLASS, blockCyanStainedGlass, "a a", "b c", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_purple", COST_GLASS, blockPurpleStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.purplePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_blue", COST_GLASS, blockBlueStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.lazurite));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_brown", COST_GLASS, blockBrownStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.tannicacid));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_green", COST_GLASS, blockGreenStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.greenPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_red", COST_GLASS, blockRedStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_stained_glass_black", COST_GLASS, blockBlackStainedGlass, "a a", " b ", "a a", 'a', moleculeSiliconDioxide, 'b', molecule(MoleculeEnum.blackPigment));

		// Glass Panes
		ItemStack blockGlassPane = new ItemStack(Blocks.GLASS_PANE);
		ItemStack blockWhiteStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 0);
		ItemStack blockOrangeStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 1);
		ItemStack blockMagentaStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 2);
		ItemStack blockLightBlueStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 3);
		ItemStack blockYellowStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 4);
		ItemStack blockLimeStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 5);
		ItemStack blockPinkStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 6);
		ItemStack blockGrayStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 7);
		ItemStack blockLightGrayStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 8);
		ItemStack blockCyanStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 9);
		ItemStack blockPurpleStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 10);
		ItemStack blockBlueStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 11);
		ItemStack blockBrownStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 12);
		ItemStack blockGreenStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 13);
		ItemStack blockRedStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 14);
		ItemStack blockBlackStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 15);
		RecipeDecomposer.add(new RecipeDecomposer(blockGlassPane, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 6)
		}));
		Molecule siO = new Molecule(MoleculeEnum.siliconDioxide, 1);
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane", COST_PANE, blockGlassPane, "aaa", "   ", "aaa", 'a', siO);
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_white", COST_PANE, blockWhiteStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_orange", COST_PANE, blockOrangeStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.orangePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_magenta", COST_PANE, blockMagentaStainedGlassPane, "aaa", "b c", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.lightbluePigment), 'c', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_lightblue", COST_PANE, blockLightBlueStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_yellow", COST_PANE, blockYellowStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.yellowPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_lime", COST_PANE, blockLimeStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.limePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_pink", COST_PANE, blockPinkStainedGlassPane, "aaa", "b c", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_gray", COST_PANE, blockGrayStainedGlassPane, "aaa", "b c", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_lightgray", COST_PANE, blockLightGrayStainedGlassPane, "aaa", "bbc", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.blackPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_cyan", COST_PANE, blockCyanStainedGlassPane, "aaa", "b c", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.whitePigment), 'c', molecule(MoleculeEnum.lightbluePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_purple", COST_PANE, blockPurpleStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.purplePigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_blue", COST_PANE, blockBlueStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.lazurite));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_brown", COST_PANE, blockBrownStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.tannicacid));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_green", COST_PANE, blockGreenStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.greenPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_red", COST_PANE, blockRedStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("block_glass_pane_black", COST_PANE, blockBlackStainedGlassPane, "aaa", " b ", "aaa", 'a', siO, 'b', molecule(MoleculeEnum.blackPigment));

		// Lapis Lazuli Ore
		ItemStack blockOreLapis = new ItemStack(Blocks.LAPIS_ORE);
		RecipeDecomposer.add(new RecipeDecomposer(blockOreLapis, new PotionChemical[] {
				this.molecule(MoleculeEnum.lazurite, 6),
				this.molecule(MoleculeEnum.sodalite),
				this.molecule(MoleculeEnum.noselite),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.pyrite)
		}));

		// Lapis Lazuli Block
		ItemStack blockLapis = new ItemStack(Blocks.LAPIS_BLOCK);
		RecipeDecomposer.add(new RecipeDecomposer(blockLapis, new PotionChemical[] {
				this.molecule(MoleculeEnum.lazurite, 9)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("lapis_block", COST_LAPISBLOCK, blockLapis, "a  ", "   ", "   ", 'a', moleculeLazurite);

		// Cobweb
		ItemStack blockCobweb = new ItemStack(Blocks.WEB);
		RecipeDecomposer.add(new RecipeDecomposer(blockCobweb, new PotionChemical[] {
				this.molecule(MoleculeEnum.fibroin)
		}));

		//double tall plants
		ItemStack blockSunFlower = new ItemStack(Blocks.DOUBLE_PLANT, 1, 0);
		ItemStack blockLilac = new ItemStack(Blocks.DOUBLE_PLANT, 1, 1);
		ItemStack blockTallGrass = new ItemStack(Blocks.DOUBLE_PLANT, 1, 2);
		ItemStack blockLargeFern = new ItemStack(Blocks.DOUBLE_PLANT, 1, 3);
		ItemStack blockRoseBush = new ItemStack(Blocks.DOUBLE_PLANT, 1, 4);
		ItemStack blockPeony = new ItemStack(Blocks.DOUBLE_PLANT, 1, 5);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockSunFlower, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLilac, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockTallGrass, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLargeFern, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRoseBush, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPeony, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("sunflower", COST_PLANT, blockSunFlower, "a", "b", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2), 'b', molecule(MoleculeEnum.yellowPigment));
		RecipeHandlerSynthesis.addShapedRecipe("lilac", COST_PLANT, blockLilac, "abc", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2), 'b', molecule(MoleculeEnum.redPigment), 'c', new Molecule(MoleculeEnum.whitePigment, 2));
		RecipeHandlerSynthesis.addShapedRecipe("tall_grass", COST_PLANT, blockTallGrass, "a  ", "   ", "   ", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2));
		RecipeHandlerSynthesis.addShapedRecipe("large_fern", COST_PLANT, blockLargeFern, " a ", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2));
		RecipeHandlerSynthesis.addShapedRecipe("rose_bush", COST_PLANT, blockRoseBush, "ab ", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2), 'b', molecule(MoleculeEnum.redPigment));
		RecipeHandlerSynthesis.addShapedRecipe("peony", COST_PLANT, blockPeony, "abc", 'a', new Molecule(MoleculeEnum.shikimicAcid, 2), 'b', molecule(MoleculeEnum.redPigment), 'c', molecule(MoleculeEnum.whitePigment));

		// Sandstone
		ItemStack blockSandStone = new ItemStack(Blocks.SANDSTONE, 1, 0);
		ItemStack blockChiseledSandStone = new ItemStack(Blocks.SANDSTONE, 1, 1);
		ItemStack blockSmoothSandStone = new ItemStack(Blocks.SANDSTONE, 1, 2);
		RecipeDecomposer.add(new RecipeDecomposer(blockSandStone, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(blockChiseledSandStone, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeDecomposer.add(new RecipeDecomposer(blockSmoothSandStone, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("sandstone", COST_PLANT, blockSandStone, "   ", " a ", "   ", 'a', molecule(MoleculeEnum.siliconDioxide, 16));
		RecipeHandlerSynthesis.addShapedRecipe("chiseled_sandstone", COST_PLANT, blockChiseledSandStone, "   ", "   ", " a ", 'a', molecule(MoleculeEnum.siliconDioxide, 16));
		RecipeHandlerSynthesis.addShapedRecipe("smooth_sandstone", COST_PLANT, blockSmoothSandStone, " a ", "   ", "   ", 'a', molecule(MoleculeEnum.siliconDioxide, 16));

		// Wool
		ItemStack blockWool = new ItemStack(Blocks.WOOL, 1, 0);
		ItemStack blockOrangeWool = new ItemStack(Blocks.WOOL, 1, 1);
		ItemStack blockMagentaWool = new ItemStack(Blocks.WOOL, 1, 2);
		ItemStack blockLightBlueWool = new ItemStack(Blocks.WOOL, 1, 3);
		ItemStack blockYellowWool = new ItemStack(Blocks.WOOL, 1, 4);
		ItemStack blockLimeWool = new ItemStack(Blocks.WOOL, 1, 5);
		ItemStack blockPinkWool = new ItemStack(Blocks.WOOL, 1, 6);
		ItemStack blockGrayWool = new ItemStack(Blocks.WOOL, 1, 7);
		ItemStack blockLightGrayWool = new ItemStack(Blocks.WOOL, 1, 8);
		ItemStack blockCyanWool = new ItemStack(Blocks.WOOL, 1, 9);
		ItemStack blockPurpleWool = new ItemStack(Blocks.WOOL, 1, 10);
		ItemStack blockBlueWool = new ItemStack(Blocks.WOOL, 1, 11);
		ItemStack blockBrownWool = new ItemStack(Blocks.WOOL, 1, 12);
		ItemStack blockGreenWool = new ItemStack(Blocks.WOOL, 1, 13);
		ItemStack blockRedWool = new ItemStack(Blocks.WOOL, 1, 14);
		ItemStack blockBlackWool = new ItemStack(Blocks.WOOL, 1, 15);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOrangeWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockMagentaWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLightBlueWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockYellowWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLimeWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPinkWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockGrayWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockLightGrayWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockCyanWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPurpleWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBlueWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBrownWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.tannicacid)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockGreenWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRedWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBlackWool, 0.6F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockOrangeWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockMagentaWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockLightBlueWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockYellowWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockLimeWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockPinkWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockGrayWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockLightGrayWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockCyanWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockPurpleWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockBlueWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockGreenWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockRedWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockBlackWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.blackPigment)
		}));

		// Wool carpet
		ItemStack carpetBlockWool = new ItemStack(Blocks.CARPET, 1, 0);
		ItemStack carpetBlockOrangeWool = new ItemStack(Blocks.CARPET, 1, 1);
		ItemStack carpetBlockMagentaWool = new ItemStack(Blocks.CARPET, 1, 2);
		ItemStack carpetBlockLightBlueWool = new ItemStack(Blocks.CARPET, 1, 3);
		ItemStack carpetBlockYellowWool = new ItemStack(Blocks.CARPET, 1, 4);
		ItemStack carpetBlockLimeWool = new ItemStack(Blocks.CARPET, 1, 5);
		ItemStack carpetBlockPinkWool = new ItemStack(Blocks.CARPET, 1, 6);
		ItemStack carpetBlockGrayWool = new ItemStack(Blocks.CARPET, 1, 7);
		ItemStack carpetBlockLightGrayWool = new ItemStack(Blocks.CARPET, 1, 8);
		ItemStack carpetBlockCyanWool = new ItemStack(Blocks.CARPET, 1, 9);
		ItemStack carpetBlockPurpleWool = new ItemStack(Blocks.CARPET, 1, 10);
		ItemStack carpetBlockBlueWool = new ItemStack(Blocks.CARPET, 1, 11);
		ItemStack carpetBlockBrownWool = new ItemStack(Blocks.CARPET, 1, 12);
		ItemStack carpetBlockGreenWool = new ItemStack(Blocks.CARPET, 1, 13);
		ItemStack carpetBlockRedWool = new ItemStack(Blocks.CARPET, 1, 14);
		ItemStack carpetBlockBlackWool = new ItemStack(Blocks.CARPET, 1, 15);
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockOrangeWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockMagentaWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockLightBlueWool, 0.2F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockYellowWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockLimeWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockPinkWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockGrayWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockLightGrayWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockCyanWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockPurpleWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockBlueWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockBrownWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.tannicacid)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockGreenWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockRedWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(carpetBlockBlackWool, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockOrangeWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockMagentaWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockLightBlueWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockYellowWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockLimeWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockPinkWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockGrayWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockLightGrayWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockCyanWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockPurpleWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockBlueWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockGreenWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockRedWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(carpetBlockBlackWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.blackPigment)
		}));

		// Flowers
		ItemStack blockYellowFlower = new ItemStack(Blocks.YELLOW_FLOWER);
		ItemStack blockPoppyFlower = new ItemStack(Blocks.RED_FLOWER, 1, 0);
		ItemStack blockBlueOrchid = new ItemStack(Blocks.RED_FLOWER, 1, 1);
		ItemStack blockAllium = new ItemStack(Blocks.RED_FLOWER, 1, 2);
		ItemStack blockAsureBluet = new ItemStack(Blocks.RED_FLOWER, 1, 3);
		ItemStack blockRedTulip = new ItemStack(Blocks.RED_FLOWER, 1, 4);
		ItemStack blockOrangeTulip = new ItemStack(Blocks.RED_FLOWER, 1, 5);
		ItemStack blockWhiteTulip = new ItemStack(Blocks.RED_FLOWER, 1, 6);
		ItemStack blockPinkTulip = new ItemStack(Blocks.RED_FLOWER, 1, 7);
		ItemStack blockOxeyeDaisy = new ItemStack(Blocks.RED_FLOWER, 1, 8);

		RecipeDecomposer.add(new RecipeDecomposerChance(blockYellowFlower, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.yellowPigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPoppyFlower, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.redPigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockBlueOrchid, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAllium, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.purplePigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockAsureBluet, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.whitePigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRedTulip, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.redPigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOrangeTulip, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.orangePigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockWhiteTulip, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.whitePigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPinkTulip, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.whitePigment, 1),
				new Molecule(MoleculeEnum.redPigment, 1)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOxeyeDaisy, 0.3F, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.whitePigment, 1)
		}));

		// Mushrooms
		ItemStack blockMushroomBrown = new ItemStack(Blocks.BROWN_MUSHROOM);
		ItemStack blockMushroomRed = new ItemStack(Blocks.RED_MUSHROOM);
		if (ModConfig.recreationalChemicalEffects) {
			RecipeDecomposer.add(new RecipeDecomposer(blockMushroomBrown, new PotionChemical[] {
					this.molecule(MoleculeEnum.psilocybin),
					this.molecule(MoleculeEnum.water, 2)
			}));
		}
		RecipeDecomposer.add(new RecipeDecomposer(blockMushroomRed, new PotionChemical[] {
				this.molecule(MoleculeEnum.pantherine),
				this.molecule(MoleculeEnum.water, 2)
		}));

		// Block of Gold
		RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(Blocks.GOLD_BLOCK), new PotionChemical[] {
				this.element(ElementEnum.Au, 144)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("gold_block", COST_METALBLOCK, new ItemStack(Blocks.GOLD_BLOCK), "aaa", "aaa", "aaa", 'a', element(ElementEnum.Au, 16));

		// Block of Iron
		RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(Blocks.IRON_BLOCK), new PotionChemical[] {
				this.element(ElementEnum.Fe, 144)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("iron_block", COST_METALBLOCK, new ItemStack(Blocks.IRON_BLOCK), "aaa", "aaa", "aaa", 'a', element(ElementEnum.Fe, 16));

		// TNT
		ItemStack blockTnt = new ItemStack(Blocks.TNT);
		RecipeDecomposer.add(new RecipeDecomposer(blockTnt, new PotionChemical[] {
				this.molecule(MoleculeEnum.tnt)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockTnt, false, COST_OBSIDIAN, new PotionChemical[] {
				this.molecule(MoleculeEnum.tnt)
		}));

		// Obsidian
		ItemStack blockObsidian = new ItemStack(Blocks.OBSIDIAN);
		RecipeDecomposer.add(new RecipeDecomposer(blockObsidian, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16),
				this.molecule(MoleculeEnum.magnesiumOxide, 8)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("obsidian", COST_OBSIDIAN, new ItemStack(Blocks.GOLD_BLOCK), "aaa", "b a", "bbb", 'a', molecule(MoleculeEnum.siliconDioxide, 4), 'b', molecule(MoleculeEnum.magnesiumOxide, 2));

		// Diamond Ore
		ItemStack blockOreDiamond = new ItemStack(Blocks.DIAMOND_ORE);
		RecipeDecomposer.add(new RecipeDecomposer(blockOreDiamond, new PotionChemical[] {
				this.molecule(MoleculeEnum.fullrene, 6)
		}));

		// Block of Diamond
		ItemStack blockDiamond = new ItemStack(Blocks.DIAMOND_BLOCK);
		RecipeDecomposer.add(new RecipeDecomposer(blockDiamond, new PotionChemical[] {
				this.molecule(MoleculeEnum.fullrene, 27)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("diamond_block", COST_GEMBLOCK, blockDiamond, "aaa", "aaa", "aaa", 'a', molecule(MoleculeEnum.fullrene, 3));

		// Pressure Plate
		ItemStack blockPressurePlatePlanks = new ItemStack(Blocks.WOODEN_PRESSURE_PLATE);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockPressurePlatePlanks, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 4)
		}));

		// Redstone Ore
		ItemStack blockOreRedstone = new ItemStack(Blocks.REDSTONE_ORE);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockOreRedstone, 0.8F, new PotionChemical[] {
				this.molecule(MoleculeEnum.iron3oxide, 9),
				this.element(ElementEnum.Cu, 9)
		}));

		// Cactus
		ItemStack blockCactus = new ItemStack(Blocks.CACTUS);
		RecipeDecomposer.add(new RecipeDecomposer(blockCactus, new PotionChemical[] {
				this.molecule(MoleculeEnum.mescaline),
				this.molecule(MoleculeEnum.water, 20)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("cactus_black", COST_PLANT, blockCactus, "a a", " b ", "a a", 'a', molecule(MoleculeEnum.water, 5), 'b', molecule(MoleculeEnum.mescaline));

		// Pumpkin
		ItemStack blockPumpkin = new ItemStack(Blocks.PUMPKIN);
		RecipeDecomposer.add(new RecipeDecomposer(blockPumpkin, new PotionChemical[] {
				this.molecule(MoleculeEnum.cucurbitacin)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(blockPumpkin, false, COST_PLANT, new PotionChemical[] {
				this.molecule(MoleculeEnum.cucurbitacin)
		}));

		// Pumpkin seed
		ItemStack pumpkinSeed = new ItemStack(Items.PUMPKIN_SEEDS);
		RecipeDecomposer.add(new RecipeDecomposer(pumpkinSeed, new PotionChemical[] {
				this.molecule(MoleculeEnum.water)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(pumpkinSeed, false, COST_PLANT, new PotionChemical[] {
				this.molecule(MoleculeEnum.cucurbitacin)
		}));

		// Netherrack
		ItemStack blockNetherrack = new ItemStack(Blocks.NETHERRACK);
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockNetherrack, 0.1F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 2),
						this.element(ElementEnum.O),
						this.element(ElementEnum.Fe)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 2),
						this.element(ElementEnum.Ni),
						this.element(ElementEnum.Tc)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 3),
						this.element(ElementEnum.Ti),
						this.element(ElementEnum.Fe)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 1),
						this.element(ElementEnum.W, 4),
						this.element(ElementEnum.Cr, 2)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 10),
						this.element(ElementEnum.W, 1),
						this.element(ElementEnum.Zn, 8),
						this.element(ElementEnum.Be, 4)
				})
		}));

		// Nether Brick
		ItemStack itemNetherbrick = new ItemStack(Items.NETHERBRICK);
		RecipeDecomposer.add(new RecipeDecomposerSelect(itemNetherbrick, 0.15F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 2),
						this.element(ElementEnum.C),
						this.element(ElementEnum.Fe)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 2),
						this.element(ElementEnum.Ni),
						this.element(ElementEnum.Tc)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 3),
						this.element(ElementEnum.Ti),
						this.element(ElementEnum.Fe)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 1),
						this.element(ElementEnum.W, 4),
						this.element(ElementEnum.Cr, 2)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 10),
						this.element(ElementEnum.W, 1),
						this.element(ElementEnum.Zn, 8),
						this.element(ElementEnum.Be, 4)
				})
		}));
		RecipeHandlerSynthesis.addShapedRecipe("nether_brick", COST_SMOOTH, itemNetherbrick, "aa ", "bc ", "dd ", 'a', element(ElementEnum.Si, 2), 'b', element(ElementEnum.Zn, 2), 'c', element(ElementEnum.W, 1), 'd', element(ElementEnum.Be, 2));

		// Water Bottle
		ItemStack itemPotion = new ItemStack(Items.POTIONITEM, 1, 0);
		RecipeDecomposer.add(new RecipeDecomposer(itemPotion, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 5),
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("water_bottle", COST_ITEM, itemPotion, " a ", "aba", " a ", 'a', molecule(MoleculeEnum.siliconDioxide, 4), 'b', molecule(MoleculeEnum.water, 5));

		//Ice Block
		ItemStack blockIce = new ItemStack(Blocks.ICE);
		RecipeDecomposer.add(new RecipeDecomposer(blockIce, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 8)
		}));

		// Soul Sand
		ItemStack blockSlowSand = new ItemStack(Blocks.SOUL_SAND);
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockSlowSand, 0.2F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pb, 3),
						this.element(ElementEnum.Be, 1),
						this.element(ElementEnum.Si, 2),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pb, 1),
						this.element(ElementEnum.Si, 5),
						this.element(ElementEnum.O, 2)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 2),
						this.element(ElementEnum.O)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 6),
						this.element(ElementEnum.O, 2)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Es, 1),
						this.element(ElementEnum.O, 2)
				})
		}));

		// Glowstone
		ItemStack blockGlowStone = new ItemStack(Blocks.GLOWSTONE);
		RecipeDecomposer.add(new RecipeDecomposer(blockGlowStone, new PotionChemical[] {
				this.element(ElementEnum.P, 4)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("glowstone_block", COST_GLOWBLOCK, blockGlowStone, "aa", "aa", 'a', element(ElementEnum.P));

		// Mycelium
		ItemStack blockMycelium = new ItemStack(Blocks.MYCELIUM);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockMycelium, 0.09F, new PotionChemical[] {
				this.molecule(MoleculeEnum.fingolimod)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(new ItemStack(Blocks.MYCELIUM, 16), false, COST_GRASS, new PotionChemical[] {
				this.molecule(MoleculeEnum.fingolimod)
		}));

		// End Stone
		ItemStack blockWhiteStone = new ItemStack(Blocks.END_STONE);
		RecipeDecomposer.add(new RecipeDecomposerSelect(blockWhiteStone, 0.8F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 2),
						this.element(ElementEnum.O),
						this.element(ElementEnum.H, 4),
						this.element(ElementEnum.Li)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Es)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Pu)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fr)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Nd)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Si, 2),
						this.element(ElementEnum.O, 4)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.H, 4)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Be, 8)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Li, 2)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Zr)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Na)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Rb)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Ga),
						this.element(ElementEnum.As)
				})
		}));

		// Emerald Ore
		ItemStack blockOreEmerald = new ItemStack(Blocks.EMERALD_ORE);
		RecipeDecomposer.add(new RecipeDecomposer(blockOreEmerald, new PotionChemical[] {
				this.molecule(MoleculeEnum.beryl, 6),
				this.element(ElementEnum.Cr, 6),
				this.element(ElementEnum.V, 6)
		}));

		// Emerald Block
		ItemStack blockEmerald = new ItemStack(Blocks.EMERALD_BLOCK);
		RecipeHandlerSynthesis.addShapedRecipe("emerald_block", COST_GEMBLOCK, blockEmerald, "aaa", "bcb", "aaa", 'a', element(ElementEnum.Cr, 3), 'b', element(ElementEnum.V, 9), 'c', molecule(MoleculeEnum.beryl, 18));

		RecipeDecomposer.add(new RecipeDecomposer(blockEmerald, new PotionChemical[] {
				this.molecule(MoleculeEnum.beryl, 18),
				this.element(ElementEnum.Cr, 18),
				this.element(ElementEnum.V, 18)
		}));

		// Section 2 - Items
		// Apple
		ItemStack itemAppleRed = new ItemStack(Items.APPLE);
		RecipeDecomposer.add(new RecipeDecomposer(itemAppleRed, new PotionChemical[] {
				this.molecule(MoleculeEnum.malicAcid)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemAppleRed, false, COST_FOOD, new PotionChemical[] {
				this.molecule(MoleculeEnum.malicAcid),
				this.molecule(MoleculeEnum.water, 2)
		}));

		// Arrow
		ItemStack itemArrow = new ItemStack(Items.ARROW);
		RecipeDecomposer.add(new RecipeDecomposer(itemArrow, new PotionChemical[] {
				this.element(ElementEnum.Si),
				this.element(ElementEnum.O, 2),
				this.element(ElementEnum.N, 6)
		}));

		// Coal
		ItemStack itemCoal = new ItemStack(Items.COAL);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemCoal, 0.92F, new PotionChemical[] {
				this.element(ElementEnum.C, 8)
		}));

		// Coal Block
		ItemStack blockCoal = new ItemStack(Blocks.COAL_BLOCK);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockCoal, 0.82F, new PotionChemical[] {
				this.element(ElementEnum.C, 72)
		}));

		// Charcoal
		ItemStack itemChar = new ItemStack(Items.COAL, 1, 1);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemChar, 0.82F, new PotionChemical[] {
				this.element(ElementEnum.C, 8)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemChar, false, COST_ITEM, new PotionChemical[] {
				this.element(ElementEnum.C, 4),
				this.element(ElementEnum.C, 4)
		}));
		// Diamond
		ItemStack itemDiamond = new ItemStack(Items.DIAMOND);
		RecipeDecomposer.add(new RecipeDecomposer(itemDiamond, new PotionChemical[] {
				this.molecule(MoleculeEnum.fullrene, 3)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("diamond", COST_GEM, itemDiamond, "   ", " a ", "   ", 'a', molecule(MoleculeEnum.fullrene, 3));

		// Polytool
		RecipeHandlerSynthesis.addShapedRecipe("polytool", COST_STAR, new ItemStack(ModItems.polytool), " a ", "a a", " a ", 'a', molecule(MoleculeEnum.fullrene, 64));

		// Iron Ingot
		ItemStack itemIngotIron = new ItemStack(Items.IRON_INGOT);
		RecipeDecomposer.add(new RecipeDecomposer(itemIngotIron, new PotionChemical[] {
				this.element(ElementEnum.Fe, 16)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemIngotIron, false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 16)
		}));

		// Gold Ingot
		ItemStack itemIngotGold = new ItemStack(Items.GOLD_INGOT);
		RecipeDecomposer.add(new RecipeDecomposer(itemIngotGold, new PotionChemical[] {
				this.element(ElementEnum.Au, 16)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemIngotGold, false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Au, 16)
		}));

		// Stick
		ItemStack itemStick = new ItemStack(Items.STICK);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemStick, 0.3F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));

		// String
		ItemStack itemString = new ItemStack(Items.STRING);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemString, 0.45F, new PotionChemical[] {
				this.molecule(MoleculeEnum.serine),
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.alinine)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("string", COST_ITEM, itemString, "abc", 'a', molecule(MoleculeEnum.serine), 'b', molecule(MoleculeEnum.glycine), 'c', molecule(MoleculeEnum.alinine));

		// Feather
		ItemStack itemFeather = new ItemStack(Items.FEATHER);
		RecipeDecomposer.add(new RecipeDecomposer(itemFeather, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 8),
				this.element(ElementEnum.N, 6)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("feather", COST_ITEM, itemFeather, "aba", "aca", "ada", 'a', element(ElementEnum.N), 'b', molecule(MoleculeEnum.water, 2), 'c', molecule(MoleculeEnum.water, 1), 'd', molecule(MoleculeEnum.water, 5));

		// Gunpowder
		ItemStack itemGunpowder = new ItemStack(Items.GUNPOWDER);
		RecipeDecomposer.add(new RecipeDecomposer(itemGunpowder, new PotionChemical[] {
				this.molecule(MoleculeEnum.potassiumNitrate),
				this.element(ElementEnum.S, 2),
				this.element(ElementEnum.C)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("gun_powder", COST_ITEM, itemGunpowder, "ab ", "c  ", "   ", 'a', molecule(MoleculeEnum.potassiumNitrate), 'b', element(ElementEnum.C), 'c', element(ElementEnum.S, 2));

		// Bread
		ItemStack itemBread = new ItemStack(Items.BREAD);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemBread, 0.1F, new PotionChemical[] {
				this.molecule(MoleculeEnum.starch),
				this.molecule(MoleculeEnum.sucrose)
		}));

		// Flint
		ItemStack itemFlint = new ItemStack(Items.FLINT);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemFlint, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("flint", COST_ITEM, itemFlint, " a ", "aaa", 'a', moleculeSiliconDioxide);

		// Golden Apple
		ItemStack itemAppleGold = new ItemStack(Items.GOLDEN_APPLE, 1, 0);
		RecipeDecomposer.add(new RecipeDecomposer(itemAppleGold, new PotionChemical[] {
				this.molecule(MoleculeEnum.malicAcid),
				this.element(ElementEnum.Au, 64)
		}));

		ItemStack itemDoorAcacia = new ItemStack(Items.ACACIA_DOOR);
		ItemStack itemDoorBirch = new ItemStack(Items.BIRCH_DOOR);
		ItemStack itemDoorDarkOak = new ItemStack(Items.DARK_OAK_DOOR);
		ItemStack itemDoorJungle = new ItemStack(Items.JUNGLE_DOOR);
		ItemStack itemDoorOak = new ItemStack(Items.OAK_DOOR);
		ItemStack itemDoorSpruce = new ItemStack(Items.SPRUCE_DOOR);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorAcacia, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorBirch, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorDarkOak, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorJungle, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorOak, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 12)
		}));
		RecipeDecomposer.add(new RecipeDecomposerChance(itemDoorSpruce, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 12)
		}));

		// Bucket
		ItemStack itemBucket = new ItemStack(Items.BUCKET);
		RecipeDecomposer.add(new RecipeDecomposer(itemBucket, this.element(ElementEnum.Fe, 48)));
		RecipeHandlerSynthesis.addShapedRecipe("bucket", COST_FOOD, itemBucket, "a a", " a ", 'a', element(ElementEnum.Fe, 16));

		// Water Bucket
		ItemStack itemBucketWater = new ItemStack(Items.WATER_BUCKET);
		RecipeDecomposer.add(new RecipeDecomposer(itemBucketWater, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 16),
				this.element(ElementEnum.Fe, 48)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("water_bucket", COST_FOOD, itemBucketWater, "aba", " a ", 'a', element(ElementEnum.Fe, 16), 'b', molecule(MoleculeEnum.water, 16));

		// Redstone Dust
		ItemStack itemRedstoneDust = new ItemStack(Items.REDSTONE);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemRedstoneDust, 0.42F, new PotionChemical[] {
				this.molecule(MoleculeEnum.iron3oxide),
				this.element(ElementEnum.Cu)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("redstone_dust", COST_LAPIS, itemRedstoneDust, "  a", " b ", 'a', molecule(MoleculeEnum.iron3oxide), 'b', element(ElementEnum.Cu));

		// Redstone Block
		ItemStack blockRedstone = new ItemStack(Blocks.REDSTONE_BLOCK);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRedstone, 0.42F, new PotionChemical[] {
				this.molecule(MoleculeEnum.iron3oxide, 9),
				this.element(ElementEnum.Cu, 9)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("redstone_block", COST_LAPISBLOCK, blockRedstone, "  a", " b ", 'a', molecule(MoleculeEnum.iron3oxide, 9), 'b', element(ElementEnum.Cu, 9));

		// Snowball
		ItemStack itemSnowball = new ItemStack(Items.SNOWBALL);
		RecipeDecomposer.add(new RecipeDecomposer(itemSnowball, new PotionChemical[] {
				this.molecule(MoleculeEnum.water)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("snowball", COST_FOOD, new ItemStack(Items.SNOWBALL, 5), "a a", " a ", "a a", 'a', molecule(MoleculeEnum.water));

		// Leather
		ItemStack itemLeather = new ItemStack(Items.LEATHER);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemLeather, 0.2F, new PotionChemical[] {
				this.molecule(MoleculeEnum.keratin)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("leather", COST_ITEM, new ItemStack(Items.LEATHER, 5), "   ", " a ", "   ", 'a', molecule(MoleculeEnum.keratin));

		// Brick
		ItemStack itemBrick = new ItemStack(Items.BRICK);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemBrick, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.kaolinite)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("brick", COST_ITEM, new ItemStack(Items.BRICK, 8), "aa ", "aa ", 'a', molecule(MoleculeEnum.kaolinite));

		// Clay
		ItemStack itemClayBall = new ItemStack(Items.CLAY_BALL);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemClayBall, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.kaolinite)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(new ItemStack(Items.CLAY_BALL, 2), false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.kaolinite)
		}));

		// Reed
		ItemStack itemReed = new ItemStack(Items.REEDS);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemReed, 0.65F, new PotionChemical[] {
				this.molecule(MoleculeEnum.sucrose),
				this.element(ElementEnum.H, 2),
				this.element(ElementEnum.O)
		}));

		// Vines
		ItemStack itemVine = new ItemStack(Blocks.VINE);
		RecipeDecomposer.add(new RecipeDecomposer(itemVine, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 6)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("vine", COST_GRASS, itemVine, "a a", "a a", "a a", 'a', molecule(MoleculeEnum.cellulose));

		// Paper
		ItemStack itemPaper = new ItemStack(Items.PAPER);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemPaper, 0.35F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("paper", COST_ITEM, new ItemStack(Items.PAPER, 8), " a ", " a ", " a ", 'a', molecule(MoleculeEnum.cellulose));

		//Compass
		ItemStack itemCompass = new ItemStack(Items.COMPASS);
		RecipeDecomposer.add(new RecipeDecomposerSelect(itemCompass, 1.0F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe, 64)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe, 64),
						this.molecule(MoleculeEnum.iron3oxide)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe, 64),
						this.molecule(MoleculeEnum.iron3oxide),
						this.element(ElementEnum.Cu)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Fe, 64),
						this.element(ElementEnum.Cu)
				})
		}));

		// Map
		ItemStack itemMap = new ItemStack(Items.MAP);
		RecipeDecomposer.add(new RecipeDecomposerSuper(itemMap, NonNullList.from(ItemStack.EMPTY, itemPaper, itemPaper, itemPaper, itemPaper, itemPaper, itemPaper, itemPaper, itemPaper, itemCompass)));

		// Book
		ItemStack itemBook = new ItemStack(Items.BOOK);
		RecipeDecomposer.add(new RecipeDecomposerSuper(itemBook, NonNullList.from(ItemStack.EMPTY, itemPaper, itemPaper, itemPaper, itemLeather)));

		// Bookshelf
		ItemStack blockBook = new ItemStack(Blocks.BOOKSHELF);
		RecipeDecomposer.add(new RecipeDecomposerSuper(blockBook, NonNullList.from(ItemStack.EMPTY, blockBirchWoodPlanks, blockBirchWoodPlanks, blockBirchWoodPlanks, itemBook, itemBook, itemBook, blockBirchWoodPlanks, blockBirchWoodPlanks, blockBirchWoodPlanks)));

		// Slimeball
		ItemStack itemSlimeBall = new ItemStack(Items.SLIME_BALL);
		RecipeDecomposer.add(new RecipeDecomposerSelect(itemSlimeBall, 0.9F, new RecipeDecomposer[] {
				new RecipeDecomposer(new PotionChemical[] {
						this.molecule(MoleculeEnum.pmma)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.element(ElementEnum.Hg)
				}),
				new RecipeDecomposer(new PotionChemical[] {
						this.molecule(MoleculeEnum.water, 10)
				})
		}));

		// Glowstone Dust
		ItemStack itemGlowstone = new ItemStack(Items.GLOWSTONE_DUST);
		RecipeDecomposer.add(new RecipeDecomposer(itemGlowstone, new PotionChemical[] {
				this.element(ElementEnum.P)
		}));

		// Bone
		ItemStack itemBone = new ItemStack(Items.BONE);
		RecipeDecomposer.add(new RecipeDecomposer(itemBone, new PotionChemical[] {
				this.molecule(MoleculeEnum.hydroxylapatite)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemBone, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.hydroxylapatite)
		}));

		// Sugar
		ItemStack itemSugar = new ItemStack(Items.SUGAR);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemSugar, 0.75F, new PotionChemical[] {
				this.molecule(MoleculeEnum.sucrose)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(itemSugar, false, COST_SUGAR, new PotionChemical[] {
				this.molecule(MoleculeEnum.sucrose)
		}));

		// Melon Slice
		ItemStack itemMelon = new ItemStack(Items.MELON);
		RecipeDecomposer.add(new RecipeDecomposer(itemMelon, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 1)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(new ItemStack(Items.MELON), false, COST_FOOD, new PotionChemical[] {
				this.molecule(MoleculeEnum.cucurbitacin),
				this.molecule(MoleculeEnum.asparticAcid),
				this.molecule(MoleculeEnum.water, 1)
		}));

		// Melon
		ItemStack blockMelon = new ItemStack(Blocks.MELON_BLOCK);
		RecipeDecomposer.add(new RecipeDecomposer(blockMelon, new PotionChemical[] {
				this.molecule(MoleculeEnum.cucurbitacin),
				this.molecule(MoleculeEnum.asparticAcid),
				this.molecule(MoleculeEnum.water, 16)
		}));
		RecipeSynthesisOld.add(new RecipeSynthesisOld(new ItemStack(Blocks.MELON_BLOCK, 1), false, COST_FOOD, new PotionChemical[] {
				this.molecule(MoleculeEnum.cucurbitacin),
				this.molecule(MoleculeEnum.asparticAcid),
				this.molecule(MoleculeEnum.water, 16)
		}));

		// Cooked Chicken
		ItemStack itemChickenCooked = new ItemStack(Items.COOKED_CHICKEN);
		RecipeDecomposer.add(new RecipeDecomposer(itemChickenCooked, new PotionChemical[] {
				this.element(ElementEnum.K),
				this.element(ElementEnum.Na),
				this.element(ElementEnum.C, 2)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("cooked_chicken", COST_FOOD, itemChickenCooked, "abc", 'a', element(ElementEnum.K, 16), 'b', element(ElementEnum.Na, 16), 'c', element(ElementEnum.C, 16));

		// Rotten Flesh
		ItemStack itemRottenFlesh = new ItemStack(Items.ROTTEN_FLESH);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemRottenFlesh, 0.05F, new PotionChemical[] {
				new Molecule(MoleculeEnum.nodularin, 1)
		}));

		// Enderpearl
		ItemStack itemEnderPearl = new ItemStack(Items.ENDER_PEARL);
		RecipeDecomposer.add(new RecipeDecomposer(itemEnderPearl, new PotionChemical[] {
				this.element(ElementEnum.Es),
				this.molecule(MoleculeEnum.calciumCarbonate, 8)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("ender_pearl", COST_TEAR, itemEnderPearl, "aaa", "aba", "aaa", 'a', molecule(MoleculeEnum.calciumCarbonate), 'b', element(ElementEnum.Es));

		// EnderDragon Egg
		ItemStack blockEnderDragonEgg = new ItemStack(Blocks.DRAGON_EGG);
		RecipeDecomposer.add(new RecipeDecomposer(blockEnderDragonEgg, new PotionChemical[] {
				this.molecule(MoleculeEnum.calciumCarbonate, 16),
				this.molecule(MoleculeEnum.hydroxylapatite, 6),
				this.element(ElementEnum.Pu, 18),
				this.element(ElementEnum.Fm, 8)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("dragon_egg", COST_BLOCK, blockEnderDragonEgg, "abc", " d ", 'a', molecule(MoleculeEnum.calciumCarbonate, 18), 'b', molecule(MoleculeEnum.hydroxylapatite, 8), 'c', element(ElementEnum.Pu, 22), 'd', element(ElementEnum.Fm, 12));

		// Blaze Rod
		ItemStack itemBlazeRod = new ItemStack(Items.BLAZE_ROD);
		RecipeDecomposer.add(new RecipeDecomposer(itemBlazeRod, new PotionChemical[] {
				this.element(ElementEnum.Pu, 6)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("blaze_rod", COST_TEAR, itemBlazeRod, "a  ", "a  ", "a  ", 'a', element(ElementEnum.Pu, 2));

		// Blaze Powder
		ItemStack itemBlazePowder = new ItemStack(Items.BLAZE_POWDER);
		RecipeDecomposer.add(new RecipeDecomposer(itemBlazePowder, new PotionChemical[] {
				this.element(ElementEnum.Pu)
		}));

		// Ghast Tear
		ItemStack itemGhastTear = new ItemStack(Items.GHAST_TEAR);
		RecipeDecomposer.add(new RecipeDecomposer(itemGhastTear, new PotionChemical[] {
				this.element(ElementEnum.Yb, 4),
				this.element(ElementEnum.No, 4)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("ghast_tear", COST_TEAR, itemGhastTear, "aab", " ab", " b ", 'a', element(ElementEnum.Yb), 'b', element(ElementEnum.No));

		if (ModConfig.recreationalChemicalEffects) {
			// Nether Wart
			ItemStack itemNetherStalkSeeds = new ItemStack(Items.NETHER_WART);
			RecipeDecomposer.add(new RecipeDecomposerChance(itemNetherStalkSeeds, 0.5F, new PotionChemical[] {
					this.molecule(MoleculeEnum.cocainehcl)
			}));
		}

		// Spider Eye
		ItemStack itemSpiderEye = new ItemStack(Items.SPIDER_EYE);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemSpiderEye, 0.2F, new PotionChemical[] {
				this.molecule(MoleculeEnum.tetrodotoxin)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("spider_eye", COST_ITEM, itemSpiderEye, "a  ", " b ", "  a", 'a', element(ElementEnum.C), 'b', molecule(MoleculeEnum.tetrodotoxin));

		// Fermented Spider Eye
		ItemStack itemFermentedSpiderEye = new ItemStack(Items.FERMENTED_SPIDER_EYE);
		RecipeDecomposer.add(new RecipeDecomposer(itemFermentedSpiderEye, new PotionChemical[] {
				this.element(ElementEnum.Po),
				this.molecule(MoleculeEnum.ethanol)
		}));

		// Magma Cream
		ItemStack itemMagmaCream = new ItemStack(Items.MAGMA_CREAM);
		RecipeDecomposer.add(new RecipeDecomposer(itemMagmaCream, new PotionChemical[] {
				this.element(ElementEnum.Hg),
				this.element(ElementEnum.Pu),
				this.molecule(MoleculeEnum.pmma, 3)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("magma_cream", COST_TEAR, itemMagmaCream, " a ", "bcb", " b ", 'a', element(ElementEnum.Pu), 'b', molecule(MoleculeEnum.pmma), 'c', element(ElementEnum.Hg));

		// Glistering Melon
		ItemStack itemSpeckledMelon = new ItemStack(Items.SPECKLED_MELON);
		RecipeDecomposer.add(new RecipeDecomposer(itemSpeckledMelon, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 4),
				this.molecule(MoleculeEnum.whitePigment),
				this.element(ElementEnum.Au, 1)
		}));

		// Emerald
		ItemStack itemEmerald = new ItemStack(Items.EMERALD);
		RecipeDecomposer.add(new RecipeDecomposer(itemEmerald, new PotionChemical[] {
				this.molecule(MoleculeEnum.beryl, 2),
				this.element(ElementEnum.Cr, 2),
				this.element(ElementEnum.V, 2)
		}));
		RecipeHandlerSynthesis.addShapedRecipe("emerald", 5000, itemEmerald, " a ", "bcb", " a ", 'a', element(ElementEnum.Cr), 'b', element(ElementEnum.V), 'c', molecule(MoleculeEnum.beryl, 2));

		// Wheat
		ItemStack itemWheat = new ItemStack(Items.WHEAT);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemWheat, 0.3F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2)
		}));

		// Carrot
		ItemStack itemCarrot = new ItemStack(Items.CARROT);
		RecipeDecomposer.add(new RecipeDecomposer(itemCarrot, new PotionChemical[] {
				this.molecule(MoleculeEnum.retinol)
		}));

		// Potato
		ItemStack itemPotato = new ItemStack(Items.POTATO);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemPotato, 0.4F, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 8),
				this.element(ElementEnum.K, 2),
				this.molecule(MoleculeEnum.cellulose)
		}));

		// Golden Carrot
		ItemStack itemGoldenCarrot = new ItemStack(Items.GOLDEN_CARROT);
		RecipeDecomposer.add(new RecipeDecomposer(itemGoldenCarrot, new PotionChemical[] {
				this.molecule(MoleculeEnum.retinol),
				this.element(ElementEnum.Au, 4)
		}));

		// Nether Star
		ItemStack itemNetherStar = new ItemStack(Items.NETHER_STAR);
		RecipeDecomposer.add(new RecipeDecomposer(itemNetherStar, new PotionChemical[] {
				elementHelium,
				elementHelium,
				elementHelium,
				elementCarbon,
				this.element(ElementEnum.Cn, 16),
				elementHelium,
				elementHydrogen,
				elementHydrogen,
				elementHydrogen
		}));
		//TODO
		RecipeHandlerSynthesis.addShapedRecipe("nether_star", COST_STAR, itemNetherStar, "aaa", "bca", "ddd", 'a', elementHelium, 'b', elementCarbon, 'c', element(ElementEnum.Cn, 16), 'd', elementHydrogen);

		// Nether Quartz
		ItemStack itemNetherQuartz = new ItemStack(Items.QUARTZ);
		RecipeDecomposer.add(new RecipeDecomposer(itemNetherQuartz, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				this.molecule(MoleculeEnum.galliumarsenide, 1)
		}));

		// Music Records
		ItemStack itemRecord13 = new ItemStack(Items.RECORD_13);
		ItemStack itemRecordCat = new ItemStack(Items.RECORD_CAT);
		ItemStack itemRecordFar = new ItemStack(Items.RECORD_FAR);
		ItemStack itemRecordMall = new ItemStack(Items.RECORD_MALL);
		ItemStack itemRecordMellohi = new ItemStack(Items.RECORD_MELLOHI);
		ItemStack itemRecordStal = new ItemStack(Items.RECORD_STAL);
		ItemStack itemRecordStrad = new ItemStack(Items.RECORD_STRAD);
		ItemStack itemRecordWard = new ItemStack(Items.RECORD_WARD);
		ItemStack itemRecordChirp = new ItemStack(Items.RECORD_CHIRP);
		ItemStack itemRecord11 = new ItemStack(Items.RECORD_11);
		ItemStack itemRecordWait = new ItemStack(Items.RECORD_WAIT);
		ItemStack itemRecordBlocks = new ItemStack(Items.RECORD_BLOCKS);

		RecipeDecomposer.add(new RecipeDecomposer(itemRecord13, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordCat, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordFar, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordMall, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordMellohi, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordStal, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordStrad, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordWard, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordChirp, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecord11, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordWait, moleculePolyvinylChloride, moleculePolyvinylChloride));
		RecipeDecomposer.add(new RecipeDecomposer(itemRecordBlocks, moleculePolyvinylChloride, moleculePolyvinylChloride));
		//TODO
		RecipeHandlerSynthesis.addShapedRecipe("record_13", COST_GEM, itemRecord13, "aa ", "   ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_cat", COST_GEM, itemRecordCat, " aa", "   ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_far", COST_GEM, itemRecordFar, "  a", "a  ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_mall", COST_GEM, itemRecordMall, "   ", "aa ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_mellohi", COST_GEM, itemRecordMellohi, "   ", " aa", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_stal", COST_GEM, itemRecordStal, "   ", "  a", "a  ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_strad", COST_GEM, itemRecordStrad, "   ", "   ", "aa ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_ward", COST_GEM, itemRecordWard, "   ", "   ", " aa", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_chirp", COST_GEM, itemRecordChirp, "a  ", "   ", "  a", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_11", COST_GEM, itemRecord11, "a  ", " a ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_wait", COST_GEM, itemRecordWait, "a  ", "a  ", "   ", 'a', moleculePolyvinylChloride);
		RecipeHandlerSynthesis.addShapedRecipe("record_blocks", COST_GEM, itemRecordBlocks, "a  ", " a ", "   ", 'a', moleculePolyvinylChloride);

		//Ironbars
		ItemStack bars = new ItemStack(Blocks.IRON_BARS);
		RecipeDecomposer.add(new RecipeDecomposer(bars, new PotionChemical[] {
				element(ElementEnum.Fe, 3),
				element(ElementEnum.Fe, 3)
		}));

		RecipeSynthesisOld.add(new RecipeSynthesisOld(bars, false, COST_BLOCK, new PotionChemical[] {
				element(ElementEnum.Fe, 3),
				element(ElementEnum.Fe, 3)
		}));

		//Uranium Ore
		RecipeDecomposer.createAndAddRecipeSafely("oreUranium", element(ElementEnum.U, 48));
	}

	public void RegisterModRecipes() {
		//OreDict stuff
		RecipeDecomposer.createAndAddRecipeSafely("dustSalt", new Element(ElementEnum.Na), new Element(ElementEnum.Cl));
		RecipeDecomposer.createAndAddRecipeSafely("logWood", new Molecule(MoleculeEnum.cellulose, 8));
		RecipeDecomposer.createAndAddRecipeSafely("plankWood", new Molecule(MoleculeEnum.cellulose, 2));
		RecipeDecomposer.createAndAddRecipeSafely("ingotIron", this.element(ElementEnum.Fe, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotGold", this.element(ElementEnum.Au, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotCopper", this.element(ElementEnum.Cu, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotTin", this.element(ElementEnum.Sn, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotSilver", this.element(ElementEnum.Ag, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotLead", this.element(ElementEnum.Pb, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotPlatinum", this.element(ElementEnum.Pt, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotAluminium", this.element(ElementEnum.Al, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotMagnesium", this.element(ElementEnum.Mg, 16));
		RecipeDecomposer.createAndAddRecipeSafely("ingotSteel", new PotionChemical[] {
				this.element(ElementEnum.Fe, 15),
				this.element(ElementEnum.C, 1)
		});
		RecipeDecomposer.createAndAddRecipeSafely("ingotHSLA", new PotionChemical[] {
				this.element(ElementEnum.Fe, 15),
				this.element(ElementEnum.C, 1)
		});
		RecipeDecomposer.createAndAddRecipeSafely("ingotBronze", new PotionChemical[] {
				this.element(ElementEnum.Cu, 12),
				this.element(ElementEnum.Sn, 4)
		});
		RecipeDecomposer.createAndAddRecipeSafely("ingotElectrum", new PotionChemical[] {
				this.element(ElementEnum.Ag, 8),
				this.element(ElementEnum.Au, 8)
		});
		RecipeDecomposer.createAndAddRecipeSafely("ingotInvar", new PotionChemical[] {
				this.element(ElementEnum.Fe, 10),
				this.element(ElementEnum.Ni, 6)
		});

		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotIron", false, COST_INGOT, this.element(ElementEnum.Fe, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotGold", false, COST_INGOT, this.element(ElementEnum.Au, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotCopper", false, COST_INGOT, this.element(ElementEnum.Cu, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotTin", false, COST_INGOT, this.element(ElementEnum.Sn, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotSilver", false, COST_INGOT, this.element(ElementEnum.Ag, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotLead", false, COST_INGOT, this.element(ElementEnum.Pb, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotPlatinum", false, COST_INGOT, this.element(ElementEnum.Pt, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotAluminium", false, COST_INGOT, this.element(ElementEnum.Al, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotMagnesium", false, COST_INGOT, this.element(ElementEnum.Mg, 16));
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotSteel", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 15),
				this.element(ElementEnum.C, 1)
		});
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotHSLA", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 15),
				this.element(ElementEnum.C, 1)
		});
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotBronze", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Cu, 12),
				this.element(ElementEnum.Sn, 4)
		});
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotElectrum", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Ag, 8),
				this.element(ElementEnum.Au, 8)
		});
		RecipeHandlerSynthesis.createAndAddRecipeSafely("ingotInvar", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 10),
				this.element(ElementEnum.Ni, 6)
		});
		//Thermal Expansion
		if (Loader.isModLoaded("ThermalExpansion")) {
			Block rockwool = Block.REGISTRY.getObject(new ResourceLocation("ThermalExpansion", "Rockwool"));
			ItemStack blockRockWool = new ItemStack(rockwool, 1, 0);
			ItemStack blockRockOrangeWool = new ItemStack(rockwool, 1, 1);
			ItemStack blockRockMagentaWool = new ItemStack(rockwool, 1, 2);
			ItemStack blockRockLightBlueWool = new ItemStack(rockwool, 1, 3);
			ItemStack blockRockYellowWool = new ItemStack(rockwool, 1, 4);
			ItemStack blockRockLimeWool = new ItemStack(rockwool, 1, 5);
			ItemStack blockRockPinkWool = new ItemStack(rockwool, 1, 6);
			ItemStack blockRockGrayWool = new ItemStack(rockwool, 1, 7);
			ItemStack blockRockLightGrayWool = new ItemStack(rockwool, 1, 8);
			ItemStack blockRockCyanWool = new ItemStack(rockwool, 1, 9);
			ItemStack blockRockPurpleWool = new ItemStack(rockwool, 1, 10);
			ItemStack blockRockBlueWool = new ItemStack(rockwool, 1, 11);
			ItemStack blockRockBrownWool = new ItemStack(rockwool, 1, 12);
			ItemStack blockRockGreenWool = new ItemStack(rockwool, 1, 13);
			ItemStack blockRockRedWool = new ItemStack(rockwool, 1, 14);
			ItemStack blockRockBlackWool = new ItemStack(rockwool, 1, 15);

			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.whitePigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockOrangeWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.orangePigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockMagentaWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.lightbluePigment),
					this.molecule(MoleculeEnum.redPigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockLightBlueWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.lightbluePigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockYellowWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.yellowPigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockLimeWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.limePigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockPinkWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.redPigment),
					this.molecule(MoleculeEnum.whitePigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockGrayWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.whitePigment),
					this.molecule(MoleculeEnum.blackPigment, 2)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockLightGrayWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.whitePigment),
					this.molecule(MoleculeEnum.blackPigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockCyanWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.lightbluePigment),
					this.molecule(MoleculeEnum.whitePigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockPurpleWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.purplePigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockBlueWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.lazurite)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockBrownWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.tannicacid)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockGreenWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.greenPigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockRedWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.redPigment)
			}));
			RecipeDecomposer.add(new RecipeDecomposerChance(blockRockBlackWool, 0.2F, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.blackPigment)
			}));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool", COST_WOOL, blockRockWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.whitePigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_orange", COST_WOOL, blockRockOrangeWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.orangePigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_magenta", COST_WOOL, blockRockMagentaWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.redPigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_lightblue", COST_WOOL, blockRockLightBlueWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.lightbluePigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_yellow", COST_WOOL, blockRockYellowWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.yellowPigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_lime", COST_WOOL, blockRockLimeWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.limePigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_pink", COST_WOOL, blockRockPinkWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.redPigment), molecule(MoleculeEnum.whitePigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_gray", COST_WOOL, blockRockGrayWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.whitePigment), molecule(MoleculeEnum.blackPigment, 2));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_lightgray", COST_WOOL, blockRockLightGrayWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.whitePigment, 2), molecule(MoleculeEnum.blackPigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_cyan", COST_WOOL, blockRockCyanWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.lightbluePigment), molecule(MoleculeEnum.whitePigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_purple", COST_WOOL, blockRockPurpleWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.purplePigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_blue", COST_WOOL, blockRockBlueWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.lazurite));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_green", COST_WOOL, blockRockGreenWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.greenPigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_red", COST_WOOL, blockRockRedWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.redPigment));
			RecipeHandlerSynthesis.addShapelessRecipe("rock_wool_black", COST_WOOL, blockRockBlackWool, molecule(MoleculeEnum.asbestos, 2), molecule(MoleculeEnum.blackPigment));

			Block glass = Block.REGISTRY.getObject(new ResourceLocation("ThermalExpansion", "Glass"));
			Block frame = Block.REGISTRY.getObject(new ResourceLocation("ThermalExpansion", "Frame"));
			Block light = Block.REGISTRY.getObject(new ResourceLocation("ThermalExpansion", "Light"));
			ItemStack glassStack = new ItemStack(glass);
			ItemStack lightFrameStack = new ItemStack(frame, 1, 9);
			ItemStack lightStack = new ItemStack(light);

			RecipeDecomposer.add(new RecipeDecomposer(glassStack, new PotionChemical[] {
					this.molecule(MoleculeEnum.magnesiumOxide, 8),
					this.molecule(MoleculeEnum.siliconOxide, 16),
					this.element(ElementEnum.Pb, 8)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(lightFrameStack, new PotionChemical[] {
					this.molecule(MoleculeEnum.siliconDioxide, 4),
					this.molecule(MoleculeEnum.galliumarsenide, 1),
					this.molecule(MoleculeEnum.magnesiumOxide, 16),
					this.molecule(MoleculeEnum.siliconOxide, 32),
					this.element(ElementEnum.Pb, 16),
					this.element(ElementEnum.Cu, 16)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(lightStack, new PotionChemical[] {
					this.molecule(MoleculeEnum.siliconDioxide, 4),
					this.molecule(MoleculeEnum.galliumarsenide, 1),
					this.molecule(MoleculeEnum.magnesiumOxide, 16),
					this.molecule(MoleculeEnum.siliconOxide, 32),
					this.element(ElementEnum.Pb, 16),
					this.element(ElementEnum.Cu, 16),
					this.element(ElementEnum.P, 4)
			}));

		}

		if (Loader.isModLoaded("ThermalFoundation")) {
			Item bucket = Item.REGISTRY.getObject(new ResourceLocation("ThermalFoundation", "bucket"));
			Item material = Item.REGISTRY.getObject(new ResourceLocation("ThermalFoundation", "material"));

			ItemStack redstoneBucket = new ItemStack(bucket, 1, 0);
			ItemStack glowstoneBucket = new ItemStack(bucket, 1, 1);
			ItemStack enderBucket = new ItemStack(bucket, 1, 2);
			ItemStack signalumBlend = new ItemStack(material, 1, 42);
			ItemStack lumiumBlend = new ItemStack(material, 1, 43);
			ItemStack enderiumBlend = new ItemStack(material, 1, 44);
			ItemStack signalumIngot = new ItemStack(material, 1, 74);
			ItemStack lumiumIngot = new ItemStack(material, 1, 75);
			ItemStack enderiumIngot = new ItemStack(material, 1, 76);

			RecipeDecomposer.add(new RecipeDecomposer(redstoneBucket, new PotionChemical[] {
					this.element(ElementEnum.Cu, 4),
					this.element(ElementEnum.Fe, 48),
					this.molecule(MoleculeEnum.iron3oxide, 4)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(enderBucket, new PotionChemical[] {
					this.element(ElementEnum.Fe, 48),
					this.element(ElementEnum.Es, 4),
					this.molecule(MoleculeEnum.calciumCarbonate, 32)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(glowstoneBucket, new PotionChemical[] {
					this.element(ElementEnum.Fe, 48),
					this.element(ElementEnum.P, 4)
			}));

			RecipeDecomposer.add(new RecipeDecomposer(signalumBlend, new PotionChemical[] {
					this.element(ElementEnum.Cu, 12),
					this.element(ElementEnum.Ag, 4),
					this.molecule(MoleculeEnum.iron3oxide)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(signalumIngot, new PotionChemical[] {
					this.element(ElementEnum.Cu, 12),
					this.element(ElementEnum.Ag, 4),
					this.molecule(MoleculeEnum.iron3oxide)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(lumiumBlend, new PotionChemical[] {
					this.element(ElementEnum.Sn, 12),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.P)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(lumiumIngot, new PotionChemical[] {
					this.element(ElementEnum.Sn, 12),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.P)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(enderiumBlend, new PotionChemical[] {
					this.element(ElementEnum.Sn, 8),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.Pt, 4),
					this.element(ElementEnum.Es),
					this.molecule(MoleculeEnum.calciumCarbonate, 8)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(enderiumIngot, new PotionChemical[] {
					this.element(ElementEnum.Sn, 8),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.Pt, 4),
					this.element(ElementEnum.Es),
					this.molecule(MoleculeEnum.calciumCarbonate, 8)
			}));
			RecipeHandlerSynthesis.addShapelessRecipe("signalum_blend", COST_INGOT, signalumBlend, element(ElementEnum.Cu, 12), element(ElementEnum.Ag, 4), molecule(MoleculeEnum.iron3oxide));

			RecipeSynthesisOld.add(new RecipeSynthesisOld(signalumIngot, false, COST_INGOT, new PotionChemical[] {
					this.element(ElementEnum.Cu, 12),
					this.element(ElementEnum.Ag, 4),
					this.molecule(MoleculeEnum.iron3oxide)
			}));
			RecipeHandlerSynthesis.addShapelessRecipe("lumium_blend", COST_INGOT, lumiumBlend, element(ElementEnum.Sn, 12), element(ElementEnum.Ag, 4), element(ElementEnum.P));
			RecipeSynthesisOld.add(new RecipeSynthesisOld(lumiumIngot, false, COST_INGOT, new PotionChemical[] {
					this.element(ElementEnum.Sn, 12),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.P)
			}));
			RecipeHandlerSynthesis.addShapelessRecipe("enderium_blend", COST_INGOT, enderiumBlend, element(ElementEnum.Sn, 8), element(ElementEnum.Ag, 4), element(ElementEnum.Pt, 4), element(ElementEnum.Es));
			RecipeSynthesisOld.add(new RecipeSynthesisOld(enderiumIngot, false, COST_INGOT * 2, new PotionChemical[] {
					this.element(ElementEnum.Sn, 8),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.Pt, 4),
					this.element(ElementEnum.Es),
					this.molecule(MoleculeEnum.calciumCarbonate, 8),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Pu),
					this.element(ElementEnum.C, 8),
					this.element(ElementEnum.S, 16)
			}));
		}

		//Redstone Arsenal
		if (Loader.isModLoaded("RedstoneArsenal")) {
			Item alloy = Item.REGISTRY.getObject(new ResourceLocation("RedstoneArsenal", "material"));
			ItemStack blend = new ItemStack(alloy, 1, 0);
			ItemStack ingot = new ItemStack(alloy, 1, 32);
			ItemStack nugget = new ItemStack(alloy, 1, 64);
			ItemStack gem = new ItemStack(alloy, 1, 96);
			RecipeDecomposer.add(new RecipeDecomposer(blend, new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.Ag, 8),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Cu)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(ingot, new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.Ag, 8),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Cu)
			}));
			RecipeDecomposer.add(new RecipeDecomposerSelect(nugget, 0.11F, new RecipeDecomposer(new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.Ag, 8),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Cu)
			})));
			RecipeDecomposer.add(new RecipeDecomposer(gem, new PotionChemical[] {
					this.molecule(MoleculeEnum.fullrene, 3),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Cu)
			}));
			RecipeSynthesisOld.add(new RecipeSynthesisOld(blend, false, COST_INGOT * 2, new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.Ag, 8),
					this.molecule(MoleculeEnum.iron3oxide, 2),
					this.element(ElementEnum.Cu, 2)
			}));
			RecipeSynthesisOld.add(new RecipeSynthesisOld(ingot, false, COST_INGOT * 2, new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.Ag, 8),
					this.molecule(MoleculeEnum.iron3oxide, 2),
					this.element(ElementEnum.Cu, 2)
			}));
			RecipeSynthesisOld.add(new RecipeSynthesisOld(gem, false, COST_INGOT * 2, new PotionChemical[] {
					this.molecule(MoleculeEnum.fullrene, 3),
					this.molecule(MoleculeEnum.iron3oxide, 2),
					this.element(ElementEnum.Cu, 2)
			}));
		}

		//EnderIO
		if (Loader.isModLoaded("EnderIO")) {
			Item alloy = Item.REGISTRY.getObject(new ResourceLocation("EnderIO", "itemAlloy"));
			ItemStack electricalSteel = new ItemStack(alloy, 1, 0);
			ItemStack energeticAlloy = new ItemStack(alloy, 1, 1);
			ItemStack vibrantAlloy = new ItemStack(alloy, 1, 2);
			ItemStack redstoneAlloy = new ItemStack(alloy, 1, 3);
			ItemStack conductiveIron = new ItemStack(alloy, 1, 4);
			ItemStack pulsatingIron = new ItemStack(alloy, 1, 5);
			ItemStack darkSteel = new ItemStack(alloy, 1, 6);
			ItemStack soularium = new ItemStack(alloy, 1, 7);
			RecipeDecomposer.add(new RecipeDecomposer(electricalSteel, new PotionChemical[] {
					this.element(ElementEnum.Fe, 8),
					this.element(ElementEnum.C, 4),
					this.element(ElementEnum.Si, 4)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(energeticAlloy, new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.P, 1),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Cu)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(vibrantAlloy, new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.P, 1),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Cu),
					this.element(ElementEnum.Es),
					this.molecule(MoleculeEnum.calciumCarbonate, 4)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(redstoneAlloy, new PotionChemical[] {
					this.element(ElementEnum.Si, 12),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Cu)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(conductiveIron, new PotionChemical[] {
					this.element(ElementEnum.Fe, 12),
					this.molecule(MoleculeEnum.iron3oxide),
					this.element(ElementEnum.Cu)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(pulsatingIron, new PotionChemical[] {
					this.element(ElementEnum.Fe, 12),
					this.element(ElementEnum.Es),
					this.molecule(MoleculeEnum.calciumCarbonate, 6)
			}));
			RecipeDecomposer.add(new RecipeDecomposer(darkSteel, new PotionChemical[] {
					this.molecule(MoleculeEnum.magnesiumOxide, 4),
					this.molecule(MoleculeEnum.siliconOxide, 8),
					this.element(ElementEnum.Fe, 8),
					this.element(ElementEnum.C, 4)
			}));
			RecipeDecomposer.add(new RecipeDecomposerSelect(soularium, 0.4F, new RecipeDecomposer[] {
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Pb, 3),
							this.element(ElementEnum.Be, 1),
							this.element(ElementEnum.Si, 2),
							this.element(ElementEnum.O),
							this.element(ElementEnum.Au, 8)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Pb, 1),
							this.element(ElementEnum.Si, 5),
							this.element(ElementEnum.O, 2),
							this.element(ElementEnum.Au, 8)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Si, 2),
							this.element(ElementEnum.O),
							this.element(ElementEnum.Au, 8)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Si, 6),
							this.element(ElementEnum.O, 2),
							this.element(ElementEnum.Au, 8)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Es, 1),
							this.element(ElementEnum.O, 2),
							this.element(ElementEnum.Au, 8)
					})
			}));
		}

		if (Loader.isModLoaded("appliedenergistics2")) {
			MoleculeEnum certusQuartzMolecule = MoleculeEnum.aluminiumPhosphate;
			MoleculeEnum chargedCertusQuartzMolecule = MoleculeEnum.aluminiumHypophosphite;
			PotionChemical chargedCertusQuartzChemical = new Molecule(chargedCertusQuartzMolecule);

			PotionChemical[] chargedCertusQuartzDecompositionFormula = new PotionChemical[] {
					new Molecule(chargedCertusQuartzMolecule, 4)
			};

			PotionChemical[] quartzGlassDecompositionFormula = new PotionChemical[] {
					new Molecule(certusQuartzMolecule, 5),
					new Molecule(MoleculeEnum.siliconDioxide, 16)
			};

			Item item = Item.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "item.ItemMultiMaterial"));
			Block skyStone = Block.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "tile.BlockSkyStone"));
			Block quartzGlass = Block.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "tile.BlockQuartzGlass"));
			ItemStack charged = new ItemStack(item, 1, 1);
			ItemStack singularity = new ItemStack(item, 1, 47);
			ItemStack skystone = new ItemStack(skyStone);
			ItemStack quartzglass = new ItemStack(quartzGlass);

			RecipeDecomposer.add(new RecipeDecomposer(charged, chargedCertusQuartzDecompositionFormula));
			RecipeHandlerSynthesis.addShapedRecipe("charged_certus_quartz", 30000, charged, " a ", "a a", " a ", 'a', chargedCertusQuartzChemical);

			RecipeDecomposer.add(new RecipeDecomposer(singularity, new PotionChemical[] {
					this.element(ElementEnum.Fm, 148),
					this.element(ElementEnum.Md, 142),
					this.element(ElementEnum.No, 133),
					this.element(ElementEnum.Lr, 124),
					this.element(ElementEnum.Rf, 107),
					this.element(ElementEnum.Db, 104),
					this.element(ElementEnum.Sg, 93),
					this.element(ElementEnum.Bh, 81),
					this.element(ElementEnum.Hs, 67),
					this.element(ElementEnum.Mt, 54),
					this.element(ElementEnum.Ds, 52),
					this.element(ElementEnum.Rg, 37),
					this.element(ElementEnum.Cn, 33),
					this.element(ElementEnum.Uut, 22)
			}));

			RecipeDecomposer.add(new RecipeDecomposerSelect(skystone, 0.9F, new RecipeDecomposer[] {
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Si),
							this.element(ElementEnum.He)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Fe),
							this.element(ElementEnum.Xe)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Mg),
							this.element(ElementEnum.Ar)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Ti),
							this.element(ElementEnum.He)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Pb),
							this.element(ElementEnum.Ne)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Zn),
							this.element(ElementEnum.He)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Al),
							this.element(ElementEnum.He)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Si),
							this.element(ElementEnum.Xe)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Fe),
							this.element(ElementEnum.Ar)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Mg),
							this.element(ElementEnum.Kr)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Ti),
							this.element(ElementEnum.Ne)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Pb),
							this.element(ElementEnum.Rn)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Zn),
							this.element(ElementEnum.Ne)
					}),
					new RecipeDecomposer(new PotionChemical[] {
							this.element(ElementEnum.Al),
							this.element(ElementEnum.Ar)
					})
			}));

			RecipeDecomposer.add(new RecipeDecomposer(quartzglass, quartzGlassDecompositionFormula));
			RecipeHandlerSynthesis.addShapedRecipe("quartz_glass", 30000, quartzglass, "aba", "bab", "aba", 'a', certusQuartzMolecule, 'b', molecule(MoleculeEnum.siliconDioxide, 4));
		}

		//RailCraft
		if (Loader.isModLoaded("Railcraft")) {
			Block metalPost = Block.REGISTRY.getObject(new ResourceLocation("Railcraft", "tile.railcraft.post.metal"));
			Block metalPlatform = Block.REGISTRY.getObject(new ResourceLocation("Railcraft", "tile.railcraft.post.metal.platform"));
			Block post = Block.REGISTRY.getObject(new ResourceLocation("Railcraft", "tile.railcraft.post"));
			RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(metalPost), this.element(ElementEnum.Fe, 5)));
			RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(metalPlatform), this.element(ElementEnum.Fe, 5)));
			RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(post), new PotionChemical[] {}));
		}
	}

	public void RegisterRecipes() {
		registerVanillaChemicalRecipes();

		ItemStack blockGlass = new ItemStack(Blocks.GLASS);
		ItemStack blockThinGlass = new ItemStack(Blocks.GLASS_PANE);
		ItemStack blockIron = new ItemStack(Blocks.IRON_BLOCK);
		ItemStack itemIngotIron = new ItemStack(Items.IRON_INGOT);
		ItemStack itemRedstone = new ItemStack(Items.REDSTONE);
		ItemStack minechemItemsAtomicManipulator = new ItemStack(ModItems.atomicManipulator);
		//ItemStack moleculePolyvinylChloride = new ItemStack(MinechemItemsRegistration.molecule, 1, MoleculeEnum.polyvinylChloride.id());

		ModLogger.debug("------------------------");
		ModLogger.debug(ModBlocks.decomposer);
		ModLogger.debug(ModBlocks.decomposer.delegate);

		if (ModBlocks.decomposer.delegate != null) {
			ModLogger.debug(ModBlocks.decomposer.delegate.get());
		}

		ModLogger.debug(new ItemStack(ModBlocks.decomposer).getItem());
		ModLogger.debug("------------------------");

		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "concave_lens"), null, ModItems.concaveLens, new Object[] {
				"G G",
				"GGG",
				"G G",
				Character.valueOf('G'),
				blockGlass
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "convex_lens"), null, ModItems.convexLens, new Object[] {
				" G ",
				"GGG",
				" G ",
				Character.valueOf('G'),
				blockGlass
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "microscope_lens"), null, ModItems.microscopeLens, new Object[] {
				"A",
				"B",
				"A",
				Character.valueOf('A'),
				ModItems.convexLens,
				Character.valueOf('B'),
				ModItems.concaveLens
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "microscope"), null, new ItemStack(ModBlocks.microscope), new Object[] {
				" LI",
				" PI",
				"III",
				Character.valueOf('L'),
				ModItems.microscopeLens,
				Character.valueOf('P'),
				blockThinGlass,
				Character.valueOf('I'),
				itemIngotIron
		});

		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "atomic_manipulator"), null, new ItemStack(ModItems.atomicManipulator), new Object[] {
				"PPP",
				"PIP",
				"PPP",
				Character.valueOf('P'),
				new ItemStack(Blocks.PISTON),
				Character.valueOf('I'),
				blockIron
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "decomposer"), null, new ItemStack(ModBlocks.decomposer), new Object[] {
				"III",
				"IAI",
				"IRI",
				Character.valueOf('A'),
				minechemItemsAtomicManipulator,
				Character.valueOf('I'),
				itemIngotIron,
				Character.valueOf('R'),
				itemRedstone
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "synthesis"), null, new ItemStack(ModBlocks.synthesis), new Object[] {
				"IRI",
				"IAI",
				"IDI",
				Character.valueOf('A'),
				minechemItemsAtomicManipulator,
				Character.valueOf('I'),
				itemIngotIron,
				Character.valueOf('R'),
				itemRedstone,
				Character.valueOf('D'),
				new ItemStack(Items.DIAMOND)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "fusion_1"), null, new ItemStack(ModBlocks.FUSION, 16, 0), new Object[] {
				"ILI",
				"ILI",
				"ILI",
				Character.valueOf('I'),
				itemIngotIron,
				Character.valueOf('L'),
				ItemElement.createStackOf(ElementEnum.Pb, 1)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "fusion_2"), null, new ItemStack(ModBlocks.FUSION, 16, 1), new Object[] {
				"IWI",
				"IBI",
				"IWI",
				Character.valueOf('I'),
				itemIngotIron,
				Character.valueOf('W'),
				ItemElement.createStackOf(ElementEnum.W, 1),
				Character.valueOf('B'),
				ItemElement.createStackOf(ElementEnum.Be, 1)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "projector_lens"), null, ModItems.projectorLens, new Object[] {
				"ABA",
				Character.valueOf('A'),
				ModItems.concaveLens,
				Character.valueOf('B'),
				ModItems.convexLens
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "blueprint_projector"), null, new ItemStack(ModBlocks.blueprintProjector), new Object[] {
				" I ",
				"GPL",
				" I ",
				Character.valueOf('I'),
				itemIngotIron,
				Character.valueOf('P'),
				blockThinGlass,
				Character.valueOf('L'),
				ModItems.projectorLens,
				Character.valueOf('G'),
				new ItemStack(Blocks.REDSTONE_LAMP)
		});
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "leaded_chest"), null, new ItemStack(ModBlocks.leadChest), new Object[] {
				"LLL",
				"LCL",
				"LLL",
				Character.valueOf('L'),
				new ItemStack(ModItems.element, 1, ElementEnum.Pb.atomicNumber()),
				Character.valueOf('C'),
				new ItemStack(Blocks.CHEST)
		});
		GameRegistry.addShapelessRecipe(new ResourceLocation(ModGlobals.ID, "journal"), null, new ItemStack(ModItems.journal), Ingredient.fromItem(Items.BOOK), Ingredient.fromItem(Item.getItemFromBlock(Blocks.GLASS)));
		// Fusion
		GameRegistry.addShapelessRecipe(new ResourceLocation(ModGlobals.ID, "blueprint_fusion"), null, ItemBlueprint.createItemStackFromBlueprint(MinechemBlueprint.fusion), Ingredient.fromStacks(new ItemStack(Items.PAPER), new ItemStack(Blocks.DIAMOND_BLOCK)));
		// Fission
		GameRegistry.addShapelessRecipe(new ResourceLocation(ModGlobals.ID, "blueprint_fission"), null, ItemBlueprint.createItemStackFromBlueprint(MinechemBlueprint.fission), Ingredient.fromStacks(new ItemStack(Items.PAPER), new ItemStack(Items.DIAMOND)));
		GameRegistry.addShapedRecipe(new ResourceLocation(ModGlobals.ID, "empty_tube"), null, ModItems.emptyTube, new Object[] {
				"   ",
				"P P",
				" P ",
				Character.valueOf('P'),
				blockThinGlass
		});

		//GameRegistry.addRecipe(new ChemistJournalRecipeCloning());
		ForgeRegistries.RECIPES.register(new RecipeCloneChemistJournal());

		addDecomposerRecipesFromMolecules();
		addSynthesisRecipesFromMolecules();
		addUnusedSynthesisRecipes();
	}

	private void addDecomposerRecipesFromMolecules() {
		for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
			if (molecule != null) {
				ItemStack itemStack = new ItemStack(ModItems.molecule, 1, molecule.id());
				RecipeDecomposer.add(new RecipeDecomposer(itemStack, molecule.components()));
			}
		}

	}

	private void addSynthesisRecipesFromMolecules() {
		for (MoleculeEnum molecule : MoleculeEnum.molecules.values()) {
			if (molecule != null) {
				ItemStack moleculeItemStack = new ItemStack(ModItems.molecule, 1, molecule.id());
				RecipeHandlerSynthesis.addShapelessRecipe("molecule_" + molecule.name().toLowerCase(Locale.US), COST_ITEM, moleculeItemStack, molecule.components());
			}
		}

	}

	private void addUnusedSynthesisRecipes() {
		Iterator<RecipeDecomposer> decomposerRecipes = RecipeDecomposer.recipes.values().iterator();

		while (decomposerRecipes.hasNext()) {
			RecipeDecomposer nextDecomposerRecipe = decomposerRecipes.next();
			if (nextDecomposerRecipe.getInput().getItemDamage() != -1) {
				boolean check = false;
				Iterator<RecipeSynthesisOld> synthesisRecipes = RecipeSynthesisOld.recipes.values().iterator();

				while (true) {
					if (synthesisRecipes.hasNext()) {
						RecipeSynthesisOld nextSynthesisRecipe = synthesisRecipes.next();
						if (!MinechemUtil.stacksAreSameKind(nextSynthesisRecipe.getOutput(), nextDecomposerRecipe.getInput())) {
							continue;
						}

						check = true;
					}

					if (!check) {
						ArrayList<PotionChemical> rawDecomposerRecipe = nextDecomposerRecipe.getOutputRaw();
						if (rawDecomposerRecipe != null) {
							if (shouldCreateSynthesis(nextDecomposerRecipe.getInput())) {
								RecipeHandlerSynthesis.addShapelessRecipe(nextDecomposerRecipe.getInput().getUnlocalizedName(), 100, nextDecomposerRecipe.getInput(), rawDecomposerRecipe);
							}
						}
					}
					break;
				}
			}
		}
	}

	public boolean shouldCreateSynthesis(ItemStack item) {
		if (item.getItem() == Items.STICK) {
			return false;
		}
		if (item.getItem() instanceof ItemBlock) {
			return shouldCreateSynthesis((ItemBlock) item.getItem());
		}
		return true;
	}

	public boolean shouldCreateSynthesis(ItemBlock block) {
		// check if the block is an oreBlock
		return !(block.getBlock() instanceof BlockOre);
	}

	private ArrayList<IOreDictionaryHandler> oreDictionaryHandlers = Lists.newArrayList();

	public static ArrayList<IOreDictionaryHandler> getOreDictionaryHandlers() {
		if (getInstance().oreDictionaryHandlers == null) {
			getInstance().registerOreDictOres();
		}
		return getInstance().oreDictionaryHandlers;
	}

	public void registerOreDictOres() {
		oreDictionaryHandlers.add(new OreDictionaryDefaultHandler());
		if (Loader.isModLoaded("appliedenergistics2")) {
			oreDictionaryHandlers.add(new OreDictionaryAppliedEnergisticsHandler());
		}
		for (String oreName : OreDictionary.getOreNames()) {
			registerOre(oreName);
		}
	}

	public void registerOre(String oreName) {
		if (oreName.contains("gemApatite")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Ca, 5),
					this.molecule(MoleculeEnum.phosphate, 4),
					this.element(ElementEnum.Cl)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.element(ElementEnum.Ca, 5),
					this.molecule(MoleculeEnum.phosphate, 4),
					this.element(ElementEnum.Cl)
			});
		}
		else if (oreName.contains("plateSilicon")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Si, 2)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.element(ElementEnum.Si, 2)
			});
		}
		else if (oreName.contains("xychoriumBlue")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Cu, 1)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Cu, 1)
			});
		}
		else if (oreName.contains("xychoriumRed")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Fe, 1)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Fe, 1)
			});
		}
		else if (oreName.contains("xychoriumGreen")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.V, 1)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.V, 1)
			});
		}
		else if (oreName.contains("xychoriumDark")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Si, 1)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Si, 1)
			});
		}
		else if (oreName.contains("xychoriumLight")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Ti, 1)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Ti, 1)
			});
		}
		else if (oreName.contains("gemPeridot")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.olivine)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.olivine)
			});
		}
		else if (oreName.contains("cropMaloberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.xylitol),
					this.molecule(MoleculeEnum.sucrose)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.xylitol),
					this.molecule(MoleculeEnum.sucrose)
			});
		}
		else if (oreName.contains("cropDuskberry")) {
			if (ModConfig.recreationalChemicalEffects) {
				RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
						this.molecule(MoleculeEnum.psilocybin),
						this.element(ElementEnum.S, 2)
				});
				RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
						this.molecule(MoleculeEnum.psilocybin),
						this.element(ElementEnum.S, 2)
				});
			}
		}
		else if (oreName.contains("cropSkyberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.theobromine),
					this.element(ElementEnum.S, 2)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.theobromine),
					this.element(ElementEnum.S, 2)
			});
		}
		else if (oreName.contains("cropBlightberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.asprin),
					this.element(ElementEnum.S, 2)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.asprin),
					this.element(ElementEnum.S, 2)
			});
		}
		else if (oreName.contains("cropBlueberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.blueorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.blueorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
		}
		else if (oreName.contains("cropRaspberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.redorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.redorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
		}
		else if (oreName.contains("cropBlackberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.purpleorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
			RecipeHandlerSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.purpleorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
		}
		else {
			for (IOreDictionaryHandler handler : oreDictionaryHandlers) {
				if (handler.canHandle(oreName)) {
					handler.handle(oreName);
					return;
				}
			}
		}
	}

	// END
	// BEGIN MISC FUNCTIONS
	private Element element(ElementEnum var1, int var2) {
		return new Element(var1, var2);
	}

	private Element element(ElementEnum var1) {
		return new Element(var1, 1);
	}

	private Molecule molecule(MoleculeEnum var1, int var2) {
		return new Molecule(var1, var2);
	}

	private Molecule molecule(MoleculeEnum var1) {
		return new Molecule(var1, 1);
	}

	public static void registerCustomRecipes(IForgeRegistry<IRecipe> registry) {
		registry.register(new RecipePotionCoating().setRegistryName(new ResourceLocation(ModGlobals.ID, "potion_coating")));
		registry.register(new RecipePotionSpiking().setRegistryName(new ResourceLocation(ModGlobals.ID, "potion_spiking")));
	}

}
