package minechem.block.tile;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import minechem.init.ModConfig;
import minechem.init.ModItems;
import minechem.inventory.InventoryBounded;
import minechem.item.ItemChemistJournal;
import minechem.recipe.RecipeSynthesisOld;
import minechem.utils.MinechemUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class OldTileSynthesis extends TileMinechemEnergyBase implements ISidedInventory {

	/**
	 * Output slot for completed item the machine will create.
	 */
	/*
	public static final int[] kOutput = {
			0,
			28
	};
	*/
	public static final int SLOT_OUTPUT_REAL = 0;
	public static final int SLOT_OUTPUT_FAKE = 28;
	/**
	 * Inventory slots that are "ghost" slots used to show the inputs of a crafting recipe from active recipe in Chemist Journal.
	 */
	public static final int[] kRecipe = {
			1,
			2,
			3,
			4,
			5,
			6,
			7,
			8,
			9
	};

	/**
	 * Input slots that make up the crafting grid so players can assemble molecules into needed shapes.
	 */
	public static final int[] kStorage = {
			10,
			11,
			12,
			13,
			14,
			15,
			16,
			17,
			18,
			19,
			20,
			21,
			22,
			23,
			24,
			25,
			26,
			27,
			28
	};

	/**
	 * Journal slot number.
	 */
	public static final int[] kJournal = {
			28
	};

	/**
	 * Slots that contain *real* items. For the purpose of dropping upon break. These are bottles, storage, and journal.
	 */
	public static int[] kRealSlots;

	/**
	 * Holds the current result for whatever the crafting matrix contains. This can change as the player moves the items around.
	 */
	private RecipeSynthesisOld currentRecipe;

	/**
	 * Holds the maximum number of input slots on the crafting matrix. Same as a crafting table in vanilla Minecraft.
	 */
	public static final int kSizeStorage = 9;

	/**
	 * Holds the slot number for the output slot for created item.
	 */
	public static final int kStartOutput = 0;

	/**
	 * Holds the starting slot number for the 'ghost' inventory slots that makeup the recipe from Chemist Journal.
	 */
	public static final int kStartRecipe = 1;

	/**
	 * Starting slot number for actual crafting grid matrix that will create an item from those chemicals.
	 */
	public static final int kStartStorage = 10;

	/**
	 * Slot number for Chemist's Journal which can activate needed synthesis recipe on crafting matrix.
	 */
	public static final int kStartJournal = 28;

	/**
	 * Wrapper for 'ghost' inventory items that show recipe from Chemist Journal.
	 */
	private final InventoryBounded recipeMatrix = new InventoryBounded(this, kRecipe);
	private NonNullList<ItemStack> oldRecipeList = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);

	/**
	 * Wrapper for crafting matrix items that make up recipe for synthesis machine.
	 */
	private final InventoryBounded storageInventory = new InventoryBounded(this, kStorage);

	/**
	 * Wrapper for output slot that will hold the end result the machine will produce for the player.
	 */
	//private final BoundedInventory outputInventory = new BoundedInventory(this, kOutput);

	/**
	 * Wrapper for Chemist's Journal slot that will read the currently active item from the journal to show in 'ghost' recipe slots.
	 */
	//private final BoundedInventory journalInventory = new BoundedInventory(this, kJournal);

	/**
	 * Wrapper for moving items in and out of the custom crafting matrix.
	 */
	//private final Transactor storageTransactor = new Transactor(storageInventory);

	/**
	 * Wrapper for moving items in and out of the output slot.
	 */
	//private final Transactor outputTransactor = new Transactor(outputInventory);

	/**
	 * Wrapper for moving items in and out of the Chemist's Journal slot.
	 */
	//private final Transactor journalTransactor = new Transactor(journalInventory, 1);

	public OldTileSynthesis() {
		super(ModConfig.maxSynthesizerStorage);

		// Creates internal inventory that will represent all of the needed slots that makeup the machine.
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

		// Initializes the individual inventory slots and assigns them accordingly.
		ArrayList<Integer> l = new ArrayList<Integer>();

		// Creates the slots for 'ghost' items that will show recipe from Chemist's Journal.
		for (int v : kStorage) {
			l.add(v);
		}

		// Creates the slot for the chemists journal to be read from.
		for (int v : kJournal) {
			l.add(v);
		}

		// Creates the slots that makeup the actual crafting grid items will assemble onto.
		kRealSlots = new int[l.size()];
		for (int idx = 0; idx < l.size(); idx++) {
			// Jump through some auto-unboxing hoops due to primitive types not being first-class types.
			kRealSlots[idx] = l.get(idx);
		}

	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
		}
		return super.getCapability(capability, facing);
	}

	/**
	 * Determines if the player or automation is allowed to take the item from output slot.
	 */
	public boolean canTakeOutputStack(boolean doTake) {
		return !inventory.get(SLOT_OUTPUT_REAL).isEmpty() && /*hasEnoughPowerForCurrentRecipe() && */takeStacksFromStorage(doTake);
	}

	/**
	 * Clears the ghost recipe items that are not real and only used to help the player place his own items down.
	 */
	public void clearRecipeMatrix() {
		for (int slot : kRecipe) {
			inventory.set(slot, ItemStack.EMPTY);
		}
	}

	private boolean valueIn(int value, int[] arr) {
		if (arr == null) {
			return false;
		}

		for (int v : arr) {
			if (value == v) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given inventory slot is a "ghost" slot used to show the output of the configured recipe. Ghost output items don't really exist and should never be dumped or extracted,
	 * except when the recipe is crafted.
	 *
	 * @param slotId Slot Id to check.
	 * @return true if the slot is a "ghost" slot for the recipe output.
	 */
	public boolean isGhostOutputSlot(int slotId) {
		return slotId == SLOT_OUTPUT_FAKE;
	}

	/**
	 * Returns true if the given inventory slot is a "ghost" slot used to show the inputs of a crafting recipe. Items in these slots don't really exist and should never be dumped or extracted.
	 *
	 * @param slotId Slot Id to check.
	 * @return true if the slot is a "ghost" slot for the recipe.
	 */
	public boolean isGhostCraftingRecipeSlot(int slotId) {
		return valueIn(slotId, kRecipe);
	}

	/**
	 * Returns true if the given inventory slot holds a "ghost" item that doesn't really exist.
	 *
	 * @param slotId Slot Id to check.
	 * @return true if the slot holds a "ghost" item.
	 */
	public boolean isGhostSlot(int slotId) {
		return isGhostOutputSlot(slotId) || isGhostCraftingRecipeSlot(slotId);
	}

	/**
	 * Returns true if the given inventory slot can hold a real (non-ghost) item, i.e., one that is really stored in the inventory.
	 *
	 * @param slotId Slot Id to check.
	 * @return true if the slot can hold a real item.
	 */
	public boolean isRealItemSlot(int slotId) {
		return !isGhostSlot(slotId);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (slot == kJournal[0]) {
			//clearRecipeMatrix();
		}
		if (!inventory.get(slot).isEmpty()) {
			ItemStack itemstack = ItemStack.EMPTY;
			if (slot == SLOT_OUTPUT_REAL) {
				int toRemove = amount;
				ItemStack result = getStackInSlot(slot).copy();
				while (toRemove > 0) {

					if (takeInputStacks()) {
						toRemove -= amount;
					}
					else {
						result.setCount(amount - toRemove);
						return result;
					}

					if (toRemove < 1) {
						return result;
					}
				}
				return ItemStack.EMPTY;
			}
			else if (inventory.get(slot).getCount() <= amount) {
				itemstack = inventory.get(slot);
				inventory.set(slot, ItemStack.EMPTY);
				return itemstack;
			}
			else {
				itemstack = inventory.get(slot).splitStack(amount);
				if (inventory.get(slot).getCount() == 0) {
					inventory.set(slot, ItemStack.EMPTY);
				}
				return itemstack;
			}
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Determines if there is any 'real' output to be given based on what is left in the internal buffer.
	 */
	public NonNullList<ItemStack> extractOutput(boolean doRemove, int maxItemCount) {
		// Stops execution if no recipe, empty output buffer, or no power or not enough items.
		//if (getCurrentRecipe() == null || !takeStacksFromStorage(false) || !canAffordRecipe(getCurrentRecipe())) {
		return NonNullList.<ItemStack>create();
		//}

		// Make a copy of the item that will be given to the player.
		//ItemStack outputStack = getCurrentRecipe().getOutput().copy();
		//NonNullList<ItemStack> output = NonNullList.from(ItemStack.EMPTY, outputStack);

		// Actually removes the items from the output buffer.
		//if (doRemove) {
		//	takeStacksFromStorage(true);
		//}

		// Item that will be given to the player.
		//return output;
	}

	/**
	 * Returns the current recipe for real items that the player has inserted into the machines crafting matrix.
	 */
	/*
	public RecipeSynthesisOld getCurrentRecipe() {
		NonNullList<ItemStack> recipeMatrixItems = getRecipeMatrixItems();
		RecipeSynthesisOld recipe = RecipeHandlerSynthesis.instance.getRecipeFromInput(recipeMatrixItems);
		return recipe;//currentRecipe;
	}
	*/
	/**
	 * Get an ordinal number representing the direction the block is facing based on metadata.
	 */
	public int getFacing() {
		return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
	}

	@Override
	public String getName() {
		return "container.synthesis";
	}

	/**
	 * Returns ItemStack array of ghost items that makeup the recipe for whatever is the active recipe in the chemists journal in that slot.
	 */
	public NonNullList<ItemStack> getRecipeMatrixItems() {
		NonNullList<ItemStack> recipeList = NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < 9; i++) {
			if (!inventory.get(i + 1).isEmpty()) {
				recipeList.set(i, inventory.get(i + 1).copy());
			}
		}
		return recipeList;
	}

	@Override
	public int getSizeInventory() {
		return 29;
	}

	/**
	 * Determines if there is enough power to allow the player to take the item from the output slot.
	 */
	/*public boolean hasEnoughPowerForCurrentRecipe() {
		if (!ModConfig.powerUseEnabled) {
			return true;
		}
		if (getCurrentRecipe() != null) {
			return canAffordRecipe(getCurrentRecipe());
		}
		return true;
	}*/

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		double dist = entityPlayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		return world.getTileEntity(pos) != this ? false : dist <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList inventoryTagList = nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
		inventory = MinechemUtil.readTagListToItemStackList(inventoryTagList);
	}

	@Override
	public int getEnergyRequired() {
		/*if (getCurrentRecipe() != null && ModConfig.powerUseEnabled) {
			return getCurrentRecipe().energyCost();
		}*/
		return 0;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
		if (slot == SLOT_OUTPUT_REAL && !getStackInSlot(slot).isEmpty()) {
			if (itemstack.isEmpty()) {
				decrStackSize(slot, 1);
				return;
			}
			if (getStackInSlot(slot).getItem() == itemstack.getItem()) {
				decrStackSize(slot, itemstack.getCount());
				return;
			}
		}

		super.setInventorySlotContents(slot, itemstack);
		if (slot == kJournal[0] && !itemstack.isEmpty()) {
			//onPutJournal(itemstack);
		}
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	/**
	 * Determines if there are items in the internal buffer which can be moved into the output slots. Allows the action of moving them to be stopped with doTake being false.
	 */
	public boolean takeStacksFromStorage(boolean doTake) {
		// Don't allow the machine to perform synthesis when no recipe or power.
		//if (getCurrentRecipe() == null || !hasEnoughPowerForCurrentRecipe()) {
		return false;
		//}

		// One of the most important features in Minechem is the ability to recombine decomposed molecules and elements into items again.
		/*NonNullList<ItemStack> ingredients = MinechemUtil.convertChemicalArrayIntoItemStackArray(getCurrentRecipe().getShapelessRecipe());
		NonNullList<ItemStack> storage = storageInventory.copyInventoryToList();
		for (ItemStack ingredient : ingredients) {
			if (!takeStackFromStorage(ingredient, storage)) {
				return false;
			}
		}

		if (doTake) {
			storageInventory.setInventoryStacks(storage);

			// Consume the required amount of energy that was the cost of the item being created.
			if (ModConfig.powerUseEnabled) {
				return useEnergy(getCurrentRecipe().energyCost());
			}
		}

		return true;*/
	}

	@Override
	public void update() {
		super.update();
		if (!shouldUpdate) {
			return;
		}
		if (!world.isRemote) {
			return;//updateHandler();
		}
		// Forces the output slot to only take a single item preventing stacking.
		//if (getCurrentRecipe() != null) {
		validate();
		//}
		//else {
		//updateRecipe();
		//getRecipeResult();
		//	inventory.set(kOutput[0], ItemStack.EMPTY);
		//}
		IBlockState iblockstate = getWorld().getBlockState(getPos());
		if (iblockstate != null) {
			getWorld().notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
		}
	}

	@Override
	public void validate() {
		super.validate();
		/*NonNullList<ItemStack> recipeMatrixItems = getRecipeMatrixItems();
		RecipeSynthesisOld recipe = RecipeHandlerSynthesis.instance.getRecipeFromInput(recipeMatrixItems);

		if (recipe != null) {

			if (inventory.get(SLOT_OUTPUT_REAL).isEmpty()) {
				if (!recipe.equals(getCurrentRecipe())) {
					setRecipe(recipe);
				}
				inventory.set(SLOT_OUTPUT_REAL, getCurrentRecipe().getOutput().copy());
			}
			//	System.out.print("test");
		}
		else {
			inventory.set(SLOT_OUTPUT_REAL, ItemStack.EMPTY);
		}*/
		//getRecipeResult();
	}

	/*
	public void updateHandler() {
		if (!ModConfig.powerUseEnabled) {
			return;
		}
		//int energyStored = getEnergyStored();
		//if (oldEnergyStored != energyStored) {
			//oldEnergyStored = energyStored;
			//SynthesisUpdateMessage message = new SynthesisUpdateMessage(this);
			//ModNetworking.INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), getPos().getY(), getPos().getZ(), ModConfig.UpdateRadius));
		//}
	}

	public void updateRecipe() {
		getRecipeResult();
		return;
		for (int i = 0; i < oldRecipeList.size(); i++) {
			if (!ItemStack.areItemStacksEqual(recipeMatrix.getStackInSlot(i), oldRecipeList.get(i))) {
				oldRecipeList = recipeMatrix.copyInventoryToList();
				getRecipeResult();
				return;
			}
		}
	}
	*/
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList inventoryTagList = MinechemUtil.writeItemStackListToTagList(inventory);
		nbt.setTag("inventory", inventoryTagList);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 255, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	/**
	 * Determines if there is enough energy in the machines internal reserve to allow the creation of this item.
	 */
	public boolean canAffordRecipe(RecipeSynthesisOld recipe) {
		return !ModConfig.powerUseEnabled || getEnergyStored() >= recipe.energyCost();
	}

	/**
	 * Returns the current recipe result for whatever is in the crafting matrix.
	 */
	/*private boolean getRecipeResult() {
		NonNullList<ItemStack> recipeMatrixItems = getRecipeMatrixItems();
		RecipeSynthesisOld recipe = RecipeHandlerSynthesis.instance.getRecipeFromInput(recipeMatrixItems);

		if (recipe != null) {
			NonNullList<ItemStack> ingredients = MinechemUtil.convertChemicalArrayIntoItemStackArray(recipe.isShaped() ? recipe.getShapedRecipe() : recipe.getShapelessRecipe());
			for (int i = 0; i < Math.min(kRecipe.length, ingredients.size()); i++) {
				inventory.set(kRecipe[i], ingredients.get(i));
			}
			setRecipe(recipe);
			inventory.set(SLOT_OUTPUT_REAL, recipe.getOutput().copy());
		}
		else {
			setRecipe(null);
			inventory.set(SLOT_OUTPUT_REAL, ItemStack.EMPTY);
		}
		shouldUpdate = true;
		return getCurrentRecipe() != null;
	}*/

	/**
	 * Called when the player places his chemists journal into the slot for it and sets ghost items to selected item recipe if active.
	 */
	/*private void onPutJournal(@Nonnull ItemStack itemstack) {
		//shouldUpdate = true;
		ItemStack activeItem = ModItems.journal.getActiveStack(itemstack);
		if (!activeItem.isEmpty()) {
			RecipeSynthesisOld recipe = RecipeHandlerSynthesis.instance.getRecipeFromOutput(activeItem);
			//if (recipe != null) {
			setRecipe(recipe);
			//}
		}
		shouldUpdate = true;
	}*/

	private boolean takeStackFromStorage(@Nonnull ItemStack ingredient, NonNullList<ItemStack> storage) {
		if (ingredient.isEmpty()) {
			return true;
		}
		int ingredientAmountLeft = ingredient.getCount();
		for (int slot = 0; slot < storage.size(); slot++) {
			ItemStack storageItem = storage.get(slot);
			if (!storageItem.isEmpty() && MinechemUtil.stacksAreSameKind(storageItem, ingredient) && storageItem.getItemDamage() == ingredient.getItemDamage()) {
				int amountToTake = Math.min(storageItem.getCount(), ingredientAmountLeft);
				ingredientAmountLeft -= amountToTake;
				storageItem.shrink(amountToTake);

				if (storageItem.getCount() <= 0) {
					storage.set(slot, ItemStack.EMPTY);
				}

				if (ingredientAmountLeft <= 0) {
					break;
				}
			}
		}
		return ingredientAmountLeft == 0;
	}

	private boolean takeInputStacks() {
		if (takeStacksFromStorage(false)) {
			return takeStacksFromStorage(true);
		}
		return false;
	}

	/*public ItemStack getOutputTemplate() {
		ItemStack template = ItemStack.EMPTY;
		ItemStack outputStack = inventory.get(SLOT_OUTPUT_REAL);
		if (!outputStack.isEmpty()) {
			template = outputStack.copy();
			if (template.getCount() == 0) {
				template.setCount(getCurrentRecipe().getOutput().getCount());
			}
		}
		shouldUpdate = true;
		return template;
	}
	
	public NonNullList<ItemStack> getOutput(int amount) {
		if (getCurrentRecipe() == null) {
			return NonNullList.<ItemStack>create();
		}
	
		ItemStack template = getOutputTemplate();
		NonNullList<ItemStack> outputs = NonNullList.<ItemStack>create();
		ItemStack initialStack = template.copy();
		initialStack.setCount(0);
		outputs.add(initialStack);
		int took = 0;
	
		while (canTakeOutputStack(false) && (amount > took) && takeInputStacks()) {
			took++;
			ItemStack output = outputs.get(outputs.size() - 1);
			if (output.getCount() + template.getCount() > output.getMaxStackSize()) {
				int leftOverStackSize = template.getCount() - (output.getMaxStackSize() - output.getCount());
				output.setCount(output.getMaxStackSize());
				if (leftOverStackSize > 0) {
					ItemStack newOutput = template.copy();
					newOutput.setCount(leftOverStackSize);
					outputs.add(newOutput);
				}
			}
			else {
				output.grow(template.getCount());
			}
	
			markDirty();
			amount -= template.getCount();
		}
		shouldUpdate = true;
		return outputs;
	}*/

	/**
	 * Sets ghost items that will make the crafting recipe from currently selected item in chemists journal if located in that slot.
	 */
	public void setRecipe(RecipeSynthesisOld recipe) {
		clearRecipeMatrix();
		if (recipe != null) {
			NonNullList<ItemStack> ingredients = MinechemUtil.convertChemicalArrayIntoItemStackArray(recipe.isShaped() ? recipe.getShapedRecipe() : recipe.getShapelessRecipe());
			for (int i = 0; i < Math.min(kRecipe.length, ingredients.size()); i++) {
				inventory.set(1 + i, ingredients.get(i));
			}
			currentRecipe = recipe;
			//onInventoryChanged();
			inventory.set(SLOT_OUTPUT_REAL, recipe.getOutput());
		}
		else {
			inventory.set(SLOT_OUTPUT_REAL, ItemStack.EMPTY);
		}
		shouldUpdate = true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// Strangely every item is always valid in the crafting matrix according to this, even though slot code prevents anything but elements of molecules.
		return true;
	}

	@Override
	public int getField(int i) {
		return 0;
	}

	@Override
	public void setField(int i, int i1) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public int[] getSlotsForFace(EnumFacing facing) {
		if (takeStacksFromStorage(false)) {
			return new int[] {
					SLOT_OUTPUT_REAL
			};
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing facing) {
		return ModConfig.AllowAutomation && !itemstack.isEmpty() && ((slot > 0 && facing.getIndex() > 0 && (itemstack.getItem() == ModItems.element || itemstack.getItem() == ModItems.molecule)) || (slot == kJournal[0] && inventory.get(slot).isEmpty() && itemstack.getItem() instanceof ItemChemistJournal));
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing facing) {
		return ModConfig.AllowAutomation && ((slot == 0 || slot == 10) && canTakeOutputStack(false));// || (facing.getIndex() != 0 && slot == kJournal[0]));
	}

	public String getState() {
		return /*canTakeOutputStack(false) ? */"Active";// : inventory.get(SLOT_OUTPUT_REAL).isEmpty() ? "No Recipe" : !hasEnoughPowerForCurrentRecipe() ? "No Power" : "Not Enough Ingredients";
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
}
