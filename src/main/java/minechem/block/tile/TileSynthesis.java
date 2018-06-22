package minechem.block.tile;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import minechem.init.ModConfig;
import minechem.init.ModItems;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import minechem.utils.MinechemUtil;
import minechem.utils.RecipeUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
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

public class TileSynthesis extends TileMinechemEnergyBase implements ISidedInventory {

	public static final int SLOT_ID_CHEMISTS_JOURNAL = 0;
	public static final int SLOT_ID_OUTPUT_JOURNAL = 1;
	public static final int SLOT_ID_OUTPUT_MATRIX = 2;
	//@formatter:off
	public static final int[] SLOT_IDS_MATRIX = { 3,4,5,6,7,8,9,10,11 };
	public static final int[] SLOT_IDS_STORAGE_BUFFER = {12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};

	private NonNullList<ItemStack> cachedRecipeStacks =NonNullList.withSize(9, ItemStack.EMPTY);
	//@formatter:on

	public TileSynthesis() {
		super(ModConfig.maxSynthesizerStorage);
		inventory = NonNullList.<ItemStack>withSize(30, ItemStack.EMPTY);
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
	/*
		public boolean canTakeOutputStack(boolean doTake) {
			return !inventory.get(SLOT_OUTPUT_REAL).isEmpty() && hasEnoughPowerForCurrentRecipe() && takeStacksFromStorage(doTake);
		}
	
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
		 * Returns true if the given inventory slot is a "ghost" slot used to show the inputs of a crafting recipe. Items in these slots don't really exist and should never be dumped or extracted.
		 *
		 * @param slotId Slot Id to check.
		 * @return true if the slot is a "ghost" slot for the recipe.
		 */
	/*
	public boolean isGhostCraftingRecipeSlot(int slotId) {
		return valueIn(slotId, kRecipe);
	}
	
	public boolean isGhostSlot(int slotId) {
		return isGhostOutputSlot(slotId) || isGhostCraftingRecipeSlot(slotId);
	}
	
	public boolean isRealItemSlot(int slotId) {
		return !isGhostSlot(slotId);
	}
	*/

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (slot == SLOT_ID_CHEMISTS_JOURNAL) {
			onTakeJournal();
		}
		if (slot >= SLOT_IDS_MATRIX[0] && slot <= SLOT_IDS_MATRIX[8]) {
			checkRecipe();
		}
		if (!inventory.get(slot).isEmpty()) {
			ItemStack itemstack = ItemStack.EMPTY;
			if (slot == SLOT_ID_OUTPUT_MATRIX) {
				int toRemove = amount;
				/*
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
				*/
				return ItemStack.EMPTY;
			}
			else if (inventory.get(slot).getCount() <= amount) {
				itemstack = inventory.get(slot);
				inventory.set(slot, ItemStack.EMPTY);
				//markDirty();
				return itemstack;
			}
			else {
				itemstack = inventory.get(slot).splitStack(amount);
				if (inventory.get(slot).getCount() == 0) {
					inventory.set(slot, ItemStack.EMPTY);
				}
				//markDirty();
				return itemstack;
			}
		}
		//markDirty();
		return ItemStack.EMPTY;
	}

