package minechem.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import minechem.init.ModLogger;
import minechem.recipe.RecipeDecomposer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeUtil {

	public static Map<MapKey, RecipeUtil> recipes = new LinkedHashMap<MapKey, RecipeUtil>();

	public static Map<ItemStack, ItemStack> smelting;
	public ItemStack output = ItemStack.EMPTY;
	public NonNullList<ItemStack> inStacks = NonNullList.create();
	private int depth;

	private static final int MAXDEPTH = 20;

	@SuppressWarnings({
			"unchecked",
			"rawtypes"
	})
	@Optional.Method(modid = "RotaryCraft")
	public static List getRotaryRecipes() {
		try {
			Class worktable = Class.forName("Reika.RotaryCraft.Auxiliary.WorktableRecipes");
			Method instance = worktable.getMethod("getInstance");
			Method list = worktable.getMethod("getRecipeListCopy");
			Class config = Class.forName("Reika.RotaryCraft.Registry.ConfigRegistry");
			Method state = config.getMethod("getState");
			boolean add = !(Boolean) state.invoke(Enum.valueOf(config, "TABLEMACHINES"));
			if (add) {
				return (List) list.invoke(instance.invoke(null));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({
			"unchecked",
			"rawtypes"
	})
	@Optional.Method(modid = "Railcraft")
	public static List getRailcraftRecipes() {
		try {
			Class rollingMachine = Class.forName("mods.railcraft.common.util.crafting.RollingMachineCraftingManager");
			Method instance = rollingMachine.getMethod("getInstance");
			Method list = rollingMachine.getMethod("getRecipeList");
			return (List) list.invoke(instance.invoke(null));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({
			"unchecked",
			"rawtypes"
	})
	public static void init() {
		Map<MapKey, ArrayList<RecipeUtil>> preCullRecipes = new Hashtable<MapKey, ArrayList<RecipeUtil>>();
		recipes = new LinkedHashMap<MapKey, RecipeUtil>();
		smelting = FurnaceRecipes.instance().getSmeltingList();
		Collection<IRecipe> craftingRecipes = ForgeRegistries.RECIPES.getValuesCollection();//CraftingManager.getInstance().getRecipeList();
		if (Loader.isModLoaded("RotaryCraft")) {
			List add = getRotaryRecipes();
			if (add != null) {
				craftingRecipes.addAll(add);
			}
		}
		if (Loader.isModLoaded("Railcaft")) {
			List add = getRailcraftRecipes();
			if (add != null) {
				craftingRecipes.addAll(add);
			}
		}
		for (Object recipe : craftingRecipes) {
			if (recipe instanceof IRecipe) {
				ItemStack input = ((IRecipe) recipe).getRecipeOutput();
				if (!invalidStack(input)) {
					ModLogger.debug("Adding recipe for " + input.toString());
					NonNullList<ItemStack> components = NonNullList.<ItemStack>create();
					NonNullList<ItemStack> inputs = NonNullList.<ItemStack>create();

					if (recipe.getClass().equals(ShapelessOreRecipe.class) && ((ShapelessOreRecipe) recipe).getIngredients().size() > 0) {
						for (Object o : ((ShapelessOreRecipe) recipe).getIngredients()) {
							if (o instanceof ItemStack) {
								inputs.add((ItemStack) o);
							}
						}
					}
					else if (recipe.getClass().equals(ShapedOreRecipe.class)) {
						for (Object o : ((ShapedOreRecipe) recipe).getIngredients()) {
							if (o instanceof ItemStack) {
								inputs.add((ItemStack) o);
							}
							else if (o instanceof String) {
								inputs.add(OreDictionary.getOres((String) o).get(0));
							}
							else if (o instanceof ArrayList && !((ArrayList) o).isEmpty()) {
								inputs.add((ItemStack) ((ArrayList) o).get(0));
							}
						}

					}
					else if (recipe.getClass().equals(ShapelessRecipes.class) && ((ShapelessRecipes) recipe).recipeItems.toArray() instanceof ItemStack[]) {
						inputs = (NonNullList<ItemStack>) (List<?>) (((ShapelessRecipes) recipe).recipeItems);
					}
					else if (recipe.getClass().equals(ShapedRecipes.class)) {
						for (Ingredient inputStack : ((ShapedRecipes) recipe).recipeItems) {
							inputs.addAll(Arrays.asList(inputStack.getMatchingStacks()));
						}
					}

					MapKey key = MapKey.getKey(input);
					if (inputs.size() > 0 && key != null) {
						for (Iterator<ItemStack> itr = inputs.iterator(); itr.hasNext();) {
							ItemStack component = itr.next();
							if (invalidStack(component) || component.getItem().hasContainerItem(component)) {
								itr.remove();
							}
						}
						components = inputs;
						if (components.size() > 0) {
							RecipeUtil addRecipe = new RecipeUtil(input, components);
							addPreCullRecipe(key, addRecipe, preCullRecipes);
						}
					}
				}
			}
		}
		for (ItemStack input : smelting.keySet()) {
			ItemStack output = smelting.get(input);
			if (invalidStack(input) || invalidStack(output)) {
				continue;
			}
			MapKey key = MapKey.getKey(output);
			if (key != null) {
				RecipeUtil addRecipe = new RecipeUtil(output, NonNullList.withSize(1, input));
				ModLogger.debug("Adding Smelting recipe for " + output.toString());
				addPreCullRecipe(key, addRecipe, preCullRecipes);
			}
		}

		for (Map.Entry<MapKey, ArrayList<RecipeUtil>> entry : preCullRecipes.entrySet()) {
			if (entry.getValue() != null && entry.getValue().size() > 0) {
				ModLogger.debug("Culling recipe for " + entry.getValue().get(0).output.toString());
			}
			int depth = cullRecipes(entry, preCullRecipes);
			if (entry.getValue().size() == 1) {
				RecipeUtil addRecipe = entry.getValue().get(0);
				addRecipe.depth = depth;
				recipes.put(entry.getKey(), addRecipe);
			}
		}
	}

	private static boolean invalidStack(ItemStack stack) {
		return stack.isEmpty() || stack.getItem() == null || stack.getCount() < 1 || stack.getItemDamage() < 0;
	}

	private static void addPreCullRecipe(MapKey key, RecipeUtil addRecipe, Map<MapKey, ArrayList<RecipeUtil>> preCullRecipes) {
		ArrayList<RecipeUtil> recipeList = preCullRecipes.get(key);
		if (recipeList == null) {
			recipeList = new ArrayList<RecipeUtil>();
		}
		recipeList.add(addRecipe);
		preCullRecipes.put(key, recipeList);
	}

	public RecipeUtil(ItemStack outStack, NonNullList<ItemStack> componentsParam) {
		output = outStack;
		NonNullList<ItemStack> components = NonNullList.withSize(componentsParam.size(), ItemStack.EMPTY);
		int i = 0;
		for (ItemStack itemStack : componentsParam) {
			if (!itemStack.isEmpty() && itemStack.getItem() != null) {
				if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					components.set(i, new ItemStack(itemStack.getItem(), itemStack.getCount(), 0));
				}
				else {
					components.set(i, new ItemStack(itemStack.getItem(), itemStack.getCount(), itemStack.getItemDamage()));
				}
			}
			i++;
		}
		inStacks = components;
	}

	public int getOutStackSize() {
		return output.getCount();
	}

	public static RecipeUtil get(ItemStack output) {
		return get(new MapKey(output));
	}

	public static RecipeUtil get(MapKey key) {
		if (recipes.containsKey(key)) {
			return recipes.get(key);
		}
		return null;
	}

	public static RecipeUtil get(String string) {
		return recipes.get(string);
	}

	private static int cullRecipes(Entry<MapKey, ArrayList<RecipeUtil>> entry, Map<MapKey, ArrayList<RecipeUtil>> preCullRecipes) {
		int returnVal = 0;
		ArrayList<RecipeUtil> value = entry.getValue();
		if (RecipeDecomposer.get(entry.getKey()) != null) {
			value.clear();
			entry.setValue(value);
			return 0;
		}
		Map<RecipeUtil, Integer> result = new Hashtable<RecipeUtil, Integer>();
		for (RecipeUtil check : value) {
			int depth = 0;
			if (check.inStacks != null && check.inStacks.size() > 0) {
				for (ItemStack stack : check.inStacks) {
					if (!stack.isEmpty()) {
						MapKey key = MapKey.getKey(stack);
						depth = Math.max(depth, getSize(key, 0, preCullRecipes));
						if (depth >= MAXDEPTH) {
							break;
						}
					}
				}
				result.put(check, depth);
			}
			else {
				result.put(check, MAXDEPTH);
			}
		}
		value.clear();
		RecipeUtil minValue = null;
		for (RecipeUtil key : result.keySet()) {
			if (minValue == null && result.get(key) < MAXDEPTH) {
				minValue = key;
			}
			else if (minValue != null) {
				if (key.getOutStackSize() < minValue.getOutStackSize()) {
					minValue = key;
				}
				else if (key.getOutStackSize() == minValue.getOutStackSize() && result.get(key) < result.get(minValue)) {
					minValue = key;
				}
			}
		}
		if (minValue != null) {
			returnVal = result.get(minValue);
			value.add(minValue);
		}
		entry.setValue(value);
		return returnVal;
	}

	private static int getSize(MapKey key, int depth, Map<MapKey, ArrayList<RecipeUtil>> preCullRecipes) {
		if (depth > MAXDEPTH) {
			return depth;
		}
		if (RecipeDecomposer.get(key) != null) {
			return 0;
		}
		if (!preCullRecipes.containsKey(key) || preCullRecipes.get(key).size() < 1) {
			return 1;
		}
		if (recipes.containsKey(key)) {
			return recipes.get(key).depth;
		}
		ModLogger.debug("Depth: " + depth + ", stack: " + preCullRecipes.get(key).get(0).output.toString());
		int result = 0;
		for (RecipeUtil recipe : preCullRecipes.get(key)) {
			int thisDepth = 0;
			for (ItemStack stack : recipe.inStacks) {
				if (!stack.isEmpty()) {
					MapKey nextKey = MapKey.getKey(stack);
					int nextDepth = getSize(nextKey, depth + 1, preCullRecipes);
					thisDepth = Math.max(thisDepth, nextDepth);
					if (thisDepth > MAXDEPTH) {
						break;
					}
				}
			}
			result = Math.max(thisDepth, result);
			if (result > MAXDEPTH) {
				break;
			}
		}
		return result + 1;
	}

}
