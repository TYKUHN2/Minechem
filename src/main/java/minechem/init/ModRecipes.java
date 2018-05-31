package minechem.init;

import java.util.ArrayList;
import java.util.Iterator;

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
import minechem.potion.PotionCoatingRecipe;
import minechem.recipe.RecipeCloneChemistJournal;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeDecomposerChance;
import minechem.recipe.RecipeDecomposerFluid;
import minechem.recipe.RecipeDecomposerFluidSelect;
import minechem.recipe.RecipeDecomposerSelect;
import minechem.recipe.RecipeDecomposerSuper;
import minechem.recipe.RecipePotionSpiking;
import minechem.recipe.RecipeSynthesis;
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
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.STONE, 16), true, COST_SMOOTH, new PotionChemical[] {
				this.element(ElementEnum.Si),
				this.element(ElementEnum.O, 2),
				null,
				this.element(ElementEnum.Al, 2),
				this.element(ElementEnum.O, 3),
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.GRASS, 16), true, COST_GRASS, new PotionChemical[] {
				null,
				moleculeCellulose,
				null,
				null,
				this.element(ElementEnum.O, 2),
				this.element(ElementEnum.Si)
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.DIRT, 1, 0), true, COST_BLOCK, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.DIRT, 1, 2), true, COST_BLOCK, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.COBBLESTONE, 16), true, COST_SMOOTH, new PotionChemical[] {
				this.element(ElementEnum.Si, 2),
				this.element(ElementEnum.O, 4),
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockOakWoodPlanks, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockSpruceWoodPlanks, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				this.molecule(MoleculeEnum.cellulose),
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBirchWoodPlanks, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				this.molecule(MoleculeEnum.cellulose),
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockJungleWoodPlanks, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockAcaciaWoodPlanks, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockDarkOakWoodPlanks, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null,
				null,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockOakWoodSlabs, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockSpruceWoodSlabs, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose),
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBirchWoodSlabs, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockJungleWoodSlabs, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockAcaciaWoodSlabs, true, COST_PLANK, new PotionChemical[] {
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockDarkOakWoodSlabs, true, COST_PLANK, new PotionChemical[] {
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null,
				null,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockOakSapling, true, COST_PLANT, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockSpruceSapling, true, COST_PLANT, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBirchSapling, true, COST_PLANT, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockJungleSapling, true, COST_PLANT, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockAcaciaSapling, true, COST_PLANT, new PotionChemical[] {
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockDarkOakSapling, true, COST_PLANT, new PotionChemical[] {
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				null,
				null,
				null
		}));

		// Sand
		ItemStack blockSand = new ItemStack(Blocks.SAND);
		RecipeDecomposer.add(new RecipeDecomposer(blockSand, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockSand, true, COST_BLOCK, new PotionChemical[] {
				moleculeSiliconDioxide,
				moleculeSiliconDioxide,
				moleculeSiliconDioxide,
				moleculeSiliconDioxide
		}));

		// Gravel
		ItemStack blockGravel = new ItemStack(Blocks.GRAVEL);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockGravel, 0.35F, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockGravel, true, COST_BLOCK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.siliconDioxide)
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockOakLog, true, COST_WOOD, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2),
				this.molecule(MoleculeEnum.cellulose, 2),
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockSpruceLog, true, COST_WOOD, new PotionChemical[] {
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				this.molecule(MoleculeEnum.cellulose, 2),
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBirchLog, true, COST_WOOD, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				null,
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockJungleLog, true, COST_WOOD, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockAcaciaLog, true, COST_WOOD, new PotionChemical[] {
				null,
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				null,
				this.molecule(MoleculeEnum.cellulose, 2)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockDarkOakLog, true, COST_WOOD, new PotionChemical[] {
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null,
				this.molecule(MoleculeEnum.cellulose, 2),
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockOakLeaves, true, COST_BLOCK, new PotionChemical[] {
				moleculeCellulose,
				moleculeCellulose,
				moleculeCellulose,
				null,
				moleculeCellulose,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockSpruceLeaves, true, COST_BLOCK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				moleculeCellulose,
				null,
				moleculeCellulose,
				moleculeCellulose,
				moleculeCellulose
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBirchLeaves, true, COST_BLOCK, new PotionChemical[] {
				moleculeCellulose,
				null,
				moleculeCellulose,
				null,
				null,
				null,
				moleculeCellulose,
				null,
				moleculeCellulose
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockJungleLeaves, true, COST_BLOCK, new PotionChemical[] {
				moleculeCellulose,
				null,
				null,
				moleculeCellulose,
				moleculeCellulose,
				null,
				moleculeCellulose,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockAcaciaLeaves, true, COST_BLOCK, new PotionChemical[] {
				null,
				null,
				moleculeCellulose,
				null,
				moleculeCellulose,
				moleculeCellulose,
				null,
				null,
				moleculeCellulose
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockDarkOakLeaves, true, COST_BLOCK, new PotionChemical[] {
				null,
				moleculeCellulose,
				null,
				moleculeCellulose,
				null,
				moleculeCellulose,
				null,
				moleculeCellulose,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderBlack, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderRed, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderGreen, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderBrown, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.theobromine)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderBlue, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderPurple, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderCyan, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderLightGray, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderGray, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderPink, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderLime, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderYellow, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderLightBlue, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderMagenta, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderOrange, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDyePowderWhite, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.whitePigment)
		}));

		// Glass
		ItemStack blockGlass = new ItemStack(Blocks.GLASS);
		ItemStack blockWhiteStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 0);
		ItemStack blockOrangeStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 1);
		ItemStack blockMagentaStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 2);
		ItemStack blockLiteBlueStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 3);
		ItemStack blockYellowStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 4);
		ItemStack blockLimeStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 5);
		ItemStack blockPinkStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 6);
		ItemStack blockGrayStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 7);
		ItemStack blockLiteGrayStainedGlass = new ItemStack(Blocks.STAINED_GLASS, 1, 8);
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

		RecipeSynthesis.add(new RecipeSynthesis(blockGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				null,
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockWhiteStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.whitePigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockOrangeStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.orangePigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockMagentaStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				this.molecule(MoleculeEnum.lightbluePigment),
				null,
				this.molecule(MoleculeEnum.redPigment),
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLiteBlueStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.lightbluePigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockYellowStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.yellowPigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLimeStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.limePigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockPinkStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				this.molecule(MoleculeEnum.whitePigment),
				null,
				this.molecule(MoleculeEnum.redPigment),
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockGrayStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				this.molecule(MoleculeEnum.whitePigment),
				null,
				this.molecule(MoleculeEnum.blackPigment),
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLiteGrayStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment),
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockCyanStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				this.molecule(MoleculeEnum.whitePigment),
				null,
				this.molecule(MoleculeEnum.lightbluePigment),
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockPurpleStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.purplePigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBlueStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.lazurite),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBrownStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.tannicacid),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockGreenStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.greenPigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockRedStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.redPigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBlackStainedGlass, true, COST_GLASS, new PotionChemical[] {
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				null,
				this.molecule(MoleculeEnum.blackPigment),
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide
		}));

		// Glass Panes
		ItemStack blockGlassPane = new ItemStack(Blocks.GLASS_PANE);
		ItemStack blockWhiteStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 0);
		ItemStack blockOrangeStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 1);
		ItemStack blockMagentaStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 2);
		ItemStack blockLiteBlueStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 3);
		ItemStack blockYellowStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 4);
		ItemStack blockLimeStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 5);
		ItemStack blockPinkStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 6);
		ItemStack blockGrayStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 7);
		ItemStack blockLiteGrayStainedGlassPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 8);
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
		RecipeSynthesis.add(new RecipeSynthesis(blockGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				null,
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockWhiteStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.whitePigment),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockOrangeStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.orangePigment),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockMagentaStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				this.molecule(MoleculeEnum.lightbluePigment),
				null,
				this.molecule(MoleculeEnum.redPigment),
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLiteBlueStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.lightbluePigment),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockYellowStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.yellowPigment),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLimeStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.limePigment),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockPinkStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				this.molecule(MoleculeEnum.whitePigment),
				null,
				this.molecule(MoleculeEnum.redPigment),
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockGrayStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				this.molecule(MoleculeEnum.whitePigment),
				null,
				this.molecule(MoleculeEnum.blackPigment),
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLiteGrayStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment),
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockCyanStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				this.molecule(MoleculeEnum.whitePigment),
				null,
				this.molecule(MoleculeEnum.lightbluePigment),
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockPurpleStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.purplePigment),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBlueStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.lazurite),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBrownStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.tannicacid),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockGreenStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.greenPigment),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockRedStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.redPigment),
				null,
				siO,
				siO,
				siO
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBlackStainedGlassPane, true, COST_PANE, new PotionChemical[] {
				siO,
				siO,
				siO,
				null,
				this.molecule(MoleculeEnum.blackPigment),
				null,
				siO,
				siO,
				siO
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockLapis, true, COST_LAPISBLOCK, new PotionChemical[] {
				moleculeLazurite,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockSunFlower, true, COST_PLANT, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.yellowPigment),
				null,
				null,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLilac, true, COST_PLANT, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.redPigment),
				new Molecule(MoleculeEnum.whitePigment, 2),
				null,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockTallGrass, true, COST_PLANT, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLargeFern, true, COST_PLANT, new PotionChemical[] {
				null,
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				null,
				null,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockRoseBush, true, COST_PLANT, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.redPigment),
				null,
				null,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockPeony, true, COST_PLANT, new PotionChemical[] {
				new Molecule(MoleculeEnum.shikimicAcid, 2),
				new Molecule(MoleculeEnum.redPigment),
				new Molecule(MoleculeEnum.whitePigment),
				null,
				null,
				null,
				null,
				null,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockSandStone, true, COST_BLOCK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.siliconDioxide, 16),
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockChiseledSandStone, true, COST_BLOCK, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.siliconDioxide, 16),
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockSmoothSandStone, true, COST_BLOCK, new PotionChemical[] {
				null,
				this.molecule(MoleculeEnum.siliconDioxide, 16),
				null,
				null,
				null,
				null,
				null,
				null,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockOrangeWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockMagentaWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLightBlueWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockYellowWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLimeWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockPinkWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockGrayWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockLightGrayWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockCyanWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockPurpleWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBlueWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockGreenWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockRedWool, false, COST_WOOL, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine, 2),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockBlackWool, false, COST_WOOL, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockOrangeWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.orangePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockMagentaWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockLightBlueWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockYellowWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.yellowPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockLimeWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.limePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockPinkWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.redPigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockGrayWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment, 2)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockLightGrayWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.whitePigment),
				this.molecule(MoleculeEnum.blackPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockCyanWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lightbluePigment),
				this.molecule(MoleculeEnum.whitePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockPurpleWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.purplePigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockBlueWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.lazurite)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockGreenWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.greenPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockRedWool, false, COST_CARPET, new PotionChemical[] {
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.redPigment)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(carpetBlockBlackWool, false, COST_CARPET, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.GOLD_BLOCK), true, COST_METALBLOCK, new PotionChemical[] {
				this.element(ElementEnum.Au, 16),
				this.element(ElementEnum.Au, 16),
				this.element(ElementEnum.Au, 16),
				this.element(ElementEnum.Au, 16),
				this.element(ElementEnum.Au, 16),
				this.element(ElementEnum.Au, 16),
				this.element(ElementEnum.Au, 16),
				this.element(ElementEnum.Au, 16),
				this.element(ElementEnum.Au, 16)
		}));

		// Block of Iron
		RecipeDecomposer.add(new RecipeDecomposer(new ItemStack(Blocks.IRON_BLOCK), new PotionChemical[] {
				this.element(ElementEnum.Fe, 144)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.IRON_BLOCK), true, COST_METALBLOCK, new PotionChemical[] {
				this.element(ElementEnum.Fe, 16),
				this.element(ElementEnum.Fe, 16),
				this.element(ElementEnum.Fe, 16),
				this.element(ElementEnum.Fe, 16),
				this.element(ElementEnum.Fe, 16),
				this.element(ElementEnum.Fe, 16),
				this.element(ElementEnum.Fe, 16),
				this.element(ElementEnum.Fe, 16),
				this.element(ElementEnum.Fe, 16)
		}));

		// TNT
		ItemStack blockTnt = new ItemStack(Blocks.TNT);
		RecipeDecomposer.add(new RecipeDecomposer(blockTnt, new PotionChemical[] {
				this.molecule(MoleculeEnum.tnt)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockTnt, false, COST_OBSIDIAN, new PotionChemical[] {
				this.molecule(MoleculeEnum.tnt)
		}));

		// Obsidian
		ItemStack blockObsidian = new ItemStack(Blocks.OBSIDIAN);
		RecipeDecomposer.add(new RecipeDecomposer(blockObsidian, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 16),
				this.molecule(MoleculeEnum.magnesiumOxide, 8)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockObsidian, true, COST_OBSIDIAN, new PotionChemical[] {
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				this.molecule(MoleculeEnum.magnesiumOxide, 2),
				null,
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				this.molecule(MoleculeEnum.magnesiumOxide, 2),
				this.molecule(MoleculeEnum.magnesiumOxide, 2),
				this.molecule(MoleculeEnum.magnesiumOxide, 2)
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockDiamond, true, COST_GEMBLOCK, new PotionChemical[] {
				this.molecule(MoleculeEnum.fullrene, 3),
				this.molecule(MoleculeEnum.fullrene, 3),
				this.molecule(MoleculeEnum.fullrene, 3),
				this.molecule(MoleculeEnum.fullrene, 3),
				this.molecule(MoleculeEnum.fullrene, 3),
				this.molecule(MoleculeEnum.fullrene, 3),
				this.molecule(MoleculeEnum.fullrene, 3),
				this.molecule(MoleculeEnum.fullrene, 3),
				this.molecule(MoleculeEnum.fullrene, 3)
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockCactus, true, COST_PLANT, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 5),
				null,
				this.molecule(MoleculeEnum.water, 5),
				null,
				this.molecule(MoleculeEnum.mescaline),
				null,
				this.molecule(MoleculeEnum.water, 5),
				null,
				this.molecule(MoleculeEnum.water, 5)
		}));

		// Pumpkin
		ItemStack blockPumpkin = new ItemStack(Blocks.PUMPKIN);
		RecipeDecomposer.add(new RecipeDecomposer(blockPumpkin, new PotionChemical[] {
				this.molecule(MoleculeEnum.cucurbitacin)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockPumpkin, false, COST_PLANT, new PotionChemical[] {
				this.molecule(MoleculeEnum.cucurbitacin)
		}));

		// Pumpkin seed
		ItemStack pumpkinSeed = new ItemStack(Items.PUMPKIN_SEEDS);
		RecipeDecomposer.add(new RecipeDecomposer(pumpkinSeed, new PotionChemical[] {
				this.molecule(MoleculeEnum.water)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(pumpkinSeed, false, COST_PLANT, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(itemNetherbrick, true, COST_SMOOTH, new PotionChemical[] {
				this.element(ElementEnum.Si, 2),
				this.element(ElementEnum.Si, 2),
				null,
				this.element(ElementEnum.Zn, 2),
				this.element(ElementEnum.W, 1),
				null,
				this.element(ElementEnum.Be, 2),
				this.element(ElementEnum.Be, 2),
				null
		}));

		// Water Bottle
		ItemStack itemPotion = new ItemStack(Items.POTIONITEM, 1, 0);
		RecipeDecomposer.add(new RecipeDecomposer(itemPotion, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 5),
				this.molecule(MoleculeEnum.siliconDioxide, 16)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemPotion, true, COST_ITEM, new PotionChemical[] {
				null,
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				null,
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				this.molecule(MoleculeEnum.water, 5),
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				null,
				this.molecule(MoleculeEnum.siliconDioxide, 4),
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(blockGlowStone, true, COST_GLOWBLOCK, new PotionChemical[] {
				this.element(ElementEnum.P),
				null,
				this.element(ElementEnum.P),
				this.element(ElementEnum.P),
				null,
				this.element(ElementEnum.P),
				null,
				null,
				null
		}));

		// Mycelium
		ItemStack blockMycelium = new ItemStack(Blocks.MYCELIUM);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockMycelium, 0.09F, new PotionChemical[] {
				this.molecule(MoleculeEnum.fingolimod)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.MYCELIUM, 16), false, COST_GRASS, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(blockEmerald, true, COST_GEMBLOCK, new PotionChemical[] {
				this.element(ElementEnum.Cr, 3),
				this.element(ElementEnum.Cr, 3),
				this.element(ElementEnum.Cr, 3),
				this.element(ElementEnum.V, 9),
				this.molecule(MoleculeEnum.beryl, 18),
				this.element(ElementEnum.V, 9),
				this.element(ElementEnum.Cr, 3),
				this.element(ElementEnum.Cr, 3),
				this.element(ElementEnum.Cr, 3)
		}));
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
		RecipeSynthesis.add(new RecipeSynthesis(itemAppleRed, false, COST_FOOD, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(itemChar, false, COST_ITEM, new PotionChemical[] {
				this.element(ElementEnum.C, 4),
				this.element(ElementEnum.C, 4)
		}));
		// Diamond
		ItemStack itemDiamond = new ItemStack(Items.DIAMOND);
		RecipeDecomposer.add(new RecipeDecomposer(itemDiamond, new PotionChemical[] {
				this.molecule(MoleculeEnum.fullrene, 3)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDiamond, true, COST_GEM, new PotionChemical[] {
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.fullrene, 3),
				null,
				null,
				null,
				null
		}));

		// Polytool
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(ModItems.polytool), true, COST_STAR, new PotionChemical[]

		{
				null,
				this.molecule(MoleculeEnum.fullrene, 64),
				null,
				this.molecule(MoleculeEnum.fullrene, 64),
				null,
				this.molecule(MoleculeEnum.fullrene, 64),
				null,
				this.molecule(MoleculeEnum.fullrene, 64),
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemDiamond, true, COST_GEM, new PotionChemical[] {
				null,
				this.molecule(MoleculeEnum.fullrene),
				null,
				this.molecule(MoleculeEnum.fullrene),
				null,
				this.molecule(MoleculeEnum.fullrene),
				null,
				this.molecule(MoleculeEnum.fullrene),
				null
		}));

		// Iron Ingot
		ItemStack itemIngotIron = new ItemStack(Items.IRON_INGOT);
		RecipeDecomposer.add(new RecipeDecomposer(itemIngotIron, new PotionChemical[] {
				this.element(ElementEnum.Fe, 16)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemIngotIron, false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 16)
		}));

		// Gold Ingot
		ItemStack itemIngotGold = new ItemStack(Items.GOLD_INGOT);
		RecipeDecomposer.add(new RecipeDecomposer(itemIngotGold, new PotionChemical[] {
				this.element(ElementEnum.Au, 16)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemIngotGold, false, COST_INGOT, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(itemString, true, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.serine),
				this.molecule(MoleculeEnum.glycine),
				this.molecule(MoleculeEnum.alinine)
		}));

		// Feather
		ItemStack itemFeather = new ItemStack(Items.FEATHER);
		RecipeDecomposer.add(new RecipeDecomposer(itemFeather, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 8),
				this.element(ElementEnum.N, 6)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemFeather, true, COST_ITEM, new PotionChemical[] {
				this.element(ElementEnum.N),
				this.molecule(MoleculeEnum.water, 2),
				this.element(ElementEnum.N),
				this.element(ElementEnum.N),
				this.molecule(MoleculeEnum.water, 1),
				this.element(ElementEnum.N),
				this.element(ElementEnum.N),
				this.molecule(MoleculeEnum.water, 5),
				this.element(ElementEnum.N)
		}));

		// Gunpowder
		ItemStack itemGunpowder = new ItemStack(Items.GUNPOWDER);
		RecipeDecomposer.add(new RecipeDecomposer(itemGunpowder, new PotionChemical[] {
				this.molecule(MoleculeEnum.potassiumNitrate),
				this.element(ElementEnum.S, 2),
				this.element(ElementEnum.C)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemGunpowder, true, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.potassiumNitrate),
				this.element(ElementEnum.C),
				null,
				this.element(ElementEnum.S, 2),
				null,
				null,
				null,
				null,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemFlint, true, COST_ITEM, new PotionChemical[] {
				null,
				moleculeSiliconDioxide,
				null,
				moleculeSiliconDioxide,
				moleculeSiliconDioxide,
				moleculeSiliconDioxide,
				null,
				null,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemBucket, true, COST_FOOD, new PotionChemical[] {
				null,
				null,
				null,
				this.element(ElementEnum.Fe, 16),
				null,
				this.element(ElementEnum.Fe, 16),
				null,
				this.element(ElementEnum.Fe, 16),
				null
		}));

		// Water Bucket
		ItemStack itemBucketWater = new ItemStack(Items.WATER_BUCKET);
		RecipeDecomposer.add(new RecipeDecomposer(itemBucketWater, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 16),
				this.element(ElementEnum.Fe, 48)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemBucketWater, true, COST_FOOD, new PotionChemical[] {
				null,
				null,
				null,
				this.element(ElementEnum.Fe, 16),
				this.molecule(MoleculeEnum.water, 16),
				this.element(ElementEnum.Fe, 16),
				null,
				this.element(ElementEnum.Fe, 16),
				null
		}));

		// Redstone
		ItemStack itemRedstone = new ItemStack(Items.REDSTONE);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemRedstone, 0.42F, new PotionChemical[] {
				this.molecule(MoleculeEnum.iron3oxide),
				this.element(ElementEnum.Cu)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRedstone, true, COST_LAPIS, new PotionChemical[] {
				null,
				null,
				this.molecule(MoleculeEnum.iron3oxide),
				null,
				this.element(ElementEnum.Cu),
				null,
				null,
				null,
				null
		}));
		// Redstone Block
		ItemStack blockRedstone = new ItemStack(Blocks.REDSTONE_BLOCK);
		RecipeDecomposer.add(new RecipeDecomposerChance(blockRedstone, 0.42F, new PotionChemical[] {
				this.molecule(MoleculeEnum.iron3oxide, 9),
				this.element(ElementEnum.Cu, 9)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockRedstone, true, COST_LAPISBLOCK, new PotionChemical[] {
				null,
				null,
				this.molecule(MoleculeEnum.iron3oxide, 9),
				null,
				this.element(ElementEnum.Cu, 9),
				null,
				null,
				null,
				null
		}));

		// Snowball
		ItemStack itemSnowball = new ItemStack(Items.SNOWBALL);
		RecipeDecomposer.add(new RecipeDecomposer(itemSnowball, new PotionChemical[] {
				this.molecule(MoleculeEnum.water)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Items.SNOWBALL, 5), true, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.water),
				null,
				this.molecule(MoleculeEnum.water),
				null,
				this.molecule(MoleculeEnum.water),
				null,
				this.molecule(MoleculeEnum.water),
				null,
				this.molecule(MoleculeEnum.water)
		}));

		// Leather
		ItemStack itemLeather = new ItemStack(Items.LEATHER);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemLeather, 0.2F, new PotionChemical[] {
				this.molecule(MoleculeEnum.keratin)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Items.LEATHER, 5), true, COST_ITEM, new PotionChemical[] {
				null,
				null,
				null,
				null,
				this.molecule(MoleculeEnum.keratin),
				null,
				null,
				null,
				null
		}));

		// Brick
		ItemStack itemBrick = new ItemStack(Items.BRICK);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemBrick, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.kaolinite)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Items.BRICK, 8), true, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.kaolinite),
				this.molecule(MoleculeEnum.kaolinite),
				null,
				this.molecule(MoleculeEnum.kaolinite),
				this.molecule(MoleculeEnum.kaolinite),
				null
		}));

		// Clay
		ItemStack itemClayBall = new ItemStack(Items.CLAY_BALL);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemClayBall, 0.5F, new PotionChemical[] {
				this.molecule(MoleculeEnum.kaolinite)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Items.CLAY_BALL, 2), false, COST_ITEM, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(itemVine, true, COST_GRASS, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose),
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose),
				this.molecule(MoleculeEnum.cellulose),
				null,
				this.molecule(MoleculeEnum.cellulose)
		}));

		// Paper
		ItemStack itemPaper = new ItemStack(Items.PAPER);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemPaper, 0.35F, new PotionChemical[] {
				this.molecule(MoleculeEnum.cellulose)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Items.PAPER, 8), true, COST_ITEM, new PotionChemical[] {
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null,
				null,
				this.molecule(MoleculeEnum.cellulose),
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemBone, false, COST_ITEM, new PotionChemical[] {
				this.molecule(MoleculeEnum.hydroxylapatite)
		}));

		// Sugar
		ItemStack itemSugar = new ItemStack(Items.SUGAR);
		RecipeDecomposer.add(new RecipeDecomposerChance(itemSugar, 0.75F, new PotionChemical[] {
				this.molecule(MoleculeEnum.sucrose)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemSugar, false, COST_SUGAR, new PotionChemical[] {
				this.molecule(MoleculeEnum.sucrose)
		}));

		// Melon Slice
		ItemStack itemMelon = new ItemStack(Items.MELON);
		RecipeDecomposer.add(new RecipeDecomposer(itemMelon, new PotionChemical[] {
				this.molecule(MoleculeEnum.water, 1)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Items.MELON), false, COST_FOOD, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(new ItemStack(Blocks.MELON_BLOCK, 1), false, COST_FOOD, new PotionChemical[] {
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
		RecipeSynthesis.add(new RecipeSynthesis(itemChickenCooked, true, COST_FOOD, new PotionChemical[] {
				this.element(ElementEnum.K, 16),
				this.element(ElementEnum.Na, 16),
				this.element(ElementEnum.C, 16)
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemEnderPearl, true, COST_TEAR, new PotionChemical[] {
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.element(ElementEnum.Es),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate),
				this.molecule(MoleculeEnum.calciumCarbonate)
		}));

		// EnderDragon Egg
		ItemStack blockEnderDragonEgg = new ItemStack(Blocks.DRAGON_EGG);
		RecipeDecomposer.add(new RecipeDecomposer(blockEnderDragonEgg, new PotionChemical[] {
				this.molecule(MoleculeEnum.calciumCarbonate, 16),
				this.molecule(MoleculeEnum.hydroxylapatite, 6),
				this.element(ElementEnum.Pu, 18),
				this.element(ElementEnum.Fm, 8)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(blockEnderDragonEgg, true, COST_BLOCK * 2, new PotionChemical[] {
				this.molecule(MoleculeEnum.calciumCarbonate, 18),
				this.molecule(MoleculeEnum.hydroxylapatite, 8),
				this.element(ElementEnum.Pu, 22),
				this.element(ElementEnum.Fm, 12)
		}));

		// Blaze Rod
		ItemStack itemBlazeRod = new ItemStack(Items.BLAZE_ROD);
		RecipeDecomposer.add(new RecipeDecomposer(itemBlazeRod, new PotionChemical[] {
				this.element(ElementEnum.Pu, 6)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemBlazeRod, true, COST_TEAR, new PotionChemical[] {
				this.element(ElementEnum.Pu, 2),
				null,
				null,
				this.element(ElementEnum.Pu, 2),
				null,
				null,
				this.element(ElementEnum.Pu, 2),
				null,
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemGhastTear, true, COST_TEAR, new PotionChemical[] {
				this.element(ElementEnum.Yb),
				this.element(ElementEnum.Yb),
				this.element(ElementEnum.No),
				null,
				this.element(ElementEnum.Yb, 2),
				this.element(ElementEnum.No, 2),
				null,
				this.element(ElementEnum.No),
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemSpiderEye, true, COST_ITEM, new PotionChemical[] {
				this.element(ElementEnum.C),
				null,
				null,
				null,
				this.molecule(MoleculeEnum.tetrodotoxin),
				null,
				null,
				null,
				this.element(ElementEnum.C)
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemMagmaCream, true, COST_TEAR, new PotionChemical[] {
				null,
				this.element(ElementEnum.Pu),
				null,
				this.molecule(MoleculeEnum.pmma),
				this.element(ElementEnum.Hg),
				this.molecule(MoleculeEnum.pmma),
				null,
				this.molecule(MoleculeEnum.pmma),
				null,
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemEmerald, true, 5000, new PotionChemical[] {
				null,
				this.element(ElementEnum.Cr),
				null,
				this.element(ElementEnum.V),
				this.molecule(MoleculeEnum.beryl, 2),
				this.element(ElementEnum.V),
				null,
				this.element(ElementEnum.Cr),
				null
		}));

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
		RecipeSynthesis.add(new RecipeSynthesis(itemNetherStar, true, COST_STAR, new PotionChemical[] {
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

		RecipeSynthesis.add(new RecipeSynthesis(itemRecord13, true, COST_GEM, new PotionChemical[] {
				moleculePolyvinylChloride,
				moleculePolyvinylChloride,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordCat, true, COST_GEM, new PotionChemical[] {
				null,
				moleculePolyvinylChloride,
				moleculePolyvinylChloride,
				null,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordFar, true, COST_GEM, new PotionChemical[] {
				null,
				null,
				moleculePolyvinylChloride,
				moleculePolyvinylChloride,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordMall, true, COST_GEM, new PotionChemical[] {
				null,
				null,
				null,
				moleculePolyvinylChloride,
				moleculePolyvinylChloride,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordMellohi, true, COST_GEM, new PotionChemical[] {
				null,
				null,
				null,
				null,
				moleculePolyvinylChloride,
				moleculePolyvinylChloride,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordStal, true, COST_GEM, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				moleculePolyvinylChloride,
				moleculePolyvinylChloride,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordStrad, true, COST_GEM, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				moleculePolyvinylChloride,
				moleculePolyvinylChloride,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordWard, true, COST_GEM, new PotionChemical[] {
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				moleculePolyvinylChloride,
				moleculePolyvinylChloride
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordChirp, true, COST_GEM, new PotionChemical[] {
				moleculePolyvinylChloride,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				moleculePolyvinylChloride
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecord11, true, COST_GEM, new PotionChemical[] {
				moleculePolyvinylChloride,
				null,
				null,
				null,
				moleculePolyvinylChloride,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordWait, true, COST_GEM, new PotionChemical[] {
				moleculePolyvinylChloride,
				null,
				null,
				moleculePolyvinylChloride,
				null,
				null,
				null,
				null,
				null
		}));
		RecipeSynthesis.add(new RecipeSynthesis(itemRecordBlocks, true, COST_GEM, new PotionChemical[] {
				moleculePolyvinylChloride,
				null,
				null,
				null,
				null,
				moleculePolyvinylChloride,
				null,
				null,
				null
		}));

		//Ironbars
		ItemStack bars = new ItemStack(Blocks.IRON_BARS);
		RecipeDecomposer.add(new RecipeDecomposer(bars, new PotionChemical[] {
				element(ElementEnum.Fe, 3),
				element(ElementEnum.Fe, 3)
		}));
		RecipeSynthesis.add(new RecipeSynthesis(bars, false, COST_BLOCK, new PotionChemical[] {
				element(ElementEnum.Fe, 3),
				element(ElementEnum.Fe, 3)
		}));

		//Uranium Ore
		RecipeDecomposer.createAndAddRecipeSafely("oreUranium", new Element(ElementEnum.U, 48));
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

		RecipeSynthesis.createAndAddRecipeSafely("ingotIron", false, COST_INGOT, this.element(ElementEnum.Fe, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotGold", false, COST_INGOT, this.element(ElementEnum.Au, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotCopper", false, COST_INGOT, this.element(ElementEnum.Cu, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotTin", false, COST_INGOT, this.element(ElementEnum.Sn, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotSilver", false, COST_INGOT, this.element(ElementEnum.Ag, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotLead", false, COST_INGOT, this.element(ElementEnum.Pb, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotPlatinum", false, COST_INGOT, this.element(ElementEnum.Pt, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotAluminium", false, COST_INGOT, this.element(ElementEnum.Al, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotMagnesium", false, COST_INGOT, this.element(ElementEnum.Mg, 16));
		RecipeSynthesis.createAndAddRecipeSafely("ingotSteel", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 15),
				this.element(ElementEnum.C, 1)
		});
		RecipeSynthesis.createAndAddRecipeSafely("ingotHSLA", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Fe, 15),
				this.element(ElementEnum.C, 1)
		});
		RecipeSynthesis.createAndAddRecipeSafely("ingotBronze", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Cu, 12),
				this.element(ElementEnum.Sn, 4)
		});
		RecipeSynthesis.createAndAddRecipeSafely("ingotElectrum", false, COST_INGOT, new PotionChemical[] {
				this.element(ElementEnum.Ag, 8),
				this.element(ElementEnum.Au, 8)
		});
		RecipeSynthesis.createAndAddRecipeSafely("ingotInvar", false, COST_INGOT, new PotionChemical[] {
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
			RecipeSynthesis.add(new RecipeSynthesis(blockRockWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.whitePigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockOrangeWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.orangePigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockMagentaWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.lightbluePigment),
					this.molecule(MoleculeEnum.redPigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockLightBlueWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.lightbluePigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockYellowWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.yellowPigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockLimeWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.limePigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockPinkWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.redPigment),
					this.molecule(MoleculeEnum.whitePigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockGrayWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.whitePigment),
					this.molecule(MoleculeEnum.blackPigment, 2)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockLightGrayWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.whitePigment),
					this.molecule(MoleculeEnum.blackPigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockCyanWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.lightbluePigment),
					this.molecule(MoleculeEnum.whitePigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockPurpleWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.purplePigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockBlueWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.lazurite)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockGreenWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.greenPigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockRedWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.redPigment)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(blockRockBlackWool, false, COST_WOOL, new PotionChemical[] {
					this.molecule(MoleculeEnum.asbestos, 2),
					this.molecule(MoleculeEnum.blackPigment)
			}));

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

			RecipeSynthesis.add(new RecipeSynthesis(signalumBlend, false, COST_INGOT, new PotionChemical[] {
					this.element(ElementEnum.Cu, 12),
					this.element(ElementEnum.Ag, 4),
					this.molecule(MoleculeEnum.iron3oxide)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(signalumIngot, false, COST_INGOT, new PotionChemical[] {
					this.element(ElementEnum.Cu, 12),
					this.element(ElementEnum.Ag, 4),
					this.molecule(MoleculeEnum.iron3oxide)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(lumiumBlend, false, COST_INGOT, new PotionChemical[] {
					this.element(ElementEnum.Sn, 12),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.P)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(lumiumIngot, false, COST_INGOT, new PotionChemical[] {
					this.element(ElementEnum.Sn, 12),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.P)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(enderiumBlend, false, COST_INGOT, new PotionChemical[] {
					this.element(ElementEnum.Sn, 8),
					this.element(ElementEnum.Ag, 4),
					this.element(ElementEnum.Pt, 4),
					this.element(ElementEnum.Es),
					this.molecule(MoleculeEnum.calciumCarbonate, 8)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(enderiumIngot, false, COST_INGOT * 2, new PotionChemical[] {
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
			RecipeSynthesis.add(new RecipeSynthesis(blend, false, COST_INGOT * 2, new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.Ag, 8),
					this.molecule(MoleculeEnum.iron3oxide, 2),
					this.element(ElementEnum.Cu, 2)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(ingot, false, COST_INGOT * 2, new PotionChemical[] {
					this.element(ElementEnum.Au, 8),
					this.element(ElementEnum.Ag, 8),
					this.molecule(MoleculeEnum.iron3oxide, 2),
					this.element(ElementEnum.Cu, 2)
			}));
			RecipeSynthesis.add(new RecipeSynthesis(gem, false, COST_INGOT * 2, new PotionChemical[] {
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

			PotionChemical[] chargedCertusQuartzCrystalSynthesisFormula = new PotionChemical[] {
					null,
					chargedCertusQuartzChemical,
					null,
					chargedCertusQuartzChemical,
					null,
					chargedCertusQuartzChemical,
					null,
					chargedCertusQuartzChemical,
					null
			};

			PotionChemical[] quartzGlassDecompositionFormula = new PotionChemical[] {
					new Molecule(certusQuartzMolecule, 5),
					new Molecule(MoleculeEnum.siliconDioxide, 16)
			};

			PotionChemical[] quartzGlassCrystalSynthesisFormula = new PotionChemical[] {
					new Molecule(certusQuartzMolecule),
					new Molecule(MoleculeEnum.siliconDioxide, 4),
					new Molecule(certusQuartzMolecule),
					new Molecule(MoleculeEnum.siliconDioxide, 4),
					new Molecule(certusQuartzMolecule),
					new Molecule(MoleculeEnum.siliconDioxide, 4),
					new Molecule(certusQuartzMolecule),
					new Molecule(MoleculeEnum.siliconDioxide, 4),
					new Molecule(certusQuartzMolecule)
			};

			Item item = Item.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "item.ItemMultiMaterial"));
			Block skyStone = Block.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "tile.BlockSkyStone"));
			Block quartzGlass = Block.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "tile.BlockQuartzGlass"));
			ItemStack charged = new ItemStack(item, 1, 1);
			ItemStack singularity = new ItemStack(item, 1, 47);
			ItemStack skystone = new ItemStack(skyStone);
			ItemStack quartzglass = new ItemStack(quartzGlass);

			RecipeDecomposer.add(new RecipeDecomposer(charged, chargedCertusQuartzDecompositionFormula));
			RecipeSynthesis.add(new RecipeSynthesis(charged, true, 30000, chargedCertusQuartzCrystalSynthesisFormula));

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
			RecipeSynthesis.add(new RecipeSynthesis(quartzglass, true, 30000, quartzGlassCrystalSynthesisFormula));
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
				RecipeSynthesis.add(new RecipeSynthesis(moleculeItemStack, false, COST_ITEM, molecule.components()));
			}
		}

	}

	private void addUnusedSynthesisRecipes() {
		Iterator<RecipeDecomposer> decomposerRecipes = RecipeDecomposer.recipes.values().iterator();

		while (decomposerRecipes.hasNext()) {
			RecipeDecomposer nextDecomposerRecipe = decomposerRecipes.next();
			if (nextDecomposerRecipe.getInput().getItemDamage() != -1) {
				boolean check = false;
				Iterator<RecipeSynthesis> synthesisRecipes = RecipeSynthesis.recipes.values().iterator();

				while (true) {
					if (synthesisRecipes.hasNext()) {
						RecipeSynthesis nextSynthesisRecipe = synthesisRecipes.next();
						if (!MinechemUtil.stacksAreSameKind(nextSynthesisRecipe.getOutput(), nextDecomposerRecipe.getInput())) {
							continue;
						}

						check = true;
					}

					if (!check) {
						ArrayList<PotionChemical> rawDecomposerRecipe = nextDecomposerRecipe.getOutputRaw();
						if (rawDecomposerRecipe != null) {
							if (shouldCreateSynthesis(nextDecomposerRecipe.getInput())) {
								RecipeSynthesis.add(new RecipeSynthesis(nextDecomposerRecipe.getInput(), false, 100, rawDecomposerRecipe));
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
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.element(ElementEnum.Ca, 5),
					this.molecule(MoleculeEnum.phosphate, 4),
					this.element(ElementEnum.Cl)
			});
		}
		else if (oreName.contains("plateSilicon")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Si, 2)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.element(ElementEnum.Si, 2)
			});
		}
		else if (oreName.contains("xychoriumBlue")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Cu, 1)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Cu, 1)
			});
		}
		else if (oreName.contains("xychoriumRed")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Fe, 1)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Fe, 1)
			});
		}
		else if (oreName.contains("xychoriumGreen")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.V, 1)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.V, 1)
			});
		}
		else if (oreName.contains("xychoriumDark")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Si, 1)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Si, 1)
			});
		}
		else if (oreName.contains("xychoriumLight")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Ti, 1)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 300, new PotionChemical[] {
					this.element(ElementEnum.Zr, 2),
					this.element(ElementEnum.Ti, 1)
			});
		}
		else if (oreName.contains("gemPeridot")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.olivine)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.olivine)
			});
		}
		else if (oreName.contains("cropMaloberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.xylitol),
					this.molecule(MoleculeEnum.sucrose)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
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
				RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
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
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.theobromine),
					this.element(ElementEnum.S, 2)
			});
		}
		else if (oreName.contains("cropBlightberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.asprin),
					this.element(ElementEnum.S, 2)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.asprin),
					this.element(ElementEnum.S, 2)
			});
		}
		else if (oreName.contains("cropBlueberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.blueorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.blueorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
		}
		else if (oreName.contains("cropRaspberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.redorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
					this.molecule(MoleculeEnum.redorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
		}
		else if (oreName.contains("cropBlackberry")) {
			RecipeDecomposer.createAndAddRecipeSafely(oreName, new PotionChemical[] {
					this.molecule(MoleculeEnum.purpleorgodye),
					this.molecule(MoleculeEnum.sucrose, 2)
			});
			RecipeSynthesis.createAndAddRecipeSafely(oreName, false, 1000, new PotionChemical[] {
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
		registry.register(new PotionCoatingRecipe().setRegistryName(new ResourceLocation(ModGlobals.ID, "potion_coating")));
		registry.register(new RecipePotionSpiking().setRegistryName(new ResourceLocation(ModGlobals.ID, "potion_spiking")));
	}

}