	@Override
	public int getSizeInventory() {
		return 30;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return true;
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

	private void onTakeJournal() {
		//setInventorySlotContents(SLOT_ID_OUTPUT_JOURNAL, ItemStack.EMPTY);
		checkRecipe();
	}

	private void onPutJournal(@Nonnull ItemStack stack) {
		//shouldUpdate = true;
		ItemStack activeItem = ModItems.journal.getActiveStack(stack);
		if (!activeItem.isEmpty()) {
			IRecipe recipe = RecipeHandlerSynthesis.getRecipeFromOutput(activeItem);
			if (recipe != null) {
				//setRecipe(recipe);
				//setInventorySlotContents(SLOT_ID_OUTPUT_JOURNAL, activeItem.copy());
				fillMatrixWithRecipe(recipe);
			}
		}
		//shouldUpdate = true;
	}

	private void fillMatrixWithRecipe(IRecipe recipe) {
		//PotionChemical[] molecules = RecipeHandlerSynthesis.getChemicalsFromRecipe(recipe);
		/*null;
		if (RecipeHandlerSynthesis.isShaped(recipe)) {
			molecules = recipe.getShapedRecipe();
		}
		else {
			molecules = recipe.getShapelessRecipe();
		}*/
		NonNullList<ItemStack> molecules = RecipeUtil.getRecipeAsStackList(recipe);
		//if (molecules != null && molecules.length > 0) {
		clearMatrix();
		for (int i = 0; i < molecules.size(); i++) {
			if (molecules.get(i) != null) {
				ItemStack stack = molecules.get(i);//MinechemUtil.chemicalToItemStack(molecules[i], molecules[i].amount);
				if (!stack.isEmpty()) {
					setInventorySlotContents(SLOT_IDS_MATRIX[i], stack);
				}
				else {
					setInventorySlotContents(SLOT_IDS_MATRIX[i], ItemStack.EMPTY);
				}
			}
			else {
				setInventorySlotContents(SLOT_IDS_MATRIX[i], ItemStack.EMPTY);
			}
		}
		//}
	}

	private void clearMatrix() {
		for (int element : SLOT_IDS_MATRIX) {
			inventory.set(element, ItemStack.EMPTY);
		}
	}

	private NonNullList<ItemStack> getMatrixStackList() {
		return getMatrixStackList(false);
	}

	private NonNullList<ItemStack> getMatrixStackList(boolean excludeEmptyStacks) {
		NonNullList<ItemStack> matrixStacks = excludeEmptyStacks ? NonNullList.create() : NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < SLOT_IDS_MATRIX.length; i++) {
			ItemStack stack = getStackInSlot(SLOT_IDS_MATRIX[i]);
			if (!stack.isEmpty()) {
				if (excludeEmptyStacks) {
					matrixStacks.add(stack);
				}
				else {
					matrixStacks.set(i, stack);
				}
			}
		}
		return matrixStacks;
	}

	private boolean recipeHasChanged() {
		NonNullList<ItemStack> matrixStacks = getMatrixStackList();
		for (int i = 0; i < 9; i++) {
			ItemStack matrixStack = matrixStacks.get(i);
			if (!ItemStack.areItemStacksEqual(matrixStack, cachedRecipeStacks.get(i))) {
				return true;
			}
		}
		return false;
	}

	private void updateCachedRecipeStacks() {
		for (int i = 0; i < 9; i++) {
			cachedRecipeStacks.set(i, inventory.get(SLOT_IDS_MATRIX[i]));
		}
	}

	public InventoryCraftingFake getCraftingInv() {
		return new InventoryCraftingFake(getMatrixStackList());
	}

	private void updateRecipeOutput() {
		IRecipe recipe = getCurrentRecipe();//RecipeHandlerSynthesis.getRecipeFromInput(getMatrixStackList());
		if (recipe != null) {
			inventory.set(SLOT_ID_OUTPUT_JOURNAL, recipe.getRecipeOutput());
		}
		else {
			inventory.set(SLOT_ID_OUTPUT_JOURNAL, ItemStack.EMPTY);
		}
	}

	public IRecipe getCurrentRecipe() {
		return CraftingManager.findMatchingRecipe(getCraftingInv(), world);
	}

	private boolean isCurrentRecipeShaped() {
		return getCurrentRecipe() == null ? false : RecipeHandlerSynthesis.isShaped(getCurrentRecipe());
	}

	private boolean isCurrentRecipeShapeless() {
		return getCurrentRecipe() == null ? false : RecipeHandlerSynthesis.isShapeless(getCurrentRecipe());
	}

	private Map<ItemStack, Integer> getRecipeStackCost() {
		Map<ItemStack, Integer> data = new HashMap<>();
		NonNullList<ItemStack> matrixList = getMatrixStackList();
		for (ItemStack stack : matrixList) {
			int count = stack.getCount();
			ItemStack tmpStack = stack.copy();
			tmpStack.setCount(1);

			if (!isStackAdded(data, tmpStack)) {
				data.put(tmpStack, count);
			}
			else {
				count += getCount(data, tmpStack);
				data.put(tmpStack, count);
			}
		}
		return data;
	}

	private int getCount(Map<ItemStack, Integer> data, ItemStack scompStack) {
		for (ItemStack stack : data.keySet()) {
			if (stack.isItemEqual(scompStack)) {
				return data.get(stack);
			}
		}
		return 0;
	}

	private boolean isStackAdded(Map<ItemStack, Integer> data, ItemStack scompStack) {
		for (ItemStack stack : data.keySet()) {
			if (stack.isItemEqual(scompStack)) {
				return true;
			}
		}
		return false;
	}

	private boolean doesStorageContainEnoughToCraft() {
		if (getStorageBuffer(true).size() > 0) {

		}
		return false;
	}

	private NonNullList<ItemStack> getStorageBuffer(boolean excludeEmptyStacks) {
		int size = SLOT_IDS_STORAGE_BUFFER.length;
		NonNullList<ItemStack> stackList = excludeEmptyStacks ? NonNullList.withSize(size, ItemStack.EMPTY) : NonNullList.create();
		for (int i = 0; i < size; i++) {
			if (excludeEmptyStacks) {
				stackList.set(i, inventory.get(SLOT_IDS_STORAGE_BUFFER[i]));
			}
			else {
				stackList.add(inventory.get(SLOT_IDS_STORAGE_BUFFER[i]));
			}
		}
		return stackList;
	}

	private void checkRecipe() {
		if (recipeHasChanged()) {
			updateCachedRecipeStacks();
			updateRecipeOutput();
		}
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		/*
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
		*/
		//super.setInventorySlotContents(slot, stack);
		inventory.set(slot, stack.copy());
		if (slot == SLOT_ID_CHEMISTS_JOURNAL && !stack.isEmpty()) {
			onPutJournal(stack);
		}
		if (slot >= SLOT_IDS_MATRIX[0] && slot <= SLOT_IDS_MATRIX[8]) {
			checkRecipe();
		}
		markDirty();
	}

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
	 * Determines if there is any 'real' output to be given based on what is left in the internal buffer.
	 */
	/*
	public NonNullList<ItemStack> extractOutput(boolean doRemove, int maxItemCount) {
		// Stops execution if no recipe, empty output buffer, or no power or not enough items.
		if (getCurrentRecipe() == null || !takeStacksFromStorage(false) || !canAffordRecipe(getCurrentRecipe())) {
			return NonNullList.<ItemStack>create();
		}
	
		// Make a copy of the item that will be given to the player.
		ItemStack outputStack = getCurrentRecipe().getOutput().copy();
		NonNullList<ItemStack> output = NonNullList.from(ItemStack.EMPTY, outputStack);
	
		// Actually removes the items from the output buffer.
		if (doRemove) {
			takeStacksFromStorage(true);
		}
	
		// Item that will be given to the player.
		return output;
	}
	*/
	/**
	 * Returns the current recipe for real items that the player has inserted into the machines crafting matrix.
	 */

	/*
	public RecipeSynthesis getCurrentRecipe() {
		NonNullList<ItemStack> recipeMatrixItems = getRecipeMatrixItems();
		RecipeSynthesis recipe = RecipeHandlerSynthesis.instance.getRecipeFromInput(recipeMatrixItems);
		return recipe;//currentRecipe;
	}
	*/
	/**
	 * Get an ordinal number representing the direction the block is facing based on metadata.
	 */
	/*
	public int getFacing() {
		return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
	}
	*/
	@Override
	public String getName() {
		return "container.synthesis";
	}

	/**
	 * Returns ItemStack array of ghost items that makeup the recipe for whatever is the active recipe in the chemists journal in that slot.
	 */
	/*
	public NonNullList<ItemStack> getRecipeMatrixItems() {
		NonNullList<ItemStack> recipeList = NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < 9; i++) {
			if (!inventory.get(i + 1).isEmpty()) {
				recipeList.set(i, inventory.get(i + 1).copy());
			}
		}
		return recipeList;
	}
	*/

	/*
		public boolean hasEnoughPowerForCurrentRecipe() {
			if (!ModConfig.powerUseEnabled) {
				return true;
			}
			if (getCurrentRecipe() != null) {
				return canAffordRecipe(getCurrentRecipe());
			}
			return true;
		}
	*/

	@Override
	public int getEnergyRequired() {
		/*
		if (getCurrentRecipe() != null && ModConfig.powerUseEnabled) {
			return getCurrentRecipe().energyCost();
		}
		*/
		return 0;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	/**
	 * Determines if there are items in the internal buffer which can be moved into the output slots. Allows the action of moving them to be stopped with doTake being false.
	 */
	/*
	public boolean takeStacksFromStorage(boolean doTake) {
		// Don't allow the machine to perform synthesis when no recipe or power.
		if (getCurrentRecipe() == null || !hasEnoughPowerForCurrentRecipe()) {
			return false;
		}
	
		// One of the most important features in Minechem is the ability to recombine decomposed molecules and elements into items again.
		NonNullList<ItemStack> ingredients = MinechemUtil.convertChemicalArrayIntoItemStackArray(getCurrentRecipe().getShapelessRecipe());
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
	
		return true;
	}
	*/
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
		checkRecipe();
		IBlockState iblockstate = getWorld().getBlockState(getPos());
		if (iblockstate != null) {
			getWorld().notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
		}
	}

	/*
		@Override
		public void validate() {
			super.validate();
			NonNullList<ItemStack> recipeMatrixItems = getRecipeMatrixItems();
			RecipeSynthesis recipe = RecipeHandlerSynthesis.instance.getRecipeFromInput(recipeMatrixItems);
	
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
			}
			//getRecipeResult();
		}
	*/
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

	/**
	 * Determines if there is enough energy in the machines internal reserve to allow the creation of this item.
	 */
	/*
	public boolean canAffordRecipe(RecipeSynthesis recipe) {
		return !ModConfig.powerUseEnabled || getEnergyStored() >= recipe.energyCost();
	}
	*/
	/**
	 * Returns the current recipe result for whatever is in the crafting matrix.
	 */

	/*
	private boolean getRecipeResult() {
		NonNullList<ItemStack> recipeMatrixItems = getRecipeMatrixItems();
		RecipeSynthesis recipe = RecipeHandlerSynthesis.instance.getRecipeFromInput(recipeMatrixItems);
	
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
	}
	*/
	/**
	 * Called when the player places his chemists journal into the slot for it and sets ghost items to selected item recipe if active.
	 */

	/*
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
	
		public ItemStack getOutputTemplate() {
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
		}
	*/
	/**
	 * Sets ghost items that will make the crafting recipe from currently selected item in chemists journal if located in that slot.
	 */
	/*
	public void setRecipe(RecipeSynthesis recipe) {
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
	*/
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
		/*
		if (takeStacksFromStorage(false)) {
			return new int[] {
					SLOT_OUTPUT_REAL
			};
		}
		*/
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing facing) {
		return false;//ModConfig.AllowAutomation && !itemstack.isEmpty() && ((slot > 0 && facing.getIndex() > 0 && (itemstack.getItem() == ModItems.element || itemstack.getItem() == ModItems.molecule)) || (slot == kJournal[0] && inventory.get(slot).isEmpty() && itemstack.getItem() instanceof ItemChemistJournal));
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing facing) {
		return false;//ModConfig.AllowAutomation && ((slot == 0 || slot == 10) && canTakeOutputStack(false));// || (facing.getIndex() != 0 && slot == kJournal[0]));
	}

	public String getState() {
		return "Active";//canTakeOutputStack(false) ? "Active" : inventory.get(SLOT_OUTPUT_REAL).isEmpty() ? "No Recipe" : !hasEnoughPowerForCurrentRecipe() ? "No Power" : "Not Enough Ingredients";
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	private static class InventoryCraftingFake extends InventoryCrafting {

		private final NonNullList<ItemStack> invList;

		public InventoryCraftingFake(NonNullList<ItemStack> invList) {
			super(null, 3, 3);
			this.invList = invList;
		}

		@Override
		public int getSizeInventory() {
			return invList.size();
		}

		@Override
		public boolean isEmpty() {
			for (ItemStack itemstack : invList) {
				if (!itemstack.isEmpty()) {
					return false;
				}
			}

			return true;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return index >= getSizeInventory() ? ItemStack.EMPTY : (ItemStack) invList.get(index);
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			return ItemStackHelper.getAndRemove(invList, index);
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			ItemStack itemstack = ItemStackHelper.getAndSplit(invList, index, count);
			return itemstack;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			invList.set(index, stack);
		}

		@Override
		public void clear() {
			invList.clear();
		}

		@Override
		public void fillStackedContents(RecipeItemHelper helper) {
			for (ItemStack itemstack : invList) {
				helper.accountStack(itemstack);
			}
		}

	}
}
